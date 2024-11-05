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
    
    }

    private void prepare(Message msg) {
    
    }

    private void accept(Message msg) {
    
    }

    private void promise(Message msg) {
    
    }

    private void learn(Message msg) {
    
    }   

    private void response(Message msg) {
    
    }
}
