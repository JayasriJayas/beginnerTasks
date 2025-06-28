package com.bank.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.AccountRequestDAO;
import com.bank.enums.RequestStatus;
import com.bank.enums.UserStatus;
import com.bank.mapper.AccountRequestMapper;
import com.bank.models.Account;
import com.bank.models.AccountRequest;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

public class AccountRequestDAOImpl implements AccountRequestDAO {

	@Override
	public List<AccountRequest> fetchAllRequests(long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException {
	    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	    qb.select("*")
	      .from("accountRequest")
	      .andBetween("createdAt", fromTimestamp, toTimestamp)  // Use your actual timestamp column
	      .orderBy("createdAt").orderDirection("DESC")
	      .limit(limit)
	      .offset(offset);

	    try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	        QueryExecutor qe = new QueryExecutor(conn);
	        List<Map<String, Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	        return AccountRequestMapper.mapToRequests(rs);
	    }
	}
	@Override
	public int countRequests(long fromTimestamp, long toTimestamp) throws SQLException, QueryException {
	    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	    qb.select().aggregate("COUNT", "*").as("total")
	      .from("accountRequest")
	      .andBetween("createdAt", fromTimestamp, toTimestamp);  // Use your timestamp column

	    try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	        QueryExecutor qe = new QueryExecutor(conn);
	        List<Map<String, Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	        return rs.isEmpty() ? 0 : ((Number) rs.get(0).get("total")).intValue();
	    }
	}
	@Override
	public List<AccountRequest> fetchRequestsByAdminBranch(long adminId, long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException {
	    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	    qb.select("ar.*")
	      .from("accountRequest ar")
	      .innerJoin("admin a", "a.branchId = ar.branchId")
	      .where("a.adminId = ?", adminId)
	      .andBetween("ar.createdAt", fromTimestamp, toTimestamp)
	      .orderBy("ar.createdAt").orderDirection("DESC")
	      .limit(limit)
	      .offset(offset);

	    try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	        QueryExecutor qe = new QueryExecutor(conn);
	        List<Map<String, Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	        return AccountRequestMapper.mapToRequests(rs);
	    }
	}
	@Override
	public int countRequestsByBranch(long adminId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException {
	    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	    qb.select().aggregate("COUNT", "*").as("total")
	      .from("accountRequest ar")
	      .innerJoin("admin a", "a.branchId = ar.branchId")
	      .where("a.adminId = ?", adminId)
	      .andBetween("ar.createdAt", fromTimestamp, toTimestamp);

	    try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	        QueryExecutor qe = new QueryExecutor(conn);
	        List<Map<String, Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	        return rs.isEmpty() ? 0 : ((Number) rs.get(0).get("total")).intValue();
	    }
	}
    @Override
    public boolean save(AccountRequest request) throws SQLException,QueryException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.insertInto("accountRequest","userId","branchId","status","createdAt").values(request.getUserId(),request.getBranchId(),request.getStatus(),System.currentTimeMillis());

    	try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
        return  qe.executeUpdate( qb.build(), qb.getParameters())>0;
    	}
    }
    @Override
    public  boolean approveRequest(long requestId,long adminId) throws SQLException,QueryException{
    	try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	        conn.setAutoCommit(false);
	        
	       System.out.println("i am hers");
	        AccountRequest req = getAccountRequest(requestId);
	        if (req == null) {
	            conn.rollback();
	            return false; 
	        }
	        
	        long userId = req.getUserId(); 
	        long branchId = req.getBranchId(); 
	
	        Account account = new Account();
	        account.setUserId(userId); 
	        account.setBranchId(branchId); 
	        account.setBalance(BigDecimal.ZERO);
	        account.setStatus(UserStatus.ACTIVE);
	        account.setCreatedAt(System.currentTimeMillis());
	        account.setModifiedAt(System.currentTimeMillis());
	        account.setModifiedBy(adminId);
	       
	        insertAccount(conn, account); 

	   
	         deleteAccountRequest(conn, requestId); 
	   
	        conn.commit();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    
	        throw new SQLException(e.getMessage());
	   
        }
    }

    private AccountRequest getAccountRequest(long requestId) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().from("accountRequest").where("requestId = ?", requestId);
        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
        List<Map<String, Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
        if (rs.isEmpty()) {
            return null;
        }
        return AccountRequestMapper.fromResultSet(rs);
        }
    }
    
    private boolean insertAccount(Connection conn, Account account) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.insertInto("account", "userId", "branchId", "balance", "status", "createdAt", "modifiedAt", "modifiedBy")
          .values(account.getUserId(), account.getBranchId(), account.getBalance(), account.getStatus().name(), 
                  account.getCreatedAt(), account.getModifiedAt(), account.getModifiedBy());
        QueryExecutor qe = new QueryExecutor(conn);
        return qe.executeUpdate(qb.build(), qb.getParameters()) > 0;
    }

  
    private boolean deleteAccountRequest(Connection conn, long requestId) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.deleteFrom("accountRequest").where("requestId = ?", requestId);
        QueryExecutor qe = new QueryExecutor(conn);
        return qe.executeUpdate(qb.build(), qb.getParameters()) > 0;
    }
    @Override
    public List<AccountRequest> findPendingRequestsByUserId(long userId) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*")
          .from("accountRequest")
          .where("userId = ?", userId)
          .andWhere("status = ?", RequestStatus.PENDING);  // status column expected to hold 'PENDING'

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
            return AccountRequestMapper.mapToRequests(rows);
        }
    }


    
}
