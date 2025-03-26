package com.zoho.jdbc.dependent;

public class DependentDetails {
    private int employeeId;
    private String employeeName;
    private int dependentId;
    private String dependentName;
    private int age;
    private String relationship;


    public int getEmployeeId() {
    	return employeeId; 	
    }
    public String getEmployeeName() { 
    	return employeeName; 
    }
    public int getDependentId() { 
    	return dependentId; 
    }
    public String getDependentName() { 
    	return dependentName;
    }
    public int getAge() { 
    	return age;
    }
    public String getRelationship() { 
    	return relationship; 
    }
    public void setDependentId(int id) {
    	this.dependentId = id;    	
    }
    public void setDependentName(String name) {
    	this.dependentName = name;
    }
    public void setAge(int age) {
    	this.age = age;
    }
    public void setRelationship(String relation) {
    	this.relationship = relation;
    }
    public void setEmployeeId(int id) {
    	this.employeeId = id;
    }
    public void setEmployeeName(String name) {
    	this.employeeName = name;
    }

    @Override
    public String toString() {
        return String.format(
            "EmployeeID: %d\n EmployeeName:%s\n Dependent ID  : %d\n Dependent Name: %s\n  Age : %d\n Relationship  : %s\n",employeeId,employeeName,dependentId, dependentName, age, relationship);

    }


}
