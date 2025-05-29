package com.bank.service.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.models.User;
import com.bank.service.AdminService;
import com.bank.util.PasswordUtil;
import com.bank.dao.AdminDAO;
import com.bank.dao.impl.AdminDAOImpl;

public class AdminServiceImpl implements AdminService {
    private final AdminDAO adminDAO = new AdminDAOImpl();
    private final Logger logger = Logger.getLogger(AdminServiceImpl.class.getName());

    @Override
    public boolean addAdmin(User user,long adminId) {
        try {
            if (adminDAO.isAdminExists(user.getUsername())) {
                logger.warning("Admin already exists: " + user.getUsername());
                return false;
            }
            user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
            return adminDAO.saveAdmin(user,adminId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding admin", e);
            return false;
        }
    }

 
}
