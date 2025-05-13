package com.bank.dao;

import java.sql.SQLException;

import com.bank.models.Request;

import exception.QueryException;

public interface RequestDAO {
	int saveRequest(Request request) throws QueryException, SQLException;
	Request getRequestById(long id)throws QueryException, SQLException;

}
