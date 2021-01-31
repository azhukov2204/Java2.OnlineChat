package onlinechat.servermultiusers.myserver.handler;

import onlinechat.servermultiusers.myserver.MyServer;
import onlinechat.servermultiusers.myserver.authservice.BaseAuthService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final MyServer myServer;
    private final Socket clientSocket;
    private final BaseAuthService baseAuthService;
    private DataInputStream in;
    private DataOutputStream out;

    private String nickName;

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + pass
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/clientMsg"; // + msg
    private static final String SERVER_MSG_CMD_PREFIX = "/serverMsg"; // + msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/w"; //sender + p + msg
    private static final String END_CMD_PREFIX = "/end"; //


    public ClientHandler(MyServer myServer, Socket clientSocket, BaseAuthService baseAuthService) {
        this.myServer = myServer;
        this.clientSocket = clientSocket;
        this.baseAuthService = baseAuthService;
    }

    public void startHandler() throws IOException {
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        new Thread(() -> {
            try {
                authenticationAndSubscribe();
                readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                myServer.unsubscribeClient(this);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void authenticationAndSubscribe() throws IOException {
        String message;
        boolean isAuthenticationSuccessful = false;
        do {
            message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                isAuthenticationSuccessful = isAuthenticationSuccessful(message);
            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX + "Ошибка авторизации");
            }
        } while (!isAuthenticationSuccessful);
        myServer.subscribeClient(this);
    }

    private boolean isAuthenticationSuccessful(String message) throws IOException {
        String[] authMessageParts = message.split("\\s+", 3);
        if (authMessageParts.length != 3) {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Неверная команда авторизации");
            return false;
        }
        String login = authMessageParts[1];
        String password = authMessageParts[2];

        nickName = baseAuthService.getNickNameByLoginAndPassword(login, password);

        if (nickName != null) {
            if (myServer.isNickNameBusy(nickName)) {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Пользователь с таким логином уже авторизован");
                return false;
            }
            out.writeUTF(AUTHOK_CMD_PREFIX + " " + nickName + " успешно авторизован");
            return true;
        } else {
            out.writeUTF(AUTHERR_CMD_PREFIX + "Введены неверные логин или пароль");
            return false;
        }
    }

    private void readMessages() throws IOException {
        while (true) {
            String message = in.readUTF();

            if (!message.isBlank()) {
                String[] partsOfMessage = message.split("\\s+", 2);

                switch (partsOfMessage[0]) {
                    case END_CMD_PREFIX:
                        return;
                    case PRIVATE_MSG_CMD_PREFIX:
                        break;
                    default:
                        myServer.broadcastMessage(this, message);
                        break;
                }
            }
        }
    }

    public void sendMessage(String senderNickName, String message) throws IOException {
        out.writeUTF(String.format("%s %s %s", CLIENT_MSG_CMD_PREFIX, senderNickName, message));
    }

    public String getNickName() {
        return nickName;
    }
}
