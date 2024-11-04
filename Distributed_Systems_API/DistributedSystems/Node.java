import java.util.UUID;

public class Node {
    
    protected final UUID uuid = UUID.randomUUID();

    public Node() {}

    public boolean register() {
        return API.register(this);
    }

    public void receive(Message message) {
        System.out.println("Node " + uuid + " received message: " + message.toString());

        if(message.getSubject() != "answer") {
            Message response = new Message();
            response.setSubject("answer");
            response.setHeader("Message received!");
            response.setContent(message.getHeader() + " /// " + message.getContent());
            response.setTo(message.getFrom());
            send(response);
        }
    }
    
    public boolean send(Message message) {
        if(message.getTo() == null) { return false; }
        
        message.setFrom(uuid);
        API.send(message, message.getTo());
        return true;
    }

    public UUID getUuid() {
        return uuid;
    }

}
