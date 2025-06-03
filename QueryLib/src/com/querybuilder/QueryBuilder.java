package com.querybuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dialect.DatabaseDialect;

import exception.QueryException;

public class QueryBuilder {
	 private String table;
	 private List<String> columns;
	 private List<String> values;
	 private List<String> primaryKeys;
	 private List<String> foreignKeys;
	 private List<String> alterTableClauses;
	 private List<String> setClauses;
	 private List<String> orderByColumns;
	 private List<String> whereConditions;
	 private List<String> groupByColumns;
	 private List<String> havingConditions;
	 private List<Object> parameters;
	 private List<Object[]> valueRows;
	 private List<String> whereOperators;
	 private List<String> havingOperators;
	 private List<String> joins; 
	 private List<String> subSelectColumns;
	 private List<String> subQueryFromClauses;
	 private String unionClause;
	 private boolean isDropTable = false;
	 private boolean useAllColumns = true;
	 private boolean ifNotExists = false;
	 private String queryType = "SELECT";
	 private boolean isCreateTable = false;
	 private boolean isAlterTable = false;
	 private int limit;
	 private boolean distinct;
	 private String orderDirection;
	 private DatabaseDialect dialect;
	
	

	 public QueryBuilder(DatabaseDialect dialect) {
	    this.dialect = dialect;
	 }
	 public QueryBuilder createTable(String tableName) {
		    this.queryType = "CREATE";
	        this.table = tableName;
	        this.isCreateTable = true;
	        return this;
	 }
	 public QueryBuilder ifNotExists() {
	        this.ifNotExists = true;
	        return this;
	 }
	 public QueryBuilder column(String name, String type) {
	        StringBuilder fields = new StringBuilder(name + " " + type);
	        this.columns = initIfNull(this.columns);
	        columns.add(fields.toString());
	        return this;
	 }
	 public QueryBuilder column(String name, String type, String... constraints) {
	        StringBuilder fields = new StringBuilder(name + " " + type);
	        for (String constraint : constraints) {
	            fields.append(" ").append(constraint);
	        }
	        this.columns = initIfNull(this.columns);
	        columns.add(fields.toString());
	        return this;
	 }
	  public QueryBuilder primaryKey(String... cols){
		    this.primaryKeys = initIfNull(this.primaryKeys);
	        primaryKeys.addAll(Arrays.asList(cols));
	        return this;
	  }
	  public QueryBuilder foreignKey(String column, String refTable, String refColumn) {
		    this.foreignKeys = initIfNull(this.foreignKeys);
	        foreignKeys.add("FOREIGN KEY (" + column + ") REFERENCES " + refTable + "(" + refColumn + ")");
	        return this;
	  }
	  public QueryBuilder dropTable(String table) {
		    this.queryType = "DROP";
	        this.table = table;
	        this.isDropTable = true;
	        return this;
	  }
	  public QueryBuilder alterTable(String table) {
		  this.queryType ="ALTER";
		  this.table = table;
		  this.isAlterTable = true;
		  return this;
				  
	  }
	  public QueryBuilder addColumn(String name, String type, String... constraints) {
	        StringBuilder querStrings = new StringBuilder("ADD " + name + " " + type);
	        for (String constraint : constraints) {
	        	querStrings.append(" ").append(constraint);
	        }
	        this.alterTableClauses = initIfNull(this.alterTableClauses);
	        alterTableClauses.add(querStrings.toString());
	        return this;
	    }

	    public QueryBuilder modifyColumn(String name, String newType) {
	    	this.alterTableClauses = initIfNull(this.alterTableClauses);
	        alterTableClauses.add("MODIFY " + name + " " + newType);
	        return this;
	    }

	    public QueryBuilder dropColumn(String name) {
	    	this.alterTableClauses = initIfNull(this.alterTableClauses);
	        alterTableClauses.add("DROP COLUMN " + name);
	        return this;
	    }
	    public QueryBuilder select(String...columns) {
	        this.queryType = "SELECT";
	        this.columns = initIfNull(this.columns);
	        this.columns.addAll(Arrays.asList(columns));
	        return this;
	    }


	    public QueryBuilder from(String table) {
	        this.table = table;
	        return this;
	    }
	    
	    public QueryBuilder insertInto(String table) {
	    	this.queryType = "INSERT";
	        this.table = table;
	        return this;
	    }

	    public QueryBuilder insertInto(String table, String... columns) {
	    	this.queryType = "INSERT";
	        this.table= table;
	        this.columns = initIfNull(this.columns);
	        this.columns = Arrays.asList(columns);
	        this.useAllColumns = false;
	        return this;
	    }

	    public QueryBuilder values(Object... values) {
	    	this.valueRows = initIfNull(this.valueRows);
	        valueRows.add(values);
	        this.parameters = initIfNull(this.parameters);
	        parameters.addAll(Arrays.asList(values));
	        return this;
	    }

	    public QueryBuilder update(String table) {
	        this.queryType = "UPDATE";
	        this.table = table;
	        return this;
	    }
	    public QueryBuilder set(String column, Object value) {
	        this.setClauses = initIfNull(this.setClauses);
	        this.parameters = initIfNull(this.parameters);
	        
	        setClauses.add(column + " = ?");
	        parameters.add(value);
	        
	        return this;
	    }
	    public QueryBuilder deleteFrom(String table) {
	        this.queryType = "DELETE";
	        this.table = table;
	        return this;
	    }
	    public QueryBuilder truncate(String table) {
	        this.queryType = "TRUNCATE";
	        this.table = table;
	        return this;
	    }
	    public QueryBuilder orderBy(String column) {
	    	this.orderByColumns = initIfNull(this.orderByColumns);
	        orderByColumns.add(column);
	        return this;
	    }
	    public QueryBuilder orderDirection(String direction) {
	        this.orderDirection = direction.toUpperCase(); 
	        return this;
	    }

	    public QueryBuilder limit(int limit) {
	        this.limit = limit;
	        return this;
	    }
	    
	    public QueryBuilder aggregate(String function, String column) {
	    	this.columns = initIfNull(this.columns);
	        columns.add(function + "(" + column + ")");
	        return this;
	    }

	    public QueryBuilder where(String condition, Object... values) {
	        this.whereConditions = initIfNull(this.whereConditions);
	        this.parameters = initIfNull(this.parameters);

	        this.whereConditions.add(condition);
	        this.parameters.addAll(Arrays.asList(values));

	        return this;
	    }
	    public QueryBuilder andWhere(String condition, Object... values) {
	        this.whereOperators = initIfNull(this.whereOperators);
	        this.whereOperators.add("AND");

	        this.whereConditions = initIfNull(this.whereConditions);
	        this.whereConditions.add(condition);

	        this.parameters = initIfNull(this.parameters);
	        this.parameters.addAll(Arrays.asList(values));

	        return this;
	    }
	    public QueryBuilder orWhere(String condition, Object... values) {
	        this.whereOperators = initIfNull(this.whereOperators);
	        this.whereOperators.add("OR");

	        this.whereConditions = initIfNull(this.whereConditions);
	        this.whereConditions.add(condition);

	        this.parameters = initIfNull(this.parameters);
	        this.parameters.addAll(Arrays.asList(values));

	        return this;
	    }

	    public QueryBuilder groupBy(String... columns) {
	    	this.groupByColumns = initIfNull(this.groupByColumns);
	        groupByColumns.addAll(Arrays.asList(columns));
	        return this;
	    }
	    public QueryBuilder whereAny(String column, String subquery) {
	        this.whereConditions = initIfNull(this.whereConditions);
	        whereConditions.add(column + " = ANY (" + subquery + ")");
	        return this;
	    }

	    public QueryBuilder whereAll(String column, String subquery) {
	        this.whereConditions = initIfNull(this.whereConditions);
	        whereConditions.add(column + " = ALL (" + subquery + ")");
	        return this;
	    }


	    public QueryBuilder having(String condition) {
	    	this.havingConditions = initIfNull(this.havingConditions);
	        havingConditions.add(condition);
	        return this;
	    }
	    public QueryBuilder andHaving(String condition) {
	    	this.havingOperators = initIfNull(this.havingOperators);
	    	havingOperators.add("AND");
	    	this.havingConditions = initIfNull(this.havingConditions);
	    	havingConditions.add(condition);
	    	return this;
	    }
	    public QueryBuilder orHaving(String condition) {
	    	this.havingOperators = initIfNull(this.havingOperators);
	    	havingOperators.add("OR");
	    	this.havingConditions = initIfNull(this.havingConditions);
	    	havingConditions.add(condition);
	    	return this;
	    }
	    public QueryBuilder havingAny(String column, String subquery) {
	        this.havingConditions = initIfNull(this.havingConditions);
	        havingConditions.add(column + " = ANY (" + subquery + ")");
	        return this;
	    }

	    public QueryBuilder havingAll(String column, String subquery) {
	        this.havingConditions = initIfNull(this.havingConditions);
	        havingConditions.add(column + " = ALL (" + subquery + ")");
	        return this;
	    }

	    public QueryBuilder innerJoin(String table, String condition) {
	        this.joins = initIfNull(this.joins);
	        joins.add("INNER JOIN " + table + " ON " + condition);
	        return this;
	    }

	    public QueryBuilder leftJoin(String table, String condition) {
	        this.joins = initIfNull(this.joins);
	        joins.add("LEFT JOIN " + table + " ON " + condition);
	        return this;
	    }

	    public QueryBuilder rightJoin(String table, String condition) {
	        this.joins = initIfNull(this.joins);
	        joins.add("RIGHT JOIN " + table + " ON " + condition);
	        return this;
	    }
	    public QueryBuilder distinct() {
	        this.distinct = true;
	        return this;
	    }

	    public QueryBuilder union(String query) {
	        this.unionClause = "UNION " + query;
	        return this;
	    }
	    public QueryBuilder unionAll(String query) {
	        this.unionClause = "UNION ALL " + query;
	        return this;
	    }
	    public QueryBuilder selectSubQuery(QueryBuilder subQuery, String alias) throws QueryException {
	        this.subSelectColumns = initIfNull(this.subSelectColumns);
	        subSelectColumns.add("(" + subQuery.build() + ") AS " + alias);
	        return this;
	    }

	    public QueryBuilder fromSubQuery(QueryBuilder subQuery, String alias) throws QueryException {
	        this.subQueryFromClauses = initIfNull(this.subQueryFromClauses);
	        subQueryFromClauses.add("(" + subQuery.build() + ") AS " + alias);
	        return this;
	    }

	    public QueryBuilder whereSubQuery(String condition, QueryBuilder subQuery) throws QueryException {
	        this.whereConditions = initIfNull(this.whereConditions);
	        whereConditions.add(condition + " (" + subQuery.build() + ")");
	        return this;
	    }
	    public QueryBuilder havingSubQuery(String condition, QueryBuilder subQuery) throws QueryException {
	        this.havingConditions = initIfNull(this.havingConditions);
	        havingConditions.add(condition + " (" + subQuery.build() + ")");
	        return this;
	    }
	    public QueryBuilder whereIn(String column, QueryBuilder subQuery) throws QueryException {
	        this.whereConditions = initIfNull(this.whereConditions);
	        whereConditions.add(column + " IN (" + subQuery.build() + ")");
	        return this;
	    }
	    public QueryBuilder whereExists(QueryBuilder subQuery) throws QueryException {
	        this.whereConditions = initIfNull(this.whereConditions);
	        whereConditions.add("EXISTS (" + subQuery.build() + ")");
	        return this;
	    }
	    public QueryBuilder whereNotExists(QueryBuilder subQuery) throws QueryException {
	        this.whereConditions = initIfNull(this.whereConditions);
	        whereConditions.add("NOT EXISTS (" + subQuery.build() + ")");
	        return this;
	    }
	    public QueryBuilder whereBetween(String column, Object from, Object to) {
	        this.whereConditions = initIfNull(this.whereConditions);
	        this.parameters = initIfNull(this.parameters);

	        this.whereConditions.add(column + " BETWEEN ? AND ?");
	        this.parameters.add(from);
	        this.parameters.add(to);

	        return this;
	    }
	    public QueryBuilder andBetween(String column, Object from, Object to) {
	        this.whereOperators = initIfNull(this.whereOperators);
	        this.whereConditions = initIfNull(this.whereConditions);
	        this.parameters = initIfNull(this.parameters);

	        this.whereOperators.add("AND");
	        this.whereConditions.add(column + " BETWEEN ? AND ?");
	        this.parameters.add(from);
	        this.parameters.add(to);

	        return this;
	    }
	    public QueryBuilder orBetween(String column, Object from, Object to) {
	        this.whereOperators = initIfNull(this.whereOperators);
	        this.whereConditions = initIfNull(this.whereConditions);
	        this.parameters = initIfNull(this.parameters);

	        this.whereOperators.add("OR");
	        this.whereConditions.add(column + " BETWEEN ? AND ?");
	        this.parameters.add(from);
	        this.parameters.add(to);

	        return this;
	    }

	    public String buildConditionClause(List<String> conditions, List<String> operators) {
	        if (conditions == null) return "";
	    

	        StringBuilder clause = new StringBuilder(conditions.get(0));
	        for (int i = 1; i < conditions.size(); i++) {
	            clause.append(" ").append(operators.get(i - 1)).append(" ").append(conditions.get(i));
	           
	        }
	      
	        return clause.toString();
	    }
	    private <T> List<T> initIfNull(List<T> list) {
	        return (list == null) ? new ArrayList<>() : list;
	    }

 	 public String build() throws QueryException
	 {
		 return dialect.buildQuery(this);
	 }
 	public String getQueryType() { return queryType; }
    public String getTable() { return table; }
    public boolean getIsCreateTable() { return isCreateTable; }
    public boolean getIfNotExists() { return ifNotExists; }
    public boolean getIsAlterTable() { return isAlterTable; }
    public boolean getIsDropTable() { return isDropTable; }
    public List<String> getColumns() { return columns; }
    public List<String> getPrimaryKeys() { return  primaryKeys; }
    public List<String> getForeignKeys() { return foreignKeys; }
    public List<String> getValues() { return values; }
    public List<String> getSetClauses() { return  setClauses; }
    public List<String> getWhereConditions() { return whereConditions; }
    public List<String> getAlterTableClauses(){ return  alterTableClauses; } 
    public List<String> getGroupByColumns() { return  groupByColumns; }
    public List<String> getHavingConditions() { return  havingConditions; }
    public List<String> getOrderByColumns() { return orderByColumns; }
    public Integer getLimit() { return limit; }
    public boolean isDistinct() { return distinct; }
    public String getOrderDirection() { return orderDirection; }
    public boolean getUseAllColumns() { return useAllColumns; }
    public List<Object[]> getValueRows() { return valueRows; }
    public List<Object> getParameters() { return parameters; }
    public List<String> getWhereOperators() { return  whereOperators; }
    public List<String> getHavingOperators(){ return  havingOperators; }
    public List<String> getJoins() { return  joins; }
    public String getUnionClause() { return unionClause; }
    public List<String> getSubSelectColumns() { return subSelectColumns; }
    public List<String> getSubQueryFromClauses() { return subQueryFromClauses; }

    

}

