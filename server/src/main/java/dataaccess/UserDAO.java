package dataaccess;

import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

public interface UserDAO {

    void createUser(UserData user) throws DataAccessException;;

    UserData getUser(String username) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void removeAuth(String authToken) throws DataAccessException;

    void clearUsers() throws DataAccessException;

    void clearAuth() throws DataAccessException;

}
