package com.techelevator.tenmo.model;

import javax.validation.constraints.NotBlank;
import java.io.StringReader;
import java.math.BigDecimal;

public class TransferDTO {

    private int id;
    @NotBlank
    private String sender;
    @NotBlank
    private String recipient;
    @NotBlank
    private BigDecimal amount;
    private String status;
    private String transferType;

    public TransferDTO(int id, String sender, String recipient, BigDecimal amount, String status, String transferType) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.status = status;
        this.transferType = transferType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}
