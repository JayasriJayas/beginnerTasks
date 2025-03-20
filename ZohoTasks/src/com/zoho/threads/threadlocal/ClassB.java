package com.zoho.threads.threadlocal;

public class ClassB {
	public static void methodB(){
		System.out.println("Method B");
		System.out.println("The current thread "+Thread.currentThread().getName());
		ClassC.methodC();
	}
}
