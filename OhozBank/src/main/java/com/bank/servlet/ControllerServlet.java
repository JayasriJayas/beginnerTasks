package com.bank.servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bank.handler.AdminDashboardHandler;
import com.bank.handler.ApprovalHandler;
import com.bank.handler.LoginHandler;
import com.bank.handler.LogoutHandler;
import com.bank.handler.RejectHandler;
import com.bank.handler.SignupHandler;
import com.bank.handler.SuperAdminDashboardHandler;
import com.bank.handler.UserDashboardHandler;

public class ControllerServlet extends HttpServlet {

   @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getServletPath();

        switch (action) {
            case "/signup":
                new SignupHandler().handleSignup(req, res);
                break;

            case "/login":
                new LoginHandler().handleLogin(req, res);
                break;

            case "/admin/approve":
                if (hasRole(req, "ADMIN")) {
                    new ApprovalHandler().handleApproval(req, res);
                } else {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;

            case "/admin/reject":
                if (hasRole(req, "ADMIN")) {
                    new RejectHandler().handleRejection(req, res);
                } else {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;

            default:
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid POST route: " + action);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getServletPath(); 

        switch (action) {
            case "/logout":
                new LogoutHandler().handleLogout(req, res);
                break;

            case "/user/dashboard":
                if (hasRole(req, "USER")) {
                    new UserDashboardHandler().handleDashboard(req, res);
                } else {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;

            case "/admin/dashboard":
                if (hasRole(req, "ADMIN")) {
                    new AdminDashboardHandler().handleAdmin(req, res);
                } else {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;

            case "/admin/requests":
                if (hasRole(req, "ADMIN")) {
                    new AdminDashboardHandler().handleAdmin(req, res);
                } else {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;

            case "/superadmin/dashboard":
                if (hasRole(req, "SUPER_ADMIN")) {
                    new SuperAdminDashboardHandler().handleSuperAdmin(req, res);
                } else {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
                break;

            default:
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid GET route: " + action);
        }
    }
    private boolean hasRole(HttpServletRequest req, String expectedRole) {
        String role = (String) req.getSession().getAttribute("userRole");
        return role != null && role.equalsIgnoreCase(expectedRole);
    }
}
