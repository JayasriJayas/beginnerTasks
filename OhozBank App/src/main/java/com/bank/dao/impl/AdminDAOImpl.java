package com.bank.dao.impl;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.AdminDAO;
import com.bank.models.User;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminDAOImpl implements AdminDAO {
	
    public boolean isAdminExists(String username) throws SQLException,QueryException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.select("1").from("user").where("username = ?", username).limit(1);
    	List<Object> params = qb.getParameters();
    	QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
   	    String query = qb.build();
   	    List<Map<String,Object>> rs = qe.executeQuery(query, params);
	    
	    return !rs.isEmpty();
    }
    public boolean saveAdmin(User user,long superAdminId) throws SQLException, QueryException {
        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            conn.setAutoCommit(false); 

            try {
                long adminId = addAdminUser(conn, user,superAdminId);
                boolean addAdminTable = addAdmin(conn,user,adminId);
                int rowsUpdated = updateBranch(conn, user, superAdminId,adminId);

                if (rowsUpdated > 0 && adminId >0 && addAdminTable) {
                    conn.commit(); 
                    return true;
                } else {
                    conn.rollback(); 
                    return false; 
                }
            } catch (Exception e) {
                conn.rollback(); 
                e.printStackTrace();
                throw new SQLException("Failed to save admin and update branch", e);
            }
        }
    }

    private long addAdminUser(Connection conn, User user,long superAdminId) throws SQLException ,QueryException{
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.insertInto("user",
                "username", "password","name", "email", "phone", "gender","roleId",
                "branchId","status","createdAt","modifiedBy","modifiedAt")
           .values(user.getUsername(),user.getPassword(),user.getName(),user.getEmail(),user.getPhone(),
        		   user.getGender(),user.getRoleId(),user.getBranchId(),user.getStatus(),System.currentTimeMillis(),superAdminId,System.currentTimeMillis());

        QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
        List<Object> rs = qe.executeUpdateWithGeneratedKeys( qb.build() ,qb.getParameters());
        BigInteger bigIntId = (BigInteger) rs.get(0);
        return bigIntId.longValue();

    }
    private boolean addAdmin(Connection conn,User user,long adminId) throws SQLException, QueryException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.insertInto("admin","adminId","branchId").values(adminId,user.getBranchId());
    	QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
    	int rowsAffected = qe.executeUpdate( qb.build(),qb.getParameters());
	    return rowsAffected >0;
    	
    }
    
    public int updateBranch(Connection conn,User user,long superadminId,long adminId) throws QueryException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.update("branch")
    	.set("adminId",adminId )
        .set("modifiedBy", superadminId)
        .set("modifiedAt", System.currentTimeMillis())
        .where("branchId = ?", user.getBranchId());

      QueryExecutor qe = new QueryExecutor(conn);
      return qe.executeUpdate(qb.build(),  qb.getParameters());
    	
    	
    }

   
}
