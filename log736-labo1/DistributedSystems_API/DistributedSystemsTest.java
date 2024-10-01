package DistributedSystems_API;

import java.util.ArrayList;

public class DistributedSystemsTest {
    

    public static void main(String[] args) {
        Berkeley n1 = new Berkeley();
        Berkeley n2 = new Berkeley();
        Berkeley n3 = new Berkeley();


        n1.setLeadership(true);

        n1.open();
        n2.open();
        n3.open();
        
        
        n1.setTime(n1.getTime() + (long) (Math.random() * 100 - 50));
        n2.setTime(n2.getTime() + (long) (Math.random() * 100 - 50));
        n3.setTime(n3.getTime() + (long) (Math.random() * 100 - 50));
        
        System.out.println("(Avant Sync) Temps du dirigeant: " + n1.getTime() + "ns");
        System.out.println("(Avant Sync) Temps de node 2: " + n2.getTime() + "ns");
        System.out.println("(Avant Sync) Temps de node 3: " + n3.getTime() + "ns");
        
        ArrayList<Channel> channels = new ArrayList<>();
        channels.add(n2.getChannel());
        channels.add(n3.getChannel());
        n1.requestTime(channels, 1000);
        
        //Sending time
        n2.update();
        n3.update();

        n2.getChannel().update();
        n3.getChannel().update();

        //Receiving time & sending offset
        n1.update();
        
        // Updating time
        n2.update();
        n3.update();


        System.out.println("(Après Sync) Temps du dirigeant: " + n1.getTime() + "ns (Offset: " + n1.getLastOffset() + "ns)" );
        System.out.println("(Après Sync) Temps de node 2: " + n2.getTime() + "ns (Offset: " + n2.getLastOffset() + "ns)" );
        System.out.println("(Après Sync) Temps de node 3: " + n3.getTime() + "ns (Offset: " + n3.getLastOffset() + "ns)" );
        
    }
}
