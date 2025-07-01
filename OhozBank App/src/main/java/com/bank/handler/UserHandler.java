package com.bank.handler;

import java.io.BufferedReader;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import com.bank.factory.ServiceFactory;
import com.bank.service.UserService;
import com.bank.util.RequestValidator;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserHandler {

    private static final Logger logger = Logger.getLogger(UserHandler.class.getName());
    private final UserService userService;
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    public UserHandler() {
        this.userService = ServiceFactory.getUserService();
       
    }

    public void profile(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;
        
        String role = (String) session.getAttribute("role");
        Long userId = (Long) session.getAttribute("userId");

        try {
            Map<String, Object> profile = userService.getProfile(role, userId);
            JSONObject jsonObject = new JSONObject(gson.toJson(profile));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonObject);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Error retrieving profile: " , e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving profile");
        }
    }


    public void edit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;


        long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

        try (BufferedReader reader = req.getReader()) {
            Map<String, Object> payload = gson.fromJson(reader, Map.class);
            
            String error = RequestValidator.validateEditableFields(payload, isAdmin);
            if (error != null) {
                logger.warning("Validation failed: " + error);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, error);
                return;
            }
            
            boolean updated = userService.updateEditableProfileFields(userId, payload);

            if (updated) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Profile updated successfully.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to update profile.");
            }

        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Profile update failed", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
    public void totalUsers(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

            String role = (String) session.getAttribute("role");
            int totalUsers;

            if ("ADMIN".equalsIgnoreCase(role)) {
                Long branchId = (Long) session.getAttribute("branchId");
                totalUsers = userService.getTotalUsersOnlyByBranch(branchId);
            } else {
                totalUsers = userService.getTotalUsersOnlyCount(); // superadmin: all users
            }

            JSONObject response = new JSONObject();
            response.put("totalUsers", totalUsers);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching total user count", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to fetch user count.");
        }
    }



   
   

    
}
