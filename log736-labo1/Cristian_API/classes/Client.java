package classes;

import interfaces.IClient;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements IClient {

    private long time;

    private long time0;
    private  long time1;
    private Long timeS;

    private long accuracy;
    private long meanArccuracy=0;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public Client(int serverPort) throws IOException {
        this.socket = new Socket("127.0.0.1",serverPort);
        System.out.println("Connected");
    }

    public void requestTime(int serverPort, long currentTime, int numberOfTries) throws IOException {

        Scanner scanner = new Scanner(System.in);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
        String messageSent = scanner.nextLine();
        out.write(messageSent);
        out.newLine();
        out.flush();
        if(messageSent.equals("close")){
            endConnection();
            return;
        }
        System.out.println("Message sent");
        dis = new DataInputStream(socket.getInputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("Message received");
        time0 = System.nanoTime();
        timeS = Long.parseLong(in.readLine());
        time1=System.nanoTime();
        System.out.println(timeS.toString());

        setTime(timeS);
    }

    public long getTime() {
        return System.nanoTime();
    }

    public void setTime(long newTime) {
        this.accuracy = getAccuracy();
        System.out.println("Old time -> "+this.time);
        this.time = newTime+this.accuracy;
        System.out.println(this.accuracy+"<- acc|time-> " + this.time);
    }

    public long getAccuracy() {
        return (time1-time0)/2;
    }

    public void setMeanAccuracy(long accuracy,int nbOfTries) {
        this.meanArccuracy =((this.meanArccuracy*(nbOfTries-1)) +accuracy)/nbOfTries;
    }
    public long getMeanAccuracy() {return this.meanArccuracy;}
    public void endConnection() {
        try {
            this.socket.close();
            this.dos.close();
            this.dis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        int nbOfTries=0;
        try {
            Client client = new Client(25000);
            for(int i =0;i<nbOfTries+1;i++) {
                nbOfTries++;

                client.requestTime(25000, client.getTime(), nbOfTries);
                client.setMeanAccuracy(client.getAccuracy(), nbOfTries);
                System.out.println("Mean Accuracy -> "+client.getMeanAccuracy());
            }
            client.endConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
