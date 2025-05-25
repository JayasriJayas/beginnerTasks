package com.bank.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class ResponseUtil {
	public static void sendSuccess(HttpServletResponse res,int statusCode, String message) throws IOException {
		JSONObject json =new JSONObject();
		json.put("status","SUCCESS");
		json.put("message", message);
		sendJson(res,statusCode,json);
		
	}
	public static void sendError(HttpServletResponse res,int statusCode, String message) throws IOException {
		JSONObject json = new JSONObject();
		json.put("status", "ERROR");
		json.put("message",message);
		sendJson(res,statusCode,json);	
	}
	
	private static void sendJson(HttpServletResponse res,int statusCode, JSONObject json) throws IOException {
		res.setStatus(statusCode);
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(json.toString());
		res.getWriter().flush();		
		
	}
}
