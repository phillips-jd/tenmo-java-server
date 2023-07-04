package com.techelevator.dao;


import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.sql.DataSource;
import java.util.List;

public class JdbcUserDaoTests extends BaseDaoTests {

    private JdbcUserDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
    }

    @Test
    public void createNewUser() {
        boolean userCreated = sut.create("TEST_USER","test_password");
        Assert.assertTrue(userCreated);
        User user = sut.findByUsername("TEST_USER");
        Assert.assertEquals("TEST_USER", user.getUsername());
    }

    @Test
    public void findIdByUsername_returns_1001_for_username_bob() {
        String username = "bob";
        int actualId = sut.findIdByUsername(username);
        Assert.assertEquals(1001, actualId);
    }

    @Test
    public void findIdByUsername_returns_1002_for_username_user() {
        String username = "user";
        int actualId = sut.findIdByUsername(username);
        Assert.assertEquals(1002, actualId);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void findIdByUsername_returns_0_for_username_test() {
        String username = "test";
        sut.findIdByUsername(username);
    }

    @Test
    public void findAll_returns_usernames_bob_and_user() {
        List<UserDTO> usersList = sut.findAll();
        Assert.assertTrue(usersList.size() == 2);
        Assert.assertEquals(usersList.get(0).getUsername(), "bob");
        Assert.assertEquals(usersList.get(1).getUsername(), "user");
    }

    @Test
    public void findByUsername_returns_user_for_username_bob() {
        User actualUser = null;
        actualUser = sut.findByUsername("bob");
        Assert.assertNotNull(actualUser);
        Assert.assertEquals(1001, actualUser.getId());
    }

    @Test
    public void findByUsername_returns_user_for_username_user() {
        User actualUser = null;
        actualUser = sut.findByUsername("user");
        Assert.assertNotNull(actualUser);
        Assert.assertEquals(1002, actualUser.getId());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void findByUsername_throws_exception_for_username_test() {
        sut.findByUsername("test");
    }

    @Test
    public void getUserByAccountId_returns_user_for_account_id_2001() {
       int testId = 2001;
       User actualUser = null;
       actualUser = sut.getUserByAccountId(testId);
       Assert.assertNotNull(actualUser);
       Assert.assertEquals("bob", actualUser.getUsername());
    }

    @Test
    public void getUserByAccountId_returns_user_for_account_id_2002() {
        int testId = 2002;
        User actualUser = null;
        actualUser = sut.getUserByAccountId(testId);
        Assert.assertNotNull(actualUser);
        Assert.assertEquals("user", actualUser.getUsername());
    }

    @Test(expected = DaoException.class)
    public void getUserByAccountId_throws_exception_for_account_id_2003() {
        int testId = 2003;
        sut.getUserByAccountId(testId);
    }

}
