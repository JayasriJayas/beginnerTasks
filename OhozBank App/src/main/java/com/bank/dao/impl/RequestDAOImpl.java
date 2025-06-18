package com.bank.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
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
	 public List<Request> getPendingRequests() throws SQLException, QueryException {
		 QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		 qb.select("*").from("request").where("status = ?",RequestStatus.PENDING);
		 try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
		        QueryExecutor qe = new QueryExecutor(conn);
		       
		 List<Map<String,Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
		 return RequestMapper.toMapResult(rows);
		 }
	 }
	 
	 @Override
	 public List<Request>  getPendingRequestsByBranch(long branchId)  throws SQLException, QueryException {
		 QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		 qb.select("*").from("request").where("branchId = ?",branchId ).andWhere("status = ?",RequestStatus.PENDING);
		 String query = qb.build();
		 try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
		        QueryExecutor qe = new QueryExecutor(conn);
		 List<Map<String,Object>> rows = qe.executeQuery(query, qb.getParameters());
		 return RequestMapper.toMapResult(rows);
		 }
	 }

	 
}

