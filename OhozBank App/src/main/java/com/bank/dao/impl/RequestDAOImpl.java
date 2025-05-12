package com.bank.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.RequestDAO;
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
	        qb.insertInto("requests",
	                "username", "password", "email", "phone", "gender", "dob", "address",
	                "maritalStatus", "aadharNo", "panNo", "branchId", "branchName", "occupation",
	                "annualIncome", "status")
	          .values(
	              req.getUsername(), req.getPassword(), req.getEmail(), String.valueOf(req.getPhone()), req.getGender(),
	              req.getDob().toString(), req.getAddress(), req.getMaritalStatus(), String.valueOf(req.getAadharNo()),
	              String.valueOf(req.getPanNo()), String.valueOf(req.getBranchId()), req.getBranchName(),
	              req.getOccupation(), String.valueOf(req.getAnnualIncome()),
	              String.valueOf(req.getStatus())
	          );
	        String query = qb.build(); 
	        List<Object> params = qb.getParameters();
	        QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	        qe.execute(query, params);
	        int rowsAffected = qe.execute(query, params);
	        
	        return rowsAffected;
	    }
	 @Override
	 public Request getRequestById(long id) throws Exception {
	     QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	     qb.select("*").from("requests").where("id = ?");
	     String query = qb.build();
	     List<Object> params = qb.getParameters();
	     params.add(id); 
	     QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	     qe.execute(query, params);

	     try (
	         Connection conn = DBConnectionPool.getInstance().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)
	     ) {
	         for (int i = 0; i < params.size(); i++) {
	             stmt.setObject(i + 1, params.get(i));
	         }

	         try (ResultSet rs = stmt.executeQuery()) {
	        	    if (rs.next()) {
	        	        return RequestMapper.fromResultSet(rs);
	        	    }
	        	}

	         }
	     } catch (Exception e) {
	         e.printStackTrace();
	     }

	     return null;
	 }


	 
}

