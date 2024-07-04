package com.techelevator.tenmo.model;

public class TransferStatus {

    private int transferStatusId;

    private int transferStatusDescription;

    public TransferStatus(int transferStatusId, int transferStatusDescription) {
        this.transferStatusId = transferStatusId;
        this.transferStatusDescription = transferStatusDescription;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getTransferStatusDescription() {
        return transferStatusDescription;
    }

    public void setTransferStatusDescription(int transferStatusDescription) {
        this.transferStatusDescription = transferStatusDescription;
    }
}
