package interfaces;

import java.time.format.DateTimeFormatter;

import java.net.Socket;

public interface IClient {

    public void requestTime(int serverPort, long currentTime, int numberOfTries) throws java.io.IOException;

    public long getTime();

    public void setTime(long newTime);

    public int getAccuracy();

}
