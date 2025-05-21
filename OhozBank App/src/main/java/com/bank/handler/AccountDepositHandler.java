package com.bank.handler;

import com.bank.service.TransactionService;
import com.bank.service.impl.TransactionServiceImpl;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AccountDepositHandler {
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    public void handleAccountDeposit(HttpServletRequest req, HttpServletResponse res) throws IOException {
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
        BigDecimal amount = new BigDecimal(payload.get("amount").toString()); 
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
