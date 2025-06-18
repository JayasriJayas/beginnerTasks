package com.bank.handler;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.models.Branch;
import com.bank.service.AdminService;
import com.bank.service.BranchService;
import com.bank.service.impl.AdminServiceImpl;
import com.bank.service.impl.BranchServiceImpl;
import com.bank.util.RequestParser;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;

public class BranchHandler {
    private final Logger logger = Logger.getLogger(BranchHandler.class.getName());
    private final AdminService adminService = new AdminServiceImpl();
    private final BranchService branchService = new BranchServiceImpl();
    Gson gson = new Gson();


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
    public void get(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

            String idParam = req.getParameter("branchId");
            if (idParam == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "branchId parameter is required");
                return;
            }

            long branchId = Long.parseLong(idParam);
            Branch branch = branchService.getBranchById(branchId);

            if (branch != null) {
                JSONObject json = new JSONObject(gson.toJson(branch));
                ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, json);
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Branch not found");
            }

        } catch (Exception e) {
            logger.severe("Error getting branch: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    public void getAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

            List<Branch> branches = branchService.getAllBranches();
            JSONArray jsonArray = new JSONArray(gson.toJson(branches));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);

        } catch (Exception e) {
            logger.severe("Error listing branches: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    
}
