package com.bank.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.enums.RequestStatus;
import com.bank.models.AccountRequest;
import com.bank.models.PaginatedResponse;

import exception.QueryException;

public interface AccountRequestService {
	PaginatedResponse<AccountRequest> getAllRequests(long fromTimestamp, long toTimestamp, int pageNumber, int pageSize,RequestStatus status) throws SQLException, QueryException;
	 PaginatedResponse<AccountRequest> getRequestsByAdminBranch(long adminId,long fromTimestamp, long toTimestamp, int pageNumber, int pageSize,RequestStatus status) throws SQLException, QueryException;
	 boolean createAccountRequest(long userId, long branchId) throws SQLException,QueryException;
	 boolean approveAccountRequest(long requestId, long adminId) throws SQLException, QueryException;
	 List<AccountRequest> getPendingRequestsForUser(long userId,RequestStatus status) throws SQLException, QueryException;
	 boolean rejectUserRequest(long requestId, long adminId, String reason, String role, Long branchId);
	 List<Long> rejectMultipleRequests(List<Long> requestIds, long adminId, String reason, String role, Long branchId)throws SQLException, QueryException;
	 List<Long> approveMultipleRequests(List<Long> requestIds, long adminId, String role, Long branchId)throws SQLException, QueryException;
	 Map<String, Long> getRequestStatusCounts(String role, long adminId)throws SQLException, QueryException;
	 AccountRequest getRequestDetailsById(long requestId)throws SQLException, QueryException;


}
