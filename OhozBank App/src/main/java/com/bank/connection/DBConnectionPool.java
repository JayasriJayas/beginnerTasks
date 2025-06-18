//	package com.bank.connection;
//	
//	import java.sql.Connection;
//	import java.sql.SQLException;
//	
//	import javax.sql.DataSource;
//	import org.apache.tomcat.jdbc.pool.PoolProperties;
//	
//	public class DBConnectionPool {
//	    private static DataSource dataSource;
//	
//	    private DBConnectionPool() {
//	        PoolProperties p = new PoolProperties();
//	        p.setUrl("jdbc:mysql://localhost:3306/ohozBank");
//	        p.setDriverClassName("com.mysql.cj.jdbc.Driver");
//	        p.setUsername("root");
//	        p.setPassword("root");
//	
//	        p.setMaxActive(50);
//	        p.setInitialSize(5);
//	        p.setMaxIdle(10);
//	        p.setMinIdle(5);
//	        p.setMaxWait(10000);
//	
//	        dataSource = new org.apache.tomcat.jdbc.pool.DataSource(p);
//	    }
//	
//	    private static class DBSingletonHelper {
//	        private static final DBConnectionPool INSTANCE = new DBConnectionPool();
//	    }
//	
//	    public static DBConnectionPool getInstance() {
//	        return DBSingletonHelper.INSTANCE;
//	    }
//	
//	    public Connection getConnection() throws SQLException {
//	        return dataSource.getConnection();
//	    }
//	    public void close() {
//	        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
//	            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).close();
//	        }
//	    }
//
//	}
package com.bank.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource; 
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class DBConnectionPool {
    private static DataSource dataSource;

    private DBConnectionPool() {
        try {
          
            InitialContext initContext = new InitialContext();
            dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/ohozBankDB");
            System.out.println("JNDI DataSource 'jdbc/ohozBankDB' successfully looked up."); 
        } catch (NamingException e) {
           
            System.err.println("CRITICAL ERROR: Failed to look up JNDI data source 'jdbc/ohozBankDB'. " + e.getMessage());
          
            throw new RuntimeException("Failed to initialize DBConnectionPool from JNDI. Check context.xml and JNDI name.", e);
        }
    }
    
// add logger
  
    private static class DBSingletonHelper {
        private static final DBConnectionPool INSTANCE = new DBConnectionPool();
    }

    public static DBConnectionPool getInstance() {
        return DBSingletonHelper.INSTANCE;
    }
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
           
            throw new SQLException("DataSource is not initialized. Check application startup logs.");
        }
        return dataSource.getConnection();
    }


    public void close() {
      
        System.out.println("DBConnectionPool.close() called. For JNDI DataSources, container manages lifecycle.");
        
    }
}