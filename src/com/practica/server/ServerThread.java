package com.practica.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread{
    private Socket socket;
    private ArrayList<ServerThread> threads;
    private PrintWriter output;

    public ServerThread(Socket socket, ArrayList<ServerThread> threads){
        this.socket = socket;
        this.threads = threads;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            while (true){
                String outputText = bufferedInput.readLine();

                //comando para que el usuario salga
                if (outputText.equals("/exit")) break;

                printToAllClients(outputText);

                System.out.println("Clients now: ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printToAllClients(String text) {
        for (ServerThread serverThread: threads){
            serverThread.output.println(text);
        }
    }
}
