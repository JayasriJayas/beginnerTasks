package com.bank.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.AdminDAO;
import com.bank.dao.UserDAO;
import com.bank.dao.impl.AdminDAOImpl;
import com.bank.dao.impl.UserDAOImpl;
import com.bank.enums.Gender;
import com.bank.enums.UserStatus;
import com.bank.models.Admin;
import com.bank.models.User;
import com.bank.service.AdminService;
import com.bank.util.PasswordUtil;

import exception.QueryException;

public class AdminServiceImpl implements AdminService {
    private final AdminDAO adminDAO = new AdminDAOImpl();
  
    private final UserDAO userDAO = new UserDAOImpl();
    private final Logger logger = Logger.getLogger(AdminServiceImpl.class.getName());

    @Override
    public boolean addAdmin(User user,long adminId) {
        try {
            if (adminDAO.isAdminExists(user.getUsername())) {
                logger.warning("Admin already exists: " + user.getUsername());
                return false;
            }
            user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
            return adminDAO.saveAdmin(user,adminId);
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
            user.setGender(Gender.valueOf(updates.get("gender").toString().toUpperCase()));
            userFieldUpdated = true;
        }
        if (updates.containsKey("status")) {
            user.setStatus(UserStatus.valueOf(updates.get("status").toString().toUpperCase()));
            userFieldUpdated = true;
        }

        if (userFieldUpdated) {
            user.setModifiedAt(System.currentTimeMillis());
            user.setModifiedBy(modifiedBy);
        }

        boolean success = true;

        if (userFieldUpdated) {
            success = userDAO.updateUserProfile(user);
        }

        Admin admin = adminDAO.getAdminByUserId(userId);
        if (admin == null) return false;

        boolean adminFieldUpdated = false;

        if (updates.containsKey("branchId")) {
            admin.setBranchId(((Number) updates.get("branchId")).longValue());
            adminFieldUpdated = true;
        }
        if (adminFieldUpdated) {
       
            success = success && adminDAO.updateAdmin(admin);
        }

    

        return success;
    }
   
        @Override
        public Map<String, Object> getAdminById(long adminId) throws SQLException {
            try {
            	System.out.println("in service");
                return adminDAO.fetchAdmin(adminId);
            } catch (Exception e) {
                logger.severe("Error in getAdminById: " + e.getMessage());
                return null;
            }
        }

        @Override
        public List<Map<String, Object>> getAllAdmins() throws SQLException {
            try {
                return adminDAO.fetchAllAdmins();
            } catch (Exception e) {
                logger.severe("Error in getAllAdminsWithUserDetails: " + e.getMessage());
                return null;
            }
        }
    }





 

