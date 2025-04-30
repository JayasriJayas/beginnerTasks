package com.bank.dao.impl;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.UserDAO;
import com.bank.models.User;
import com.bank.util.PasswordUtil;
import com.querybuilder.DBConnector;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;
import com.dialect.MySQLDialect;
import exception.QueryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserDAOImpl implements UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class.getName());
    private final DBConnectionPool connectionPool;
    private final QueryBuilder queryBuilder;

    public UserDAOImpl() {
        this.connectionPool = DBConnectionPool.getInstance();
        this.queryBuilder = new QueryBuilder(new MySQLDialect());
    }

    @Override
    public int addUser(User user) {
        Connection connection = null;
        ResultSet resultSet = null;

        try {
        	connection = connectionPool.getConnection();
            QueryExecutor executor = new QueryExecutor(connection);

         String query =   queryBuilder
                .insertInto("users", 
                    "username", 
                    "password", 
                    "email", 
                    "phone", 
                    "gender", 
                    "dob", 
                    "address", 
                    "marital_status", 
                    "aadhar_no", 
                    "pan_no", 
                    "branch_id", 
                    "occupation", 
                    "annual_income", 
                    "role_id", 
                    "created_date", 
                    "active"
                )
                .values(
                    user.getUsername(),
                    PasswordUtil.hashPassword(user.getPassword()),
                    user.getEmail(),
                    user.getPhone(),
                    user.getGender(),
                    user.getDob().toString(), // Assuming dob is Date — converting to String for now
                    user.getAddress(),
                    user.getMaritalStatus(),
                    user.getAadharNo(),
                    user.getPanNo(),
                    String.valueOf(user.getBranchId()),
                    user.getOccupation(),
                    String.valueOf(user.getAnnualIncome()),
                    String.valueOf(user.getRoleId()),
                    Timestamp.valueOf(user.getCreatedDate().atStartOfDay()).toString(),
                    String.valueOf(user.isActive())
                ).build();
//                String query = queryBuilder.insertInto("Student",
//                	    "STUDENT_ID", 
//                	    "NAME", 
//                	    "AGE", 
//                	    "DEPARTMENT", 
//                	    "GPA", 
//                	    "marks", 
//                	    "grade", 
//                	    "status")
//                	.values (
//                	    "101", 
//                	    "Alice Johnson", 
//                	    "21", 
//                	    "Computer Science", 
//                	    "8.5", 
//                	    "430", 
//                	    "A", 
//                	    "active"
//                	)
//                .build();

           
            System.out.println("Executing Query: " + query);  // Log the final query

            // getParameters() will return List<Object> — values are already collected while values() method is called
            List<Object> params = new ArrayList<>(queryBuilder.getParameters());
            System.out.println("Executing Query: " + query);  // Log the final query
            System.out.println(params.toString());

            executor.execute(query, params);
            System.out.println("Executing: " + query);
         

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()")) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return -1;
        } catch (SQLException | QueryException e) {
            LOGGER.log(Level.SEVERE, "Error adding user: " + user.getUsername(), e);
            return -1;
        }
    }

@Override
public User authenticateUser(String username, String password) {
    QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    qb.select("*").from("users")
      .where("username = '" + username + "'")
      .andWhere("password = '" + password + "'")
      .andWhere("active = true");

    try (Connection conn = DBConnectionPool.getInstance().getConnection();
         PreparedStatement stmt = conn.prepareStatement(qb.build());
    		ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            User user = new User();
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setRoleId(rs.getInt("roleId"));
            // Add other fields as needed
            return user;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
}


//    @Override
//    public boolean updateUser(User user) {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        
//        try {
//            connection = connectionPool.getConnection();
//            
//            queryBuilder.reset()
//                    .table("users")
//                    .set("email", user.getEmail())
//                    .set("phone", user.getPhone())
//                    .set("address", user.getAddress())
//                    .set("marital_status", user.getMaritalStatus())
//                    .set("occupation", user.getOccupation())
//                    .set("annual_income", user.getAnnualIncome())
//                    .where("id = " + user.getId());
//            
//            String query = queryBuilder.buildUpdateQuery();
//            Object[] values = queryBuilder.getUpdateValues();
//            
//            statement = connection.prepareStatement(query);
//            
//            // Set parameters
//            for (int i = 0; i < values.length; i++) {
//                statement.setObject(i + 1, values[i]);
//            }
//            
//            int rowsAffected = statement.executeUpdate();
//            return rowsAffected > 0;
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error updating user with ID: " + user.getId(), e);
//            return false;
//        } finally {
//            closeResources(connection, statement, null);
//        }
//    }
//
//    @Override
//    public User getUserById(int userId) {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        
//        try {
//            connection = connectionPool.getConnection();
//            
//            queryBuilder.reset()
//                    .table("users u")
//                    .select("u.*", "b.branch_name", "r.name as role_name")
//                    .join("LEFT", "branches b", "u.branch_id = b.id")
//                    .join("LEFT", "roles r", "u.role_id = r.id")
//                    .where("u.id = ?");
//            
//            String query = queryBuilder.buildSelectQuery();
//            
//            statement = connection.prepareStatement(query);
//            statement.setInt(1, userId);
//            
//            resultSet = statement.executeQuery();
//            
//            if (resultSet.next()) {
//                return extractUserFromResultSet(resultSet);
//            }
//            
//            return null;
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error getting user by ID: " + userId, e);
//            return null;
//        } finally {
//            closeResources(connection, statement, resultSet);
//        }
//    }
//
//    @Override
//    public User getUserByUsername(String username) {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        
//        try {
//            connection = connectionPool.getConnection();
//            
//            queryBuilder.reset()
//                    .table("users u")
//                    .select("u.*", "b.branch_name", "r.name as role_name")
//                    .join("LEFT", "branches b", "u.branch_id = b.id")
//                    .join("LEFT", "roles r", "u.role_id = r.id")
//                    .where("u.username = ?");
//            
//            String query = queryBuilder.buildSelectQuery();
//            
//            statement = connection.prepareStatement(query);
//            statement.setString(1, username);
//            
//            resultSet = statement.executeQuery();
//            
//            if (resultSet.next()) {
//                return extractUserFromResultSet(resultSet);
//            }
//            
//            return null;
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error getting user by username: " + username, e);
//            return null;
//        } finally {
//            closeResources(connection, statement, resultSet);
//        }
//    }
//
//    @Override
//    public User getUserByEmail(String email) {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        
//        try {
//            connection = connectionPool.getConnection();
//            
//            queryBuilder.reset()
//                    .table("users u")
//                    .select("u.*", "b.branch_name", "r.name as role_name")
//                    .join("LEFT", "branches b", "u.branch_id = b.id")
//                    .join("LEFT", "roles r", "u.role_id = r.id")
//                    .where("u.email = ?");
//            
//            String query = queryBuilder.buildSelectQuery();
//            
//            statement = connection.prepareStatement(query);
//            statement.setString(1, email);
//            
//            resultSet = statement.executeQuery();
//            
//            if (resultSet.next()) {
//                return extractUserFromResultSet(resultSet);
//            }
//            
//            return null;
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error getting user by email: " + email, e);
//            return null;
//        } finally {
//            closeResources(connection, statement, resultSet);
//        }
//    }
//
//    @Override
//    public User authenticateUser(String username, String password) {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        
//        try {
//            connection = connectionPool.getConnection();
//            
//            queryBuilder.reset()
//                    .table("users u")
//                    .select("u.*", "b.branch_name", "r.name as role_name")
//                    .join("LEFT", "branches b", "u.branch_id = b.id")
//                    .join("LEFT", "roles r", "u.role_id = r.id")
//                    .where("u.username = ? AND u.active = true");
//            
//            String query = queryBuilder.buildSelectQuery();
//            
//            statement = connection.prepareStatement(query);
//            statement.setString(1, username);
//            
//            resultSet = statement.executeQuery();
//            
//            if (resultSet.next()) {
//                String storedPassword = resultSet.getString("password");
//                
//                if (PasswordUtil.verifyPassword(password, storedPassword)) {
//                    User user = extractUserFromResultSet(resultSet);
//                    
//                    // Update last login time
//                    updateLastLogin(user.getId());
//                    
//                    return user;
//                }
//            }
//            
//            return null;
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error authenticating user: " + username, e);
//            return null;
//        } finally {
//            closeResources(connection, statement, resultSet);
//        }
//    }
//
//    @Override
//    public boolean changePassword(int userId, String newPassword) {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        
//        try {
//            connection = connectionPool.getConnection();
//            
//            queryBuilder.reset()
//                    .table("users")
//                    .set("password", PasswordUtil.hashPassword(newPassword))
//                    .where("id = ?");
//            
//            String query = queryBuilder.buildUpdateQuery();
//            
//            statement = connection.prepareStatement(query);
//            statement.setObject(1, PasswordUtil.hashPassword(newPassword));
//            statement.setInt(2, userId);
//            
//            int rowsAffected = statement.executeUpdate();
//            return rowsAffected > 0;
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error changing password for user ID: " + userId, e);
//            return false;
//        } finally {
//            closeResources(connection, statement, null);
//        }
//    }
//
//    @Override
//    public List<User> getUsersByBranch(int branchId) {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        List<User> users = new ArrayList<>();
//        
//        try {
//            connection = connectionPool.getConnection();
//            
//            queryBuilder.reset()
//                    .table("users u")
//                    .select("u.*", "b.branch_name", "r.name as role_name")
//                    .join("LEFT", "branches b", "u.branch_id = b.id")
//                    .join("LEFT", "roles r", "u.role_id = r.id")
//                    .where("u.branch_id = ? AND u.active = true");
//            
//            String query = queryBuilder.buildSelectQuery();
//            
//            statement = connection.prepareStatement(query);
//            statement.setInt(1, branchId);
//            
//            resultSet = statement.executeQuery();
//            
//            while (resultSet.next()) {
//                users.add(extractUserFromResultSet(resultSet));
//            }
//            
//            return users;
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error getting users by branch ID: " + branchId, e);
//            return users;
//        } finally {
//            closeResources(connection, statement, resultSet);
//        }
//    }
//
//    @Override
//    public List<User> getUsersByRole(int roleId) {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        List<User> users = new ArrayList<>();
//        
//        try {
//            connection = connectionPool.getConnection();
//            
//            queryBuilder.reset()
//                    .table("users u")
//                    .select("u.*", "b.branch_name", "r.name as role_name")
//                    .join("LEFT", "branches b", "u.branch_id = b.id")
//                    .join("LEFT", "roles r", "u.role_id = r.id")
//                    .where("u.role_id = ? AND u.active = true");
//            
//            String query = queryBuilder.buildSelectQuery();
//            
//            statement = connection.prepareStatement(query);
//            statement.setInt(1, roleId);
//            
//            resultSet = statement.executeQuery();
//            
//            while (resultSet.next()) {
//                users.add(extractUserFromResultSet(resultSet));
//            }
//            
//            return users;
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error getting users by role ID: " + roleId, e);
//            return users;
//        } finally {
//            closeResources(connection, statement, resultSet);
//        }
//    }
//
//    @Override
//    public boolean updateLastLogin(int userId) {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        
//        try {
//            connection = connectionPool.getConnection();
//            
//            queryBuilder.reset()
//                    .table("users")
//                    .set("last_login", new Timestamp(new Date().getTime()))
//                    .where("id = ?");
//            
//            String query = queryBuilder.buildUpdateQuery();
//            
//            statement = connection.prepareStatement(query);
//            statement.setTimestamp(1, new Timestamp(new Date().getTime()));
//            statement.setInt(2, userId);
//            
//            int rowsAffected = statement.executeUpdate();
//            return rowsAffected > 0;
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error updating last login for user ID: " + userId, e);
//            return false;
//        } finally {
//            closeResources(connection, statement, null);
//        }
//    }
//
//    @Override
//    public boolean deactivateUser(int userId) {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        
//        try {
//            connection = connectionPool.getConnection();
//            
//            queryBuilder.reset()
//                    .table("users")
//                    .set("active", false)
//                    .where("id = ?");
//            
//            String query = queryBuilder.buildUpdateQuery();
//            
//            statement = connection.prepareStatement(query);
//            statement.setBoolean(1, false);
//            statement.setInt(2, userId);
//            
//            int rowsAffected = statement.executeUpdate();
//            return rowsAffected > 0;
//        } catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, "Error deactivating user ID: " + userId, e);
//            return false;
//        } finally {
//            closeResources(connection, statement, null);
//        }
//    }
//
//    /**
//     * Extracts user data from a result set
//     * @param resultSet Result set containing user data
//     * @return User object with data from the result set
//     * @throws SQLException if data access error occurs
//     */
//    private User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
//        User user = new User();
//        user.setId(resultSet.getInt("id"));
//        user.setUsername(resultSet.getString("username"));
//        user.setPassword(resultSet.getString("password"));
//        user.setEmail(resultSet.getString("email"));
//        user.setPhone(resultSet.getString("phone"));
//        user.setGender(resultSet.getString("gender"));
//        user.setDob(resultSet.getDate("dob"));
//        user.setAddress(resultSet.getString("address"));
//        user.setMaritalStatus(resultSet.getString("marital_status"));
//        user.setAadharNo(resultSet.getString("aadhar_no"));
//        user.setPanNo(resultSet.getString("pan_no"));
//        user.setBranchId(resultSet.getInt("branch_id"));
//        user.setBranchName(resultSet.getString("branch_name"));
//        user.setOccupation(resultSet.getString("occupation"));
//        user.setAnnualIncome(resultSet.getDouble("annual_income"));
//        user.setRoleId(resultSet.getInt("role_id"));
//        user.setRoleName(resultSet.getString("role_name"));
//        user.setCreatedDate(resultSet.getTimestamp("created_date"));
//        
//        Timestamp lastLogin = resultSet.getTimestamp("last_login");
//        if (lastLogin != null) {
//            user.setLastLogin(new Date(lastLogin.getTime()));
//        }
//        
//        user.setActive(resultSet.getBoolean("active"));
//        return user;
//    }
//
//    /**
//     * Closes database resources safely
//     * @param connection Connection to close
//     * @param statement Statement to close
//     * @param resultSet Result set to close
//     */
//    private void closeResources(Connection connection, PreparedStatement statement, ResultSet resultSet) {
//        try {
//            if (resultSet != null) {
//                resultSet.close();
//            }
//            if (statement != null) {
//                statement.close();
//            }
//            if (connection != null) {
//                connectionPool.releaseConnection(connection);
//            }
//        } catch (SQLException e) {
//            LOGGER.log(Level.WARNING, "Error closing database resources", e);
//        }
//    }
//}
