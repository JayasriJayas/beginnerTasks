package com.bank.dao.impl;

import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.BranchDAO;
import com.bank.models.Branch;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

public class BranchDAOImpl implements BranchDAO {
	
	  @Override
	  public long getBranchIdByAdminId(long adminId) throws SQLException,QueryException {
		   	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		   	qb.select("branchId").from("admin").where("adminId = ?",adminId);	
		    QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
		    List<Map<String,Object>> rs = qe.executeQuery( qb.build(), qb.getParameters());
		    if (rs.isEmpty()) {
	          return -1;
		    }
		    Object value = rs.get(0).get("branchId");
		    return value instanceof Number ? ((Number) value).longValue() : Long.parseLong(value.toString());
	    }
	  
	   @Override
	   public boolean isBranchExits(String ifscCode) throws SQLException, QueryException {
		   QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		   qb.select("1").from("branch").where("ifscCode = ?",ifscCode);
	       QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	       List<Map<String,Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	       return !rs.isEmpty();
		   
	   }
	   
	   @Override
	   public boolean saveBranch(Branch branch,long superadminId) throws SQLException, QueryException{
		   QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	        qb.insertInto("branch",
	                "branchName", "ifscCode","location", "contact", "createdAt","modifiedAt","modifiedBy")
	           .values(branch.getBranchName(),branch.getIfscCode(),branch.getLocation(),branch.getContact(),System.currentTimeMillis(),System.currentTimeMillis(),superadminId);
	        QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	        int rowsAffected = qe.executeUpdate(qb.build(), qb.getParameters());
	        return rowsAffected >0;
	   }
	   @Override
	   public boolean updateBranch(Branch branch) throws SQLException, QueryException {
	       QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	       qb.update("branch");

	       if (branch.getBranchName() != null) qb.set("branchName", branch.getBranchName());
	       if (branch.getIfscCode() != null) qb.set("ifscCode", branch.getIfscCode());
	       if (branch.getLocation() != null) qb.set("location", branch.getLocation());
	       if (branch.getContact() != 0) qb.set("contact", branch.getContact());

	       qb.set("modifiedAt", branch.getModifiedAt());
	       qb.set("modifiedBy", branch.getModifiedBy());

	       qb.where("branchId = ?", branch.getBranchId());

	       QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	       int rowsAffected = qe.executeUpdate(qb.build(), qb.getParameters());
	       return rowsAffected > 0;
	   }


}
