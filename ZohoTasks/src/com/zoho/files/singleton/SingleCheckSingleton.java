package com.zoho.files.singleton;

public class SingleCheckSingleton {
	private static SingleCheckSingleton instance;
	private SingleCheckSingleton() {
		
	}
	public static synchronized SingleCheckSingleton getInstance() {
		if(instance == null) {
			instance = new SingleCheckSingleton();
		}
		return instance;
		
	}
}
