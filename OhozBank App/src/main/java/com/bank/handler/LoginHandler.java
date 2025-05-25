package com.bank.handler;

import com.bank.enums.UserRole;
import com.bank.models.Login;
import com.bank.models.Request;
import com.bank.models.User;
import com.bank.service.UserService;
import com.bank.service.impl.UserServiceImpl;
import com.bank.util.ResponseUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

//public class LoginHandler {
//
//    private final UserService userService = new UserServiceImpl();
//    private final Gson gson = new Gson();
//
//    public void handleLogin(HttpServletRequest req, HttpServletResponse res) {
//        res.setContentType("application/json");
//
//        try (PrintWriter out = res.getWriter()) {
//            StringBuilder jsonBuilder = new StringBuilder();
//            String line;
//            try (BufferedReader reader = req.getReader()) {
//                while ((line = reader.readLine()) != null) {
//                    jsonBuilder.append(line);
//                }
//            }
//
//     
//            Login loginData = gson.fromJson(jsonBuilder.toString(), Login.class);
//
//            String username = loginData.getUsername();
//            String password = loginData.getPassword();
//
//
//            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
//                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                out.write("{\"error\":\"Username and password are required.\"}");
//                return;
//            }
//
//            User user = userService.login(username, password);
//            
//            if (user != null) {
//                
//                HttpSession session = req.getSession(true);
//
//            
//                UserRole userRole = UserRole.fromId(user.getRoleId());
//
//                session.setAttribute("role", userRole.name());
//                session.setAttribute("userId", user.getUserId());
//                session.setAttribute("username", user.getUsername());
//                session.setAttribute("branchId", user.getBranchId());
//
//
//                
//                if (userRole == UserRole.ADMIN || userRole == UserRole.SUPER_ADMIN) {
//                    session.setAttribute("adminId", user.getUserId());
//                }
//
//             
//                Map<String, String> success = new HashMap<>();
//                success.put("message", "Login successful");
//                success.put("role", userRole.name());
//
//                
//                res.setStatus(HttpServletResponse.SC_OK);
//                out.write(gson.toJson(success));
//            } else {
//                
//                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                out.write("{\"error\":\"Invalid username or password.\"}");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            try {
//                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//                res.getWriter().write("{\"error\":\"Internal server error.\"}");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//}
public class LoginHandler{
	private final UserService userService = new UserServiceImpl();
	private final Gson gson = new Gson();
	
	public void handleLogin(HttpServletRequest req, HttpServletResponse res) {
		try {
			Login loginData = parseRequest(req);
			 if (loginData == null || loginData.getUsername() == null || loginData.getPassword() == null ||
		                loginData.getUsername().isEmpty() || loginData.getPassword().isEmpty()) {
		                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Username and password are required.");
		                return;
		            }
			 User user = userService.login(loginData.getUsername(), loginData.getPassword());
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


