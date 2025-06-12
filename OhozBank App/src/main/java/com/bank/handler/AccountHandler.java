package com.bank.handler;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.enums.UserRole;
import com.bank.models.Account;
import com.bank.service.AccountService;
import com.bank.service.impl.AccountServiceImpl;
import com.bank.util.RequestParser;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;

import exception.QueryException;

public class AccountHandler {
    private final Logger logger = Logger.getLogger(AccountHandler.class.getName());
    private final AccountService accountService = new AccountServiceImpl();
    private final Gson gson = new Gson();
 

    public void edit(HttpServletRequest req, HttpServletResponse res) throws IOException {
       try {
    	HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;

      
        String role = session.getAttribute("role").toString();
        long adminId = (Long) session.getAttribute("userId");
        long adminBranchId = (Long) session.getAttribute("branchId");
        if (!("ADMIN".equals(role) || "SUPERADMIN".equals(role))) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Access denied. Insufficient privileges.");
            return;
        }
        
        Account account = RequestParser.parseRequest(req,Account.class);
        if (account.getAccountId()!=null) {
        	ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing accountId in request payload.");
        	return;
        }
        long accountId = account.getAccountId();
        if (UserRole.ADMIN.name().equals(role)) {
        	long accountBranchId = accountService.getBranchIdByAccountId(accountId);
        	if (adminBranchId != accountBranchId) {
        		ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Admins can only edit accounts within their own branch.");
        		return;
            }
        }

            boolean updated = accountService.updateAccountStatus(account, adminId);

            if (updated) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Account status updated successfully.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to update account status. Please check request data or account details.");
            }

        } catch (Exception e) {
            logger.severe("Account status update failed: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error during account status update.");
        }
    }

    public void balance(HttpServletRequest req, HttpServletResponse res) throws IOException, QueryException, SQLException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;

        Account account = RequestParser.parseRequest(req,Account.class);
        Long accountId = account.getAccountId();
        BigDecimal balance = accountService.getAccountBalance(accountId);
        if (balance != null) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("balance", balance);
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonObj);
        } else {
            ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Account not found.");
        }
    }
 

    public void get(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

            long accountId = Long.parseLong(req.getParameter("accountId"));
            Account account = accountService.getAccountById(accountId);

            if (account != null) {
                JSONObject jsonObject = new JSONObject(gson.toJson(account));
                ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonObject);
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Account not found");
            }
        } catch (Exception e) {
            logger.severe("Error fetching account: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    public void listBranch(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

            long branchId = Long.parseLong(req.getParameter("branchId"));
            List<Account> accounts = accountService.getAccountsByBranchId(branchId);
            JSONArray jsonArray = new JSONArray(gson.toJson(accounts));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);
        } catch (Exception e) {
            logger.severe("Error fetching accounts by branch: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    public void listAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSuperAdmin(session, res)) return;

            List<Account> accounts = accountService.getAllAccounts();
            JSONArray jsonArray = new JSONArray(gson.toJson(accounts));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);
        } catch (Exception e) {
            logger.severe("Error fetching all accounts: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }
}

