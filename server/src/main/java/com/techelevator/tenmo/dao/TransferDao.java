package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

        TransferDTO initiateTransfer(String sender, String recipient, BigDecimal transferAmount, String action);
        List<TransferDTO> viewListOfTransfers(String username);
        TransferDTO updateBalanceIfTransferIsApproved(String action, String recipient, int transferId);
        Transfer getTransferById(int id);
        TransferDTO getTransferDtoById(int id);


}
