package com.dialect;

import com.querybuilder.QueryBuilder;

public interface DatabaseDialect {
	String buildQuery(QueryBuilder builder);

}
