package com.zoho.files.singleton;

public class LazyInitialization {
	private static LazyInitialization instance;
	
	private LazyInitialization (){
		if(instance!=null) {
			 throw new RuntimeException("Use getInstance() method to create an instance");
		}
//		handling reflection while violating singleton property
	}
	
	public static LazyInitialization getInstance() {
		if(instance == null) {
			instance= new LazyInitialization();
		}
		return instance;
	}
}
