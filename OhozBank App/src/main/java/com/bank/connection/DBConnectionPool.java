package com.bank.connection;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnectionPool {

    private static final Logger logger = Logger.getLogger(DBConnectionPool.class.getName());
    private static DataSource dataSource;

    private DBConnectionPool() {
        try {
            InitialContext initContext = new InitialContext();
            dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/ohozBankDB");
            logger.info("JNDI DataSource 'jdbc/ohozBankDB' successfully initialized.");
        } catch (NamingException e) {
            logger.log(Level.SEVERE, "CRITICAL ERROR: Failed to look up JNDI data source 'jdbc/ohozBankDB'.", e);
            throw new RuntimeException("Failed to initialize DBConnectionPool from JNDI. Check context.xml and JNDI name.", e);
        }
    }

    private static class DBSingletonHelper {
        private static final DBConnectionPool INSTANCE = new DBConnectionPool();
    }

    public static DBConnectionPool getInstance() {
        return DBSingletonHelper.INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            logger.severe("DataSource is null when attempting to get a DB connection.");
            throw new SQLException("DataSource is not initialized. Check application startup logs.");
        }
        logger.fine("Database connection requested from DataSource.");
        return dataSource.getConnection();
    }

    public void close() {
        logger.info("DBConnectionPool.close() called. JNDI DataSources are managed by the container.");
    }
}
