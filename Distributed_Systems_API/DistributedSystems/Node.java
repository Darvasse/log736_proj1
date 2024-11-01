package DistributedSystems;

import java.util.UUID;

public class Node {
    
    protected final UUID uuid = UUID.randomUUID();

    public Node() {}

    public boolean register() {
        return API.register(this);
    }

    public void receive(Message message) {
        System.out.println("Node " + uuid + " received message: " + message.toString());
    }
    
    public boolean send(Message message) {
        message.setFrom(uuid);
        return API.send(message);
    }

    public UUID getUuid() {
        return uuid;
    }

}
