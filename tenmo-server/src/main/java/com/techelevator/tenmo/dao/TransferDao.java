package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import org.springframework.stereotype.Repository;


public interface TransferDao {

    Transfer makeTransfer(TransferDto transferDto);

    public Transfer getTransferById(int transferId);
}
