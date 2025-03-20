package com.zoho.threads.threadlocal;

public class ClassA {
	    static ThreadLocal<Integer> localValue = new ThreadLocal<>();
		public static void methodA(int input) {
			 localValue.set(input);
			 System.out.println("Method A ");
			 System.out.println("The current thread "+Thread.currentThread().getName()+" The given input "+ input);
			 ClassB.methodB();
		}
}
