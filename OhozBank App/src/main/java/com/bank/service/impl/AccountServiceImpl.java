package com.bank.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.AccountDAO;
import com.bank.dao.impl.AccountDAOImpl;
import com.bank.enums.RequestStatus;
import com.bank.enums.UserStatus;
import com.bank.models.Account;
import com.bank.models.AccountRequest;
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
    public boolean createAccountRequest(long userId, long branchId) throws SQLException,QueryException {
        AccountRequest request = new AccountRequest();
        request.setUserId(userId);
        request.setBranchId(branchId);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(System.currentTimeMillis());

        return accountDAO.save(request);
    }
    
    @Override
    public boolean approveAccountRequest(long requestId, long adminId)throws SQLException,QueryException {
        try {
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
           
            if (payload.getAccountId()!= null|| payload.getStatus()!= null) {
                logger.warning("Missing accountId or status in payload for updateAccountStatus.");
                return false;
            }

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


}

