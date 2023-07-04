package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.validation.constraints.AssertTrue;
import java.math.BigDecimal;

public class JdbcAccountDaoTests extends BaseDaoTests {

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void getUserAccount_should_return_account_with_id_2001_for_user_bob() {
        Account testAccount = sut.getUserAccount("bob");
        int testAccountId = testAccount.getAccountId();
        Assert.assertEquals(2001, testAccountId);
    }

    @Test
    public void getUserAccount_should_return_account_with_id_2002_for_user_user() {
        Account testAccount = sut.getUserAccount("user");
        int testAccountId = testAccount.getAccountId();
        Assert.assertEquals(2002, testAccountId);
    }

    @Test
    public void updateAccountBalance_should_return_true_with_username_bob_and_amount_50() {
        String username = "bob";
        BigDecimal balanceAmount = new BigDecimal("50");
        boolean actualValue = sut.updateAccountBalance(username, balanceAmount);
        Assert.assertTrue(actualValue);
    }

    @Test
    public void updateAccountBalance_should_return_true_with_username_user_and_amount_50() {
        String username = "user";
        BigDecimal balanceAmount = new BigDecimal("50");
        boolean actualValue = sut.updateAccountBalance(username, balanceAmount);
        Assert.assertTrue(actualValue);
    }

    @Test(expected = DaoException.class)
    public void updateAccountBalance_should_throw_exception_with_username_test_and_amount_50() {
        String username = "test";
        BigDecimal balanceAmount = new BigDecimal("50");
        boolean actualValue = sut.updateAccountBalance(username, balanceAmount);
    }


}
