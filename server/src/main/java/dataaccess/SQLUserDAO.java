package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.*;

public class SQLUserDAO implements UserDAO {

    //users table: username, password, email (<Sting, UserData>)
    String usersInit = """
                            CREATE TABLE IF NOT EXISTS users (
                            username varchar(255),
                            password varchar(255),
                            email varchar(255)
                            );
                        """;

    //auth table: authToken, username (<String, authData>)
    String authInit = """
                            CREATE TABLE IF NOT EXISTS auth (
                            authToken varchar(255),
                            username varchar(255)
                            );
                        """;

    public SQLUserDAO() {//} throws DataAccessException {

//        try {

            //DatabaseManager.createDatabase();
            try {

                DatabaseManager.createDatabase(); //NEW

                Connection connection = DatabaseManager.getConnection();
                try (PreparedStatement userStatement = connection.prepareStatement(usersInit);
                     PreparedStatement authStatement = connection.prepareStatement(authInit)) {
                    userStatement.executeUpdate();
                    authStatement.executeUpdate();
                }
            } catch (Exception e) { //(SQLException e) {
                //throw new DataAccessException(e.getMessage());

                throw new RuntimeException(e.getMessage()); //NEW

            }


//        } catch (DataAccessException e) {
//            throw new RuntimeException(e.getMessage());
//        }

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String statementString = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(statementString)) {
                statement.setString(1, user.username());
                statement.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                statement.setString(3, user.email());
                statement.executeUpdate();
            }
            connection.close();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user;
        String queryString = "SELECT username, password, email FROM users WHERE username = ?";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement query = connection.prepareStatement(queryString)) {
                query.setString(1, username);
                ResultSet result = query.executeQuery();
                result.next();
                user = new UserData(result.getString(1), result.getString(2), result.getString(3));
                result.close();
            }
            connection.close();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        return user;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String statementString = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(statementString)) {
                statement.setString(1, auth.authToken());
                statement.setString(2, auth.username());
                statement.executeUpdate();
            }
            connection.close();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData auth;
        String queryString = "SELECT authToken, username FROM auth WHERE authToken = ?";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement query = connection.prepareStatement(queryString)) {
                query.setString(1, authToken);
                ResultSet result = query.executeQuery();
                result.next();
                auth = new AuthData(result.getString(2), result.getString(1));
                result.close();
            }
            connection.close();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        return auth;
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {
        String statementString = "DELETE FROM auth WHERE authToken = ?";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement query = connection.prepareStatement(statementString)) {
                query.setString(1, authToken);
                query.executeUpdate();
            }
            connection.close();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    @Override
    public void clearUsers() throws DataAccessException {
        String statementString = "DELETE FROM users";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(statementString)) {
                statement.executeUpdate();
            }
            connection.close();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearAuth() throws DataAccessException {
        String statementString = "DELETE FROM auth";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(statementString)) {
                statement.executeUpdate();
            }
            connection.close();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
