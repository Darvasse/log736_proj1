public class Test {
    public static void main(String[] args) {
        raftOperationTest();
        System.out.println("Test completed.");
        System.exit(0);
    }

    public static void raftElectionTest() {
        RaftNode node1 = new RaftNode();
        RaftNode node2 = new RaftNode();
        RaftNode node3 = new RaftNode();

        node1.register();
        node2.register();
        node3.register();

        API.setSimulationType(NetworkSimulator.SimulationType.Async);
        int simulationRound = 0;
        while(simulationRound < 10) {
            System.out.println("----- Round " + simulationRound + " ----- ");
            API.simulate();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            simulationRound++;
        }
    }

    public static void raftOperationTest() {
        RaftNode node1 = new RaftNode();
        RaftNode node2 = new RaftNode();
        RaftNode node3 = new RaftNode();

        node1.register();
        node2.register();
        node3.register();

        API.setSimulationType(NetworkSimulator.SimulationType.Async);
        System.out.println("----- Simulating until leader's stability (10 rounds) ----- ");
        int simulationRound = 0;
        while(simulationRound < 10) {
            System.out.println("----- Round " + simulationRound + " ----- ");
            API.simulate();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            simulationRound++;
        }

        Node client = new Node();
        client.register();
        Message msg = new Message();
        msg.setSubject(RaftNode.ClientCommand);
        msg.setHeader(RaftNode.SetAction);
        msg.setContent("10");
        msg.setTo(node1.getUuid());

        client.send(msg);
        System.out.println("----- Simulating until client's request is processed (10 rounds) ----- ");
        while (simulationRound < 20) {
            System.out.println("----- Round " + simulationRound + " ----- ");
            API.simulate();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            simulationRound++;
        }

        System.out.println("----- Value in each node ----- ");
        System.out.println("    Node 1: " + node1.getValue());
        System.out.println("    Node 2: " + node2.getValue());
        System.out.println("    Node 3: " + node3.getValue());
    }

    
    public static void testPaxos() {
        //Pour tester Paxos
        //Vous devez créer au moins un leader et autant d'accepteur que vous souhaitez
        //Le leader se register et les accepteurs se register avec le leader ou les leaders en paramètre
        //Ensuite vous pouvez envoyer des messages en utilisant la même démarche que si dessous
        //Vous pouvez aussi terminer un accepteur ou un leader en utilisant la méthode terminate
        //Chaque tour (envoie de message) s'effectue grâce à la méthode simulate
        
        PaxosNode leader = new PaxosNode();
        PaxosNode acceptor1 = new PaxosNode();
        PaxosNode acceptor2 = new PaxosNode();
        PaxosNode acceptor3 = new PaxosNode();
        PaxosNode acceptor4 = new PaxosNode();
        PaxosNode acceptor5 = new PaxosNode();
        leader.register();
        acceptor1.register(leader);
        acceptor2.register(leader);
        acceptor3.register(leader);
        acceptor4.register(leader);
        acceptor5.register(leader);

        Message msg = new Message();
        msg.setSubject(PaxosNode.RequestCommand);
        msg.setHeader("1");
        msg.setContent("1");
        msg.setTo(leader.getUuid());
        msg.setFrom(leader.getUuid());
        API.send(msg, leader.getUuid());
        System.out.println("----- Round 1 ----- ");
        //API.setSimulationType(NetworkSimulator.SimulationType.Async);
        API.simulate();

        System.out.println("----- Round 2 ----- ");
        API.simulate();

        System.out.println("----- Round 3 ----- ");
        API.simulate();
        acceptor3.terminate();

        System.out.println("----- Round 4 ----- ");
        API.simulate();

        System.out.println("----- Round 5 ----- ");
        API.simulate();

        msg = new Message();
        msg.setSubject(PaxosNode.RequestCommand);
        msg.setHeader("2");
        msg.setContent("2");
        msg.setTo(leader.getUuid());
        msg.setFrom(leader.getUuid());
        API.send(msg, leader.getUuid());
        System.out.println("----- Round 6 ----- ");
        //API.setSimulationType(NetworkSimulator.SimulationType.Async);
        API.simulate();

        System.out.println("----- Round 7 ----- ");
        API.simulate();

        System.out.println("----- Round 8 ----- ");
        API.simulate();

        System.out.println("----- Round 9 ----- ");
        API.simulate();

        System.out.println("----- Round 10 ----- ");
        API.simulate();
    }
    
    public static void basicTest() {
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