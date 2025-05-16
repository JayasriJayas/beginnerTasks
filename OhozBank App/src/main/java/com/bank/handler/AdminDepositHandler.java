package com.bank.handler;

import com.bank.service.TransactionService;
import com.bank.service.impl.TransactionServiceImpl;
import com.google.gson.Gson;

import javax.servlet.http.*;
import java.io.*;
import java.util.Map;

public class AdminDepositHandler {
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    public void handleDeposit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.write("{\"error\":\"Only admins can deposit.\"}");
            return;
        }

        Map<String, Object> payload = gson.fromJson(req.getReader(), Map.class);
        long accountId = ((Double) payload.get("accountId")).longValue();
        double amount = (Double) payload.get("amount");
        String admin = session.getAttribute("username").toString();

        try {
            transactionService.deposit(accountId, amount, admin);
            out.write("{\"message\":\"Deposit successful.\"}");
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
