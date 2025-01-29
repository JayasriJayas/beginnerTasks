package com.zoho.string.task;
import com.zoho.string.util.Util;
import com.zoho.string.exception.OutOfBoundException;
import com.zoho.string.exception.InvalidException;


public class StringTask {
    public int findLength(String string)throws InvalidException{
        Util.validate(string);
        return string.length();
    }

    public char[] convertToCharArray(String string)throws InvalidException{
        Util.validate(string);
        return string.toCharArray();
    }

    public char penultimateChar(String string,int position)throws InvalidException, OutOfBoundException{
	if(string==""){
		throw new InvalidException("Error:Input string cannot be empty");
	}
        int length =findLength(string);
	if(length==0){
		throw new InvalidException("Error:Input string cannot be empty");
	}
        Util.indexValidation(length,position);
        return string.charAt(length - position);
    }
	
    public int toFindOccurances(String string, char character)throws InvalidException { 
        int len = findLength(string);
        int count = 0;
        
        for (int i = 0; i < len; i++) {
            if (string.charAt(i) == character) {
                count++;
            }
        }
        return count;
    }

    
    public int lastPosition(String string, char character)throws InvalidException{
        int length=findLength(string);
        int lastIndex = string.lastIndexOf(character, length-1);
        return lastIndex;
    }

    public String lastSubstring(String string,int subString)throws InvalidException, OutOfBoundException{
	int length=findLength(string);
        Util.indexValidation(length,subString);
        return string.substring(length-subString);
    }

    public String firstSubstring(String string,int firstsubString)throws InvalidException, OutOfBoundException  {
	int length=findLength(string);
	Util.indexValidation(length,firstsubString);
        return string.substring(0, firstsubString);
    }

    public String replaceString(String string, String replaceString)throws InvalidException, OutOfBoundException  {
	int lenOfString=findLength(string);
	int lenOfReplace=findLength(replaceString);
        Util.indexValidation(lenOfString,lenOfReplace);
        String firstString = firstSubstring(string,lenOfReplace);
        return string.replace(firstString, replaceString);
    }


    public boolean starts(String string, String replaceString)throws InvalidException {
        Util.validate(string);
	Util.validate(replaceString);
        return string.startsWith(replaceString);
    }

    public boolean ends(String string, String replaceString)throws InvalidException {
        Util.validate(string);
	Util.validate(replaceString);
        return string.endsWith(replaceString);
    }

   
    public String toUpper(String string)throws InvalidException  {
        Util.validate(string);
        return string.toUpperCase();
    }

    public String toLower(String string)throws InvalidException{
        Util.validate(string);
        return string.toLowerCase();
    }

     public String reverse(String string)throws InvalidException  {
    	char[] charArray = convertToCharArray(string);    
    	int i=0,j=charArray.length-1;
	char temp;
	while(i<j){
		temp=charArray[i];
		charArray[i]=charArray[j];
		charArray[j]=temp;
		i++;
		j--;
	}
   	 return new String(charArray);
	}

    public boolean checkEquals(String string1, String string2)throws InvalidException  {
   	Util.validate(string1);
    	Util.validate(string2);
    	return string1.equals(string2);
    }

    public boolean checkEqualsIgnoreCase(String string1, String string2)throws InvalidException  {
        Util.validate(string1);
        Util.validate(string2);
        return string1.equalsIgnoreCase(string2);
    }

    public String stringsWithNoSpaces(String string,String replaceString,String replaceWith)throws InvalidException  {
        Util.validate(string);
        return string.replaceAll(replaceString,replaceWith);
    }

    public String[] stringArrayWithWords(String lineword,String splitSymbol)throws InvalidException { 
        Util.validate(lineword);
	Util.validate(splitSymbol);
        return lineword.split(splitSymbol);
    }

    public String mergeString(String symbol,String[] stringArray) throws InvalidException {
	Util.validate(stringArray);
	Util.validate(symbol);
        return String.join(symbol, stringArray);
    }

    public String removeSpaces(String string) throws InvalidException {
        Util.validate(string);
        return string.trim();
    }
    public String concatenateStrings(String[] string)throws InvalidException{
	Util.validate(string);
	String stringanswer="";
	for(int i=0;i<string.length;i++){
		stringanswer+=string[i];
	}
	return stringanswer;
    }
		
	
	

} 