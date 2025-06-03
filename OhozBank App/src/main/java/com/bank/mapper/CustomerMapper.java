package com.bank.mapper;

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
        //need to change from gson
    }
}
