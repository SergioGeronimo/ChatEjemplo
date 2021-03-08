package com.practica.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TextSocketThread extends Thread{

    private Socket socket;
    private BufferedReader bufferedInput;

    public TextSocketThread(Socket socket) throws IOException{
        this.socket = socket;
        this.bufferedInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try{
            while (true){
                String response = bufferedInput.readLine();
                System.out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedInput.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
