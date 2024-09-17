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
            System.out.println(receivedMessage+" server");
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeUTF(getTime()+"");
            dos.flush();
            dos.close();


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
        return System.nanoTime();
    }
    public static void main(String[] args){
        Server server = new Server();
        try {
            server.startServer(25000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
