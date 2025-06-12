//package com.bank.service.impl;
//
//import com.bank.dao.AccountDAO;
//
//import com.bank.dao.TransactionDAO;
//import com.bank.dao.impl.AccountDAOImpl;
//import com.bank.dao.impl.TransactionDAOImpl;
//import com.bank.enums.TransactionStatus;
//import com.bank.enums.TransactionType;
//import com.bank.exception.BankingException;
//import com.bank.mapper.TransactionMapper;
//import com.bank.models.Account;
//import com.bank.models.Transaction;
//import com.bank.service.TransactionService;
//import exception.QueryException;
//
//import java.util.List;
//import java.math.BigDecimal;
//import java.sql.SQLException;
//
//public class TransactionServiceImpl implements TransactionService {
//
//    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
//    private final AccountDAO accountDAO = new AccountDAOImpl();
//
//    @Override
//    public synchronized boolean deposit(long accountId, BigDecimal amount, long performedBy)
//            throws SQLException, QueryException, BankingException {
//
//        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new BankingException("Amount must be positive");
//        }
//        Account account = accountDAO.getAccountById(accountId);
//        if (account == null) {
//            throw new BankingException("Account not found");
//        }
//        		BigDecimal newBalance = account.getBalance().add(amount);
//        account.setBalance(newBalance);
//        accountDAO.updateAccount(account);
//
//        Transaction trans = createTransaction(
//                accountId,
//                account.getUserId(),
//                null,
//                amount,
//                newBalance,
//                TransactionType.DEPOSIT,
//                "Deposited by"+performedBy,
//                TransactionStatus.SUCCESS
//        );
//        return transactionDAO.saveTransaction(trans);
//    }
//
//    @Override
//    public synchronized boolean withdraw(long accountId, BigDecimal amount, long performedBy)
//            throws SQLException, QueryException, BankingException {
//
//        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new BankingException("Amount must be positive");
//        }
//
//        Account account = accountDAO.getAccountById(accountId);
//        if (account == null) {
//            throw new BankingException("Account not found");
//        }
//
//        BigDecimal currentBalance = account.getBalance();
//        if (currentBalance.compareTo(amount) < 0) {
//            throw new BankingException("Insufficient balance");
//        }
//
//        BigDecimal newBalance = currentBalance.subtract(amount);
//        account.setBalance(newBalance);
//        accountDAO.updateAccount(account);
//
//        Transaction trans = createTransaction(
//                accountId,
//                account.getUserId(),
//                null,
//                amount,
//                newBalance,
//                TransactionType.WITHDRAWAL,
//                "Withdrawal by " + performedBy,
//                TransactionStatus.SUCCESS
//        );
//
//        return transactionDAO.saveTransaction(trans);
//    }
//
//    @Override
//    public synchronized boolean transfer(long accountId, long transactionAccount, BigDecimal amount, String performedBy)
//            throws SQLException, QueryException, BankingException {
//
//        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new BankingException("Amount must be positive");
//        }
//
//        if (accountId == transactionAccount) {
//            throw new BankingException("Cannot transfer to the same account");
//        }
//
//        Account from = accountDAO.getAccountById(accountId);
//        Account to = accountDAO.getAccountById(transactionAccount);
//
//        if (from == null || to == null) {
//            throw new BankingException("Account(s) not found");
//        }
//
//        BigDecimal currentBalance = from.getBalance();
//        if (currentBalance.compareTo(amount) < 0) {
//            throw new BankingException("Insufficient balance");
//        }
//
//        from.setBalance(currentBalance.subtract(amount));
//        to.setBalance(to.getBalance().add(amount));
//
//        accountDAO.updateAccount(from);
//        accountDAO.updateAccount(to);
//
//        Transaction trans = createTransaction(
//                accountId,
//                from.getUserId(),
//                to.getAccountId(),
//                amount,
//                from.getBalance(),
//                TransactionType.TRANSFER,
//                "Transfer from " + accountId + " to " + transactionAccount,
//                TransactionStatus.SUCCESS
//                
//        );
//
//        return transactionDAO.saveTransaction(trans);
//    }
//  
//
//    @Override
//    public List<Transaction> getStatementByDateRange(long accountId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException {
//        return transactionDAO.getTransactionsByAccountIdAndDateRange(accountId, fromTimestamp, toTimestamp);
//    }
//
//
//    private Transaction createTransaction(long accountId, long userId, Long transactionAccountId,
//                                          BigDecimal amount, BigDecimal closingBalance,
//                                          TransactionType type, String description,TransactionStatus status) {
//        Transaction t = new Transaction();
//        t.setAccountId(accountId);
//        t.setUserId(userId);
//        t.setTransactionAccountId(transactionAccountId);
//        t.setAmount(amount);
//        t.setClosingBalance(closingBalance);
//        t.setType(type);
//        t.setTimestamp(System.currentTimeMillis());
//        t.setDescription(description);
//        t.setStatus(TransactionStatus.SUCCESS);
//        return t;
//    }
//}
package com.bank.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.bank.dao.AccountDAO;
import com.bank.dao.TransactionDAO;
import com.bank.dao.impl.AccountDAOImpl;
import com.bank.dao.impl.TransactionDAOImpl;
import com.bank.enums.TransactionStatus;
import com.bank.enums.TransactionType;
import com.bank.exception.BankingException;
import com.bank.models.Account;
import com.bank.models.Transaction;
import com.bank.service.TransactionService;

import exception.QueryException; 

public class TransactionServiceImpl implements TransactionService {

    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();


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

            Transaction trans = createTransaction(
                    accountId,
                    account.getUserId(),
                    null, 
                    amount,
                    newBalance,
                    TransactionType.DEPOSIT,
                    "Deposited by " + performedBy,
                    TransactionStatus.SUCCESS
            );
            return transactionDAO.saveTransaction(trans);
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

            Transaction trans = createTransaction(
                    accountId,
                    account.getUserId(),
                    null, 
                    amount,
                    newBalance,
                    TransactionType.WITHDRAWAL,
                    "Withdrawal by " + performedBy,
                    TransactionStatus.SUCCESS
            );

            return transactionDAO.saveTransaction(trans);
        } finally {
            accountLock.unlock(); // Release lock
        }
    }

    @Override
    public boolean transfer(long accountId, long transactionAccount, BigDecimal amount, String performedBy)
            throws SQLException, QueryException, BankingException {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Amount must be positive");
        }

        if (accountId == transactionAccount) {
            throw new BankingException("Cannot transfer to the same account");
        }

        long firstLockId = Math.min(accountId, transactionAccount);
        long secondLockId = Math.max(accountId, transactionAccount);

        ReentrantLock lock1 = accountLocks.computeIfAbsent(firstLockId, k -> new ReentrantLock());
        ReentrantLock lock2 = accountLocks.computeIfAbsent(secondLockId, k -> new ReentrantLock());

        lock1.lock();
      
        if (lock1 != lock2) {
            lock2.lock(); 
        }
        
        try {
            Account fromAccount = accountDAO.getAccountById(accountId);
            Account toAccount = accountDAO.getAccountById(transactionAccount);

            if (fromAccount == null || toAccount == null) {
                throw new BankingException("Account(s) not found");
            }

            BigDecimal currentBalance = fromAccount.getBalance();
            if (currentBalance.compareTo(amount) < 0) {
                throw new BankingException("Insufficient balance in source account");
            }

            fromAccount.setBalance(currentBalance.subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));
            accountDAO.updateAccount(fromAccount);
            accountDAO.updateAccount(toAccount);

            Transaction trans = createTransaction(
                    accountId,
                    fromAccount.getUserId(),
                    toAccount.getAccountId(),
                    amount,
                    fromAccount.getBalance(), 
                    TransactionType.TRANSFER,
                    "Transfer from " + accountId + " to " + transactionAccount + " by " + performedBy,
                    TransactionStatus.SUCCESS
            );

            return transactionDAO.saveTransaction(trans);
        } finally {
          
            if (lock1 != lock2) {
                lock2.unlock();
            }
            lock1.unlock();
        }
    }
    
    @Override
    public List<Transaction> getStatementByDateRange(long accountId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException {
      
        return transactionDAO.getTransactionsByAccountIdAndDateRange(accountId, fromTimestamp, toTimestamp);
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

