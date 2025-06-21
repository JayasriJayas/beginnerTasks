package com.bank.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.AccountDAO;
import com.bank.factory.DaoFactory;
import com.bank.models.Account;
import com.bank.service.AccountService;

import exception.QueryException;

public class AccountServiceImpl implements AccountService {

    private static final Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());
    private final AccountDAO accountDAO = DaoFactory.getAccountDAO();

    @Override
    public BigDecimal getAccountBalance(long accountId) throws QueryException, SQLException {
        Account account = accountDAO.getAccountById(accountId);
        return account != null ? account.getBalance() : null;
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
        if (payload == null || payload.getAccountId() <= 0 || payload.getStatus() == null) {
            logger.warning("Invalid payload for account status update");
            return false;
        }

        try {
            Account account = accountDAO.getAccountById(payload.getAccountId());
            if (account == null) {
                logger.warning("Account not found for update: " + payload.getAccountId());
                return false;
            }

            account.setStatus(payload.getStatus());
            account.setModifiedBy(modifiedBy);
            account.setModifiedAt(System.currentTimeMillis());

            return accountDAO.updateAccount(account);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating account status for account: " + payload.getAccountId(), e);
            return false;
        }
    }

    @Override
    public Account getAccountById(long accountId) throws SQLException {
        try {
            return accountDAO.getAccountById(accountId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching account by ID: " + accountId, e);
            return null;
        }
    }

    @Override
    public List<Account> getAccountsByBranchId(long branchId) throws SQLException {
        try {
            return accountDAO.getAccountsByBranchId(branchId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching accounts by branch: " + branchId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Account> getAllAccounts() throws SQLException {
        try {
            return accountDAO.getAllAccounts();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching all accounts", e);
            return Collections.emptyList();
        }
    }
    @Override
    public List<Account> getAccountsByUserId(long userId) throws SQLException {
        try {
            return accountDAO.getAccountsByUserId(userId);
        } catch (Exception e) {
            logger.severe("Error fetching accounts by userId: " + e.getMessage());
            return null;
        }
    }

}
