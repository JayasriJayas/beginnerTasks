package com.bank.service.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.AdminDAO;
import com.bank.dao.UserDAO;
import com.bank.enums.Gender;
import com.bank.enums.UserStatus;
import com.bank.factory.DaoFactory;
import com.bank.models.Admin;
import com.bank.models.PaginatedResponse;
import com.bank.models.User;
import com.bank.service.AdminService;
import com.bank.util.PaginationUtil;
import com.bank.util.PasswordUtil;

import exception.QueryException;

public class AdminServiceImpl implements AdminService {

    private static final Logger logger = Logger.getLogger(AdminServiceImpl.class.getName());

    private final AdminDAO adminDAO = DaoFactory.getAdminDAO();
    private final UserDAO userDAO = DaoFactory.getUserDAO();

    @Override
    public boolean addAdmin(User user, long adminId) {
        try {
            if (adminDAO.isAdminExists(user.getUsername())) {
                logger.warning("Admin already exists: " + user.getUsername());
                return false;
            }
            user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
            return adminDAO.saveAdmin(user, adminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding admin", e);
            return false;
        }
    }

    @Override
    public boolean updateEmployeeDetails(Map<String, Object> updates) throws SQLException, QueryException {
        if (!updates.containsKey("userId") || !updates.containsKey("modifiedBy")) return false;

        long userId = ((Number) updates.get("userId")).longValue();
        long modifiedBy = ((Number) updates.get("modifiedBy")).longValue();

        User user = userDAO.getUserById(userId);
        if (user == null) return false;

        boolean userFieldUpdated = false;

        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
            userFieldUpdated = true;
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
            userFieldUpdated = true;
        }
        if (updates.containsKey("phone")) {
            user.setPhone(((Number) updates.get("phone")).longValue());
            userFieldUpdated = true;
        }
        if (updates.containsKey("gender")) {
            try {
                user.setGender(Gender.valueOf(updates.get("gender").toString().toUpperCase()));
                userFieldUpdated = true;
            } catch (IllegalArgumentException e) {
                logger.warning("Invalid gender value: " + updates.get("gender"));
                return false;
            }
        }
        if (updates.containsKey("status")) {
            try {
                user.setStatus(UserStatus.valueOf(updates.get("status").toString().toUpperCase()));
                userFieldUpdated = true;
            } catch (IllegalArgumentException e) {
                logger.warning("Invalid status value: " + updates.get("status"));
                return false;
            }
        }

        if (userFieldUpdated) {
            user.setModifiedAt(System.currentTimeMillis());
            user.setModifiedBy(modifiedBy);
        }

        boolean success = !userFieldUpdated || userDAO.updateUserProfile(user);

        Admin admin = adminDAO.getAdminByUserId(userId);
        if (admin == null) return false;

        if (updates.containsKey("branchId")) {
            admin.setBranchId(((Number) updates.get("branchId")).longValue());
            success = success && adminDAO.updateAdmin(admin);
        }

        return success;
    }

    @Override
    public Map<String, Object> getAdminById(long adminId) throws SQLException {
        try {
            return adminDAO.fetchAdmin(adminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in getAdminById", e);
            return null;
        }
    }

    @Override
    public PaginatedResponse<Map<String, Object>> getAllAdmins(int page, int size) throws SQLException, QueryException {
        int offset = PaginationUtil.calculateOffset(page, size);
        int total = adminDAO.countAllAdmins();
        List<Map<String, Object>> admins = adminDAO.fetchAllAdmins(size, offset);
        return new PaginatedResponse<>(admins, page, size, total);
    }

}
