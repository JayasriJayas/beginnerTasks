package com.bank.service.impl;


import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.RequestDAO;
import com.bank.dao.impl.RequestDAOImpl;
import com.bank.models.Request;
import com.bank.models.User;
import com.bank.service.UserService;
import com.bank.util.PasswordUtil;
import com.bank.dao.impl.UserDAOImpl;
import com.bank.enums.UserRole;
import com.bank.dao.UserDAO;
import com.bank.dao.AdminDAO;
import com.bank.dao.impl.AdminDAOImpl;


import exception.QueryException;

public class UserServiceImpl implements UserService {
	
	private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public boolean isAdmin(User user) {
        UserRole role = UserRole.fromId(user.getRoleId());
        return role == UserRole.ADMIN || role == UserRole.SUPERADMIN;
    }
    
    @Override
    public UserRole getUserRole(User user) {
        return UserRole.fromId(user.getRoleId());
    }

}


