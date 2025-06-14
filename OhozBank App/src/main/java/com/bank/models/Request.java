package com.bank.models;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;

import com.bank.enums.Gender;
import com.bank.enums.RequestStatus;

public class Request {
    private Long id;
    private String username;
    private String password;
	private String name;
    private String email;
    private String address;
    private long phone;
    private Gender gender;
    private long aadharNo;
    private String panNo;
    private String maritalStatus;
    private String occupation;
    private double annualIncome;
    private long branchId;
    private long requestTimestamp;
    private RequestStatus status;
    private String rejectionReason;
    private long processedBy;
    private long processedTimestamp;
    private Date dob;



 
    public Request() {
        this.requestTimestamp = System.currentTimeMillis();
        this.status = RequestStatus.PENDING;
    }

    public  Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }


    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public long getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(long aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
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

    public long getBranchId() {
        return branchId;
    }

    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(long requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public long getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(long processedBy) {
        this.processedBy = processedBy;
    }

    public long getProcessedTimestamp() {
        return processedTimestamp;
    }

    public void setProcessedTimestamp(long processedTimestamp) {
        this.processedTimestamp = processedTimestamp;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
