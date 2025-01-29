package com.zoho.string.runner;

import java.util.Scanner;
import java.util.Arrays;
import com.zoho.string.task.StringTask;
import com.zoho.string.exception.OutOfBoundException;
import com.zoho.string.exception.InvalidException;

class Runner {

    public static void main(String args[]) {

        Scanner sc = new Scanner(System.in);
        StringTask task = new StringTask();
        int choice;
	
	/*public static void replaceString(){
		System.out.println("Enter a string:");
                String stringToReplace = sc.next();
                System.out.println("Enter substring to replace:");
                String replacement = sc.next();
                System.out.println("Modified string: " + task.replaceString(stringToReplace, replacement));
                
	}*/

        do {
            System.out.println("\nEnter an option number or enter -1 to exit:");
            System.out.println("1. To display command-line argument");
            System.out.println("2. Find length of the string");
            System.out.println("3. Convert string to character array");
            System.out.println("4. Find penultimate character at a given position");
            System.out.println("5. Find occurrences of a given character");
            System.out.println("6. Find the greatest position of a given character");
            System.out.println("7. Substring from last few characters");
            System.out.println("8. Substring from first few characters");
            System.out.println("9. Replace characters in a string");
            System.out.println("10. Check if string starts with a given substring");
            System.out.println("11. Check if string ends with a given substring");
            System.out.println("12. Convert string to uppercase");
            System.out.println("13. Convert string to lowercase");
            System.out.println("14. Reverse the string");
            System.out.println("15. Compare two strings for equality case-sensitive");
            System.out.println("16. Compare two strings for equality case-insensitive");
            System.out.println("17. Split a line into a string array ");
            System.out.println("18. Merge strings with a specified delimiter");
            System.out.println("19. Remove spaces from a string");
            System.out.println("20. Concatenate multiple strings into a single string");
            choice = sc.nextInt();

            try {
                switch (choice) {
                    case 1:
                        if (args.length > 0) {
                            System.out.println("Command-line argument: " + args[0]);
                        } else {
                            System.out.println("No arguments provided.");
                        }
                        break;

                    case 2:
                        System.out.println("Enter a string to find its length:");
                        String stringLength = sc.next();
                        System.out.println("The length of the string is: " + task.findLength(stringLength));
                        break;

                    case 3:
                        System.out.println("Enter a string to convert into a character array:");
                        String stringToChar = sc.next();
                        System.out.println("The string as a char array: " + Arrays.toString(task.convertToCharArray(stringToChar)));
                        break;

                    case 4:
                        System.out.println("Enter a string to find the penultimate character:");
                        String stringSecondLast = sc.next();
                        System.out.println("Enter the position from the end:");
                        int pos = sc.nextInt();
                        System.out.println("The penultimate character at position " + pos + " is: " + task.penultimateChar(stringSecondLast, pos));
                        break;

                    case 5:
                        System.out.println("Enter a string:");
                        String stringFreq = sc.next();
                        System.out.println("Enter a character to find its occurrence:");
                        char charFreq = sc.next().charAt(0);
                        System.out.println("Frequency of the character '" + charFreq + "': " +  task.toFindOccurances(stringFreq, charFreq));
                        break;

                    case 6:
                        System.out.println("Enter a string:");
                        String stringGreatPos = sc.next();
                        System.out.println("Enter a character to find its greatest position:");
                        char charGreatPos = sc.next().charAt(0);
                        System.out.println("The greatest position of the character '" + charGreatPos + "' is: " + task.lastPosition(stringGreatPos, charGreatPos));
                        break;

                    case 7:
                        System.out.println("Enter a string:");
                        String subStringLast = sc.next();
                        System.out.println("Enter the index to extract substring from the end:");
                        int endIndex = sc.nextInt();
                        System.out.println("Extracted substring: " + task.lastSubstring(subStringLast, endIndex));
                        break;

                    case 8:
                        System.out.println("Enter a string:");
                        String subStringFirst = sc.next();
                        System.out.println("Enter the number of characters to extract from the start:");
                        int startIndex = sc.nextInt();
                        System.out.println("First few characters: " + task.firstSubstring(subStringFirst, startIndex));
                        break;

                    case 9:
                        try{
				         System.out.println("Enter a string:");
               		     String stringToReplace = null;
                         System.out.println("Enter substring to replace:");
                         String replacement = null;
                         System.out.println("Modified string: " + task.replaceString(stringToReplace, replacement));
				         //replaceString();
			           }
			           catch(InvalidException e){
				            throw new OutOfBoundException("Error:Value is in out of range",e);
                        } 	
                        break;

                    case 10:
                        System.out.println("Enter a string:");
                        String stringStart = sc.next();
                        System.out.println("Enter substring to check if it starts with:");
                        String startCheck = sc.next();
                        System.out.println("Does the string start with " + startCheck + ": " + task.starts(stringStart, startCheck));
                        break;

                    case 11:
                        System.out.println("Enter a string:");
                        String stringEnd = sc.next();
                        System.out.println("Enter substring to check if it ends with:");
                        String endCheck = sc.next();
                        System.out.println("Does the string end with '" + endCheck + "': " + task.ends(stringEnd, endCheck));
                        break;

                    case 12:
                        System.out.println("Enter a string to convert to uppercase:");
                        String stringUpper = sc.next();
                        System.out.println("Uppercase string: " + task.toUpper(stringUpper));
                        break;

                    case 13:
                        System.out.println("Enter a string to convert to lowercase:");
                        String stringLower = sc.next();
                        System.out.println("Lowercase string: " + task.toLower(stringLower));
                        break;

                    case 14:
                        System.out.println("Enter a string to reverse:");
                        String stringReverse = sc.next();
                        System.out.println("Reversed string: " + task.reverse(stringReverse));
                        break;

                    case 15:
                        System.out.println("Enter the first string:");
                        String stringCompare1 = sc.next();
                        System.out.println("Enter the second string:");
                        String stringCompare2 = sc.next();
                        System.out.println("Strings are equal case-sensitive: " + task.checkEquals(stringCompare1, stringCompare2));
                        break;

                    case 16:
                        System.out.println("Enter the first string:");
                        String stringCaseIgnore1 = sc.next();
                        System.out.println("Enter the second string:");
                        String stringCaseIgnore2 = sc.next();
                        System.out.println("Strings are equal case-insensitive: " + task.checkEqualsIgnoreCase(stringCaseIgnore1, stringCaseIgnore2));
                        break;

                    case 17:
                        System.out.println("Enter a line of text:");
                        sc.nextLine(); 
                        String line = sc.nextLine();
                        System.out.println("String array: " + Arrays.toString(task.stringArrayWithWords(line, "\\s+")));
                        break;

                    case 18:
                        System.out.println("Enter the number of strings to merge:");
                        int numStrings = sc.nextInt();
                        String[] stringsToMerge = new String[numStrings];
                        for (int i = 0; i < numStrings; i++) {
                            System.out.println("Enter string " + (i + 1) + ":");
                            stringsToMerge[i] = sc.next();
                        }
                        System.out.println("Enter delimiter:");
                        String delimiter = sc.next();
                        System.out.println("Merged string: " + task.mergeString(delimiter, stringsToMerge));
                        break;

                    case 19:
                        System.out.println("Enter a string to remove spaces:");
                        String stringNoSpaces = sc.next();
                        System.out.println("String without spaces: " + task.removeSpaces(stringNoSpaces));
                        break;

                    case 20:
                        System.out.println("Enter the number of strings to concatenate:");
                        int concatCount = sc.nextInt();
                        String[] stringsToConcat = new String[concatCount];
                        for (int i = 0; i < concatCount; i++) {
                            System.out.println("Enter string " + (i + 1) + ":");
                            stringsToConcat[i] = sc.next();
                        }
                        System.out.println("Concatenated string: " + task.concatenateStrings(stringsToConcat));
                        break;

                    case -1:
                        System.out.println("Exiting the program");
                        break;

                    default:
                        System.out.println("Invalid choice");
                }
          
            } catch (InvalidException e) {
                System.out.println("Invalid operation: " + e.getMessage());
            }catch (OutOfBoundException e) {
                System.out.println("Out of bound error: " + e.getMessage());
    		    if (e.getCause() != null) {
       			System.out.println("Cause: " + e.getCause().getMessage());
    		    } 
            }
        } while (choice != -1);

        sc.close();
    }
}