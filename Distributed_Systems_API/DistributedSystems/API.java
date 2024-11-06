import java.util.UUID;

class API {

    private NetworkSimulator network;
    public final static int BasePort = 25000;

    private static API instance = new API();

    private API() {
        network = new NetworkSimulator(BasePort, 1000);
    }

    /**
     * Sets the simulation type for the network simulator.
     *
     * @param type The type of simulation to be set.
     */
    public static void setSimulationType(NetworkSimulator.SimulationType type) {
        instance.network.setSimulationType(type);
    }

    /**
     * Simulate one round of communication.
     */
    public static void simulate() {
        instance.network.simulate();
    }

    /**
     * Simulate one iteration of communication.
     */
    public static void simulateOnce() {
        instance.network.simulateOnce();
    }


    /**
     * Registers a node with the network simulator.
     *
     * @param node The node to be registered.
     * @return true if the node was successfully registered, false otherwise.
     */
    public static boolean register(Node node) {
        return instance.network.connect(node) != null;
    }

    /**
     * Sends a message to a specific node identified by its UUID.
     *
     * @param msg The message to be sent.
     * @param destination The UUID of the destination node.
     */
    public static void send(Message msg, UUID destination) {
        msg.setTo(destination);
        instance.network.unicast(destination, msg);
    }

    /**
     * Broadcasts a message to all nodes in the network.
     *
     * @param msg The message to be broadcasted.
     */
    public static void broadcast(Message msg) {
        instance.network.broadcast(msg);
    }

}