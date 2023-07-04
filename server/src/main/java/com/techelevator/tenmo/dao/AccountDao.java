package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.security.Principal;

public interface AccountDao {
    Account getUserAccount(String username);
    boolean updateAccountBalance(String username, BigDecimal amount);
}
