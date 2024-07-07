package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.util.BasicLogger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;


public class TransferService {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final RestTemplate restTemplate = new RestTemplate();


    private String authToken = null;

    public TransferService (String url){
        this.authToken = authToken;
    }

    public String sendBucks(String authToken, TransferDto transferDto){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TransferDto> entity = new HttpEntity<>(transferDto, headers);


        String transferOutcome = null;

        try{
            String url = API_BASE_URL + "accounts/transfers/maketransfer";
            //System.out.println("Fetching transfer information" + url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            transferOutcome = response.getBody();

        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }

        return transferOutcome;
    }

    public List<Transfer> viewTransferHistory(String authToken, int userId){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        List<Transfer> transferHistory = new ArrayList<>();



        try{
            String url = API_BASE_URL + "accounts/transfers/{userId}/transferhistory";
            //System.out.println("Fetching transfer information from " + url);

            String expandedUrl = UriComponentsBuilder.fromUriString(url)
                    .buildAndExpand(userId)
                    .toUriString();

//            ResponseEntity<Transfer> response = restTemplate.exchange(url, HttpMethod.GET, entity, Transfer.class);
            ResponseEntity<List<Transfer>> response = restTemplate.exchange(
                    expandedUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Transfer>>() {}
            );
            transferHistory = response.getBody();

        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }

        return transferHistory;
    }

    public Transfer viewTransferById(String authToken, int transferId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Transfer transfer = null;

        try {
            String url = API_BASE_URL + "accounts/transfers/{transferId}";
            //System.out.println("Fetching transfer information from " + url);

            String expandedUrl = UriComponentsBuilder.fromUriString(url)
                    .buildAndExpand(transferId)
                    .toUriString();

            ResponseEntity<Transfer> response = restTemplate.exchange(
                    expandedUrl,
                    HttpMethod.GET,
                    entity,
                    Transfer.class
            );

            transfer = response.getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfer;
    }

    public List<Transfer> viewPendingRequests(String authToken, int userId){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        List<Transfer> pendingTransfers = new ArrayList<>();

        try{
            String url = API_BASE_URL + "accounts/transfers/{userId}/pendingtransfers";
            //System.out.println("Fetching pending transfers from " + url);
            String expandedUrl = UriComponentsBuilder.fromUriString(url).buildAndExpand(userId).toUriString();

            ResponseEntity<List<Transfer>> response = restTemplate.exchange(
                    expandedUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Transfer>>() {}
            );
            pendingTransfers = response.getBody();

        }catch(RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }

            return pendingTransfers;
    }

    public String requestBucks(String authToken, TransferDto transferDto){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TransferDto> entity = new HttpEntity<>(transferDto, headers);

        String bucksRequested = null;

        try{
            String url = API_BASE_URL + "accounts/transfers/request";
            //System.out.println("Requesting transfer" + url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            bucksRequested = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return bucksRequested;
    }

    }

