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
import com.bank.models.PaginatedResponse;
import com.bank.service.AccountRequestService;
import com.bank.util.PaginationUtil;

import exception.QueryException;

public class AccountRequestServiceImpl implements AccountRequestService {

    private static final Logger logger = Logger.getLogger(AccountRequestServiceImpl.class.getName());
    private final AccountRequestDAO accountReqDAO = DaoFactory.getAccountRequestDAO();

    @Override
    public PaginatedResponse<AccountRequest> getAllRequests(long fromTimestamp, long toTimestamp, int pageNumber, int pageSize) throws SQLException, QueryException {
        try {
        	pageNumber = PaginationUtil.validatePageNumber(pageNumber);
            pageSize = PaginationUtil.validatePageSize(pageSize);
            int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);
            List<AccountRequest> requestList;
            long totalRequests;
            requestList =  accountReqDAO.fetchAllRequests(fromTimestamp, toTimestamp, pageSize, offset);
            totalRequests = accountReqDAO.countRequests(fromTimestamp, toTimestamp);
            return new PaginatedResponse<>(requestList, pageNumber, pageSize, totalRequests);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch all account requests", e);
            throw e;
        }
    }

    @Override
    public PaginatedResponse<AccountRequest> getRequestsByAdminBranch(long adminId,long fromTimestamp, long toTimestamp, int pageNumber, int pageSize) throws SQLException, QueryException {
        try {
        	pageNumber = PaginationUtil.validatePageNumber(pageNumber);
            pageSize = PaginationUtil.validatePageSize(pageSize);
            int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);
            List<AccountRequest> requestList;
            long totalRequests;
            requestList =  accountReqDAO.fetchRequestsByAdminBranch(adminId,fromTimestamp, toTimestamp, pageSize, offset);
            totalRequests =accountReqDAO.countRequestsByBranch(adminId, fromTimestamp, toTimestamp); // Total for the branch
            return new PaginatedResponse<>(requestList, pageNumber, pageSize, totalRequests);
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
