package com.zoho.threads.runnable;

public class RunnableThread implements Runnable {
	long milliseconds;
	boolean running = true;
	public RunnableThread() {
		
	}
	public RunnableThread(long milliseconds) {
		this.milliseconds = milliseconds;
	}
  

	@Override
	public void run() {
		
			try {
				while(running) {
					
				System.out.println("Inside run method:");
				System.out.println("Thread Name: " + Thread.currentThread().getName());
				System.out.println("Thread Priority: " + Thread.currentThread().getPriority());
				System.out.println("Thread State: " + Thread.currentThread().getState());
				System.out.println("Thread going to  sleep:"+Thread.currentThread().getName() );
				Thread.sleep(milliseconds);
				System.out.println("After sleeping"+ Thread.currentThread().getName());
				}
	        
	      
			}
			catch(InterruptedException e) {
				System.out.println("Thread was interrupted.");
			}
		  
	 }
	public void setRunning(boolean running) {
		this.running = running;
	}
}
	


