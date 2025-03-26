package com.zoho.jdbc.employee;

public class Employee {
	private int EmpId;
	private String EmpName;
	private String mobile;
	private String email;
	private String department;

	public int getEmpId() {
		return this.EmpId;
	}
	public String getEmpName() {
		return this.EmpName;
	}
	public String getMobile() {
		return this.mobile;
	}
	public String getEmail() {
		return this.email;
	}
	public String getDepartment() {
		return this.department;
	}
	public void setEmpId(int id) {
		this.EmpId = id;
		
	}
	public void setEmpName(String name) {
		this.EmpName = name;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public void setEmail(String mail) {
		this.email=mail;
	}
	public void setDepartment(String department) {
		this.department = department;
		
	}
	
	@Override
	public String toString() {
		return String.format("EmpId: %d EmpName: %s Mobile: %s Email: %s Department: %s",EmpId,EmpName,mobile,email,department);
	}
	
}
