package com.bank.service;

import java.sql.SQLException;
import java.util.List;

import com.bank.models.AccountRequest;

import exception.QueryException;

public interface AccountRequestService {
	 List<AccountRequest> getAllRequests()throws SQLException,QueryException;
	 List<AccountRequest> getRequestsByAdminBranch(long adminId) throws SQLException,QueryException;
	 boolean createAccountRequest(long userId, long branchId) throws SQLException,QueryException;
}
