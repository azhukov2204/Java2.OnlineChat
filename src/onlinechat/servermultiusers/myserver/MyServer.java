package onlinechat.servermultiusers.myserver;

import onlinechat.servermultiusers.myserver.authservice.BaseAuthService;
import onlinechat.servermultiusers.myserver.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private final ServerSocket serverSocket;
    private final BaseAuthService baseAuthService;
    private final List<ClientHandler> activeClients = new ArrayList<>();

    public MyServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        baseAuthService = new BaseAuthService();
        baseAuthService.startAuthentication();
    }


    public void startMyServer() {
        System.out.println("Сервер запущен");
        try {
            while (true) {
                System.out.println("Ожидаем подключения пользователя");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клиент подключился, переходим к созданию указателя");
                new ClientHandler(this, clientSocket, baseAuthService).startHandler();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при подключении клиента");
            e.printStackTrace();
        } finally {
            if (baseAuthService!=null) {
                baseAuthService.endAuthentication();
            }
            System.out.println("Сервер остановлен");
        }

    }

    public synchronized boolean isNickNameBusy(String nickName) {
        for (ClientHandler activeClient : activeClients) {
            if (activeClient.getNickName().equals(nickName)) {
                return true;
            }
        }
        return false;
    }

    //методы, которые работают с ClientHandler, делаем синхронными

    public synchronized void subscribeClient(ClientHandler clientHandler) {
        System.out.println("Подключился клиент " + clientHandler.getNickName());
        activeClients.add(clientHandler);
        printActiveClients();
    }

    public void unsubscribeClient(ClientHandler clientHandler) {
        System.out.println("Отключился клиент " + clientHandler.getNickName());
        activeClients.remove(clientHandler);
        printActiveClients();
    }


    public synchronized void printActiveClients() {
        System.out.println("Список активных клиентов:");
        for (ClientHandler activeClient : activeClients) {
            System.out.println(activeClient.getNickName());
        }
    }

    public synchronized void broadcastMessage(ClientHandler sender, String message) throws IOException {
        for (ClientHandler activeClient : activeClients) {
            /*if (activeClient == sender) { //закомменрировал, самому себе сообщения будут отправляться в режиме echo. Чтоб видеть, работает ли отправка
                continue;
            }*/
            activeClient.sendMessage(sender.getNickName(), message);
        }
    }
}