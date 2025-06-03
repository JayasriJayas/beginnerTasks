// File: com.bank.handler.AccountStatementHandler.java
package com.bank.handler;

import com.bank.models.StatementRequest;
import com.bank.models.Transaction;
import com.bank.service.TransactionService;
import com.bank.service.impl.TransactionServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;
import org.json.JSONArray;


import java.io.BufferedReader;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AccountStatementHandler {

    private final Logger logger = Logger.getLogger(AccountStatementHandler.class.getName());
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    public void handleAccountStatement(HttpServletRequest req, HttpServletResponse res) throws IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
            return;
        }
   
        try (BufferedReader reader = req.getReader()) {
            StatementRequest payload = gson.fromJson(reader, StatementRequest.class);
      
            long accountId = payload.getAccountId();
            String fromDateStr = payload.getFromDate();
            String toDateStr = payload.getToDate();

            long fromTimestamp = Instant.parse(fromDateStr + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(toDateStr + "T23:59:59Z").toEpochMilli();
            
            List<Transaction> transactions = transactionService.getStatementByDateRange(accountId, fromTimestamp, toTimestamp);

            JSONArray jsonArray = new JSONArray(new Gson().toJson(transactions));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);

        } catch (DateTimeParseException | NumberFormatException e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Invalid input format.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception during statement processing", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred.");
        }
    }
}
