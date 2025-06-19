package com.bank.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bank.models.Branch;
import com.bank.models.Request;
import com.bank.models.User;

public class RequestValidator {
	 private static final Set<String> customerEditableFields = new HashSet<>(Arrays.asList(
		        "address", "dob", "maritalStatus", "occupation", "annualIncome","name", "email", "phone", "gender"
		    ));

		    private static final Set<String> adminEditableFields = new HashSet<>(Arrays.asList(
		         "name", "email", "phone", "gender", "branchId"
		    ));
	
    public static boolean isNullOrEmpty(Object value) {
        return value == null;
    }

    public static String validateSignupFields(Request request) {
        if (isNullOrEmpty(request.getUsername())) return "Username is required";
        if (isNullOrEmpty(request.getPassword())) return "Password is required";
        if (isNullOrEmpty(request.getName())) return "Name is required";
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

        if (!FormatValidator.isValidEmail(request.getUsername())) return "Username must be your email or Ivalid email format";
        if (!FormatValidator.isValidEmail(request.getEmail())) return "Invalid email format";
        if (!FormatValidator.isValidPhone(String.valueOf(request.getPhone()))) return "Invalid phone number format";
        if (!FormatValidator.isValidPassword(request.getPassword())) return "Password does not meet strength requirements";
        if (!FormatValidator.isValidAadhar(String.valueOf(request.getAadharNo()))) return "Invalid Aadhar number format";
        
        return null; 
    }
    public static String validateFields(User user) {
		  if (isNullOrEmpty(user.getUsername())) return "Username is required";
	        if (isNullOrEmpty(user.getPassword())) return "Password is required";
	        if (isNullOrEmpty(user.getName())) return "Name is required";
	        if (isNullOrEmpty(user.getEmail())) return "Email is required";
	        if (isNullOrEmpty(user.getPhone())) return "Phone number is required";
	        if (isNullOrEmpty(user.getGender())) return "Gender is required";
	        if (user.getBranchId() <= 0 ) return "Branch ID must be greater than zero";
	        if (isNullOrEmpty(user.getBranchId())) return "Branch id cannot be null";
	        
	        if (!FormatValidator.isValidEmail(user.getUsername())) return "Username must be your email or Ivalid email format";
	        if (!FormatValidator.isValidEmail(user.getEmail())) return "Invalid email format";
	        if (!FormatValidator.isValidPhone(String.valueOf(user.getPhone()))) return "Invalid phone number format";
	        if (!FormatValidator.isValidPassword(user.getPassword())) return "Password does not meet strength requirements";
	        
	        return null;      
	        
	  }
    public static String validateBranchFields(Branch branch) {
        if (branch == null) return "Branch object is null";
        if (isNullOrEmpty(branch.getBranchName())) return "Branch name is required";
        if (isNullOrEmpty(branch.getIfscCode())) return "IFSC code is required";
        if (isNullOrEmpty(branch.getLocation())) return "Branch location is required";
        if (branch.getContact() <= 0) return "Contact number must be valid";

        if (!FormatValidator.isValidPhone(String.valueOf(branch.getContact()))) return "Invalid contact number format";
        if (!FormatValidator.isValidIFSC(branch.getIfscCode())) return "Invalid IFSC code format";

        return null;
    }
    public static String validateEditableFields(Map<String, Object> payload, boolean isAdmin) {
        Set<String> allowedFields = isAdmin ? adminEditableFields : customerEditableFields;

        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            
            if (!allowedFields.contains(key)) {
                return key + " is not editable";
            }

            
            if (value == null || String.valueOf(value).trim().isEmpty()) {
                return key + " cannot be null or empty";
            }

            
            switch (key) {
                
                case "email":
                    if (!FormatValidator.isValidEmail(String.valueOf(value))) {
                        return key + " is not a valid email";
                    }
                    break;

                case "phone":
                    if (!FormatValidator.isValidPhone(String.valueOf(value))) {
                        return "Invalid phone number format";
                    }
                    break;

                case "annualIncome":
                    try {
                        double income = Double.parseDouble(String.valueOf(value));
                        if (income <= 0) {
                            return "Annual income must be greater than zero";
                        }
                    } catch (NumberFormatException e) {
                        return "Annual income must be a valid number";
                    }
                    break;

                case "branchId":
                    try {
                        long branchId = Long.parseLong(String.valueOf(value));
                        if (branchId <= 0) {
                            return "Branch ID must be greater than zero";
                        }
                    } catch (NumberFormatException e) {
                        return "Branch ID must be a valid number";
                    }
                    break;

                case "dob":
                    if (value instanceof String) {
                        if (!FormatValidator.isValidDate((String) value)) {
                            return "Invalid date format for DOB (expected yyyy-MM-dd)";
                        }
                    }
                    break;
            }
        }

        return null; 
    }

    
}
    
    

