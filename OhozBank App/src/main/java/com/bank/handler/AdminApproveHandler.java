package com.bank.handler;

import com.bank.enums.RequestStatus;

import com.bank.enums.UserRole;
import com.bank.models.Request;
import com.bank.service.UserService;
import com.bank.service.impl.UserServiceImpl;
import com.bank.util.ResponseUtil;
import com.bank.dao.AdminDAO;
import com.bank.dao.RequestDAO;
import com.bank.dao.impl.AdminDAOImpl;
import com.bank.dao.impl.RequestDAOImpl;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class AdminApproveHandler {

    private final UserService userService = new UserServiceImpl();
    private final AdminDAO adminDAO = new AdminDAOImpl();
    private final RequestDAO requestDAO = new RequestDAOImpl();
    private final Gson gson = new Gson();

   
    public void handleAdminApprove(HttpServletRequest req, HttpServletResponse res) {
        try {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("role") == null || session.getAttribute("adminId") == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized. Admin session missing.");
                return;
            }

            BufferedReader reader = req.getReader();
            Map<String, Object> requestBody = gson.fromJson(reader, Map.class);

            Object requestIdObj = requestBody.get("requestId");
            if (requestIdObj == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing requestId in JSON body.");
                return;
            }

            long requestId = Long.parseLong(requestIdObj.toString());
            long adminId = (long) session.getAttribute("adminId");
            String role = session.getAttribute("role").toString();

            Request request = userService.getRequestById(requestId);
            if (request == null || !request.getStatus().equals(RequestStatus.PENDING)) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Request not found or not pending.");
                return;
            }

            if (UserRole.ADMIN.name().equals(role)) {
                boolean sameBranch = userService.isAdminInSameBranch(adminId, request.getBranchId());
                if (!sameBranch) {
                    ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Admins can only approve requests from their own branch.");
                    return;
                }
            }

            boolean success = userService.approveUserRequest(requestId, adminId);
            if (success) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "User approved and account created.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Approval failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            } catch (IOException ex) {
                ex.printStackTrace(); // fallback logging
            }
        }
    }
}

