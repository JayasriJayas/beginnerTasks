package com.runner;

import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

import java.util.ArrayList;
import java.util.List;

import com.dialect.MySQLDialect;

public class Runner {
	public static void main(String[] args) throws QueryException {
		QueryBuilder subQuery = new QueryBuilder(new MySQLDialect())
				   .select()
				   .aggregate("AVG", "marks")
				    .from("Student");

		QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		String query = qb
				  .select("*")
				    .from("Student")
				    .whereSubQuery("marks >", subQuery)
				    .build();





		System.out.println("Generated Query: " + query);
		QueryExecutor.execute(query);
		


    }

}

