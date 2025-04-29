package com.bank.models;

import java.time.LocalDate;

public class User {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String gender;
    private LocalDate dob;
    private String address;
    private String maritalStatus;
    private String aadharNo;
    private String panNo;
    private int branchId;
    private String branchName;
    private String occupation;
    private double annualIncome;
    private int roleId; 
    private LocalDate createdDate;
    private LocalDate modifiedAt;
    private String modifiedBy;
	private boolean active;

    
    public User() {
        this.createdDate = LocalDate.now();
        this.active = true;
    }

    // Getters and setters for all fields
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    public LocalDate getModifiedAt() {
  		return modifiedAt;
  	}

  	public void setModifiedAt(LocalDate modifiedAt) {
  		this.modifiedAt = modifiedAt;
  	}

  	public String getModifiedBy() {
  		return modifiedBy;
  	}

  	public void setModifiedBy(String modifiedBy) {
  		this.modifiedBy = modifiedBy;
  	}


//    @Override
//    public String toString() {
//        return "User{" +
//                "username='" + username + '\'' +
//                ", email='" + email + '\'' +
//                ", phone='" + phone + '\'' +
//                ", branchName='" + branchName + '\'' +
//                ", roleId=" + roleId +
//                ", createdDate=" + createdDate +
//                ", active=" + active +
//                '}';
//    }
}
