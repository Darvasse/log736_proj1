package interfaces;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.net.Socket;

public interface INode {
    public long[] requestTime(int[] ports, long seuil) throws IOException;

    public Socket startNoeud(int port) throws IOException;

    public long getTime();

    public void setTime(long offset);
}
