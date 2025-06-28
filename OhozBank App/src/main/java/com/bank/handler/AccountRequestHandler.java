package com.bank.handler;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

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
            UserRole role = UserRole.valueOf(session.getAttribute("role").toString().toUpperCase());
            PaginatedResponse<AccountRequest> requests;
            

            if (role == UserRole.SUPERADMIN) {
                requests = accountService.getAllRequests(fromTimestamp, toTimestamp, page, size);
            } else if (role == UserRole.ADMIN) {
                long adminId = (long) session.getAttribute("adminId");
                requests = accountService.getRequestsByAdminBranch(adminId,fromTimestamp, toTimestamp, page, size);
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
            String role = session.getAttribute("role").toString();
            long branchId = (Long) session.getAttribute("branchId");
            long adminId = (Long) session.getAttribute("userId");

            if (UserRole.ADMIN.name().equals(role)) {
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

            long userId = (Long) session.getAttribute("userId");

            List<AccountRequest> pendingRequests = accountService.getPendingRequestsForUser(userId);

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


}
