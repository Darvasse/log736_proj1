

public class Test {
    public static void main(String[] args) {
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
}