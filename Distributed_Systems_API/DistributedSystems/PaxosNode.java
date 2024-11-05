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
    private Integer value = null;

    @Override
    public boolean register() {
        if(super.register()) {
            ConnectedNodes.add(this);
            return true;
        }
        return false;
    }


    /**
     * Receives a message and processes it based on its subject.
     *
     * @param message the message to be processed
     */
    @Override
    public void receive(Message message) {
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
        ProposalCount++;
        Message prepare = new Message();
        prepare.setSubject(PrepareCommand);
        prepare.setHeader(String.valueOf(ProposalCount));
        prepare.setContent(msg.getContent());
        prepare.setTo(msg.getFrom());
        API.broadcast(prepare);
    }

    private void prepare(Message msg) {
        int proposal = Integer.parseInt(msg.getHeader());
        if(proposal > promisedThreshold) {
            promisedThreshold = proposal;
            Message promise = new Message();
            promise.setSubject(PromiseCommand);
            promise.setHeader(String.valueOf(proposal));
            promise.setContent(value != null ? value.toString() : "");
            promise.setTo(msg.getFrom());
            API.send(promise, msg.getFrom());
        }
    }

    private void accept(Message msg) {
        int proposal = Integer.parseInt(msg.getHeader());
        if(proposal >= promisedThreshold) {
            promisedThreshold = proposal;
            value = Integer.parseInt(msg.getContent());
            Message learn = new Message();
            learn.setSubject(LearnCommand);
            learn.setHeader(String.valueOf(proposal));
            learn.setContent(value.toString());
            API.broadcast(learn);
        }
    }

    private void promise(Message msg) {
        int proposal = Integer.parseInt(msg.getHeader());
        if(proposal == ProposalCount) {
            int acceptedValue = msg.getContent().isEmpty() ? 0 : Integer.parseInt(msg.getContent());
            if(acceptedValue > value) {
                value = acceptedValue;
            }
            Message accept = new Message();
            accept.setSubject(AcceptCommand);
            accept.setHeader(String.valueOf(proposal));
            accept.setContent(value.toString());
            API.broadcast(accept);
        }
    }

    private void learn(Message msg) {
        int proposal = Integer.parseInt(msg.getHeader());
        if(proposal == ProposalCount) {
            value = Integer.parseInt(msg.getContent());
        }
    }   

    private void response(Message msg) {
        System.out.println("Node " + getUuid() + " received response: " + msg.toString());
    }
}
