package dataaccess;

import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    public void createUser(UserData user) {
        String statementString = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(statementString)) {
                statement.setString(1, user.username());
                statement.setString(2, user.password());
                statement.setString(3, user.email());
                statement.executeUpdate();
            }
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createAuth(AuthData auth) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void removeAuth(String authToken) {

    }


    @Override
    public void clearUsers() {
        String statementString = "DELETE FROM users";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(statementString)) {
                statement.executeUpdate();
            }
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearAuth() {
        String statementString = "DELETE FROM auth";

        try {
            Connection connection = DatabaseManager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(statementString)) {
                statement.executeUpdate();
            }
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
