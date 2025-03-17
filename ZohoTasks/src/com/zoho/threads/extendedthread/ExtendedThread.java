	package com.zoho.threads.extendedthread;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.zoho.threads.log.ThreadLog;
import com.zoho.threads.util.Util;



public class ExtendedThread extends Thread {
	 		private static final Logger logger = ThreadLog.getLogger();
			long milliseconds;
			boolean running = true;
			public ExtendedThread() {
				
			}
			public ExtendedThread(long milliseconds) {
				this.milliseconds = milliseconds;
			}
			
		
			@Override
		    public void run() {
				Util.configureLogger(logger);
				try {
					while(running) {
						
					logger.info("Inside run method:");
					logger.info("Thread Name: " + Thread.currentThread().getName());
					logger.info("Thread Priority: " + Thread.currentThread().getPriority());
					logger.info("Thread State: " + Thread.currentThread().getState());
					logger.info("Thread going to  sleep:"+Thread.currentThread().getName() );
					Thread.sleep(milliseconds);
					logger.info("After sleeping"+ Thread.currentThread().getName());
					}
		        
		      
				}
				catch(InterruptedException e) {
					logger.log(Level.SEVERE,e.getMessage());
				}
			  
		  }
			public void setRunning(boolean running) {
				this.running = running;
			}
	
	}
