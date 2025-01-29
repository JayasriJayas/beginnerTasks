package com.zoho.stringbuilder.runner;
import java.util.Scanner;
import java.util.Arrays;
import com.zoho.stringbuilder.task.StringBuilderTask;
import com.zoho.stringbuilder.exception.OutOfBoundException;
import com.zoho.stringbuilder.exception.InvalidException;

class Runner{
	
	public static void main(String[] args){
		Scanner sc=new Scanner(System.in);
		StringBuilderTask task = new StringBuilderTask();
		int choice;
		try{
		do{
			System.out.println("1.Adding a string and its length");
			System.out.println("2.Adding strings and  final length");
			System.out.println("3.Inserting a string in between and final length");
			System.out.println("4.Delete a string and final length");
			System.out.println("5.Replace a symbol and  the final length");
			System.out.println("6.Reverse the string and the final length");
			System.out.println("7.Delete the substring and the final length");
			System.out.println("8.Replace the substring and the final length");
			System.out.println("9.First occurance of the symbol");
			System.out.println("10.Last occurance of the symbol");
			System.out.println("Enter the choice:");
			choice = sc.nextInt();

			try{
			switch(choice){
				case 1:
					StringBuilder stringBuilder= task.getStringBuilder();
					System.out.println("The length of string builder: "+ task.findLength(stringBuilder ));
					System.out.println("Enter the string to be added");
					String string = sc.next();
					System.out.println("StringBuilder:"+task.addString(stringBuilder,string));
					System.out.println("The length of string builder: "+ task.findLength(stringBuilder));	
					break;		
				
				case 2:
					StringBuilder stringBuilderOne=task.getStringBuilder("hello");
					System.out.println("StringBuilder: "+stringBuilderOne);
					System.out.println("The length of string builder: "+ task.findLength(stringBuilderOne));
					System.out.println("Enter the no of strings to be added");
					int noOfStrings=sc.nextInt();	
					String[] stringArray = new String[noOfStrings];	
					System.out.println("Enter the strings");
					for(int i=0;i<noOfStrings;i++){
						stringArray[i]=sc.next();
					}
					System.out.println("Enter the symbol to seperate them");
				        char character = sc.next().charAt(0);
					System.out.println("After addition of strings:"+task.addStrings(stringBuilderOne,stringArray,noOfStrings,character));
					System.out.println("The length of string builder: "+ task.findLength(stringBuilderOne));
					break;

				case 3:
				
					StringBuilder stringBuilderTwo = task.getStringBuilder("Zoho Corporation");
					System.out.println("StringBuilder: "+stringBuilderTwo);
					System.out.println("Enter the string to be added");
					String stringInBetween = sc.next();
					System.out.println("After addition of string in between "+ task.insertInBetween(stringBuilderTwo,stringInBetween,' '));
					System.out.println("The length of string builder: "+ task.findLength(stringBuilderTwo));
					break;
					
					
					
				case 4:
					StringBuilder stringBuilderDelete = task.getStringBuilder("Nice Day");
					System.out.println("StringBuilder: "+stringBuilderDelete);
					System.out.println("The length of string builder: "+ task.findLength(stringBuilderDelete));
					System.out.println("Enter the string to delete");
					String stringToDelete = sc.next();
					System.out.println("After deletion of string "+ task.deleteString(stringBuilderDelete,stringToDelete));
					System.out.println("The length of string builder: "+ task.findLength(stringBuilderDelete));
					break;


				case 5:
					StringBuilder stringBuilderReplace = task.getStringBuilder("Task Completion Message");
					System.out.println("StringBuilder: "+stringBuilderReplace);
					System.out.println("The length of string builder: "+ task.findLength(stringBuilderReplace));
					System.out.println("Enter the character to be replaced ");
					char toBeReplaced=sc.next().charAt(0);
					sc.nextLine();
					System.out.println("Enter the character to replace");
					char charac=sc.next().charAt(0);
					System.out.println("After replacing the character "+task.replaceChar(stringBuilderReplace,toBeReplaced,charac));
					System.out.println("The length of string builder "+ task.findLength(stringBuilderReplace));
					break;

				case 6:
					
					StringBuilder stringBuilderReverse = task.getStringBuilder("Reverse the stringbuilder");
					System.out.println("StringBuilder: "+stringBuilderReverse);
					System.out.println("The length of string builder " + task.findLength(stringBuilderReverse));
					System.out.println("After reversing  the character "+task.reverseBuilder(stringBuilderReverse));
					System.out.println("The length of string builder "+ task.findLength(stringBuilderReverse));
					break;

				case 7:
					
					StringBuilder subStringDelete = task.getStringBuilder("StringBuilderismutable");
					System.out.println("StringBuilder: "+subStringDelete);
					System.out.println("The length of string builder "+ task.findLength(subStringDelete));
					System.out.println("Enter the start index");
					int startIndex = sc.nextInt();
					System.out.println("Enter the end index ");
					int endIndex =sc.nextInt();
					System.out.println("After deleting the substring "+task.deleteSubstring(subStringDelete,startIndex,endIndex));
					System.out.println("The length of string builder "+ task.findLength(subStringDelete));
					break;

				case 8:

					StringBuilder subStringReplace = task.getStringBuilder("StringBuilderismutable");
					System.out.println("StringBuilder: "+subStringReplace);
					System.out.println("The length of string builder "+ task.findLength(subStringReplace));
					System.out.println("Enter the start index");
					int start = sc.nextInt();
					System.out.println("Enter the end index");
					int end =sc.nextInt();
					System.out.println("Enter the string to replace");
					String replace=sc.next();
					System.out.println("After deleting the substring"+task.replaceSubstring(subStringReplace,start,end,replace));
					System.out.println("The length of string builder "+ task.findLength(subStringReplace));
					break;
				
				case 9:
		
					StringBuilder firstOccurance = task.getStringBuilder("String#Builder#is#mutable");
					System.out.println("StringBuilder: "+firstOccurance);
					System.out.println("The length of string builder: "+ task.findLength(firstOccurance));
					System.out.println("Enter the string to find");
					String findString = sc.next();
					System.out.println("After deleting the substring "+task.firstOccuranceOf(firstOccurance,findString));
					System.out.println("The length of string builder: "+ task.findLength(firstOccurance));
					break;
					
				case 10:

					StringBuilder lastOccurance = task.getStringBuilder("String#Builder#is#mutable");
					System.out.println("StringBuilder: "+lastOccurance);
					System.out.println("The length of string builder "+ task.findLength(lastOccurance));
					System.out.println("Enter the string to find");
					String findLastString = sc.next();
					System.out.println("After deleting the substring "+task.lastOccuranceOf(lastOccurance,findLastString));
					System.out.println("The length of string builder "+ task.findLength(lastOccurance));
					break;



			}
			}
			catch (InvalidException e) {
                    		System.out.println("Invalid operation: " + e.getMessage());
                	} 
		    catch (OutOfBoundException e) {
                    		System.out.println("Out of bound error: " + e.getMessage());
			}
		
						
                }
		while(choice !=-1);}
		finally{
		sc.close();
		}
					
	}
}					
					

					
								

		
	