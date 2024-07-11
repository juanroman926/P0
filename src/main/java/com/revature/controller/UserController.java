package com.revature.controller;

import com.revature.entity.User;
import com.revature.exception.LoginFail;
import com.revature.service.UserService;

import java.sql.SQLOutput;
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
        System.out.println("Welcome to The Bank");
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
                    controlMap.put("User", login().getUsername());
                    break;
                case "q":
                    System.out.println("Goodbye!");
                    controlMap.put("Continue Loop", "false");
                    break;
            }
        } catch (LoginFail exception){
            System.out.println(exception.getMessage());
        }
    }
    public void registerNewUser(){
        User newCredentials = getUserCredentials();
        User newUser = userService.validateNewCredentials(newCredentials);
        System.out.printf("New account created: %s\n", newUser);
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
    public void bankingPortal(Map<String, String> controlMap){
        boolean flag = true;
        while(flag) {
            System.out.println("BANKING PORTAL");
            System.out.println("--------------");
            System.out.println("1. Create checking account");
            System.out.println("2. View active accounts");
            System.out.println("3. Logout");
            int userId = userService.getUserId(controlMap.get("User"));
            try {
                String userInput = scanner.nextLine();
                switch (userInput) {
                    case "1":
                        createAccountInController(userId);
                        break;
                    case "2":
                        viewActiveAccounts(userId);
                        break;
                    case "3":
                        System.out.printf("\'%s\' logged out successfully\n", controlMap.get("User"));
                        System.out.println("--------------");
                        flag = false;
                        controlMap.remove("User");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (LoginFail exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
    public void createAccountInController(int userId){
        System.out.println("Create Checking account");
        int accountId = userService.createAccount(userId);
        System.out.println(accountId);
        System.out.println("Checking account created successfully!");
        System.out.println("ID:" + userId);
        System.out.println("Balance: " + userService.getBalance(userId, accountId));
        double amount = 0;
        try{
            System.out.println("Amount to Deposit: ");
            amount = scanner.nextDouble();
            scanner.nextLine();
            double newBalance = userService.addToBalance(userId, amount,accountId);
            System.out.println("New Balance: " + userService.getBalance(userId, accountId));
        }catch (LoginFail exception){
            System.out.println(exception.getMessage());
        }
    }
    public void displayCheckingAccounts(int userId){
        userService.displayUserWithAccounts(userId);
    }
    public void viewActiveAccounts(int userId) {
        displayCheckingAccounts(userId);
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Close a checking account");
        System.out.println("4. Quit");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                deposit(userId);
                break;
            case 2:
                withdraw(userId);
                break;
            case 3:
              closeAccount();
            case 4:
                break;
        }
    }
    public void deposit(int userId){
        int accountId = 0;
        double amount = 0;
        System.out.print("Account ID: ");
        accountId = scanner.nextInt();
        System.out.print("Amount: ");
        amount = scanner.nextDouble();
        scanner.nextLine();
        userService.addToBalance(userId, amount, accountId);
        System.out.println("New balance: " + userService.getBalance(userId, accountId));
    }
    public void withdraw(int userId){
        int accountId = 0;
        double amount = 0;
        System.out.print("Account ID: ");
        accountId = scanner.nextInt();
        System.out.print("Amount: ");
        amount = scanner.nextDouble();
        scanner.nextLine();
        userService.withdrawBalance(userId, amount, accountId);
        System.out.println("New balance: " + userService.getBalance(userId, accountId));
    }
    public void closeAccount(){
        System.out.print("Account ID: ");
        int accountId= scanner.nextInt();
        scanner.nextLine();
        int accountClosed = userService.deleteAccount(accountId);
        if(accountClosed != -1) {
            System.out.println("Checking account " + accountClosed + " closed successfully");
        }
        else{
            System.out.println("Checking account" + accountId+ "not found");
        }
    }

}
