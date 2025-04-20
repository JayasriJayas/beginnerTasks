package com.dialect;

import com.querybuilder.QueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MySQLDialect implements DatabaseDialect {

    @Override
    public String buildQuery(QueryBuilder qb) {
        StringBuilder sql = new StringBuilder();
        
        switch (qb.getQueryType()) {
        
            case "SELECT":
                sql.append("SELECT ");
                if (!qb.getColumns().isEmpty()) {
                    sql.append(String.join(", ", qb.getColumns()));
                } else {
                    sql.append("*");
                }
                sql.append(" FROM ").append(qb.getTable()).append(" ");

                for (String join : qb.getJoins()) {
                    sql.append(join).append(" ");
                }

                break;

	            case "INSERT":
	                sql.append("INSERT INTO ").append(qb.getTable());
	
	                // Check if columns are provided (if not using all columns)
	                if (!qb.getUseAllColumns() && !qb.getColumns().isEmpty()) {
	                    sql.append(" (").append(String.join(", ", qb.getColumns())).append(")");
	                }
	
	                // Check if there are value rows to insert
	                if (!qb.getValueRows().isEmpty()) {
	                    sql.append(" VALUES ");
	
	                    List<String> rowPlaceholders = new ArrayList<>();
	                    for (String[] row : qb.getValueRows()) {  // Change List<String> to String[]
	                        // For each row, map each value to a placeholder ('?')
	                        String placeholders = Arrays.stream(row)  // Use Arrays.stream() to process the String[] array
	                                                     .map(v -> "?")
	                                                     .collect(Collectors.joining(", "));
	                        rowPlaceholders.add("(" + placeholders + ")");
	                    }
	
	                    // Join all rows together with commas
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
                    

                    if ( !qb.getPrimaryKeys().isEmpty()) {
                        sql.append(", PRIMARY KEY (")
                           .append(String.join(",", qb.getPrimaryKeys()))
                           .append(")");
                    }

                    if (!qb.getForeignKeys().isEmpty()) {
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
        }

        // WHERE condition
        if (!qb.getWhereConditions().isEmpty()) {
            String conditionClause = qb.buildConditionClause(qb.getWhereConditions(), qb.getWhereOperators());
            sql.append("WHERE ").append(conditionClause).append(" ");
        }
     

        // ORDER BY condition
        if (!qb.getOrderByColumns().isEmpty()) {
            sql.append("ORDER BY ").append(String.join(", ", qb.getOrderByColumns())).append(" ");
            if (qb.getOrderDirection() != null) {
                sql.append(qb.getOrderDirection());
            }
        }

        // LIMIT condition
        if (qb.getLimit() > 0) {
            sql.append("LIMIT ").append(qb.getLimit()).append(" ");
        }
     // GROUP BY clause
        if (!qb.getGroupByColumns().isEmpty()) {
            sql.append("GROUP BY ").append(String.join(", ", qb.getGroupByColumns())).append(" ");
        }

        // HAVING clause
        if (!qb.getHavingConditions().isEmpty()) {
        	String conditionClause = qb.buildConditionClause(qb.getHavingConditions(), qb.getHavingOperators());
            sql.append("HAVING ").append(conditionClause).append(" ");
        }


        return sql.toString().trim();
    }
}
