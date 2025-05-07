package com.querybuilder;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
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

    public int execute(String query) throws QueryException {
        try (Statement stmt = conn.createStatement()) {

            boolean isResultSet = stmt.execute(query);

            if (isResultSet) {
                printResultSet(stmt.getResultSet());
            } else {
                int rowsAffected = stmt.getUpdateCount();
                LOGGER.info("Rows affected: " + rowsAffected);
                return rowsAffected;
            }

            SQLLogger.log(query);

        } catch (SQLException e) {
            throw new QueryException("Execution failed");
        }
        return 0;
    }

    public int execute(String query, List<Object> parameters) throws QueryException {
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        	
       

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

          
            boolean isResultSet = pstmt.execute();

            if (isResultSet) {
                printResultSet(pstmt.getResultSet());
            } else {
                int rowsAffected = pstmt.getUpdateCount();
                LOGGER.info("Rows affected: " + rowsAffected);
                return rowsAffected;
            }

            SQLLogger.log(query);

        }catch (SQLException e) {
            
            e.printStackTrace(); 
            throw new QueryException("Execution with parameters failed: " + e.getMessage());
        }
        return 0;

    }
    // need to check
    public void batchExecute(List<String> queries) throws QueryException {
        try (Statement stmt = conn.createStatement()) {

            for (String query : queries) {
                stmt.addBatch(query);
            }

            int[] results = stmt.executeBatch();
            LOGGER.info("Batch executed. " + results.length + " statements run.");

            for (String query : queries) {
                SQLLogger.log(query);
            }

        } catch (SQLException e) {
            throw new QueryException("Batch execution failed");
        }
    }

    private void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        StringBuilder header = new StringBuilder();
        for (int i = 1; i <= columnCount; i++) {
            header.append(meta.getColumnLabel(i)).append("\t");
        }
        LOGGER.info(header.toString());

        while (rs.next()) {
            StringBuilder row = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                row.append(rs.getString(i)).append("\t");
            }
            LOGGER.info(row.toString());
        }
    }
}
