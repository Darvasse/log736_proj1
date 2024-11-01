
// This class is a facade for the NetworkSimulator class. It is a singleton
class API {

    private NetworkSimulator network;
    public final static int BasePort = 25000;

    private static API instance = new API();

    private API() {
        network = new NetworkSimulator();
    }

    public static void setSimulationType(NetworkSimulator.SimulationType type) {
        instance.network.setSimulationType(type);
    }

    public static void simulate() {
        instance.network.simulate();
    }

    public static boolean register(Node node) {
        return instance.network.connect(node) != null;
    }

    public static void send(Message msg, UUID destination) {
        msg.setDestination(destination);
        instance.network.send(msg, destination);
    }

}