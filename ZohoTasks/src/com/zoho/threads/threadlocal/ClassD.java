package com.zoho.threads.threadlocal;

public class ClassD {
	public static void methodD() {
		Integer finalVal = ClassA.localValue.get(); 
		System.out.println("The current thread "+Thread.currentThread().getName());
		System.out.println("Printing the input in ClassD "+finalVal);
	}

}
