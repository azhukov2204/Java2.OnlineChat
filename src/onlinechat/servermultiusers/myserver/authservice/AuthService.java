package onlinechat.servermultiusers.myserver.authservice;

public interface AuthService {
    String getNickNameByLoginAndPassword(String login, String password);
    void startAuthentication();
    void endAuthentication();
}
