 package com.zoho.arraylist.util;

 import java.util.Scanner;
 import com.zoho.arraylist.exception.InvalidException;
 import com.zoho.arraylist.exception.OutOfBoundException;
 public class Util{
 	public static void validate(Object obj)throws InvalidException{
 		if(obj==null){
 			throw new InvalidException("Error :Invalid Input");
 		}
 	}
 	public static void indexCheck(int value,int length)throws OutOfBoundException{
 		if(value<0||value>length){
 			throw new OutOfBoundException("Error : IndexOutOFBound ");
 		}
 	}
 	public static String[] getStrings(int count,Scanner sc) {
        		String[] stringArray = new String[count];
         	for (int i = 0; i < count; i++) {
             		stringArray[i] = sc.next();
         	}
         	return stringArray;
         }


 	
 }
