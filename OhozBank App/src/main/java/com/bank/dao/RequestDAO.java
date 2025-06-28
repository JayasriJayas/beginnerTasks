package com.bank.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.models.Request;

import exception.QueryException;

public interface RequestDAO {
	int saveRequest(Request request) throws QueryException, SQLException;
	Request getRequestById(long id)throws QueryException, SQLException;
	List<Request> getPendingRequestsWithDateRange(long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException;
	int countRequestsWithDateRange(long fromTimestamp, long toTimestamp) throws SQLException, QueryException ;
	List<Request> getPendingRequestsByBranchWithDateRange(long branchId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException ;
	int countRequestsByBranchWithDateRange(long branchId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException;
	boolean rejectRequest(long requestId, long adminId, String reason) throws SQLException, QueryException;
	Map<String, Long> getRequestStatusCounts(Long branchId) throws SQLException, QueryException ;
	 Request fetchRequestById(long requestId) throws SQLException, QueryException;

}
