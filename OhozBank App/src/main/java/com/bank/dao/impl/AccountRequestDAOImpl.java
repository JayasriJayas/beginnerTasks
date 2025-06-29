package com.bank.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
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
	public List<AccountRequest> fetchAllRequests(long fromTimestamp, long toTimestamp, int limit, int offset,RequestStatus status) throws SQLException, QueryException {
	    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	    qb.select("*")
	      .from("accountRequest");
	      if (status != null) {
	          qb.where("ar.status = ?", status.name());
	      }

	      qb.andBetween("createdAt", fromTimestamp, toTimestamp)
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
	public int countRequests(long fromTimestamp, long toTimestamp,RequestStatus status) throws SQLException, QueryException {
	    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	    qb.select().aggregate("COUNT", "*").as("total")
	      .from("accountRequest");
	      if (status != null) {
	          qb.where("ar.status = ?", status.name());
	      }

	      qb.andBetween("createdAt", fromTimestamp, toTimestamp);  // Use your timestamp column

	    try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	        QueryExecutor qe = new QueryExecutor(conn);
	        List<Map<String, Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	        return rs.isEmpty() ? 0 : ((Number) rs.get(0).get("total")).intValue();
	    }
	}
	@Override
	public List<AccountRequest> fetchRequestsByAdminBranch(long adminId, long fromTimestamp, long toTimestamp, int limit, int offset,RequestStatus status) throws SQLException, QueryException {
	    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	    qb.select("ar.*")
	      .from("accountRequest ar")
	      .innerJoin("admin a", "a.branchId = ar.branchId")
	      .where("a.adminId = ?", adminId);
	      if (status != null) {
	          qb.andWhere("ar.status = ?", status.name());
	      }

	      qb.andBetween("ar.createdAt", fromTimestamp, toTimestamp)
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
	public int countRequestsByBranch(long adminId, long fromTimestamp, long toTimestamp,RequestStatus status) throws SQLException, QueryException {
	    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	    qb.select().aggregate("COUNT", "*").as("total")
	      .from("accountRequest ar")
	      .innerJoin("admin a", "a.branchId = ar.branchId")
	      .where("a.adminId = ?", adminId);
	      if (status != null) {
	          qb.andWhere("ar.status = ?", status.name());
	      }

	      qb.andBetween("ar.createdAt", fromTimestamp, toTimestamp);

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

	   
	         updateAccountRequest(conn, requestId,adminId); 
	   
	        conn.commit();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    
	        throw new SQLException(e.getMessage());
	   
        }
    }

    public AccountRequest getAccountRequest(long requestId) throws SQLException, QueryException {
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

  
    private boolean updateAccountRequest(Connection conn, long requestId,long adminId) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.update("accountRequest").set("status", RequestStatus.APPROVED).set("approvedBy", adminId).set("approvedAt", System.currentTimeMillis()).where("requestId = ?", requestId);
        QueryExecutor qe = new QueryExecutor(conn);
        return qe.executeUpdate(qb.build(), qb.getParameters()) > 0;
    }
    @Override
    public List<AccountRequest> findPendingRequestsByUserId(long userId,RequestStatus status) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*")
          .from("accountRequest")
          .where("userId = ?", userId)
          .andWhere("status = ?", status); 

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
            return AccountRequestMapper.mapToRequests(rows);
        }
    }
    @Override
    public boolean rejectRequest(long requestId, long adminId, String reason) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.update("accounRequest")
          .set("status", RequestStatus.REJECTED.name())
          .set("rejectionReason", reason)
          .set("approvedBy", adminId)
          .set("approvedAt", System.currentTimeMillis())
          .where("requestId = ?", requestId);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            return new QueryExecutor(conn).executeUpdate(qb.build(), qb.getParameters()) > 0;
        }
    }
    @Override
    public Map<String, Long> getStatusCounts(Long branchId) throws SQLException, QueryException {
        Map<String, Long> resultMap = new HashMap<>();
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("status", "COUNT(*) AS count").from("accountRequest");

        if (branchId != null) {
            qb.where("branchId = ?", branchId);
        }

        qb.groupBy("status");

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn).executeQuery(qb.build(), qb.getParameters());

            for (Map<String, Object> row : result) {
                String status = String.valueOf(row.get("status"));
                Long count = ((Number) row.get("count")).longValue();
                resultMap.put(status, count);
            }
            return resultMap;
        }
    }
   


    
}
