package com.bank.service;

import com.bank.models.Request;

public interface RequestService {
    boolean registerRequest(Request request);
    boolean approveUserRequest(long requestId, long adminId);
    boolean isAdminInSameBranch(long adminId, long requestBranchId);
    Request getRequestById(long requestId);
}