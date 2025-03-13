package com.zoho.threads.runner;
import com.zoho.threads.extendedthread.ExtendedThread;
import com.zoho.threads.runnable.RunnableThread;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import com.zoho.threads.threaddump.ThreadDumpTask;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.Scanner;

public class Runner {
	static final Logger logger = Logger.getLogger(Runner.class.getName());

	public static void main(String[] args) {
		 ExtendedThread thread;
		 Thread threadOne;
		 ExtendedThread[] exthreads;
		 Thread threaddump;
		 Thread[] runthreads ;
		 Scanner sc = new Scanner(System.in);
	     int choice,noOfThreads;
	     configureLogger();
		 try {
			 do {
				 System.out.println("1.Get current thread name,priority,state using ExtendedThread class");
				 System.out.println("2.Get current thread name,priority,state using RunnableThread class");
				 System.out.println("3.Set thread name and Get current thread name,priority,state");
				 System.out.println("4.Get current thread name,priority,state using ExtendedThread class");
				 System.out.println("5.Get thread dump after the threads are spawned");
				 System.out.println("6.Analyse the thread dump");
				 System.out.println("Enter your choice (to exit enter -1)");
				 choice = sc.nextInt();
				 switch(choice) {
				 	case 1:
				 		thread = new  ExtendedThread();
				 		 logger.info("Before calling start method:");
				         logger.info("Thread Name: " + thread.getName());
				         logger.info("Thread Priority: " + thread.getPriority());
				         logger.info("Thread State: " + thread.getState());
				         
				         thread.start();
				         
				         logger.info("After calling start method:");
				         logger.info("Thread Name: " + thread.getName());
				         logger.info("Thread Priority: " + thread.getPriority());
				         logger.info("Thread State: " + thread.getState());
				         break;
				 	case 2:
				 		threadOne = new Thread(new RunnableThread());
				 		logger.info("Before calling start method:");
				        logger.info("Thread Name: " + threadOne.getName());
				        logger.info("Thread Priority: " + threadOne.getPriority());
				        logger.info("Thread State: " + threadOne.getState());
				         
				         threadOne.start();
				         
				         logger.info("After calling start method:");
				         logger.info("Thread Name: " + threadOne.getName());
				         logger.info("Thread Priority: " + threadOne.getPriority());
				         logger.info("Thread State: " + threadOne.getState());
				         break;
				 	case 3:
				 		 thread = new  ExtendedThread();
				 		 logger.info("Before calling start method:");
				         logger.info("Thread Name: " + thread.getName());
				         logger.info("Thread Priority: " + thread.getPriority());
				         logger.info("Thread State: " + thread.getState());
				        
				         thread.start();
				         thread.setName("ExtendedThread");
					 		
				         logger.info("After calling start method:");
				         logger.info("Thread Name: " + thread.getName());
				         logger.info("Thread Priority: " + thread.getPriority());
				         logger.info("Thread State: " + thread.getState());
				         
				         threadOne = new Thread(new RunnableThread());
				         logger.info("Before calling start method:");
					     logger.info("Thread Name: " + threadOne.getName());
					     logger.info("Thread Priority: " + threadOne.getPriority());
					     logger.info("Thread State: " + threadOne.getState());
					         
					     threadOne.start();
					     thread.setName("RunnableThread");
					         
					     logger.info("After calling start method:");
					     logger.info("Thread Name: " + threadOne.getName());
					     logger.info("Thread Priority: " + threadOne.getPriority());
					     logger.info("Thread State: " + threadOne.getState());
					     break;
				 	case 4:
				 		logger.info("Enter the number of threads to be spawned in ExtendedThreads");
				 		noOfThreads = sc.nextInt();
				 		exthreads = new ExtendedThread[noOfThreads];
				 		for(int i=0;i<noOfThreads;i++) {
				 			logger.info("Enter te milliseconds to make thread sleep ");
				 			long milli = sc.nextInt();
				 			thread = new ExtendedThread(milli);
				 			thread.start();
				 		    thread.setName("ExtendedThread:"+i);
				 		    exthreads[i]=thread;
				 			
				 		}
				 		logger.info("Enter the number of threads to be spawned in RunnableThreads");
				 		int noOfRunThreads = sc.nextInt();
				 		runthreads = new Thread[noOfThreads];
				 		for(int i=0;i<noOfRunThreads;i++) {
				 			logger.info("Enter te milliseconds to make thread sleep ");
				 			long milli = sc.nextInt();
				 			threadOne = new Thread(new RunnableThread(milli));
				 			threadOne.start();
				 		    threadOne.setName("RunnableThread:"+i);
				 		    runthreads[i]=threadOne;
				 			
				 		}
				 		break;
				 	case 5:
				 		logger.info("Enter the number of threads to be spawned in ExtendedThreads");
				 		noOfThreads = sc.nextInt();
				 		exthreads = new ExtendedThread[noOfThreads];
				 		for (int i = 0; i < noOfThreads; i++) {
				 		    thread = new ExtendedThread();
				 		    thread.setName("ExtendedThread:" + i);
				 		    thread.start();
				 		    exthreads[i] = thread;
				 		}
				 		
				 		Thread.sleep(120000);
				 		threaddump = new Thread(new ThreadDumpTask(3,30000));
				 		threaddump.start();

				 		for (ExtendedThread t : exthreads) {
				 		    t.setRunning(false);
				 		}
				 		break;
				 	case 6:
				 		logger.info("Enter the number of threads to be spawned in ExtendedThreads");
				 		noOfThreads = sc.nextInt();
				 		exthreads = new ExtendedThread[noOfThreads];
				 		for(int i=0;i<noOfThreads;i++) {
				 			thread = new ExtendedThread();
				 			thread.start();
				 		    thread.setName("ExtendedThread:"+i);
				 		    exthreads[i]=thread;
				 			
				 		}
				 	   Thread.sleep(12000);
				 	   for (ExtendedThread t : exthreads) {
				 		   Thread.sleep(60000);
				 		    t.setRunning(false); 
				 	   }
				 	  boolean alive = false; 
				 	 for (ExtendedThread t : exthreads) { 
				 	     if (t.isAlive()) {  
				 	         alive = true;  
				 	         break; 
				 	     }
				 	 }
				 	 if (!alive) {  
				 	     logger.info("Task completed"); 
				 	 }

				 		
				       Thread.sleep(12000);
				       threaddump = new Thread(new ThreadDumpTask(10,45000));
				 	   threaddump.start();
				 		
				 	   threaddump = new Thread(new ThreadDumpTask());
					   threaddump.start();
				 		
				 		break;
				 		

				 		
				 
				 }
		
			 }while(choice!=-1);
		 }
		 catch(InterruptedException e) {
			 logger.log(Level.SEVERE,e.getMessage());
	 			
	 	  }
		 catch(IllegalStateException e) {
			 logger.log(Level.SEVERE,e.getMessage());
		 }
		 catch(Exception e) { 
			 logger.log(Level.SEVERE,e.getMessage());
			 e.printStackTrace();
		
		 }

	}
	private static void configureLogger() {
		try {
		logger.setUseParentHandlers(false);
		FileHandler severeFile = new FileHandler("threadsevere.log");
		severeFile.setFormatter(new SimpleFormatter());
		severeFile.setLevel(Level.SEVERE);
		
		FileHandler infoFile = new FileHandler("threadsevere.log");
		infoFile.setFormatter(new SimpleFormatter());
		infoFile.setLevel(Level.SEVERE);
		infoFile.setFilter(record -> record.getLevel()==Level.INFO);
		
		logger.addHandler(severeFile);
		logger.addHandler(infoFile);
		
	}
		catch(IOException e) {
			e.printStackTrace();
}
	}
}

