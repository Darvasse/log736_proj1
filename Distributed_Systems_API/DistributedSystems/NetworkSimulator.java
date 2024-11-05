import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * The NetworkSimulator class simulates a network environment where nodes can connect and communicate with each other.
 * It supports both synchronous and asynchronous simulation types.
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Manages network servers and clients.</li>
 *   <li>Supports unicast and broadcast messaging.</li>
 *   <li>Simulates network interactions either synchronously or asynchronously.</li>
 * </ul>
 * 
 * <p>Usage:</p>
 * <pre>
 * {@code
 * NetworkSimulator simulator = new NetworkSimulator(basePort, maxActivePorts);
 * simulator.setSimulationType(NetworkSimulator.SimulationType.Async);
 * simulator.simulate(10);
 * }
 * </pre>
 * 
 * <p>Note:</p>
 * <ul>
 *   <li>Ensure that the base port and maximum active ports are properly configured.</li>
 *   <li>Handle exceptions during network operations appropriately.</li>
 * </ul>
 * 
 * @see NetworkServer
 * @see Node
 * @see Message
 */
public class NetworkSimulator {
    

    enum SimulationType {
        Sync,
        Async
    }
    
    private final int BasePort;
    private final int MaxActivePorts;
    private PriorityQueue<Integer> availablePorts;
    private SimulationType type = SimulationType.Sync;
    private NetworkServer simulatorServer;
    // Each server is associated with a node managing its communication
    private HashMap<UUID, NetworkServer> servers = new HashMap<>();
    private HashMap<UUID, Socket> clients = new HashMap<>();


    
    public NetworkSimulator(int basePort, int maxActivePorts) {
        BasePort = basePort;
        MaxActivePorts = maxActivePorts;
        availablePorts = new PriorityQueue<Integer>(
                IntStream.rangeClosed(BasePort + 1, BasePort + MaxActivePorts).boxed().toList()
            );

        simulatorServer = new NetworkServer(new SimulatorNode(), BasePort);
    }

// Node interactions

    public Integer connect(Node n) {
        Integer port = getAvailablePort();
        if(port != null) {
            NetworkServer server = new NetworkServer(n, port);
            server.start();
            Socket client = new Socket();

            try {
                client.connect(new InetSocketAddress(port), 1000);
                servers.put(n.getUuid(), server);
                clients.put(n.getUuid(), client);
            } catch (IOException e) {
                port = null;
            }
        }
        return port;
    }

    public void unicast(UUID destination, Message msg) {
        if(!clients.containsKey(destination)) {
            System.out.println("Destination not found.");
            return;
        }

        Socket client = clients.get(destination);
        try {
            OutputStream output = client.getOutputStream();
            output.write((msg.toString() + "\n").getBytes());
            output.flush();
        } catch (IOException e) {
            System.out.println("Error sending message in port " + client.getPort());
            e.printStackTrace();

        }
    }

    public void broadcast(Message msg) {
        for(Socket client : clients.values()) {
            try {
                OutputStream output = client.getOutputStream();
                output.write((msg.toString() + "\n").getBytes());
                output.flush();
            } catch (IOException e) {
                System.out.println("Error sending message in port " + client.getPort());
                e.printStackTrace();
            }
        }

    }

// Simulation

    public void setSimulationType(SimulationType type) {
        this.type = type;
    }

    public void simulateOnce() {
        simulatorServer.flush();

        switch (type) {
            case Sync:  updateAll();    break;
            case Async: updateOne();    break;
        }
    }

    public void simulate() {
        simulatorServer.flush();

        updateAll();
    }

    public void simulate(int steps) {
        simulatorServer.flush();

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

     private void updateAll() {
        lockServers();
        for(NetworkServer server : servers.values()) {
            switch (type) {
                case Sync:  server.retreive(-1);    break;
                case Async: server.retreive(1);     break;
            }
        }
        unlockServers();
    }

    private void updateOne() {
        switch (type) {
            case Sync: {
                lockServers();
                for(NetworkServer server : servers.values()) {
                    server.retreive(1);
                }
                unlockServers();
                break;
            }
            case Async: {
                int index = new Random().nextInt(servers.size());
                NetworkServer randomServer = servers.get(servers.keySet().toArray()[index]);
                randomServer.retreive(1);
                break;
            }
        }
    }

// Private methods
    private Integer getAvailablePort() {
        return availablePorts.isEmpty() ? null : availablePorts.poll();  
    }

    private void lockServers() {
        for(NetworkServer server : servers.values()) {
            server.lock();
        }
    }

    private void unlockServers() {
        for(NetworkServer server : servers.values()) {
            server.unlock();
        }
    }

}

