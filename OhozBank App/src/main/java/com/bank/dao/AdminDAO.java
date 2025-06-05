package com.bank.dao;

import java.sql.SQLException;

import com.bank.models.Admin;
import com.bank.models.User;

import exception.QueryException;

public interface AdminDAO {
    boolean isAdminExists(String username) throws SQLException,QueryException;
    boolean saveAdmin(User user,long superAdminId) throws SQLException, QueryException;
    Admin getAdminByUserId(long userId)throws SQLException , QueryException;
}
