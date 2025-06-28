package com.bank.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.RequestDAO;
import com.bank.enums.RequestStatus;
import com.bank.mapper.RequestMapper;
import com.bank.models.Request;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

public class RequestDAOImpl implements RequestDAO {
	 @Override
	    public int saveRequest(Request req) throws QueryException, SQLException {
	        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	        qb.insertInto("request",
	                "username", "password","name", "email", "phone", "gender", "dob", "address",
	                "maritalStatus", "aadharNo", "panNo", "branchId", "occupation",
	                "annualIncome", "status","requestDate")
	          .values(
	              req.getUsername(), req.getPassword(),req.getName(), req.getEmail(), String.valueOf(req.getPhone()), req.getGender(),
	              req.getDob().toString(), req.getAddress(), req.getMaritalStatus(), String.valueOf(req.getAadharNo()),
	              String.valueOf(req.getPanNo()), String.valueOf(req.getBranchId()),
	              req.getOccupation(), String.valueOf(req.getAnnualIncome()),
	              String.valueOf(req.getStatus()),req.getRequestTimestamp()
	          );

	        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	            QueryExecutor qe = new QueryExecutor(conn);
	        return  qe.executeUpdate( qb.build(), qb.getParameters());
	        }
	        
	    }
	 @Override
	 public Request getRequestById(long id) throws QueryException, SQLException {
	     QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	     qb.select("*").from("request").where("id = ?",id);
	     try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	         QueryExecutor qe = new QueryExecutor(conn);
	     List<Map<String,Object>> rs = qe.executeQuery( qb.build(),qb.getParameters());
	     return RequestMapper.fromResultSet(rs);
	     }
	 }
	 

	 @Override
	 public List<Request> getPendingRequestsWithDateRange(long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException {
	     QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	     qb.select("*")
	       .from("request")
	       .where("status = ?", RequestStatus.PENDING)  // Filter by status
	       .andBetween("requestDate", fromTimestamp, toTimestamp)  // Updated column name
	       .orderBy("requestDate")  // Order by the updated column name
	       .limit(limit)  // Apply pagination limit
	       .offset(offset);  // Apply pagination offset
	     
	     String query = qb.build();
	     System.out.println(query);  // Print the query for debugging
	     System.out.println(qb.getParameters());  // Print query parameters

	     try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	         QueryExecutor qe = new QueryExecutor(conn);
	         List<Map<String, Object>> rows = qe.executeQuery(query, qb.getParameters());
	         return RequestMapper.toMapResult(rows);  // Assuming you have a mapper to convert rows to Request objects
	     }
	 }

	 @Override
	 public int countRequestsWithDateRange(long fromTimestamp, long toTimestamp) throws SQLException, QueryException {
	     QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	     qb.select().aggregate("COUNT", "*").as("total")
	       .from("request")
	       .where("status = ?", RequestStatus.PENDING)  // Assuming "PENDING" is the status you're filtering by
	       .andBetween("requestDate", fromTimestamp, toTimestamp);  // Filter by date range

	     try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	         List<Map<String, Object>> result = new QueryExecutor(conn).executeQuery(qb.build(), qb.getParameters());
	         return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();  // Return the count
	     }
	 }
	 @Override
	 public List<Request> getPendingRequestsByBranchWithDateRange(long branchId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException {
	     QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	     qb.select("*")
	       .from("request")
	       .where("branchId = ?", branchId)  	       .
	       andWhere("status = ?", RequestStatus.PENDING) 
	       .andBetween("requestDate", fromTimestamp, toTimestamp)  
	       .orderBy("requestDate")  
	       .limit(limit)
	       .offset(offset);  

	     try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	         QueryExecutor qe = new QueryExecutor(conn);
	         List<Map<String, Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
	         return RequestMapper.toMapResult(rows);  // Assuming you have a mapper that converts rows into Request objects
	     }
	 }
	 @Override
	 public int countRequestsByBranchWithDateRange(long branchId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException {
	     QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	     qb.select().aggregate("COUNT", "*").as("total")
	       .from("request")
	       .where("branchId = ?", branchId)  // Filter by branch
	       .andWhere("status = ?", RequestStatus.PENDING)  // Filter by status
	       .andBetween("requestDate", fromTimestamp, toTimestamp);  // Filter by date range

	     try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	         List<Map<String, Object>> result = new QueryExecutor(conn).executeQuery(qb.build(), qb.getParameters());
	         return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();  // Return the count
	     }
	 }
	 @Override
	 public boolean rejectRequest(long requestId, long adminId, String reason) throws SQLException, QueryException {
	     QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	     qb.update("request")
	       .set("status", RequestStatus.REJECTED)
	       .set("rejectionReason", reason)
	       .set("processedBy", adminId)
	       .set("processedTimestamp", System.currentTimeMillis())
	       .where("id = ?", requestId);

	     try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	         QueryExecutor executor = new QueryExecutor(conn);
	         return executor.executeUpdate(qb.build(), qb.getParameters()) > 0;
	     }
	 }
	 @Override
	 public Map<String, Long> getRequestStatusCounts(Long branchId) throws SQLException, QueryException {
	     Map<String, Long> resultMap = new HashMap<>();

	     QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	     qb.select("status", "COUNT(*) AS count").from("request");

	     if (branchId != null) {
	         qb.where("branchId = ?", branchId);
	     }

	     qb.groupBy("status");
	     List<Object> params = qb.getParameters();
	     if (params == null) {
	         params = new ArrayList<>();
	     }


	     try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	         QueryExecutor qe = new QueryExecutor(conn);
	         List<Map<String, Object>> rows = qe.executeQuery(qb.build(), params);

	         for (Map<String, Object> row : rows) {
	             String status = (String) row.get("status");
	             Long count = ((Number) row.get("count")).longValue();
	             resultMap.put(status, count);
	         }

	         return resultMap;
	     }
	 }
	 @Override
	 public Request fetchRequestById(long requestId) throws SQLException, QueryException {
	     QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	     qb.select("*").from("request").where("id = ?", requestId);

	     try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	         QueryExecutor executor = new QueryExecutor(conn);
	         List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());

	         return RequestMapper.fromResultSet(rows);
	     }
	 }







	 
}

