package com.zoho.regex.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTask {
	public Pattern getPattern(String regex) {
		Pattern pattern = Pattern.compile(regex);
		return pattern;
	}
	public Pattern getPattern(String regex,int flag) {
		Pattern pattern = Pattern.compile(regex,flag);
		return pattern;
	}
	
	public boolean validateMobile(String mobile) {
		String regex ="^[789][0-9]{9}$";
		Pattern pattern = getPattern(regex);
		Matcher matcher = pattern.matcher(mobile);
		return matcher.find();	
	}
	public boolean validateAlphaNum(String alphaNum) {
		String regex ="^[a-zA-Z0-9]+$";
		Pattern pattern = getPattern(regex);
		Matcher matcher = pattern.matcher(alphaNum);
		return matcher.find();
	}
	public boolean validateStarts(String given,String check) {
		String regex = "^"+"check"+".*$";
		Pattern pattern = getPattern(regex);
		Matcher matcher = pattern.matcher(given);
		return matcher.find();	

	}
	public boolean validateEnds(String given,String check) {
		String regex = "^.*"+"check"+"$";
		Pattern pattern = getPattern(regex);
		Matcher matcher = pattern.matcher(given);
		return matcher.find();	

	}
	public boolean validateContains(String given,String check) {
		String regex = "^.*"+"check"+".*$";
		Pattern pattern = getPattern(regex);
		Matcher matcher = pattern.matcher(given);
		return matcher.find();	

	}
	public boolean validateExactString(String given,String check) {
		String regex = "^"+"check"+"$";
		Pattern pattern = getPattern(regex);
		Matcher matcher = pattern.matcher(given);
		return matcher.find();	

	}
	public boolean validEmail(String email) {
		String regex ="^[a-zA-Z0-9]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		Pattern pattern = getPattern(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.find();
		
	}
	
	

}
