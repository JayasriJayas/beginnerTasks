package com.querybuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.dialect.DatabaseDialect;

public class QueryBuilder {
	 private String table;
     private Optional<List<String>> columns = Optional.empty();
     private Optional<List<String>> dmlcolumns = Optional.empty();
     private Optional<List<String>> values = Optional.empty();
	 private Optional<List<String>> primaryKeys = Optional.empty();
	 private Optional<List<String>> foreignKeys = Optional.empty();
	 private Optional<List<String>> alterTableClauses = Optional.empty();
	 private boolean isDropTable = false;
	 private boolean ifNotExists = false;
	 private String queryType = "SELECT";
	 private boolean isCreateTable = false;
	 private boolean isAlterTable = false;
	    

	 private DatabaseDialect dialect;

	 public QueryBuilder(DatabaseDialect dialect) {
	    this.dialect = dialect;
	 }
	 public QueryBuilder createTable(String tableName) {
	        this.table = tableName;
	        this.isCreateTable = true;
	        return this;
	 }
	 public QueryBuilder ifNotExists() {
	        this.ifNotExists = true;
	        return this;
	 }
	 public QueryBuilder column(String name, String type, String... constraints) {
	        StringBuilder fields = new StringBuilder(name + " " + type);
	        for (String constraint : constraints) {
	            fields.append(" ").append(constraint);
	        }
	        columns.get().add(fields.toString());
	        return this;
	 }
	  public QueryBuilder primaryKey(String... cols) {
	        primaryKeys.get().addAll(Arrays.asList(cols));
	        return this;
	  }
	  public QueryBuilder foreignKey(String column, String refTable, String refColumn) {
	        if (!foreignKeys.isPresent()) {
	            foreignKeys = Optional.of(new ArrayList<>());
	        }
	        foreignKeys.get().add("FOREIGN KEY (" + column + ") REFERENCES " + refTable + "(" + refColumn + ")");
	        return this;
	  }
	  public QueryBuilder dropTable(String tableName) {
	        this.table = tableName;
	        this.isDropTable = true;
	        return this;
	  }
	  public QueryBuilder addColumn(String name, String type, String... constraints) {
	        StringBuilder querStrings = new StringBuilder("ADD " + name + " " + type);
	        for (String constraint : constraints) {
	        	querStrings.append(" ").append(constraint);
	        }
	        alterTableClauses.get().add(querStrings.toString());
	        return this;
	    }

	    public QueryBuilder modifyColumn(String name, String newType) {
	        alterTableClauses.get().add("MODIFY " + name + " " + newType);
	        return this;
	    }

	    public QueryBuilder dropColumn(String name) {
	        alterTableClauses.get().add("DROP COLUMN " + name);
	        return this;
	    }
	    public QueryBuilder select(String...columns) {
	    	this.queryType = "SELECT";
	    	dmlcolumns.get().addAll(Arrays.asList(columns));
	    	return this;
	    	
	    }
	    public QueryBuilder insertInto(String table, String... cols) {
	        this.queryType = "INSERT";
	        this.table = table;
	        dmlcolumns.get().addAll(Arrays.asList(cols));
	        return this;
	    }
	    public QueryBuilder values(String...vals) {
	    	dmlcolumns.get().addAll(Arrays.asList(vals));
	    	return this;
	    }
	 
 	 public String build()
	 {
		 if(isCreateTable)
		 {
			StringBuilder query = new StringBuilder("CREATE TABLE");
			if (ifNotExists) query.append("IF NOT EXISTS ");
            query.append(table).append(" (");
            List<String> queryStrings = new ArrayList<>();
            columns.ifPresent(queryStrings::addAll);
            
            if (primaryKeys.isPresent() && !primaryKeys.get().isEmpty()) {
            	queryStrings.add("PRIMARY KEY"+String.join(",",primaryKeys.get())+")");
            	
            }
            
            foreignKeys.ifPresent(queryStrings::addAll);

            query.append(String.join(", ", queryStrings)).append(");");
            return query.toString();
            
            

		 }
		 else if (isDropTable) {
	            return "DROP TABLE IF EXISTS " + table + ";";

	     } else if (isAlterTable) {
	            StringBuilder query = new StringBuilder("ALTER TABLE ");
	            query.append(table).append(" ");
	            query.append(String.join(", ", alterTableClauses.get())).append(";");
	            return query.toString();
	        }
		 
		 return dialect.buildQuery(this);
	 }
	 

}
