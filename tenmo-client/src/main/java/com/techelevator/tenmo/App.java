package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);

    private final TransferDto transferDto = new TransferDto();



    private final Scanner scanner = new Scanner(System.in);
    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();

    }


    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();

        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
//        BigDecimal balance = accountService.getBalanceByAccountId(accountId);
//        System.out.println("Your current Balance is : $ " + balance);
        if (currentUser != null) {
            // Assuming there's a method in AuthenticationService or related service to fetch balance
            //int accountId = currentUser.getUser().getId();
            BigDecimal balance = accountService.getCurrentBalance(currentUser.getToken());
            if (balance != null) {
                System.out.println("Your current account balance is: $" + balance);
            } else {
                System.out.println("Failed to fetch balance.");
            }
        } else {
            System.out.println("You need to login first.");
        }
    }
		


	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        if(currentUser != null){
            System.out.println("-----------------------------");
            System.out.println("Would you like to view all transfers or a specific transfer?");
            System.out.println("-----------------------------");
            System.out.println("To view all transfers enter 1 ");
            System.out.println("To view a specific transfer enter 2 ");
            System.out.println("-----------------------------");
            System.out.print("Please choose an option: ");



            int userSelection = Integer.parseInt(scanner.nextLine());
            if(userSelection == 1){
                List<Transfer> transfers = transferService.viewTransferHistory(currentUser.getToken(), currentUser.getUser().getId());
                for(Transfer transfer : transfers){
                    System.out.println(transfer);
                }
            }else if(userSelection == 2){
                System.out.print("Please insert transfer ID: ");
                int userSelection2 = Integer.parseInt(scanner.nextLine());
                Transfer transfer = transferService.viewTransferById(currentUser.getToken(), userSelection2);
                System.out.println(transfer);
            }else {
                System.out.println("Invalid selection");
            }

        }
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub

        if(currentUser != null){
            System.out.println("----------------------------");
            System.out.println("Below are your pending transfers:");
            List<Transfer> transfers = transferService.viewPendingRequests(currentUser.getToken(), currentUser.getUser().getId());

            if (transfers.isEmpty()) {
                System.out.println("You have no pending transfers.");
                mainMenu(); // Call to the main menu method
                return;
            }

            for(Transfer transfer : transfers){

                System.out.println(transfers);
                System.out.println("                ");
                System.out.println("If you would like to approve a transfer enter 1");
                System.out.println("If you would like to reject a transfer enter 2");
            }
            int userSelection = Integer.parseInt(scanner.nextLine());
            if (userSelection == 1) {
                System.out.println("Please enter a transfer ID from the list above.");
                int transferId = Integer.parseInt(scanner.nextLine());
                String result = transferService.approveTransfer(currentUser.getToken(), transferId);
                System.out.println(result);
            } else if (userSelection == 2) {
                System.out.println("Please enter a transfer ID from the list above.");
                int transferId = Integer.parseInt(scanner.nextLine());
                String result = transferService.rejectTransfer(currentUser.getToken(), transferId);
                System.out.println(result);
            } else {
                System.out.println("Invalid Selection");
            }


        }
		
	}

	private void sendBucks() {
        // TODO Auto-generated method stub

        if (currentUser != null) {
            List<User> users = userService.getAllUsers(currentUser.getToken());
            System.out.println("---------------------------------");
            System.out.println("Available users to send bucks to:");
            System.out.println("---------------------------------");
            for (User user : users) {
                if (user.getId() != currentUser.getUser().getId()) {
                    System.out.println("User ID: " + user.getId() + ", Username: " + user.getUsername());
                }
            }
                System.out.println("---------------------------------");
                System.out.println("Enter recipient User Id: ");
                int accountTo = Integer.parseInt(scanner.nextLine());

                System.out.println("Enter transfer amount");
                BigDecimal amount = new BigDecimal(scanner.nextLine());

                TransferDto transferDto = new TransferDto();
                transferDto.setAccountFrom(currentUser.getUser().getId());
                transferDto.setAccountTo(accountTo);
                transferDto.setAmount(amount);

                String transferOutcome = transferService.sendBucks(currentUser.getToken(), transferDto);

                if (transferOutcome != null) {
                    System.out.println(transferOutcome);
                } else {
                    System.out.println("Transfer failed");
                }
            }
//
        }


	private void requestBucks() {
		// TODO Auto-generated method stub

        if(currentUser != null){
            List<User> users = userService.getAllUsers(currentUser.getToken());
            System.out.println("---------------------------------");
            System.out.println("Available users to request bucks from:");
            System.out.println("---------------------------------");
            for(User user : users) {
                if (user.getId() != currentUser.getUser().getId()) {
                    System.out.println("User ID: " + user.getId() + ", Username: " + user.getUsername());
                }
            }
                System.out.println("------------------------------");
                System.out.print("Enter recipient User Id: ");
                int accountRequested = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter transfer amount: ");
                BigDecimal amount = new BigDecimal(scanner.nextLine());

                TransferDto transferDto = new TransferDto();
                transferDto.setAccountFrom(currentUser.getUser().getId());
                transferDto.setAccountTo(accountRequested);
                transferDto.setAmount(amount);

                String requestOutcome = transferService.requestBucks(currentUser.getToken(), transferDto);

                if(requestOutcome != null){
                    System.out.println("Request sent");
                }else{
                    System.out.println("Request failed");
                }
            }
        }

	}


