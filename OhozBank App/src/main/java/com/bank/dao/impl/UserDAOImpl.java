package com.bank.dao.impl;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.bank.connection.DBConnectionPool;
import com.bank.dao.UserDAO;
import com.bank.enums.RequestStatus;
import com.bank.enums.UserStatus;
import com.bank.mapper.CustomerMapper;
import com.bank.mapper.RequestMapper;
import com.bank.mapper.UserMapper;
import com.bank.models.Account;
import com.bank.models.Customer;
import com.bank.models.Request;
import com.bank.models.User;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;
import exception.QueryException;

public class UserDAOImpl implements UserDAO{
	@Override
	public boolean approveRequestAndCreateUser(long requestId, long adminId) throws SQLException,QueryException{
	    try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	        conn.setAutoCommit(false);
	        
	        Request req = getRequestById(requestId);
	     
	        User user = UserMapper.fromRequest(req);
	        System.out.println(user.getName());
	        long userId = insertUser(conn, user);
	
	 
	        Customer customer = CustomerMapper.fromRequest(req, userId);
	
	        insertCustomer(conn, customer);
	
	        Account account = new Account();
	        account.setUserId(userId);
	        account.setBranchId(req.getBranchId());
	        account.setBalance(BigDecimal.ZERO);
	        account.setStatus(UserStatus.ACTIVE);
	        account.setCreatedAt(System.currentTimeMillis());
	        account.setModifiedAt(System.currentTimeMillis());
	        account.setModifiedBy(adminId);
	       
	        insertAccount(conn, account);
	  
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
	    qb.insertInto("user", "username", "password","name", "email", "phone", "gender", "roleId", "status","createdAt", "modifiedBy")
	      .values(user.getUsername(), user.getPassword(),user.getName() ,user.getEmail(), user.getPhone(),
	              user.getGender(), user.getRoleId(), user.getStatus().name(),user.getCreatedDate(),
	              user.getModifiedBy());
	
	    QueryExecutor executor = new QueryExecutor(conn);
	    List<Object> rs =  executor.executeUpdateWithGeneratedKeys(qb.build(), qb.getParameters());
	    BigInteger id = (BigInteger) rs.get(0);
	    return id.longValue();
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
	    qb.insertInto("account", "userId", "branchId", "balance", "status","createdAt","modifiedAt", "modifiedBy")
	      .values(account.getUserId(), account.getBranchId(), account.getBalance(), account.getStatus(),
	               account.getCreatedAt(),account.getModifiedAt(),account.getModifiedBy());
	
	    QueryExecutor executor = new QueryExecutor(conn);
	    return executor.executeUpdate(qb.build(), qb.getParameters()) > 0;
	}
	public boolean updateRequestStatus(Connection conn, long requestId, long adminId, RequestStatus approved) throws SQLException,QueryException {
	    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	    qb.update("request")
	      .set("status", approved)
	      .set("processedBy", adminId)
	      .set("processedTimestamp", System.currentTimeMillis())
	      .where("id = ?" , requestId);
	
	    QueryExecutor executor = new QueryExecutor(conn);
	    return executor.executeUpdate(qb.build(), qb.getParameters()) > 0;
	}
	
	public Request getRequestById(long id) throws QueryException, SQLException {
	    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	    qb.select("*").from("request").where("id = ?",id);
	    String query = qb.build();
	    List<Object> params = qb.getParameters();
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
	
	
	    List<Object> params = qb.getParameters();
	 
	
	    String query = qb.build();
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
	    user.setRoleId((Integer)row.get("roleId"));
	    user.setBranchId((Long) row.get("branchId"));
	    return user;
	            
	}
	@Override
	public boolean existsByUsername(String username)throws SQLException,QueryException {
		  QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		  qb.select("1").from("user").where("username = ?", username);
		  QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
		  List<Map<String, Object>> result = qe.executeQuery(qb.build(), qb.getParameters()); 
	
		  return !result.isEmpty();
	}
	
	
	@Override
	    public User getUserById(long userId)throws SQLException,QueryException {
		QueryBuilder qb = new QueryBuilder(new MySQLDialect());
	    qb.select("*").from("user").where("userId = ?",userId);
	    QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
	    List<Map<String, Object>> result = qe.executeQuery(qb.build(), qb.getParameters());    
	    return UserMapper.fromResultSet(result);
	    }
	
	@Override
	public boolean updateUserProfile(User user) throws SQLException,QueryException{
		QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		qb.update("user");
		if(user.getName() != null) {
			qb.set("name", user.getName());
		}
		if(user.getPhone() != 0) {
			qb.set("phone", user.getPhone());
			
		}
		if(user.getEmail()!= null ) {
			qb.set("email",user.getEmail());
			
		}
		if(user.getGender() != null) {
			qb.set("gender", user.getGender());
		}
		qb.set("modifiedAt", user.getModifiedAt());
	    qb.set("modifiedBy", user.getModifiedBy());
		
		qb.where("userId =?", user.getUserId());
		QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
    	int rowsAffected = qe.executeUpdate( qb.build(),qb.getParameters());
	    return rowsAffected >0;
		
	}
}



