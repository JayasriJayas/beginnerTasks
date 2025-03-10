package com.zoho.files.singleton;

public class EagarInitialization implements Cloneable	 {
	public static final EagarInitialization instance = new EagarInitialization();
	
	private EagarInitialization() {	
	}
	
	public static EagarInitialization getInstance() {
		return instance;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
	    throw new CloneNotSupportedException("Cloning cannot be done"); 
	}


	
}
