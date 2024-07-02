package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;

    public AccountService (String url){
        this.authToken = authToken;
    }

    //Method created for client to pull Balance by account ID on client side.
//    public BigDecimal getBalanceByAccountId(int accountId){
//        BigDecimal returnedBalance = null;
//        try{
//            ResponseEntity<BigDecimal> response = restTemplate.exchange(API_BASE_URL + "/accounts" + accountId, HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
//             returnedBalance = response.getBody();
//        }catch (RestClientResponseException | ResourceAccessException e){
//            BasicLogger.log(e.getMessage());
//        }
//        return returnedBalance;
//    }
    public BigDecimal getCurrentBalance(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        BigDecimal balance = null;
        Account account = null;
        try {
            String url = API_BASE_URL + "accounts/balance";
            System.out.println("Fetching balance from URL: " + url);
            ResponseEntity<Account> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Account.class
            );
            account = response.getBody();
            balance = account.getBalance();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(account, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

}
