package com.zoho.files.singleton;

public class EagarInitialization {
	public static final EagarInitialization instance = new EagarInitialization();
	
	private EagarInitialization() {	
	}
	
	public static EagarInitialization getInstance() {
		return instance;
	}
	
}
