package client.models;

import client.controllers.MainChatWindowController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {

    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = 8081;

    private String serverHost;
    private int serverPort;
    private Socket clientSocket;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    public Network() {
        serverHost = DEFAULT_SERVER_HOST;
        serverPort = DEFAULT_SERVER_PORT;
    }

    public Network(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }


    public void connection() {
        try {
            clientSocket = new Socket(serverHost, serverPort);
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReceiver(MainChatWindowController chatWindowController) {
        Thread receiver = new Thread(() -> {
            while (true) {
                try {
                    String inputMessage = in.readUTF();
                    chatWindowController.addMessage(inputMessage, "Петров Петя");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        });

    }


}
