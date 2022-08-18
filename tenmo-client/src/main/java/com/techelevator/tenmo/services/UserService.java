package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class UserService {

    private String API_BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private String userToken = null;

    public UserService(String API_BASE_URL) {
        this.API_BASE_URL = API_BASE_URL;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    private User[] findAllUsers(AuthenticatedUser currentUser) {
        setUserToken(currentUser.getToken());
        User[] users = null;
        HttpEntity<Void> entity = makeVoidEntity();
        try {
            ResponseEntity<User[]> responseEntity = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, entity, User[].class);
            users = responseEntity.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }

    public void printUsers(AuthenticatedUser currentUser) {
        User[] users = findAllUsers(currentUser);
        String str = "_";
        System.out.println(System.lineSeparator() + str.repeat(40));
        System.out.printf("%-20s%-20s", "User ID", "Name");
        System.out.println(System.lineSeparator() + str.repeat(40));
        for (User user : users) {
            System.out.printf("%-20d%-20s%n", user.getId(), user.getUsername());
        }
        System.out.println(str.repeat(40) + System.lineSeparator());
    }

    public boolean checkUserExists(long userId, AuthenticatedUser currentUser) {
        User[] users = findAllUsers(currentUser);
        for (User user : users) {
            if (user.getId() == userId) return true;
        }
        return false;
    }

    private HttpEntity<User> makeUserEntity(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        return new HttpEntity<>(user, headers);
    }

    private HttpEntity<Void> makeVoidEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        return new HttpEntity<>(headers);
    }

}
