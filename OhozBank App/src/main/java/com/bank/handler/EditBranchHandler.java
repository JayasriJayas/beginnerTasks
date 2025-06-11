package com.bank.handler;

import com.bank.service.AdminService;
import com.bank.service.impl.AdminServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class EditBranchHandler {
    private final Logger logger = Logger.getLogger(EditBranchHandler.class.getName());
    private final AdminService adminService = new AdminServiceImpl();
    private final Gson gson = new Gson();

    public void handleEditBranch(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !"SUPERADMIN".equals(session.getAttribute("role"))) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
            return;
        }
        long userId = (Long)session.getAttribute("userId");
        try (BufferedReader reader = req.getReader()) {
            Map<String, Object> payload = gson.fromJson(reader, Map.class);
            boolean updated = adminService.updateBranchDetails(userId,payload);

            if (updated) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Branch updated successfully.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to update branch.");
            }

        } catch (Exception e) {
            logger.severe("Branch update failed: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
}
