class SimulatorNode extends Node {

    public static final String ConnectionAction = "connect";
    private static final String UnicastAction = "unicast";
    private static final String BroadcastAction = "broadcast";

    @Override
    public void receive(Message message) {
        for(Message msg : buffer) {
            switch(msg.getSubject()) {
                case ConnectionAction:  handleConnection(msg); break;
                case UnicastAction:     simulator.unicast(msg.getTo(), msg); break;
                case BroadcastAction:   simulator.broadcast(msg); break;
            }
        }
    }

    private void handleConnection(Message msg) {
        InetSocketAddress externalAddress = new InetSocketAddress(msg.getHeader(), msg.getContent());
        ExternalNode external = new ExternalNode(externalAddress);
        if(external.isConnected()) {
            Message response = new Message();
            response.setSubject(ConnectionAction);
            if(external.register()) {
                response.setHeader("Success");
                response.setContent(external.getUUID().toString());
            } else {
                response.setHeader("Error");
            }

            external.forward(response);
        }
    }
}