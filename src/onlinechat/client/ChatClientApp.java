package onlinechat.client;

import javafx.stage.Modality;
import onlinechat.client.controllers.AuthWindowController;
import onlinechat.client.controllers.MainChatWindowController;
import onlinechat.client.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatClientApp extends Application {

    private Stage primaryStage;
    private Stage authWindowStage;
    private Network network;
    private MainChatWindowController mainChatWindowController;

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;

        network = new Network();


        FXMLLoader authWindowLoader = new FXMLLoader();
        authWindowLoader.setLocation(ChatClientApp.class.getResource("views/AuthWindow.fxml"));

        Parent authWindowRoot = authWindowLoader.load();
        authWindowStage = new Stage();
        authWindowStage.setTitle("Аутентификация");
        authWindowStage.setScene(new Scene(authWindowRoot));
        authWindowStage.initModality(Modality.WINDOW_MODAL);
        authWindowStage.initOwner(primaryStage);
        authWindowStage.show();
        network.connection();
        AuthWindowController authWindowController = authWindowLoader.getController();
        authWindowController.setNetwork(network);
        authWindowController.setChatClientApp(this);




        FXMLLoader mainChatWindowLoader = new FXMLLoader();
        mainChatWindowLoader.setLocation(ChatClientApp.class.getResource("views/MainChatWindow.fxml"));

        Parent mainChatWindowRoot = mainChatWindowLoader.load();
        primaryStage.setTitle("Online Chat");
        primaryStage.setScene(new Scene(mainChatWindowRoot));
        //primaryStage.show();

        mainChatWindowController = mainChatWindowLoader.getController();


    }


    public static void main(String[] args) {
        launch(args);
    }


    public void startChat() {
        authWindowStage.close();
        primaryStage.show();
        primaryStage.setTitle(network.getNickName());
        primaryStage.setAlwaysOnTop(true);
        network.startReceiver(mainChatWindowController);
        mainChatWindowController.setNetwork(network);

    }
}
