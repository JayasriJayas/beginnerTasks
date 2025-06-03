package com.bank.dao;

import java.sql.SQLException;
import java.util.List;

import com.bank.models.Transaction;

import exception.QueryException;

public interface TransactionDAO {
	boolean saveTransaction(Transaction trans) throws SQLException,QueryException;
	 List<Transaction> getTransactionsByAccountId(long accountId)throws SQLException,QueryException;
	 List<Transaction> getTransactionsByAccountIdAndDateRange(long accountId, long fromMillis, long toMillis)throws SQLException,QueryException;
	}

