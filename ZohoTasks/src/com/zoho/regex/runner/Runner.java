package com.zoho.regex.runner;

import java.util.Scanner;
import java.util.logging.Logger;

import com.zoho.regex.task.RegexTask;

public class Runner {
	static final Logger logger = Logger.getLogger(Runner.class.getName());
	public static void main(String[] args) {
		int choice;
		RegexTask task = new RegexTask();
		Scanner sc = new Scanner(System.in);
		try {
		do {
			logger.info("1.");
			logger.info("Enter the choice(enter -1 to exit)");
			choice = sc.nextInt();
			
			switch(choice) {
			case 1:
				System.out.println("Enter the mobile to validate");
				String mobileNo = sc.next();
				logger.info("Is it valid mobile no:"+task.validateMobile(mobileNo));
				
			case 2:
				System.out.println("Enter a string to validate");
				String alphanum = sc.next();
				logger.info("Is it valid ");
				
				}

			
			
		}while(choice!=-1);
	}
		catch(Exception e) {
			
		}
}
}
