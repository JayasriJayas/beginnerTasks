package com.bank.handler;

import com.bank.service.AccountService;
import com.bank.service.impl.AccountServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

import exception.QueryException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AccountBalanceHandler {
    private final AccountService accountService = new AccountServiceImpl();
    private final Gson gson = new Gson();

    public void handleAccountBalance(HttpServletRequest req, HttpServletResponse res) throws IOException, QueryException, SQLException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
            return;
        }
        
        BufferedReader reader = req.getReader();
        Map<String, Object> payload = gson.fromJson(reader, Map.class);

        Long accountId = ((Number) payload.get("accountId")).longValue();

        

        BigDecimal balance = accountService.getAccountBalance(accountId);
        if (balance != null) {
        	JSONObject jsonObj = new JSONObject();
        	jsonObj.put("balance", balance);
        	ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonObj);

        } else {
            ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Account not found.");
        }
    }
}
