package com.revature.controller;

import com.revature.entity.User;
import com.revature.exception.LoginFail;
import com.revature.service.UserService;
import java.util.Map;
import java.util.Scanner;

public class UserController {
    private Scanner scanner;
    private UserService userService;

    public UserController(Scanner scanner, UserService userService) {
        this.scanner = scanner;
        this.userService = userService;
    }
    public void promptUserForService(Map<String, String> controlMap){
        System.out.println("1. Register an account");
        System.out.println("2. Login");
        System.out.println("q. Quit");
        try {
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1":
                    registerNewUser();
                    break;
                case "2":
                    login();
                    break;
                case "q":
                    System.out.println("Goodbye!");
                    controlMap.put("Continue Loop", "false");
            }
        } catch (LoginFail exception){
            System.out.println(exception.getMessage());
        }
    }
    public void registerNewUser(){
        User newCredentials = getUserCredentials();
        User newUser = userService.validateNewCredentials(newCredentials);
        System.out.printf("New account created: %s", newUser);
        System.out.println("");
    }
    public User login(){
        return userService.checkLoginCredentials(getUserCredentials());
    }

    public User getUserCredentials(){
        String newUsername;
        String newPassword;
        System.out.println("Please enter a username: ");
        newUsername = scanner.nextLine();
        System.out.println("Please enter a password");
        newPassword = scanner.nextLine();
        return new User(newUsername, newPassword);
    }


}
