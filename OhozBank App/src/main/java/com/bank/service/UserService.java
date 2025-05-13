package com.bank.service;

import com.bank.models.Request;
import com.bank.models.User;

public interface UserService {
	 boolean registerRequest(Request request);
	 boolean approveUserRequest(long requestId, long adminId);
	 User login(String username, String password) throws Exception;
}
