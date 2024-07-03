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

//@RestController
//@RequestMapping(path = "/accounts/transfers")
//@PreAuthorize("isAuthenticated()")
//
//public class TransferController {
//
//    private TransferDao transferDao;
//    private AccountDao accountDao;
//    private UserDao userDao;
//
//    @Autowired
//    public TransferController(TransferDao transferDao, AccountDao accountDao, UserDao userDao) {
//        this.transferDao = transferDao;
//        this.accountDao = accountDao;
//        this.userDao = userDao;
//    }
//
//    @GetMapping("/maketransfer")
//    public ResponseEntity<Transfer> makeTransfer(TransferDto transferDto, Principal principal) {
//        User user = userDao.getUserByUsername(principal.getName());
//
//        transferDto.setAccountFrom(user.getId());
//
//        Transfer transfer = transferDao.makeTransfer(transferDto);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(transfer);
//
//
//    }
//
//    @GetMapping("/viewtransferinfo")
//    public ResponseEntity<List<Transfer>>
//
//
//}
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
    @GetMapping("/maketransfer")
    public ResponseEntity<String> makeTransfer(@RequestBody TransferDto transferDto, Principal principal) {
        // Get the authenticated user (sender)
        User sender = userDao.getUserByUsername(principal.getName());
        if (sender == null) {
            return ResponseEntity.status(404).body("Sender not found");
        }
        // Get the recipient user
        User recipient = userDao.getUserById(transferDto.getAccountTo());
//                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        // Get the sender's and recipient's accounts
        Account senderAccount = accountDao.getAccountBalanceByAccountId(sender.getId());
        Account recipientAccount = accountDao.getAccountBalanceByAccountId(recipient.getId());
        // Sender Limitations
        if (transferDto.getAccountFrom() == transferDto.getAccountFrom()) {
            throw new IllegalArgumentException("Cannot send money to yourself");
        }
        if (transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cannot send $0 or negative funds.");
        }
        BigDecimal fromAccountBalance = accountDao.getAccountBalanceByAccountId(transferDto.getAccountFrom()).getBalance();
        if (fromAccountBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("You have insufficient funds for transfer.");
        }
        // Perform the transfer: update balances
        senderAccount.setBalance(senderAccount.getBalance().subtract(transferDto.getAmount()));
        recipientAccount.setBalance(recipientAccount.getBalance().add(transferDto.getAmount()));
//        // Save the updated accounts
//        accountDao.save(senderAccount);
//        accountDao.save(recipientAccount);
//        // Create the transfer record
//        Transfer transfer = new Transfer();
//        transfer.setAccountFrom(sender.getId());
//        transfer.setAccountTo(recipient.getId());
//        transfer.setAmount(transferDto.getAmount());
//        transferDao.save(transfer);
        // Return a success response
        return ResponseEntity.ok("Transfer successful");
    }
}