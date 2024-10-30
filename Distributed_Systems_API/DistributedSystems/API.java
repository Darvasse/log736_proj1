
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

    public static void register(Node node) {
        instance.network.register(node);
    }

    public static void send(Message msg) {
        instance.network.send(msg);
    }

}