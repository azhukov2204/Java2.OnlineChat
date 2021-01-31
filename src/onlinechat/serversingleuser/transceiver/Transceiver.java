package onlinechat.serversingleuser.transceiver;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Transceiver {
    private boolean senderRunning = false;
    private boolean receiverRunning = false;
    private Socket clientSocket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    private String selfName;
    private String stopWord;

    private Scanner scanner = new Scanner(System.in);


    public Thread checkAndRunReceiver() {
        Thread receiver = null;

        if (!receiverRunning) {
            receiver = new Thread(() -> {
                try {
                    System.out.println("Запуск потока receiver");
                    receiverRunning = true;
                    while (true) {
                        String inputMessage = in.readUTF();
                        System.out.println(inputMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Собеседник отключился. Receiver остановлен");
                    closeConnection();
                    receiverRunning = false;
                }
            });
            receiver.start();
        }
        return receiver;
    }

    public Thread checkAndRunSender() {
        Thread sender = null;


        if (!senderRunning) {
            sender = new Thread(() -> {
                try {
                    System.out.println("Запуск потока sender");
                    senderRunning = true;
                    while (true) {
                        String message = scanner.nextLine();
                        if (message.equals(stopWord)) {
                            System.out.println("Введено стоп-слово. Завершаем работу.");
                            System.exit(0);
                        } else if (!message.isEmpty()) {
                            if (!clientSocket.isClosed()) {
                                out.writeUTF(selfName + ": " + message);
                                System.out.println("Сообщение отправлено");
                            } else {
                                System.out.println("Сообщение не отправлено. Нет связи с собеседником");
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("sender остановлен");
                    e.printStackTrace();
                    closeConnection();
                    senderRunning = false;
                }
            });
            sender.start();
        }
        return sender;
    }

    private void closeConnection() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;

        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Transceiver(String selfName, String stopWord) {
        this.selfName = selfName;
        this.stopWord = stopWord;
    }
}