
// This class is a facade for the NetworkSimulator class. It is a singleton

import java.util.UUID;

class API {

    private NetworkSimulator network;
    public final static int BasePort = 25000;

    private static API instance = new API();

    private API() {
        network = new NetworkSimulator(BasePort, 1000);
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
        msg.setTo(destination);
        instance.network.unicast(destination, msg);
    }

    public static void broadcast(Message msg) {
        instance.network.broadcast(msg);
    }

}