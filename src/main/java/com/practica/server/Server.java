package com.practica.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {

    // puerto por defecto, el numero puede ser cualquiera
    final static int PORT = 4444;


    // buffer para decodificar charset latino
    private final Charset charset = StandardCharsets.ISO_8859_1;
	// Accept the data buffer
    private static ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
	// Transmit data buffer
    private static ByteBuffer outputBuffer = ByteBuffer.allocate(1024);
	// Mapping client channel
    private Map<String, SocketChannel> clientsMap = new HashMap<String, SocketChannel>();
    private static Selector selector;

    public boolean isListening() {
        return isListening;
    }

    private boolean isListening;


    public boolean init() throws IOException{
        //Servidor iniciado, sin bloqueos y el puerto enlazado, se acepta registro de eventos
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(PORT));
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Servidor iniciado en el puerto:" + PORT);

        return serverSocket.isBound();
    }

     //Monitor de peticiones
    public void listen(){
        while (true) {
            try {
                this.isListening = true;
                selector.select (); // Obtener el numero de eventos
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for(SelectionKey key : selectionKeys){ //Manejar cada opcion por evento
                    handle(key);
                }
                selectionKeys.clear (); // Limpiar los eventos procesados
            } catch (Exception e) {
                this.isListening = false;
                e.printStackTrace();
                break;
            }

        }
    }

    /**
     * Handle different events
     */
    public void handle(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocket = null;
        SocketChannel clientSocket = null;
        String receivedText=null;
        int bytesRead=0;
        if (selectionKey.isAcceptable()) {
            /*
             * El cliente pide una conexión
             * serverSocket establece conexion para el cliente,
             * registrando un evento para leer (READ), escucha la entrada
             */
            serverSocket = (ServerSocketChannel) selectionKey.channel();
            clientSocket = serverSocket.accept();
            clientSocket.configureBlocking(false);
            clientSocket.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            /*
             * READ continue to register after the incident, received the client sends data, the data is read listen for client
             * leer la informacion del cliente, mandar a otros canales
             */
            clientSocket = (SocketChannel) selectionKey.channel();
            outputBuffer.clear();
            bytesRead = clientSocket.read(outputBuffer);

            if (bytesRead > 0) {
                outputBuffer.flip();
                receivedText = String.valueOf(charset.decode(outputBuffer).array()); // Esto causa un error con la multimedia
                System.out.println(clientSocket.toString() + ": " + receivedText);
                dispatch(clientSocket, receivedText);
                clientSocket = (SocketChannel) selectionKey.channel();
                clientSocket.register(selector, SelectionKey.OP_READ);

            }
        }
    }


    // El mensaje de un cliente es despachado a los otros
    public void dispatch(SocketChannel client,String message) throws IOException{
        Socket socket = client.socket();
        String name = "["+socket.getInetAddress().toString().substring(1)+": "+Integer.toHexString(client.hashCode())+"]";
        if(!clientsMap.isEmpty()){
            for(Map.Entry<String, SocketChannel> entry : clientsMap.entrySet()){
                SocketChannel tempChannel = entry.getValue();
                if(!client.equals(tempChannel)){
                    inputBuffer.clear();
                    inputBuffer.put((name+": " + message).getBytes());
                    inputBuffer.flip();
                    // output to the channel
                    tempChannel.write(inputBuffer);
                }
            }
        }
        clientsMap.put(name, client);
    }


    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.init();
        server.listen();
    }

}



