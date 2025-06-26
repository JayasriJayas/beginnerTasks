package com.bank.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

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
	PaginatedResponse<Transaction> getDepositTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)throws SQLException, QueryException;
	PaginatedResponse<Transaction> getDepositTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)throws SQLException, QueryException;
	PaginatedResponse<Transaction> getWithdrawTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)throws SQLException, QueryException;
	PaginatedResponse<Transaction> getWithdrawTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)throws SQLException, QueryException;
	PaginatedResponse<Transaction> getTransferTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)throws SQLException, QueryException;
	PaginatedResponse<Transaction> getTransferTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)throws SQLException, QueryException;
	List<Transaction> getRecentTransactionsForUser(long userId, int limit) throws SQLException, QueryException;
	BigDecimal getTotalIncomeByUser(long userId) throws SQLException, QueryException;
	BigDecimal getTotalExpenseByUser(long userId) throws SQLException, QueryException;
	 BigDecimal getTotalIncomeByAccount(long accountId) throws SQLException, QueryException;
	    BigDecimal getTotalExpenseByAccount(long accountId) throws SQLException, QueryException;

	
//	boolean transfer(long fromAccountId, long toAccountId, BigDecimal amount, String performedBy);
//	List<Transaction> getStatement(long accountId);
}
