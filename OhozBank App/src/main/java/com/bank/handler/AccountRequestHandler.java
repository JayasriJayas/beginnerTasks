package com.bank.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.bank.service.AccountService;
import com.bank.service.impl.AccountServiceImpl;

import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

public class AccountRequestHandler {

    private final AccountService accountService = new AccountServiceImpl();

    public void handleAccountRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Please log in.");
            return;
        }

        long userId = (Long) session.getAttribute("userId");

        try (BufferedReader reader = req.getReader()) {
            Map<String, Object> payload = new Gson().fromJson(reader, Map.class);
            long branchId = ((Number) payload.get("branchId")).longValue();
            

            boolean success = accountService.createAccountRequest(userId, branchId);

            if (success) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Account request submitted.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Request failed.");
            }
        } catch (Exception e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
}
