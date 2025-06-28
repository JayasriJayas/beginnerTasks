package com.bank.service;

import java.sql.SQLException;
import java.util.List;

import com.bank.models.AccountRequest;
import com.bank.models.PaginatedResponse;

import exception.QueryException;

public interface AccountRequestService {
	PaginatedResponse<AccountRequest> getAllRequests(long fromTimestamp, long toTimestamp, int pageNumber, int pageSize) throws SQLException, QueryException;
	 PaginatedResponse<AccountRequest> getRequestsByAdminBranch(long adminId,long fromTimestamp, long toTimestamp, int pageNumber, int pageSize) throws SQLException, QueryException;
	 boolean createAccountRequest(long userId, long branchId) throws SQLException,QueryException;
	 boolean approveAccountRequest(long requestId, long adminId)throws SQLException,QueryException;
	 List<AccountRequest> getPendingRequestsForUser(long userId) throws SQLException, QueryException;

}
