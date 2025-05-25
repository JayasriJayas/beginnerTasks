package com.bank.service;

import java.sql.SQLException;

import com.bank.enums.UserRole;
import com.bank.models.Request;
import com.bank.models.User;

import exception.QueryException;

public interface UserService {
	 boolean registerRequest(Request request);
	 boolean approveUserRequest(long requestId, long adminId);
	 boolean isAdmin(User user) ;
	 public UserRole getUserRole(User user) ;
	 boolean isAdminInSameBranch(long adminId, long requestBranchId);
	 Request getRequestById(long requestId);
	 User login(String username, String password) throws SQLException,QueryException;
}
