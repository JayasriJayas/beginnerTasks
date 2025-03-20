	package com.zoho.threads.threadlocal;
	
	
	public class ThreadClass implements Runnable  {
		int value;
		
		@Override
		public void run(){
				ClassA.methodA(value);
		}
		public void setValue(int value) {
			this.value = value;
		}
	
	}
