package com.bank.util;

import java.util.regex.Pattern;

import com.bank.models.Request;

public class RequestValidator {
	
	private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\d{10}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");

    private static final Pattern AADHAR_PATTERN =
            Pattern.compile("^\\d{12}$");

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
        if (isNullOrEmpty(request.getOccupation())) return "Occupation is required";
        if (request.getAnnualIncome() <= 0) return "Annual income must be greater than zero";

        if (!isValidEmail(request.getEmail())) return "Invalid email format";
        if (!isValidPhone(String.valueOf(request.getPhone()))) return "Invalid phone number format";
        if (!isValidPassword(request.getPassword())) return "Password does not meet strength requirements";
        if (!isValidAadhar(String.valueOf(request.getAadharNo()))) return "Invalid Aadhar number format";
        
        return null; 
    }
    
    public static boolean isValidEmail(String email) {
    	return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPassword(String password) {
    	return PASSWORD_PATTERN.matcher(password).matches();
    }
    public static boolean isValidPhone(String phone) {
    	return PHONE_PATTERN.matcher(phone).matches();
    }
    public static boolean isValidAadhar(String aadhar) {
    	return AADHAR_PATTERN.matcher(aadhar).matches();
    }
}
