package com.bank.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.models.Branch;
import com.bank.models.User;

import exception.QueryException;

public interface AdminService {
    boolean addAdmin(User user,long adminID);
    boolean updateBranchDetails(long userId,Branch updates) throws SQLException, QueryException;
    boolean updateEmployeeDetails(Map<String, Object> updates) throws SQLException, QueryException;
    Map<String, Object> getAdminById(long adminId) throws SQLException;
    List<Map<String, Object>> getAllAdmins() throws SQLException;


}