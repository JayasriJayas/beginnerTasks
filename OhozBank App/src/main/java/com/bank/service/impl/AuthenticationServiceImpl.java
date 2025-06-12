package com.bank.service.impl;

import java.sql.SQLException;

import com.bank.dao.UserDAO;
import com.bank.dao.impl.UserDAOImpl;
import com.bank.models.User;
import com.bank.service.AuthenticationService;
import com.bank.util.PasswordUtil;

import exception.QueryException;

public class AuthenticationServiceImpl implements AuthenticationService {
	    private final UserDAO userDAO = new UserDAOImpl();

	    @Override
	    public User login(String username, String password) throws SQLException, QueryException {
	        User user = userDAO.findByUsername(username);
	        if (user == null) return null;
	        boolean passwordMatch = PasswordUtil.checkPassword(password, user.getPassword());
	        return passwordMatch ? user : null;
	    }


	}



