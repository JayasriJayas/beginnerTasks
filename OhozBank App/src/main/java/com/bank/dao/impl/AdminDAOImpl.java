package com.bank.dao.impl;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.AdminDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDAOImpl implements AdminDAO {

    @Override
    public long getBranchIdByAdminId(long adminId) {
        String query = "SELECT branchId FROM admin WHERE adminId = ?";
        try (
            Connection conn = DBConnectionPool.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setLong(1, adminId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("branchId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; 
    }
}
