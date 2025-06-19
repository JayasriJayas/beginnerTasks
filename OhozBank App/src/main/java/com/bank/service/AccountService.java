package com.bank.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.models.Account;
import com.bank.models.AccountRequest;

import exception.QueryException;

public interface AccountService {
    BigDecimal getAccountBalance(long accountId) throws QueryException, SQLException;
    long getBranchIdByAccountId(long accountId) throws QueryException, SQLException ;
    boolean updateAccountStatus(Account payload, long modifiedBy) throws QueryException, SQLException;
    Account getAccountById(long accountId) throws SQLException;
    List<Account> getAccountsByBranchId(long branchId) throws SQLException;
    List<Account> getAllAccounts() throws SQLException;
    
   

}
