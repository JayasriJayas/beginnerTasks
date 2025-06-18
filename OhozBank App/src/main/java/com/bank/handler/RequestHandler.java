package com.bank.handler;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import com.bank.enums.RequestStatus;
import com.bank.enums.UserRole;
import com.bank.models.Request;
import com.bank.service.RequestService;
import com.bank.service.impl.RequestServiceImpl;
import com.bank.util.RequestParser;
import com.bank.util.RequestValidator;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RequestHandler {

    private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());
    private final RequestService requestService = new RequestServiceImpl();
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create();

    public void signup(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Request userRequest = RequestParser.parseRequest(req,Request.class);
            String validateError = validate(userRequest);

            if (validateError != null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, validateError);
                return;
            }

            boolean registered = requestService.registerRequest(userRequest);

            if (registered) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK,
                        "Signup request submitted. Awaiting approval.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Signup failed. Try again later.");
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during signup", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Server error or invalid input.");
        }
    }

    public void list(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;

        String role = (String) session.getAttribute("role");
        Long adminId = (Long) session.getAttribute("adminId");

        try {
            List<Request> requestList = requestService.getRequestList(role, adminId);
            JSONArray jsonArray = new JSONArray(gson.toJson(requestList));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching request list", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
    public void approve(HttpServletRequest req, HttpServletResponse res) {
        try {
        
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

            Request request = RequestParser.parseRequest(req, Request.class);
          
            long requestId = request.getId();
    

            
            long adminId = (long) session.getAttribute("adminId");
            String role = session.getAttribute("role").toString();
            Request requestObj = requestService.getRequestById(requestId);
            if (requestObj == null || !requestObj.getStatus().equals(RequestStatus.PENDING)) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Request not found or not pending.");
                return;
            }

            if (UserRole.ADMIN.name().equals(role)) {
                boolean sameBranch = requestService.isAdminInSameBranch(adminId, request.getBranchId());
                if (!sameBranch) {
                    ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Admins can only approve requests from their own branch.");
                    return;
                }
            }

            boolean success = requestService.approveUserRequest(requestId, adminId);
            if (success) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "User approved and account created.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Approval failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            } catch (IOException ex) {
                ex.printStackTrace(); 
            }
        }
    }
    

   
    private String validate(Request request) {
        return RequestValidator.validateSignupFields(request);
    }
}
