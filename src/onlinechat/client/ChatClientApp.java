package onlinechat.client;

import onlinechat.client.controllers.MainChatWindowController;
import onlinechat.client.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChatClientApp.class.getResource("views/MainChatWindow.fxml"));

        Parent root = loader.load();
        primaryStage.setTitle("Online Chat");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Network network = new Network();

        MainChatWindowController mainChatWindowController = loader.getController();
        System.out.println(loader.getController().toString());

        network.connection();
        network.startReceiver(mainChatWindowController);
        mainChatWindowController.setNetwork(network);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
