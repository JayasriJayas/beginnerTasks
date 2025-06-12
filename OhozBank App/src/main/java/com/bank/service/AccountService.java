package com.bank.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

import com.bank.models.Account;

import exception.QueryException;

public interface AccountService {
    BigDecimal getAccountBalance(long accountId) throws QueryException, SQLException;
    boolean createAccountRequest(long userId, long branchId) throws SQLException,QueryException;
    boolean approveAccountRequest(long requestId, long adminId)throws SQLException,QueryException;
    long getBranchIdByAccountId(long accountId) throws QueryException, SQLException ;
    boolean updateAccountStatus(Account payload, long modifiedBy) throws QueryException, SQLException;
}
