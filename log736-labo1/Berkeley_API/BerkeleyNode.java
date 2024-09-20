import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class BerkeleyNode {
    static final private String GetTimeCMD = "GetTime";
    static final private String SendTimeCMD = "SendTime"; // Args: Time in nano seconds
    static final private String SynchronizeTimeCMD = "Sync"; // Args: Offset in nano seconds

    private long time = System.nanoTime();
    private long synchronizationThreshold = Long.MAX_VALUE;
    private boolean isLeader = false;
    private ServerSocket server = null;
    private Socket client = null;
    private ArrayList<Socket> nodes = new ArrayList<>();
    private HashMap<Integer, Long> nodeTimes = new HashMap<>(); 

    public void synchronizeTime(int[] ports, long seuil) throws IOException {
        if(isLeader) {
            for(Socket node : nodes) {
                node.close();
            }
            nodes.clear();
            nodeTimes.clear();

            synchronizationThreshold = Math.abs(seuil);
            time = System.nanoTime();
            for (int port : ports) {
                Socket node = new Socket("localhost", port);

                OutputStream nodeOutput = node.getOutputStream();
                nodeOutput.write(GetTimeCMD.getBytes());
                nodeOutput.flush();

                nodes.add(node);
            }
        }
    }

    public Socket start(int port) throws IOException {
        close();

        System.out.println("Starting Server on port: " + port);

        server = new ServerSocket(port);
        client = server.accept();

        return client;
    }

    public void close() throws IOException {
        if(client.isConnected()) {
            System.out.println("Closing Server on port: " + server.getLocalPort());

            client.close();
            server.close();
        }
    }

    public void update() throws IOException {
        if(isLeader) {
            processReceivedTime();
            updateSynchronisation();
        } else {
            String[] data = readClient().split(" ");
            if(data.length > 0) {
                switch (data[0]) {
                    case SendTimeCMD: sendTime(); break;
                    case SynchronizeTimeCMD: finishSynchronisation(data); break;
                    default:break;
                }
            }
        }
    }

    public void setTime(long newTime) {
        time = newTime;
    }
    
    public long getTime() {
        return time;
    }

    public boolean isLeader() {
        return this.isLeader;
    }
    
    private void sendTime() throws IOException {
        OutputStream output = client.getOutputStream();
        output.write((SendTimeCMD + " " + this.time).getBytes());
        output.flush();
    }

    private void finishSynchronisation(String[] command) throws IOException {
        if(command.length == 2) {
            long offset = Long.parseLong(command[1]);
            this.time += offset;
        }
    }

    private void processReceivedTime() throws IOException {
        for(Socket node : nodes) {
            if(!nodeTimes.containsKey(node.getLocalPort())) {
                InputStream input = node.getInputStream();
                String[] data = new String(input.readAllBytes()).split(" ");
                if(data.length == 2 && data[0] == SendTimeCMD) {
                    nodeTimes.put(node.getLocalPort(), Long.parseLong(data[1]));
                }
            }
        }   
    }

    private void updateSynchronisation() throws IOException {
        if(nodeTimes.size() == nodes.size()) {
            long offset = 0;
            long counted = 0;
            for(Long nodeTime : nodeTimes.values()) {
                long localOffset = time - nodeTime;
                if (Math.abs(localOffset) <= synchronizationThreshold) {
                    offset += localOffset;
                    ++counted;
                } 
            }
            offset /= counted;
            
            for(Socket node : nodes) {
                OutputStream nodeOutput = node.getOutputStream();
                long localOffset = time - nodeTimes.getOrDefault(node.getLocalPort(), time);
                nodeOutput.write((SynchronizeTimeCMD + " " + localOffset).getBytes());
                nodeOutput.flush();
            }
            time += offset;
        }
    }

    private String readClient() throws IOException {
        String data = new String();
        InputStream input = client.getInputStream();
        if(input.available() > 0) {
            data = new String(input.readAllBytes());
        }
        
        return data;
    }
}
