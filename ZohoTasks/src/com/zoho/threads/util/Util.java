package com.zoho.threads.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Util {
	public static void configureLogger(Logger logger) {
		try {
		logger.setUseParentHandlers(false);
		FileHandler severeFile = new FileHandler("threadsevere.log");
		severeFile.setFormatter(new SimpleFormatter());
		severeFile.setLevel(Level.SEVERE);
		
		FileHandler infoFile = new FileHandler("threadlog.log");
		infoFile.setFormatter(new SimpleFormatter());
		infoFile.setLevel(Level.INFO);
		infoFile.setFilter(record -> record.getLevel()==Level.INFO);
		
		logger.addHandler(severeFile);
		logger.addHandler(infoFile);
		
	}
	 catch(IOException e) {
			e.printStackTrace();
}

}
}
