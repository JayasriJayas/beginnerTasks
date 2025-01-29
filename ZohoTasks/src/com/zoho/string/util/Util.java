package com.zoho.string.util;
import com.zoho.string.exception.OutOfBoundException;
import com.zoho.string.exception.InvalidException;


public class Util{
	
   public static void validate(Object obj)throws InvalidException{
	if(obj==null){
		throw new InvalidException("Error:Input cannot be null");
	}
	
    }
    public static void indexValidation(int stringLen,int value)throws OutOfBoundException{
	if(value<0 || stringLen<value){
		 throw new OutOfBoundException("Error:The value is out of range");
	}
    }
}