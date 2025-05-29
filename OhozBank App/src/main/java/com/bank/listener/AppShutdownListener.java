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

        DBConnectionPool.getInstance().close();

        AbandonedConnectionCleanupThread.checkedShutdown();

    }


    @Override
    public void contextInitialized(ServletContextEvent sce) {
            }
}
