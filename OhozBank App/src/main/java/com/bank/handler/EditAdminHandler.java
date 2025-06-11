package com.bank.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.bank.service.AdminService;
import com.bank.service.impl.AdminServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

public class EditAdminHandler {
	 private final Logger logger = Logger.getLogger(EditBranchHandler.class.getName());
	    private final AdminService adminService = new AdminServiceImpl();
	    private final Gson gson = new Gson();
	    
	public void handleEditAdmin(HttpServletRequest req, HttpServletResponse res) throws IOException {
	    HttpSession session = req.getSession(false);
	    if (session == null || !"SUPERADMIN".equals(session.getAttribute("role"))) {
	        ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
	        return;
	    }

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


}
