package com.customer.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import com.customer.model.User;


public class DataBaseConnection {
    private static final String jdbcURL = "jdbc:mysql://localhost:3306/application";
    private static final String jdbcUsername= "root";
    private static final String jdbcPassword = "root";
    private Connection conn;

    public DataBaseConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    public void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users (firstname, surname, username, password, dob, gender, address, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        	 stmt.setString(1, user.getFirstname());
             stmt.setString(2, user.getSurname());
             stmt.setString(3, user.getUsername());
             stmt.setString(4, user.getPassword());
             stmt.setString(5, user.getDob());
             stmt.setString(6, user.getGender());
             stmt.setString(7, user.getAddress());
             stmt.setString(8, user.getEmail());
             stmt.setString(9, user.getPhone());
             stmt.executeUpdate();
        }
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET firstname=?, surname=?, username=?, password=?,dob=?, gender=?, address=?, email=?, phone=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        	 stmt.setString(1, user.getFirstname());
             stmt.setString(2, user.getSurname());
             stmt.setString(3, user.getUsername());
             stmt.setString(4, user.getPassword());
             stmt.setString(5, user.getDob());
             stmt.setString(6, user.getGender());
             stmt.setString(7, user.getAddress());
             stmt.setString(8, user.getEmail());
             stmt.setString(9, user.getPhone());
             stmt.setInt(10, user.getId());
             stmt.executeUpdate();
        }
    }

    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            	return new User(
                        rs.getInt("id"),
                        rs.getString("firstname"),
                        rs.getString("surname"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("email"),
                        rs.getString("phone")
                    );
            }
        }
        return null;
    }
    public List<User> listUsers()throws SQLException{
    	List<User> userList = new ArrayList<>();
    	String sql = "SELECT * FROM users";
    	try(PreparedStatement stmt = conn.prepareStatement(sql)){
    		ResultSet rs = stmt.executeQuery();
    		while(rs.next()) {
    			userList.add(new User(rs.getInt("id"),rs.getString("firstname"),rs.getString("surname"),rs.getString("username"),rs.getString("password"),rs.getString("dob"),rs.getString("gender"),rs.getString("address"),rs.getString("email"),rs.getString("phone")));
    		}
    	}
    	 catch (SQLException e) {
             e.printStackTrace();
         }
    	return userList;
    	
    }





    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
