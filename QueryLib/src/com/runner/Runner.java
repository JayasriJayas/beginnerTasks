package com.runner;

import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

import java.util.ArrayList;
import java.util.List;

import com.dialect.MySQLDialect;

public class Runner {
	public static void main(String[] args) throws QueryException {
		QueryBuilder qb = new QueryBuilder(new MySQLDialect());
		String query = qb.select("*")
				.from("Student")
				.where("age > 21")
				.andWhere("department = 'CSE'")
				.build();
				



		System.out.println("Generated Query: " + query);
		QueryExecutor.execute(query);
		


    }

}

