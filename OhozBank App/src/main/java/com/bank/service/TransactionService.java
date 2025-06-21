package com.bank.service;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.bank.exception.BankingException;
import com.bank.models.PaginatedResponse;
import com.bank.models.Transaction;

import exception.QueryException;
public interface TransactionService {
	boolean deposit(long accountId, BigDecimal amount, long performedBy)throws SQLException,QueryException,BankingException;
	boolean withdraw(long accountId, BigDecimal amount, long performedBy)throws SQLException,QueryException,BankingException;
	boolean transfer(long accountId, long transactionAccount, BigDecimal amount, String performedBy)throws SQLException, QueryException, BankingException;
	PaginatedResponse<Transaction> getStatementByDateRange(long accountId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize) throws SQLException, QueryException;
	boolean isAccountInBranch(long accountId, long branchId) throws SQLException,QueryException,BankingException;
	PaginatedResponse<Transaction> getReceivedTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)throws SQLException, QueryException;
	PaginatedResponse<Transaction> getReceivedTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)throws SQLException, QueryException;

	
//	boolean transfer(long fromAccountId, long toAccountId, BigDecimal amount, String performedBy);
//	List<Transaction> getStatement(long accountId);
}
