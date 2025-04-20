package com.runner;

import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import java.util.ArrayList;
import java.util.List;

import com.dialect.MySQLDialect;

public class Runner {
	public static void main(String[] args) {
		QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		String query =qb.select("d.dept_name, e.name AS employee")
			    .from("employees e")
			    .rightJoin("departments d", "e.dept_id = d.id")
			    .build();



		System.out.println("Generated Query: " + query);
		QueryExecutor.execute(query);


    }

}

