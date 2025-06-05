package com.bank.mapper;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.bank.models.Customer;
import com.bank.models.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CustomerMapper {

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create();

    public static Customer fromRequest(Request req, long userId) {
       
        String json = gson.toJson(req);
        Customer customer = gson.fromJson(json, Customer.class);
        customer.setUserId(userId); 
        return customer;
        
    }
   	
    public static Customer fromResultSet(List<Map<String, Object>> rows) {
    	  if (rows == null || rows.isEmpty()) return null;

    	        Map<String, Object> row = rows.get(0);
    	        Customer customer = new Customer();

    	        if (row.get("userId") != null) {
    	            customer.setUserId(((Number) row.get("userId")).longValue());
    	        }

    	        if (row.get("aadharNo") != null) {
    	            customer.setAadharNo(((Number) row.get("aadharNo")).longValue());
    	        }

    	        if (row.get("panNo") != null) {
    	            customer.setPanNo((String) row.get("panNo"));
    	        }

    	        if (row.get("address") != null) {
    	            customer.setAddress((String) row.get("address"));
    	        }

    	        if (row.get("dob") != null) {
    	            customer.setDob((Date) row.get("dob"));
    	        }

    	        if (row.get("maritalStatus") != null) {
    	            customer.setMaritalStatus((String) row.get("maritalStatus"));
    	        }

    	        if (row.get("occupation") != null) {
    	            customer.setOccupation((String) row.get("occupation"));
    	        }

    	        if (row.get("annualIncome") != null) {
    	            customer.setAnnualIncome(((Number) row.get("annualIncome")).doubleValue());
    	        }

    	        return customer;
    	    }
    	
    }

