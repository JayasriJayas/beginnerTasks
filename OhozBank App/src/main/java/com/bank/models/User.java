package com.bank.models;

import java.time.Instant;

import com.bank.enums.Gender;
import com.bank.enums.UserStatus;

public class User {
	private long userId;
    private String username;
    private String password;
    private String name;
	private String email;
    private long phone;
    private Gender gender;
    private int roleId;
    private long createdDate;
    private long modifiedAt;
    private long modifiedBy;
    private UserStatus status;
    private long branchId;

    public User() {
        this.createdDate = System.currentTimeMillis();  
        this.status = UserStatus.ACTIVE;
    }
    public long getUserId() {
    	return userId;
    }
    public void setUserId(long userId) {
    	this.userId = userId;
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
    
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
    
    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
    public long getBranchId() {
    	return this.branchId;
    }
    public void setBranchId(long branchId) {
    	this.branchId = branchId;
    }

}
