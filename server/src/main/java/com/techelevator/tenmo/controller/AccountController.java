package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping(path = "/accounts")
@PreAuthorize("isAuthenticated()")
public class AccountController {
    private AccountDao dao;

    public AccountController(AccountDao dao) {
        this.dao = dao;
    }

    @GetMapping
    public Account getUserAccount(Principal principal){
        return dao.getUserAccount(principal.getName());
    }

    @PutMapping
    public boolean updateAccountBalance(@RequestParam BigDecimal amount, Principal principal){
        return dao.updateAccountBalance(principal.getName(), amount);
    }
}
