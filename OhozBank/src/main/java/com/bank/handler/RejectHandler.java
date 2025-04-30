package com.bank.handler;

import com.bank.service.UserService;
import com.bank.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class RejectHandler {

    private final UserService userService = new UserServiceImpl();

    public void handleRejection(HttpServletRequest req, HttpServletResponse res) {
        try {
            String username = req.getParameter("username");

            PrintWriter out = res.getWriter();
            res.setContentType("text/html");

            if (username == null || username.trim().isEmpty()) {
                out.println("Username is required to reject.");
                return;
            }

            boolean rejected = userService.rejectRequest(username);
            if (rejected) {
                out.println("User '" + username + "' has been rejected.");
            } else {
                out.println("Rejection failed or request not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                res.getWriter().println("Rejection process failed due to server error.");
            } catch (Exception ignored) {}
        }
    }
}
