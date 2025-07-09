package com.bank.service;

import java.sql.SQLException;
import java.util.Map;

import com.bank.models.PaginatedResponse;
import com.bank.models.User;

import exception.QueryException;

public interface AdminService {
    boolean addAdmin(User user,long adminID,long branchId);
    boolean updateEmployeeDetails(Map<String, Object> updates) throws SQLException, QueryException;
    Map<String, Object> getAdminById(long adminId) throws SQLException;
    PaginatedResponse<Map<String, Object>> getAllAdmins(int page, int size) throws SQLException, QueryException ;
    int getTotalAdminCount() throws SQLException, QueryException;


}