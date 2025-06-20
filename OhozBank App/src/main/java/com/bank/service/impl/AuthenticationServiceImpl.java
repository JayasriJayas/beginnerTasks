package com.bank.service.impl;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.UserDAO;
import com.bank.factory.DaoFactory;
import com.bank.models.User;
import com.bank.service.AuthenticationService;
import com.bank.util.PasswordUtil;

import exception.QueryException;

public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = Logger.getLogger(AuthenticationServiceImpl.class.getName());
    private final UserDAO userDAO = DaoFactory.getUserDAO();

    @Override
    public User login(String username, String password) throws SQLException, QueryException {
        try {
            User user = userDAO.findByUsername(username);
            if (user == null) {
                logger.warning("Login failed: User not found - " + username);
                return null;
            }

            boolean passwordMatch = PasswordUtil.checkPassword(password, user.getPassword());
            if (!passwordMatch) {
                logger.warning("Login failed: Incorrect password for user - " + username);
                return null;
            }

            logger.info("Login successful for user: " + username);
            return user;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during login for user: " + username, e);
            throw e;
        }
    }
}
