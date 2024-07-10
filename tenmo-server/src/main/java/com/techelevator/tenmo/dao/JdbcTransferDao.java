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

    //Create Variables
    private TransferDao transferDao;
    private UserDao userDao;
    private AccountDao accountDao;
    private final JdbcTemplate jdbcTemplate;

    //Constructor For Class
@Autowired
    public JdbcTransferDao(JdbcTemplate jdbcTemplate, UserDao userDao, AccountDao accountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    //SQL Query For GettingTransfersById
    @Override
    public Transfer getTransferById(int transferId) {
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()) {
            return mapRowToTransfer(results);
        }
        return null;
    }

    //Method For Sending Funds
    @Override
    public Transfer makeTransfer(TransferDto transferDto) {
        return insertTransfer(transferDto, 2, 2); // Send transfer with Approved status
    }

    //Method For Requesting Funds
    public Transfer createTransferRequest(TransferDto transferDto) {
        return insertTransfer(transferDto, 1, 1); // Request transfer with Pending status
    }
    //Generic Transfer Method
    private Transfer insertTransfer(TransferDto transferDto, int transferTypeId, int transferStatusId) {
        Transfer newTransfer = null;

        // Check if the parameters are valid
        if (transferDto.getAccountFrom() == transferDto.getAccountTo()) {
            throw new IllegalArgumentException("Cannot send money to yourself");
        }
        if (transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cannot send $0 or negative funds.");
        }
        if (transferTypeId == 2) {
            BigDecimal fromAccountBalance = accountDao.getAccountBalanceByAccountId(transferDto.getAccountFrom()).getBalance();
            if (fromAccountBalance.compareTo(transferDto.getAmount()) < 0) {
                throw new IllegalArgumentException("You have insufficient funds for transfer.");
            }
        }

        String sqlInsertTransfer = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, (SELECT account_id FROM account WHERE user_id = ?), (SELECT account_id FROM account WHERE user_id = ?), ?) RETURNING transfer_id";

        try {
            // Insert the transfer and get the new transfer ID
            int newTransferId = jdbcTemplate.queryForObject(
                    sqlInsertTransfer, Integer.class,
                    transferTypeId, transferStatusId, transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount()
            );

            // Retrieve the new transfer object
            newTransfer = getTransferById(newTransferId);

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newTransfer;
    }

    //Added method to getTransfersByUserId
    @Override
    public List<Transfer> getTransfersByUserId(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from IN (SELECT account_id FROM account WHERE user_id = ?) OR account_to IN (SELECT account_id FROM account WHERE user_id = ?)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while (results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

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

    @Override
    public List<Transfer> getPendingTransfersByUserId(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE (account_from IN (SELECT account_id FROM account WHERE user_id = ?) OR account_to IN (SELECT account_id FROM account WHERE user_id = ?)) AND transfer_status_id = 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while (results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    @Override
    public void updateTransferStatus(int transferId, int statusId) {
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?";
        jdbcTemplate.update(sql, statusId, transferId);
    }

    @Override
    public void approveTransferRequest(int transferId, String username) {
        Transfer transfer = getTransferById(transferId);
        if (transfer == null || transfer.getTransferStatusId() != 1) {
            throw new IllegalArgumentException("Invalid or non-pending transfer request.");
        }

        Account toAccount = accountDao.getAccountBalanceByAccountIds(transfer.getAccountTo());
        Account fromAccount = accountDao.getAccountBalanceByAccountIds(transfer.getAccountFrom());

        User currentUser = userDao.getUserByUsername(username);
        if (toAccount.getUserId() != currentUser.getId()) {
            throw new IllegalArgumentException("Only the recipient can approve the transfer.");
        }

        toAccount.setBalance(toAccount.getBalance().subtract(transfer.getAmount()));
        fromAccount.setBalance(fromAccount.getBalance().add(transfer.getAmount()));

        accountDao.save(fromAccount);
        accountDao.save(toAccount);

        updateTransferStatus(transferId, 2);
    }
    @Override
    public void rejectTransferRequest(int transferId, String username) {
        Transfer transfer = getTransferById(transferId);
        if (transfer == null || transfer.getTransferStatusId() != 1) {
            throw new IllegalArgumentException("Invalid or non-pending transfer request.");
        }
        Account toAccount = accountDao.getAccountBalanceByAccountIds(transfer.getAccountTo());

        User currentUser = userDao.getUserByUsername(username);
        if (toAccount.getUserId() != currentUser.getId()) {
            throw new IllegalArgumentException("Only the recipient can reject the transfer.");
        }
        updateTransferStatus(transferId, 3);
    }

    public Account getAccountBalanceByAccountId(int accountId) {
        Account account = null;
        String sql = "SELECT * FROM account WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        if (account == null) {
            throw new IllegalArgumentException("Account with ID " + accountId + " does not exist.");
        }
        return account;
    }
    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getInt("account_id"));
        account.setUserId(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }




}


