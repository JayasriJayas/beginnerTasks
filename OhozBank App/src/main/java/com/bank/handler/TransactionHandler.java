package com.bank.handler;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bank.enums.UserRole;
import com.bank.factory.ServiceFactory;
import com.bank.models.PaginatedResponse;
import com.bank.models.StatementRequest;
import com.bank.models.Transaction;
import com.bank.service.TransactionService;
import com.bank.util.PaginationUtil;
import com.bank.util.RequestParser;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.bank.util.TransactionValidator;
import com.google.gson.Gson;

public class TransactionHandler {
    private static final Logger logger = Logger.getLogger(TransactionHandler.class.getName());
    private final TransactionService transactionService = ServiceFactory.getTransactionService();
    private final Gson gson = new Gson();

    public void deposit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;
            
            Transaction transaction = RequestParser.parseRequest(req, Transaction.class);
            String error = TransactionValidator.validateDepositOrWithdraw(transaction);
            if (error != null) {
                logger.warning("Deposit validation failed: " + error);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, error);
                return;
            }
            
            long accountId = transaction.getAccountId();
            long adminId = (long) session.getAttribute("userId");
            Long branchId = (Long) session.getAttribute("branchId");
            String role = session.getAttribute("role").toString();

            if (UserRole.ADMIN.name().equals(role)) {
                if (!transactionService.isAccountInBranch(accountId, branchId)) {
                    logger.warning("Unauthorized deposit by admin ID " + adminId + " on account " + accountId);
                    ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN,
                            "Access denied: Account not in your branch.");
                    return;
                }
            }
           
            BigDecimal amount = transaction.getAmount();
            transactionService.deposit(accountId, amount, adminId);
            logger.info("Deposit successful by admin ID " + adminId + " to account " + accountId);
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Deposit successful.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Deposit failed", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    public void withdraw(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isAdminOrSuperAdmin(session, res)) return;

            Transaction transaction = RequestParser.parseRequest(req, Transaction.class);
            String error = TransactionValidator.validateDepositOrWithdraw(transaction);
            if (error != null) {
                logger.warning("Withdraw validation failed: " + error);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, error);
                return;
            }

            long accountId = transaction.getAccountId();
            long adminId = (long) session.getAttribute("userId");
            Long branchId = (Long) session.getAttribute("branchId");
            String role = session.getAttribute("role").toString();

            if (UserRole.ADMIN.name().equals(role)) {
                if (!transactionService.isAccountInBranch(accountId, branchId)) {
                    logger.warning("Unauthorized withdraw by admin ID " + adminId + " on account " + accountId);
                    ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN,
                            "Access denied: Account not in your branch.");
                    return;
                }
            }

            BigDecimal amount = transaction.getAmount();
            transactionService.withdraw(accountId, amount, adminId);
            logger.info("Withdrawal successful by admin ID " + adminId + " from account " + accountId);
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Withdrawal successful.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Withdrawal failed", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    public void transfer(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            Transaction transaction = RequestParser.parseRequest(req, Transaction.class);
            String error = TransactionValidator.validateTransfer(transaction);
            if (error != null) {
                logger.warning("Transfer validation failed: " + error);
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, error);
                return;
            }

            long fromAccountId = transaction.getAccountId();
            long toAccountId = transaction.getTransactionAccountId();
            BigDecimal amount = transaction.getAmount();
            String user = (String) session.getAttribute("username");
            Long branchId = (Long) session.getAttribute("branchId");
            String role = session.getAttribute("role").toString();

            if (UserRole.ADMIN.name().equals(role)) {
                if (!transactionService.isAccountInBranch(fromAccountId, branchId)) {
                    logger.warning("Unauthorized transfer by " + user + " from account " + fromAccountId);
                    ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN,
                            "Transfer not allowed: Source account not in your branch.");
                    return;
                }
            }

            transactionService.transfer(fromAccountId, toAccountId, amount, user);
            logger.info("Transfer of " + amount + " from " + fromAccountId + " to " + toAccountId + " by " + user);
            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Transfer successful.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Transfer failed", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    public void statement(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            StatementRequest payload = RequestParser.parseRequest(req, StatementRequest.class);
            if (payload == null || payload.getAccountId() <= 0 ||
                payload.getFromDate() == null || payload.getToDate() == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing required statement fields.");
                return;
            }

            long accountId = payload.getAccountId();
            String fromDateStr = payload.getFromDate();
            String toDateStr = payload.getToDate();
            Long branchId = (Long) session.getAttribute("branchId");
            String role = session.getAttribute("role").toString();

            if (UserRole.ADMIN.name().equals(role)) {
                if (!transactionService.isAccountInBranch(accountId, branchId)) {
                    logger.warning("Unauthorized statement request by admin for account " + accountId);
                    ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN, "Access denied: Account not in your branch.");
                    return;
                }
            }

            long fromTimestamp = Instant.parse(fromDateStr + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(toDateStr + "T23:59:59Z").toEpochMilli();

            // Extract pagination info
            int pageNumber = payload.getPageNumber();
            int pageSize = payload.getPageSize();


            // Get paginated results from the service
            PaginatedResponse<Transaction> paginatedResponse =
                    transactionService.getStatementByDateRange(accountId, fromTimestamp, toTimestamp, pageNumber, pageSize);

            // Send paginated response to frontend
            JSONArray jsonArray = new JSONArray(gson.toJson(paginatedResponse.getData()));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);

        } catch (DateTimeParseException | NumberFormatException e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Invalid input format.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception during statement processing", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error occurred.");
        }
        
    }
    public void receivedAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            long userId = (long) session.getAttribute("userId");

            // Parse date and pagination fields from request
            StatementRequest payload = RequestParser.parseRequest(req, StatementRequest.class);

            if (payload == null || payload.getFromDate() == null || payload.getToDate() == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing required date fields.");
                return;
            }

            long fromTimestamp = Instant.parse(payload.getFromDate() + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(payload.getToDate() + "T23:59:59Z").toEpochMilli();

            int page = PaginationUtil.validatePageNumber((payload.getPageNumber()));       // fallback to 1 if null
            int size = PaginationUtil.validatePageSize(payload.getPageSize());       // fallback to default like 10 or 20

            PaginatedResponse<Transaction> paginated = transactionService
                .getReceivedTransactionsForUser(userId, fromTimestamp, toTimestamp, page, size);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, new JSONObject(gson.toJson(paginated)));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving received transactions for user", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
    public void receivedAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            long userId = (long) session.getAttribute("userId");

            // Parse date and pagination fields from request
            StatementRequest payload = RequestParser.parseRequest(req, StatementRequest.class);

            if (payload == null || payload.getFromDate() == null || payload.getToDate() == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing required date fields.");
                return;
            }
            long accountId = payload.getAccountId();

            long fromTimestamp = Instant.parse(payload.getFromDate() + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(payload.getToDate() + "T23:59:59Z").toEpochMilli();

            int page = PaginationUtil.validatePageNumber((payload.getPageNumber()));       // fallback to 1 if null
            int size = PaginationUtil.validatePageSize(payload.getPageSize());    // fallback to default like 10 or 20

            PaginatedResponse<Transaction> paginated = transactionService
                .getReceivedTransactionsForAccount(userId, fromTimestamp, toTimestamp, page, size);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, new JSONObject(gson.toJson(paginated)));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving received transactions for user", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }




}
