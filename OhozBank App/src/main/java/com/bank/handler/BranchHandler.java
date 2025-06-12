package com.bank.handler;

import com.bank.enums.UserRole;
import com.bank.models.Branch;
import com.bank.models.Request;
import com.bank.service.AdminService;
import com.bank.service.BranchService;
import com.bank.service.impl.AdminServiceImpl;
import com.bank.service.impl.BranchServiceImpl;
import com.bank.util.RequestParser;
import com.bank.util.RequestValidator;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

public class BranchHandler {
    private final Logger logger = Logger.getLogger(BranchHandler.class.getName());
    private final AdminService adminService = new AdminServiceImpl();
    private final BranchService branchService = new BranchServiceImpl();
    private final Gson gson = new Gson();

    public void edit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSuperAdmin(session, res)) return;

            long userId = (Long) session.getAttribute("userId");
            Branch branch = RequestParser.parseRequest(req,Branch.class);
            boolean updated = adminService.updateBranchDetails(userId, branch);

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

    public void add(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSuperAdmin(session, res)) return;

            Branch branch = RequestParser.parseRequest(req,Branch.class);
            String validateError = validate(branch);
            if (validateError != null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, validateError);
                return;
            }

            long superadminId = (long) session.getAttribute("adminId");
            boolean success = branchService.addBranch(branch, superadminId);
            if (success) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Branch added successfully");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to add branch");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }


    private String validate(Branch branch) {
        return RequestValidator.validateBranchFields(branch);
    }

    
}
