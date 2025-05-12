package com.runner;

import com.querybuilder.DBConnector;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dialect.MySQLDialect;

public class Runner {
	public static void main(String[] args) throws QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().from("Student")
        .where("grade = ?");
        
       

        String query = qb.build(); 

        System.out.println("Generated Query: " + query);

        QueryExecutor qe = new QueryExecutor(DBConnector.getConnection());
        
        
        List<Object> selectParams = Arrays.asList("A");
        System.out.println("Running SELECT with parameters...");
        List<Map<String, Object>> gradeAPlusStudents = qe.executeQuery(query, selectParams);
        
       

	       
	        
	       
	       

    }

}

