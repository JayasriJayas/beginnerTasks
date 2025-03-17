package com.zoho.threads.synchronize;

public class Synchronized implements Runnable{
	boolean running = true;
	long milliseconds;
	public Synchronized () {
		
	}
	public Synchronized (long milliseconds) {
		this.milliseconds = milliseconds;
	}
  
	@Override
	public  void run() {
		testMethod();
		
	}
	public synchronized void  testMethod() {
		try{
		while(running) {
		System.out.println("Thread enters"+ Thread.currentThread().getName());
		Thread.sleep(milliseconds);
		}
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void setRunning(boolean running) {
		this.running = running;
	}

}
