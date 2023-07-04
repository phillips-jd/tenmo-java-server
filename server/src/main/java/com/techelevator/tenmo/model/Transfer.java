package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class Transfer {

    @NotBlank(message = "Cannot be blank")
    @JsonProperty("transfer_id")
    private int transferId;
    @NotBlank(message = "Cannot be blank")
    @JsonProperty("account_id_from")
    private int accountIdForSender;
    @NotBlank(message = "Cannot be blank")
    @JsonProperty("account_id_to")
    private int accountIdForReceiver;
    @NotBlank(message = "Cannot be blank")
    @Min(value = 1)
    @JsonProperty("balance_transfer")
    private BigDecimal balanceBeingTransferred;
    @NotBlank(message = "Cannot be blank")
    private String status;
    @NotBlank
    private String transferType;

    public Transfer() {

    }

    public Transfer(int transferId, int accountIdForSender, int accountIdForReceiver,
                    BigDecimal balanceBeingTransferred, String status, String transferType) {
        this.transferId = transferId;
        this.accountIdForSender = accountIdForSender;
        this.accountIdForReceiver = accountIdForReceiver;
        this.balanceBeingTransferred = balanceBeingTransferred;
        this.status = status;
        this.transferType = transferType;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getAccountIdForSender() {
        return accountIdForSender;
    }

    public void setAccountIdForSender(int accountIdForSender) {
        this.accountIdForSender = accountIdForSender;
    }

    public int getAccountIdForReceiver() {
        return accountIdForReceiver;
    }

    public void setAccountIdForReceiver(int accountIdForReceiver) {
        this.accountIdForReceiver = accountIdForReceiver;
    }

    public BigDecimal getBalanceBeingTransferred() {
        return balanceBeingTransferred;
    }

    public void setBalanceBeingTransferred(BigDecimal balanceBeingTransferred) {
        this.balanceBeingTransferred = balanceBeingTransferred;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transfer_id = " + transferId +
                ", account_id_from = " + accountIdForSender +
                ", account_id_to = " + accountIdForReceiver +
                ", balance_transfer = " + balanceBeingTransferred +
                ", status = " + status +
                "}";
    }

}
