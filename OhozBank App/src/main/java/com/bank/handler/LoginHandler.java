package com.bank.handler;

import com.bank.enums.UserRole;

import com.bank.models.Login;
import com.bank.models.User;
import com.bank.service.AuthenticationService;
import com.bank.service.impl.AuthenticationServiceImpl;
import com.bank.service.UserService;
import com.bank.service.impl.UserServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;

public class LoginHandler{
	private final AuthenticationService authenticationService = new AuthenticationServiceImpl();
	private final UserService userService = new UserServiceImpl();
;	private final Gson gson = new Gson();
	
	public void handleLogin(HttpServletRequest req, HttpServletResponse res) {
		try {
			
			Login loginData = parseRequest(req);
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
	            e.printStackTrace();
	            try {
	                ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
	            } catch (IOException ioException) {
	                ioException.printStackTrace();
	            }
	        }
	    }

	private Login parseRequest(HttpServletRequest req) throws IOException {
	
	    try (BufferedReader reader = req.getReader()) {
	        return gson.fromJson(reader, Login.class);
	    }
	}

}


