package com.practica.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        try (Socket socket = new Socket("localhost", 4444)) {
            BufferedReader bufferedInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            String userInput, response, clientName = "";

            ClientThread clientThread = new ClientThread(socket);
            clientThread.start();

            do {
                if (clientName.equals("")) {
                    System.out.println("Ingresa tu nombre...");
                    userInput = scanner.nextLine();
                    clientName = userInput;
                    output.println(userInput);
                }else {
                    String message = ("(" + clientName + ")" + "mensaje: ");
                    System.out.println(message);

                    userInput = scanner.nextLine();
                    output.println(message + " " + userInput);
                }

            } while (!userInput.equals("/exit"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
