package classes;

import interfaces.INode;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Node implements INode {
    private long time;

    public long[] requestTime(int[] ports, long seuil) throws IOException {
        // Changez le code ci-dessous pour retourner un tableau d'intiers
        return null;
    }

    public Socket startNoeud(int port) throws IOException {
        // Changez le code ci-dessous pour retourner un objet Socket
        return null;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long offset) {

    }
}
