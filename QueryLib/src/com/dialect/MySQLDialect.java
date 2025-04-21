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
        
        switch (qb.getQueryType()) {
        
            case "SELECT":
                sql.append("SELECT ");
                if (qb.getColumns()!= null) {
                    sql.append(String.join(", ", qb.getColumns()));
                } else {
                    sql.append("*");
                }
                sql.append(" FROM ").append(qb.getTable()).append(" ");
                
                if (qb.getJoins() != null) {
	                for (String join : qb.getJoins()) {
	                    sql.append(join).append(" ");
	                }
                }
                break;

            case "INSERT":
	            sql.append("INSERT INTO ").append(qb.getTable());
	      	 
	            if (!qb.getUseAllColumns() && qb.getColumns() != null)                               
	            {
	                sql.append(" (").append(String.join(", ", qb.getColumns())).append(")");
	            }
	            if (qb.getValueRows() != null) {
	                sql.append(" VALUES ");
	                List<String> rowPlaceholders = new ArrayList<>();
	                for (String[] row : qb.getValueRows()) {  
	                        
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
                if (qb.getIsCreateTable()) {
                    sql.append("CREATE TABLE");
                    if (qb.getIfNotExists()) {
                        sql.append(" IF NOT EXISTS");
                    }
                    sql.append(" ").append(qb.getTable()).append(" (");

                   
                    sql.append(String.join(", ", qb.getColumns()));
                    

                    if ( qb.getPrimaryKeys() != null) {
                        sql.append(", PRIMARY KEY (")
                           .append(String.join(",", qb.getPrimaryKeys()))
                           .append(")");
                    }

                    if (qb.getForeignKeys() != null) {
                        sql.append(", ");
                        sql.append(String.join(", ", qb.getForeignKeys()));
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

        
        if (qb.getWhereConditions()!= null) {
            String conditionClause = qb.buildConditionClause(qb.getWhereConditions(), qb.getWhereOperators());
            sql.append("WHERE ").append(conditionClause).append(" ");
        }
     

        if (qb.getOrderByColumns() != null) {
            sql.append("ORDER BY ").append(String.join(", ", qb.getOrderByColumns())).append(" ");
            if (qb.getOrderDirection() != null) {
                sql.append(qb.getOrderDirection());
            }
        }

        if (qb.getLimit() > 0) {
            sql.append("LIMIT ").append(qb.getLimit()).append(" ");
        }

        if (qb.getGroupByColumns() != null) {
            sql.append("GROUP BY ").append(String.join(", ", qb.getGroupByColumns())).append(" ");
        }

        if (qb.getHavingConditions()!=null) {
        	String conditionClause = qb.buildConditionClause(qb.getHavingConditions(), qb.getHavingOperators());
            sql.append("HAVING ").append(conditionClause).append(" ");
        }


        return sql.toString().trim();
    }
}
