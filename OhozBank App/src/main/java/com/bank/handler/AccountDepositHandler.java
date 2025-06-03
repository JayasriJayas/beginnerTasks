package com.bank.handler;

import com.bank.service.TransactionService;
import com.bank.service.impl.TransactionServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AccountDepositHandler {
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    public void handleAccountDeposit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Only admins can deposit.");
            return;
        }

        try {
            Map<String, Object> payload = gson.fromJson(req.getReader(), Map.class);
            
            if (payload == null || !payload.containsKey("accountId") || !payload.containsKey("amount")) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing required fields.");
                return;
            }

            long accountId = ((Double) payload.get("accountId")).longValue();
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            long admin = (long)session.getAttribute("userId");

            transactionService.deposit(accountId, amount, admin);
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Deposit successful.");

        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
