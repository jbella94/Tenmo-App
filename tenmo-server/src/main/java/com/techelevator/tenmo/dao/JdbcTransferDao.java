package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Repository
public class JdbcTransferDao implements TransferDao {


    private TransferDao transferDao;
    private UserDao userDao;
    private AccountDao accountDao;


    private final JdbcTemplate jdbcTemplate;

@Autowired
    public JdbcTransferDao(JdbcTemplate jdbcTemplate, UserDao userDao, AccountDao accountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @Override
    public Transfer makeTransfer(TransferDto transferDto) {
//        userDao.getUsers();

        try {
            //CHECK IF PARAMETERS BELOW//
        if (transferDto.getAccountFrom() == transferDto.getAccountTo()) {
            throw new IllegalArgumentException("Cannot send money to yourself");
        }
        if (transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cannot send $0 or negative funds.");
        }
        BigDecimal fromAccountBalance = accountDao.getAccountBalanceByAccountId(transferDto.getAccountFrom()).getBalance();
        if (fromAccountBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("You have insufficient funds for transfer.");
        }

        //SQL QUERIES
        String updateBalanceSender = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
        String updateBalanceReceiver = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
        String insertTransferInfo = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?)";


        //CONNECT VIA JDBC TEMPLATE
            jdbcTemplate.update(updateBalanceSender, transferDto.getAmount(), transferDto.getAccountFrom());
            jdbcTemplate.update(updateBalanceReceiver, transferDto.getAmount(), transferDto.getAccountTo());
//            jdbcTemplate.update(insertTransferInfo, 1, 1,transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount());
            jdbcTemplate.update(insertTransferInfo, 1, 1, transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount());

            return new Transfer(1,1, transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount());
        } catch (Exception e) {
            throw new RuntimeException("Error has occurred while processing transfer...");
        }

    }


    private Transfer mapRowToTransfer(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rowSet.getInt("transfer_Id"));
        transfer.setTransferTypeId(rowSet.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rowSet.getInt("transfer_status_id"));
        transfer.setAccountFrom(rowSet.getInt("account_from"));
        transfer.setAccountTo(rowSet.getInt("account_to"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        return transfer;
    }


}
