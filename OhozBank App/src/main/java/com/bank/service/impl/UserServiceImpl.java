package com.bank.service.impl;


import java.sql.SQLException;

import java.sql.Date; 
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.bank.dao.AdminDAO;
import com.bank.dao.CustomerDAO;
import com.bank.dao.UserDAO;
import com.bank.dao.impl.AdminDAOImpl;
import com.bank.dao.impl.CustomerDAOImpl;
import com.bank.dao.impl.UserDAOImpl;
import com.bank.enums.Gender;
import com.bank.enums.UserRole;
import com.bank.models.Admin;
import com.bank.models.Customer;
import com.bank.models.User;
import com.bank.service.UserService;

import exception.QueryException;

public class UserServiceImpl implements UserService {
	private final AdminDAO adminDAO = new AdminDAOImpl();
	private final UserDAO userDAO = new UserDAOImpl();
	private final CustomerDAO customerDAO = new CustomerDAOImpl();
	
	private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

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
        Map<String, Object> profile = new HashMap<>();
        User user = userDAO.getUserById(userId);
        profile.put("user", user);

        if ("USER".equalsIgnoreCase(role)) {
            Customer customer = customerDAO.getCustomerByUserId(userId);
            profile.put("customer", customer);
        } else if ("ADMIN".equalsIgnoreCase(role)) {
        	System.out.println("in service");
            Admin admin = adminDAO.getAdminByUserId(userId);
            profile.put("admin", admin);
        }
        
        return profile;
    }
    @Override
    public boolean updateEditableProfileFields(long userId, Map<String, Object> updates) throws SQLException,QueryException {
        User user = userDAO.getUserById(userId);
        if (user == null) return false;

 
        if (updates.containsKey("name")) user.setName((String) updates.get("name"));
        if (updates.containsKey("email")) user.setEmail((String) updates.get("email"));
        if (updates.containsKey("phone")) user.setPhone(((Number) updates.get("phone")).longValue());
        if (updates.containsKey("gender")) user.setGender(Gender.valueOf(updates.get("gender").toString().toUpperCase()));

        user.setModifiedAt(System.currentTimeMillis());
        user.setModifiedBy(userId);
        System.out.println("i am here");

        boolean userUpdated = userDAO.updateUserProfile(user);

       
        boolean customerUpdated = true;
        if (user.getRoleId() == UserRole.USER.getId()) {
            Customer customer = customerDAO.getCustomerByUserId(userId);
            if (customer == null) return false; 

            if (updates.containsKey("address")) customer.setAddress(updates.get("address").toString());
            if (updates.containsKey("dob")) customer.setDob(Date.valueOf(updates.get("dob").toString()));
            if (updates.containsKey("maritalStatus")) customer.setMaritalStatus(updates.get("maritalStatus").toString());
            if (updates.containsKey("occupation")) customer.setOccupation(updates.get("occupation").toString());
            if (updates.containsKey("annualIncome")) customer.setAnnualIncome(Double.parseDouble(updates.get("annualIncome").toString()));
            System.out.println("i am in service");
            customerUpdated = customerDAO.updateCustomerProfile(customer);
        }

        return user.getRoleId() == UserRole.USER.getId() ? userUpdated && customerUpdated : userUpdated;

    }



}


