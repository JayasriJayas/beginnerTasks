package com.bank.dao;

import java.sql.SQLException;
import java.util.List;

import com.bank.models.Account;
import com.bank.models.AccountRequest;

import exception.QueryException;

public interface AccountDAO {
	  Account getAccountById(long accountId) throws QueryException, SQLException  ;
	  boolean updateAccount(Account account) throws QueryException, SQLException ;
	  long getBranchIdByAccountId(long accountId) throws QueryException, SQLException ;
	  boolean approveRequest(long requestId,long adminId) throws SQLException,QueryException;
	  List<Account> getAccountsByBranchId(long branchId) throws SQLException, QueryException;
	  List<Account> getAllAccounts() throws SQLException, QueryException;


}
