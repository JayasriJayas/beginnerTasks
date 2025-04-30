package com.bank.servlet;

import com.bank.handler.LoginHandler;
import com.bank.handler.RejectHandler;
import com.bank.handler.SignupHandler;
import com.bank.handler.ApprovalHandler;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class ControllerServlet extends HttpServlet {

    private final LoginHandler loginHandler = new LoginHandler();
    private final SignupHandler signupHandler = new SignupHandler();
    private final ApprovalHandler approvalHandler = new ApprovalHandler();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = (String) req.getAttribute("action");

        switch (action) {
            case "/signup":
                signupHandler.handleSignup(req, res);
                break;

            case "/login":
                loginHandler.handleLogin(req, res);
                break;

            case "/approve":
                approvalHandler.handleApproval(req, res);
                break;

            case "/dashboard":
                req.getRequestDispatcher("/dashboard.jsp").forward(req, res);
                break;
            case "/reject":
                new RejectHandler().handleRejection(req, res);
                break;


            default:
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown API");
        }
    }
}
