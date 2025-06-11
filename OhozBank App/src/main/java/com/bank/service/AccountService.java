package com.bank.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

import exception.QueryException;

public interface AccountService {
    BigDecimal getAccountBalance(long accountId) throws QueryException, SQLException;
    boolean createAccountRequest(long userId, long branchId) throws SQLException,QueryException;
    boolean approveAccountRequest(long requestId, long adminId)throws SQLException,QueryException;
    long getBranchIdByAccountId(long accountId) throws QueryException, SQLException ;
    boolean updateAccountStatus(Map<String, Object> payload, long modifiedBy) throws QueryException, SQLException;
}
