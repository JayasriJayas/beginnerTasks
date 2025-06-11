package com.bank.handler;

import com.bank.enums.UserRole;
import com.bank.service.AccountService;
import com.bank.service.impl.AccountServiceImpl;
import com.bank.service.RequestService; // Assuming RequestService for other functionalities, but not directly used for branch check here
import com.bank.service.impl.RequestServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class EditAccountHandler {
    private final Logger logger = Logger.getLogger(EditAccountHandler.class.getName());
    private final AccountService accountService = new AccountServiceImpl();
 

    private final Gson gson = new Gson();

    public void handleEditAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
     
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userId") == null || session.getAttribute("role") == null) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Please log in.");
            return;
        }

        String role = session.getAttribute("role").toString();
        long adminId = (Long) session.getAttribute("userId");
        long adminBranchId = (Long) session.getAttribute("branchId");
        System.out.println(adminBranchId);

        if (!("ADMIN".equals(role) || "SUPERADMIN".equals(role))) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Access denied. Insufficient privileges.");
            return;
        }
       
        try (BufferedReader reader = req.getReader()) {
            Map<String, Object> payload = gson.fromJson(reader, Map.class);

           
            if (!payload.containsKey("accountId")) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing accountId in request payload.");
                return;
            }
            long accountId = ((Number) payload.get("accountId")).longValue();


            if (UserRole.ADMIN.name().equals(role)) {
                long accountBranchId = accountService.getBranchIdByAccountId(accountId);
                if (accountBranchId == -1) {
                    ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Account not found or invalid for branch check.");
                    return;
                }
             
                if (adminBranchId != accountBranchId) {
                    ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Admins can only edit accounts within their own branch.");
                    return;
                }
            }

      
            boolean updated = accountService.updateAccountStatus(payload, adminId);

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
}
