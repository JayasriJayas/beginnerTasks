package com.bank.dao.impl;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.AccountDAO;
import com.bank.enums.RequestStatus;
import com.bank.enums.UserStatus;
import com.bank.mapper.AccountMapper;
import com.bank.mapper.AccountRequestMapper;
import com.bank.mapper.CustomerMapper;
import com.bank.mapper.UserMapper;
import com.bank.models.Account;
import com.bank.models.AccountRequest;
import com.bank.models.Customer;
import com.bank.models.Request;
import com.bank.models.User;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public Account getAccountById(long accountId)throws QueryException, SQLException  {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.select().from("account").where("accountId = ?", accountId);
	     QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	     List<Map<String,Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	     return AccountMapper.fromResultSet(rs);
    }

    @Override
    public boolean updateAccount(Account account) throws QueryException, SQLException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.update("account");
    	if(account.getBalance()!=null) {
    		qb.set("balance",account.getBalance());
    	}
    	if(account.getStatus()!=null) {
    		qb.set("status", account.getStatus());
    	}
    	
    	 qb.set("modifiedAt",System.currentTimeMillis())
    	  .set("modifiedBy",account.getModifiedBy()).where("accountId = ?",account.getAccountId());
	     QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
        return qe.executeUpdate(qb.build(), qb.getParameters())> 0;
    }

    @Override
    public long getBranchIdByAccountId(long accountId)throws QueryException, SQLException  {
     	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
     	qb.select("branchId").from("account").where("accountId = ?",accountId);
	    QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	    List<Map<String,Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	    if (rs.isEmpty()) {
            return -1;
        }

        Object value = rs.get(0).get("branchId");
        return value instanceof Number ? ((Number) value).longValue() : Long.parseLong(value.toString());//need to check the return type
     	
    }
    @Override
    public boolean save(AccountRequest request) throws SQLException,QueryException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.insertInto("accountRequest","userId","branchId","status","createdAt").values(request.getUserId(),request.getBranchId(),request.getStatus(),System.currentTimeMillis());

        QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
        return  qe.executeUpdate( qb.build(), qb.getParameters())>0;
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
        QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
        List<Map<String, Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
        if (rs.isEmpty()) {
            return null;
        }
        return AccountRequestMapper.fromResultSet(rs);
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

 
}
