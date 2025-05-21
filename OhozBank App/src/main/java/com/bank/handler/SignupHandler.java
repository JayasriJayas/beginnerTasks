	package com.bank.handler;

import java.io.BufferedReader;


import java.io.IOException;
import java.io.PrintWriter;
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

            Request userRequest = gson.fromJson(reader, Request.class);  

            String validationError = RequestValidator.validateSignupFields(userRequest);
            if (validationError != null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", validationError);
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson(errorResponse));
                return;
            }

            String hashedPassword = PasswordUtil.hashPassword(userRequest.getPassword());
            userRequest.setPassword(hashedPassword);

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
            e.printStackTrace();  
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


