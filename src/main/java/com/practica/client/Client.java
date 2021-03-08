package com.practica.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Set;

public class Client implements Runnable{
	/* Transmit data buffer */
    private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);
	/* Accept the data buffer */
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);
	/* The server side address */
    private InetSocketAddress server;
    private static Selector selector;
    private static SocketChannel clientSocketChannel;
    private static String receivedText;
    private static String sentText;
    private static int bytesRead =0;

    public Client(String host,int port){
        server = new InetSocketAddress(host, port);

    }

    public void connect(){
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(server);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void run(){
        try {
            while (true) {
                selector.select();
                Set<SelectionKey> keySet = selector.selectedKeys();
                for(final SelectionKey key : keySet){
                    handle(key);
                };
                keySet.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 4444);
        client.connect();
        client.run();
    }

    private void handle(SelectionKey selectionKey) throws IOException{
        if (selectionKey.isConnectable()) {
            /*
             * Connection is established event has been successfully connected to the server
             */
            clientSocketChannel = (SocketChannel) selectionKey.channel();
            if (clientSocketChannel.isConnectionPending()) {
                clientSocketChannel.finishConnect();
                System.out.println("connect success !");
                sBuffer.clear();
                sBuffer.put((new Date().toLocaleString()+" connected!").getBytes());
                sBuffer.flip();
                clientSocketChannel.write (sBuffer); // send a message to the server
                /*
                 * Start the thread has been listening client input, confidence is sent to the server-side input
                 * Because the input stream is blocked, so a separate thread monitor
                 */
                new Thread(){
                    @Override
                    public void run() {
                        while(true){
                            try {
                                sBuffer.clear();
                                InputStreamReader input = new InputStreamReader(System.in);
                                BufferedReader br = new BufferedReader(input);
                                sentText = br.readLine();
                                /*
                                 * WRITE event is not registered, because most of the time channel can all be written
                                 */
                                sBuffer.put(sentText.getBytes());
                                sBuffer.flip();
                                clientSocketChannel.write(sBuffer);
                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    };
                }.start();
            }
            // Register event read
            clientSocketChannel.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            /*
             * Reading event triggers
             * There is information sent from the server over, after reading the output to the screen, continue reading event registration
             * Monitor server sends information
             */
            clientSocketChannel = (SocketChannel) selectionKey.channel();
            rBuffer.clear();
            bytesRead = clientSocketChannel.read(rBuffer);
            if(bytesRead >0){
                receivedText = new String( rBuffer.array(),0, bytesRead);
                System.out.println(receivedText);
                clientSocketChannel = (SocketChannel) selectionKey.channel();
                clientSocketChannel.register(selector, SelectionKey.OP_READ);
            }
        }
    }
}

