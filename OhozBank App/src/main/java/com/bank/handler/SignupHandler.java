package com.bank.handler;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bank.models.Request;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.bank.models.Request;
import com.bank.service.RequestService;
import com.bank.service.impl.RequestServiceImpl;
import com.bank.util.RequestValidator;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class SignupHandler{
	private final Logger logger = Logger.getLogger(SignupHandler.class.getName());
	private final RequestService requestService = new RequestServiceImpl();
	private static final Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd").create();
	
	public void handleSignup(HttpServletRequest req,HttpServletResponse res) throws IOException {
		
		try {
			Request userRequest = parseRequest(req);
			
			String validateError = validate(userRequest);
			
			if(validateError != null) {	
				ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, validateError);
				return;
			}
			
			boolean registered = requestService.registerRequest(userRequest);
			
			if(registered) {
				ResponseUtil.sendSuccess(res,HttpServletResponse.SC_OK,
	                    "Signup request submitted. Awaiting approval.");
			}
			else {
				ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	                    "Signup failed. Try again later.");
			}
			
		}catch(IOException e) {
			try {
			    ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			            "Server error or invalid input.");
			} catch (IOException ioEx) {
			    logger.log(Level.SEVERE, "Failed to send error response", ioEx);
			}

			
		}
		
	}
	private Request parseRequest(HttpServletRequest req) throws IOException {
		try(BufferedReader reader = req.getReader()){
			return gson.fromJson(reader,Request.class);
		}
		
	}
	private String validate(Request request) {
		return RequestValidator.validateSignupFields(request);
		
	}
}

