package com.bank.service.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.BranchDAO;
import com.bank.dao.RequestDAO;
import com.bank.dao.UserDAO;
import com.bank.enums.UserRole;
import com.bank.factory.DaoFactory;
import com.bank.models.Request;
import com.bank.service.RequestService;
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
            return requestDAO.getRequestById(requestId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching request by ID", e);
            return null;
        }
    }

    @Override
    public List<Request> getRequestList(String adminRole, long id) {
        try {
            if (UserRole.SUPERADMIN.name().equalsIgnoreCase(adminRole)) {
                return requestDAO.getPendingRequests();
            } else if (UserRole.ADMIN.name().equalsIgnoreCase(adminRole)) {
                long branchId = branchDAO.getBranchIdByAdminId(id);
                return requestDAO.getPendingRequestsByBranch(branchId);
            } else {
                logger.warning("Unauthorized role for viewing requests: " + adminRole);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching request list", e);
            return Collections.emptyList();
        }
    }
}
