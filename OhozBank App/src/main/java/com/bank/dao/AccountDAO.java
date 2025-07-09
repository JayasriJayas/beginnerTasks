package com.bank.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.bank.models.Account;
import com.bank.models.AccountRequest;

import exception.QueryException;

public interface AccountDAO {
	  Account getAccountById(long accountId) throws QueryException, SQLException  ;
	  boolean updateAccount(Account account) throws QueryException, SQLException ;
	  long getBranchIdByAccountId(long accountId) throws QueryException, SQLException;
	  List<Account> getAccountsByBranchId(long branchId) throws SQLException, QueryException;
	 
	  boolean isAccountInBranch(long accountId, long branchId) throws SQLException, QueryException ;
	  List<Account> getAccountsByUserId(long userId) throws SQLException, QueryException;
	  BigDecimal getTotalBalanceByUser(long userId) throws SQLException, QueryException;
	  int countAccountsByBranch(Long branchId) throws SQLException, QueryException;
	  int countAllAccounts() throws SQLException, QueryException;
	  List<Account> getAccountsByBranchId(long branchId, int limit, int offset) throws SQLException, QueryException ;
	  List<Account> getAllAccounts(int limit, int offset) throws SQLException, QueryException ;
	  List<Account> searchAccounts(String search, int limit, int offset, Long branchId) throws SQLException,QueryException ;
	    int countMatchingAccounts(String search, Long branchId) throws SQLException,QueryException ;




}
