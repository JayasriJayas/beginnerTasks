package com.bank.handler;

import com.bank.enums.UserRole;
import com.bank.service.RequestService;
import com.bank.service.impl.RequestServiceImpl;
import com.bank.service.AccountService;
import com.bank.service.impl.AccountServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;


public class AccountApproveHandler {

    private final AccountService accountService = new AccountServiceImpl();
    private final RequestService requestService = new RequestServiceImpl();
    private final Gson gson = new Gson();

    public void handleAccountApprove(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null || session.getAttribute("role") == null) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
            return;
        }

        String role = session.getAttribute("role").toString();
        long branchId = (Long)session.getAttribute("branchId");
        long adminId =(Long) session.getAttribute("userId");

        if (!("ADMIN".equals(role) || "SUPERADMIN".equals(role))) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Access denied.");
            return;
        }
        if (UserRole.ADMIN.name().equals(role)) {
            boolean sameBranch = requestService.isAdminInSameBranch(adminId, branchId);
            if (!sameBranch) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Admins can only approve requests from their own branch.");
                return;
            }
        }

      
        try (BufferedReader reader = req.getReader()) {
            Map<String, Object> payload = gson.fromJson(reader, Map.class);
            long requestId = ((Number) payload.get("requestId")).longValue();

            boolean success = accountService.approveAccountRequest(requestId, adminId);

            if (success) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Account approved and created.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Approval failed.");
            }
        } catch (Exception e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
}

