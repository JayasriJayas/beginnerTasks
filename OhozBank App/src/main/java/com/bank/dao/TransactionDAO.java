package com.bank.dao;

import java.sql.SQLException;

import com.bank.models.Transaction;

import exception.QueryException;

public interface TransactionDAO {
	boolean saveTransaction(Transaction trans) throws SQLException,QueryException;
}
