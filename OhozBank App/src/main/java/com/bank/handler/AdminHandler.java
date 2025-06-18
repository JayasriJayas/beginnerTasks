package com.bank.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.enums.UserRole;
import com.bank.models.Admin;
import com.bank.models.Request;
import com.bank.service.AccountService;
import com.bank.service.AdminService;
import com.bank.service.RequestService;
import com.bank.service.impl.AccountServiceImpl;
import com.bank.service.impl.AdminServiceImpl;
import com.bank.service.impl.RequestServiceImpl;
import com.bank.util.RequestParser;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;

public class AdminHandler {

    private final Logger logger = Logger.getLogger(AdminHandler.class.getName());

    private final AccountService accountService = new AccountServiceImpl();
    private final RequestService requestService = new RequestServiceImpl();
    private final AdminService adminService = new AdminServiceImpl();
    private final Gson gson = new Gson();
   

    public void approveAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	try {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        String role = session.getAttribute("role").toString();
        long branchId = (Long)session.getAttribute("branchId");
        long adminId =(Long) session.getAttribute("userId");
        
        if (UserRole.ADMIN.name().equals(role)) {
            boolean sameBranch = requestService.isAdminInSameBranch(adminId, branchId);
            if (!sameBranch) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Admins can only approve requests from their own branch.");
                return;
            }
        }
       
        Request request = RequestParser.parseRequest(req, Request.class);
            long requestId = request.getId();
          
            boolean success = accountService.approveAccountRequest(requestId, adminId);

            if (success) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Account approved and created.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Approval failed.");
            }
        } catch (Exception e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }

 
    public void edit(HttpServletRequest req, HttpServletResponse res) throws IOException {
	    HttpSession session = req.getSession(false);
	    if (!SessionUtil.isSuperAdmin(session, res)) return;

	    long modifiedBy = (Long) session.getAttribute("userId"); 

	    try (BufferedReader reader = req.getReader()) {
	        Map<String, Object> payload = new Gson().fromJson(reader, Map.class);
	        payload.put("modifiedBy", modifiedBy); 

	        boolean updated = adminService.updateEmployeeDetails(payload);

	        if (updated) {
	            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Employee updated successfully.");
	        } else {
	            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to update employee.");
	        }

	    } catch (Exception e) {
	        logger.severe("Employee update failed: " + e.getMessage());
	        ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
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
                ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonObject);
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Admin not found");
            }
        } catch (Exception e) {
            logger.severe("Error fetching admin: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    public void getAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSuperAdmin(session, res)) return;

            List<Map<String, Object>> admins = adminService.getAllAdmins();
            JSONArray jsonArray = new JSONArray(gson.toJson(admins));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);
        } catch (Exception e) {
            logger.severe("Error fetching all admins: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }
}
