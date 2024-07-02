package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/accounts")
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private AccountDao accountDao;

   // private AccountService accountService;

    @Autowired
    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

//    Create pathway to server to implement getAccountBalanceByAccountId
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Account> get(@PathVariable int accountId) {
        Account account = accountDao.getAccountBalanceByAccountId(accountId);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        } else {
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
    }


}
