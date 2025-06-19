package com.bank.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.bank.exception.BankingException;
import com.bank.models.Transaction;

import exception.QueryException;
public interface TransactionService {
	boolean deposit(long accountId, BigDecimal amount, long performedBy)throws SQLException,QueryException,BankingException;
	boolean withdraw(long accountId, BigDecimal amount, long performedBy)throws SQLException,QueryException,BankingException;
	boolean transfer(long accountId, long transactionAccount, BigDecimal amount, String performedBy)throws SQLException, QueryException, BankingException;
	List<Transaction> getStatementByDateRange(long accountId, long fromTimestamp, long toTimestamp)throws SQLException, QueryException, BankingException;
	boolean isAccountInBranch(long accountId, long branchId) throws SQLException,QueryException,BankingException;
	
//	boolean transfer(long fromAccountId, long toAccountId, BigDecimal amount, String performedBy);
//	List<Transaction> getStatement(long accountId);
}
