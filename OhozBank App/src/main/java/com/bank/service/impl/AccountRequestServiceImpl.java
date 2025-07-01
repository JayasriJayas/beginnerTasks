package com.bank.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.AccountRequestDAO;
import com.bank.dao.BranchDAO;
import com.bank.enums.RequestStatus;
import com.bank.enums.UserRole;
import com.bank.factory.DaoFactory;
import com.bank.models.AccountRequest;
import com.bank.models.PaginatedResponse;
import com.bank.service.AccountRequestService;
import com.bank.util.PaginationUtil;

import exception.QueryException;

public class AccountRequestServiceImpl implements AccountRequestService {

    private static final Logger logger = Logger.getLogger(AccountRequestServiceImpl.class.getName());
    private final AccountRequestDAO accountReqDAO = DaoFactory.getAccountRequestDAO();
    private final BranchDAO branchDAO = DaoFactory.getBranchDAO();

    @Override
    public PaginatedResponse<AccountRequest> getAllRequests(long fromTimestamp, long toTimestamp, int pageNumber, int pageSize,RequestStatus status) throws SQLException, QueryException {
        try {
        	pageNumber = PaginationUtil.validatePageNumber(pageNumber);
            pageSize = PaginationUtil.validatePageSize(pageSize);
            int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);
            List<AccountRequest> requestList;
            long totalRequests;
            requestList =  accountReqDAO.fetchAllRequests(fromTimestamp, toTimestamp, pageSize, offset,status);
            totalRequests = accountReqDAO.countRequests(fromTimestamp, toTimestamp,status);
            return new PaginatedResponse<>(requestList, pageNumber, pageSize, totalRequests);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch all account requests", e);
            throw e;
        }
    }

    @Override
    public PaginatedResponse<AccountRequest> getRequestsByAdminBranch(long adminId,long fromTimestamp, long toTimestamp, int pageNumber, int pageSize,RequestStatus status) throws SQLException, QueryException {
        try {
        	pageNumber = PaginationUtil.validatePageNumber(pageNumber);
            pageSize = PaginationUtil.validatePageSize(pageSize);
            int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);
            List<AccountRequest> requestList;
            long totalRequests;
            requestList =  accountReqDAO.fetchRequestsByAdminBranch(adminId,fromTimestamp, toTimestamp, pageSize, offset,status);
            totalRequests =accountReqDAO.countRequestsByBranch(adminId, fromTimestamp, toTimestamp,status);
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
        	System.out.println("i am here");
            return accountReqDAO.approveRequest(requestId, adminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error approving account request ID: " + requestId, e);
            return false;
        }
    }
    @Override
    public List<AccountRequest> getPendingRequestsForUser(long userId,RequestStatus status) throws SQLException, QueryException {
        try {
            return accountReqDAO.findPendingRequestsByUserId(userId,status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting pending requests for userId: " + userId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Long> getRequestStatusCounts(String role, long adminId) throws SQLException, QueryException{
        try {
            if ("ADMIN".equalsIgnoreCase(role)) {
                long branchId = branchDAO.getBranchIdByAdminId(adminId);
                return accountReqDAO.getRequestStatusCounts(branchId);
            } else if ("SUPERADMIN".equalsIgnoreCase(role)) {
                return accountReqDAO.getRequestStatusCounts(null); 
            } else {
                return Collections.emptyMap(); 
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching request status counts", e);
            return Collections.emptyMap();
        }
    }
    @Override
    public AccountRequest getRequestDetailsById(long requestId) throws SQLException, QueryException {
        try {
            return accountReqDAO.getAccountRequest(requestId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch account request details", e);
            throw e;
        }
    }
    @Override
    public List<Long> rejectMultipleRequests(List<Long> requestIds, long adminId, String reason, String role, Long branchId) throws SQLException, QueryException {
        List<Long> failed = new ArrayList<>();
        for (Long id : requestIds) {
            try {
                AccountRequest request = accountReqDAO.getAccountRequest(id);
                if (request == null || request.getStatus() != RequestStatus.PENDING) {
                    failed.add(id);
                    continue;
                }
                if (UserRole.ADMIN.name().equalsIgnoreCase(role)) {
                    if (branchId == null || !branchId.equals(request.getBranchId())) {
                        failed.add(id);
                        continue;
                    }
                }


                boolean success = accountReqDAO.rejectRequest(id, adminId, reason);
                if (!success) failed.add(id);

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Bulk reject error for ID: " + id, e);
                failed.add(id);
            }
        }
        return failed;
    }
    @Override
    public boolean rejectUserRequest(long requestId, long adminId, String reason, String role, Long branchId) {
        try {
            AccountRequest request = accountReqDAO.getAccountRequest(requestId);
            if (request == null || request.getStatus() != RequestStatus.PENDING) return false;

            if (UserRole.ADMIN.name().equalsIgnoreCase(role)) {
                if (branchId == null || !branchId.equals(request.getBranchId())){
                return false;
            }
            }

            return accountReqDAO.rejectRequest(requestId, adminId, reason);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error rejecting account request", e);
            return false;
        }
    }
    @Override
    public List<Long> approveMultipleRequests(List<Long> requestIds, long adminId, String role, Long branchId) throws SQLException, QueryException {
        List<Long> failedIds = new ArrayList<>();

        for (Long requestId : requestIds) {
            try {
                AccountRequest request = accountReqDAO.getAccountRequest(requestId);

                if (request == null || request.getStatus() != RequestStatus.PENDING) {
                    failedIds.add(requestId);
                    continue;
                }

                if (UserRole.ADMIN.name().equalsIgnoreCase(role)) {
                    if (branchId == null || !branchId.equals(request.getBranchId())) {
                        failedIds.add(requestId);
                        continue;
                    }
                }

                boolean success = accountReqDAO.approveRequest(requestId, adminId);
                if (!success) {
                    failedIds.add(requestId);
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error approving request ID " + requestId, e);
                failedIds.add(requestId);
            }
        }

        return failedIds;
    }



}
