package com.zoho.arraylist.customclass;


public class Employee{
	private String name;
	private String empid;
	public Employee(String name,String empid){
		this.name=name;
		this.empid=empid;
	}
	public String getName(){
		return name;
	}
	public String getEmpId(){
		return empid;
	}
	public void setName(String name){
		this.name=name;
	}
	public void setEmpId(String empid){
		this.empid=empid;
	}
	
}