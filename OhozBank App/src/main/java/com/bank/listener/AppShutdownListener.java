package com.bank.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.bank.connection.DBConnectionPool;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

@WebListener
public class AppShutdownListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Cleaning up DB resources...");

     
        DBConnectionPool.getInstance().close();

        AbandonedConnectionCleanupThread.checkedShutdown();

        System.out.println("DB resources cleaned up successfully.");
    }


    @Override
    public void contextInitialized(ServletContextEvent sce) {
            }
}
