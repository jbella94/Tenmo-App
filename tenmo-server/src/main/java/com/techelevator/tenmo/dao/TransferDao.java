package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;

import java.util.List;


public interface TransferDao {

    Transfer makeTransfer(TransferDto transferDto);

    public Transfer getTransferById(int transferId);

    //Created interface method which will be overriden in jdbctransferdao for listing the transfers
    List<Transfer> getTransfersByUserId(int userId);

    Transfer createTransferRequest(TransferDto transferDto);

    List<Transfer> getPendingTransfersByUserId(int userId);
}
