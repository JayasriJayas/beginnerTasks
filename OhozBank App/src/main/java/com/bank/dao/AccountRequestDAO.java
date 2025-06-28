package com.bank.dao;

import java.sql.SQLException;
import java.util.List;

import com.bank.models.AccountRequest;

import exception.QueryException;

public interface AccountRequestDAO {
	 List<AccountRequest> fetchAllRequests(long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	 int countRequests(long fromTimestamp, long toTimestamp) throws SQLException, QueryException ;
	 List<AccountRequest> fetchRequestsByAdminBranch(long adminId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	 int countRequestsByBranch(long adminId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;
    boolean save(AccountRequest request) throws SQLException,QueryException;
    boolean approveRequest(long requestId,long adminId) throws SQLException,QueryException;
    List<AccountRequest> findPendingRequestsByUserId(long userId) throws SQLException, QueryException;

}
