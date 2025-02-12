package com.zoho.files.applicant;

public class Applicant {
	

	private String name;
	private int applicantNo;
	private int age;
	private String DOB;
	private String address;
	
	public Applicant(String name,int applicantNo,int age,String DOB,String address){
		this.name = name;
		this.applicantNo = applicantNo;
		this.age = age;
		this.DOB = DOB;
		this.address = address;
		
	}
	public Applicant() {}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getApplicantNo() {
		return applicantNo;
	}

	public void setApplicantNo(int applicantNo) {
		this.applicantNo = applicantNo;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getDOB() {
		return DOB;
	}

	public void setDOB(String dOB) {
		DOB = dOB;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return String.format("Name: %s Age: %d Applicant No: %d DOB: %s Address: %s ",name,age,applicantNo,DOB,address);
	}

}
