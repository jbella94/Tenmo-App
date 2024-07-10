package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDao {

    Account getAccountBalanceByAccountId(int userId);


    void save(Account Account);

    Account getAccountBalanceByAccountIds(int accountId);
}
