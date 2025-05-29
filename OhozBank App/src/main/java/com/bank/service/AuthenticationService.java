package com.bank.service;

import java.sql.SQLException;

import com.bank.models.User;

import exception.QueryException;

public interface AuthenticationService {
    User login(String username, String password) throws SQLException, QueryException;

}
