package DistributedSystems_API;

public class DistributedSystemsTest {
    

    public static void main(String[] args) {
        Channel channel = Channel.open();

        Message m = new Message();
        m.setSubject("Testing");
        m.setHeader("this is a header");
        m.setContent("bla bla bla");

        channel.send(m);
        channel.update();

        for(Message msg : channel.retreiveAll()) {
            System.out.println("Received:" + msg.toString());
        }

        channel.close();

        System.out.println("So?");
        
    }
}
