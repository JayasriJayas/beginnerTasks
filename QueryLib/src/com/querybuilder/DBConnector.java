package com.querybuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import exception.QueryException;

public class DBConnector {
	private static final String URL = "jdbc:mysql://localhost:3306/incubationDB";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	public static Connection getConnection() throws QueryException {
	    try {
	        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
	    } catch (SQLException e) {
	        throw new QueryException("Failed to connect to the database");
	    }
	}

}

