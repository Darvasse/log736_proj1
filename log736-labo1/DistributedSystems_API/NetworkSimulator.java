package DistributedSystems_API;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkSimulator {

    public static NetworkSimulator network = new NetworkSimulator();
    private NetworkSimulator() {}
    
    enum SimulationType {
        Sync,
        Async
    }
    
    private SimulationType type = SimulationType.Sync;
    private ArrayList<Channel> channels = new ArrayList<>();
    private HashMap<Integer, ServerTask> servers = new HashMap<>();

    public void setSimulationType(SimulationType type) {
        this.type = type;
    }

    public boolean startCommunication(int port) {
        if(port < 25000 || port > 26000) { return false;}
        if(servers.containsKey(port)) { return true; }
        
        ServerTask server = new ServerTask(port);
        Thread serverThread = new Thread(server);
        serverThread.start();

        boolean isServerStarted = !server.isStopped();
        if(isServerStarted) {
            servers.put(port, server);
        }

        return isServerStarted;
    }

    public void stopCommunication(int port) {
        if(servers.containsKey(port)) {
            servers.remove(port).stop();
        }
    }

    public void simulateOnce() {
        if(type == SimulationType.Sync) { updateAll(); }
        else {
            // SimulationType.ASync -> update one random channel once
        }
    }

    public void simulate(int steps) {
        if(type == SimulationType.Sync) { updateAll(); }
        else {
            for(int i = 0; i < steps; ++i) {
                simulateOnce();
            }
        }
    }

    public void updateAll() {
        for(Channel c : channels) {
            c.update();
        }
    }

    public Socket getServer(int port) {
        if(servers.containsKey(port)) {
            return servers.get(port).connection;
        }
        return null;
    }

    // The ServerTaks class was inspired by: 
    //      https://jenkov.com/tutorials/java-multithreaded-servers/thread-pooled-server.html
    private class ServerTask implements Runnable {
        protected int          port         = 25000;
        protected ServerSocket server       = null;
        protected Socket       connection   = null;
        protected boolean      isStopped    = true;

        private ServerTask(int port) {
            try {
                this.server = new ServerSocket(port);
                isStopped = false;
            } catch (IOException e) {
                server = null;
                isStopped = true;
                
                System.out.println("Server " + port + ": Error opening server.") ;
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while(!isStopped()) {
                try {
                    connection = this.server.accept();
                } catch (IOException e) {
                    if(isStopped()) {
                        System.out.println("Server " + port + ": Stopped.") ;
                        break;
                    }
                    System.out.println("Server " + port + ": Error accepting client connection.") ;
                    e.printStackTrace();
                }
            }
            System.out.println("Server " + port + ": Stopped.") ;
        }

        private synchronized boolean isStopped() {
            return this.isStopped;
        }
    
        public synchronized void stop(){
            this.isStopped = true;
            try {
                this.server.close();
            } catch (IOException e) {
                System.out.println("Server " + port + ": Error closing server.");
                e.printStackTrace();
            }
        }
    }
}
