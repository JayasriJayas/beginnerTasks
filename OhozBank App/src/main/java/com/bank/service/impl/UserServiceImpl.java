package com.bank.service.impl;


import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.RequestDAO;
import com.bank.dao.impl.RequestDAOImpl;
import com.bank.models.Request;
import com.bank.models.User;
import com.bank.service.UserService;
import com.bank.util.PasswordUtil;
import com.bank.dao.impl.UserDAOImpl;
import com.bank.enums.UserRole;
import com.bank.dao.UserDAO;
import com.bank.dao.AdminDAO;
import com.bank.dao.impl.AdminDAOImpl;


import exception.QueryException;

public class UserServiceImpl implements UserService {
	
	private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    private final RequestDAO requestDAO = new RequestDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();
    private final AdminDAO adminDAO = new AdminDAOImpl();
    

    @Override
    public boolean registerRequest(Request request) {
        try {
        	if (userDAO.existsByUsername(request.getUsername())) {
                logger.warning("Username already exists: " + request.getUsername());
                return false; 
            }
        	 String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
             request.setPassword(hashedPassword);
             return requestDAO.saveRequest(request) > 0;
        } catch (QueryException | SQLException e) {
            logger.log(Level.SEVERE,"Error saving user request", e);
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
        public User login(String username, String password) throws SQLException,QueryException {
            User user = userDAO.findByUsername(username);            
            if (user == null) {
                return null;
            }
//            return user;
           
            boolean passwordMatch = PasswordUtil.checkPassword(password, user.getPassword());
            return passwordMatch ? user : null;
           
        }
        @Override
        public boolean isAdmin(User user) {
            UserRole role = UserRole.fromId(user.getRoleId());
            return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN;
        }
        
        @Override
        public UserRole getUserRole(User user) {
            return UserRole.fromId(user.getRoleId());
        }
        @Override
        public boolean isAdminInSameBranch(long adminId, long requestBranchId) {
            try {
                long adminBranchId = adminDAO.getBranchIdByAdminId(adminId);
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
}


