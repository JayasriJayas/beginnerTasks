package com.bank.dao;

import java.sql.SQLException;

import com.bank.models.Account;

import exception.QueryException;

public interface AccountDAO {
	  Account getAccountById(long accountId) throws QueryException, SQLException  ;
	  boolean updateAccount(Account account) throws QueryException, SQLException ;
	  long getBranchIdByAccountId(long accountId) throws QueryException, SQLException ;

}
