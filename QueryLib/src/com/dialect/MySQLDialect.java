package com.dialect;

import com.querybuilder.QueryBuilder;

import exception.QueryException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MySQLDialect implements DatabaseDialect {

    @Override
    public String buildQuery(QueryBuilder qb) throws QueryException {
        StringBuilder sql = new StringBuilder();
        List<String> whereConditions = qb.getWhereConditions();
        List<String> whereOperators = qb.getWhereOperators();
        List<String> orderByColumns = qb.getOrderByColumns();
        List<String> groupByColumns = qb.getGroupByColumns();
        List<String> havingConditions = qb.getHavingConditions();
        List<String> havingOperators = qb.getHavingOperators();
        String unionClause = qb.getUnionClause();
        String orderByDirection = qb.getOrderDirection();
        int limit = qb.getLimit();
        int offset= qb.getOffset();
        
        switch (qb.getQueryType()) {
        
            case "SELECT":
            	List<String> columns =qb.getColumns();
            	List<String> joins = qb.getJoins();
            	List<String> subSelects = qb.getSubSelectColumns();
            	 List<String> subFromClauses = qb.getSubQueryFromClauses();
                sql.append("SELECT ");
                if (qb.isDistinct()) {
                    sql.append("DISTINCT ");
                }
                
                if (subSelects != null && !subSelects.isEmpty()) {
                    sql.append(String.join(", ", subSelects));
                } else if (columns != null && !columns.isEmpty()) {
                    sql.append(String.join(", ", columns));
                } else {
                    sql.append("*");
                }

               
                if (subFromClauses != null && !subFromClauses.isEmpty()) {
                    sql.append(" FROM ").append(String.join(", ", subFromClauses)).append(" ");
                } else {
                    sql.append(" FROM ").append(qb.getTable()).append(" ");
                }

                
                if (joins!= null) {
	                for (String join : joins) {
	                    sql.append(join).append(" ");
	                }
                }
                break;

            case "INSERT":
            	List<String> column =qb.getColumns();
 	            sql.append("INSERT INTO ").append(qb.getTable());
	      	 
	            if (!qb.getUseAllColumns() && column  != null)                               
	            {
	                sql.append(" (").append(String.join(", ", column )).append(")");
	            }
	            if (qb.getValueRows() != null) {
	                sql.append(" VALUES ");
	                List<String> rowPlaceholders = new ArrayList<>();
	                for (Object[] row : qb.getValueRows()) {  
	                        
	                	String placeholders = Arrays.stream(row) 
	                                                .map(v -> "?")
	                                                .collect(Collectors.joining(", "));
	                        rowPlaceholders.add("(" + placeholders + ")");
	                 }
	
	                 sql.append(String.join(", ", rowPlaceholders));
	              }
	                
	              break;



            case "UPDATE":
                sql.append("UPDATE ").append(qb.getTable())
                   .append(" SET ")
                   .append(String.join(", ", qb.getSetClauses())).append(" ");
                break;

            case "DELETE":
                sql.append("DELETE FROM ").append(qb.getTable()).append(" ");
                break;


            case "TRUNCATE":
                sql.append("TRUNCATE TABLE ").append(qb.getTable()).append(";");
                break;

            case "CREATE":
            	List<Object> primaryKeys = qb.getParameters();
            	List<String> foreignKeys = qb.getForeignKeys();
                if (qb.getIsCreateTable()) {
                    sql.append("CREATE TABLE");
                    if (qb.getIfNotExists()) {
                        sql.append(" IF NOT EXISTS");
                    }
                    sql.append(" ").append(qb.getTable()).append(" (");

                   
                    sql.append(String.join(", ", qb.getColumns()));
                    

                    if (primaryKeys != null) {
                        sql.append(", PRIMARY KEY (")
                           .append(primaryKeys.stream()
                                              .map(String::valueOf)
                                              .collect(Collectors.joining(", ")))
                           .append(")");
                    }


                    if (foreignKeys != null) {
                        sql.append(", ");
                        sql.append(String.join(", ", foreignKeys));
                    }

                    sql.append(");");
                }
                break;

            case "DROP":
                if (qb.getIsDropTable()) {
                    sql.append("DROP TABLE IF EXISTS ").append(qb.getTable()).append(";");
                }
                break;

            case "ALTER":
                if (qb.getIsAlterTable()) {
                    sql.append("ALTER TABLE ").append(qb.getTable()).append(" ");
                    sql.append(String.join(", ", qb.getAlterTableClauses())).append(";");
                }
                break;
            default :
            	throw new QueryException ("Not a valid query");
        }

        
        if (whereConditions!= null) {
            String conditionClause = qb.buildConditionClause(whereConditions, whereOperators);
            sql.append("WHERE ").append(conditionClause).append(" ");
        }
     

        if (orderByColumns != null) {
            sql.append("ORDER BY ").append(String.join(", ", orderByColumns)).append(" ");
            if (orderByDirection != null) {
                sql.append(orderByDirection).append(" ");
            }
        }

        if (limit > 0) {
            sql.append("LIMIT ").append(limit).append(" ");
        }
        if (offset > 0) {
            sql.append("OFFSET ").append(offset).append(" ");
        }

        if (groupByColumns != null) {
            sql.append("GROUP BY ").append(String.join(", ", groupByColumns)).append(" ");
        }

        if (havingConditions!=null) {
        	String conditionClause = qb.buildConditionClause(havingConditions, havingOperators);
            sql.append("HAVING ").append(conditionClause).append(" ");
        }
        if (unionClause != null) {
            sql.append(" ").append(unionClause);
        }


        return sql.toString().trim();
    }
}
