package com.zoho.threads.threadlocal;

public class ClassC {
	public static void methodC()
	{
		System.out.println("Method C");
		System.out.println("The current thread "+Thread.currentThread().getName());
		ClassD.methodD();
	}
}
