package com.bank.service;

import java.sql.SQLException;
import java.util.Map;

import com.bank.enums.UserRole;
import com.bank.models.Customer;
import com.bank.models.Request;
import com.bank.models.User;

import exception.QueryException;

public interface UserService  {
    boolean isAdmin(User user);
    UserRole getUserRole(User user);
    Map<String, Object> getProfile(String role, long userId)throws SQLException, QueryException ;
    boolean updateEditableProfileFields(long userId, Map<String, Object> updates) throws SQLException,QueryException;

}

