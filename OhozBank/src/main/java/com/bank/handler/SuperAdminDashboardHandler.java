package com.bank.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SuperAdminDashboardHandler {

    public void handleSuperAdmin(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/superadmin-dashboard.jsp").forward(req, res);
    }
}
