package com.bank.service;

import java.sql.SQLException;

import com.bank.models.Request;
import com.bank.models.User;

import exception.QueryException;

public interface UserService {
	 boolean registerRequest(Request request);
	 boolean approveUserRequest(long requestId, long adminId);
	 User login(String username, String password) throws SQLException,QueryException;
}
