package com.zoho.threads.runner;
import com.zoho.threads.extendedthread.ExtendedThread;
import com.zoho.threads.runnable.RunnableThread;
import java.util.logging.Logger;
import java.util.Scanner;

public class Runner {
	static final Logger logger = Logger.getLogger(Runner.class.getName());
	
	
	
	public static void main(String[] args) {
		 ExtendedThread thread;
		 Thread threadOne;
		 ExtendedThread[] exthreads;
		 Thread[] runthreads ;
		 Scanner sc = new Scanner(System.in);
	     int choice;
		 try {
			 do {
				 logger.info("1.Get current thread name,priority,state using ExtendedThread class");
				 logger.info("Enter your choice (to exit enter -1)");
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
				 		threadOne = new Thread(new RunnableThread());//explain flow
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
				 		int noOfThreads = sc.nextInt();
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
				 		
				 		
				 		
				 		
				 		
				 
				 		
				         
				 		
				 		
				 
				 }
		
			 }while(choice!=-1);
		 }
		 catch(Exception e) { 
			 e.printStackTrace();
		
		 }

	}
}
