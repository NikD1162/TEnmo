package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
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

    public List<Transfer> list(long accountId, AuthenticatedUser currentUser) {
        setUserToken(currentUser.getToken());
        List<Transfer> list = new ArrayList<>();
        HttpEntity<Void> entity = makeVoidEntity();
//        try {
            ResponseEntity<Transfer[]> responseEntity = restTemplate.exchange(API_BASE_URL + "?account_id=" + accountId, HttpMethod.GET, entity, Transfer[].class);
            list = Arrays.asList(responseEntity.getBody());
//        }
//        catch (RestClientResponseException | ResourceAccessException e) {
//            BasicLogger.log(e.getMessage());
//        }
        return list;
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
