package com.bank.handler;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import com.bank.enums.RequestStatus;
import com.bank.enums.UserRole;
import com.bank.factory.ServiceFactory;
import com.bank.models.Request;
import com.bank.service.RequestService;
import com.bank.util.RequestParser;
import com.bank.util.RequestValidator;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RequestHandler {

    private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());
    private final RequestService requestService = ServiceFactory.getRequestService();
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    public void signup(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Request userRequest = RequestParser.parseRequest(req, Request.class);
            String validateError = validate(userRequest);

            if (validateError != null) {
                logger.warning("Signup validation failed: " + validateError);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, validateError);
                return;
            }

            boolean registered = requestService.registerRequest(userRequest);
            if (registered) {
                logger.info("Signup request submitted for username: " + userRequest.getUsername());
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK,
                        "Signup request submitted. Awaiting approval.");
            } else {
                logger.warning("Signup failed for username: " + userRequest.getUsername());
                ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Signup failed. Try again later.");
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during signup", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Server error or invalid input.");
        }
    }

    public void list(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;

        String role = (String) session.getAttribute("role");
        Long adminId = (Long) session.getAttribute("adminId");

        try {
            List<Request> requestList = requestService.getRequestList(role, adminId);
            JSONArray jsonArray = new JSONArray(gson.toJson(requestList));
            logger.info("Request list fetched by role: " + role + ", adminId: " + adminId);
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching request list", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching request list.");
        }
    }

    public void approve(HttpServletRequest req, HttpServletResponse res) {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

            Request request = RequestParser.parseRequest(req, Request.class);
            long requestId = request.getId();
            long adminId = (long) session.getAttribute("adminId");
            String role = session.getAttribute("role").toString();

            Request requestObj = requestService.getRequestById(requestId);
            if (requestObj == null || !RequestStatus.PENDING.equals(requestObj.getStatus())) {
                logger.warning("Approval failed: request not found or not pending. Request ID: " + requestId);
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Request not found or not pending.");
                return;
            }

            if (UserRole.ADMIN.name().equals(role)) {
                boolean sameBranch = requestService.isAdminInSameBranch(adminId, request.getBranchId());
                if (!sameBranch) {
                    logger.warning("Admin attempted to approve request from another branch. Admin ID: " + adminId);
                    ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN,
                            "Admins can only approve requests from their own branch.");
                    return;
                }
            }

            boolean success = requestService.approveUserRequest(requestId, adminId);
            if (success) {
                logger.info("Request approved by admin ID: " + adminId + ", request ID: " + requestId);
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "User approved and account created.");
            } else {
                logger.warning("Approval failed for request ID: " + requestId);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Approval failed.");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception during approval process", e);
            try {
                ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            } catch (IOException ioEx) {
                logger.log(Level.SEVERE, "Error writing error response during approval", ioEx);
            }
        }
    }

    private String validate(Request request) {
        return RequestValidator.validateSignupFields(request);
    }
}
