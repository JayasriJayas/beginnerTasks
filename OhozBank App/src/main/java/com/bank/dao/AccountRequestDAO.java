package com.bank.dao;

import java.sql.SQLException;
import java.util.List;

import com.bank.models.AccountRequest;

import exception.QueryException;

public interface AccountRequestDAO {
	List<AccountRequest> fetchAllRequests() throws SQLException,QueryException;
    List<AccountRequest> fetchRequestsByAdminBranch(long adminId) throws SQLException,QueryException;
    boolean save(AccountRequest request) throws SQLException,QueryException;
    boolean approveRequest(long requestId,long adminId) throws SQLException,QueryException;
}
