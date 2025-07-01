package com.bank.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.AccountDAO;
import com.bank.mapper.AccountMapper;
import com.bank.models.Account;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public Account getAccountById(long accountId)throws QueryException, SQLException  {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.select().from("account").where("accountId = ?", accountId);
    	try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
	     List<Map<String,Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	     return AccountMapper.fromResultSet(rs);
    	}
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
    	 try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
    	        QueryExecutor qe = new QueryExecutor(conn);
        return qe.executeUpdate(qb.build(), qb.getParameters())> 0;
    	 }
    }

    @Override
    public long getBranchIdByAccountId(long accountId)throws QueryException, SQLException  {
     	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
     	qb.select("branchId").from("account").where("accountId = ?",accountId);
     	try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
	    List<Map<String,Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
	    if (rs.isEmpty()) {
            return -1;
        }
     	

        Object value = rs.get(0).get("branchId");
        return value instanceof Number ? ((Number) value).longValue() : Long.parseLong(value.toString());//need to check the return type
     	}
    }
    
    
    @Override
    public List<Account> getAccountsByBranchId(long branchId) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*").from("account").where("branchId = ?", branchId);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
        List<Map<String, Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());

        return AccountMapper.mapToAccounts(rs);
    }
    }

    @Override
    public List<Account> getAllAccounts() throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*").from("account");
        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
         
        List<Map<String, Object>> rs = qe.executeQuery(qb.build());

       
        return AccountMapper.mapToAccounts(rs);
    }
    }
    @Override
    public boolean isAccountInBranch(long accountId, long branchId) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("accountId") 
          .from("account")
          .where("accountId = ?", accountId)
          .andWhere("branchId = ?", branchId);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> result = qe.executeQuery(qb.build(), qb.getParameters());

            
            return !result.isEmpty();
        }
    }
    @Override
    public List<Account> getAccountsByUserId(long userId) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*").from("account").where("userId = ?", userId);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> result = qe.executeQuery(qb.build(), qb.getParameters());
            return AccountMapper.mapToAccounts(result);
        }
    }
    @Override
    public BigDecimal getTotalBalanceByUser(long userId) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("SUM", "balance").as("total")
          .from("account")
          .where("userId = ?", userId)
          .andWhere("status = ?", "ACTIVE");

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());

            if (!rows.isEmpty()) {
                Object totalObj = rows.get(0).get("total");
                return totalObj != null ? new BigDecimal(totalObj.toString()) : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        }
    }
    @Override
    public int countAccountsByBranch(Long branchId) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").as("total")
          .from("account")
          .where("branchId = ?", branchId);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> result = executor.executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
        }
    }
    @Override
    public int countAllAccounts() throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").as("total").from("account");

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> result = executor.executeQuery(qb.build());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
        }
    }





}
