package com.bank.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.AccountDAO;
import com.bank.dao.impl.AccountDAOImpl;
import com.bank.enums.UserStatus;
import com.bank.models.Account;
import com.bank.service.AccountService;

import exception.QueryException;

public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO = new AccountDAOImpl();
    
    
    private final Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());

    @Override
    public BigDecimal getAccountBalance(long accountId) throws QueryException, SQLException {
        Account account = accountDAO.getAccountById(accountId);
        return account != null ? account.getBalance() : null;
    }
    
    
    @Override
    public boolean approveAccountRequest(long requestId, long adminId)throws SQLException,QueryException {
        try {
        	System.out.println("in serv");
            return accountDAO.approveRequest(requestId, adminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error approving user request", e);
            return false;
        }
    }
    @Override
    public long getBranchIdByAccountId(long accountId) throws QueryException, SQLException {
        try {
            return accountDAO.getBranchIdByAccountId(accountId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting branch ID for account: " + accountId, e);
            return -1;
        }
    }

    @Override
    public boolean updateAccountStatus(Account payload, long modifiedBy) throws QueryException, SQLException {
        try {
            long accountId = payload.getAccountId();
            UserStatus newStatus = payload.getStatus();
            Account account = accountDAO.getAccountById(accountId);
            if (account == null) {
                logger.warning("Account not found for update: " + accountId);
                return false;
            }
            account.setStatus(newStatus);
            account.setModifiedBy(modifiedBy);
            account.setModifiedAt(System.currentTimeMillis());
       
            return accountDAO.updateAccount(account);
        }  catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating account status for account: " + payload.getAccountId(), e);
            return false;
        }
    }
 
        @Override
        public Account getAccountById(long accountId) throws SQLException {
            try {
                return accountDAO.getAccountById(accountId);
            } catch (Exception e) {
                logger.severe("Error fetching account by ID: " + e.getMessage());
                return null;
            }
        }

        @Override
        public List<Account> getAccountsByBranchId(long branchId) throws SQLException {
            try {
                return accountDAO.getAccountsByBranchId(branchId);
            } catch (Exception e) {
                logger.severe("Error fetching accounts by branch: " + e.getMessage());
                return null;
            }
        }

        @Override
        public List<Account> getAllAccounts() throws SQLException {
            try {
                return accountDAO.getAllAccounts();
            } catch (Exception e) {
                logger.severe("Error fetching all accounts: " + e.getMessage());
                return null;
            }
        }
      
    }




