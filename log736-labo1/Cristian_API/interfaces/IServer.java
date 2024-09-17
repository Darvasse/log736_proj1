package interfaces;

import java.time.format.DateTimeFormatter;

public interface IServer {
    public void startServer(int port) throws java.io.IOException;

    public void stopServer() throws java.io.IOException;

    public long getTime();

}
