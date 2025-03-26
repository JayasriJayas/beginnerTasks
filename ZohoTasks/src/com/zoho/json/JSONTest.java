package com.zoho.json;

import org.json.JSONObject;

public class JSONTest {
    public static void main(String[] args) {
        // Creating a JSON object
        JSONObject obj = new JSONObject();
        obj.put("name", "Jayasri");
        obj.put("age", 23);
        obj.put("city", "Chennai");

        // Print JSON output
        System.out.println("Generated JSON: " + obj.toString(4)); // Pretty print with indentation
    }
}
