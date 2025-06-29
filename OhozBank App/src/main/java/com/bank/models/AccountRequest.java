package com.bank.models;

import com.bank.enums.RequestStatus;

public class AccountRequest {
    private long requestId;
    private long userId;
    private long branchId;
    private RequestStatus status; 
    private long createdAt;
    private Long approvedBy;
    private Long approvedAt;
    private String rejectionReason;
	public String getRejectionReason() {
		return rejectionReason;
	}
	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}
	public Long getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}
	public Long getApprovedAt() {
		return approvedAt;
	}
	public void setApprovedAt(Long approvedAt) {
		this.approvedAt = approvedAt;
	}
	public long getRequestId() {
		return requestId;
	}
	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getBranchId() {
		return branchId;
	}
	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}
	public RequestStatus getStatus() {
		return status;
	}
	public void setStatus(RequestStatus status) {
		this.status = status;
	}
	public long getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

 
}

