package com.dialect;

import com.querybuilder.QueryBuilder;

import exception.QueryException;

public interface DatabaseDialect {
	String buildQuery(QueryBuilder builder) throws QueryException;

}
