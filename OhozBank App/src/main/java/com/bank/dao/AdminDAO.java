package com.bank.dao;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.models.Admin;
import com.bank.models.User;

import exception.QueryException;

public interface AdminDAO {
    boolean isAdminExists(String username) throws SQLException,QueryException;
    boolean saveAdmin(User user,long superAdminId) throws SQLException, QueryException;
    Admin getAdminByUserId(long userId)throws SQLException , QueryException;
    boolean updateAdmin(Admin admin) throws SQLException,QueryException;
    Map<String, Object> fetchAdmin(long adminId) throws SQLException, QueryException;
    List<Map<String, Object>> fetchAllAdmins() throws SQLException, QueryException;

}
