package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private int transferId;

    private int transferTypeId;

    private int transferStatusId;

    private int accountFrom;

    private int accountTo;

    private BigDecimal amount;

    public int getTransferId() {
        return transferId;
    }



    public int getTransferTypeId() {
        return transferTypeId;
    }



    public int getTransferStatusId() {
        return transferStatusId;
    }



    public int getAccountFrom() {
        return accountFrom;
    }



    public int getAccountTo() {
        return accountTo;
    }



    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "\n--------------------------------------------" +
                "\n Transfer Details" +
                "\n--------------------------------------------" +
                "\n Transfer Id: " + transferId +
                "\n Transfer Type Id: " + transferTypeId +
                "\n Transfer Status Id: " + transferStatusId +
                "\n Account From: " + accountFrom +
                "\n Account To: " + accountTo +
                "\n Amount: " + amount;

    }

}
