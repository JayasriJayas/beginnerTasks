package com.bank.handler;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import com.bank.enums.UserRole;
import com.bank.models.AccountRequest;
import com.bank.service.AccountRequestService;
import com.bank.service.impl.AccountRequestServiceImpl;
import com.bank.util.RequestParser;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;

public class AccountRequestHandler {
	
    private final Gson gson = new Gson();
    private final AccountRequestService accountService = new AccountRequestServiceImpl();
    private final Logger logger = Logger.getLogger(AccountRequestHandler.class.getName());

    public void request(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	try {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;


        long userId = (Long) session.getAttribute("userId");

      AccountRequest request = RequestParser.parseRequest(req, AccountRequest.class); 
            long branchId = request.getBranchId();
            

            boolean success = accountService.createAccountRequest(userId, branchId);

            if (success) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Account request submitted.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Request failed.");
            }
        } catch (Exception e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }


    public void list(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("role") == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Login required");
                return;
            }

            UserRole role = UserRole.valueOf(session.getAttribute("role").toString().toUpperCase());
            List<AccountRequest> requests;

            if (role == UserRole.SUPERADMIN) {
                requests = accountService.getAllRequests();
            } else if (role == UserRole.ADMIN) {
                long adminId = (long) session.getAttribute("adminId");
                requests = accountService.getRequestsByAdminBranch(adminId);
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Unauthorized access");
                return;
            }

            JSONArray jsonArray = new JSONArray(gson.toJson(requests));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);

        } catch (Exception e) {
            logger.severe("Failed to retrieve requests: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

}
