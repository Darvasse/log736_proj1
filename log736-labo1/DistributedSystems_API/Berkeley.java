package DistributedSystems_API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Berkeley extends Node {
    static final private String GetTimeCMD = "GetTime";
    static final private String SendTimeCMD = "SendTime"; // Args: Time in nano seconds
    static final private String SynchronizeTimeCMD = "Sync"; // Args: Offset in nano seconds

    private long lastOffset = 0;
    private long time = System.currentTimeMillis();
    private boolean isLeader = false;
    private long synchronizationThreshold = Long.MAX_VALUE;
    private HashMap<UUID, Message> nodeTimes = new HashMap<>(); // channel uuid to time
    private ArrayList<Channel> network = new ArrayList<>();

    public Berkeley() {}

    public void requestTime(ArrayList<Channel> network, long seuil) {
        if(isLeader) {
            this.network = network;
            nodeTimes.clear();
            synchronizationThreshold = Math.abs(seuil);
            time = System.nanoTime();
            

            for (Channel node : this.network) {
                Message request = new Message();
                request.setSubject(GetTimeCMD);
                request.setHeader(uuid.toString());
                
                node.send(request);
            }
        }
    }

    public void update() {
        /*
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
        */
    }

    public void setLeadership(boolean isLeader) {
        this.isLeader = isLeader;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    private void sendTime() {
        Message sendingTime = new Message();
        sendingTime.setSubject(SendTimeCMD);
        sendingTime.setHeader(uuid.toString());
        sendingTime.setContent(String.valueOf(time));
        send(sendingTime);
    }

    private void finishSynchronisation() {
        ArrayList<Message> offsets = connection.retreiveFromSubject(SynchronizeTimeCMD);
        for(int i= offsets.size() - 1; i >= 0; --i) {
            Message msg = offsets.get(i);
            if(msg.getHeader().equals(uuid.toString())) {
                long offset = Long.valueOf(msg.getContent());
                time += offset;
                break;
            }
        }
    }

    private void processReceivedTime() {
        // Retreive time from all channels

        for(Channel c : network) {
            ArrayList<Message> messages = c.retreiveFromSubject(SendTimeCMD);
            if(!messages.isEmpty()) {
                nodeTimes.put(c.getUuid(), messages.getLast());
            }
        }

        /*
        ArrayList<Message> 
        for(Socket node : nodes) {
            if(!nodeTimes.containsKey(node.getLocalPort())) {
                DataInputStream input = new DataInputStream(node.getInputStream());
                if(input.available() > 0) {
                    String[] data = input.readUTF().split(" ");
                    if(data.length == 2 && data[0] == SendTimeCMD) {
                        nodeTimes.put(node.getLocalPort(), Long.parseLong(data[1]));
                    }
                }
            }
        } 
        */  
    }

    private void updateSynchronisation() {
        if(nodeTimes.size() == network.size()) {
            long offset = 0;
            long counted = 0;
            for(Message message : nodeTimes.values()) {
                long nodeTime = Long.valueOf(message.getContent());
                long localOffset = time - nodeTime;
                if (Math.abs(localOffset) <= synchronizationThreshold) {
                    offset += localOffset;
                    ++counted;
                } 
            }
            offset /= counted;
            
            for(Channel node : network) {
                Message received = nodeTimes.get(node.getUuid());
                long nodeTime = Long.valueOf(received.getContent());

                long localOffset = time - nodeTime;
                Message sendingOffset = new Message();
                sendingOffset.setSubject(SynchronizeTimeCMD);
                sendingOffset.setHeader(received.getHeader());
                sendingOffset.setContent(String.valueOf(localOffset));
                
                node.send(sendingOffset);
            }
            time += offset;
            this.lastOffset = offset;
        }
        
    }
}
