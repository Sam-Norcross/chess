package dataaccess;

import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

public interface UserDAO {

    public UserData getUser(String Username) throws DataAccessException;

    public void createUser(UserData user) throws DataAccessException;

    public void createAuth(AuthData auth) throws DataAccessException;

}
