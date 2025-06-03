package com.runner;

import com.querybuilder.DBConnector;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dialect.MySQLDialect;

public class Runner {
	public static void main(String[] args) throws QueryException, SQLException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*")
        .from("Student")
        .whereBetween("marks",80,90).andBetween("age",22,23);

       

        String query = qb.build(); 

        System.out.println("Generated Query: " + query);

        QueryExecutor qe = new QueryExecutor(DBConnector.getConnection());
        
        List<Object> params = qb.getParameters();       
        System.out.println("Running SELECT with parameters...");
        List<Map<String, Object>> results = qe.executeQuery(query, params);

        // ✅ Print results clearly
        if (results.isEmpty()) {
            System.out.println("No records found.");
        } else {
            System.out.println("Results:");
            for (Map<String, Object> row : results) {
                System.out.println("---------------");
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    System.out.printf("%-15s: %s%n", entry.getKey(), entry.getValue());
                }
            }
        }
      

    }

}

