package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public UserService(String url) {
        this.baseUrl = url;
    }

    public List<User> getAllUsers(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<User[]> response = restTemplate.exchange(baseUrl + "users", HttpMethod.GET, entity, User[].class);
        return response.getBody() != null ? Arrays.asList(response.getBody()) : new ArrayList<>();
    }
}
