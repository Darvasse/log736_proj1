import java.util.UUID;

/**
 * Represents a node in a distributed system.
 * Each node has a unique identifier (UUID) and can send and receive messages.
 */
public class Node {
    
    protected final UUID uuid = UUID.randomUUID();

    /**
     * Constructs a new Node with a unique identifier.
     */
    public Node() {}

    /**
     * Registers the node with the API.
     * 
     * @return true if the registration was successful, false otherwise.
     */
    public boolean register() {
        return API.register(this);
    }

    /**
     * Receives a message and processes it.
     * If the message subject is not "answer", it sends a response back to the sender.
     * 
     * @param message the message received by the node.
     */
    public void receive(Message message) {
        System.out.println("Node " + uuid + " received message: " + message.toString());
    }
    
    /**
     * Sends a message to another node.
     * 
     * @param message the message to be sent.
     * @return true if the message was successfully sent, false otherwise.
     */
    public boolean send(Message message) {
        if(message.getTo() == null) { return false; }
        
        message.setFrom(uuid);
        API.send(message, message.getTo());
        return true;
    }

    public void broadcast(Message message) {
        message.setFrom(uuid);
        API.broadcast(message);
    }

    /**
     * Gets the unique identifier of the node.
     * 
     * @return the UUID of the node.
     */
    public UUID getUuid() {
        return uuid;
    }

}
