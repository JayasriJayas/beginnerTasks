package com.bank.handler;

import com.bank.dao.UserDAO;
import com.bank.dao.impl.UserDAOImpl;
import com.bank.enums.UserRole;
import com.bank.models.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginHandler {

    private static final Logger LOGGER = Logger.getLogger(LoginHandler.class.getName());
    private final UserDAO userDAO;

    public LoginHandler() {
        this.userDAO = new UserDAOImpl();
    }

    public void handleLogin(HttpServletRequest req, HttpServletResponse res) {
        try {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            PrintWriter out = res.getWriter();
            res.setContentType("text/html");

            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                out.println("Username and password must not be empty.");
                return;
            }

            User user = userDAO.authenticateUser(username, password);
            if (user != null) {
                UserRole role = getRoleById(user.getRoleId()); // Convert int to enum
                HttpSession session = req.getSession();
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", role.name()); // Store enum name for filter use

                LOGGER.info("User " + username + " logged in as " + role);
                res.sendRedirect("dashboard");
            } else {
                out.println("Invalid username or password, or account not approved.");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Login error", e);
            try {
                res.getWriter().println("System error. Please try again later.");
            } catch (Exception ignored) {}
        }
    }

    private UserRole getRoleById(int roleId) {
        switch (roleId) {
            case 1: return UserRole.SUPER_ADMIN;
            case 2: return UserRole.ADMIN;
            case 3: return UserRole.USER;
            default: return UserRole.PUBLIC;
        }
    }
}
