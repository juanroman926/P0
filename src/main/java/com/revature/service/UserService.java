package com.revature.service;

import com.revature.entity.User;
import com.revature.exception.LoginFail;
import com.revature.exception.UserSQLException;
import com.revature.repository.UserDao;
import com.revature.utility.DatabaseConnector;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDao userDao;
    public UserService(UserDao userDao){
        this.userDao = userDao;
    }

    public User validateNewCredentials(User newUserCredentials){
        if (checkUsernamePasswordLength(newUserCredentials)){
            if (checkUsernameIsUnique(newUserCredentials)){
                return userDao.createUser(newUserCredentials);
            }
            throw new LoginFail("Username is already taken");
        }
        throw new LoginFail("Invalid username or password");
    }

    private boolean checkUsernamePasswordLength(User newUserCredentials)
    {
        boolean usernameIsValid = newUserCredentials.getUsername().length() <= 30;
        boolean passwordIsValid = newUserCredentials.getPassword().length() <= 30;
        return usernameIsValid && passwordIsValid;

    }

    private boolean checkUsernameIsUnique(User newUserCredentials){
        boolean userNameIsUnique = true;
        List<User> users = userDao.getAllUsers();
        for(User user : users){
            if(newUserCredentials.getUsername().equals(user.getUsername())){
                userNameIsUnique = false;
                break;
            }
        }
        return userNameIsUnique;
    }

    public User checkLoginCredentials(User credentials){
        for (User user : userDao.getAllUsers()){
            boolean usernameMatches = user.getUsername().equals(credentials.getUsername());
            boolean passwordMatches = user.getPassword().equals(credentials.getPassword());
            if(passwordMatches && usernameMatches){
                return credentials;
            }
        }
        throw new LoginFail("Credentials are invalid!");
    }
    public int getUserId(String username){
        String sql = "select userId from user where username = ?";
        try(Connection connection = DatabaseConnector.createConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return rs.getInt("userId");
            }else {
                return -1;
            }
            }catch (SQLException e) {
            System.out.println(e.getMessage());
            return -2;
        }
    }
    public int createAccount(int userId) {
        String sql = "INSERT INTO checkingAccount (userId) VALUES (?) RETURNING accountId;)";
        try(Connection connection = DatabaseConnector.createConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("accountId");
                } else {
                    throw new SQLException("Failed to retrieve generated accountId");
                }
            }
        }catch(SQLException exception){
            throw new UserSQLException(exception.getMessage());
        }
    }
    public double getBalance(int userId, int accountId){
        String sql = "SELECT ca.balance FROM checkingAccount ca WHERE ca.userId = ? AND ca.accountId = ?";
        try(Connection connection = DatabaseConnector.createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, accountId);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return rs.getDouble("balance");
            }else {
                return -1;
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }
    public double addToBalance(int userId, double amount, int accountId){
        double originalBalance = getBalance(userId,accountId);
        double balance = getBalance(userId,accountId);
        balance = balance + amount;
        if(balance < 0){
            System.out.println("Balance cannot be negative. Withdraw not made");
            return originalBalance;
        }
        String sql = "UPDATE checkingAccount SET balance = ? WHERE accountId = ?";
        try(Connection connection = DatabaseConnector.createConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, balance);
            preparedStatement.setDouble(2, accountId);
            int result = preparedStatement.executeUpdate();
            if(result == 1) {
                return getBalance(userId, accountId);
            }
            throw new UserSQLException ("User could not be created: please try again");
        }catch(SQLException exception){
            throw new UserSQLException(exception.getMessage());
        }
    }
    public void displayUserWithAccounts(int userId) {
        String sql = "SELECT u.userId, u.username, ca.accountId, ca.balance " +
                "FROM user u " +
                "LEFT JOIN checkingAccount ca ON u.userId = ca.userId " +
                "WHERE u.userId = ?";

        try (Connection connection = DatabaseConnector.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                boolean userFound = false;
                boolean firstRow = true;

                while (rs.next()) {
                    if (firstRow) {
                        System.out.println("User ID: " + rs.getInt("userId"));
                        System.out.println("Username: " + rs.getString("username"));
                        System.out.println("Checking Accounts:");
                        userFound = true;
                        firstRow = false;
                    }

                    int accountId = rs.getInt("accountId");
                    if (!rs.wasNull()) {
                        double balance = rs.getDouble("balance");
                        System.out.printf("  Account ID: %d, Balance: $%.2f%n", accountId, balance);
                    }
                }

                if (!userFound) {
                    System.out.println("User not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public double withdrawBalance(int userId, double amount, int accountId){
        amount *= -1;
        return addToBalance(userId, amount, accountId);
    }
    public int deleteAccount(int accountId){
        String sql = "delete from checkingAccount where accountId = ?";

        try(Connection connection = DatabaseConnector.createConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, accountId);
            int result = preparedStatement.executeUpdate();
            if(result == 1) {
                return accountId;
            }
            throw new UserSQLException ("Action could not be completed");
        }catch(SQLException exception){
            throw new UserSQLException(exception.getMessage());
        }
    }
    public boolean accountAccessCheck(int userId, int accountId){
        String sql = "SELECT COUNT(*) FROM checkingAccount WHERE userId = ? AND accountId = ?";

        try (Connection connection = DatabaseConnector.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, accountId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
