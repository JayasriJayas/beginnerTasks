package com.zoho.jdbc.task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.zoho.jdbc.dependent.DependentDetails;
import com.zoho.jdbc.employee.Employee;

public class JdbcConnection {
    private static final String jdbcURL = "jdbc:mysql://localhost:3306/incubationDB";
    private static final String jdbcUsername = "root";
    private static final String jdbcPassword = "root";
    private Connection conn;

    public JdbcConnection() throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);  
    }

    public void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS Employee (EMPLOYEE_ID INT PRIMARY KEY,NAME VARCHAR(50) NOT NULL,MOBILE VARCHAR(15)NOT NULL UNIQUE,EMAIL VARCHAR(50)NOT NULL UNIQUE,DEPARTMENT VARCHAR(30) NOT NULL);";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        }
    }

    public void insertuser(Employee employee) throws SQLException {
        String query = "INSERT INTO Employee (EMPLOYEE_ID, NAME, MOBILE, EMAIL, DEPARTMENT) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, employee.getEmpId());
            stmt.setString(2, employee.getEmpName());
            stmt.setString(3, employee.getMobile());
            stmt.setString(4, employee.getEmail());
            stmt.setString(5, employee.getDepartment());
            stmt.executeUpdate();
         
        }
    }


	public Employee getByName(String name) throws SQLException {
		String query = "SELECT * FROM Employee WHERE NAME = ?;";
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            	return new Employee(
                        rs.getInt("EMPLOYEE_ID"),
                        rs.getString("NAME"),
                        rs.getString("MOBILE"),
                        rs.getString("EMAIL"),
                        rs.getString("DEPARTMENT")
                     
                    );
            }
        }
		return null;
	}
	public void updateDetails(String value,int id)throws SQLException {
		String query = "UPDATE Employee SET MOBILE=? WHERE EMPLOYEE_ID=?;";
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setString(1,value);
			stmt.setInt(2,id);
			stmt.executeUpdate();

        }
	}
	public List<Employee> getRows(int rows)throws SQLException {
		List<Employee> list = new ArrayList<>();
		String query = "SELECT * FROM Employee LIMIT ?;";
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setInt(1,rows);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				list.add( new Employee(
                        rs.getInt("EMPLOYEE_ID"),
                        rs.getString("NAME"),
                        rs.getString("MOBILE"),
                        rs.getString("EMAIL"),
                        rs.getString("DEPARTMENT")
                     
                    ));
				
			}
		}
		return list;
		
	}

	public List<Employee> orderBy(int number,String column)throws SQLException{
		List<Employee> list = new ArrayList<>();
		String query = "SELECT * FROM Employee ORDER BY " + column + " DESC LIMIT ?";
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setInt(1,number);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				list.add( new Employee(
                        rs.getInt("EMPLOYEE_ID"),
                        rs.getString("NAME"),
                        rs.getString("MOBILE"),
                        rs.getString("EMAIL"),
                        rs.getString("DEPARTMENT")
                     
                    ));
				
			}
		}
		return list;
			
		
	}
		public void deleteID(int id)throws SQLException {
			String query = "DELETE FROM Employee WHERE EMPLOYEE_ID = ?;";
			try(PreparedStatement stmt = conn.prepareStatement(query)){
				stmt.setInt(1,id);
				stmt.executeUpdate();
				
			}
		}
		public void createDependentTable()throws SQLException{
			String query ="CREATE TABLE IF NOT EXISTS Dependent(DEPENDENT_ID INT AUTO_INCREMENT PRIMARY KEY,EMPLOYEE_ID INT,NAME VARCHAR(30) NOT NULL,AGE INT,RELATIONSHIP VARCHAR(15), FOREIGN KEY (EMPLOYEE_ID) REFERENCES Employee (EMPLOYEE_ID) ON DELETE CASCADE);";
			//cascade
			try (Statement stmt = conn.createStatement()) {
	            stmt.executeUpdate(query);
	            System.out.println("Dependent table created successfully.");
	        }
			
		}
		public void insertDependent(int empId,String name,int age,String relation)throws SQLException{
			String query = "INSERT INTO Dependent(EMPLOYEE_ID,NAME,AGE,RELATIONSHIP) VALUES(?,?,?,?);";
			 try (PreparedStatement stmt = conn.prepareStatement(query)) {
				 stmt.setInt(1,empId);
				 stmt.setString(2, name);
				 stmt.setInt(3, age);
				 stmt.setString(4, relation);
				 stmt.executeUpdate();
				 
			 
			 }
		
		}
		public List<DependentDetails> getDependentDetails(int id)throws SQLException{
			String query = "SELECT Employee.EMPLOYEE_ID, Employee.NAME,Dependent.DEPENDENT_ID, Dependent.NAME AS Dependent_Name,Dependent.AGE, Dependent.RELATIONSHIP FROM Employee INNER JOIN Dependent ON Employee.EMPLOYEE_ID = Dependent.EMPLOYEE_ID WHERE Employee.EMPLOYEE_ID = ?;";
			List<DependentDetails> dependents = new ArrayList<>(); 
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				 stmt.setInt(1,id);
				 ResultSet rs = stmt.executeQuery();
		            while (rs.next()) {
		            			dependents.add( new DependentDetails(
		            			rs.getInt("EMPLOYEE_ID"),
		                        rs.getString("NAME"),
		                        rs.getInt("DEPENDENT_ID"),
		                        rs.getString("Dependent_Name"),
		                        rs.getInt("AGE"),
		                        rs.getString("RELATIONSHIP")));
		            			
		            }
		          
		}
			  return dependents;
		}
		public List<DependentDetails> getAllDependent(int limit)throws SQLException{
			String query = "SELECT Employee.EMPLOYEE_ID, Employee.NAME,Dependent.DEPENDENT_ID, Dependent.NAME AS Dependent_Name,Dependent.AGE, Dependent.RELATIONSHIP FROM Employee INNER JOIN Dependent ON Employee.EMPLOYEE_ID = Dependent.EMPLOYEE_ID ORDER BY Employee.NAME ASC LIMIT ?;";
			List<DependentDetails> dependents = new ArrayList<>(); 
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				 stmt.setInt(1,limit);
				 ResultSet rs = stmt.executeQuery();
		            while (rs.next()) {
		            			dependents.add( new DependentDetails(
		            			rs.getInt("EMPLOYEE_ID"),
		                        rs.getString("NAME"),
		                        rs.getInt("DEPENDENT_ID"),
		                        rs.getString("Dependent_Name"),
		                        rs.getInt("AGE"),
		                        rs.getString("RELATIONSHIP")));
		            			
		            }
		          
		}
			  return dependents;
		}

}




