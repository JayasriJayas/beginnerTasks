package com.bank.handler;

import java.io.BufferedReader;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bank.enums.UserRole;
import com.bank.models.Request;
import com.bank.models.User;
import com.bank.util.RequestValidator;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;
import com.bank.service.AdminService;
import com.bank.service.impl.AdminServiceImpl;

public class SuperadminAddEmployeeHandler {
	
	private final AdminService adminService = new AdminServiceImpl();
	Gson gson = new Gson();
	public void handleSuperadminAddEmployee(HttpServletRequest req, HttpServletResponse res) throws IOException {
	    try {
	        HttpSession session = req.getSession(false);
	        if (session == null || session.getAttribute("role") == null || session.getAttribute("adminId") == null) {
	            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized. Admin session missing.");
	            return;
	        }

	        User userRequest = parseRequest(req);
			
			String validateError = validate(userRequest);
			
			if(validateError != null) {	
				ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, validateError);
				return;
			}
			userRequest.setRoleId(2); 

	        String role = session.getAttribute("role").toString();
	        long adminId = (long)session.getAttribute("adminId");

	        if (UserRole.SUPERADMIN.name().equals(role)) {
	            boolean success = adminService.addAdmin(userRequest,adminId);
	            if (success) {
	                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Admin added successfully");
	            } else {
	                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to add admin");
	            }
	        } else {
	            ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Only Super Admin can add Admin");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
	    }
	}
	private User parseRequest(HttpServletRequest req) throws IOException {
		try(BufferedReader reader = req.getReader()){
			return gson.fromJson(reader,User.class);
		}
		
	}
	private String validate(User user) {
		return RequestValidator.validateFields(user);
		
	}

	}
	
	

