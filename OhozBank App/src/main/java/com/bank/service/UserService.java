package com.bank.service;

import com.bank.models.Request;

public interface UserService {
	 boolean registerRequest(Request request);
	 boolean approveUserRequest(long requestId, long adminId);
}
