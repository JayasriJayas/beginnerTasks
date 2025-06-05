package com.bank.handler;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.bank.service.UserService;
import com.bank.service.impl.UserServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

public class ProfileHandler {
    private final Logger logger = Logger.getLogger(ProfileHandler.class.getName());
    private final UserService userService = new UserServiceImpl();
    private final Gson gson = new Gson();

    public void handleProfile(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
            return;
        }

        String role = session.getAttribute("role").toString();
        Long userId = (Long) session.getAttribute("userId");

        try {
            Map<String, Object> profile = userService.getProfile(role, userId);
            JSONObject jsonObject = new JSONObject(new Gson().toJson(profile));
   		    ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonObject);
        } catch (Exception e) {
            logger.severe("Error retrieving profile: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving profile");
        }
    }
}

