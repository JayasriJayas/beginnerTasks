package com.zoho.regex.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import com.zoho.regex.task.RegexTask;

public class Runner {
	static final Logger logger = Logger.getLogger(Runner.class.getName());
	public static void main(String[] args) {
		int choice,noOfStrings;
		RegexTask task = new RegexTask();
		List<String> strList = new ArrayList<>();
		String val;
		Scanner sc = new Scanner(System.in);
		try {
		do {
			System.out.println("1.Validate mobile no");
			System.out.println("2.Validate input string");
			System.out.println("3.Check string starts with,ends with,contains and exact match");
			System.out.println("4.Match string irrespective of case");
			System.out.println("5.Email validation");
			System.out.println("6.Check length of the string");
			System.out.println("7.Matching strings in the lists");
			System.out.println("8.Extracting Html Tags");
			
			System.out.println("Enter the choice(enter -1 to exit)");
			choice = sc.nextInt();
			sc.nextLine();
			switch(choice) {
			case 1:
				System.out.println("Enter the mobile to validate");
				String mobileNo = sc.next();
				logger.info("Is it valid mobile no:"+task.validateMobile(mobileNo));
				break;
				
			case 2:
				System.out.println("Enter a string to validate");
				String alphanum = sc.next();
				logger.info("Is it valid alphanumeric"+task.validateAlphaNum(alphanum));
				break;
				
				
			case 3:
				System.out.println("Enter a string to validate");
				String input = sc.nextLine();
				System.out.println("Enter a string to be started with");
				String start = sc.nextLine();
				logger.info("Is it valid string that starts with "+start+" "+task.validateStarts(input,start));
				System.out.println("Enter a string to validate");
				String inputContains = sc.nextLine();
				System.out.println("Enter a string that contains in string");
				String contains = sc.nextLine();
				logger.info("Is it valid string that contains"+contains+" "+task.validateContains(inputContains, contains));
				System.out.println("Enter a string to validate");
				String inputEnd = sc.nextLine();
				System.out.println("Enter a string to end with");
				String end = sc.nextLine();
				logger.info("Is it valid string that ends with"+end+" "+task.validateEnds(inputEnd, end));
				System.out.println("Enter a string to validate");
				String inputExact = sc.nextLine();
				System.out.println("Enter a string ");
				String exact = sc.nextLine(); 
				logger.info("Is it valid exact string"+task.validateExactString(inputExact, exact));
				break;
			case 4:
				System.out.println("Enter no of Strings in the list");
				noOfStrings = sc.nextInt();
				for(int i=0;i<noOfStrings;i++){
					val = sc.next();		
					strList.add(val);
				}
				System.out.println("Enter a string to validate");
				String caseOne = sc.next();
				logger.info("Is a valid list of strings" + task.validateCaseInSensitive(strList, caseOne));
				break;
				
				
			case 5:
				System.out.println("Enter email");
				String email = sc.next();
				logger.info("Is a valid email "+ task.validEmail(email));
				break;
			case 6:
				System.out.println("Enter no of Strings in the list");
				noOfStrings = sc.nextInt();
				for(int i=0;i<noOfStrings;i++){
					val = sc.next();		
					strList.add(val);
				}
				 System.out.println("Valid Strings: " + task.validateStrings(strList));
				 break;
			case 7:
				System.out.println("Enter no of Strings in the list");
				noOfStrings = sc.nextInt();
				List<String> list1 = new ArrayList<>();
				for(int i=0;i<noOfStrings;i++){
				 	val = sc.next();		
				 	list1.add(val);
				}
				System.out.println("Enter no of Strings in the list");
			    noOfStrings = sc.nextInt();
			    List<String> list2= new ArrayList<>();
			    for(int j=0;j<noOfStrings;j++){
			     	val = sc.next();		
					list2.add(val);
			    }
				Map<String, List<Integer>> resultt = task.getMatchingIndexes(list1, list2);
		        System.out.println("Matching Map: " + resultt);
		        break;
			case 8:
				
			     System.out.println("Enter the string:");
		            String html = sc.nextLine(); 
		            sc.nextLine();
		            List<String> result = task.extractHtmlTags(html);
		            if (result.isEmpty()) {
		                System.out.println("No tags found.");
		            } else {
		                for (String item : result) {
		                    System.out.println(item);
		                }
		            }

				break;
			}
			
		}while(choice!=-1);
	}
		catch(Exception e) {
			 
		}
}
}
