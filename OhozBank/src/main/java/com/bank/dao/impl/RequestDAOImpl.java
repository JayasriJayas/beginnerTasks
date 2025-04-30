package com.bank.dao.impl;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.RequestDAO;
import com.bank.models.Request;
import com.querybuilder.QueryBuilder;
import com.dialect.MySQLDialect;
import exception.QueryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAOImpl implements RequestDAO {

    @Override
    public int saveRequest(Request req) {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.insertInto("requests",
                "username", "password", "email", "phone", "gender", "dob", "address",
                "maritalStatus", "aadharNo", "panNo", "branchId", "branchName", "occupation",
                "annualIncome", "requestDate", "status")
          .values(
              req.getUsername(), req.getPassword(), req.getEmail(), req.getPhone(), req.getGender(),
              req.getDob().toString(), req.getAddress(), req.getMaritalStatus(), req.getAadharNo(),
              req.getPanNo(), String.valueOf(req.getBranchId()), req.getBranchName(),
              req.getOccupation(), String.valueOf(req.getAnnualIncome()), req.getRequestDate().toString(),
              req.getStatus()
          );

        try (Connection conn = DBConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(qb.build())) {
            return stmt.executeUpdate();
        } catch (SQLException | QueryException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Request getPendingRequestByUsername(String username) {
        String sql = "SELECT * FROM requests WHERE username = ? AND status = 'PENDING'";
        try (Connection conn = DBConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Request r = new Request();
                r.setUsername(rs.getString("username"));
                r.setPassword(rs.getString("password"));
                r.setEmail(rs.getString("email"));
                r.setPhone(rs.getString("phone"));
                r.setGender(rs.getString("gender"));
                r.setDob(rs.getDate("dob").toLocalDate());
                r.setAddress(rs.getString("address"));
                r.setMaritalStatus(rs.getString("maritalStatus"));
                r.setAadharNo(rs.getString("aadharNo"));
                r.setPanNo(rs.getString("panNo"));
                r.setBranchId(rs.getInt("branchId"));
                r.setBranchName(rs.getString("branchName"));
                r.setOccupation(rs.getString("occupation"));
                r.setAnnualIncome(rs.getDouble("annualIncome"));
                return r;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean markRequestAsApproved(String username) {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.update("requests")
          .set("status = 'APPROVED'", "processedDate = CURRENT_DATE")
          .where("username = '" + username + "'");

        try (Connection conn = DBConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(qb.build())) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException | QueryException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Request> getAllPendingRequests() {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT * FROM requests WHERE status = 'PENDING'";
        try (Connection conn = DBConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Request r = new Request();
                r.setUsername(rs.getString("username"));
                r.setEmail(rs.getString("email"));
                r.setPhone(rs.getString("phone"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    @Override
    public boolean markRequestAsRejected(String username) {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.update("requests")
          .set("status = 'REJECTED'", "processedDate = CURRENT_DATE")
          .where("username = '" + username + "'");

        try (Connection conn = DBConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(qb.build())) {
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
