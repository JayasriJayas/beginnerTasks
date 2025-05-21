package com.bank.dao;

import java.sql.SQLException;

import exception.QueryException;

public interface AdminDAO {
    long getBranchIdByAdminId(long adminId)throws SQLException, QueryException;
}
