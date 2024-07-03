package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);

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
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        if(currentUser != null){
            System.out.println("Enter recipient User Id: ");
            int accountTo = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter transfer amount");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            TransferDto transferDto = new TransferDto();
            transferDto.setAccountFrom(currentUser.getUser().getId());
            transferDto.setAccountTo(accountTo);
            transferDto.setAmount(amount);

            String transferOutcome = transferService.sendBucks(currentUser.getToken(), transferDto);

            if (transferOutcome != null){
                System.out.println(transferOutcome);
            }else{
                System.out.println("Transfer failed");
            }
        }
//		if(currentUser != null){
//        String transferOutcome = transferService.sendBucks(currentUser.getToken(), transferDto);
//        System.out.println("Enter recipient User ID: ");
//        int accountTo = Integer.parseInt(scanner.nextLine());
//        System.out.println("Enter transfer amount: ");
//        BigDecimal amount = new BigDecimal(scanner.nextLine());
//        if(transferOutcome != null){
//            System.out.println(transferOutcome);
//        }else{
//            System.out.println("Transfer failed");
//        }
//
//        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
