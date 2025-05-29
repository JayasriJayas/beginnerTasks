package com.bank.handler;

import java.io.BufferedReader;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bank.enums.UserRole;
import com.bank.models.Branch;
import com.bank.models.User;
import com.bank.service.BranchService;
import com.bank.service.impl.BranchServiceImpl;
import com.bank.util.RequestValidator;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

public class SuperadminAddBranchHandler {
	private final BranchService branchService = new BranchServiceImpl();
	Gson gson = new Gson();
	
	public void handleSuperadminAddBranch(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
	        HttpSession session = req.getSession(false);
	        if (session == null || session.getAttribute("role") == null || session.getAttribute("adminId") == null) {
	            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized. Admin session missing.");
	            return;
	        }
	        
	        Branch branch = parseRequest(req);
	        String validateError = validate(branch);
	        if(validateError != null) {	
				ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, validateError);
				return;
			}
	        String role = session.getAttribute("role").toString();
	        long superadminId = (long)session.getAttribute("adminId");
	        if (UserRole.SUPERADMIN.name().equals(role)) {
	            boolean success = branchService.addBranch(branch,superadminId);
	            if (success) {
	                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Branch added successfully");
	            } else {
	                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to add branch");
	            }
	        } else {
	            ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Only Super Admin can add Branch");
	        }
	        
		 } catch (Exception e) {
		        e.printStackTrace();
		        ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
		    }

		
	}
	
	private Branch parseRequest(HttpServletRequest req) throws IOException {
		try(BufferedReader reader = req.getReader()){
			return gson.fromJson(reader, Branch.class);
			
		}
	}

	private String validate(Branch branch) {
		return RequestValidator.validateBranchFields(branch);
		
	}
	

}
