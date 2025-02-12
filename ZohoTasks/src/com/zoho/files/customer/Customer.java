package com.zoho.files.customer;

public class Customer {
	private String customerName;
	public Customer(String name){
		this.customerName = name;
	}
	
	@Override 
	public String toString() {
		return customerName;
	}
	
}