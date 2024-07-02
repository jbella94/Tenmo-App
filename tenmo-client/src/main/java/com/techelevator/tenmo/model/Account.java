package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {

    private int accountId;

    private int userId;

    private BigDecimal balance;

    public int getAccountId() {
        return accountId;
    }



    public int getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }



    @Override
    public String toString() {
        return "\n--------------------------------------------" +
                "\n Account Details" +
                "\n--------------------------------------------" +
                "\n Account Id: " + accountId +
                "\n User Id:'" + userId + '\'' +
                "\n Balance: " + balance;
    }
}
