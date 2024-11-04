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

    public Berkeley() { super(); }

    public void requestTime(ArrayList<Channel> network, long seuil) {
        if(isLeader) {
            this.network = network;
            nodeTimes.clear();
            synchronizationThreshold = Math.abs(seuil);

            for (Channel node : this.network) {
                Message request = new Message();
                request.setSubject(GetTimeCMD);
                request.setHeader(uuid.toString());
                
                node.send(request);
            }
        }
    }

    public void update() {
        super.update();

        if(isLeader) {
            processReceivedTime();
            updateSynchronisation();
        } else {
            sendTime();
            finishSynchronisation();
        }
        
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
        ArrayList<Message> messages = connection.retreiveFromSubject(GetTimeCMD);
        if(!messages.isEmpty()) {
            Message sendingTime = new Message();
            sendingTime.setSubject(SendTimeCMD);
            sendingTime.setHeader(uuid.toString());
            sendingTime.setContent(String.valueOf(time));
            send(sendingTime);
        }
    }

    private void finishSynchronisation() {
        ArrayList<Message> offsets = connection.retreiveFromSubject(SynchronizeTimeCMD);
        for(int i= offsets.size() - 1; i >= 0; --i) {
            Message msg = offsets.get(i);
            if(msg.getHeader().equals(uuid.toString())) {
                this.lastOffset = Long.valueOf(msg.getContent());
                time += lastOffset;

                break;
            }
        }
    }

    private void processReceivedTime() {
        for(Channel c : network) {
            ArrayList<Message> messages = c.retreiveFromSubject(SendTimeCMD);
            if(!messages.isEmpty()) {
                nodeTimes.put(c.getUuid(), messages.getLast());
            }
        } 
    }

    private void updateSynchronisation() {
        if(nodeTimes.size() == network.size()) {
            long offset = 0;
            long counted = 1;
            for(Message message : nodeTimes.values()) {
                long nodeTime = Long.valueOf(message.getContent());
                long localOffset = nodeTime - time;
                if (Math.abs(localOffset) <= synchronizationThreshold) {
                    offset += localOffset;
                    ++counted;
                } 
            }
            offset /= counted;
            time += offset;
            this.lastOffset = offset;
            
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
        }
        
    }

    public long getLastOffset() {
        return lastOffset;
    }
}
