package com.querybuilder;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSetMetaData;

import exception.QueryException;
import utils.SQLLogger;

public class QueryExecutor {

    private static final Logger LOGGER = Logger.getLogger(QueryExecutor.class.getName());
    private final Connection conn;

    public QueryExecutor(Connection connection) {
        this.conn = connection;
    }
    public int executeUpdate(String query) throws QueryException {
        try (Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(query);
            SQLLogger.log(query);
            return rowsAffected;
        } catch (SQLException e) {
            throw new QueryException("Execution failed: " + e.getMessage());
        }
    }
    public List<Map<String, Object>> executeQuery(String query) throws QueryException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<Map<String, Object>> result = extractResultSet(rs);
            SQLLogger.log(query);
            return result;

        } catch (SQLException e) {
            throw new QueryException("Query execution failed: " + e.getMessage());
        }
    }
    public int executeUpdate(String query, List<Object> parameters) throws QueryException {
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            setParameters(pstmt, parameters);
            int rowsAffected = pstmt.executeUpdate();
            SQLLogger.log(query);
            return rowsAffected;
        } catch (SQLException e) {
            throw new QueryException("Execution with parameters failed: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> executeQuery(String query, List<Object> parameters) throws QueryException {
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            setParameters(pstmt, parameters);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Map<String, Object>> result = extractResultSet(rs);
                SQLLogger.log(query);
                return result;
            }
        } catch (SQLException e) {
            throw new QueryException("Query with parameters failed: " + e.getMessage());
        }
    }

    public void batchExecute(List<String> queries) throws QueryException {
        try (Statement stmt = conn.createStatement()) {
            for (String query : queries) {
                stmt.addBatch(query);
            }
            stmt.executeBatch();
            for (String query : queries) {
                SQLLogger.log(query);
            }
        } catch (SQLException e) {
            throw new QueryException("Batch execution failed: " + e.getMessage());
        }
    }

    private void setParameters(PreparedStatement pstmt, List<Object> parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            Object param = parameters.get(i);
            if (param instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) param);
            } else if (param instanceof Double) {
                pstmt.setDouble(i + 1, (Double) param);
            } else if (param instanceof Boolean) {
                pstmt.setBoolean(i + 1, (Boolean) param);
            } else if (param instanceof java.sql.Date) {
                pstmt.setDate(i + 1, (java.sql.Date) param);
            } else if (param instanceof java.util.Date) {
                pstmt.setTimestamp(i + 1, new java.sql.Timestamp(((java.util.Date) param).getTime()));
            } else if (param == null) {
                pstmt.setNull(i + 1, java.sql.Types.NULL);
            } else {
                pstmt.setString(i + 1, param.toString());
            }
        }
    }
    private List<Map<String, Object>> extractResultSet(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(meta.getColumnLabel(i), rs.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }
    public List<Object> executeUpdateWithGeneratedKeys(String query,List<Object> parameters) throws QueryException, SQLException{
    	List<Object> generatedKeys = new ArrayList<>();
    	try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
    		setParameters(pstmt,parameters);
    		pstmt.executeUpdate();
            SQLLogger.log(query);
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                while (rs.next()) {
                	 generatedKeys.add(rs.getObject(1));
                }

    	}catch (SQLException e) {
            throw new QueryException("Execution with generated keys failed: " + e.getMessage());
        }
        return generatedKeys;

    }
    }
}
    
//    private static void printResults(List<Map<String, Object>> resultSet) {
//        if (resultSet == null || resultSet.isEmpty()) {
//            System.out.println("No results found.");
//            return;
//        }
//
//        System.out.println("---- ResultSet ----");
//        for (Map<String, Object> row : resultSet) {
//            for (Map.Entry<String, Object> entry : row.entrySet()) {
//                System.out.print(entry.getKey() + ": " + entry.getValue() + "\t");
//            }
//            System.out.println(); // new line after each row
//        }
//        System.out.println("-------------------\n");
//    }

