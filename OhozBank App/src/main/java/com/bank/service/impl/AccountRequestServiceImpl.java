package com.bank.service.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.AccountRequestDAO;
import com.bank.enums.RequestStatus;
import com.bank.factory.DaoFactory;
import com.bank.models.AccountRequest;
import com.bank.service.AccountRequestService;

import exception.QueryException;

public class AccountRequestServiceImpl implements AccountRequestService {

    private static final Logger logger = Logger.getLogger(AccountRequestServiceImpl.class.getName());
    private final AccountRequestDAO accountReqDAO = DaoFactory.getAccountRequestDAO();

    @Override
    public List<AccountRequest> getAllRequests() throws SQLException, QueryException {
        try {
            return accountReqDAO.fetchAllRequests();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch all account requests", e);
            throw e;
        }
    }

    @Override
    public List<AccountRequest> getRequestsByAdminBranch(long adminId) throws SQLException, QueryException {
        try {
            return accountReqDAO.fetchRequestsByAdminBranch(adminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch requests by admin branch: " + adminId, e);
            throw e;
        }
    }

    @Override
    public boolean createAccountRequest(long userId, long branchId) throws SQLException, QueryException {
        try {
            AccountRequest request = new AccountRequest();
            request.setUserId(userId);
            request.setBranchId(branchId);
            request.setStatus(RequestStatus.PENDING);
            request.setCreatedAt(System.currentTimeMillis());

            return accountReqDAO.save(request);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create account request for userId: " + userId, e);
            return false;
        }
    }

    @Override
    public boolean approveAccountRequest(long requestId, long adminId) throws SQLException, QueryException {
        try {
            return accountReqDAO.approveRequest(requestId, adminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error approving account request ID: " + requestId, e);
            return false;
        }
    }
    @Override
    public List<AccountRequest> getPendingRequestsForUser(long userId) throws SQLException, QueryException {
        try {
            return accountReqDAO.findPendingRequestsByUserId(userId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting pending requests for userId: " + userId, e);
            return Collections.emptyList();
        }
    }

}
