package classes;

import interfaces.IClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements IClient {

    private long time;

    private long time0;
    private  long time1;
    private long timeS;

    private long accuracy;


    public void requestTime(int serverPort, long currentTime, int numberOfTries) throws IOException {
        Socket socket = new Socket("127.0.0.1",serverPort);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        time0 = System.nanoTime();
        timeS = dis.readLong();
        time1=System.nanoTime();
        System.out.println(timeS+ " aaaa");
        dis.close();
        socket.close();
        setTime(timeS);
    }

    public long getTime() {
        return System.nanoTime();
    }

    public void setTime(long newTime) {
        this.accuracy = getAccuracy();
        this.time = newTime+this.accuracy;
        System.out.println(this.accuracy+"<- acc|time-> " + this.time);
    }

    public long getAccuracy() {
        return (time1-time0)/2;
    }

    public static void main(String[] args) {
        Client client = new Client();
        int nbOfTries=0;
        try {
            nbOfTries++;

            client.requestTime(25000, client.getTime(), nbOfTries);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
