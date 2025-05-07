package com.bank.util;

import com.bank.models.Request;

public class RequestValidator {

    public static boolean isNullOrEmpty(Object value) {
        return value == null;
    }

    public static String validateSignupFields(Request request) {
        if (isNullOrEmpty(request.getUsername())) return "Username is required";
        if (isNullOrEmpty(request.getPassword())) return "Password is required";
        if (isNullOrEmpty(request.getEmail())) return "Email is required";
        if (isNullOrEmpty(request.getPhone())) return "Phone number is required";
        if (isNullOrEmpty(request.getGender())) return "Gender is required";
        if (request.getDob() == null) return "Date of Birth is required";
        if (isNullOrEmpty(request.getAddress())) return "Address is required";
        if (isNullOrEmpty(request.getMaritalStatus())) return "Marital status is required";
        if (isNullOrEmpty(request.getAadharNo())) return "Aadhar number is required";
        if (isNullOrEmpty(request.getPanNo())) return "PAN number is required";
        if (request.getBranchId() <= 0) return "Branch ID must be greater than zero";
        if (isNullOrEmpty(request.getBranchName())) return "Branch name is required";
        if (isNullOrEmpty(request.getOccupation())) return "Occupation is required";
        if (request.getAnnualIncome() <= 0) return "Annual income must be greater than zero";
        
        return null; 
    }
}
