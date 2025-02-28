package com.customer.util;

import java.util.regex.Pattern;

public class Util {
	private static final String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    private static final String phoneRegex= "^[0-9]{10}$";
    private static final String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
     public static boolean validateInputs(String firstname, String surname, String username, String password, String dob, String gender, String address, String email, String phone) {
    	
        return firstname != null && !firstname.isEmpty() &&
               surname != null && !surname.isEmpty() &&
               username != null && !username.isEmpty() &&
               password != null && !password.isEmpty() &&
               dob != null && !dob.isEmpty() &&
               gender != null && !gender.isEmpty() &&
               address != null && !address.isEmpty() &&
               email != null &&
               phone != null;
    }
    public static boolean checkPassword(String password ,String confirmPassword ) {
    	return password.equals(confirmPassword);
    }
    public static  boolean checkStrongPassword(String password) {
    	return Pattern.matches(passwordRegex,password);
    	
    }
    public static boolean checkMail(String email) {
    	return Pattern.matches(emailRegex, email);
    	
    }
    public static boolean checkPhoneNo(String phone) {
    	return Pattern.matches(phoneRegex, phone);
    	
    }

}
