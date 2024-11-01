import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;



class NetworkServer extends Thread {
    private final int port;
    private ServerSocket server = null;
    protected Socket connectionToSimulator = null;
    protected PriorityQueue<Message> buffer = new PriorityQueue<>();
    protected Node node = null;

    private NetworkServer(Node n, int port) {
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
            connectionToSimulator = server.accept();   
            
            // continously read from the server
            while(!interrupted() && server != null) {
                update();
            }

        } while (!interrupted());
    }

    public synchronized boolean open() {
        if (server != null) { return true; }

        try {
            server = new ServerSocket(port);
            // connectionToSimulator should be null
        } catch (IOException e) {
            server = null;
            
            System.out.println("Server " + port + ": Error opening server.") ;
            e.printStackTrace();
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

        InputStream input =  connectionToSimulator.getInputStream();
        int availability = input.available(); 
        if(availability > 0) {
            String data = new String(input.readNBytes(availability));
            for(String raw : data.split("\n")) {
                Message msg = Message.fromString(raw);
                if(!msg.isEmpty()) {
                    msg.setTimestamp(requestTimeStamp());

                    synchronized(this) {
                        buffer.add(msg);
                    }
                    
                    System.out.println("Client " + port + " - Read: " + msg.toString()) ;
                }
            }
        }
    }

    static protected long requestTimeStamp() {
        static long timestamp = 0;
        static ReentrantLock lock = new ReentrantLock();

        long time = -1;
        lock.lock();
        time = timestamp++;
        lock.unlock();

        return time;
    }
}