package com.bank.handler;

import com.bank.service.TransactionService;
import com.bank.service.impl.TransactionServiceImpl;
import com.bank.util.AuthorizationUtil;
import com.google.gson.Gson;

import exception.QueryException;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AccountTransferHandler {
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    public void handleAccountTransfer(HttpServletRequest req, HttpServletResponse res) throws IOException, QueryException, SQLException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("role") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"error\":\"Unauthorized.\"}");
            return;
        }

        Map<String, Object> payload = gson.fromJson(req.getReader(), Map.class);
        long fromAccountId = ((Number) payload.get("accountId")).longValue();
        long toAccountId = ((Number) payload.get("transactionAccountId")).longValue();
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        String actor = session.getAttribute("username").toString();

        if (!AuthorizationUtil.canAccessAccount(session, fromAccountId)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.write("{\"error\":\"Access denied to source account.\"}");
            return;
        }

        try {
            transactionService.transfer(fromAccountId, toAccountId, amount, actor);
            out.write("{\"message\":\"Transfer successful.\"}");
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
