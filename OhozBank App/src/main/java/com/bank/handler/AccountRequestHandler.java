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
import com.bank.models.AccountRequest;
import com.bank.models.PaginatedResponse;
import com.bank.models.Pagination;
import com.bank.models.Request;
import com.bank.service.AccountRequestService;
import com.bank.service.RequestService;
import com.bank.util.PaginationUtil;
import com.bank.util.RequestParser;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;

public class AccountRequestHandler {

    private static final Logger logger = Logger.getLogger(AccountRequestHandler.class.getName());
    private final AccountRequestService accountService = ServiceFactory.getAccountRequestService();
    private final RequestService requestService = ServiceFactory.getRequestService();
    private final Gson gson = new Gson();

    public void request(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;

        try {
            long userId = (Long) session.getAttribute("userId");
            AccountRequest request = RequestParser.parseRequest(req, AccountRequest.class);
            long branchId = request.getBranchId();

            boolean success = accountService.createAccountRequest(userId, branchId);
            if (success) {
                logger.info("Account request submitted by userId: " + userId);
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Account request submitted.");
            } else {
                logger.warning("Account request failed for userId: " + userId);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Request failed.");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during account request submission", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }

    public void list(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;
        Pagination payload = RequestParser.parseRequest(req, Pagination.class);


        String fromDateStr = payload.getFromDate();
        String toDateStr = payload.getToDate();
        try {
        	 long fromTimestamp = Instant.parse(fromDateStr.trim() + "T00:00:00Z").toEpochMilli();
             long toTimestamp = Instant.parse(toDateStr.trim() + "T23:59:59Z").toEpochMilli();

       
             int page = PaginationUtil.validatePageNumber(payload.getPageNumber());
             int size = PaginationUtil.validatePageSize(payload.getPageSize());
             RequestStatus status = payload.getStatus(); 
            UserRole role = UserRole.valueOf(session.getAttribute("role").toString().toUpperCase());
            PaginatedResponse<AccountRequest> requests;
            

            if (role == UserRole.SUPERADMIN) {
                requests = accountService.getAllRequests(fromTimestamp, toTimestamp, page, size,status);
            } else if (role == UserRole.ADMIN) {
                long adminId = (long) session.getAttribute("adminId");
                requests = accountService.getRequestsByAdminBranch(adminId,fromTimestamp, toTimestamp, page, size,status);
            } else {
                logger.warning("Unauthorized access attempt to list account requests by role: " + role);
                ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Unauthorized access");
                return;
            }

            String response = gson.toJson(requests);
            logger.info("Account requests listed by role: " + role);
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, response);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to retrieve account requests", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    public void approveAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        try {
        	System.out.println("ia m here");
            String role = session.getAttribute("role").toString();
            Long branchId = null;
            long adminId = (Long) session.getAttribute("userId");

            if (UserRole.ADMIN.name().equals(role)) {
            	 branchId = (Long) session.getAttribute("branchId");
                boolean sameBranch = requestService.isAdminInSameBranch(adminId, branchId);
                if (!sameBranch) {
                    logger.warning("Admin from different branch attempted approval. Admin ID: " + adminId);
                    ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN,
                            "Admins can only approve requests from their own branch.");
                    return;
                }
            }

            Request request = RequestParser.parseRequest(req, Request.class);
            long requestId = request.getId();
            System.out.println("here");

            boolean success = accountService.approveAccountRequest(requestId, adminId);
            if (success) {
                logger.info("Account approved by admin ID: " + adminId + " for request ID: " + requestId);
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Account approved and created.");
            } else {
                logger.warning("Account approval failed for request ID: " + requestId);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Approval failed.");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during account approval", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
    public void pending(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;
            Pagination payload = RequestParser.parseRequest(req, Pagination.class);


            long userId = (Long) session.getAttribute("userId");
            RequestStatus status = payload.getStatus(); 

            List<AccountRequest> pendingRequests = accountService.getPendingRequestsForUser(userId,status);

            if (pendingRequests != null && !pendingRequests.isEmpty()) {
                JSONArray jsonArray = new JSONArray(new Gson().toJson(pendingRequests));
                ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "No pending requests found for this user.");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching pending requests for user", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
    public void viewDetails(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        try {
            Request requestObj = RequestParser.parseRequest(req, Request.class);
            long requestId = requestObj.getId();

            AccountRequest request = accountService.getRequestDetailsById(requestId);

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
    public void multipleReject(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        try {
            long adminId = (long) session.getAttribute("adminId");
            String role = session.getAttribute("role").toString();
            Long branchId = null;

            if ("ADMIN".equalsIgnoreCase(role)) {
                Object branchObj = session.getAttribute("branchId");
                if (branchObj != null) {
                    branchId = (Long) branchObj;
                }
            }

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

                List<Long> failedIds = accountService.rejectMultipleRequests(requestIds, adminId, reason, role,branchId);

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
    public void statusCounts(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        try {
            long adminId = (long) session.getAttribute("adminId");
            String role = (String) session.getAttribute("role");

            Map<String, Long> counts = accountService.getRequestStatusCounts(role, adminId);
            String json = gson.toJson(counts);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, json);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch request status counts", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch request status counts");
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
           
            String role = session.getAttribute("role").toString();
            Long branchId = null;

            if ("ADMIN".equalsIgnoreCase(role)) {
                Object branchObj = session.getAttribute("branchId");
                if (branchObj != null) {
                    branchId = (Long) branchObj;
                }
            }
 
            boolean success = accountService.rejectUserRequest(requestId, adminId, reason,role,branchId);

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
    public void multipleApprove(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        try {
            long adminId = (long) session.getAttribute("adminId");
            String role = session.getAttribute("role").toString();
            Long branchId = null;

            if ("ADMIN".equalsIgnoreCase(role)) {
                Object branchObj = session.getAttribute("branchId");
                if (branchObj != null) {
                    branchId = (Long) branchObj;
                }
            }
            try (BufferedReader reader = req.getReader()) {
                String body = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                JSONArray jsonArray = new JSONArray(body); 

                List<Long> requestIds = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    requestIds.add(jsonArray.getLong(i));
                }

                List<Long> failedIds = accountService.approveMultipleRequests(requestIds, adminId, role,branchId);

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


}
