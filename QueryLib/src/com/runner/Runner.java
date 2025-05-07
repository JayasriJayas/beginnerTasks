package com.runner;

import com.querybuilder.DBConnector;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

import java.util.ArrayList;
import java.util.List;

import com.dialect.MySQLDialect;

public class Runner {
	public static void main(String[] args) throws QueryException {
		Object[] row1 = {11, "John", 22,"A"};
        Object[] row2 = {21, "Jane", 22,"B"};

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.insertInto("Student","STUDENT_ID", "NAME", "AGE","grade")
          .values(row1)
          .values(row2);

        String query = qb.build(); // builds INSERT INTO ... VALUES (?, ?, ?), (?, ?, ?)
        List<Object> params = qb.getParameters(); // should return List<Object> = [1, "John", 85.5, 2, "Jane", 92.0]

        System.out.println("Generated Query: " + query);
        System.out.println("Parameters: " + params);
        System.out.println("---- Parameter Debugging ----");
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param == null) {
                System.out.println("Parameter " + (i + 1) + ": null");
            } else {
                System.out.println("Parameter " + (i + 1) + ": " + param + " (Type: " + param.getClass().getName() + ")");
            }
        }
        System.out.println("------------------------------");


        QueryExecutor qe = new QueryExecutor(DBConnector.getConnection());
        qe.execute(query, params);
		

    }

}

