package com.bank.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.enums.RequestStatus;
import com.bank.models.AccountRequest;

import exception.QueryException;

public interface AccountRequestDAO {
	 List<AccountRequest> fetchAllRequests(long fromTimestamp, long toTimestamp, int limit, int offset,RequestStatus status) throws SQLException, QueryException;
	 int countRequests(long fromTimestamp, long toTimestamp,RequestStatus status) throws SQLException, QueryException ;
	 List<AccountRequest> fetchRequestsByAdminBranch(long adminId, long fromTimestamp, long toTimestamp, int limit, int offset,RequestStatus status) throws SQLException, QueryException;
	 int countRequestsByBranch(long adminId, long fromTimestamp, long toTimestamp,RequestStatus status) throws SQLException, QueryException;
    boolean save(AccountRequest request) throws SQLException,QueryException;
    List<AccountRequest> findPendingRequestsByUserId(long userId,RequestStatus status) throws SQLException, QueryException;
    boolean approveRequest(long requestId,long adminId) throws SQLException,QueryException;
    
    boolean rejectRequest(long requestId, long adminId, String reason) throws SQLException, QueryException;
    Map<String, Long> getStatusCounts(Long branchId) throws SQLException, QueryException;
    AccountRequest getAccountRequest(long requestId) throws SQLException, QueryException;

}
