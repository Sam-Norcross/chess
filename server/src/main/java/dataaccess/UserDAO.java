package dataaccess;

import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

public interface UserDAO {

    void createUser(UserData user);

    UserData getUser(String username);

    void createAuth(AuthData auth);

    AuthData getAuth(String authToken);

    String getUsernameFromAuth(String authToken);

    void removeAuth(String authToken);

    void clearUsers();

    void clearAuth();

}
