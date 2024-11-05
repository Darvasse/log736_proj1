import java.net.InetSocketAddress;

/**
 * The SimulatorNode class extends the Node class and handles different types of messages
 * such as connection, unicast, and broadcast actions.
 */
public class SimulatorNode extends Node {

    /**
     * Accepted actions' subject
     */
    public static final String ConnectionCommand = "connect";
    public static final String UnicastCommand = "unicast";
    public static final String BroadcastCommand = "broadcast";

    /**
     * Receives a message and processes it based on its subject.
     *
     * @param message the message to be processed
     */
    @Override
    public void receive(Message message) {
        switch(message.getSubject()) {
            case ConnectionCommand:  handleConnection(message); break;
            case UnicastCommand:     API.send(message, message.getTo()); break;
            case BroadcastCommand:   API.broadcast(message); break;
        }
    }

    /**
     * Handles connection messages by creating an external node and attempting to register it.
     * Sends a response message indicating success or error.
     *
     * @param msg the connection message to be handled
     */
    private void handleConnection(Message msg) {
        InetSocketAddress externalAddress = new InetSocketAddress(msg.getHeader(), Integer.parseInt(msg.getContent()));
        ExternalNode external = new ExternalNode(externalAddress);
        if(external.isConnected()) {
            Message response = new Message();
            response.setSubject(ConnectionCommand);
            if(external.register()) {
                response.setHeader("Success");
                response.setContent(external.getUuid().toString());
            } else {
                response.setHeader("Error");
            }

            external.forward(response);
        }
    }
}