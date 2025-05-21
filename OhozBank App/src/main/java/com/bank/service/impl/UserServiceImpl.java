package com.bank.service.impl;


import java.sql.SQLException;
import com.bank.dao.RequestDAO;
import com.bank.dao.impl.RequestDAOImpl;
import com.bank.models.Request;
import com.bank.models.User;
import com.bank.service.UserService;
import com.bank.util.PasswordUtil;
import com.bank.dao.impl.UserDAOImpl;
import com.bank.dao.UserDAO;


import exception.QueryException;

public class UserServiceImpl implements UserService {

    private final RequestDAO requestDAO = new RequestDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();
    

    @Override
    public boolean registerRequest(Request request) {
        try {
            return requestDAO.saveRequest(request) > 0;
        } catch (QueryException | SQLException e) {
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

        @Override
        public User login(String username, String password) throws SQLException,QueryException {
            System.out.println(username);
            System.out.println(password);
            boolean match = false;
            User user = userDAO.findByUsername(username);
           
            
            if (user == null) {
 
                return null;
            }
//            return user;
           
            boolean passwordMatch = PasswordUtil.checkPassword(password, user.getPassword());
            return passwordMatch ? user : null;
            
            
           
        }
}


