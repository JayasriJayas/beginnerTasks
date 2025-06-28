package com.bank.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.BranchDAO;
import com.bank.dao.RequestDAO;
import com.bank.dao.UserDAO;
import com.bank.enums.RequestStatus;
import com.bank.enums.UserRole;
import com.bank.factory.DaoFactory;
import com.bank.models.PaginatedResponse;
import com.bank.models.Request;
import com.bank.service.RequestService;
import com.bank.util.PaginationUtil;
import com.bank.util.PasswordUtil;

import exception.QueryException;

public class RequestServiceImpl implements RequestService {

    private static final Logger logger = Logger.getLogger(RequestServiceImpl.class.getName());

    private final RequestDAO requestDAO = DaoFactory.getRequestDAO();
    private final UserDAO userDAO = DaoFactory.getUserDAO();
    private final BranchDAO branchDAO = DaoFactory.getBranchDAO();

    @Override
    public boolean registerRequest(Request request) {
        try {
            if (userDAO.existsByUsername(request.getUsername())) {
                logger.warning("Username already exists: " + request.getUsername());
                return false;
            }

            request.setPassword(PasswordUtil.hashPassword(request.getPassword()));
            return requestDAO.saveRequest(request) > 0;

        } catch (QueryException | SQLException e) {
            logger.log(Level.SEVERE, "Error saving user request", e);
            return false;
        }
    }

    @Override
    public boolean approveUserRequest(long requestId, long adminId) {
        try {
            return userDAO.approveRequestAndCreateUser(requestId, adminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error approving user request", e);
            return false;
        }
    }

    @Override
    public boolean isAdminInSameBranch(long adminId, long requestBranchId) {
        try {
            long adminBranchId = branchDAO.getBranchIdByAdminId(adminId);
            return adminBranchId == requestBranchId;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking admin branch", e);
            return false;
        }
    }

    @Override
    public Request getRequestById(long requestId) {
        try {
        	Request request = requestDAO.getRequestById(requestId);
            return request ;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching request by ID", e);
            return null;
        }
    }

    @Override
    public PaginatedResponse<Request> getRequestList(String adminRole, long id, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize) throws SQLException, QueryException {
        try {

            pageNumber = PaginationUtil.validatePageNumber(pageNumber);
            pageSize = PaginationUtil.validatePageSize(pageSize);
            int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);

            List<Request> requestList;
            long totalRequests;

            if (UserRole.SUPERADMIN.name().equalsIgnoreCase(adminRole)) {
               
                requestList = requestDAO.getPendingRequestsWithDateRange(fromTimestamp, toTimestamp, pageSize, offset);
                totalRequests = requestDAO.countRequestsWithDateRange(fromTimestamp, toTimestamp); // Total for all branches
            } else if (UserRole.ADMIN.name().equalsIgnoreCase(adminRole)) {
               
                long branchId = branchDAO.getBranchIdByAdminId(id);
                requestList = requestDAO.getPendingRequestsByBranchWithDateRange(branchId, fromTimestamp, toTimestamp, pageSize, offset);
                totalRequests = requestDAO.countRequestsByBranchWithDateRange(branchId, fromTimestamp, toTimestamp); // Total for the branch
            } else {
                logger.warning("Unauthorized role for viewing requests: " + adminRole);
                return new PaginatedResponse<>(Collections.emptyList(), pageNumber, pageSize, 0);
            }

          
            return new PaginatedResponse<>(requestList, pageNumber, pageSize, totalRequests);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching request list", e);
            return new PaginatedResponse<>(Collections.emptyList(), pageNumber, pageSize, 0);
        }
    }
    @Override
    public List<Long> approveMultipleRequests(List<Long> requestIds, long adminId, String role)throws SQLException, QueryException {
        List<Long> failedIds = new ArrayList<>();

        for (Long requestId : requestIds) {
            try {
            	
                Request request = getRequestById(requestId);
                if (request == null || request.getStatus() != RequestStatus.PENDING) {
                	
                    failedIds.add(requestId);
                    continue;
                }

                if (UserRole.ADMIN.name().equalsIgnoreCase(role)) {
                    boolean sameBranch = isAdminInSameBranch(adminId, request.getBranchId());
                    if (!sameBranch) {
                        failedIds.add(requestId);
                        continue;
                    }
                }
           
                boolean success = userDAO.approveRequestAndCreateUser(requestId, adminId);
        
                if (!success) failedIds.add(requestId);

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error approving request ID " + requestId, e);
                failedIds.add(requestId);
            }
        }

        return failedIds;
    }
    @Override
    public boolean rejectUserRequest(long requestId, long adminId, String reason)throws SQLException, QueryException  {
        try {
            return requestDAO.rejectRequest(requestId, adminId, reason);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error rejecting request", e);
            return false;
        }
    }
    @Override
    public List<Long> rejectMultipleRequests(List<Long> requestIds, long adminId, String reason, String role) throws SQLException, QueryException {
        List<Long> failedIds = new ArrayList<>();

        for (Long requestId : requestIds) {
            try {
                Request request = getRequestById(requestId);
                if (request == null || request.getStatus() != RequestStatus.PENDING) {
                    failedIds.add(requestId);
                    continue;
                }

                if (UserRole.ADMIN.name().equalsIgnoreCase(role)) {
                    boolean sameBranch = isAdminInSameBranch(adminId, request.getBranchId());
                    if (!sameBranch) {
                        failedIds.add(requestId);
                        continue;
                    }
                }

                boolean success = requestDAO.rejectRequest(requestId, adminId, reason);

                if (!success) {
                    failedIds.add(requestId);
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error rejecting request ID " + requestId, e);
                failedIds.add(requestId);
            }
        }

        return failedIds;
    }
    @Override
    public Request getRequestDetailsById(long requestId) throws SQLException, QueryException {
        try {
            return requestDAO.fetchRequestById(requestId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching request details", e);
            throw e;
        }
    }

    @Override
    public Map<String, Long> getRequestStatusCounts(String role, long adminId) {
        try {
            if ("ADMIN".equalsIgnoreCase(role)) {
                long branchId = branchDAO.getBranchIdByAdminId(adminId);
                return requestDAO.getRequestStatusCounts(branchId);
            } else if ("SUPERADMIN".equalsIgnoreCase(role)) {
                return requestDAO.getRequestStatusCounts(null); // All branches
            } else {
                return Collections.emptyMap(); // Unauthorized role
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching request status counts", e);
            return Collections.emptyMap();
        }
    }
    




}
