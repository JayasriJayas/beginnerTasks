package com.zoho.threads.runner;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zoho.threads.extendedthread.ExtendedThread;
import com.zoho.threads.log.ThreadLog;
import com.zoho.threads.runnable.RunnableThread;
import com.zoho.threads.synchronize.Synchronized;
import com.zoho.threads.threaddump.ThreadDumpTask;
import com.zoho.threads.threadlocal.ThreadClass;
import com.zoho.threads.util.Util;

public class Runner {
	private static final Logger logger = ThreadLog.getLogger();

	public static void main(String[] args) {
		 Util.configureLogger(logger);
		 ExtendedThread thread;
		 Thread threadOne;
		 ExtendedThread[] exthreads;
		 Thread threaddump;
		 Thread[] runthreads ;
		 long sleepmilli;
		 Thread threadLocal;
		 Scanner sc = new Scanner(System.in);
	     int choice,noOfThreads,noOfDumps,interval;
	     
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
				         System.out.println("Enter the thread name");
					 	 String extendedName = sc.next();
				        
				         thread.start();
				         thread.setName(extendedName);
					 		
				         logger.info("After calling start method:");
				         logger.info("Thread Name: " + thread.getName());
				         logger.info("Thread Priority: " + thread.getPriority());
				         logger.info("Thread State: " + thread.getState());
				         
				         threadOne = new Thread(new RunnableThread());
				         logger.info("Before calling start method:");
					     logger.info("Thread Name: " + threadOne.getName());
					     logger.info("Thread Priority: " + threadOne.getPriority());
					     logger.info("Thread State: " + threadOne.getState());
					     System.out.println("Enter the thread name");
					 	 String runnName = sc.next();
					         
					     threadOne.start();
					     thread.setName(runnName);
					         
					     logger.info("After calling start method:");
					     logger.info("Thread Name: " + threadOne.getName());
					     logger.info("Thread Priority: " + threadOne.getPriority());
					     logger.info("Thread State: " + threadOne.getState());
					     break;
				 	case 4:
				 		System.out.println("Enter the number of threads to be spawned in ExtendedThreads");
				 		noOfThreads = sc.nextInt();
				 		exthreads = new ExtendedThread[noOfThreads];
				 		System.out.println("Enter the thread name");
				 		String extendName = sc.next();
				 		System.out.println("Enter te milliseconds to make thread sleep ");
			 			long milli = sc.nextInt();
				 		for(int i=0;i<noOfThreads;i++) {
				 			
				 			thread = new ExtendedThread(milli);
				 			thread.start();
				 		    thread.setName(extendName + ": "+i);
				 		    exthreads[i]=thread;
				 			
				 		}
				 		System.out.println("Enter the number of threads to be spawned in RunnableThreads");
				 		int noOfRunThreads = sc.nextInt();
				 		runthreads = new Thread[noOfThreads];
				 		System.out.println("Enter the thread name");
				 		String runName = sc.next();
				 		System.out.println("Enter te milliseconds to make thread sleep ");
			 			long millis = sc.nextInt();
				 		for(int i=0;i<noOfRunThreads;i++) {
				 			
				 			threadOne = new Thread(new RunnableThread(millis));
				 			threadOne.start();
				 		    threadOne.setName(runName+" "+i);
				 		    runthreads[i]=threadOne;
				 			
				 		}
				 		break;
				 	case 5:
				 		System.out.println("Enter the number of threads to be spawned in ExtendedThreads");
				 		noOfThreads = sc.nextInt();
				 	
				 		exthreads = new ExtendedThread[noOfThreads];
				 		for (int i = 0; i < noOfThreads; i++) {
				 		    thread = new ExtendedThread();
				 		    thread.start();
				 		    exthreads[i] = thread;
				 		}
				 		System.out.println("Enter the no  of millisecnds to sleep");	
				 		sleepmilli = sc.nextLong();
				 		Thread.sleep(sleepmilli);
				 		System.out.println("Enter the no of threadDumps and intervals");
				 		noOfDumps = sc.nextInt();
				 		interval = sc.nextInt();
				 	    Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();

				 	    for (Thread threadd : stackTraces.keySet()) {
				 	        System.out.println("Thread: " + threadd.getName());
				 	        StackTraceElement[] stackTrace = stackTraces.get(threadd);
				 	        for (StackTraceElement element : stackTrace) {
				 	            System.out.println("  " + element);
				 	        }
				 	    }

//				 		threaddump = new Thread(new ThreadDumpTask(noOfDumps,interval));
//				 		threaddump.start();

				 		for (ExtendedThread t : exthreads) {
				 		    t.setRunning(false);
				 		}
				 		break;
				 	case 6:
				 		System.out.println("Enter the number of threads to be spawned in ExtendedThreads");
				 		noOfThreads = sc.nextInt();
				 		exthreads = new ExtendedThread[noOfThreads];
				 		for(int i=0;i<noOfThreads;i++) {
				 			thread = new ExtendedThread();
				 			thread.start();
				 		    exthreads[i]=thread;
				 			
				 		}
				 		System.out.println("Enter the no  of millisecnds to sleep");	
				 		sleepmilli = sc.nextLong();
				 	    Thread.sleep(sleepmilli);
				 	    System.out.println("Enter the no  of millisecnds to stop threads");	
				 		long stopmilli = sc.nextLong();
				 	    for (ExtendedThread t : exthreads) {
				 		   Thread.sleep(stopmilli);
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

				 	   System.out.println("Enter the no of millisecnds to create thread dump ");	
				 	   long dumpmilli = sc.nextLong();
				       Thread.sleep(dumpmilli);
				       System.out.println("Enter the no of threadDumps and intervals");
				 	   noOfDumps = sc.nextInt();
				 	   interval = sc.nextInt();
				       threaddump = new Thread(new ThreadDumpTask(noOfDumps,interval));
				 	   threaddump.start();
				 		
				 	   threaddump = new Thread(new ThreadDumpTask());
					   threaddump.start();
				 		
				 		break;
				 	case 7:
				 		System.out.println("Enter the number of threads to be spawned in ExtendedThreads");
				 		noOfThreads = sc.nextInt();
				 	
				        Thread[] synchthreadss = new Thread[noOfThreads];
				        Synchronized syncInstance = new Synchronized(5000); 
				        for (int i = 0; i < noOfThreads; i++) {
				            Thread threadSync = new Thread(syncInstance, "Thread-" + (i + 1));
				            threadSync.start();
				            synchthreadss[i] = threadSync;
				        }

				        System.out.println("Enter the number of thread dumps and interval ms:");
				        noOfDumps = sc.nextInt();
				        interval = sc.nextInt();

				      
				        Thread threadump = new Thread(new ThreadDumpTask(noOfDumps, interval));
				        threadump.start();
				        syncInstance.setRunning(false);
				        break;
				 	case 8:
				 		System.out.println("Enter the number of threads to be spawned in ExtendedThreads");
				 		noOfThreads = sc.nextInt();
				 		ThreadClass threadInstance = new ThreadClass(); 
				 		Thread[] local = new Thread[noOfThreads];

				 		for (int i = 0; i < noOfThreads; i++) {
				 		    System.out.println("Enter the value to set");
				 		    int input = sc.nextInt();
				 		    threadInstance.setValue(input); 
				 		    local[i] = new Thread(threadInstance); 
				 		    local[i].start();
				 		    local[i].join();
				 		}

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
			
		 }
	

	}

		
	}


