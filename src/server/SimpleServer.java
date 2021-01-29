package server;

import server.Transceiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {
    private static final int SERVER_PORT = 8081;

    private static Socket clientSocket = null;

    public static void main(String[] args) {
        System.out.println("Домашняя работа №6. Сервер консольного чата.");
        startServer();
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Сервер запущен");

            Transceiver transceiver = new Transceiver("Сервер", "/stop");

            while (true) {
                initConnection(serverSocket); //инициируется clientSocket

                transceiver.setSocket(clientSocket);

                Thread sender = transceiver.checkAndRunSender(); //Запуск потока sender (если этот поток не запущен)
                Thread receiver = transceiver.checkAndRunReceiver(); //Запуск потока receiver (если этот поток не запущен)

                try {
                    if (receiver != null) {
                        /*Если клиент отвалится, то receiver сразу среагирует.
                        Поэтому делаем ожидание этого потока. Если receiver завершится - начинаем ждать новое подключение*/
                        receiver.join();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static void initConnection(ServerSocket serverSocket) {
        System.out.println("Ждем подключения...");
        try {
            //сокет для связи с клиентом
            clientSocket = serverSocket.accept(); //при подключении клиента инициируется эта переменная
            System.out.println("Подключился клиент " + clientSocket.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
