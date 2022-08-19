package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private static final String API_BASE_URL_ACCOUNT = "http://localhost:8080/account/";
    private static final String API_BASE_URL_USER = "http://localhost:8080/user/";
    private static final String API_BASE_URL_TRANSFER = "http://localhost:8080/transfer/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL_ACCOUNT);
    private final UserService userService = new UserService(API_BASE_URL_USER);
    private final TransferService transferService = new TransferService(API_BASE_URL_TRANSFER);

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
        System.out.println("Your current account balance is: $" + accountService.getBalance(currentUser));
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        long accountId =  accountService.findAccountByUserId(currentUser.getUser().getId(), currentUser).getAccountId();
        transferService.printTransfers(accountId, accountService, currentUser);
        long transferId = consoleService.promptForMenuSelection("Please enter transfer ID to view details (0 to cancel): ");
        if (transferId == 0) {
            mainMenu();
            return;
        }
        Transfer transfer = transferService.getTransferById(transferId, accountId, currentUser);
        if (transfer == null) {
            System.out.println(System.lineSeparator() + "*** Transfer does not exist ***");
            consoleService.pause();
            viewTransferHistory();
            return;
        }
        transferService.printTransfer(transfer, accountId, accountService, currentUser);
        consoleService.pause();
        viewTransferHistory();
    }

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		userService.printUsers(currentUser);
        long userId = consoleService.promptForMenuSelection("Enter ID of user you are sending to (0 to cancel): ");
        if (userId == 0) {
            mainMenu();
            return;
        }
        else if (currentUser.getUser().getId() == userId) {
            System.out.println(System.lineSeparator() + "*** Prohibited transfer, transfer to your own account ***");
            consoleService.pause();
            sendBucks();
            return;
        }
        else if (!userService.checkUserExists(userId, currentUser)) {
            System.out.println(System.lineSeparator() + "*** User does not exist ***");
            consoleService.pause();
            sendBucks();
            return;
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println(System.lineSeparator() + "*** Prohibited transfer, amount less or equal to 0 ***");
            consoleService.pause();
            sendBucks();
            return;
        }
        else if (accountService.getBalance(currentUser).compareTo(amount)  <= 0) {
            System.out.println(System.lineSeparator() + "*** Insufficient funds ***");
            consoleService.pause();
            sendBucks();
            return;
        }
        long accountFrom = accountService.findAccountByUserId(currentUser.getUser().getId(), currentUser).getAccountId();
        long accountTo = accountService.findAccountByUserId(userId, currentUser).getAccountId();
        transferService.transferSend(currentUser, accountFrom, accountTo, amount);
        accountService.updateAccountBalance(userId, currentUser, amount, true);
        accountService.updateAccountBalance(currentUser.getUser().getId(), currentUser, amount, false);
        System.out.println(System.lineSeparator() + "*** Transfer successfully completed ***");
        consoleService.pause();
        mainMenu();
    }

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
