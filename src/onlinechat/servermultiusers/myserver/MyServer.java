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
            if (baseAuthService != null) {
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

    public synchronized void subscribeClient(ClientHandler clientHandler) throws IOException {
        System.out.println("Подключился клиент " + clientHandler.getNickName());
        activeClients.add(clientHandler);
        broadcastSystemMessage("Подключился клиент " + clientHandler.getNickName());
        printActiveClients();
        sendActiveUsersList();
    }

    public void unsubscribeClient(ClientHandler clientHandler) throws IOException {
        System.out.println("Отключился клиент " + clientHandler.getNickName());
        activeClients.remove(clientHandler);
        broadcastSystemMessage("Отключился клиент " + clientHandler.getNickName());
        printActiveClients();
        sendActiveUsersList();
    }


    public synchronized void printActiveClients() {
        System.out.println("Список активных клиентов:");
        for (ClientHandler activeClient : activeClients) {
            System.out.println(activeClient.getNickName());
        }
    }

    public synchronized void broadcastMessage(String senderNickName, String message) throws IOException {
        for (ClientHandler activeClient : activeClients) {
            if (activeClient.getNickName().equals(senderNickName)) {
                continue;
            }
            activeClient.sendMessage(senderNickName, message);
        }
    }

    //если нет отправителя (sender), то считаем, что это серверное сообщение
    public synchronized void broadcastSystemMessage(String message) throws IOException {
        broadcastMessage(null, message);
    }

    public synchronized boolean privateMessage(String senderNickName, String recipientNickName, String message) throws IOException {
        for (ClientHandler activeClient : activeClients) {
            if (activeClient.getNickName().equals(recipientNickName)) {
                activeClient.sendMessage(senderNickName, "Приватное сообщение: " + message);
                return true;
            }
        }
        return false;
    }

    public synchronized void sendActiveUsersList() throws IOException {
        String activeUsersList = "";

        for (ClientHandler activeClient : activeClients) {
            activeUsersList = new StringBuilder().append(activeUsersList).append(activeClient.getNickName()).append(";").toString();
        }

        for (ClientHandler activeClient : activeClients) {
            activeClient.sendUsersList(activeUsersList);
        }
    }

}
