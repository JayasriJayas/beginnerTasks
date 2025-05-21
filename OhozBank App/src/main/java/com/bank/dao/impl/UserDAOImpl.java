package com.bank.dao.impl;
import java.math.BigDecimal;
import java.sql.Connection;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.RequestDAO;
import com.bank.dao.UserDAO;
import com.bank.enums.RequestStatus;
import com.bank.enums.UserRole;
import com.bank.enums.UserStatus;
import com.bank.mapper.CustomerMapper;
import com.bank.mapper.RequestMapper;
import com.bank.mapper.UserMapper;
import com.bank.models.Account;
import com.bank.models.Customer;
import com.bank.models.Request;
import com.bank.models.User;
import com.bank.util.IdGeneratorUtil;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;
import com.bank.models.User;

import exception.QueryException;
public class UserDAOImpl implements UserDAO{
@Override
public boolean approveRequestAndCreateUser(long requestId, long adminId) throws SQLException,QueryException{
    try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
        conn.setAutoCommit(false);

        Request req = getRequestById(requestId);
        
        User user = UserMapper.fromRequest(req);
        long userId = IdGeneratorUtil.generateUserId();
        user.setUserId(userId);
        insertUser(conn, user);
      

        Customer customer = CustomerMapper.fromRequest(req, userId);

        insertCustomer(conn, customer);
        Account account = new Account();
        account.setUserId(userId);
        account.setBranchId(req.getBranchId());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(UserStatus.ACTIVE);
        account.setCreatedAt(System.currentTimeMillis());
        account.setModifiedBy("admin-" + adminId);
       
        insertAccount(conn, account);
        System.out.println("successful");
        updateRequestStatus(conn, requestId, adminId, RequestStatus.APPROVED);

        conn.commit();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        throw new SQLException(e.getMessage());
        
    }
    
}
public long insertUser(Connection conn, User user) throws SQLException,QueryException {
    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    qb.insertInto("user","userId", "username", "password", "email", "phone", "gender", "roleId", "status","createdAt", "modifiedBy")
      .values(user.getUserId(),user.getUsername(), user.getPassword(), user.getEmail(), user.getPhone(),
              user.getGender(), user.getRoleId(), user.getStatus().name(),user.getCreatedDate(),
              user.getModifiedBy());

    QueryExecutor executor = new QueryExecutor(conn);
    return executor.executeUpdate(qb.build(), qb.getParameters());
}
public boolean insertCustomer(Connection conn, Customer customer) throws SQLException,QueryException {
    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    qb.insertInto("customer", "userId", "aadharNo", "panNo", "address", "dob", "maritalStatus", "occupation", "annualIncome")
      .values(customer.getUserId(), customer.getAadharNo(), customer.getPanNo(), customer.getAddress(),
              customer.getDob(), customer.getMaritalStatus(), customer.getOccupation(), customer.getAnnualIncome());
 
    QueryExecutor executor = new QueryExecutor(conn);
    return executor.executeUpdate(qb.build(), qb.getParameters()) > 0;
}
public boolean insertAccount(Connection conn, Account account) throws SQLException,QueryException {
    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    qb.insertInto("account", "userId", "branchId", "balance", "status","createdAt", "modifiedBy")
      .values(account.getUserId(), account.getBranchId(), account.getBalance(), account.getStatus(),
               account.getCreatedAt(),account.getModifiedBy());

    QueryExecutor executor = new QueryExecutor(conn);
    return executor.executeUpdate(qb.build(), qb.getParameters()) > 0;
}
public boolean updateRequestStatus(Connection conn, long requestId, long adminId, RequestStatus approved) throws SQLException,QueryException {
    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    qb.update("requests")
      .set("status", approved)
      .set("processedBy", adminId)
      .set("processedTimestamp", System.currentTimeMillis())
      .where("id = ?" , requestId);

    QueryExecutor executor = new QueryExecutor(conn);
    return executor.executeUpdate(qb.build(), qb.getParameters()) > 0;
}

public Request getRequestById(long id) throws QueryException, SQLException {
    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    qb.select("*").from("requests").where("id = ?");
    String query = qb.build();
    List<Object> params = qb.getParameters();
    params.add(id); 
    QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
    List<Map<String,Object>> rs = qe.executeQuery(query, params);
       
    return RequestMapper.fromResultSet(rs);

}
@Override
public User findByUsername(String username) throws SQLException,QueryException{
    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    qb.select("*")
      .from("user")
      .where("username = ?",username);
//      .andWhere("password = ?",password);

    List<Object> params = qb.getParameters();
 

    String query = qb.build();
    System.out.println(query);
    QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
    List<Map<String,Object>> rs = qe.executeQuery(query, params);
    if (rs == null || rs.isEmpty()) {
    	
    	return null;
    }
    
    Map<String, Object> row = rs.get(0); 
    
    User user = new User();
    user.setUserId((Long) row.get("userId"));
    user.setUsername((String)row.get("username"));
    user.setPassword((String)row.get("password"));
    System.out.println(row.get("roleId"));
    user.setRoleId((Integer)row.get("roleId"));
    return user;
            
}
}

