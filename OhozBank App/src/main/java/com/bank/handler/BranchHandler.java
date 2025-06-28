package com.bank.handler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.enums.UserRole;
import com.bank.factory.ServiceFactory;
import com.bank.models.Branch;
import com.bank.models.PaginatedResponse;
import com.bank.models.Pagination;
import com.bank.service.BranchService;
import com.bank.util.BranchValidator;
import com.bank.util.PaginationUtil;
import com.bank.util.RequestParser;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;

public class BranchHandler {

    private static final Logger logger = Logger.getLogger(BranchHandler.class.getName());
    private final BranchService branchService = ServiceFactory.getBranchService();
    private static final Gson gson = new Gson();

    public void add(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSuperAdmin(session, res)) return;

            Branch branch = RequestParser.parseRequest(req, Branch.class);
            if (!BranchValidator.isValidBranch(branch, res)) return;

            
            Branch existing = branchService.getBranchByIfscCode(branch.getIfscCode());
            if (existing != null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_CONFLICT, "IFSC code already exists.");
                return;
            }

            long superadminId = (Long) session.getAttribute("adminId");

            boolean success = branchService.addBranch(branch, superadminId);
            if (success) {
                logger.info("Branch added by superadmin ID: " + superadminId);
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Branch added successfully");
            } else {
                logger.warning("Failed to add branch by superadmin ID: " + superadminId);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to add branch");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Branch addition failed", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    public void edit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSuperAdmin(session, res)) return;

            long userId = (Long) session.getAttribute("userId");
            Branch branch = RequestParser.parseRequest(req, Branch.class);
            if (!BranchValidator.isValidBranch(branch, res)) return;

            boolean updated = branchService.updateBranchDetails(userId, branch);
            if (updated) {
                logger.info("Branch updated by superadmin ID: " + userId);
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Branch updated successfully.");
            } else {
                logger.warning("Branch update failed by superadmin ID: " + userId);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to update branch.");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Branch update failed", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }

    public void get(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

            Branch requestBranch = RequestParser.parseRequest(req, Branch.class);
            Long idParam = requestBranch.getBranchId();

            if (idParam == null) {
                logger.warning("Branch ID is missing in get request.");
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "branchId parameter is required");
                return;
            }

            String role = (String) session.getAttribute("role");
            Long sessionBranchId = (Long) session.getAttribute("branchId");

            if (!UserRole.SUPERADMIN.equals(role) && !idParam.equals(sessionBranchId)) {
                logger.warning("Admin tried to access branch not assigned to them. Session branch: "
                        + sessionBranchId + ", Requested: " + idParam);
                ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN,
                        "Access denied: You can only view your own branch details.");
                return;
            }

            Branch branchObj = branchService.getBranchById(idParam);
            if (branchObj != null) {
                logger.info("Branch details fetched for branchId: " + idParam);
                JSONObject json = new JSONObject(gson.toJson(branchObj));
                ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, json);
            } else {
                logger.warning("Branch not found for ID: " + idParam);
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Branch not found");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting branch", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    public void getAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSuperAdmin(session, res)) return;
            
            Pagination payload = RequestParser.parseRequest(req, Pagination.class);
            if (payload == null ) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing required date fields.");
                return;
            }
          

            int page = PaginationUtil.validatePageNumber((payload.getPageNumber()));      
            int size = PaginationUtil.validatePageSize(payload.getPageSize());   
            PaginatedResponse<Branch>branches = branchService.getAllBranches( page, size	);
            String response = gson.toJson(branches);
            logger.info("All branches listed by superadmin.");
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, response);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error listing branches", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

   
}
