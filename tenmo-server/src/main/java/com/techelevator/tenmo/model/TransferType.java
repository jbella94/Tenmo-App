package com.techelevator.tenmo.model;

public class TransferType {

    private int transferTypeId;

    private int transferTypeDescription;

    public TransferType(int transferTypeId, int transferTypeDescription) {
        this.transferTypeId = transferTypeId;
        this.transferTypeDescription = transferTypeDescription;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferTypeDescription() {
        return transferTypeDescription;
    }

    public void setTransferTypeDescription(int transferTypeDescription) {
        this.transferTypeDescription = transferTypeDescription;
    }
}
