import java.net.InetSocketAddress;

/**
 * The SimulatorNode class extends the Node class and handles different types of messages
 * such as connection, unicast, and broadcast actions.
 */
public class SimulatorNode extends Node {

    /**
     * The action string for connection messages.
     */
    public static final String ConnectionAction = "connect";

    /**
     * The action string for unicast messages.
     */
    private static final String UnicastAction = "unicast";

    /**
     * The action string for broadcast messages.
     */
    private static final String BroadcastAction = "broadcast";

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
            response.setSubject(ConnectionAction);
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