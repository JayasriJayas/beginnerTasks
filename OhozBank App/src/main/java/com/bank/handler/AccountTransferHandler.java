package com.bank.handler;

import com.bank.service.TransactionService;

import com.bank.service.impl.TransactionServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

import exception.QueryException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

public class AccountTransferHandler {
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    public void handleAccountTransfer(HttpServletRequest req, HttpServletResponse res)
            throws IOException, QueryException, SQLException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("role") == null) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized.");
            return;
        }

        Map<String, Object> payload = gson.fromJson(req.getReader(), Map.class);
        long fromAccountId = ((Number) payload.get("accountId")).longValue();
        long toAccountId = ((Number) payload.get("transactionAccountId")).longValue();
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        String actor = session.getAttribute("username").toString();

        try {
            transactionService.transfer(fromAccountId, toAccountId, amount, actor);
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK,"Transfer successful.");
        } catch (Exception e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
