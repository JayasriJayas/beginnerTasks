package com.bank.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DBConnectionPool {
    private static DataSource dataSource;

    private DBConnectionPool() {
        PoolProperties p = new PoolProperties();
        p.setUrl("jdbc:mysql://localhost:3306/incubationDB");
        p.setDriverClassName("com.mysql.cj.jdbc.Driver");
        p.setUsername("root");
        p.setPassword("root");

        p.setMaxActive(50);
        p.setInitialSize(5);
        p.setMaxIdle(10);
        p.setMinIdle(5);
        p.setMaxWait(10000);

        dataSource = new org.apache.tomcat.jdbc.pool.DataSource(p);
    }

    // Inner static class responsible for holding the Singleton instance
    private static class SingletonHelper {
        private static final DBConnectionPool INSTANCE = new DBConnectionPool();
    }

    public static DBConnectionPool getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
