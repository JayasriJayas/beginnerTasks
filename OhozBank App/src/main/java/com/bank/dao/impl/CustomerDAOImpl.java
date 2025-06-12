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
	@Override
	public boolean updateCustomerProfile(Customer customer) throws SQLException, QueryException {
		QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		qb.update("customer");

	    if (customer.getAddress() != null) {
	    	qb.set("address",customer.getAddress());
	    }
	    if (customer.getDob() != null) {
	    	qb.set("dob",customer.getDob());
	    }
	    if (customer.getMaritalStatus() != null) {
	    	qb.set("maritalStatus",customer.getMaritalStatus());
	    }
	    if (customer.getOccupation() != null) {
	    	qb.set("occupation",customer.getOccupation());
	    }
	    if (customer.getAnnualIncome() > 0) {
	    	qb.set("annualIncome",customer.getAnnualIncome());
	    }
	    qb.where("userId =?", customer.getUserId());
	    QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
    	int rowsAffected = qe.executeUpdate( qb.build(),qb.getParameters());
	    return rowsAffected >0;

	}

	}

