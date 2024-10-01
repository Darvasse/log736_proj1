package classes;

import interfaces.IServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements IServer {
    public String receivedMessage;
    private ServerSocket serverSocket;
    private Socket connection;
    private boolean opened = true;
    private DataOutputStream dos;
    private int test;
    public void startServer(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            connection = serverSocket.accept();
        } catch (Exception e){
            System.out.println(e);
        }
    }
    public void listen() throws IOException {
        try{
            System.out.println("Waiting for connection...");
            System.out.println("Connection accepted");
            //Reader reader = new InputStreamReader(connection.getInputStream());
            System.out.println("Reader created");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            DataInputStream dis = new DataInputStream(connection.getInputStream());
            System.out.println("BufferedReader created");
            receivedMessage = in.readLine();
            System.out.println(receivedMessage);
            if(receivedMessage.toString().equals("close")){
                stopServer();
                System.out.println("Server stopped");
            }
            else {
                System.out.println(receivedMessage + " server");
                dos = new DataOutputStream(connection.getOutputStream());
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                out.write(getTime().toString());
                out.newLine();
                out.flush();
                //dos.writeUTF(getTime().toString());
                //dos.flush();
            }
            System.out.println("Server huh");
        }catch (Exception e){System.out.println(e);}
    }
    public boolean isOpened(){
        return opened;
    }
    public void closeServer(boolean newBool){
        this.opened=newBool;
    }
    public void stopServer() throws IOException {
        try {
            serverSocket.close();
            connection.close();
        }catch (Exception e){
            System.out.println(e);
        }
        opened = false;
    }

    public Long getTime() {
        // Changez le code ci-dessous pour retourner un long
        Long time = System.nanoTime();
        return time;
    }
    public static void main(String[] args){
        Server server = new Server();
        try {
            server.startServer(25000);
            while(server.isOpened()) {
                server.listen();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
