package com.bank.handler;

import com.bank.service.TransactionService;
import com.bank.service.impl.TransactionServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class AccountWithdrawHandler {
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    public void handleAccountWithdraw(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Only admins can withdraw.");
            return;
        }

        Map<String, Object> payload = gson.fromJson(req.getReader(), Map.class);

        long accountId = ((Number) payload.get("accountId")).longValue();
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        long admin = (long)session.getAttribute("userId");

        try {
            transactionService.withdraw(accountId, amount, admin);
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Withdrawal successful.");
        } catch (Exception e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
