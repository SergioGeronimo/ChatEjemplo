package com.practica.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) {

        ArrayList<ServerThread> serverThreadArrayList = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(4444)) {
            while (true){
                Socket socket = serverSocket.accept();
                ServerThread serverThread = new ServerThread(socket, serverThreadArrayList);

                serverThreadArrayList.add(serverThread);
                serverThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
