package DistributedSystems_API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.io.*;
import java.net.Socket;
import java.util.PriorityQueue;
import java.util.stream.IntStream;

public class Channel {

    private static final int GlobalPort = 25000;
    private static final int MaxActivePorts = 1000;
    private static PriorityQueue<Integer> AvailablePorts = new PriorityQueue<Integer>(
            IntStream.rangeClosed(GlobalPort + 1, GlobalPort + MaxActivePorts).boxed().toList());
    private static HashMap<Integer, Channel> ActiveChannels = new HashMap<>();

    private Socket client = null;
    private ArrayList<Message> buffer = new ArrayList<>();
    private HashMap<String, ArrayList<Message>> bySubject = new HashMap<>();
    private final UUID uuid = UUID.randomUUID();

    public static Channel open() {
        if (MaxActivePorts == ActiveChannels.size()) { return null; }

        int port = getAvailablePort();
        Channel channel = new Channel();
        if(NetworkSimulator.network.startCommunication(channel, port)) {
            try {
                channel.client = new Socket("localhost", port);
                ActiveChannels.put(port, channel);
                
            } catch (IOException e) {
                channel = null;
                System.out.println("Client " + port + ": Error opening socket.");
                e.printStackTrace();
            }
        }

        return channel;
    }

    private Channel() {}

    public synchronized void close() {
        if(client != null) {
            try {
                NetworkSimulator.network.stopCommunication(client.getPort());
                ActiveChannels.remove(client.getPort());
                client.close();
            } catch (IOException e) {
                System.out.println("Client " + client.getPort() + ": Error while closing socket.") ;
                e.printStackTrace();
            }

        }
    }

    public void update() {
        if(!isOpened()) { return; }
        
        try {
            int port = client.getPort();
            Socket server = NetworkSimulator.network.getServer(client.getPort());
            if(server != null) {
                InputStream input =  server.getInputStream();
                int availability = input.available(); 
                if(availability > 0) {
                    String data = new String(input.readNBytes(availability));
                    for(String raw : data.split("\n")) {
                        Message msg = Message.fromString(raw);
                        if(!msg.isEmpty()) {
                            buffer.add(msg);
                            if(!bySubject.containsKey(msg.getSubject())) {
                                bySubject.put(msg.getSubject(), new ArrayList<>());
                            }
                            bySubject.get(msg.getSubject()).add(msg);
                            System.out.println("Client " + client.getPort() + " - Read: " + msg.toString()) ;
                        }
                    }
                }
            } else {
                System.out.println("Client " + client.getPort() + ": Error while reading on update.") ;
            }
        } catch (IOException e) {
                System.out.println("Client " + client.getPort() + ": Error while reading on update.") ;
                e.printStackTrace();
        }
    }
    
    public boolean send(Message message) {
        if(!isOpened()) { return false; }

        try {
            OutputStream output = client.getOutputStream();
            output.write((message.toString() + "\n").getBytes());
            output.flush();
            System.out.println("Client " + client.getPort() + " - Write: " + message.toString()) ;
            return true;
        } catch (IOException e) {
            System.out.println("Client " + client.getPort() + ": Error while sending message.") ;
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Message> retreiveAll() {
        return new ArrayList<>(buffer);
    }

    public ArrayList<Message> retreive(int from) {
        ArrayList<Message> messages = new ArrayList<>(Math.max(0, buffer.size() - from));
        
        for(int i=from; i < buffer.size(); ++i) {
            messages.add(buffer.get(i));
        }

        return messages;
    }

    public ArrayList<Message> retreiveFromSubject(String subject) {
        return bySubject.getOrDefault(subject, new ArrayList<>());
    }

    public boolean isOpened() {
        return client != null && client.isConnected();
    }

    public UUID getUuid() {
        return uuid;
    }

    
}
