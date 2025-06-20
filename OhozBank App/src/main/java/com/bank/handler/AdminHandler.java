package com.bank.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.factory.ServiceFactory;
import com.bank.models.Admin;
import com.bank.models.User;
import com.bank.service.AdminService;
import com.bank.util.RequestParser;
import com.bank.util.RequestValidator;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AdminHandler {

    private static final Logger logger = Logger.getLogger(AdminHandler.class.getName());
    private final AdminService adminService = ServiceFactory.getAdminService();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    public void edit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSuperAdmin(session, res)) return;

        long modifiedBy = (Long) session.getAttribute("userId");

        try (BufferedReader reader = req.getReader()) {
            Map<String, Object> payload = gson.fromJson(reader, Map.class);
            payload.put("modifiedBy", modifiedBy);

            boolean updated = adminService.updateEmployeeDetails(payload);
            if (updated) {
                logger.info("Admin updated successfully by user ID: " + modifiedBy);
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Employee updated successfully.");
            } else {
                logger.warning("Admin update failed for user ID: " + modifiedBy);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to update employee.");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Employee update failed", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }

    public void addAdmin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSuperAdmin(session, res)) return;

        User userRequest = RequestParser.parseRequest(req, User.class);
        String validateError = RequestValidator.validateFields(userRequest);

        if (validateError != null) {
            logger.warning("Add admin failed due to validation: " + validateError);
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, validateError);
            return;
        }

        userRequest.setRoleId(2); // Role ID for Admin
        long adminId = (long) session.getAttribute("adminId");

        boolean success = adminService.addAdmin(userRequest, adminId);
        if (success) {
            logger.info("New admin added by superadmin ID: " + adminId);
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Admin added successfully.");
        } else {
            logger.warning("Failed to add admin by superadmin ID: " + adminId);
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to add admin.");
        }
    }

    public void get(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSuperAdmin(session, res)) return;

            Admin admin = RequestParser.parseRequest(req, Admin.class);
            long adminId = admin.getAdminId();

            Map<String, Object> adminDetails = adminService.getAdminById(adminId);
            if (adminDetails != null) {
                JSONObject jsonObject = new JSONObject(gson.toJson(adminDetails));
                logger.info("Admin details fetched for admin ID: " + adminId);
                ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonObject);
            } else {
                logger.warning("Admin not found for ID: " + adminId);
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Admin not found");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching admin details", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    public void getAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSuperAdmin(session, res)) return;

            List<Map<String, Object>> admins = adminService.getAllAdmins();
            JSONArray jsonArray = new JSONArray(gson.toJson(admins));
            logger.info("Admin list fetched by superadmin.");
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching all admins", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }
}
