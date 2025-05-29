package com.bank.models;

import java.math.BigDecimal;
import java.time.Instant;

import com.bank.enums.UserStatus;

public class Account {
    private long accountId;
    private long branchId;
    private long userId;
    private BigDecimal balance;
    private UserStatus status; 
    private long createdAt;
    private long modifiedAt;
    private long modifiedBy;

    
    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getBranchId() {
        return branchId;
    }

    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long  getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(long adminId) {
        this.modifiedBy = adminId;
    }
}
