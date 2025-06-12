package com.bank.handler;

import com.bank.enums.UserRole;
import com.bank.models.Branch;
import com.bank.models.StatementRequest;
import com.bank.models.Transaction;
import com.bank.service.TransactionService;
import com.bank.service.impl.TransactionServiceImpl;
import com.bank.util.RequestParser;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;
import org.json.JSONArray;

import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionHandler {
    private final Logger logger = Logger.getLogger(TransactionHandler.class.getName());
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    public void deposit(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	 try {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

       
            Transaction transaction = RequestParser.parseRequest(req, Transaction.class);

//            if (transaction == null || transaction.getAccountId()!= null || transaction.getAmount()!= null) {
//                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing required fields.");
//                return;
//            }//need to be carried out util

            long accountId = transaction.getAccountId();
            BigDecimal amount = transaction.getAmount();
            long admin = (long) session.getAttribute("userId");

            transactionService.deposit(accountId, amount, admin);
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Deposit successful.");

        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    public void withdraw(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

        Transaction transaction = RequestParser.parseRequest(req, Transaction.class);

            long accountId = transaction.getAccountId();
            BigDecimal amount = transaction.getAmount();
            long admin = (long) session.getAttribute("userId");

            transactionService.withdraw(accountId, amount, admin);
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Withdrawal successful.");
        } catch (Exception e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    public void transfer(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	try {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;
        
        Transaction transaction =RequestParser.parseRequest(req, Transaction.class);
            long fromAccountId = transaction.getAccountId();
            long toAccountId = transaction.getTransactionAccountId();
            BigDecimal amount = transaction.getAmount();
            String user = session.getAttribute("username").toString();

            transactionService.transfer(fromAccountId, toAccountId, amount, user);
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Transfer successful.");
        } catch (Exception e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    public void statement(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	  try {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;

      
            StatementRequest payload = RequestParser.parseRequest(req,StatementRequest.class);

            long accountId = payload.getAccountId();
            String fromDateStr = payload.getFromDate();
            String toDateStr = payload.getToDate();

            long fromTimestamp = Instant.parse(fromDateStr + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(toDateStr + "T23:59:59Z").toEpochMilli();

            List<Transaction> transactions = transactionService.getStatementByDateRange(accountId, fromTimestamp, toTimestamp);

            JSONArray jsonArray = new JSONArray(gson.toJson(transactions));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);

        } catch (DateTimeParseException | NumberFormatException e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Invalid input format.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception during statement processing", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred.");
        }
    }

 
    
}

