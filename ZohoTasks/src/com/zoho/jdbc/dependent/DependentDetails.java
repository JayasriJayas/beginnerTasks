package com.zoho.jdbc.dependent;

public class DependentDetails {
    private int employeeId;
    private String employeeName;
    private int dependentId;
    private String dependentName;
    private int age;
    private String relationship;


    public DependentDetails(int employeeId, String employeeName, int dependentId, String dependentName, int age, String relationship) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.dependentId = dependentId;
        this.dependentName = dependentName;
        this.age = age;
        this.relationship = relationship;
    }

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

    @Override
    public String toString() {
        return String.format(
            "Employee ID   : %d\n Employee Name : %s\n Dependent ID  : %d\n Dependent Name: %s\n  Age : %d\n Relationship  : %s\n", employeeId, employeeName, dependentId, dependentName, age, relationship);

    }


}
