package com.bank.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.BranchDAO;
import com.bank.mapper.BranchMapper;
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
		   	try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
		        QueryExecutor qe = new QueryExecutor(conn);
		    List<Map<String,Object>> rs = qe.executeQuery( qb.build(), qb.getParameters());
		    if (rs.isEmpty()) {
	          return -1;
		    }
		    Object value = rs.get(0).get("branchId");
		    return value instanceof Number ? ((Number) value).longValue() : Long.parseLong(value.toString());
	    }
	  }
	  
	   @Override
	   public boolean isBranchExits(String ifscCode) throws SQLException, QueryException {
		   QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		   qb.select("1").from("branch").where("ifscCode = ?",ifscCode);
		   try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
		        QueryExecutor qe = new QueryExecutor(conn);
	       List<Map<String,Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	       return !rs.isEmpty();
		   }
		   
	   }
	   
	   @Override
	   public boolean saveBranch(Branch branch,long superadminId) throws SQLException, QueryException{
		   QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	        qb.insertInto("branch",
	                "branchName", "ifscCode","location", "contact", "createdAt","modifiedAt","modifiedBy")
	           .values(branch.getBranchName(),branch.getIfscCode(),branch.getLocation(),branch.getContact(),System.currentTimeMillis(),System.currentTimeMillis(),superadminId);
	        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	            QueryExecutor qe = new QueryExecutor(conn);
	        int rowsAffected = qe.executeUpdate(qb.build(), qb.getParameters());
	        return rowsAffected >0;
	        }
	   }
	   @Override
	   public boolean updateBranch(Branch branch) throws SQLException, QueryException {
		
	       QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	       qb.update("branch");

	       if (branch.getBranchName() != null) qb.set("branchName", branch.getBranchName());
	       if (branch.getIfscCode() != null) qb.set("ifscCode", branch.getIfscCode());
	       if (branch.getLocation() != null) qb.set("location", branch.getLocation());
	       if (branch.getContact() != null) qb.set("contact", branch.getContact());

	       qb.set("modifiedAt", branch.getModifiedAt());
	       qb.set("modifiedBy", branch.getModifiedBy());

	       qb.where("branchId = ?", branch.getBranchId());

	       try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	           QueryExecutor qe = new QueryExecutor(conn);
	       int rowsAffected = qe.executeUpdate(qb.build(), qb.getParameters());

	       return rowsAffected > 0;
	       }
	   }
	   @Override
	   public Branch getBranchById(long branchId) throws SQLException, QueryException {
	       QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	       qb.select("*").from("branch").where("branchId = ?", branchId);

	       try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	           QueryExecutor qe = new QueryExecutor(conn);
	       List<Map<String, Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	       return BranchMapper.fromResultSet(rs);
	       }
	   }

	   @Override
	   public List<Branch> getAllBranches(int limit, int offset) throws SQLException, QueryException {
	       QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	       qb.select("*")
	         .from("branch")
	         .limit(limit)
	         .offset(offset);

	       try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	           QueryExecutor qe = new QueryExecutor(conn);
	           List<Map<String, Object>> rs = qe.executeQuery(qb.build());
	           return BranchMapper.toMapResult(rs);
	       }
	   }
	   @Override
	   public int countGetAllBranch() throws SQLException, QueryException {
	       QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	       qb.select("COUNT(*) AS total")
	         .from("branch");

	       try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	    	    List<Map<String, Object>> result = new QueryExecutor(conn)
	    	        .executeQuery(qb.build());

	    	    return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
	    	}

	   }


	   @Override
	    public Branch findByIfscCode(String ifscCode)throws SQLException, QueryException {
		   QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	       qb.select("*").from("branch").where("ifscCode = ?", ifscCode);
	        String sql = "SELECT * FROM branch WHERE ifscCode = ?";
	        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
		           QueryExecutor qe = new QueryExecutor(conn);
		       List<Map<String, Object>> rs = qe.executeQuery(qb.build(),qb.getParameters());
		       return BranchMapper.fromResultSet(rs);
	    }

	   }
	   @Override
	   public int countAllBranches() throws SQLException, QueryException {
	       QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	       qb.select().aggregate("COUNT", "*").as("total").from("branch");

	       try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	           QueryExecutor qe = new QueryExecutor(conn);
	           List<Map<String, Object>> result = qe.executeQuery(qb.build());
	           return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
	       }
	   }
	   @Override
	   public List<Map<String, Object>> getBranchFunds() throws SQLException, QueryException {
	       QueryBuilder qb = new QueryBuilder(new MySQLDialect());

	       qb.select("b.branchId")
	         .select("b.branchName")
	         .aggregate("SUM", "a.balance").as("totalFunds")
	         .from("branch b")
	         .leftJoin("account a", "b.branchId = a.branchId")
	         .groupBy("b.branchId")
	         .groupBy("b.branchName");

	       try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	           QueryExecutor executor = new QueryExecutor(conn);
	           return executor.executeQuery(qb.build());
	       }
	   }
	   @Override
	   public List<Map<String, Object>> getAllBranches() throws SQLException, QueryException {
	       QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	       qb.select("*").from("branch");

	       try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	           QueryExecutor qe = new QueryExecutor(conn);
	           return qe.executeQuery(qb.build());
	       }
	   }
	   public Map<Long, String> getBranchesWithoutAdmin() throws SQLException, QueryException {
		   
		    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		    
		    qb.select("branchId", "branchName")
		      .from("branch")
		      .where("adminId IS NULL");

		    Map<Long,String> branches = new HashMap<>();

		    try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
		        QueryExecutor qe = new QueryExecutor(conn);
		        List<Map<String, Object>> result = qe.executeQuery(qb.build(), qb.getParameters());

		       
		        for (Map<String, Object> row : result) {
		            Long branchId = ((Number) row.get("branchId")).longValue(); 
		            String branchName = (String) row.get("branchName"); 
		            branches.put(branchId, branchName); 
		        }
		    }
		    return branches;
		}



	   

	   


}
