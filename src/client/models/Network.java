package client.models;

import client.controllers.MainChatWindowController;
import javafx.application.Platform;

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

    private boolean isConnected = false;

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
            isConnected = true;
            System.out.println("Соединение установлено");
        } catch (IOException e) {
            System.out.println("Не удалось установить соединение");
            e.printStackTrace();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            System.out.println("Пробуем подключиться еще раз");
            connection();
        }
    }

    public void startReceiver(MainChatWindowController chatWindowController) {
        Thread receiver = new Thread(() -> {
            while (isConnected) {
                try {
                    String inputMessage = in.readUTF();
                    Platform.runLater(() -> chatWindowController.addMessage(inputMessage, "Петров Петя"));
                    System.out.println(inputMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                    isConnected = false;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    System.out.println("Соединение прервано. Пробуем подключиться еще раз");
                    connection();
                }
            }
        });
        receiver.setDaemon(true);
        receiver.start();

    }


    public void sendMessage(String message, String currentUser, MainChatWindowController mainChatWindowController) throws IOException {
        if (clientSocket.isConnected() && isConnected) {
            out.writeUTF(message);
            mainChatWindowController.addMessage(message, currentUser);
        }
    }


}
