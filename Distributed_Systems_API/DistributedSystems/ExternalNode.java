import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * The ExternalNode class represents a node that connects to an external system via a socket.
 * It extends the Node class and provides functionality to forward messages to the external system.
 */
public class ExternalNode extends Node {

    private InetSocketAddress address;
    private Socket externalConnnection;

    /**
     * Constructs an ExternalNode with the specified address.
     * Attempts to establish a connection to the external node.
     *
     * @param address the address of the external node
     */
    public ExternalNode(InetSocketAddress address) {
        super();
        this.address = address;
        externalConnnection = new Socket();
        try {
            externalConnnection.connect(address, 1000);
        } catch (IOException e) {
            System.out.println("Error connecting to external node: " + address.toString());
            e.printStackTrace();
        }
    }

    /**
     * Receives a message and forwards it to the external node.
     *
     * @param message the message to be received and forwarded
     */
    @Override
    public void receive(Message message) {
        super.receive(message);
        forward(message);
    }

    /**
     * Forwards the specified message to the external node.
     *
     * @param msg the message to be forwarded
     */
    public void forward(Message msg) {
        if(!isConnected()) { return;}
        
        try {
            OutputStream output = externalConnnection.getOutputStream();
            output.write((msg.toString() + "\n").getBytes());
            output.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to external node: " + address.toString());
            e.printStackTrace();
        }
    }

    /**
     * Returns the address of the external node.
     *
     * @return the address of the external node
     */
    public InetSocketAddress getAddress() {
        return address;
    }   

    /**
     * Checks if the connection to the external node is established.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return externalConnnection.isConnected();
    }
}