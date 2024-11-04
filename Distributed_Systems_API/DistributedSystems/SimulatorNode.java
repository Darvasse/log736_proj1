import java.net.InetSocketAddress;

public class SimulatorNode extends Node {

    public static final String ConnectionAction = "connect";
    private static final String UnicastAction = "unicast";
    private static final String BroadcastAction = "broadcast";
    

    @Override
    public void receive(Message message) {
        switch(message.getSubject()) {
            case ConnectionAction:  handleConnection(message); break;
            case UnicastAction:     API.send(message, message.getTo()); break;
            case BroadcastAction:   API.broadcast(message); break;
        }
    }

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