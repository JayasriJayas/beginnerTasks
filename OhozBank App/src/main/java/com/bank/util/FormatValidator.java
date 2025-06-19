package com.bank.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.regex.Pattern;

public class FormatValidator {
	private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\d{10}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");

    private static final Pattern AADHAR_PATTERN =
            Pattern.compile("^\\d{12}$");
    private static final Pattern IFSC_CODE = 
    		Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");
    
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
    public static boolean isValidIFSC(String ifscCode) {
    	return IFSC_CODE.matcher(ifscCode).matches();
    }
    public static boolean isValidDate(String dateStr) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(false);
            df.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

}
