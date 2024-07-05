package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.security.Principal;
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
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()) {
            return mapRowToTransfer(results);
        }
        return null;
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

        String sqlInsertTransfer = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, (SELECT account_id FROM account WHERE user_id = ?),  (SELECT account_id FROM account WHERE user_id = ?), ?) RETURNING transfer_id";

        transferDto.setTransferTypeId(2);
        transferDto.setTransferStatusId(2);

        try {
            // Insert the transfer and get the new transfer ID
            int newTransferId = jdbcTemplate.queryForObject(
                    sqlInsertTransfer, Integer.class,
                    transferDto.getTransferTypeId(), transferDto.getTransferStatusId(), transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount()

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


    }


