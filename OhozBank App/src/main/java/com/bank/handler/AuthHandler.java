package com.bank.handler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bank.enums.UserRole;
import com.bank.factory.ServiceFactory;
import com.bank.models.User;
import com.bank.service.AuthenticationService;
import com.bank.service.UserService;
import com.bank.util.RequestParser;
import com.bank.util.ResponseUtil;

public class AuthHandler {

    private static final Logger logger = Logger.getLogger(AuthHandler.class.getName());

    private final AuthenticationService authenticationService = ServiceFactory.getAuthService();
    private final UserService userService = ServiceFactory.getUserService();

    public void login(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            User loginData = RequestParser.parseRequest(req, User.class);

            if (loginData == null || loginData.getUsername() == null || loginData.getPassword() == null ||
                loginData.getUsername().isEmpty() || loginData.getPassword().isEmpty()) {
                logger.warning("Login failed: Missing credentials");
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Username and password are required.");
                return;
            }

            User user = authenticationService.login(loginData.getUsername(), loginData.getPassword());

            if (user == null) {
                logger.warning("Login failed: Invalid credentials for username " + loginData.getUsername());
                ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password.");
                return;
            }

            HttpSession session = req.getSession(true);
            UserRole role = userService.getUserRole(user);

            session.setAttribute("role", role.name());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());

            if (!UserRole.SUPERADMIN.name().equals(role.name())) {
                session.setAttribute("branchId", user.getBranchId());
            }

            if (userService.isAdmin(user)) {
                session.setAttribute("adminId", user.getUserId());
            }

            logger.info("Login successful for user: " + user.getUsername() + " as " + role.name());
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Login successful as " + role.name());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Login error", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
        }
    }

    public void logout(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);

        if (session != null) {
            String username = (String) session.getAttribute("username");
            session.invalidate();
            logger.info("Logout successful for user: " + username);
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Logout successful.");
        } else {
            logger.warning("Logout failed: No active session");
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "No active session found.");
        }
    }
}
