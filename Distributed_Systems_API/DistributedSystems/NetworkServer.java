
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * NetworkServer is a class that extends Thread and is responsible for handling network connections.
 * It listens on a specified port and accepts connections from a simulator.
 * It reads messages from the connection and stores them in a buffer.
 * The messages can be retrieved and processed by the associated Node.
 */
class NetworkServer extends Thread {
    /**
     * The port number on which the server listens.
     */
    private final int port;

    /**
     * The ServerSocket used to accept connections.
     */
    private ServerSocket server = null;

    /**
     * The Socket representing the connection to the simulator.
     */
    protected Socket connectionToSimulator = null;

    /**
     * A PriorityQueue used to buffer incoming messages.
     */
    protected PriorityQueue<Message> buffer = new PriorityQueue<>();

    /**
     * The Node associated with this server.
     */
    protected Node node = null;

    protected boolean isLocked = false;

    /**
     * Constructs a NetworkServer with the specified Node and port.
     *
     * @param n the Node associated with this server
     * @param port the port number on which the server listens
     */
    public NetworkServer(Node n, int port) {
        this.port = port;
        this.node = n;
        open();
    }

    /**
     * Flushes all messages in the buffer to the associated Node.
     */
    public synchronized void flush() {
        for (Message msg : buffer) {
            node.receive(msg);
        }
        buffer.clear();
    }

    /**
     * Retrieves a specified number of messages from the buffer and sends them to the associated Node.
     * If the count is negative or greater than the buffer size, all messages are flushed.
     *
     * @param count the number of messages to retrieve
     */
    public synchronized void retreive(int count) {
        if (count < 0 || count > buffer.size()) {
            flush();
            return;
        } else {
            for (int c = 0; c < count; ++c) {
                node.receive(buffer.poll());
            }
        }
    }

    /**
     * Returns the Socket representing the connection to the simulator.
     *
     * @return the Socket representing the connection to the simulator
     */
    public Socket getConnection() {
        return connectionToSimulator;
    }

    /**
     * Returns the port number on which the server listens.
     *
     * @return the port number on which the server listens
     */
    public int getPort() {
        return port;
    }

    /**
     * Checks if the server is open and the connection to the simulator is active.
     *
     * @return true if the server is open and the connection to the simulator is active, false otherwise
     */
    public boolean isOpen() {
        return server != null && !server.isClosed() && connectionToSimulator != null && !connectionToSimulator.isClosed();
    }

    /**
     * The main run method of the thread. It waits for the server to be opened,
     * accepts connections from the simulator, and continuously reads from the server.
     */
    @Override
    public void run() {
        do {
            // Wait until server is opened
            boolean isConnected = false;
            do {
                synchronized (this) {
                    isConnected = server != null;
                }
            } while (!isConnected);

            // Accept simulator connection
            try {
                connectionToSimulator = server.accept();
                System.out.println("Server " + port + ": Accepted connection from " + connectionToSimulator.getRemoteSocketAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Continuously read from the server
            while (!interrupted() && isOpen()) {
                synchronized (this) {
                    if(isLocked) { continue; }
                }
                update();
            }

        } while (!interrupted());
    }

    /**
     * Opens the server socket on the specified port.
     *
     * @return true if the server was successfully opened, false otherwise
     */
    public synchronized boolean open() {
        if (server != null) {
            return true;
        }

        try {
            server = new ServerSocket(port);
            return true;
        } catch (IOException e) {
            server = null;
            System.out.println("Server " + port + ": Error opening server.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Closes the server socket and the connection to the simulator.
     */
    public synchronized void close() {
        if (server == null) {
            return;
        }

        try {
            connectionToSimulator.close();
            server.close();
            server = null;
            connectionToSimulator = null;
        } catch (IOException e) {
            System.out.println("Server " + port + ": Error closing server.");
            e.printStackTrace();
        }
    }

    public synchronized void lock() {
        isLocked = true;
    }
    public synchronized void unlock() {
        isLocked = false;
    }   

    /**
     * Reads data from the connection to the simulator and processes incoming messages.
     */
    protected synchronized void update() {
        if (connectionToSimulator == null) {
            return;
        }

        InputStream input;
        try {
            input = connectionToSimulator.getInputStream();
            int availability = input.available();
            if (availability > 0) {
                System.out.println("Server " + port + " - Reading " + availability + " bytes.");
                String data = new String(input.readNBytes(availability));
                for (String raw : data.split("\n")) {
                    Message msg = Message.BasicFormatter.fromString(raw);
                    if (!msg.isEmpty()) {
                        buffer.add(msg);

                        System.out.println("Client " + port + " - Read: " + msg.toString());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Server " + port + ": Error reading from server.");
            e.printStackTrace();
        }
    }

    
}