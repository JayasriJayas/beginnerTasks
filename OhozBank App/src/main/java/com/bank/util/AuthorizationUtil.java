package com.bank.util;

import com.bank.dao.AccountDAO;
import com.bank.dao.impl.AccountDAOImpl;

import exception.QueryException;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

public class AuthorizationUtil {
    private static final AccountDAO accountDAO = new AccountDAOImpl();

    public static boolean canAccessAccount(HttpSession session, long accountId) throws QueryException, SQLException {
        if (session == null || session.getAttribute("role") == null) return false;

        String role = session.getAttribute("role").toString();
        Long sessionUserId = (Long) session.getAttribute("userId");
        Long sessionBranchId = session.getAttribute("branchId") != null ? (Long) session.getAttribute("branchId") : null;
        Long accountBranchId = accountDAO.getBranchIdByAccountId(accountId);

        if (role.equals("SUPER_ADMIN")) return true;
        if (role.equals("USER") && sessionUserId != null) {
        
            long accountUserId = accountDAO.getAccountById(accountId).getUserId();
            return sessionUserId == accountUserId;
        }
        if (role.equals("ADMIN") && sessionBranchId != null) {
            return sessionBranchId == accountBranchId;
        }
        return false;
    }
}


