package chatwindow;

import javafx.beans.property.SimpleStringProperty;

public class RowChatMessage {
    private SimpleStringProperty user;
    private SimpleStringProperty message;

    public RowChatMessage(String user, String message) {
        this.user = new SimpleStringProperty(user);
        this.message = new SimpleStringProperty(message);
    }

    public String getUser() {
        return user.get();
    }

    public SimpleStringProperty userProperty() {
        return user;
    }

    public String getMessage() {
        return message.get();
    }

    public SimpleStringProperty messageProperty() {
        return message;
    }
}
