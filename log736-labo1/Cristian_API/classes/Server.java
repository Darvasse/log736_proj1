package classes;

import interfaces.IServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements IServer {
    public String receivedMessage;
    private ServerSocket serverSocket;
    private Socket connection;
    public void startServer(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            connection = serverSocket.accept();

        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void stopServer() throws IOException {
        try {
            serverSocket.close();
            connection.close();
        }catch (Exception e){
            System.out.println(e);
        }

    }

    public long getTime() {
        // Changez le code ci-dessous pour retourner un long
        return System.currentTimeMillis();
    }
}
