package com.bank.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import com.bank.dao.AccountDAO;
import com.bank.dao.TransactionDAO;
import com.bank.enums.TransactionStatus;
import com.bank.enums.TransactionType;
import com.bank.exception.BankingException;
import com.bank.factory.DaoFactory;
import com.bank.models.Account;
import com.bank.models.PaginatedResponse;
import com.bank.models.Transaction;
import com.bank.service.TransactionService;
import com.bank.util.PaginationUtil;

import exception.QueryException;

public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = Logger.getLogger(TransactionServiceImpl.class.getName());

    private final TransactionDAO transactionDAO = DaoFactory.getTransactionDAO();
    private final AccountDAO accountDAO = DaoFactory.getAccountDAO();

    private final ConcurrentHashMap<Long, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

    @Override
    public boolean deposit(long accountId, BigDecimal amount, long performedBy)
            throws SQLException, QueryException, BankingException {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Amount must be positive");
        }

        ReentrantLock accountLock = accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
        accountLock.lock();
        try {
            Account account = accountDAO.getAccountById(accountId);
            if (account == null) {
                throw new BankingException("Account not found");
            }

            BigDecimal newBalance = account.getBalance().add(amount);
            account.setBalance(newBalance);
            accountDAO.updateAccount(account);

            Transaction transaction = createTransaction(
                    accountId,
                    account.getUserId(),
                    null,
                    amount,
                    newBalance,
                    TransactionType.DEPOSIT,
                    "Deposited by user ID: " + performedBy,
                    TransactionStatus.SUCCESS
            );

            logger.info("Deposit successful: Account ID " + accountId + ", Amount " + amount);
            return transactionDAO.saveTransaction(transaction);
        } finally {
            accountLock.unlock();
        }
    }

    @Override
    public boolean withdraw(long accountId, BigDecimal amount, long performedBy)
            throws SQLException, QueryException, BankingException {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Amount must be positive");
        }

        ReentrantLock accountLock = accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
        accountLock.lock();
        try {
            Account account = accountDAO.getAccountById(accountId);
            if (account == null) {
                throw new BankingException("Account not found");
            }

            BigDecimal currentBalance = account.getBalance();
            if (currentBalance.compareTo(amount) < 0) {
                throw new BankingException("Insufficient balance");
            }

            BigDecimal newBalance = currentBalance.subtract(amount);
            account.setBalance(newBalance);
            accountDAO.updateAccount(account);

            Transaction transaction = createTransaction(
                    accountId,
                    account.getUserId(),
                    null,
                    amount,
                    newBalance,
                    TransactionType.WITHDRAWAL,
                    "Withdrawn by user ID: " + performedBy,
                    TransactionStatus.SUCCESS
            );

            logger.info("Withdrawal successful: Account ID " + accountId + ", Amount " + amount);
            return transactionDAO.saveTransaction(transaction);
        } finally {
            accountLock.unlock();
        }
    }

    @Override
    public boolean transfer(long fromAccountId, long toAccountId, BigDecimal amount, String performedBy)
            throws SQLException, QueryException, BankingException {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Amount must be positive");
        }

        if (fromAccountId == toAccountId) {
            throw new BankingException("Cannot transfer to the same account");
        }

        long firstLockId = Math.min(fromAccountId, toAccountId);
        long secondLockId = Math.max(fromAccountId, toAccountId);

        ReentrantLock lock1 = accountLocks.computeIfAbsent(firstLockId, k -> new ReentrantLock());
        ReentrantLock lock2 = accountLocks.computeIfAbsent(secondLockId, k -> new ReentrantLock());

        lock1.lock();
        if (lock1 != lock2) lock2.lock();

        try {
            Account fromAccount = accountDAO.getAccountById(fromAccountId);
            Account toAccount = accountDAO.getAccountById(toAccountId);

            if (fromAccount == null || toAccount == null) {
                throw new BankingException("One or both accounts not found");
            }

            BigDecimal currentBalance = fromAccount.getBalance();
            if (currentBalance.compareTo(amount) < 0) {
                throw new BankingException("Insufficient balance in source account");
            }

            fromAccount.setBalance(currentBalance.subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));

            accountDAO.updateAccount(fromAccount);
            accountDAO.updateAccount(toAccount);

            Transaction transaction = createTransaction(
                    fromAccountId,
                    fromAccount.getUserId(),
                    toAccountId,
                    amount,
                    fromAccount.getBalance(),
                    TransactionType.TRANSFER,
                    "Transfer from account " + fromAccountId + " to " + toAccountId + " by " + performedBy,
                    TransactionStatus.SUCCESS
            );

            logger.info("Transfer successful: From " + fromAccountId + " to " + toAccountId + ", Amount " + amount);
            return transactionDAO.saveTransaction(transaction);
        } finally {
            if (lock1 != lock2) lock2.unlock();
            lock1.unlock();
        }
    }

    @Override
    public PaginatedResponse<Transaction> getStatementByDateRange(
            long accountId, long fromTimestamp, long toTimestamp,
            int pageNumber, int pageSize
    ) throws SQLException, QueryException {

        pageNumber = PaginationUtil.validatePageNumber(pageNumber);
        pageSize = PaginationUtil.validatePageSize(pageSize);
        int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);
        System.out.println(offset);

        List<Transaction> transactions = transactionDAO.getTransactionsByAccountIdAndDateRange(
            accountId, fromTimestamp, toTimestamp, pageSize, offset);

        int totalTransactions = transactionDAO.countTransactionsByAccountIdAndDateRange(
            accountId, fromTimestamp, toTimestamp);

        return new PaginatedResponse<>(transactions, pageNumber, pageSize, totalTransactions);
    }



    @Override
    public boolean isAccountInBranch(long accountId, long branchId) throws SQLException, QueryException {
        logger.info("Checking if account " + accountId + " belongs to branch " + branchId);
        return accountDAO.isAccountInBranch(accountId, branchId);
    }
    @Override
    public PaginatedResponse<Transaction> getReceivedTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException {

        pageNumber = PaginationUtil.validatePageNumber(pageNumber);
        pageSize = PaginationUtil.validatePageSize(pageSize);
        int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);

        // Fetch data
        List<Transaction> transactions = transactionDAO.getReceivedTransactionsForUser(userId, fromTimestamp, toTimestamp, pageSize, offset);
        int total = transactionDAO.countReceivedTransactionsForUser(userId, fromTimestamp, toTimestamp);

        return new PaginatedResponse<>(transactions, pageNumber, pageSize, total);
    }

    @Override
    public PaginatedResponse<Transaction> getReceivedTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException {

        pageNumber = PaginationUtil.validatePageNumber(pageNumber);
        pageSize = PaginationUtil.validatePageSize(pageSize);
        int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);

        // Fetch data
        List<Transaction> transactions = transactionDAO.getReceivedTransactionsForAccount(accountId, fromTimestamp, toTimestamp, pageSize, offset);
        int total = transactionDAO.countReceivedTransactionsForAccount(accountId, fromTimestamp, toTimestamp);

        return new PaginatedResponse<>(transactions, pageNumber, pageSize, total);
    }
    @Override
    public PaginatedResponse<Transaction> getDepositTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException {

        pageNumber = PaginationUtil.validatePageNumber(pageNumber);
        pageSize = PaginationUtil.validatePageSize(pageSize);
        int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);

        // Fetch data
        List<Transaction> transactions = transactionDAO.getDepositTransactionsForUser(userId, fromTimestamp, toTimestamp, pageSize, offset);
        int total = transactionDAO.countDepositTransactionsForUser(userId, fromTimestamp, toTimestamp);

        return new PaginatedResponse<>(transactions, pageNumber, pageSize, total);
    }

    @Override
    public PaginatedResponse<Transaction> getDepositTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException {

        pageNumber = PaginationUtil.validatePageNumber(pageNumber);
        pageSize = PaginationUtil.validatePageSize(pageSize);
        int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);

        // Fetch data
        List<Transaction> transactions = transactionDAO.getDepositTransactionsForAccount(accountId, fromTimestamp, toTimestamp, pageSize, offset);
        int total = transactionDAO.countDepositTransactionsForAccount(accountId, fromTimestamp, toTimestamp);

        return new PaginatedResponse<>(transactions, pageNumber, pageSize, total);
    }
    @Override
    public PaginatedResponse<Transaction> getWithdrawTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException {

        pageNumber = PaginationUtil.validatePageNumber(pageNumber);
        pageSize = PaginationUtil.validatePageSize(pageSize);
        int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);

        // Fetch data
        List<Transaction> transactions = transactionDAO.getWithdrawTransactionsForUser(userId, fromTimestamp, toTimestamp, pageSize, offset);
        int total = transactionDAO.countWithdrawTransactionsForUser(userId, fromTimestamp, toTimestamp);

        return new PaginatedResponse<>(transactions, pageNumber, pageSize, total);
    }

    @Override
    public PaginatedResponse<Transaction> getWithdrawTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException {

        pageNumber = PaginationUtil.validatePageNumber(pageNumber);
        pageSize = PaginationUtil.validatePageSize(pageSize);
        int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);

        // Fetch data
        List<Transaction> transactions = transactionDAO.getWithdrawTransactionsForAccount(accountId, fromTimestamp, toTimestamp, pageSize, offset);
        int total = transactionDAO.countWithdrawTransactionsForAccount(accountId, fromTimestamp, toTimestamp);

        return new PaginatedResponse<>(transactions, pageNumber, pageSize, total);
    }
    @Override
    public PaginatedResponse<Transaction> getTransferTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException {

        pageNumber = PaginationUtil.validatePageNumber(pageNumber);
        pageSize = PaginationUtil.validatePageSize(pageSize);
        int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);

        // Fetch data
        List<Transaction> transactions = transactionDAO.getTransferTransactionsForUser(userId, fromTimestamp, toTimestamp, pageSize, offset);
        int total = transactionDAO.countTransferTransactionsForUser(userId, fromTimestamp, toTimestamp);

        return new PaginatedResponse<>(transactions, pageNumber, pageSize, total);
    }

    @Override
    public PaginatedResponse<Transaction> getTransferTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException {

        pageNumber = PaginationUtil.validatePageNumber(pageNumber);
        pageSize = PaginationUtil.validatePageSize(pageSize);
        int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);

        // Fetch data
        List<Transaction> transactions = transactionDAO.getTransferTransactionsForAccount(accountId, fromTimestamp, toTimestamp, pageSize, offset);
        int total = transactionDAO.countTransferTransactionsForAccount(accountId, fromTimestamp, toTimestamp);

        return new PaginatedResponse<>(transactions, pageNumber, pageSize, total);
    }
    @Override
    public List<Transaction> getRecentTransactionsForUser(long userId, int limit) throws SQLException, QueryException {
        return transactionDAO.getRecentTransactionsForUser(userId, limit);
    }
    



    private Transaction createTransaction(long accountId, long userId, Long transactionAccountId,
                                          BigDecimal amount, BigDecimal closingBalance,
                                          TransactionType type, String description, TransactionStatus status) {
        Transaction t = new Transaction();
        t.setAccountId(accountId);
        t.setUserId(userId);
        t.setTransactionAccountId(transactionAccountId);
        t.setAmount(amount);
        t.setClosingBalance(closingBalance);
        t.setType(type);
        t.setTimestamp(System.currentTimeMillis());
        t.setDescription(description);
        t.setStatus(status);
        return t;
    }
}
