package com.bank.dao;

import java.sql.SQLException;
import java.util.List;

import com.bank.models.Transaction;

import exception.QueryException;

public interface TransactionDAO {
	boolean saveTransaction(Transaction trans) throws SQLException,QueryException;
	 List<Transaction> getTransactionsByAccountId(long accountId)throws SQLException,QueryException;
	 List<Transaction> getTransactionsByAccountIdAndDateRange(long accountId, long fromTimestamp, long toTimestamp, int pageSize, int offset) throws SQLException,QueryException ;
	 int countTransactionsByAccountIdAndDateRange(long accountId, long fromTimestamp, long toTimestamp) throws SQLException,QueryException;
	 List<Transaction> getReceivedTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	 int countReceivedTransactionsForUser(long userId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;

	 List<Transaction> getReceivedTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	 int countReceivedTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;
	 List<Transaction> getDepositTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	 int countDepositTransactionsForUser(long userId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;

	 List<Transaction> getDepositTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	 int countDepositTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;
	 List<Transaction> getWithdrawTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	 int countWithdrawTransactionsForUser(long userId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;

	 List<Transaction> getWithdrawTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	 int countWithdrawTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;
	 List<Transaction> getTransferTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	 int countTransferTransactionsForUser(long userId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;

	 List<Transaction> getTransferTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	 int countTransferTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;
	 
	 List<Transaction> getRecentTransactionsForUser(long userId, int limit) throws SQLException, QueryException;
	 
}

