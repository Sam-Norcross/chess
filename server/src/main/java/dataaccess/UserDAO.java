package dataaccess;

import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

public interface UserDAO {

    public void createUser(UserData user);

    public UserData getUser(UserData user);

    public void createAuth(AuthData auth);

    public AuthData getAuth(String authToken);

    public void removeAuth(String authToken);

    public void clearUsers();

    public void clearAuth();

}
