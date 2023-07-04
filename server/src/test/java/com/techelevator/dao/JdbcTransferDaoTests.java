package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

public class JdbcTransferDaoTests extends BaseDaoTests {

    private JdbcTransferDao sut;
    private JdbcAccountDao sutAccount;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
        sutAccount = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void initiateTransfer_returns_transferdto_for_sender_bob_recipient_user_transferamount_100() {
        String sender = "bob";
        String recipient = "user";
        BigDecimal transferAmount = new BigDecimal("100");
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.initiateTransfer(sender, recipient, transferAmount, "pay");
        Assert.assertNotNull(actualTransferDTO);
        Assert.assertEquals("bob", actualTransferDTO.getSender());
        Assert.assertEquals("user", actualTransferDTO.getRecipient());
        Assert.assertTrue(transferAmount.compareTo(actualTransferDTO.getAmount()) == 0);
        Assert.assertEquals("Approved", actualTransferDTO.getStatus());
        Assert.assertEquals("Payment", actualTransferDTO.getTransferType());
    }

    @Test
    public void initiateTransfer_returns_transferdto_for_sender_user_recipient_bob_transferamount_100() {
        String sender = "user";
        String recipient = "bob";
        BigDecimal transferAmount = new BigDecimal("100");
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.initiateTransfer(sender, recipient, transferAmount, "Request");
        Assert.assertNotNull(actualTransferDTO);
        Assert.assertEquals("user", actualTransferDTO.getSender());
        Assert.assertEquals("bob", actualTransferDTO.getRecipient());
        Assert.assertTrue(transferAmount.compareTo(actualTransferDTO.getAmount()) == 0);
        Assert.assertEquals("Pending", actualTransferDTO.getStatus());
        Assert.assertEquals("Request", actualTransferDTO.getTransferType());
    }

    @Test(expected = DaoException.class)
    public void initiateTransfer_throws_exception_for_sender_bob_recipient_bob_transferamount_100() {
        String sender = "bob";
        String recipient = "bob";
        BigDecimal transferAmount = new BigDecimal("100");
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.initiateTransfer(sender, recipient, transferAmount, "Request");
    }

    @Test(expected = DaoException.class)
    public void initiateTransfer_throws_exception_for_sender_bob_recipient_user_transferamount_0() {
        String sender = "bob";
        String recipient = "user";
        BigDecimal transferAmount = new BigDecimal("0");
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.initiateTransfer(sender, recipient, transferAmount, "Payment");
    }

    @Test(expected = DaoException.class)
    public void initiateTransfer_throws_exception_for_sender_bob_recipient_user_transferamount_2000() {
        String sender = "bob";
        String recipient = "user";
        BigDecimal transferAmount = new BigDecimal("2000");
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.initiateTransfer(sender, recipient, transferAmount, "pay");
    }

    @Test(expected = DaoException.class)
    public void initiateTransfer_throws_exception_for_sender_test_recipient_user_transferamount_100() {
        String sender = "test";
        String recipient = "user";
        BigDecimal transferAmount = new BigDecimal("100");
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.initiateTransfer(sender, recipient, transferAmount, "Request");
    }

    @Test
    public void updateBalanceIfTransferIsApproved_returns_transferdto_for_action_approve_recipient_user_transferid_3001() {
        String action = "approve";
        String recipient = "user";
        int transferId = 3001;
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.updateBalanceIfTransferIsApproved(action, recipient, transferId);
        Account bobAccount = sutAccount.getUserAccount(actualTransferDTO.getSender());
        Account userAccount = sutAccount.getUserAccount(actualTransferDTO.getRecipient());
        Assert.assertNotNull(actualTransferDTO);
        Assert.assertEquals("bob", actualTransferDTO.getSender());
        Assert.assertEquals("user", actualTransferDTO.getRecipient());
        Assert.assertTrue(actualTransferDTO.getAmount().compareTo(new BigDecimal("100")) == 0);
        Assert.assertEquals("Approved", actualTransferDTO.getStatus());
        Assert.assertTrue(bobAccount.getBalance().compareTo(new BigDecimal("1100")) == 0);
        Assert.assertTrue(userAccount.getBalance().compareTo(new BigDecimal("900")) == 0);
    }

    @Test
    public void updateBalanceIfTransferIsApproved_returns_transferdto_for_action_approve_recipient_bob_transferid_3003() {
        String action = "approve";
        String recipient = "bob";
        int transferId = 3003;
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.updateBalanceIfTransferIsApproved(action, recipient, transferId);
        Account userAccount = sutAccount.getUserAccount(actualTransferDTO.getSender());
        Account bobAccount = sutAccount.getUserAccount(actualTransferDTO.getRecipient());
        Assert.assertNotNull(actualTransferDTO);
        Assert.assertEquals("user", actualTransferDTO.getSender());
        Assert.assertEquals("bob", actualTransferDTO.getRecipient());
        Assert.assertTrue(actualTransferDTO.getAmount().compareTo(new BigDecimal("100")) == 0);
        Assert.assertEquals("Approved", actualTransferDTO.getStatus());
        Assert.assertTrue(userAccount.getBalance().compareTo(new BigDecimal("1100")) == 0);
        Assert.assertTrue(bobAccount.getBalance().compareTo(new BigDecimal("900")) == 0);
    }

    @Test
    public void updateBalanceIfTransferIsApproved_returns_transferdto_for_action_reject_recipient_user_transferid_3007() {
        String action = "reject";
        String recipient = "user";
        int transferId = 3007;
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.updateBalanceIfTransferIsApproved(action, recipient, transferId);
        Account bobAccount = sutAccount.getUserAccount(actualTransferDTO.getSender());
        Account userAccount = sutAccount.getUserAccount(actualTransferDTO.getRecipient());
        Assert.assertNotNull(actualTransferDTO);
        Assert.assertEquals("bob", actualTransferDTO.getSender());
        Assert.assertEquals("user", actualTransferDTO.getRecipient());
        Assert.assertTrue(actualTransferDTO.getAmount().compareTo(new BigDecimal("100")) == 0);
        Assert.assertEquals("Rejected", actualTransferDTO.getStatus());
        Assert.assertTrue(bobAccount.getBalance().compareTo(new BigDecimal("1000")) == 0);
        Assert.assertTrue(userAccount.getBalance().compareTo(new BigDecimal("1000")) == 0);
    }


    @Test(expected = ResponseStatusException.class)
    public void updateBalanceIfTransferIsApproved_throws_exception_for_action_approve_recipient_bob_transferid_3008() {
        String action = "approve";
        String recipient = "bob";
        int transferId = 3008;
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.updateBalanceIfTransferIsApproved(action, recipient, transferId);
    }

    @Test(expected = DaoException.class)
    public void updateBalanceIfTransferIsApproved_throws_exception_for_action_approve_recipient_bob_transferid_3005() {
        String action = "approve";
        String recipient = "bob";
        int transferId = 3005;
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.updateBalanceIfTransferIsApproved(action, recipient, transferId);
    }

    @Test(expected = DaoException.class)
    public void updateBalanceIfTransferIsApproved_throws_exception_for_action_approve_recipient_bob_transferid_3006() {
        String action = "approve";
        String recipient = "bob";
        int transferId = 3006;
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.updateBalanceIfTransferIsApproved(action, recipient, transferId);
    }

    @Test(expected = DaoException.class)
    public void updateBalanceIfTransferIsApproved_throws_exception_for_action_test_recipient_bob_transferid_3006() {
        String action = "test";
        String recipient = "bob";
        int transferId = 3006;
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.updateBalanceIfTransferIsApproved(action, recipient, transferId);
    }

    @Test
    public void viewListOfTransfers_returns_transferdto_list_size_7_for_username_bob() {
        String username = "bob";
        List<TransferDTO> actualList = null;
        actualList = sut.viewListOfTransfers(username);
        Assert.assertNotNull(actualList);
        Assert.assertTrue(actualList.size() == 7);
        Assert.assertTrue(actualList.get(0).getSender().equals("bob"));
        Assert.assertTrue(actualList.get(0).getRecipient().equals("user"));
        Assert.assertTrue(actualList.get(0).getStatus().equals("Pending"));
        Assert.assertTrue(actualList.get(0).getTransferType().equalsIgnoreCase("Request"));
        Assert.assertTrue(actualList.get(0).getAmount().compareTo(new BigDecimal("100")) == 0);
        Assert.assertTrue(actualList.get(1).getSender().equals("bob"));
        Assert.assertTrue(actualList.get(2).getSender().equals("user"));
        Assert.assertTrue(actualList.get(3).getSender().equals("user"));
    }

    @Test
    public void viewListOfTransfers_returns_transferdto_list_size_0_for_username_test() {
        String username = "test";
        List<TransferDTO> actualList = null;
        actualList = sut.viewListOfTransfers(username);
        Assert.assertNotNull(actualList);
        Assert.assertTrue(actualList.size() == 0);
    }

    @Test
    public void getTransferById_returns_transfer_for_id_3001() {
        int id = 3001;
        Transfer actualTransfer = null;
        actualTransfer = sut.getTransferById(id);
        Assert.assertNotNull(actualTransfer);
        Assert.assertTrue(actualTransfer.getAccountIdForSender() == 2001);
        Assert.assertTrue(actualTransfer.getAccountIdForReceiver() == 2002);
        Assert.assertTrue(actualTransfer.getBalanceBeingTransferred().compareTo(new BigDecimal("100")) == 0);
        Assert.assertTrue(actualTransfer.getStatus().equals("Pending"));
        Assert.assertTrue(actualTransfer.getTransferType().equalsIgnoreCase("Request"));
    }

    @Test(expected = DaoException.class)
    public void getTransferById_throws_exception_for_id_3008() {
        int id = 3008;
        sut.getTransferById(id);;
    }

    @Test
    public void getTransferDtoById_returns_transferdto_for_id_3001() {
        int id = 3001;
        TransferDTO actualTransferDTO = null;
        actualTransferDTO = sut.getTransferDtoById(id);
        Assert.assertNotNull(actualTransferDTO);
        Assert.assertTrue(actualTransferDTO.getSender().equals("bob"));
        Assert.assertTrue(actualTransferDTO.getRecipient().equals("user"));
        Assert.assertTrue(actualTransferDTO.getAmount().compareTo(new BigDecimal("100")) == 0);
        Assert.assertTrue(actualTransferDTO.getStatus().equals("Pending"));
    }

    @Test(expected = ResponseStatusException.class)
    public void getTransferDtoById_throws_exception_for_id_3008() {
        int id = 3008;
        sut.getTransferDtoById(id);
    }

}
