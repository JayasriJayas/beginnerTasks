package com.zoho.files.singleton;

public class StaticBlockInitialization {
	private static StaticBlockInitialization instance;
	
	private StaticBlockInitialization()
	{
	}
	
	static {
		try {
			instance = new StaticBlockInitialization();
			
		}
		catch(Exception e) {
			throw new RuntimeException("Exception while creating singleton instance");
		}
	}
	
	public static StaticBlockInitialization getInstance() {
		return instance;
	}
}
