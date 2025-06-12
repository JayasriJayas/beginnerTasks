package com.bank.handler;

import com.bank.enums.UserRole;
import com.bank.models.Request;
import com.bank.models.User;
import com.bank.service.*;
import com.bank.service.impl.*;
import com.bank.util.RequestParser;
import com.bank.util.RequestValidator;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class UserHandler {

    private final Logger logger = Logger.getLogger(UserHandler.class.getName());
    private final RequestService requestService = new RequestServiceImpl();
    private final UserService userService = new UserServiceImpl();
    private final AuthenticationService authenticationService = new AuthenticationServiceImpl();
    private final AdminService adminService = new AdminServiceImpl();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();



    public void login(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            User loginData = RequestParser.parseRequest(req, User.class);
            if (loginData == null || loginData.getUsername() == null || loginData.getPassword() == null ||
                    loginData.getUsername().isEmpty() || loginData.getPassword().isEmpty()) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Username and password are required.");
                return;
            }

            User user = authenticationService.login(loginData.getUsername(), loginData.getPassword());
            if (user == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password.");
                return;
            }

            HttpSession session = req.getSession(true);
            UserRole role = userService.getUserRole(user);

            session.setAttribute("role", role.name());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("branchId", user.getBranchId());

            if (userService.isAdmin(user)) {
                session.setAttribute("adminId", user.getUserId());
            }

            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Login successful as " + role.name());

        } catch (Exception e) {
            logger.severe("Login error: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
        }
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
            logger.severe("Error retrieving profile: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving profile");
        }
    }


    public void edit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;


        long userId = (Long) session.getAttribute("userId");

        try (BufferedReader reader = req.getReader()) {
            Map<String, Object> payload = gson.fromJson(reader, Map.class);
            boolean updated = userService.updateEditableProfileFields(userId, payload);

            if (updated) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Profile updated successfully.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to update profile.");
            }

        } catch (Exception e) {
            logger.severe("Profile update failed: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }

    public void addAdmin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSuperAdmin(session, res)) return;
       

        User userRequest = RequestParser.parseRequest(req, User.class);
        String validateError = RequestValidator.validateFields(userRequest);

        if (validateError != null) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, validateError);
            return;
        }

        userRequest.setRoleId(2); 

        String role = session.getAttribute("role").toString();
        long adminId = (long) session.getAttribute("adminId");

        if (UserRole.SUPERADMIN.name().equals(role)) {
            boolean success = adminService.addAdmin(userRequest, adminId);
            if (success) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Admin added successfully.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to add admin.");
            }
        } else {
            ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Only Super Admin can add Admin.");
        }
    }


    
}
