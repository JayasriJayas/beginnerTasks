package com.bank.service.impl;

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

import java.math.BigDecimal;
import java.sql.SQLException;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();

    @Override
    public synchronized boolean deposit(long accountId, BigDecimal amount, String performedBy)throws SQLException,QueryException,BankingException{
    	 
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) 
            throw new BankingException("Amount must be positive");

        Account account = accountDAO.getAccountById(accountId);
        if (account == null) 
            throw new BankingException("Account not found");
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountDAO.updateAccount(account);


        Transaction trans = new Transaction();

        trans.setAccountId(accountId);   
        trans.setUserId(account.getUserId());
        trans.setTransactionAccountId(null);
        trans.setAmount(amount);
        trans.setClosingBalance(newBalance);
        trans.setType(TransactionType.DEPOSIT);
        trans.setTimestamp(System.currentTimeMillis());
        trans.setDescription("Deposit by " + performedBy);
        trans.setStatus(TransactionStatus.SUCCESS); 
        
        return transactionDAO.saveTransaction(trans);
    }

    @Override
    public synchronized boolean withdraw(long accountId, BigDecimal amount, String performedBy)
            throws SQLException, QueryException, BankingException {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Amount must be positive");
        }

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

        Transaction trans = new Transaction();
        trans.setAccountId(accountId);
        trans.setUserId(account.getUserId());
        trans.setTransactionAccountId(null);
        trans.setAmount(amount);
        trans.setClosingBalance(newBalance);
        trans.setType(TransactionType.WITHDRAWAL);
        trans.setTimestamp(System.currentTimeMillis());
        trans.setDescription("Withdrawal by " + performedBy);

        return transactionDAO.saveTransaction(trans);
    }

    @Override
    public synchronized boolean transfer(long accountId, long transactionAccount, BigDecimal amount, String performedBy)
            throws SQLException, QueryException, BankingException {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new BankingException("Amount must be positive");

        if (accountId == transactionAccount)
            throw new BankingException("Cannot transfer to the same account");

        Account from = accountDAO.getAccountById(accountId);
        Account to = accountDAO.getAccountById(transactionAccount);

        if (from == null || to == null)
            throw new BankingException("Account(s) not found");

        BigDecimal currentBalance = from.getBalance();

        if (currentBalance.compareTo(amount) < 0)
            throw new BankingException("Insufficient balance");

        from.setBalance(currentBalance.subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountDAO.updateAccount(from);
        accountDAO.updateAccount(to);

        Transaction trans = new Transaction();
        trans.setAccountId(accountId);
        trans.setUserId(from.getUserId());
        trans.setTransactionAccountId(to.getAccountId());
        trans.setAmount(amount);
        trans.setType(TransactionType.TRANSFER);
        trans.setTimestamp(System.currentTimeMillis());
        trans.setClosingBalance(from.getBalance());
        trans.setDescription("Transfer from " + accountId + " to " + transactionAccount);
        trans.setStatus(TransactionStatus.SUCCESS); 

        return transactionDAO.saveTransaction(trans);
    }


//    @Override
//    public List<Transaction> getStatement(long accountId) {
//        return transactionDAO.getTransactionsByAccountId(accountId);
//    }
//}


}