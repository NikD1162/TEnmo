package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AccountService {

    private String API_BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private String userToken = null;

    public AccountService(String API_BASE_URL) {
        this.API_BASE_URL = API_BASE_URL;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Account findAccountByUserId(long userId, AuthenticatedUser currentUser) {
        setUserToken(currentUser.getToken());
        Account account = null;
        HttpEntity<Void> entity = makeVoidEntity();
        try {
            ResponseEntity<Account> responseEntity = restTemplate.exchange(API_BASE_URL + userId, HttpMethod.GET, entity, Account.class);
            account = responseEntity.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public void updateAccountBalance(long userId, AuthenticatedUser currentUser, BigDecimal amount, boolean positive) {
        Account account = findAccountByUserId(userId, currentUser);
        if (positive) account.setBalance(account.getBalance().add(amount));
        else account.setBalance(account.getBalance().subtract(amount));
        HttpEntity<Account> entity = makeAccountEntity(account);
        try {
            restTemplate.exchange(API_BASE_URL, HttpMethod.PUT, entity, Void.class);
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    public BigDecimal getBalance(AuthenticatedUser currentUser) {
        Account account = findAccountByUserId(currentUser.getUser().getId(), currentUser);
        BigDecimal balance = account.getBalance();
        balance = balance.setScale(2, RoundingMode.DOWN);
        return balance;
    }

    public Account findAccountUsernameByAccountId(long accountId, AuthenticatedUser currentUser) {
        setUserToken(currentUser.getToken());
        Account account = null;
        HttpEntity<Void> entity = makeVoidEntity();
        try {
        ResponseEntity<Account> responseEntity = restTemplate.exchange(API_BASE_URL + "?account_id=" + accountId, HttpMethod.GET, entity, Account.class);
        account = responseEntity.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
        BasicLogger.log(e.getMessage());
        }
        return account;
    }

    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        return new HttpEntity<>(account, headers);
    }

    private HttpEntity<Void> makeVoidEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        return new HttpEntity<>(headers);
    }

}
