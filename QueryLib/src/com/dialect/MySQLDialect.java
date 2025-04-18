package com.dialect;

import java.util.stream.Collectors;

import com.querybuilder.QueryBuilder;

public class MySQLDialect {
	 public String buildQuery(QueryBuilder qb) {
	        StringBuilder sql = new StringBuilder();
//	        switch (qb.getQueryType()) {
//	        
//	        	
//	            case "SELECT":
//	                sql.append("SELECT ")
//	                   .append(qb.getColumns().isEmpty() ? "*" : String.join(", ", qb.getColumns()))
//	                   .append(" FROM ").append(qb.getTable()).append(" ");
//	                break;
//
//	            case "INSERT":
//	                sql.append("INSERT INTO ").append(qb.getTable())
//	                   .append(" (").append(String.join(", ", qb.getColumns())).append(")")
//	                   .append(" VALUES (")
//	                   .append(qb.getValues().stream().map(v -> "?").collect(Collectors.joining(", ")))
//	                   .append(") ");
//	                break;
//
//	            case "UPDATE":
//	                sql.append("UPDATE ").append(qb.getTable())
//	                   .append(" SET ")
//	                   .append(String.join(", ", qb.getSetClauses())).append(" ");
//	                break;
//
//	            case "DELETE":
//	                sql.append("DELETE FROM ").append(qb.getTable()).append(" ");
//	                break;
//	            
//	        }
//
//	        if (!qb.getJoinClauses().isEmpty()) {
//	            sql.append(String.join(" ", qb.getJoinClauses())).append(" ");
//	        }
//
//	        if (!qb.getWhereConditions().isEmpty()) {
//	            sql.append("WHERE ").append(String.join(" AND ", qb.getWhereConditions())).append(" ");
//	        }
//
//	        if (!qb.getGroupByColumns().isEmpty()) {
//	            sql.append("GROUP BY ").append(String.join(", ", qb.getGroupByColumns())).append(" ");
//	        }
//
//	        if (!qb.getOrderByColumns().isEmpty()) {
//	            sql.append("ORDER BY ").append(String.join(", ", qb.getOrderByColumns())).append(" ");
//	        }

	        return sql.toString().trim();
	    }
}
