package com.zoho.jdbc.task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zoho.jdbc.dependent.DependentDetails;
import com.zoho.jdbc.employee.Employee;
import com.zoho.jdbc.exception.DatabaseException;

public class JdbcConnection  { 
    private static final String jdbcURL = "jdbc:mysql://localhost:3306/incubationDB";
    private static final String jdbcUsername = "root";
    private static final String jdbcPassword = "root";
    private Connection getConnection() throws DatabaseException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseException("Error while establishing database connection", e);
        }
       
    }

    public void createTable() throws DatabaseException{
        String query = "CREATE TABLE IF NOT EXISTS Employee (EMPLOYEE_ID INT PRIMARY KEY,"
        		+ "NAME VARCHAR(50) NOT NULL,"
        		+ "MOBILE VARCHAR(15)NOT NULL UNIQUE,"
        		+ "EMAIL VARCHAR(50)NOT NULL UNIQUE,"
        		+ "DEPARTMENT VARCHAR(30) NOT NULL);";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
               stmt.executeUpdate(query);
           } catch (SQLException e) {
               throw new DatabaseException("Error while creating table", e);
           }
    }

    public void insertUser(List<Employee> employee) throws DatabaseException{
        String query = "INSERT INTO Employee (EMPLOYEE_ID, NAME,MOBILE, EMAIL, DEPARTMENT) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = getConnection();
        	PreparedStatement stmt = conn.prepareStatement(query)) {
        	for(Employee emp: employee ) {
        		stmt.setInt(1, emp.getEmpId());
        		stmt.setString(2, emp.getEmpName());
        		stmt.setString(3, emp.getMobile());
        		stmt.setString(4, emp.getEmail());
        		stmt.setString(5, emp.getDepartment());
        		stmt.addBatch();
        	}
            stmt.executeBatch();
         
        }
        catch (SQLException e) {
            throw new DatabaseException("Error while inserting data into table", e);
        }
    }
    
	public JSONArray getByName(String name) throws DatabaseException {
		JSONArray jsonArray = new JSONArray();
		String query = "SELECT * FROM Employee WHERE NAME LIKE ?;";
		try(Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(query))
		{
			stmt.setString(1, name + "%");
				
			try(ResultSet rs = stmt.executeQuery()){
		

		
            while (rs.next()) {
            	JSONObject jsonObj = new JSONObject();
				jsonObj.put("EMPLOYEE_ID", rs.getInt("EMPLOYEE_ID"));
				jsonObj.put("NAME", rs.getString("NAME"));
				jsonObj.put("MOBILE", rs.getString("MOBILE"));
				jsonObj.put("EMAIL", rs.getString("EMAIL"));
				jsonObj.put("DEPARTMENT", rs.getString("DEPARTMENT"));
                jsonArray.put(jsonObj);
              
            }
        }
}
		catch (SQLException e) {
            throw new DatabaseException("Error: Cannot retrive data", e);
        }
		return jsonArray;
	}
	
	public void updateDetails(String update,String value,int id)throws DatabaseException {
		String query = "UPDATE Employee SET "+ update + "=? WHERE EMPLOYEE_ID=?;";
		try(Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setString(1,value);
			stmt.setInt(2,id);
			stmt.executeUpdate();

        }
		catch (SQLException e) {
			throw new DatabaseException("Error while updating data", e);
        }
	}
	
	public JSONArray getRows(int rows)throws DatabaseException {
		JSONArray jsonArray = new JSONArray();
		Employee employee;
		String query = "SELECT * FROM Employee LIMIT ?;";
		try(Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setInt(1,rows);	
			try(ResultSet rs = stmt.executeQuery()){
			
			
			
			while(rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("EMPLOYEE_ID", rs.getInt("EMPLOYEE_ID"));
				jsonObj.put("NAME", rs.getString("NAME"));
				jsonObj.put("MOBILE", rs.getString("MOBILE"));
				jsonObj.put("EMAIL", rs.getString("EMAIL"));
				jsonObj.put("DEPARTMENT", rs.getString("DEPARTMENT"));
                jsonArray.put(jsonObj);
	               
	
			}
			}
		}
		catch (SQLException e) {
			throw new DatabaseException("Error while retriving rows", e);
        }
		return jsonArray;
		
	} 

	public JSONArray orderBy(int number,String column)throws DatabaseException{
		JSONArray jsonArray = new JSONArray();
		String query = "SELECT * FROM Employee ORDER BY " + column + " DESC LIMIT ?";
		try(Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setInt(1,number);
			try(ResultSet rs = stmt.executeQuery()){
		
		   
			while(rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("EMPLOYEE_ID", rs.getInt("EMPLOYEE_ID"));
				jsonObj.put("NAME", rs.getString("NAME"));
				jsonObj.put("MOBILE", rs.getString("MOBILE"));
				jsonObj.put("EMAIL", rs.getString("EMAIL"));
				jsonObj.put("DEPARTMENT", rs.getString("DEPARTMENT"));
                jsonArray.put(jsonObj);
		}
		}
		}
		catch (SQLException e) {
			throw new DatabaseException("Error: Query is not affected", e);
        }
		
		return jsonArray;
			
		
	}
		public void deleteID(int id)throws DatabaseException {
			String query = "DELETE FROM Employee WHERE EMPLOYEE_ID = ?;";
			try(Connection conn = getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)){
				stmt.setInt(1,id);
				stmt.executeUpdate();
			}
			catch (SQLException e) {
				throw new DatabaseException("Error while deleting the record", e);	           }
		}
		
		public void createDependentTable()throws DatabaseException{
			String query ="CREATE TABLE IF NOT EXISTS Dependent(DEPENDENT_ID INT AUTO_INCREMENT PRIMARY KEY,"
					+ "EMPLOYEE_ID INT,"
					+ "NAME VARCHAR(30) NOT NULL,"
					+ "AGE INT,RELATIONSHIP VARCHAR(15),"
					+ " FOREIGN KEY (EMPLOYEE_ID) REFERENCES Employee (EMPLOYEE_ID));";
			try (Connection conn = getConnection();
				Statement stmt = conn.createStatement()) {
	            stmt.executeUpdate(query);
	        }
			catch (SQLException e) {
				throw new DatabaseException("Error: Query is not affected", e);
	           }
			
		}
		
		public void insertDependent(List<DependentDetails> dependent)throws DatabaseException{
			String query = "INSERT INTO Dependent(EMPLOYEE_ID,NAME,AGE,RELATIONSHIP) VALUES(?,?,?,?);";
			 try (Connection conn = getConnection();
				 PreparedStatement stmt = conn.prepareStatement(query)) {
				 for(DependentDetails dep : dependent) {
				 stmt.setInt(1,dep.getDependentId());
				 stmt.setString(2, dep.getDependentName());
				 stmt.setInt(3, dep.getAge());
				 stmt.setString(4,dep.getRelationship());
				 stmt.addBatch();
				 }
				 stmt.executeBatch();
				 
			 
			 }catch (SQLException e) {
				 throw new DatabaseException("Error while inserting data into table", e);
	           }
		
		}
		
		public JSONArray getDependentDetails(int id) throws DatabaseException {
	        String query = "SELECT Employee.EMPLOYEE_ID, Employee.NAME, Dependent.DEPENDENT_ID, "
	                + "Dependent.NAME AS Dependent_Name, Dependent.AGE, Dependent.RELATIONSHIP "
	                + "FROM Employee INNER JOIN Dependent ON Employee.EMPLOYEE_ID = Dependent.EMPLOYEE_ID "
	                + "WHERE Employee.EMPLOYEE_ID = ?;";
	        
	        JSONArray jsonArray = new JSONArray();
	        
	        try (Connection conn = getConnection();
	             PreparedStatement stmt = conn.prepareStatement(query)) {
	            
	            stmt.setInt(1, id);
	            
	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    JSONObject jsonObj = new JSONObject();
	                    jsonObj.put("EMPLOYEE_ID", rs.getInt("EMPLOYEE_ID"));
	                    jsonObj.put("EMPLOYEE_NAME", rs.getString("NAME"));
	                    jsonObj.put("DEPENDENT_ID", rs.getInt("DEPENDENT_ID"));
	                    jsonObj.put("DEPENDENT_NAME", rs.getString("Dependent_Name"));
	                    jsonObj.put("AGE", rs.getInt("AGE"));
	                    jsonObj.put("RELATIONSHIP", rs.getString("RELATIONSHIP"));
	                    jsonArray.put(jsonObj);
	                }
	            }
	        } catch (SQLException e) {
	            throw new DatabaseException("Error while retrieving rows", e);
	        }
	        
	        return jsonArray; 
	    }

	    public JSONArray getAllDependent(int limit) throws DatabaseException {
	        String query = "SELECT Employee.EMPLOYEE_ID, Employee.NAME, Dependent.DEPENDENT_ID, "
	                + "Dependent.NAME AS Dependent_Name, Dependent.AGE, Dependent.RELATIONSHIP "
	                + "FROM Employee INNER JOIN Dependent ON Employee.EMPLOYEE_ID = Dependent.EMPLOYEE_ID "
	                + "ORDER BY Employee.NAME ASC LIMIT ?;";
	        
	        JSONArray jsonArray = new JSONArray();
	        
	        try (Connection conn = getConnection();
	             PreparedStatement stmt = conn.prepareStatement(query)) {
	            
	            stmt.setInt(1, limit);
	            
	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    JSONObject jsonObj = new JSONObject();
	                    jsonObj.put("EMPLOYEE_ID", rs.getInt("EMPLOYEE_ID"));
	                    jsonObj.put("EMPLOYEE_NAME", rs.getString("NAME"));
	                    jsonObj.put("DEPENDENT_ID", rs.getInt("DEPENDENT_ID"));
	                    jsonObj.put("DEPENDENT_NAME", rs.getString("Dependent_Name"));
	                    jsonObj.put("AGE", rs.getInt("AGE"));
	                    jsonObj.put("RELATIONSHIP", rs.getString("RELATIONSHIP"));
	                    jsonArray.put(jsonObj);
	                }
	            }
	        } catch (SQLException e) {
	            throw new DatabaseException("Error while retrieving rows", e);
	        }
	        
	        return jsonArray; 
	    }
	}







