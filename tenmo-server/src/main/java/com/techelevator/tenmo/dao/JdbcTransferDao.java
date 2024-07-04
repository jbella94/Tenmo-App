package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
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
    public Transfer getTransferById(int transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE transfer_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
            if (results.next()) {
                transfer = mapRowToTransfer(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfer;
    }

    public Transfer makeTransfer(TransferDto transferDto) {
        Transfer newTransfer = null;

        // Check if the parameters are valid
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

        String sqlInsertTransfer = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
        String sqlUpdateBalanceSender = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
        String sqlUpdateBalanceReceiver = "UPDATE account SET balance = balance + ? WHERE account_id = ?";

        try {
            // Insert the transfer and get the new transfer ID
            int newTransferId = jdbcTemplate.queryForObject(
                    sqlInsertTransfer,
                    new Object[]{transferDto.getTransferTypeId(), transferDto.getTransferStatusId(), transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount()},
                    Integer.class
            );

            // Update the balances of the sender and receiver
            jdbcTemplate.update(sqlUpdateBalanceSender, transferDto.getAmount(), transferDto.getAccountFrom());
            jdbcTemplate.update(sqlUpdateBalanceReceiver, transferDto.getAmount(), transferDto.getAccountTo());

            // Retrieve the new transfer object
            newTransfer = getTransferById(newTransferId);

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newTransfer;
    }

//    @Override
//    public Transfer makeTransfer(TransferDto transfer) {
//
//    Transfer newTransfer = null;
//
//    String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
//        try{
//            int newTrasnferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferTypeId(), transfer.getTransferTypeId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
//            newTransfer = getTransferById(newTrasnferId);
//            if( newTransfer != null){
//
//            }
//        }


//        userDao.getUsers();

//        try {
//            //CHECK IF PARAMETERS BELOW//
//        if (transferDto.getAccountFrom() == transferDto.getAccountTo()) {
//            throw new IllegalArgumentException("Cannot send money to yourself");
//        }
//        if (transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("Cannot send $0 or negative funds.");
//        }
//        BigDecimal fromAccountBalance = accountDao.getAccountBalanceByAccountId(transferDto.getAccountFrom()).getBalance();
//        if (fromAccountBalance.compareTo(BigDecimal.ZERO) < 0) {
//            throw new IllegalArgumentException("You have insufficient funds for transfer.");
//        }
//
//        //SQL QUERIES
//        String updateBalanceSender = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
//        String updateBalanceReceiver = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
//        String insertTransferInfo = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
//
//
//        //CONNECT VIA JDBC TEMPLATE
//            jdbcTemplate.update(updateBalanceSender, transferDto.getAmount(), transferDto.getAccountFrom());
//            jdbcTemplate.update(updateBalanceReceiver, transferDto.getAmount(), transferDto.getAccountTo());
////            jdbcTemplate.update(insertTransferInfo, 1, 1,transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount());
//            //jdbcTemplate.queryForObject(insertTransferInfo, transferDto.getTransferTypeId(), transferDto.getTransferStatusId(), transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount());
//
//
//            Integer transferId = jdbcTemplate.queryForObject(
//                    insertTransferInfo,
//                    new Object[]{
//                            transferDto.getTransferTypeId(),
//                            transferDto.getTransferStatusId(),
//                            transferDto.getAccountFrom(),
//                            transferDto.getAccountTo(),
//                            transferDto.getAmount()
//                    },
//                    Integer.class
//            );
//
//
//            return new Transfer(transferId, transferDto.getTransferTypeId(), transferDto.getTransferStatusId(), transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount());
//        } catch (Exception e) {
//            throw new RuntimeException("Error has occurred while processing transfer...");
//        }

//    }


//    private Transfer mapRowToTransfer(SqlRowSet rowSet) {
//        Transfer transferRow = new Transfer();
//        transfer.setTransferId(rowSet.getInt("transfer_Id"));
//        transfer.setTransferTypeId(rowSet.getInt("transfer_type_id"));
//        transfer.setTransferStatusId(rowSet.getInt("transfer_status_id"));
//        transfer.setAccountFrom(rowSet.getInt("account_from"));
//        transfer.setAccountTo(rowSet.getInt("account_to"));
//        transfer.setAmount(rowSet.getBigDecimal("amount"));
//        return transferRow ;

        private Transfer mapRowToTransfer(SqlRowSet rowSet) {
            Transfer transferRow = new Transfer();
            transferRow.setTransferId(rowSet.getInt("transfer_id")); // Ensure the column name matches your database schema
            transferRow.setTransferTypeId(rowSet.getInt("transfer_type_id"));
            transferRow.setTransferStatusId(rowSet.getInt("transfer_status_id"));
            transferRow.setAccountFrom(rowSet.getInt("account_from"));
            transferRow.setAccountTo(rowSet.getInt("account_to"));
            transferRow.setAmount(rowSet.getBigDecimal("amount"));
            return transferRow;
        }


    }


