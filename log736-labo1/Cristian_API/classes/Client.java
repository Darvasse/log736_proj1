package classes;

import interfaces.IClient;

import java.io.IOException;
import java.net.Socket;

public class Client implements IClient {

    private long time;
    private int accuracy;

    public void requestTime(int serverPort, long currentTime, int numberOfTries) throws IOException {
        Socket socket = new Socket("127.0.0.1",serverPort);
    }

    public long getTime() {
        return System.currentTimeMillis();
    }

    public void setTime(long newTime) {
        this.accuracy = getAccuracy();
        this.time = newTime;
    }

    public int getAccuracy() {

    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.startServer(25000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
