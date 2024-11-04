import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

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
            servers.put(n.getUuid(), new NetworkServer(n, port));
            
        }
        return port;
    }

    public void unicast(UUID destination, Message msg) {
        if(!servers.containsKey(destination)) {
            System.out.println("Destination not found.");
            return;
        }

        NetworkServer server = servers.get(destination); 

        try {
            Socket connection = server.getConnection();
            PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
            out.print(msg.toString());
        } catch (IOException e) {
            System.out.println("Error sending message in port " + server.getPort());
            e.printStackTrace();

        }
    }

    public void broadcast(Message msg) {
        for(NetworkServer server : servers.values()) {
            try {
                Socket connection = server.getConnection();
                PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
                out.print(msg.toString());
            } catch (IOException e) {
                System.out.println("Error sending message in port " + server.getPort());
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
        for(NetworkServer server : servers.values()) {
            switch (type) {
                case Sync:  server.retreive(-1);    break;
                case Async: server.retreive(1);     break;
            }
        }
    }

    private void updateOne() {
        switch (type) {
            case Sync: {
                for(NetworkServer server : servers.values()) {
                    server.retreive(1);
                }
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

}