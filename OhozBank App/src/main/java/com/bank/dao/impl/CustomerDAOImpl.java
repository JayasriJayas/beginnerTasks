package com.bank.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.CustomerDAO;
import com.bank.mapper.CustomerMapper;
import com.bank.models.Customer;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

public class CustomerDAOImpl implements CustomerDAO{
	@Override
	public Customer getCustomerByUserId(long userId)throws SQLException,QueryException{
		 QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		 qb.select("*").from("customer").where("userId = ?",userId);
		 QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
		 List<Map<String,Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
		 return CustomerMapper.fromResultSet(rows);
	     
	    }
	}

