package com.bank.service.impl;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.AdminDAO;
import com.bank.dao.CustomerDAO;
import com.bank.dao.UserDAO;
import com.bank.enums.Gender;
import com.bank.enums.UserRole;
import com.bank.factory.DaoFactory;
import com.bank.models.Admin;
import com.bank.models.Customer;
import com.bank.models.User;
import com.bank.service.UserService;
import com.bank.util.PasswordUtil;

import exception.QueryException;

public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    private final AdminDAO adminDAO = DaoFactory.getAdminDAO();
    private final UserDAO userDAO = DaoFactory.getUserDAO();
    private final CustomerDAO customerDAO = DaoFactory.getCustomerDAO();

    @Override
    public boolean isAdmin(User user) {
        UserRole role = UserRole.fromId(user.getRoleId());
        return role == UserRole.ADMIN || role == UserRole.SUPERADMIN;
    }

    @Override
    public UserRole getUserRole(User user) {
        return UserRole.fromId(user.getRoleId());
    }

    @Override
    public Map<String, Object> getProfile(String role, long userId) throws SQLException, QueryException {
        logger.info("Fetching profile for userId: " + userId + " as role: " + role);

        Map<String, Object> profile = new HashMap<>();
        User user = userDAO.getUserById(userId);
        profile.put("user", user);

        if ("USER".equalsIgnoreCase(role)) {
            Customer customer = customerDAO.getCustomerByUserId(userId);
            profile.put("customer", customer);
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            Admin admin = adminDAO.getAdminByUserId(userId);
            profile.put("admin", admin);
        }

        return profile;
    }

    @Override
    public boolean updateEditableProfileFields(long userId, Map<String, Object> updates) throws SQLException, QueryException {
        logger.info("Updating profile for userId: " + userId);

        User user = userDAO.getUserById(userId);
        if (user == null) {
            logger.warning("User not found for userId: " + userId);
            return false;
        }

        updateUserFields(user, updates, userId);
        boolean userUpdated = userDAO.updateUserProfile(user);

        UserRole role = UserRole.fromId(user.getRoleId());
        if (role == UserRole.USER) {
            Customer customer = customerDAO.getCustomerByUserId(userId);
            if (customer == null) {
                logger.warning("Customer not found for userId: " + userId);
                return false;
            }
            updateCustomerFields(customer, updates);
            boolean customerUpdated = customerDAO.updateCustomerProfile(customer);
            return userUpdated && customerUpdated;
        }

        return userUpdated;
    }

    private void updateUserFields(User user, Map<String, Object> updates, long modifierId) {
        if (updates.containsKey("name") && updates.get("name") != null) user.setName((String) updates.get("name"));
        if (updates.containsKey("email") && updates.get("email") != null) user.setEmail((String) updates.get("email"));
        if (updates.containsKey("phone") && updates.get("phone") != null) {
            Object phoneObj = updates.get("phone");
            if (phoneObj instanceof Number) {
                user.setPhone(((Number) phoneObj).longValue());
            } else {
                logger.warning("Phone is not a valid number: " + phoneObj);
            }
        }
        if (updates.containsKey("gender") && updates.get("gender") != null) {
            user.setGender(Gender.valueOf(updates.get("gender").toString().toUpperCase()));
        }

        user.setModifiedAt(System.currentTimeMillis());
        user.setModifiedBy(modifierId);
    }

    private void updateCustomerFields(Customer customer, Map<String, Object> updates) {
        if (updates.containsKey("address") && updates.get("address") != null) customer.setAddress(updates.get("address").toString());
        if (updates.containsKey("dob") && updates.get("dob") != null) {
            try {
                customer.setDob(Date.valueOf(updates.get("dob").toString()));
            } catch (IllegalArgumentException e) {
                logger.warning("Invalid date format for DOB: " + updates.get("dob"));
            }
        }
        if (updates.containsKey("maritalStatus") && updates.get("maritalStatus") != null) customer.setMaritalStatus(updates.get("maritalStatus").toString());
        if (updates.containsKey("occupation") && updates.get("occupation") != null) customer.setOccupation(updates.get("occupation").toString());
        if (updates.containsKey("annualIncome") && updates.get("annualIncome") != null) {
            try {
                customer.setAnnualIncome(Double.parseDouble(updates.get("annualIncome").toString()));
            } catch (NumberFormatException e) {
                logger.warning("Invalid annual income value: " + updates.get("annualIncome"));
            }
        }

        logger.fine("Customer profile fields updated: " + updates.keySet());
    }

    @Override
    public boolean verifyPassword(long userId, String password) throws SQLException, QueryException {
        try {
            User user = userDAO.getUserById(userId);
            return user != null && PasswordUtil.checkPassword(password, user.getPassword());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error verifying password for userId: " + userId, e);
            return false;
        }
    }
    @Override
    public boolean changePassword(long userId, String currentPassword, String newPassword) throws SQLException, QueryException {
        try {
            User user = userDAO.getUserById(userId);
            if (user == null || !PasswordUtil.checkPassword(currentPassword, user.getPassword())) {
                return false;
            }

            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            return userDAO.updatePassword(userId, hashedPassword);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error changing password for userId: " + userId, e);
            return false;
        }
    }



}
