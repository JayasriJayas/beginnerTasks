package com.bank.util;

import com.bank.enums.UserRole;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionUtil {

    public static boolean isSessionAvailable(HttpSession session, HttpServletResponse res) throws IOException {
        if (session == null || session.getAttribute("role") == null) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
            return false;
        }
        return true;
    }

    public static boolean isAdminOrSuperAdmin(HttpSession session, HttpServletResponse res) throws IOException {
        if (!isSessionAvailable(session, res)) return false;

        String role = (String) session.getAttribute("role");
        if (!UserRole.ADMIN.name().equals(role) && !UserRole.SUPERADMIN.name().equals(role)) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN,
                    "Access denied. Only Admin or Super Admin is allowed.");
            return false;
        }
        return true;
    }

    public static boolean isSuperAdmin(HttpSession session, HttpServletResponse res) throws IOException {
        if (!isSessionAvailable(session, res)) return false;

        if (!UserRole.SUPERADMIN.name().equals(session.getAttribute("role"))) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN,
                    "Access denied. Only Super Admin is allowed.");
            return false;
        }
        return true;
    }
}
