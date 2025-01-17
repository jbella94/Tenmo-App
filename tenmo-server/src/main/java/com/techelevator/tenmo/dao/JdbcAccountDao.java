package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
@Repository
public class JdbcAccountDao implements AccountDao {

    private final List<Account> accounts = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getAccountBalanceByAccountId(int userId) {
        Account account = null;
        String sql = "SELECT * FROM account WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public void save(Account account) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, account.getBalance(), account.getAccountId());
        if (rowsAffected == 0) {
            throw new RuntimeException("Failed to update account with ID " + account.getAccountId());
        }
    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getInt("account_id"));
        account.setUserId(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }

    @Override
    public Account getAccountBalanceByAccountIds(int accountId) {
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




}
