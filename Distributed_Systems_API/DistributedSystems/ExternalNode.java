import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ExternalNode extends Node {

    private InetSocketAddress address;
    private Socket externalConnnection;

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

    public void forward(Message msg) {
        if(!isConnected()) { return;}
        
        try {
            PrintWriter out = new PrintWriter(externalConnnection.getOutputStream(), true);
            out.print(msg.toString());
        } catch (IOException e) {
            System.out.println("Error sending message to external node: " + address.toString());
            e.printStackTrace();
        }
    }

    public InetSocketAddress getAddress() {
        return address;
    }   

    public boolean isConnected() {
        return externalConnnection.isConnected();
    }
}