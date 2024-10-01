package DistributedSystems_API;

import java.util.ArrayList;

public class DistributedSystemsTest {
    

    public static void main(String[] args) {
        Berkeley n1 = new Berkeley();
        Berkeley n2 = new Berkeley();
        Berkeley n3 = new Berkeley();

        n1.setLeadership(true);
        
        Channel c1 = n1.open();
        Channel c2 = n2.open();
        Channel c3 = n3.open();
        
        n1.setTime(n1.getTime() + (long) (Math.random() * 100 - 50));
        n2.setTime(n2.getTime() + (long) (Math.random() * 100 - 50));
        n3.setTime(n3.getTime() + (long) (Math.random() * 100 - 50));
        
        System.out.println("(Avant Sync) Temps du leader: " + n1.getTime() + "ms");
        System.out.println("(Avant Sync) Temps de node 2: " + n2.getTime() + "ms");
        System.out.println("(Avant Sync) Temps de node 3: " + n3.getTime() + "ms");
        
        System.out.println();
        System.out.println("Leader is requesting time");
        ArrayList<Channel> channels = new ArrayList<>();
        channels.add(c2);
        channels.add(c3);
        n1.requestTime(channels, 1000);
        
        System.out.println();
        System.out.println("Clients are reading their respective communication channel");
        c2.update();
        c3.update();

        System.out.println();
        System.out.println("Nodes are responding to the leader's request");
        n2.update();
        n3.update();

        System.out.println();
        System.out.println("Clients are reading their respective communication channel");
        c2.update();
        c3.update();

        //Receiving time & sending offset
        System.out.println();
        System.out.println("Leader is calculating and sending local offset to each client");
        n1.update();

        System.out.println();
        System.out.println("Clients are reading their respective communication channel");
        c2.update();
        c3.update();
        
        // Updating time
        System.out.println();
        System.out.println("Nodes are updating their time in correlation to the leader's local offset calculation");
        n2.update();
        n3.update();

        System.out.println();
        System.out.println("(Après Sync) Temps du leader: " + n1.getTime() + "ns (Offset: " + n1.getLastOffset() + "ms)" );
        System.out.println("(Après Sync) Temps de node 2: " + n2.getTime() + "ns (Offset: " + n2.getLastOffset() + "ms)" );
        System.out.println("(Après Sync) Temps de node 3: " + n3.getTime() + "ns (Offset: " + n3.getLastOffset() + "ms)" );
        
    }
}
