package com.bank.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.RequestDAO;
import com.bank.dao.impl.RequestDAOImpl;
import com.bank.enums.RequestStatus;
import com.bank.enums.UserStatus;
import com.bank.mapper.CustomerMapper;
import com.bank.mapper.UserMapper;
import com.bank.models.Account;
import com.bank.models.Customer;
import com.bank.models.Request;
import com.bank.models.User;
import com.bank.service.UserService;

import exception.QueryException;

public class UserServiceImpl implements UserService {

    private final RequestDAO requestDAO = new RequestDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();
    

    @Override
    public boolean registerRequest(Request request) {
        try {
            return requestDAO.saveRequest(request) > 0;
        } catch (QueryException | SQLException e) {
            // Log the error
            System.err.println("Error occurred while saving request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
        @Override
        public boolean approveUserRequest(long requestId, long adminId) {
            try {
                return userDAO.approveRequestAndCreateUser(requestId, adminId);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    


}
	
