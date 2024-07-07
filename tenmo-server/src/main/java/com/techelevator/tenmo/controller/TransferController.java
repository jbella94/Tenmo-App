package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/accounts/transfers")
@PreAuthorize("isAuthenticated()")
public class TransferController {
    private final TransferDao transferDao;
    private final AccountDao accountDao;
    private final UserDao userDao;
    @Autowired
    public TransferController(TransferDao transferDao, AccountDao accountDao, UserDao userDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        this.userDao = userDao;
    }
    @PostMapping("/maketransfer")
    public ResponseEntity<String> makeTransfer(@RequestBody TransferDto transferDto, Principal principal) {
        // Get the authenticated user (sender)
        User sender = userDao.getUserByUsername(principal.getName());
        if (sender == null) {
            return ResponseEntity.status(404).body("Sender not found");
        }
        // Get the recipient user
        User recipient = userDao.getUserById(transferDto.getAccountTo());
        if (recipient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipient not found");
        }

        // Get the sender's and recipient's accounts
        Account senderAccount = accountDao.getAccountBalanceByAccountId(sender.getId());
        Account recipientAccount = accountDao.getAccountBalanceByAccountId(recipient.getId());
        // Sender Limitations
        if (transferDto.getAccountFrom() == transferDto.getAccountTo()) {
            throw new IllegalArgumentException("Cannot send money to yourself");
        }
        if (transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cannot send $0 or negative funds.");
        }
        BigDecimal fromAccountBalance = accountDao.getAccountBalanceByAccountId(transferDto.getAccountFrom()).getBalance();
        if (fromAccountBalance.compareTo(transferDto.getAmount()) < 0) {
            throw new IllegalArgumentException("You have insufficient funds for transfer.");
        }
        // Perform the transfer: update balances
        senderAccount.setBalance(senderAccount.getBalance().subtract(transferDto.getAmount()));
        recipientAccount.setBalance(recipientAccount.getBalance().add(transferDto.getAmount()));
        // Save the updated accounts
        accountDao.save(senderAccount);
        accountDao.save(recipientAccount);

        transferDao.makeTransfer(transferDto);
        // Return a success response
        return ResponseEntity.ok("Approved");
    }


    //Created API Endpoint for transfers based on UserId
    @GetMapping("/{userId}/transferhistory")
    public ResponseEntity <List <Transfer>> getTransfersByUserId(@PathVariable int userId, Principal principal) {
        User authenticatedUser = userDao.getUserByUsername(principal.getName());
        if(authenticatedUser.getId() != userId){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Transfer> transfers = transferDao.getTransfersByUserId(userId);
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }
    //Created API Endpoint for transfers based on TransferId
    @GetMapping("/{transferId}")
    public Transfer getTransferById(@PathVariable int transferId) {
        return transferDao.getTransferById(transferId);
    }
    @PostMapping("/request")
    public ResponseEntity<String> createTransferRequest(@RequestBody TransferDto transferDto, Principal principal) {
        // Get the authenticated user (requester)
        User requester = userDao.getUserByUsername(principal.getName());
        if (requester == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requester not found");
        }

        // Set the accountFrom to the requester's userId
        transferDto.setAccountFrom(requester.getId());

        // Set the transfer type and status for request
        transferDto.setTransferTypeId(1); // 1 = Request
        transferDto.setTransferStatusId(1); // 1 = Pending

        // Create the transfer request
        Transfer newTransfer = transferDao.createTransferRequest(transferDto);
        return ResponseEntity.ok("Transfer request " + newTransfer.getTransferId() + " created successfully");
    }

    @GetMapping("/{userId}/pendingtransfers")
    public ResponseEntity<List<Transfer>> getPendingTransfersByUserId(@PathVariable int userId, Principal principal) {
        User authenticatedUser = userDao.getUserByUsername(principal.getName());
        if (authenticatedUser.getId() != userId) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Transfer> transfers = transferDao.getPendingTransfersByUserId(userId);
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

}
