package com.bank.configuration;


import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerConfigurator {

    public static void configure() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setUseParentHandlers(false);  

        try {
          
            new File("logs").mkdirs();

            FileHandler severeHandler = new FileHandler("logs/severe.log", true);
            severeHandler.setFormatter(new SimpleFormatter());
            severeHandler.setLevel(Level.SEVERE);
            severeHandler.setFilter(record -> record.getLevel() == Level.SEVERE);

            FileHandler infoHandler = new FileHandler("logs/info.log", true);
            infoHandler.setFormatter(new SimpleFormatter());
            infoHandler.setLevel(Level.INFO);
            infoHandler.setFilter(record -> record.getLevel() == Level.INFO);

            FileHandler warningHandler = new FileHandler("logs/warning.log", true);
            warningHandler.setFormatter(new SimpleFormatter());
            warningHandler.setLevel(Level.WARNING);
            warningHandler.setFilter(record -> record.getLevel() == Level.WARNING);

            rootLogger.addHandler(severeHandler);
            rootLogger.addHandler(infoHandler);
            rootLogger.addHandler(warningHandler);

            rootLogger.setLevel(Level.INFO);  

        } catch (IOException e) {
            System.err.println("Logging configuration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
