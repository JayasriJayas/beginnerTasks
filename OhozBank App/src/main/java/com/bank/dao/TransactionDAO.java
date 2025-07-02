package com.bank.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
	 BigDecimal getTotalIncomeByUser(long userId) throws SQLException, QueryException;
	 BigDecimal getTotalExpenseByUser(long userId) throws SQLException, QueryException;
	 BigDecimal getTotalIncomeByAccount(long accountId) throws SQLException, QueryException;
	 BigDecimal getTotalExpenseByAccount(long accountId) throws SQLException, QueryException;
	 
	 List<Transaction> getDepositTransactionsForAll(long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	 int countDepositTransactionsForAll(long fromTimestamp, long toTimestamp) throws SQLException, QueryException ;
	 
	 List<Transaction> getTransferTransactionsForAll(long fromTimestamp, long toTimestamp, int limit, int offset)
	            throws SQLException, QueryException;
	 int countTransferTransactionsForAll(long fromTimestamp, long toTimestamp)
	            throws SQLException, QueryException;
	 
	 List<Transaction> getWithdrawTransactionsForAll(long fromTimestamp, long toTimestamp, int limit, int offset)
	            throws SQLException, QueryException ;
	 int countWithdrawTransactionsForAll(long fromTimestamp, long toTimestamp)
	            throws SQLException, QueryException;
	 List<Transaction> getDepositTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp, int limit, int offset)
	            throws SQLException, QueryException;
	 int countDepositTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp)
	            throws SQLException, QueryException;
	 List<Transaction> getTransferTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp, int limit, int offset)
             throws SQLException, QueryException;
	 int countTransferTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp)
	            throws SQLException, QueryException;
	 List<Transaction> getWithdrawTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp, int limit, int offset)
	            throws SQLException, QueryException ;
	 int countWithdrawTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp)
	            throws SQLException, QueryException;
	 List<Map<String, Object>> getCurrentMonthOutgoingPerBranch() throws SQLException, QueryException;
	 List<Map<String, Object>> getCurrentMonthIncomingPerBranch() throws SQLException, QueryException;
	 BigDecimal getTotalAmountByTypeAndBranch(String type, long branchId)throws SQLException, QueryException;
	 BigDecimal getTotalAmountByType(String type) throws SQLException, QueryException;
	 List<Map<String, Object>> getTopBranchesByTransactionCount(int limit) throws SQLException, QueryException;
	 List<Map<String, Object>> getDailyTransactionCountsForBranch(long branchId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;
	 List<Map<String, Object>> getAccountTransactionSummaryByBranch(long branchId, long totalLimit) throws SQLException, QueryException;

}

