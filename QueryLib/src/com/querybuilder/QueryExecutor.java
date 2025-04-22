package com.querybuilder;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import exception.QueryException;
import utils.SQLLogger;

public class QueryExecutor {

    private static final Logger LOGGER = Logger.getLogger(QueryExecutor.class.getName());

 
    public static void execute(String query) throws QueryException {
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            boolean isResultSet = stmt.execute(query);

            if (isResultSet) {
                printResultSet(stmt.getResultSet());
            } else {
                int rowsAffected = stmt.getUpdateCount();
                LOGGER.info("Rows affected: " + rowsAffected);
            }

            SQLLogger.log(query);

        } catch (SQLException e) {
            throw new QueryException("Execution failed");
        }
    }

   
    public static void execute(String query, List<String> parameters) throws QueryException {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
        	
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setString(i + 1, parameters.get(i));  
            }

            boolean isResultSet = pstmt.execute();

            if (isResultSet) {
                printResultSet(pstmt.getResultSet());
            } else {
                int rowsAffected = pstmt.getUpdateCount();
                LOGGER.info("Rows affected: " + rowsAffected);
            }

            SQLLogger.log(query);

        } catch (SQLException e) {
        	throw new QueryException("Execution with parameters failed: " );
        }
    }

    private static void printResultSet(ResultSet rs) throws SQLException {
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
