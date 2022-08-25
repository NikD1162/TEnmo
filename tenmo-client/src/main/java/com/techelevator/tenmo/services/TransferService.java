package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TransferService {

    private String API_BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private String userToken = null;

    private static final long SEND_ID = 1;
    private static final long REQUEST_ID = 2;
    private static final long PENDING_ID = 1;
    private static final long APPROVED_ID = 2;
    private static final long REJECTED_ID = 3;


    public TransferService(String API_BASE_URL) {
        this.API_BASE_URL = API_BASE_URL;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public boolean transferSend(AuthenticatedUser currentUser, long accountFrom, long accountTo, BigDecimal amount) {
        setUserToken(currentUser.getToken());
        Transfer transfer = new Transfer(SEND_ID, APPROVED_ID, accountFrom, accountTo, amount);
        return transfer(transfer);
    }

    public boolean transferRequest(AuthenticatedUser currentUser, long accountFrom, long accountTo, BigDecimal amount) {
        setUserToken(currentUser.getToken());
        Transfer transfer = new Transfer(REQUEST_ID, PENDING_ID, accountFrom, accountTo, amount);
        return transfer(transfer);
    }

    private boolean transfer(Transfer transfer){
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        try {
            ResponseEntity<Integer> responseEntity = restTemplate.exchange(API_BASE_URL, HttpMethod.POST, entity, Integer.class);
            return responseEntity.getBody() != null;
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return false;
    }

    public void transferUpdateStatus(Transfer transfer, long transferDecision) {
        if (transferDecision == 1) {
            transfer.setTransferStatusId(APPROVED_ID);
        }
        else if (transferDecision == 2) {
            transfer.setTransferStatusId(REQUEST_ID);
        }
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        try {
            ResponseEntity<Void> responseEntity = restTemplate.exchange(API_BASE_URL, HttpMethod.PUT, entity, Void.class);
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public List<Transfer> list(long accountId, AuthenticatedUser currentUser) {
        setUserToken(currentUser.getToken());
        List<Transfer> list = new ArrayList<>();
        HttpEntity<Void> entity = makeVoidEntity();
        try {
            ResponseEntity<Transfer[]> responseEntity = restTemplate.exchange(API_BASE_URL + "?account_id=" + accountId, HttpMethod.GET, entity, Transfer[].class);
            list = Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return list;
    }

    public List<Transfer> listPending(long accountId, AuthenticatedUser currentUser) {
        setUserToken(currentUser.getToken());
        List<Transfer> list = new ArrayList<>();
        HttpEntity<Void> entity = makeVoidEntity();
        try {
            ResponseEntity<Transfer[]> responseEntity = restTemplate.exchange(API_BASE_URL +
                    "?account_id=" + accountId + "&transfer_status_id=" + PENDING_ID, HttpMethod.GET, entity, Transfer[].class);
            list = Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return list;
    }

    public void printTransfers(long accountId, AccountService accountService, AuthenticatedUser currentUser) {
        List<Transfer> list = list(accountId, currentUser);
        String str = "_";
        System.out.println(System.lineSeparator() + str.repeat(45));
        System.out.println("Transfers");
        System.out.printf("%-15s%-15s%-15s", "ID", "From/To", "Amount");
        System.out.println(System.lineSeparator() + str.repeat(45));
        for (Transfer transfer : list) {
            long transferId = transfer.getTransferId();
            long accountFrom = transfer.getAccountFrom();
            long accountTo = transfer.getAccountTo();
            BigDecimal amount = transfer.getAmount();
            if (accountFrom != accountId) {
                System.out.printf("%-15s%-15s%-15s%n", transferId, "From: " +
                        accountService.findAccountUsernameByAccountId(accountFrom, currentUser).getUsername(), "$ " + amount);
            }
            if (accountTo != accountId) {
                System.out.printf("%-15s%-15s%-15s%n", transferId, "To: " +
                        accountService.findAccountUsernameByAccountId(accountTo, currentUser).getUsername(), "$ " + amount);
            }
        }
        System.out.println(str.repeat(45) + System.lineSeparator());
    }

    public void printPendingTransfers(long accountId, AccountService accountService, AuthenticatedUser currentUser) {
        List<Transfer> listPending = listPending(accountId, currentUser);
        String str = "_";
        System.out.println(System.lineSeparator() + str.repeat(45));
        System.out.println("Pending Transfers");
        System.out.printf("%-15s%-15s%-15s", "ID", "To", "Amount");
        System.out.println(System.lineSeparator() + str.repeat(45));
        for (Transfer transfer : listPending) {
            long transferId = transfer.getTransferId();
            long accountTo = transfer.getAccountTo();
            BigDecimal amount = transfer.getAmount();
            System.out.printf("%-15s%-15s%-15s%n", transferId, "To: " +
                    accountService.findAccountUsernameByAccountId(accountTo, currentUser).getUsername(), "$ " + amount);
        }
        System.out.println(str.repeat(45) + System.lineSeparator());
    }

    public Transfer getTransferById(long transferId, AuthenticatedUser currentUser) {
        Transfer transfer = null;
        setUserToken(currentUser.getToken());
        HttpEntity<Void> entity = makeVoidEntity();
        try {
            ResponseEntity<Transfer> responseEntity = restTemplate.exchange(API_BASE_URL + transferId, HttpMethod.GET, entity, Transfer.class);
            transfer = responseEntity.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    public void printTransfer(Transfer transfer, long accountId, AccountService accountService, AuthenticatedUser currentUser) {
        String str = "_";
        System.out.println(System.lineSeparator() + str.repeat(40));
        System.out.print("Transfer Details");
        System.out.println(System.lineSeparator() + str.repeat(40));
        System.out.println("Id: " + transfer.getTransferId());
        if (transfer.getAccountFrom() == accountId) {
            System.out.println("From: Me " + currentUser.getUser().getUsername());
            System.out.println("To: " + accountService.findAccountUsernameByAccountId(transfer.getAccountTo(), currentUser).getUsername());

        }
        else if (transfer.getAccountTo() == accountId) {
            System.out.println("From: " + accountService.findAccountUsernameByAccountId(transfer.getAccountFrom(), currentUser).getUsername());
            System.out.println("To: Me" + currentUser.getUser().getUsername());
        }
        if (transfer.getTransferTypeId() == SEND_ID) System.out.println("Type: Send");
        else if (transfer.getTransferTypeId() == REQUEST_ID) System.out.println("Type: Request");
        if (transfer.getTransferStatusId() == PENDING_ID) System.out.println("Status: Pending");
        else if (transfer.getTransferStatusId() == APPROVED_ID) System.out.println("Status: Approved");
        else if (transfer.getTransferStatusId() == REJECTED_ID) System.out.println("Status: Rejected");
        System.out.println("Amount: $" + transfer.getAmount());
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeVoidEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        return new HttpEntity<>(headers);
    }

}
