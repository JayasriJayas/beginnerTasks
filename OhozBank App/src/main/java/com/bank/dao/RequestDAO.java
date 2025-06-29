package com.bank.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.enums.RequestStatus;
import com.bank.models.Request;

import exception.QueryException;

public interface RequestDAO {
	int saveRequest(Request request) throws QueryException, SQLException;
	Request getRequestById(long id)throws QueryException, SQLException;
	boolean rejectRequest(long requestId, long adminId, String reason) throws SQLException, QueryException;
	Map<String, Long> getRequestStatusCounts(Long branchId) throws SQLException, QueryException ;
	 Request fetchRequestById(long requestId) throws SQLException, QueryException;
	 List<Request> getRequestsByDateAndStatus(long from, long to, int limit, int offset, RequestStatus status) throws SQLException, QueryException;
	 int countRequestsByDateAndStatus(long from, long to, RequestStatus status) throws SQLException, QueryException ;
	 List<Request> getRequestsByBranchDateAndStatus(long branchId, long from, long to, int limit, int offset, RequestStatus status) throws SQLException, QueryException;
	 int countRequestsByBranchDateAndStatus(long branchId, long from, long to, RequestStatus status) throws SQLException, QueryException ;
}
