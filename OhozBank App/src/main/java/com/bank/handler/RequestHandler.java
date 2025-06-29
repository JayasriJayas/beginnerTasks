package com.bank.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.enums.RequestStatus;
import com.bank.enums.UserRole;
import com.bank.factory.ServiceFactory;
import com.bank.models.PaginatedResponse;
import com.bank.models.Pagination;
import com.bank.models.Request;
import com.bank.service.RequestService;
import com.bank.util.PaginationUtil;
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
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        String role = (String) session.getAttribute("role");
        Long adminId = (Long) session.getAttribute("adminId");
        Pagination payload = RequestParser.parseRequest(req, Pagination.class);

        String fromDateStr = payload.getFromDate();
        String toDateStr = payload.getToDate();
        RequestStatus status = payload.getStatus(); // <- New

        try {
            long fromTimestamp = Instant.parse(fromDateStr.trim() + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(toDateStr.trim() + "T23:59:59Z").toEpochMilli();
            int page = PaginationUtil.validatePageNumber(payload.getPageNumber());
            int size = PaginationUtil.validatePageSize(payload.getPageSize());

            PaginatedResponse<Request> paginatedResponse = requestService.getRequestList(
                role, adminId, fromTimestamp, toTimestamp, page, size, status);

            String response = gson.toJson(paginatedResponse);
            logger.info("Request list fetched by role: " + role + ", adminId: " + adminId);
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, response);
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
    public void multipleApprove(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        try {
            long adminId = (long) session.getAttribute("adminId");
            String role = session.getAttribute("role").toString();
            try (BufferedReader reader = req.getReader()) {
                String body = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                JSONArray jsonArray = new JSONArray(body); 

                List<Long> requestIds = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    requestIds.add(jsonArray.getLong(i));
                }

                List<Long> failedIds = requestService.approveMultipleRequests(requestIds, adminId, role);

                if (failedIds.isEmpty()) {
                    ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "All requests approved successfully.");
                } else {
                    ResponseUtil.sendError(res, HttpServletResponse.SC_PARTIAL_CONTENT,
                            "Some requests failed: " + failedIds.toString());
                }
            }


        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during bulk approval", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error during bulk approval.");
        }
    }
    public void reject(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        try {
            Request rejectPayload = RequestParser.parseRequest(req, Request.class);
            long requestId = rejectPayload.getId();
            String reason = rejectPayload.getRejectionReason();
            long adminId = (long) session.getAttribute("adminId");

            boolean success = requestService.rejectUserRequest(requestId, adminId, reason);

            if (success) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Request rejected successfully.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Rejection failed.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during rejection", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error during rejection.");
        }
    }
    public void statusCounts(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        try {
            long adminId = (long) session.getAttribute("adminId");
            String role = (String) session.getAttribute("role");

            Map<String, Long> counts = requestService.getRequestStatusCounts(role, adminId);
            String json = gson.toJson(counts);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, json);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch request status counts", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch request status counts");
        }
    }
    public void multipleReject(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        try {
            long adminId = (long) session.getAttribute("adminId");
            String role = session.getAttribute("role").toString();
            try (BufferedReader reader = req.getReader()) {
                String body = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                JSONArray jsonArray = new JSONArray(body);

                List<Long> requestIds = new ArrayList<>();
                String reason = null;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    requestIds.add(obj.getLong("id"));
                    if (reason == null && obj.has("rejectionReason")) {
                        reason = obj.getString("rejectionReason");
                    }
                }

                if (reason == null || reason.trim().isEmpty()) {
                    ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Rejection reason is required.");
                    return;
                }

                List<Long> failedIds = requestService.rejectMultipleRequests(requestIds, adminId, reason, role);

                if (failedIds.isEmpty()) {
                    ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "All requests rejected successfully.");
                } else {
                    ResponseUtil.sendError(res, HttpServletResponse.SC_PARTIAL_CONTENT,
                            "Some rejections failed: " + failedIds.toString());
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during bulk rejection", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error during bulk rejection.");
        }
    }
    public void viewDetails(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        try {
            Request requestObj = RequestParser.parseRequest(req, Request.class);
            long requestId = requestObj.getId();

            Request request = requestService.getRequestDetailsById(requestId);

            if (request != null) {
                String json = gson.toJson(request);  
                ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, json);
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Request not found.");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error viewing request details", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving request details.");
        }
    }


    private String validate(Request request) {
        return RequestValidator.validateSignupFields(request);
    }
}
