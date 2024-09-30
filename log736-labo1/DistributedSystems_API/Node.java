package DistributedSystems_API;

public class Node {
    
    private Channel connection = null;


    public Node() {
        
    }


    public boolean connect(Channel channel) {
        if(isConnected()) { close(); }

        return false;
    }

    public void open() {

    }

    public void close() {

    }

    public void update() {
        connection.update();

    }

    public boolean isConnected() {
        return false;
    }
    
    public boolean send(Message message) {
        return false;
    }

    public Channel getChannel() {
        return connection;
    }
}
