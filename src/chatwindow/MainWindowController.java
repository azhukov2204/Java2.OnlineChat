package chatwindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainWindowController {

    @FXML
    private ListView<String> chatUsersList;

    @FXML
    private TableView<RowChatMessage> chatMessages;

    @FXML
    private TableColumn<RowChatMessage, String> userTableView;

    @FXML
    private  TableColumn<RowChatMessage, String> messageTableView;

    @FXML
    private TableColumn<RowChatMessage, String> timeTableView;


    @FXML
    private Button sendMessageButton;

    @FXML
    private TextField sendMessageText;


    private String currentUser = "Иванов Иван"; //Пока в начальной реализации фиксированное значение

    @FXML
    void initialize() {
        userTableView.setCellValueFactory(new PropertyValueFactory<>("User"));
        messageTableView.setCellValueFactory(new PropertyValueFactory<>("Message"));
        timeTableView.setCellValueFactory(new PropertyValueFactory<>("Time"));

        messageTableView.setPrefWidth(500);


        ObservableList<String> chatUsers = FXCollections.observableArrayList("Иванов Иван", "Петров Петр", "Федор Михайлович");
        chatUsersList.setItems(chatUsers); //добавим несколько записей в поле с текущими активными пользователями чата, для теста

    }


    @FXML
    void sendMessage() {
        sendMessageText.requestFocus(); //при вызове метода фокус сразу возвращается на sendMessageText

        Date date = new Date(); //текущая дата и время
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String currentTime = dateFormat.format(date).toString();

        String message=sendMessageText.getText().trim();

        if (!message.isBlank()) {
            chatMessages.getItems().add(new RowChatMessage(currentTime, currentUser, message));
            sendMessageText.clear();
        }

        int messagesCount = chatMessages.getItems().size();
        chatMessages.scrollTo(messagesCount -1 ); //прокрутим к последнему сообшению
        sendMessageButton.setDisable(true); //после отправки сделаем кнопку неактивной*/



    }


    @FXML
    void setActiveSendButton() { //метод вызывается при вводе текста
        if (!sendMessageText.getText().trim().isBlank()) {
            sendMessageButton.setDisable(false);
        }
    }

    @FXML
    void exit() {
        System.exit(0);
    }

    @FXML
    void about() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("О программе");
        about.setHeaderText("Online - чат");
        about.setContentText("Курс Java Core. Продвинутый уровень.");
        about.show();
    }


    @FXML
    void resize() {
        System.out.println("resize");

    }

}
