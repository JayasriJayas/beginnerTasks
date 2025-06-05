package com.bank.handler;

import java.io.IOException;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import com.bank.models.Request;
import com.bank.service.RequestService;
import com.bank.service.impl.RequestServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;


public class AdminRequestlistHandler {
	
	private final Logger logger = Logger.getLogger(AdminRequestlistHandler.class.getName());
	private final RequestService requestService = new RequestServiceImpl();
	private final Gson gson = new Gson();
	
    public void handleAdminRequestlist(HttpServletRequest req, HttpServletResponse res) throws IOException {

	HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("role") == null) {
        ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
        return;
    }
    String admin = (String)session.getAttribute("role");
	long id = (Long)session.getAttribute("adminId");
	try {
		 List<Request> requestList = requestService.getRequestList(admin,id);
		 JSONArray jsonArray = new JSONArray(new Gson().toJson(requestList));
		 ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);
		
	}
	catch (Exception e) {
		ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
	}
   
    
	
	
    }
}
