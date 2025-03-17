package com.zoho.threads.log;

import java.util.logging.Logger;

public class ThreadLog {
    private static final Logger logger = Logger.getLogger(ThreadLog.class.getName());

    private ThreadLog() {
    }

    public static Logger getLogger() {
        return logger;
    }
}