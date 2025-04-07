package com.zoho.regex.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
		String regex = "^"+ Pattern.quote(check)+".*$";
		Pattern pattern = getPattern(regex);
		Matcher matcher = pattern.matcher(given);
		return matcher.find();	

	}
	public boolean validateEnds(String given,String check) {
		String regex = "^.*"+ Pattern.quote(check)+"$";
		Pattern pattern = getPattern(regex);
		Matcher matcher = pattern.matcher(given);
		return matcher.find();	

	}
	public boolean validateContains(String given,String check) {
		String regex = "^.*"+ Pattern.quote(check)+".*$";
		Pattern pattern = getPattern(regex);
		Matcher matcher = pattern.matcher(given);
		return matcher.find();	

	}
	public boolean validateExactString(String given,String check) {
		String regex = "^"+ Pattern.quote(check)+"$";
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
	public List<String> validateCaseInSensitive(List<String> stringList,String match) {
		String regex = "^" + Pattern.quote(match) + "$";
		Pattern pattern = getPattern(regex, Pattern.CASE_INSENSITIVE);
		return stringList.stream() 
				.filter(s -> pattern.matcher(s).matches())
				.collect(Collectors.toList());
	}
    public  List<String> validateStrings(List<String> stringList) {
        String regex = "^[a-zA-Z]{1,6}$"; 
        Pattern pattern = getPattern(regex);
        return stringList.stream()
                .filter(s -> pattern.matcher(s).matches())
                .collect(Collectors.toList());
    }
    public  Map<String, List<Integer>> getMatchingIndexes(List<String> list1, List<String> list2) {
        Map<String, List<Integer>> resultMap = new HashMap<>();

        for (String match : list2) {
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < list1.size(); i++) {
                if (list1.get(i).equals(match)) {
                    indexes.add(i);
                }
            }
            if (!indexes.isEmpty()) {
                resultMap.put(match, indexes);
            }
        }
        return resultMap;
    }
    public static List<String> extractHtmlTags(String htmlString) {
    	 String regex = "(</?[a-z]*>)"; 
        List<String> tagList = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(htmlString);

        while (matcher.find()) {
            tagList.add(matcher.group()); 
        }
        
        return tagList; 
    }

	

}
