

public class Test {
    public static void main(String[] args) {
        Node node1 = new Node();
        Node node2 = new Node();
        node1.register();
        node2.register();

        Message msg = new Message();
        msg.setSubject("test");
        msg.setHeader("Houston we have a problem!");
        msg.setContent("Hello, World!");
        msg.setTo(node2.getUuid());
        node1.send(msg);

        System.out.println("----- Round 1 ----- ");

        API.simulate();

        System.out.println("----- Round 2 ----- ");

        API.simulate();

    }
}