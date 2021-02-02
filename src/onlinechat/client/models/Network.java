package onlinechat.client.models;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import onlinechat.client.ChatClientApp;
import onlinechat.client.controllers.MainChatWindowController;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class Network {

    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = 8081;

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + pass
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/clientMsg"; // login + msg
    private static final String SERVER_MSG_CMD_PREFIX = "/serverMsg"; // + msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/w"; //sender + p + msg
    private static final String END_CMD_PREFIX = "/end"; //


    private String serverHost;
    private int serverPort;
    private Socket clientSocket;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    private String nickName;
    private ChatClientApp chatClientApp;

    private boolean isConnected = false;

    public void setChatClientApp(ChatClientApp chatClientApp) {
        this.chatClientApp = chatClientApp;
    }

    public Network() {
        serverHost = DEFAULT_SERVER_HOST;
        serverPort = DEFAULT_SERVER_PORT;
    }

    public Network(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public String getNickName() {
        return nickName;
    }

    public void connection() {
        try {
            clientSocket = new Socket(serverHost, serverPort);
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            isConnected = true;
            System.out.println("Соединение установлено");
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Не удалось установить соединение");
            alert.setHeaderText("Не удалось установить соединение");
            alert.setContentText("Повторить попытку подключения?");
            ButtonType yesButton = new ButtonType("Да");
            ButtonType noButton = new ButtonType("Нет, выйти");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(yesButton, noButton);

            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == yesButton) {
                connection();
            } else {
                System.exit(-1);
            }

        }

    }

    public void startReceiver(MainChatWindowController chatWindowController) {
        Thread receiver = new Thread(() -> {
            while (isConnected) {
                try {
                    String message = in.readUTF();
                    if (!message.isBlank()) {
                        String[] partsOfMessage = message.split("\\s+", 2);
                        switch (partsOfMessage[0]) {
                            case CLIENT_MSG_CMD_PREFIX:
                                String[] partsOfClientMessage = message.split("\\s+", 3);
                                Platform.runLater(() -> chatWindowController.addMessage(partsOfClientMessage[2], partsOfClientMessage[1]));
                                break;
                            case SERVER_MSG_CMD_PREFIX:
                                String[] partsOfServerMessage = message.split("\\s+", 2);
                                Platform.runLater(() -> chatWindowController.addMessage(partsOfServerMessage[1], "Сервер"));
                            default:
                                Platform.runLater(() -> System.out.println("!!Неизвестная ошибка сервера"));
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    isConnected = false;
                    System.out.println("Соединение прервано");
                    //todo тут сделать вызов аутентификации и алерт:
                    Platform.runLater(() -> {
                        try {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Ошибка");
                            alert.setContentText("Отпало подключение");
                            alert.showAndWait();
                            chatClientApp.createAndStartAuthWindow();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    });

                }
            }
            System.out.println("Receiver остановлен");
        });
        receiver.setDaemon(true);
        receiver.start();

    }


    public void sendMessage(String message, MainChatWindowController mainChatWindowController) throws IOException {
        if (clientSocket.isConnected() && isConnected) {
            out.writeUTF(message);
            mainChatWindowController.addMessage(message, "Я");
        }
    }

    public String sendAuthCommand(String login, String password) {
        try {
            out.writeUTF(String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));
            String response = in.readUTF();

            if (response.startsWith(AUTHOK_CMD_PREFIX)) {
                nickName = response.split("\\s+", 3)[1];
                return null;
            } else {
                return response.split("\\s+", 2)[1];
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
