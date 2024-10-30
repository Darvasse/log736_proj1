public class NetworkSimulator {
    

    enum SimulationType {
        Sync,
        Async
    }
    
    private final int BasePort;
    private final int MaxActivePorts;
    private PriorityQueue<Integer> availablePorts;
    private SimulationType type = SimulationType.Sync;
    private NetworkSimulatorServer simulatorServer;
    // Each server is associated with a node managing its communication
    private HashMap<UUID, NetworkServer> servers = new HashMap<>();
    private HashMap<UUID, Node> registeredNodes = new ArrayList<>();

    

    public NetworkSimulator(int basePort, int maxActivePorts) {
        BasePort = basePort;
        MaxActivePorts = maxActivePorts;
        availablePorts = new PriorityQueue<Integer>(
                IntStream.rangeClosed(BasePort + 1, BasePort + MaxActivePorts).boxed().toList()
            );

        simulatorServer = new NetworkSimulatorServer(BasePort);
    }

    public void setSimulationType(SimulationType type) {
        this.type = type;
    }

    public void simulateOnce() {
        switch (type) {
            case Sync:  updateAll();    break;
            case Async: updateOne();    break;
        }
    }

    public void simulate() {
        updateAll();
    }

    public void simulate(int steps) {
        switch (type) {
            case Sync:  updateAll();    break;
            case Async: {
                for(int i = 0; i < steps; ++i) {
                    updateOne();
                }
                break;
            }
        }
    }

    public void register(Node n) {
        registeredNodes.add(n);
    }

    public Socket getServer(int port) {
        if(servers.containsKey(port)) {
            return servers.get(port).connection;
        }
        return null;
    }

    private void updateAll() {
        for(Channel c : channels) {
            c.update();
        }

        for(Node n : registeredNodes) {
            n.update();
        }
    }

    private void updateOne() {

    }

    private boolean isValidPort(int port) {
        return port > BasePort && port < BasePort + MaxActivePorts;
    }

    private int getAvailablePort() {
        return availablePorts.isEmpty() ? BasePort : availablePorts.poll();  
    }

}