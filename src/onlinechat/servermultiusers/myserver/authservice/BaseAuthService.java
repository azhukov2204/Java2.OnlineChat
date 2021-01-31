package onlinechat.servermultiusers.myserver.authservice;

import java.util.List;

public class BaseAuthService implements AuthService {

    //этот тип используется только тут, сделаем класс вложенным
    public class User {
        private String login;
        private String password;
        private String nickName;

        public User(String login, String password, String nickName) {
            this.login = login;
            this.password = password;
            this.nickName = nickName;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getNickName() {
            return nickName;
        }
    }

    private List<User> users;

    //При создании объекта происходит инициализация списка учетных записей
    public BaseAuthService() {
        users = List.of(
                new User("boris", "123456", "Боря"),
                new User("andrey", "654321", "Андрей"),
                new User("ivan", "111111", "Ваня")
        );
    }

    @Override
    public String getNickNameByLoginAndPassword(String login, String password) {
        return null;
    }

    @Override
    public void startAuthentication() {

    }

    @Override
    public void endAuthentication() {

    }
}
