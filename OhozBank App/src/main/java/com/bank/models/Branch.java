package com.bank.models;

import java.math.BigDecimal;

public class Branch {
    private Long branchId;
    private String branchName;
    private String ifscCode;
    private String location;
    private Long contact;
    private Long adminId;
    private long createdAt;
    private long modifiedAt;
    private long modifiedBy;
    private BigDecimal totalOutgoing;
    private BigDecimal totalIncoming;
    private BigDecimal totalFunds;
    

    
    public BigDecimal getTotalFunds() {
		return totalFunds;
	}

	public void setTotalFunds(BigDecimal totalFunds) {
		this.totalFunds = totalFunds;
	}

	public BigDecimal getTotalIncoming() {
		return totalIncoming;
	}

	public void setTotalIncoming(BigDecimal totalIncoming) {
		this.totalIncoming = totalIncoming;
	}

	public BigDecimal getTotalOutgoing() {
		return totalOutgoing;
	}

	public void setTotalOutgoing(BigDecimal totalOutgoing) {
		this.totalOutgoing = totalOutgoing;
	}

	public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getContact() {
        return contact;
    }

    public void setContact(Long contact) {
        this.contact = contact;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
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
}
