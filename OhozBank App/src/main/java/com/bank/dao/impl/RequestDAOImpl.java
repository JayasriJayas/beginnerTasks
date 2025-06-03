package com.bank.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.RequestDAO;
import com.bank.mapper.RequestMapper;
import com.bank.mapper.TransactionMapper;
import com.bank.models.Request;
import com.dialect.MySQLDialect;
import com.querybuilder.DBConnector;
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

	        QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	        return  qe.executeUpdate( qb.build(), qb.getParameters());
	        
	    }
	 @Override
	 public Request getRequestById(long id) throws QueryException, SQLException {
	     QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	     qb.select("*").from("request").where("id = ?",id);
	     QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	     List<Map<String,Object>> rs = qe.executeQuery( qb.build(),qb.getParameters());
	     return RequestMapper.fromResultSet(rs);

	 }
	 
	 @Override
	 public List<Request> getPendingRequests() throws SQLException, QueryException {
		 QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		 qb.select("*").from("request").where("status = ?","PENDING");
		 QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
		 List<Map<String,Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
		 return RequestMapper.toMapResult(rows);
	 }
	 
	 @Override
	 public List<Request>  getPendingRequestsByBranch(long branchId)  throws SQLException, QueryException {
		 QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		 qb.select("*").from("request").where("branchId = ?",branchId ).andWhere("status = ?","PENDING");
		 QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
		 List<Map<String,Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
		 return RequestMapper.toMapResult(rows);
	 }

	 
}

