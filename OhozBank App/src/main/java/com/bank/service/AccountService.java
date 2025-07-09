package com.bank.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.bank.models.Account;
import com.bank.models.PaginatedResponse;

import exception.QueryException;

public interface AccountService {
    BigDecimal getAccountBalance(long accountId) throws QueryException, SQLException;
    long getBranchIdByAccountId(long accountId) throws QueryException, SQLException ;
    boolean updateAccountStatus(Account payload, long modifiedBy) throws QueryException, SQLException;
    Account getAccountById(long accountId) throws SQLException;
    List<Account> getAccountsByBranchId(long branchId) throws SQLException;
  
    List<Account> getAccountsByUserId(long userId) throws SQLException;
	BigDecimal getTotalBalanceByUser(long userId) throws SQLException, QueryException;
	int getTotalAccountsByBranch(Long branchId) throws SQLException, QueryException;
	int getTotalAccountCount() throws SQLException, QueryException;
	PaginatedResponse<Account> getPaginatedAccounts(int pageNumber, int pageSize)throws SQLException, QueryException;
	PaginatedResponse<Account> getPaginatedAccountsByBranchId(long branchId, int pageNumber, int pageSize) throws SQLException, QueryException;
	PaginatedResponse<Account> getPaginatedAccounts(String search, int page, int limit, Long branchId)
            throws SQLException ,QueryException;
}
