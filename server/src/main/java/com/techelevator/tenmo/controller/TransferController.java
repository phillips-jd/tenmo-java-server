package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/transfers")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao dao;

    public TransferController(TransferDao dao) {
        this.dao = dao;
    }

    @GetMapping
    public List<TransferDTO> getListOfTransfers(Principal principal) {
        return dao.viewListOfTransfers(principal.getName());
    }

    @GetMapping(path = "/{transferId}")
    public TransferDTO getTransfer(@PathVariable int transferId) {
        return dao.getTransferDtoById(transferId);
    }

    @PostMapping(path = "/{recipient}/pay")
    public TransferDTO initiateTransfer(@PathVariable String recipient, @RequestParam BigDecimal transferAmount, Principal principal) {
        return dao.initiateTransfer(principal.getName(), recipient, transferAmount, "pay");
    }

    @PostMapping(path = "/{recipient}/request")
    public TransferDTO initiateRequest(@PathVariable String recipient, @RequestParam BigDecimal transferAmount, Principal principal) {
        return dao.initiateTransfer(principal.getName(), recipient, transferAmount, "request");
    }

    @PutMapping(path = "/{transferId}/{action}")
    public TransferDTO updateTransferStatus(@PathVariable int transferId, @PathVariable String action, Principal principal){
        return dao.updateBalanceIfTransferIsApproved(action, principal.getName(), transferId);
    }

}
