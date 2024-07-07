package com.revature.service;

import com.revature.entity.User;
import com.revature.exception.LoginFail;
import com.revature.repository.UserDao;
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
        }
        throw new RuntimeException("placeholder for custom exception");
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
            boolean passwordMatches = user.getPassword().equals(credentials.getPassword());
            boolean usernameMatches = user.getUsername().equals(credentials.getUsername());
            if(passwordMatches && usernameMatches){
                return credentials;
            }
        }
        throw new LoginFail("Credentials are invalid!");
    }

}
