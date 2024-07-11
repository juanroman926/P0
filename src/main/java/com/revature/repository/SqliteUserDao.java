package com.revature.repository;

import com.revature.entity.User;
import com.revature.exception.LoginFail;
import com.revature.utility.DatabaseConnector;
import com.revature.exception.UserSQLException;
import com.revature.utility.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteUserDao implements UserDao{
    @Override
    public User createUser(User newUserCredentials){
        String sql = "insert into user (username, password) values (?, ?)";
        try(Connection connection = DatabaseConnector.createConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,newUserCredentials.getUsername());
            preparedStatement.setString(2,newUserCredentials.getPassword());

            int result = preparedStatement.executeUpdate();
            if(result == 1){
                return newUserCredentials;
            }
            throw new UserSQLException ("User could not be created: please try again");
        }catch(SQLException exception){
            throw new UserSQLException(exception.getMessage());
        }
    }
    @Override
    public  List<User> getAllUsers(){
        String sql = "select * from user";
        try(Connection connection = DatabaseConnector.createConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            List<User> users = new ArrayList<>();
            while(resultSet.next()){
                User userRecord = new User();
                userRecord.setUsername(resultSet.getString("username"));
                userRecord.setPassword(resultSet.getString("password"));
                users.add(userRecord);
            }
            return users;
        } catch (SQLException exception){
            throw new UserSQLException(exception.getMessage());
        }
    }


}
