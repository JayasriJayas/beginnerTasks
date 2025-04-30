package com.bank.service.impl;

import com.bank.dao.RequestDAO;
import com.bank.dao.UserDAO;
import com.bank.dao.impl.RequestDAOImpl;
import com.bank.dao.impl.UserDAOImpl;
import com.bank.mapper.UserMapper;
import com.bank.models.Request;
import com.bank.models.User;
import com.bank.service.UserService;

public class UserServiceImpl implements UserService {

    private final RequestDAO requestDAO = new RequestDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public boolean registerRequest(Request request) {
        return requestDAO.saveRequest(request) > 0;
    }

    @Override
    public boolean approveRequest(String username) {
        Request r = requestDAO.getPendingRequestByUsername(username);
        if (r == null) return false;

        User user = UserMapper.fromRequest(r);
        boolean added = userDAO.addUser(user) > 0;
        boolean updated = requestDAO.markRequestAsApproved(username);
        return added && updated;
    }

    @Override
    public boolean rejectRequest(String username) {
        return requestDAO.markRequestAsRejected(username);
    }

    @Override
    public User authenticate(String username, String password) {
        return userDAO.authenticateUser(username, password);
    }
}
