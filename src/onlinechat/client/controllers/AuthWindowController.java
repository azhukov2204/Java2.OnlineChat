package onlinechat.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import onlinechat.client.ChatClientApp;
import onlinechat.client.models.Network;

public class AuthWindowController {

    private Network network;
    private ChatClientApp chatClientApp;
    public void setNetwork(Network network) {
        this.network = network;
    }
    public void setChatClientApp(ChatClientApp chatClientApp) {
        this.chatClientApp = chatClientApp;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button enterButton;

    @FXML
    void initialize() {
        assert loginField != null : "fx:id=\"loginField\" was not injected: check your FXML file 'AuthWindow.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'AuthWindow.fxml'.";
        assert enterButton != null : "fx:id=\"enterButton\" was not injected: check your FXML file 'AuthWindow.fxml'.";

    }


    @FXML
    void doAuthentication(ActionEvent event) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.length() == 0) {
            System.out.println("Логин не может быть пустым");
            return;
        }

        String authErrorMessage = network.sendAuthCommand(login, password);

        if (authErrorMessage == null) {
            chatClientApp.startChat();
        } else {
            System.out.println("Ошибка аутентификации " + authErrorMessage);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка аутентификации");
            alert.setHeaderText("Ошибка аутентификации");
            alert.setContentText(authErrorMessage);
            alert.show();
        }

    }

}
