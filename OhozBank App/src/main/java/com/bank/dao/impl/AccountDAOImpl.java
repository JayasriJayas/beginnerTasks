package com.bank.dao.impl;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.AccountDAO;
import com.bank.mapper.AccountMapper;
import com.bank.models.Account;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public Account getAccountById(long accountId)throws QueryException, SQLException  {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.select().from("account").where("accountId = ?", accountId);
    	String query = qb.build();
	     List<Object> params = qb.getParameters();
	     QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	     List<Map<String,Object>> rs = qe.executeQuery(query, params);
	     return AccountMapper.fromResultSet(rs);
    }

    @Override
    public boolean updateAccount(Account account) throws QueryException, SQLException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.update("account").set("balance",account.getBalance())
    	  .set("modifiedAt",System.currentTimeMillis())
    	  .set("modifiedBy",account.getModifiedBy()).where("accountId = ?",account.getAccountId());
    	String query = qb.build();
	     List<Object> params = qb.getParameters();
	     QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
        return qe.executeUpdate(query, params)> 0;
    }

    @Override
    public long getBranchIdByAccountId(long accountId)throws QueryException, SQLException  {
     	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
     	qb.select("branchId").from("account").where("accountId = ?",accountId);
     	String query = qb.build();
	    List<Object> params = qb.getParameters();
	    QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	    List<Map<String,Object>> rs = qe.executeQuery(query, params);
	    if (rs.isEmpty()) {
            return -1;
        }

        Object value = rs.get(0).get("branchId");
        return value instanceof Number ? ((Number) value).longValue() : Long.parseLong(value.toString());
     	
    }
}
