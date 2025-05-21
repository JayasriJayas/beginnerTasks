package com.bank.handler;

import com.bank.enums.RequestStatus;
import com.bank.enums.UserRole;
import com.bank.models.Request;
import com.bank.service.UserService;
import com.bank.service.impl.UserServiceImpl;
import com.bank.dao.AdminDAO;
import com.bank.dao.RequestDAO;
import com.bank.dao.impl.AdminDAOImpl;
import com.bank.dao.impl.RequestDAOImpl;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class AdminApproveHandler {

    private final UserService userService = new UserServiceImpl();
    private final AdminDAO adminDAO = new AdminDAOImpl();
    private final RequestDAO requestDAO = new RequestDAOImpl();
    private final Gson gson = new Gson();

    public void handleAdminApprove(HttpServletRequest req, HttpServletResponse res) {
        res.setContentType("application/json");

        try (PrintWriter out = res.getWriter()) {

          
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("role") == null || session.getAttribute("adminId") == null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"error\":\"Unauthorized. Admin session missing.\"}");
                return;
            }

           BufferedReader reader = req.getReader();
            Map<String, Object> requestBody = gson.fromJson(reader, Map.class);
           

            Object requestIdObj = requestBody.get("requestId");
            if (requestIdObj == null) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Missing requestId in JSON body.\"}");
                return;
            }
            long requestId = Long.parseLong(requestIdObj.toString());
            long adminId = (long) session.getAttribute("adminId");
            String role = session.getAttribute("role").toString();

            Request request = requestDAO.getRequestById(requestId);
          
            if (request == null || !request.getStatus().equals(RequestStatus.PENDING)) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Request not found or not pending.\"}");
                return;
            }

            if (UserRole.ADMIN.name().equals(role)) {
                long adminBranchId = adminDAO.getBranchIdByAdminId(adminId);
                if (adminBranchId != request.getBranchId()) {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.write("{\"error\":\"Admins can only approve requests from their own branch.\"}");
                    return;
                }
            }
           
            boolean success = userService.approveUserRequest(requestId, adminId);
            Map<String, String> result = new HashMap<>();
            if (success) {
                res.setStatus(HttpServletResponse.SC_OK);
                result.put("message", "User approved and account created.");
            } else {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("error", "Approval failed.");
            }

            out.write(gson.toJson(result));

        } catch (Exception e) {
            e.printStackTrace();
            try {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.getWriter().write("{\"error\":\"Internal server error.\"}");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
