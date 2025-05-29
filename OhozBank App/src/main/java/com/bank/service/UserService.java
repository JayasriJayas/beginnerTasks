package com.bank.service;

import java.sql.SQLException;

import com.bank.enums.UserRole;
import com.bank.models.Request;
import com.bank.models.User;

import exception.QueryException;

public interface UserService {
    boolean isAdmin(User user);
    UserRole getUserRole(User user);
}

