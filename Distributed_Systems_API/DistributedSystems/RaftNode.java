public class RaftNode extends Node{
    
    /**
     * Accepted actions' subject
     */
    public static final String ConnectionAction = "connect";
    public static final String UnicastAction = "unicast";
    public static final String BroadcastAction = "broadcast";

    /**
     * Receives a message and processes it based on its subject.
     *
     * @param message the message to be processed
     */
    @Override
    public void receive(Message message) {
        switch(message.getSubject()) {
            case ConnectionAction:  handleConnection(message); break;
            case UnicastAction:     API.send(message, message.getTo()); break;
            case BroadcastAction:   API.broadcast(message); break;
        }
    }
}
