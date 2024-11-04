import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

// Handle message sending before that connectionToSimulator is allocated
// Add a function to create the Socket before Thread starts

class NetworkServer extends Thread {
    private final int port;
    private ServerSocket server = null;
    protected Socket connectionToSimulator = null;
    protected PriorityQueue<Message> buffer = new PriorityQueue<>();
    protected Node node = null;

    public NetworkServer(Node n, int port) {
        this.port = port;
        this.node = n;
        open();
    }

    public synchronized void flush() {
        for(Message msg : buffer) {
            node.receive(msg);
        }
        buffer.clear();
    }

    public synchronized void retreive(int count) {
        if(count < 0 || count > buffer.size()) { flush(); return; }
        else {
            for(int c = 0; c < count; ++c) {
                node.receive(buffer.poll());
            }
        }
    }

    public Socket getConnection() {
        return connectionToSimulator;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        do {
            // Wait until server is opened
            boolean isConnected = false;
            do {
                synchronized(this) {
                    isConnected = server != null;
                }
            } while(!isConnected);

            // Accept simulator connection
            try {
                connectionToSimulator = server.accept();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }   
            
            // continously read from the server
            while(!interrupted() && server != null && connectionToSimulator != null) {
                update();
            }

        } while (!interrupted());
    }

    public synchronized boolean open() {
        if (server != null) { return true; }

        try {
            server = new ServerSocket(port);
            // connectionToSimulator should be null
            return true;
        } catch (IOException e) {
            server = null;
            
            System.out.println("Server " + port + ": Error opening server.") ;
            e.printStackTrace();
            return false;
        }
    }

    public synchronized void close(){
        if (server == null) { return; }

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

    protected void update() {
        if(connectionToSimulator == null) { return; }

        InputStream input;
        try {
            input = connectionToSimulator.getInputStream();
            int availability = input.available(); 
            if(availability > 0) {
                String data = new String(input.readNBytes(availability));
                for(String raw : data.split("\n")) {
                    Message msg = Message.BasicFormatter.fromString(raw);
                    if(!msg.isEmpty()) {
                        msg.setTimestamp(requestTimeStamp());
    
                        synchronized(this) {
                            buffer.add(msg);
                        }
                        
                        System.out.println("Client " + port + " - Read: " + msg.toString()) ;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Server " + port + ": Error reading from server.");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static private long TimeStampCounter = 0;
    static private ReentrantLock TimeStampLock = new ReentrantLock();
    static protected long requestTimeStamp() {
        long time = -1;
        TimeStampLock.lock();
        time = TimeStampCounter++;
        TimeStampLock.unlock();

        return time;
    }
}