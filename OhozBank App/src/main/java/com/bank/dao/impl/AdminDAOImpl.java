package com.bank.dao.impl;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.AdminDAO;
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

public class AdminDAOImpl implements AdminDAO {

    @Override
    public long getBranchIdByAdminId(long adminId) throws SQLException,QueryException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.select("branchId").from("admin").where("adminId = ?",adminId);
    	
    	 List<Object> params = qb.getParameters();
    	 String query = qb.build();
    	 QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
    	 List<Map<String,Object>> rs = qe.executeQuery(query, params);
    	    if (rs.isEmpty()) {
                return -1;
            }

            Object value = rs.get(0).get("branchId");
            return value instanceof Number ? ((Number) value).longValue() : Long.parseLong(value.toString());
    }
}
