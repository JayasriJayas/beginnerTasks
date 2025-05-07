package com.bank.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bank.models.Request;
import com.bank.service.UserService;
import com.bank.service.impl.UserServiceImpl;
import com.bank.util.PasswordUtil;
import com.bank.util.RequestValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SignupHandler {

    private final UserService userService = new UserServiceImpl();

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final Gson gson = new GsonBuilder()
            .setDateFormat(DATE_FORMAT)
            .create();

    public void handleSignup(HttpServletRequest req, HttpServletResponse res) {
        res.setContentType("application/json");

        try (BufferedReader reader = req.getReader(); PrintWriter out = res.getWriter()) {

            Request userRequest = gson.fromJson(reader, Request.class);  // 'dob' must be java.util.Date

            // Validate request
            String validationError = RequestValidator.validateSignupFields(userRequest);
            if (validationError != null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", validationError);
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson(errorResponse));
                return;
            }

            // Hash the password before saving
            String hashedPassword = PasswordUtil.hashPassword(userRequest.getPassword());
            userRequest.setPassword(hashedPassword);

            // Register the user
            boolean registered = userService.registerRequest(userRequest);

            Map<String, String> responseMap = new HashMap<>();
            if (registered) {
                responseMap.put("message", "Signup request submitted. Awaiting approval.");
                res.setStatus(HttpServletResponse.SC_OK);
            } else {
                responseMap.put("error", "Signup failed. Try again later.");
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            out.write(gson.toJson(responseMap));

        } catch (Exception e) {
            e.printStackTrace();  // You can replace with proper logging
            try {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter out = res.getWriter();
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Server error or invalid input.");
                out.write(gson.toJson(errorResponse));
            } catch (IOException ioEx) {
                ioEx.printStackTrace();  // Fallback logging
            }
        }
    }
}


//package com.bank.handler;
//import com.bank.models.Request;
//
//import com.bank.service.UserService;
//import com.bank.service.impl.UserServiceImpl;
//import com.bank.util.PasswordUtil;
//import com.bank.util.RequestValidator;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.PrintWriter;
//import java.time.LocalDate;
//
//public class SignupHandler {
//
//    private final UserService userService = new UserServiceImpl();
//
//    public void handleSignup(HttpServletRequest req, HttpServletResponse res) {
//        try {
//            Request userRequest = new Request();
//          
//            userRequest.setUsername(req.getParameter("username"));
//            System.out.println(req.getParameter("username"));
//            userRequest.setPassword(req.getParameter("password"));
//            userRequest.setEmail(req.getParameter("email"));
//            
//            userRequest.setPhone(Integer.parseInt(req.getParameter("phone")));
//            
//            userRequest.setGender(req.getParameter("gender"));
//            userRequest.setDob(LocalDate.parse(req.getParameter("dob")));
//            userRequest.setAddress(req.getParameter("address"));
//            userRequest.setMaritalStatus(req.getParameter("maritalStatus"));
//            System.out.println(req.getParameter("aadharNo"));
//          
//            userRequest.setAadharNo(Long.parseLong(req.getParameter("aadharNo")));
//            
//            userRequest.setPanNo(req.getParameter("panNo"));
//            userRequest.setBranchId(Long.parseLong(req.getParameter("branchId")));
//            userRequest.setBranchName(req.getParameter("branchName"));
//            userRequest.setOccupation(req.getParameter("occupation"));
//            userRequest.setAnnualIncome(Double.parseDouble(req.getParameter("annualIncome")));
//
//        
//            String error = RequestValidator.validateSignupFields(userRequest);
//            if (error != null) {
//                res.setContentType("text/html");
//                res.getWriter().println("Signup failed: " + error);
//                return;
//            }
//
//            
//            String hashedPassword = PasswordUtil.hashPassword(userRequest.getPassword());
//            userRequest.setPassword(hashedPassword);
//
//            boolean registered = userService.registerRequest(userRequest);
//            System.out.println(registered);
//            res.setContentType("text/html");
//            PrintWriter out = res.getWriter();
//            if (registered) {
//                out.println("Signup request submitted. Awaiting approval.");
//            } else {
//                out.println("Signup failed. Try again later.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            try {
//                res.getWriter().println("Error: Invalid input or server error.");
//            } catch (Exception ignored) {}
//        }
//    }
//}
