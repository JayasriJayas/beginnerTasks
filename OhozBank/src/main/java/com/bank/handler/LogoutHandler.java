package com.bank.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutHandler {
    public void handleLogout(HttpServletRequest req, HttpServletResponse res) {
        try {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            res.sendRedirect("login.jsp"); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
