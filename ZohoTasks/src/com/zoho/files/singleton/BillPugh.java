package com.zoho.files.singleton;

public class BillPugh {
	
	
	private BillPugh() {}
	
	private static class InnerClass{
		private static final BillPugh instance = new BillPugh();
	} 
	
	public static BillPugh getInstance() {
		return InnerClass.instance;
	}
	

}
