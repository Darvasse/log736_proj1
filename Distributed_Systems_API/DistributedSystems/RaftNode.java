import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class RaftNode extends Node{
    
    /**
     * Accepted actions' subject
     */
    public static final String ClientCommand = "client";
    public static final String SetAction = "SET";
    public static final String AddAction = "ADD";
    public static final String SubAction = "SUB";
    public static final String MultAction = "MULT";
    public static final String CommitCommand = "commit";
    protected static final String ClientRolledbackCommand = "clientRolledback";
    protected static final String AwknowledgeCommand = "acknowledge";
    protected static final String UpdateLeaderCommand = "updateLeader";
    protected static final String ReplyHeartBeatCommand = "replyHeartBeat";
    protected static final String RequestVoteCommand = "requestVote";
    protected static final String ProposeVoteAction = "proposeVote";
    protected static final String VoteGrantedAction = "voteGranted";
    protected static final String AppendEntriesCommand = "appendEntries";
    private static final int MinimalElectTimeout = 750;
    private static final int MaximalElectTimeout = 5000;
    private static final int HeartbeatTimeout = 500;
    private static int ConnectedNodesCount = 0;

    private State state = State.FOLLOWER;
    private int term = 0;
    private HashSet<UUID> votedBy = new HashSet<>();
    private int value = 0;
    private UUID votedFor = null;
    private UUID leader = null;
    private ArrayList<UUID> clientsUuid = new ArrayList<>();
    private ArrayList<Message> logs = new ArrayList<>();
    private ArrayList<HashSet<UUID>> approuvals = new ArrayList<>();
    private int commitedIndex = -1;
    private int commitedTerm = -1;
    private Timer electionTimer = new Timer();
    private Timer heartbeatTimer = new Timer();

    enum State {
        FOLLOWER,
        CANDIDATE,
        LEADER
    }

    @Override
    public boolean register() {
        if(super.register()) {
            synchronized (RaftNode.class) {
                ConnectedNodesCount++;
            }
            resetElectionTimer();
            
            return true;
        }
        return false;
    }

    public int getValue() {
        return value;
    }

    /**
     * Receives a message and processes it based on its subject.
     *
     * @param message the message to be processed
     */
    @Override
    public void receive(Message message) {
        switch(message.getSubject()) {
            case ClientCommand:         writeOperation(message);                    break;
            case RequestVoteCommand:    respondVote(message);                       break;
            case AppendEntriesCommand:  appendEntries(message);                     break;
            case UpdateLeaderCommand:   rollbackAndSynchronizeCommits(message);     break;
            case AwknowledgeCommand:    approuveOperation(message);                 break;
            case CommitCommand:         commitOperation(message);                   break;
        }
    }

    private void writeOperation(Message message) {
        if(state != State.LEADER && leader != null) {
            API.send(message, leader);
        } else {
            switch (message.getHeader()) {
                case SetAction: 
                case AddAction:
                case SubAction:
                case MultAction: {
                    clientsUuid.add(message.getFrom());
                    message.setContent("[" + value + "/" + message.getContent() + "]");
                    logs.add(message);
                    break;   
                }
                default: break;
            }
        }
    }

    private void approuveOperation(Message message) {
        if(state != State.LEADER && leader != null) {
            API.send(message, leader);
        } else {
            String header = message.getHeader();
            String[] parts = header.substring(1, header.length() - 1).split("/");
            if(parts.length != 4) { return; }
            
            int currentTerm = Integer.parseInt(parts[0]);
            int sourceCurrentIndex = Integer.parseInt(parts[1]);
            int lastCommitedTerm = Integer.parseInt(parts[2]);
            int lastCommitedIndex = Integer.parseInt(parts[3]);
            // Check if the message is outdated
            if(lastCommitedIndex != commitedIndex || lastCommitedTerm != commitedTerm) { return; }

            for(int index = lastCommitedIndex + 1; index < sourceCurrentIndex; ++index) {
                if(index == approuvals.size()) {
                    approuvals.add(new HashSet<>());
                } else if(index > approuvals.size()) {
                    return;
                }

                // modification should follow in approuvals since every class are reference in Java....
                HashSet<UUID> operationApprouval = approuvals.get(index);
                operationApprouval.add(message.getFrom());
                int voteThreshold = Integer.MAX_VALUE;
                synchronized (RaftNode.class) {
                    voteThreshold = ConnectedNodesCount / 2 + 1;
                }
                if(operationApprouval.size() >= voteThreshold && index == commitedIndex + 1) {
                    commitedIndex++;
                    Message command = logs.get(index);
                    String operation = command.getHeader();
                    String content = command.getContent();
                    content = content.substring(1, content.length() - 1);
                    String[] partsContent = content.split("/");
                    if(partsContent.length != 2) { return; }
                    int operand = Integer.parseInt(partsContent[1]);

                    applyAction(operation, operand);

                    notifyClientCommit(command, 0);
                }


            }
        }
    }

    private void commitOperation(Message command) {
        if(state == State.LEADER || leader != command.getFrom()) { return; }

        String operation = command.getHeader();
        String content = command.getContent();
        content = content.substring(1, content.length() - 1);
        String[] partsContent = content.split("/");
        if(partsContent.length != 2) { return; }
        int operand = Integer.parseInt(partsContent[1]);

        applyAction(operation, operand);
        logs.add(command);
        commitedIndex = logs.size() - 1;
    }

    private void notifyClientCommit(Message message, int index) {
        Message commit = new Message(message);
        commit.setSubject(CommitCommand);
        broadcast(commit);

        Message operationMade = new Message(message);
        operationMade.setSubject(ClientCommand);
        operationMade.setHeader("Successfull");
        operationMade.setContent("[" + message.getHeader() + "\\" + message.getContent() + "]");
        operationMade.setTo(clientsUuid.get(index));

        send(operationMade);
        clientsUuid.remove(index);
    }

    private void respondVote(Message message) {
        System.out.println("Client " + getUuid() + " received vote request: " + message.toString());
        String header = message.getHeader();
        String[] parts = header.substring(1, header.length() - 1).split("/");
        if(parts.length != 2) { return; }

        String action = parts[0];
        int candidateTerm = Integer.parseInt(parts[1]);

        switch (action) {
            case ProposeVoteAction:{
                if(candidateTerm > term && votedFor == null) {
                    term = candidateTerm;
                    state = State.FOLLOWER;
                    votedFor = UUID.fromString(message.getContent());
                    heartbeatTimer.cancel();
                    heartbeatTimer = new Timer();
                    
                    System.out.println("[" + uuid.toString() + "] Voting for: " + votedFor.toString() + " on term " + term);
                    Message response = new Message();
                    response.setSubject(RequestVoteCommand);
                    response.setHeader("[" + VoteGrantedAction + "/" + term + "]");
                    response.setContent(uuid.toString());
                    response.setTo(message.getFrom());
                    send(response);
                }
                break;
            }
            case VoteGrantedAction: {
                if(state != State.CANDIDATE || candidateTerm != term) { return; }
                votedBy.add(UUID.fromString(message.getContent()));

                int voteThreshold = Integer.MAX_VALUE;
                synchronized (RaftNode.class) {
                    voteThreshold = ConnectedNodesCount / 2 + 1;
                }
                if(votedBy.size() >= voteThreshold) {
                    System.out.println("[" + uuid.toString() + "] Is elected with: " + votedBy.size() + " votes");

                    state = State.LEADER;
                    leader = uuid;
                    electionTimer.cancel();
                    electionTimer = new Timer();
                    heartbeatTimer.schedule(new TimerTask() {
                        @Override public void run() { sendAppendEntries(); }
                    }, 0, HeartbeatTimeout);

                    votedBy.clear();
                }
                break;
            }
            default:
                break;
        }

        
    }

    private void appendEntries(Message message) {
        if(state == State.LEADER) { return; }
        
        String header = message.getHeader().substring(1, message.getHeader().length() - 1);
        String[] parts = header.split("/");
        int leaderTerm = Integer.parseInt(parts[0]);
        if(leaderTerm < term) { return; } // Ignore outdated messages
        
        // Update leader
        if(leader != message.getFrom()) {
            term = leaderTerm;
            leader = message.getFrom();
            state = State.FOLLOWER;
            votedFor = null;
        }
        
        resetElectionTimer();
        
        // Append entries to logs
        // Check if the leader has more logs than the follower
        if(message.getContent().isEmpty()) { 
            // Empty message are heart beats from the leader
            // If the logs are the same, then the follower will send an empty message to the leader
            Message response = new Message();
            response.setSubject(ReplyHeartBeatCommand);
            response.setTo(leader);
            send(response);
            return; 
        }
        else if(parts.length == 4) {
            int leaderLastCommitedIndex = Integer.parseInt(parts[3]);
            if(leaderLastCommitedIndex < commitedIndex) {
                // If the leader has less logs, then the follower will send its logs to the leader
                Message requestUpdate = new Message();
                requestUpdate.setSubject(UpdateLeaderCommand);
                requestUpdate.setHeader("[" + term + "/" + commitedIndex + "/" + leaderTerm + "/" + leaderLastCommitedIndex + "]");
                requestUpdate.setContent(createMessageEntriesContent(leaderLastCommitedIndex, commitedIndex + 1));
                requestUpdate.setTo(leader);
                send(requestUpdate);
            } else {
                // If the leader has more logs, then the follower will update its logs
                acknowledge(message);
            }
        }
        
    }

    private boolean applyAction(String action, int operand) {
        switch (action) {
            case SetAction: value = operand; break;
            case AddAction: value += operand; break;
            case SubAction: value -= operand; break;
            case MultAction: value *= operand; break;
            default: return false; 
        }
        return true;
    }

    private void rollbackAndSynchronizeCommits(Message message) {
        // Rollback the logs that are not commited
        // Synchronize the logs with the leader
        String header = message.getHeader();
        String[] parts = header.substring(1, header.length() - 1).split("/");
        if(parts.length != 4) { return; }

        int sourceCurrentTerm = Integer.parseInt(parts[0]);
        int sourceCurrentIndex = Integer.parseInt(parts[1]);
        int sourceLastTerm = Integer.parseInt(parts[2]);
        int sourceLastIndex = Integer.parseInt(parts[3]);

        if(sourceLastIndex <= commitedIndex) { return; } // Synchronization was already done, maybe by a message from another node
        if(sourceLastIndex > sourceCurrentIndex) { return; } // Invalid message

        // Undo step by steps the logs that are not commited or are desynchronized
        for(int index = logs.size() - 1; index > commitedIndex; --index) {
            Message log = logs.remove(index);
            String content = log.getContent();
            content = content.substring(1, content.length() - 1);
            String[] partsContent = content.split("\\");
            if(partsContent.length != 2) { continue; }

            int lastValue = Integer.parseInt(partsContent[0]);
            String operation = log.getHeader();
            String operand = partsContent[1];
            
            switch (state) {
                case LEADER: {
                    int clientIndex = index - commitedIndex;
                    UUID client = clientsUuid.get(clientIndex);
                    Message rolledback = new Message();
                    rolledback.setSubject(ClientRolledbackCommand);
                    rolledback.setHeader(operation);
                    rolledback.setContent(operand);
                    rolledback.setTo(client);

                    send(rolledback);
                    clientsUuid.remove(clientIndex);
                } 
                case CANDIDATE:
                case FOLLOWER:
                    value = lastValue;
                    break;
                default:
                    break;
            }
        }

        // Apply the more recent logs
        String compressedCommands = message.getContent();
        compressedCommands = compressedCommands.substring(1, compressedCommands.length() - 1);
        String[] commands = compressedCommands.split("\\");
        for(String command : commands) {
            command = command.substring(1, command.length() - 1);
            String[] partsCommand = command.split("/");
            if(partsCommand.length != 2) { continue; }

            String operation = partsCommand[0];
            int operand = Integer.parseInt(partsCommand[1]);

            int lastValue = value;
            if(applyAction(operation, operand)) {
                Message operationMessage = new Message();
                operationMessage.setSubject(ClientCommand);
                operationMessage.setHeader(operation);
                operationMessage.setContent("[" + lastValue + "/" + operand + "]");
                logs.add(operationMessage);
                commitedIndex = logs.size() - 1;
            }

        }

    }

    private void acknowledge(Message message) {
        String header = message.getHeader();
        String[] parts = header.substring(1, header.length() - 1).split("/");
        if(parts.length != 4) { return; }
        int leaderLastCommitedTerm = Integer.parseInt(parts[2]);
        int leaderLastCommitedIndex = Integer.parseInt(parts[3]);
        if(leaderLastCommitedIndex == logs.size() - 1 && leaderLastCommitedTerm != term) { rollbackAndSynchronizeCommits(message); return; }

        Message response = new Message();
        response.setSubject(AwknowledgeCommand);
        response.setHeader(header);
        response.setContent(message.getContent());
        response.setTo(message.getFrom());
        send(response);
    }
    
    private void startElection() {
        if(state == State.CANDIDATE) { return; }
        state = State.CANDIDATE;
        term++;
        votedBy.add(uuid); // Vote for self
        votedFor = uuid;
        System.out.println("Starting election for: " + votedFor.toString() + " on term " + term);


        Message requestVote = new Message();
        requestVote.setSubject(RequestVoteCommand);
        requestVote.setHeader("[" + ProposeVoteAction + "/" + term + "]");
        requestVote.setContent(uuid.toString());
        broadcast(requestVote);

        // Reset election timer
        resetElectionTimer();
    }

    private void resetElectionTimer() {
        electionTimer.cancel();
        electionTimer = new Timer();

        electionTimer.schedule(new TimerTask() {
                @Override public void run() { startElection(); }
            }, MinimalElectTimeout + (int)(Math.random() * (MaximalElectTimeout - MinimalElectTimeout)));
    }

    private void sendAppendEntries() {
        Message appendEntries = new Message();;
        String header = "[" + term + "]";

        if(commitedIndex < logs.size() - 1) {
            header = header.substring(0, header.length() - 1) + "/" + (logs.size() - 1) + "/" + commitedTerm + "/" + commitedIndex + "]";
            appendEntries.setContent(createMessageEntriesContent(commitedIndex + 1, logs.size()));
        } else {
            System.out.println("Sending heartbeat");
        }

        appendEntries.setHeader(header);
        appendEntries.setSubject(AppendEntriesCommand);
        broadcast(appendEntries);
    }

    private String createMessageEntriesContent(int from, int to) {
        String content = "[";
        // Send logs with commited index/term and current logs
        for(int i = from; i < to; ++i) {
            Message action = logs.get(i);
            String actionContent = action.getContent();
            actionContent = actionContent.substring(1, actionContent.length() - 1);
            String[] partsContent = actionContent.split("/");
            if(partsContent.length != 2) { continue; }

            String operation = action.getHeader();
            String operand = partsContent[1];
            content += "[" + operation + "/" + operand + "]\\";
        }
        content = content.substring(0, content.length() - 1) + "]"; // Remove last comma

        return content;
    }
}
