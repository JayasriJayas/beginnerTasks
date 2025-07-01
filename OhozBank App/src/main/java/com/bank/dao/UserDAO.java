package com.bank.dao;

import java.sql.SQLException;

import com.bank.models.User;

import exception.QueryException;

public interface UserDAO {
	boolean existsByUsername(String username) throws SQLException, QueryException;
	boolean approveRequestAndCreateUser(long requestId, long adminId) throws SQLException,QueryException;
	 User findByUsername(String username) throws SQLException,QueryException;
	 User getUserById(long userId) throws SQLException,QueryException;
	 boolean updateUserProfile(User user) throws SQLException,QueryException;
	 boolean updatePassword(long userId, String newPassword) throws SQLException, QueryException;
	 int countAllUsersOnly() throws SQLException, QueryException;
	 int countUsersOnlyByBranch(Long branchId) throws SQLException, QueryException;

}
