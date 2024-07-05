package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface TransferDao {

    Transfer makeTransfer(TransferDto transferDto);

    public Transfer getTransferById(int transferId);

    //Created interface method which will be overriden in jdbctransferdao for listing the transfers
    List<Transfer> getTransfersByUserId(int userId);
}
