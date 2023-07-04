package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;


@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getUserAccount(String username) {
        Account account = null;
        String sql = "SELECT * FROM account " +
                "JOIN tenmo_user AS tu ON tu.user_id = account.user_id " +
                "WHERE tu.username = ?;";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
            account = null;
            if(rowSet.next()) {
                account = mapRowToAccount(rowSet);
            } else if(account == null) {
                throw new DaoException("Account does not exist");
            }
        } catch(CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect to data source");
        } catch(BadSqlGrammarException e) {
            throw new DaoException(e.getMessage());
        } catch(DataIntegrityViolationException e) {
            throw new DaoException("Invalid operation - Data integrity error");
        }
        return account;
    }

    @Override
    public boolean updateAccountBalance(String username, BigDecimal amount) {
        Account account = getUserAccount(username);
        String sql = "UPDATE account SET balance = ? " +
                "FROM tenmo_user AS tu " +
                "WHERE tu.user_id = account.user_id AND tu.username = ?;";
        try {
            int numrows = jdbcTemplate.update(sql, account.getBalance().add(amount), username);
            if(numrows != 0) {
                return true;
            } else {
                return false;
            }
        } catch(CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect to data source");
        } catch(BadSqlGrammarException e) {
            throw new DaoException(e.getMessage());
        } catch(DataIntegrityViolationException e) {
            throw new DaoException("Invalid operation - Data integrity error");
        }
    }

    private Account mapRowToAccount(SqlRowSet rowSet){
        return new Account(rowSet.getInt("account_id"), rowSet.getInt("user_id"), rowSet.getBigDecimal("balance"));
    }
}
