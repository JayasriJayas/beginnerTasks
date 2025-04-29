//package com.bank.handler;
//
//
//import com.bank.dao.UserDAO;
//import com.bank.dao.impl.UserDAOImpl;
//import com.bank.model.User;
//import com.bank.util.ValidationUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * Handler for login-related business logic.
// */
//public class LoginHandler {
//    private static final Logger LOGGER = Logger.getLogger(LoginHandler.class.getName());
//    private final UserDAO userDAO;
//
//    /**
//     * Constructor initializes the UserDAO
//     */
//    public LoginHandler() {
//        this.userDAO = new UserDAOImpl();
//    }
//
//    /**
//     * Constructor with dependency injection for testing
//     * @param userDAO UserDAO implementation
//     */
//    public LoginHandler(UserDAO userDAO) {
//        this.userDAO = userDAO;
//    }
//
//    /**
//     * Authenticates a user with username and password
//     * @param username Username to authenticate
//     * @param password Password to verify
//     * @return Map containing authentication result and user object if successful
//     */
//    public Map<String, Object> authenticateUser(String username, String password) {
//        Map<String, Object> result = new HashMap<>();
//        
//        // Validate input
//        if (username == null || username.trim().isEmpty()) {
//            result.put("success", false);
//            result.put("message", "Username cannot be empty");
//            return result;
//        }
//        
//        if (password == null || password.trim().isEmpty()) {
//            result.put("success", false);
//            result.put("message", "Password cannot be empty");
//            return result;
//        }
//        
//        try {
//            // Attempt to authenticate the user
//            User user = userDAO.authenticateUser(username, password);
//            
//            if (user != null) {
//                result.put("success", true);
//                result.put("user", user);
//                result.put("message", "Login successful");
//                LOGGER.info("User authenticated successfully: " + username);
//            } else {
//                result.put("success", false);
//                result.put("message", "Invalid username or password");
//                LOGGER.info("Failed login attempt for username: " + username);
//            }
//        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Error during authentication for user: " + username, e);
//            result.put("success", false);
//            result.put("message", "System error during login. Please try again later.");
//        }
//        
//        return result;
//    }
//
//    /**
//     * Gets the dashboard URL based on user role
//     * @param user User object
//     * @return URL of the appropriate dashboard for the user's role
//     */
//    public String getDashboardURLByRole(User user) {
//        if (user == null) {
//            return "login.jsp";
//        }
//        
//        switch (user.getRoleId()) {
//            case 1: // Super Admin
//                return "dashboard/superadmin.jsp";
//            case 2: // Admin
//                return "dashboard/admin.jsp";
//            case 3: // Customer
//                return "dashboard/user.jsp";
//            default:
//                return "login.jsp";
//        }
//    }
//
//    /**
//     * Logs a user out by updating their last login time
//     * @param userId User ID
//     * @return true if successful, false if failed
//     */
//    public boolean logoutUser(int userId) {
//        try {
//            return userDAO.updateLastLogin(userId);
//        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Error during logout for user ID: " + userId, e);
//            return false;
//        }
//    }
//}
