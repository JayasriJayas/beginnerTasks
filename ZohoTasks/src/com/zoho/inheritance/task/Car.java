package com.zoho.inheritance.task;


public class Car{
	private int yearOfMake;
	private String engineNumber;
	private String type;
	public Car()
	{
	}
	public Car(String string){
		System.out.println(string);
	}
	
	public int getYearOfMake(){
		return yearOfMake;
	}
	public String getEngineNumber(){
		return engineNumber;
	}
	public String getType(){
		return type;
	}
	public void setYearOfMake(int year){
		this.yearOfMake = year;
	}
	public void setEngineNumber(String engineNumber){
		this.engineNumber = engineNumber;
	}
	public void setType(String type){
		this.type=type;
	}
	public void maintenance(){
	System.out.println("Car under maintenance");
}
}

		

		
	