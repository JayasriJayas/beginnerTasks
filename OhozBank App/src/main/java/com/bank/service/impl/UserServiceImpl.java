package com.bank.service.impl;

import java.sql.SQLException;

import com.bank.dao.RequestDAO;
import com.bank.dao.impl.RequestDAOImpl;
import com.bank.models.Request;
import com.bank.models.User;
import com.bank.service.UserService;

import exception.QueryException;

public class UserServiceImpl implements UserService {

    private final RequestDAO requestDAO = new RequestDAOImpl();

    @Override
    public boolean registerRequest(Request request) {
        try {
            return requestDAO.saveRequest(request) > 0;
        } catch (QueryException | SQLException e) {
            // Log the error
            System.err.println("Error occurred while saving request: " + e.getMessage());
            e.printStackTrace(); // Or use a logger
            return false;
        }
    }
}
	
