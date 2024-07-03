package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.util.BasicLogger;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;


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
//        int accountFrom = 0;
//        int accountTo = 0;
//        BigDecimal amount = BigDecimal.valueOf(0);
        try{
            String url = API_BASE_URL + "accounts/transfers/maketransfer";
            System.out.println("Fetching transfer information from" + url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            transferOutcome = response.getBody();

        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }

        return transferOutcome;
    }
}
