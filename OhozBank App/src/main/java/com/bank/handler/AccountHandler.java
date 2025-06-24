package com.bank.handler;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.enums.UserRole;
import com.bank.factory.ServiceFactory;
import com.bank.models.Account;
import com.bank.models.Branch;
import com.bank.service.AccountService;
import com.bank.util.RequestParser;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;

public class AccountHandler {

    private static final Logger logger = Logger.getLogger(AccountHandler.class.getName());
    private final AccountService accountService = ServiceFactory.getAccountService();
    private final Gson gson = new Gson();

    public void edit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            String role = session.getAttribute("role").toString();
            long adminId = (Long) session.getAttribute("userId");
            long adminBranchId = (Long) session.getAttribute("branchId");

            if (!("ADMIN".equals(role) || "SUPERADMIN".equals(role))) {
                logger.warning("Unauthorized account edit attempt by userId: " + adminId);
                ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                return;
            }

            Account account = RequestParser.parseRequest(req, Account.class);
            long accountId = account.getAccountId();

            if (UserRole.ADMIN.name().equals(role)) {
                long accountBranchId = accountService.getBranchIdByAccountId(accountId);
                if (adminBranchId != accountBranchId) {
                    logger.warning("Admin " + adminId + " attempted to edit account in different branch");
                    ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN,
                            "Admins can only edit accounts within their own branch.");
                    return;
                }
            }

            boolean updated = accountService.updateAccountStatus(account, adminId);
            if (updated) {
                logger.info("Account status updated by userId: " + adminId + ", accountId: " + accountId);
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Account status updated successfully.");
            } else {
                logger.warning("Failed to update account status for accountId: " + accountId);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST,
                        "Failed to update account status.");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Account status update failed", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Server error during account status update.");
        }
    }

    public void balance(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            Account account = RequestParser.parseRequest(req, Account.class);
            Long accountId = account.getAccountId();

            BigDecimal balance = accountService.getAccountBalance(accountId);
            if (balance != null) {
                JSONObject json = new JSONObject();
                json.put("balance", balance);
                ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, json);
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Account not found.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching account balance", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }

    public void get(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            Account accountObj = RequestParser.parseRequest(req, Account.class);
            Long accountId = accountObj.getAccountId();

            Account account = accountService.getAccountById(accountId);
            if (account != null) {
                JSONObject json = new JSONObject(gson.toJson(account));
                ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, json);
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Account not found");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching account", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    public void listBranch(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

            Branch branch = RequestParser.parseRequest(req, Branch.class);
            Long branchId = branch.getBranchId();

            List<Account> accounts = accountService.getAccountsByBranchId(branchId);
            JSONArray jsonArray = new JSONArray(gson.toJson(accounts));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching accounts by branch", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    public void getAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSuperAdmin(session, res)) return;

            List<Account> accounts = accountService.getAllAccounts();
            JSONArray jsonArray = new JSONArray(gson.toJson(accounts));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching all accounts", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    public void getAccounts(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            Long userId = (Long) session.getAttribute("userId");
            List<Account> accounts = accountService.getAccountsByUserId(userId);

            if (accounts == null || accounts.isEmpty()) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "No accounts found for user.");
                return;
            }

            JSONArray jsonArray = new JSONArray(gson.toJson(accounts));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching user accounts", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }
    public void totalBalance(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            long userId = (Long) session.getAttribute("userId");

            BigDecimal totalBalance = accountService.getTotalBalanceByUser(userId);

            JSONObject json = new JSONObject();
            json.put("totalBalance", totalBalance);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, json);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting total balance", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }


}
