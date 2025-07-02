package com.bank.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.exception.BankingException;
import com.bank.models.Branch;
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
    PaginatedResponse<Transaction> getDepositTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize) throws SQLException, QueryException;
    PaginatedResponse<Transaction> getDepositTransactionsForAll(long fromTimestamp, long toTimestamp, int pageNumber, int pageSize) throws SQLException, QueryException;
    PaginatedResponse<Transaction> getTransferTransactionsForAll(long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException;
    PaginatedResponse<Transaction> getWithdrawTransactionsForAll(long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException;
    boolean externalTransfer(long fromAccountId, Long externalAccountNumber,
            String receiverBank, String receiverIFSC,
            BigDecimal amount, long performedBy)
throws SQLException, QueryException, BankingException ;
    PaginatedResponse<Transaction> getTransferTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException ;
    PaginatedResponse<Transaction> getWithdrawTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize)
            throws SQLException, QueryException ;
    
   
    List<Branch> getCurrentMonthOutgoingPerBranch() throws SQLException, QueryException;
    List<Branch> getCurrentMonthIncomingPerBranch() throws SQLException, QueryException;
    Map<String, BigDecimal> getTransactionTypeSummaryByBranch(long branchId) throws SQLException, QueryException;
    Map<String, BigDecimal> getTransactionTypeSummaryAllBranches() throws SQLException, QueryException;
    List<Map<String, Object>> getTopBranchesByTransactionCount(int limit) throws SQLException, QueryException;
    List<Map<String, Object>> getDailyTransactionCountsForBranch(long branchId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;
    List<Map<String, Object>> getAccountTransactionSummaryByBranch(long branchId, long totalLimit) throws SQLException, QueryException ;


	
//	boolean transfer(long fromAccountId, long toAccountId, BigDecimal amount, String performedBy);
//	List<Transaction> getStatement(long accountId);
}
