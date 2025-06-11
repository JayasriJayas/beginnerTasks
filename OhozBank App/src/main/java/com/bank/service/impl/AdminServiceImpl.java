package com.bank.service.impl;

import java.sql.SQLException;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.models.Admin;
import com.bank.models.Branch;
import com.bank.models.User;
import com.bank.service.AdminService;
import com.bank.util.PasswordUtil;
import exception.QueryException;
import com.bank.dao.AdminDAO;
import com.bank.dao.impl.AdminDAOImpl;
import com.bank.dao.impl.UserDAOImpl;
import com.bank.dao.UserDAO;
import com.bank.dao.BranchDAO;
import com.bank.dao.impl.BranchDAOImpl;
import com.bank.enums.Gender;
import com.bank.enums.UserStatus;

public class AdminServiceImpl implements AdminService {
    private final AdminDAO adminDAO = new AdminDAOImpl();
    private final BranchDAO branchDAO = new BranchDAOImpl();
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
    public boolean updateBranchDetails(long userId,Map<String, Object> updates) throws SQLException, QueryException {
        Branch branch = new Branch();

        if (updates.containsKey("branchId")) {
            branch.setBranchId(((Number) updates.get("branchId")).longValue());
        } else {
            logger.warning("BranchId is required");
            return false; 
        }
        if (updates.containsKey("branchName")) branch.setBranchName((String) updates.get("branchName"));
        if (updates.containsKey("ifscCode")) branch.setIfscCode((String) updates.get("ifscCode"));
        if (updates.containsKey("location")) branch.setLocation((String) updates.get("location"));
        if (updates.containsKey("contact")) branch.setContact(((Number) updates.get("contact")).longValue());
        if(updates.containsKey("adminId)")) branch.setAdminId(((Number) updates.get("adminId")).longValue());
 
        branch.setModifiedAt(System.currentTimeMillis());
        branch.setModifiedBy(userId); 

       
		return branchDAO.updateBranch(branch);
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
   




 
}
