import java.util.ArrayList;

public class PaxosNode extends Node {
    
    /**
     * Accepted actions' subject
     */
    public static final String RequestCommand = "request";
    public static final String GetValueAction = "getValue";
    public static final String SetValueAction = "setValue";
    public static final String PrepareCommand = "prepare";
    public static final String AcceptCommand = "accept";
    public static final String PromiseCommand = "promise";
    public static final String LearnCommand = "learn";
    public static final String ResponseCommand = "response";

    private static ArrayList<PaxosNode> ConnectedNodes = new ArrayList<>();
    private static long ProposalCount = 0;
    private long promisedThreshold = 0;
    private long acceptedThreshold = 2;
    private Integer value = null;
    private boolean isTerminated = false;

    public void terminate() {
        this.isTerminated=true;
    }
    public boolean register( PaxosNode... leader) {
        if(super.register()) {
            for(PaxosNode node : leader)
                node.connectNode(this);
            return true;
        }
        return false;
    }
    public void connectNode(PaxosNode node) {
        ConnectedNodes.add(node);
        acceptedThreshold = (ConnectedNodes.size() / 2)+1;
    }


    /**
     * Receives a message and processes it based on its subject.
     *
     * @param message the message to be processed
     */
    @Override
    public void receive(Message message) {
        if(isTerminated) return;
        switch(message.getSubject()) {
            case RequestCommand:  request(message); break;
            case PrepareCommand:  prepare(message); break;
            case AcceptCommand:   accept(message); break;
            case PromiseCommand:  promise(message); break;
            case LearnCommand:    learn(message); break;
            case ResponseCommand: response(message); break;
        }
    }

    private void request(Message msg) {
        System.out.println("Node " + " received request: " + msg.toString());
        promisedThreshold = 0;
        ProposalCount++;
        Message prepare = new Message();
        prepare.setSubject(PrepareCommand);
        prepare.setHeader(String.valueOf(ProposalCount));
        prepare.setContent(msg.getContent());
        prepare.setTo(msg.getFrom());
        prepare.setFrom(getUuid());
        broadcast(prepare);
        System.out.println("Node " + " sent prepare: " + prepare.toString());
    }

    private void prepare(Message msg) {
        System.out.println("Node " + " received prepare: " + msg.toString());
        int proposal = Integer.parseInt(msg.getHeader());
        if(proposal >= ProposalCount) {
            ProposalCount = proposal;
            Message promise = new Message();
            promise.setSubject(PromiseCommand);
            promise.setHeader(String.valueOf(proposal));
            promise.setContent(msg.getContent());
            promise.setTo(msg.getFrom());
            promise.setFrom(getUuid());
            API.send(promise, msg.getFrom());
            System.out.println("Node " + getUuid() + " sent promise: " + promise.toString());
        }
    }

    private void accept(Message msg) {
        int proposal = Integer.parseInt(msg.getHeader());
        if(proposal >= ProposalCount) {
            ProposalCount = proposal;
            value = Integer.parseInt(msg.getContent());
            Message learn = new Message();
            learn.setSubject(LearnCommand);
            learn.setHeader(String.valueOf(proposal));
            learn.setContent(value.toString());
            learn.setFrom(getUuid());
            learn.setTo(msg.getFrom());
            API.broadcast(learn);
            System.out.println("Node " + getUuid() + " sent learn: " + learn.toString());
        }
    }

    private void promise(Message msg) {
        promisedThreshold++;
        if(acceptedThreshold <= promisedThreshold){
            promisedThreshold=0;
            Message accept = new Message();
            accept.setSubject(AcceptCommand);
            accept.setHeader(msg.getHeader());
            accept.setContent(msg.getContent());
            accept.setTo(msg.getFrom());
            accept.setFrom(getUuid());
            broadcast(accept);
            System.out.println("Node " + getUuid() + " sent accept: " + accept.toString());
        }
    }

    private void learn(Message msg) {
        int proposal = Integer.parseInt(msg.getHeader());
        if(proposal == ProposalCount) {
            value = Integer.parseInt(msg.getContent());
            System.out.println("Node " + getUuid() + " learned value: " + value);
        }
    }   

    private void response(Message msg) {
        System.out.println("Node " + getUuid() + " received response: " + msg.toString());
    }
}
