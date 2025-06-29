package com.bank.handler;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
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
    public void externalTransfer(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            Transaction transaction = RequestParser.parseRequest(req, Transaction.class);

            if (transaction.getAccountId() == null || transaction.getAmount() == null ||
                transaction.getReceiverBank() == null || transaction.getReceiverIFSC() == null ||
                transaction.getTransactionAccountId() == null) {  // external account number
            	System.out.println(transaction.getAccountId());
            	System.out.println(transaction.getAmount());
            	System.out.println(transaction.getReceiverBank());
            	System.out.println(transaction.getReceiverIFSC());
            	System.out.println(transaction.getTransactionAccountId());
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing fields for external transfer.");
                return;
            }

            long performedBy = (long) session.getAttribute("userId");
            String role = (String) session.getAttribute("role");
            Long branchId = (Long) session.getAttribute("branchId");

            long fromAccountId = transaction.getAccountId();

            // ADMIN: Restrict to only their branch
            if (UserRole.ADMIN.name().equals(role)) {
                if (!transactionService.isAccountInBranch(fromAccountId, branchId)) {
                    logger.warning("Unauthorized external transfer attempt by admin " + performedBy + " from account " + fromAccountId);
                    ResponseUtil.sendError(res, HttpServletResponse.SC_FORBIDDEN,
                            "You are not allowed to transfer from accounts outside your branch.");
                    return;
                }
            }

            transactionService.externalTransfer(
                    fromAccountId,
                    transaction.getTransactionAccountId(), // external account number
                    transaction.getReceiverBank(),
                    transaction.getReceiverIFSC(),
                    transaction.getAmount(),
                    performedBy
            );

            logger.info("External transfer by " + performedBy + " from account " + fromAccountId +
                    " to external account " + transaction.getTransactionAccountId() +
                    " (" + transaction.getReceiverBank() + ", IFSC: " + transaction.getReceiverIFSC() + ")");

            ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "External transfer successful.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "External transfer failed", e);
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
            String fromDateStr = payload.getFromDate().trim();
            String toDateStr = payload.getToDate().trim();
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

            int pageNumber = payload.getPageNumber();
            int pageSize = payload.getPageSize();

            PaginatedResponse<Transaction> paginatedResponse =
                    transactionService.getStatementByDateRange(accountId, fromTimestamp, toTimestamp, pageNumber, pageSize);

//            // Send paginated response to frontend
//            JSONArray jsonArray = new JSONArray(gson.toJson(paginatedResponse.getData()));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, new JSONObject(gson.toJson(paginatedResponse)));

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
            StatementRequest payload = RequestParser.parseRequest(req, StatementRequest.class);

            if (payload == null || payload.getFromDate() == null || payload.getToDate() == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing required date fields.");
                return;
            }

            long fromTimestamp = Instant.parse(payload.getFromDate().trim() + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(payload.getToDate().trim() + "T23:59:59Z").toEpochMilli();

            int page = PaginationUtil.validatePageNumber((payload.getPageNumber()));    
            int size = PaginationUtil.validatePageSize(payload.getPageSize()); 

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

            long fromTimestamp = Instant.parse(payload.getFromDate().trim() + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(payload.getToDate().trim() + "T23:59:59Z").toEpochMilli();
            System.out.println(fromTimestamp);
            System.out.println(toTimestamp);
            
            int page = PaginationUtil.validatePageNumber((payload.getPageNumber()));      
            int size = PaginationUtil.validatePageSize(payload.getPageSize());   

            PaginatedResponse<Transaction> paginated = transactionService
                .getReceivedTransactionsForAccount(accountId, fromTimestamp, toTimestamp, page, size);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, new JSONObject(gson.toJson(paginated)));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving received transactions for user", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
    public void depositAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            String role = (String) session.getAttribute("role");
            Long userId = (Long) session.getAttribute("userId");
            Long branchId = (Long) session.getAttribute("branchId");

            StatementRequest payload = RequestParser.parseRequest(req, StatementRequest.class);
            if (payload == null || payload.getFromDate() == null || payload.getToDate() == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing required date fields.");
                return;
            }

            long fromTimestamp = Instant.parse(payload.getFromDate().trim() + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(payload.getToDate().trim() + "T23:59:59Z").toEpochMilli();

            int page = PaginationUtil.validatePageNumber(payload.getPageNumber());
            int size = PaginationUtil.validatePageSize(payload.getPageSize());

            PaginatedResponse<Transaction> paginated;

            if (UserRole.SUPERADMIN.name().equals(role)) {
                paginated = transactionService.getDepositTransactionsForAll(fromTimestamp, toTimestamp, page, size);
            } else if (UserRole.ADMIN.name().equals(role)) {
                paginated = transactionService.getDepositTransactionsByBranch(branchId, fromTimestamp, toTimestamp, page, size);
            } else {
                paginated = transactionService.getDepositTransactionsForUser(userId, fromTimestamp, toTimestamp, page, size);
            }

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, new JSONObject(gson.toJson(paginated)));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving deposit transactions (all)", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }

    public void depositAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
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

            long fromTimestamp = Instant.parse(payload.getFromDate().trim() + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(payload.getToDate().trim() + "T23:59:59Z").toEpochMilli();

            int page = PaginationUtil.validatePageNumber((payload.getPageNumber()));      
            int size = PaginationUtil.validatePageSize(payload.getPageSize());   

            PaginatedResponse<Transaction> paginated = transactionService
                .getDepositTransactionsForAccount(accountId, fromTimestamp, toTimestamp, page, size);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, new JSONObject(gson.toJson(paginated)));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving received transactions for user", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
    public void withdrawAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            String role = (String) session.getAttribute("role");
            Long userId = (Long) session.getAttribute("userId");
            Long branchId = (Long) session.getAttribute("branchId");

            StatementRequest payload = RequestParser.parseRequest(req, StatementRequest.class);
            if (payload == null || payload.getFromDate() == null || payload.getToDate() == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing required date fields.");
                return;
            }

            long fromTimestamp = Instant.parse(payload.getFromDate().trim() + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(payload.getToDate().trim() + "T23:59:59Z").toEpochMilli();

            int page = PaginationUtil.validatePageNumber(payload.getPageNumber());
            int size = PaginationUtil.validatePageSize(payload.getPageSize());

            PaginatedResponse<Transaction> paginated;

            if (UserRole.SUPERADMIN.name().equals(role)) {
                paginated = transactionService.getWithdrawTransactionsForAll(fromTimestamp, toTimestamp, page, size);
            } else if (UserRole.ADMIN.name().equals(role)) {
                paginated = transactionService.getWithdrawTransactionsByBranch(branchId, fromTimestamp, toTimestamp, page, size);
            } else {
                paginated = transactionService.getWithdrawTransactionsForUser(userId, fromTimestamp, toTimestamp, page, size);
            }

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, new JSONObject(gson.toJson(paginated)));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving withdraw transactions (all)", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }

    public void withdrawAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
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

            long fromTimestamp = Instant.parse(payload.getFromDate().trim() + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(payload.getToDate().trim()+ "T23:59:59Z").toEpochMilli();

            int page = PaginationUtil.validatePageNumber((payload.getPageNumber()));      
            int size = PaginationUtil.validatePageSize(payload.getPageSize());   

            PaginatedResponse<Transaction> paginated = transactionService
                .getWithdrawTransactionsForAccount(accountId, fromTimestamp, toTimestamp, page, size);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, new JSONObject(gson.toJson(paginated)));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving received transactions for user", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
    public void transferAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            String role = (String) session.getAttribute("role");
            Long userId = (Long) session.getAttribute("userId");
            Long branchId = (Long) session.getAttribute("branchId");

            StatementRequest payload = RequestParser.parseRequest(req, StatementRequest.class);
            if (payload == null || payload.getFromDate() == null || payload.getToDate() == null) {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing required date fields.");
                return;
            }

            long fromTimestamp = Instant.parse(payload.getFromDate().trim() + "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(payload.getToDate().trim() + "T23:59:59Z").toEpochMilli();

            int page = PaginationUtil.validatePageNumber(payload.getPageNumber());
            int size = PaginationUtil.validatePageSize(payload.getPageSize());

            PaginatedResponse<Transaction> paginated;

            if (UserRole.SUPERADMIN.name().equals(role)) {
                paginated = transactionService.getTransferTransactionsForAll(fromTimestamp, toTimestamp, page, size);
            } else if (UserRole.ADMIN.name().equals(role)) {
                paginated = transactionService.getTransferTransactionsByBranch(branchId, fromTimestamp, toTimestamp, page, size);
            } else {
                paginated = transactionService.getTransferTransactionsForUser(userId, fromTimestamp, toTimestamp, page, size);
            }

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, new JSONObject(gson.toJson(paginated)));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving transfer transactions (all)", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }

    public void transferAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
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

            long fromTimestamp = Instant.parse(payload.getFromDate().trim()+ "T00:00:00Z").toEpochMilli();
            long toTimestamp = Instant.parse(payload.getToDate().trim() + "T23:59:59Z").toEpochMilli();

            int page = PaginationUtil.validatePageNumber((payload.getPageNumber()));      
            int size = PaginationUtil.validatePageSize(payload.getPageSize());   

            PaginatedResponse<Transaction> paginated = transactionService
                .getTransferTransactionsForAccount(accountId, fromTimestamp, toTimestamp, page, size);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, new JSONObject(gson.toJson(paginated)));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving received transactions for user", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
    public void recentTransactions(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            long userId = (long) session.getAttribute("userId");

            StatementRequest payload = RequestParser.parseRequest(req, StatementRequest.class);
            int limit = (payload.getLimit() == 0) ? 10 : payload.getLimit();


            List<Transaction> transactions = transactionService.getRecentTransactionsForUser(userId, limit);
            JSONArray jsonArray = new JSONArray(gson.toJson(transactions));
            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, jsonArray);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching recent transactions", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }
    public void totalIncome(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            long userId = (Long) session.getAttribute("userId");

            BigDecimal income = transactionService.getTotalIncomeByUser(userId);

            JSONObject response = new JSONObject();
            response.put("totalIncome", income);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating income summary", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching income");
        }
    }
    public void totalExpense(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;

            long userId = (Long) session.getAttribute("userId");

            BigDecimal expense = transactionService.getTotalExpenseByUser(userId);

            JSONObject response = new JSONObject();
            response.put("totalExpense", expense);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating expense summary", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching expense");
        }
    }
    public void incomeAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;
            Transaction transaction = RequestParser.parseRequest(req, Transaction.class);
            long accountId = transaction.getAccountId(); // Get accountId from request parameters

            BigDecimal totalIncome = transactionService.getTotalIncomeByAccount(accountId); // Call service method

            JSONObject json = new JSONObject();
            json.put("totalIncome", totalIncome);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, json);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating income for account", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching income");
        }
    }

    public void expenseAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!SessionUtil.isSessionAvailable(session, res)) return;
            Transaction transaction = RequestParser.parseRequest(req, Transaction.class);
            long accountId = transaction.getAccountId();

            BigDecimal totalExpense = transactionService.getTotalExpenseByAccount(accountId); // Call service method

            JSONObject json = new JSONObject();
            json.put("totalExpense", totalExpense);

            ResponseUtil.sendJson(res, HttpServletResponse.SC_OK, json);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calculating expense for account", e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching expense");
        }
    }


    





}
