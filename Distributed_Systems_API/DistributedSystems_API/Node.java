package DistributedSystems_API;

import java.util.UUID;

public class Node {
    
    protected Channel connection = null;
    protected final UUID uuid = UUID.randomUUID();


    public Node() {
        NetworkSimulator.network.register(this);
    }


    public boolean connect(Channel channel) {
        if(isConnected()) { 
            Message disconnecting = new Message();
            disconnecting.setSubject("disconnect");
            disconnecting.setHeader(uuid.toString());
            disconnecting.setContent("Node " + uuid.toString() + " is disconnecting");
            connection.send(disconnecting);
        }

        connection = channel;

        return isConnected();
    }

    public Channel open() {
        if(!isConnected()) {
            connection = Channel.open();
        }
        return connection;
    }

    public void close() {
        connection = null;
    }

    public void update() {}

    public boolean isConnected() {
        return connection != null && connection.isOpened();
    }
    
    public boolean send(Message message) {
        if(isConnected()) {
            return connection.send(message);
        }
        return false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Channel getChannel() {
        return connection;
    }
}
