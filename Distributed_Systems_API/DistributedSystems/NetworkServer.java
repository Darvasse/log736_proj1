import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;



class NetworkServer extends Thread {
    private     final int           port;
    private     ServerSocket        server                  = null;
    protected   Socket              connectionToSimulator   = null;
    protected   ArrayList<Message>  buffer                  = new ArrayList<>();

    private NetworkServer(int port) {
        this.port = port;
        open();
    }

    public ArrayList<Message> retreive() {
        ArrayList<Message> messages = buffer;
        buffer = new ArrayList<>();
        return messages;
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

    private void update() {
        if(connectionToSimulator == null) { return; }

        InputStream input =  connectionToSimulator.getInputStream();
        int availability = input.available(); 
        if(availability > 0) {
            String data = new String(input.readNBytes(availability));
            for(String raw : data.split("\n")) {
                Message msg = Message.fromString(raw);
                if(!msg.isEmpty()) {
                    msg.setTimestamp(requestTimeStamp());
                    buffer.add(msg);
                    if(!bySubject.containsKey(msg.getSubject())) {
                        bySubject.put(msg.getSubject(), new ArrayList<>());
                    }
                    bySubject.get(msg.getSubject()).add(msg);
                    System.out.println("Client " + client.getPort() + " - Read: " + msg.toString()) ;
                }
            }
        }
    }

    static private long requestTimeStamp() {
        static long timestamp = 0;
        static ReentrantLock lock = new ReentrantLock();

        long time = -1;
        lock.lock();
        time = timestamp++;
        lock.unlock();

        return time;
    }
}