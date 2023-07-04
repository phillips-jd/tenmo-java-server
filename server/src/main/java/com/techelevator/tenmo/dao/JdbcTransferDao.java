package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = new JdbcAccountDao(jdbcTemplate);
    }

    @Override
    public TransferDTO initiateTransfer(String sender, String recipient, BigDecimal transferAmount, String action) {
        int newTransferId = 0;
        String sqlStatement = "INSERT INTO transfer (account_id_from, account_id_to, balance_transfer, status, transfer_type) " +
                "VALUES ((SELECT account_id FROM account JOIN tenmo_user AS tu on tu.user_id = account.user_id WHERE tu.username = ?), " +
                "(SELECT account_id FROM account JOIN tenmo_user AS tu on tu.user_id = account.user_id WHERE tu.username = ?), ?, ?, ?) " +
                "RETURNING transfer_id;";
        if(sender.equals(recipient) || transferAmount.compareTo(new BigDecimal("0")) <= 0) {
            throw new DaoException("Invalid transfer, please review the transfer details.");
        }
        try {
            if(action.equalsIgnoreCase("pay")) {
                Account senderAccount = accountDao.getUserAccount(sender);
                if(senderAccount.getBalance().compareTo(transferAmount) < 0){
                    throw new DaoException("Insufficient funds in your account.");
                }
                newTransferId = jdbcTemplate.queryForObject(sqlStatement, Integer.class, sender,
                        recipient, transferAmount, "Approved", "Payment");
                if(!accountDao.updateAccountBalance(recipient, transferAmount) || !accountDao.updateAccountBalance(sender, transferAmount.negate())) {
                    throw new DaoException("There was an error processing the transfer.");
                }
            }
            else if(action.equalsIgnoreCase("request")) {
                newTransferId = jdbcTemplate.queryForObject(sqlStatement, Integer.class, sender,
                        recipient, transferAmount, "Pending", "Request");
            }

            if(newTransferId != 0) {
                return getTransferDtoById(newTransferId);
            } else {
                throw new DaoException("Error initializing the transfer, please try again.");
            }
        } catch(CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect to data source");
        } catch(BadSqlGrammarException e) {
            throw new DaoException(e.getMessage());
        } catch(DataIntegrityViolationException e) {
            throw new DaoException("Invalid operation - Data integrity error");
        }
    }

    @Override
    public TransferDTO updateBalanceIfTransferIsApproved(String action, String recipient, int transferId) {
        TransferDTO dto = null;
        String transferSql = "UPDATE transfer SET status = ? WHERE transfer_id = ?;";
        try {
            dto = getTransferDtoById(transferId);
            if (dto == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            if (dto.getTransferType().equalsIgnoreCase("Payment")){
                throw new DaoException("Invalid action.");
            }
            if (!dto.getStatus().equalsIgnoreCase("Pending")) {
                throw new DaoException("This transfer has already been " + dto.getStatus() + ".");
            }
            if(dto.getRecipient().equalsIgnoreCase(recipient)) {
                if (action.equalsIgnoreCase("approve")) {
                    Account payingAccount = accountDao.getUserAccount(dto.getRecipient());
                    if (payingAccount.getBalance().compareTo(dto.getAmount()) < 1) {
                        throw new DaoException("Insufficient funds in your account.");
                    }
                    if (accountDao.updateAccountBalance(dto.getSender(), dto.getAmount()) && accountDao.updateAccountBalance(dto.getRecipient(), dto.getAmount().negate())) {
                        jdbcTemplate.update(transferSql, "Approved", transferId);
                        return getTransferDtoById(transferId);
                    }
                    else {
                        throw new DaoException("Unable to process this transfer.");
                    }
                } else if (action.equalsIgnoreCase("reject")) {
                    jdbcTemplate.update(transferSql, "Rejected", transferId);
                    return getTransferDtoById(transferId);
                } else {
                    throw new DaoException("Invalid action specified.");
                }
            }
            else {
                throw new DaoException("This can only be approved or rejected by the recipient.");
            }
        } catch(CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect to data source");
        } catch(BadSqlGrammarException e) {
            throw new DaoException("Bad SQL grammar - Review the SQL statement syntax");
        } catch(DataIntegrityViolationException e) {
            throw new DaoException("Invalid operation - Data integrity error");
        }
    }

    @Override
    public List<TransferDTO> viewListOfTransfers(String username) {
        List<TransferDTO> listOfTransfers = new ArrayList<>();
        String sql = "SELECT t.transfer_id AS id, tu.username AS sender, u.username AS recipient, t.balance_transfer AS amount, t.status, t.transfer_type AS type FROM transfer AS t " +
                "JOIN account AS acc ON acc.account_id = t.account_id_to " +
                "JOIN account AS a ON a.account_id = t.account_id_from " +
                "JOIN tenmo_user AS tu ON tu.user_id = a.user_id " +
                "JOIN tenmo_user AS u ON u.user_id = acc.user_id " +
                "WHERE tu.username = ? OR u.username = ?";

        try {
            SqlRowSet queryResult = jdbcTemplate.queryForRowSet(sql, username, username);
            while(queryResult.next()) {
                listOfTransfers.add(mapRowToTransferDTO(queryResult));
            }
        } catch(CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect to data source");
        } catch(BadSqlGrammarException e) {
            throw new DaoException("Bad SQL grammar - Review the SQL statement syntax");
        } catch(DataIntegrityViolationException e) {
            throw new DaoException(e.getMessage());
        }
        return listOfTransfers;
    }

    @Override
    public Transfer getTransferById(int id) {
        Transfer transfer = null;
        String sqlStatement = "SELECT * FROM transfer WHERE transfer_id = ?;";
        try {
            SqlRowSet queryResult = jdbcTemplate.queryForRowSet(sqlStatement, id);
            if(queryResult.next()) {
                transfer = mapRowToTransferObject(queryResult);
            } else {
                throw new DaoException("No transfer with that ID was located");
            }
        } catch(CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect to data source");
        } catch(BadSqlGrammarException e) {
            throw new DaoException("Bad SQL grammar - Review the SQL statement syntax");
        } catch(DataIntegrityViolationException e) {
            throw new DaoException("Invalid operation - Data integrity error");
        }
        return transfer;
    }

    @Override
    public TransferDTO getTransferDtoById(int id) {
        String sql = "SELECT t.transfer_id AS id, tu.username AS sender, u.username AS recipient, t.balance_transfer AS amount, t.status, t.transfer_type AS type FROM transfer AS t " +
                "JOIN account AS acc ON acc.account_id = t.account_id_to " +
                "JOIN account AS a ON a.account_id = t.account_id_from " +
                "JOIN tenmo_user AS tu ON tu.user_id = a.user_id " +
                "JOIN tenmo_user AS u ON u.user_id = acc.user_id " +
                "WHERE t.transfer_id = ?;";
        TransferDTO dto = null;
        try {
            SqlRowSet queryResult = jdbcTemplate.queryForRowSet(sql, id);
            if(queryResult.next()) {
                dto = mapRowToTransferDTO(queryResult);
            }
            else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the specified transfer id.");
            }
        }catch(CannotGetJdbcConnectionException e) {
            throw new DaoException("Could not connect to data source");
        } catch(BadSqlGrammarException e) {
            throw new DaoException("Bad SQL grammar - Review the SQL statement syntax");
        } catch(DataIntegrityViolationException e) {
            throw new DaoException("Invalid operation - Data integrity error");
        }
        return dto;
    }

    public Transfer mapRowToTransferObject(SqlRowSet queryResult) {
        Transfer newTransfer = new Transfer();
        newTransfer.setTransferId(queryResult.getInt("transfer_id"));
        newTransfer.setAccountIdForSender(queryResult.getInt("account_id_from"));
        newTransfer.setAccountIdForReceiver(queryResult.getInt("account_id_to"));
        newTransfer.setBalanceBeingTransferred(queryResult.getBigDecimal("balance_transfer"));
        newTransfer.setStatus(queryResult.getString("status"));
        newTransfer.setTransferType(queryResult.getString("transfer_type"));
        return newTransfer;
    }

    public TransferDTO mapRowToTransferDTO(SqlRowSet rowSet) {
        return new TransferDTO(rowSet.getInt("id"),
                rowSet.getString("sender"),
                rowSet.getString("recipient"),
                rowSet.getBigDecimal("amount"),
                rowSet.getString("status"),
                rowSet.getString("type"));
    }


}
