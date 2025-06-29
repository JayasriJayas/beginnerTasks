package com.bank.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.enums.RequestStatus;
import com.bank.models.PaginatedResponse;
import com.bank.models.Request;

import exception.QueryException;


public interface RequestService {
    boolean registerRequest(Request request);
    boolean approveUserRequest(long requestId, long adminId);
    boolean isAdminInSameBranch(long adminId, long requestBranchId);
    Request getRequestById(long requestId);
	 List<Long> approveMultipleRequests(List<Long> requestIds, long adminId, String role)throws SQLException, QueryException;
	 boolean rejectUserRequest(long requestId, long adminId, String reason)throws SQLException, QueryException;
	 Map<String, Long> getRequestStatusCounts(String role, long adminId);
	 List<Long> rejectMultipleRequests(List<Long> requestIds, long adminId, String reason, String role) throws SQLException, QueryException ;
	 Request getRequestDetailsById(long requestId) throws SQLException, QueryException ;
	 PaginatedResponse<Request> getRequestList(String adminRole, long id, long fromTimestamp, long toTimestamp, int pageNumber, int pageSize, RequestStatus status)
	            throws SQLException, QueryException
;
}