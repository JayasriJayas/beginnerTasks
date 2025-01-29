package com.zoho.stringbuilder.util;
import com.zoho.stringbuilder.exception.InvalidException;
import com.zoho.stringbuilder.exception.OutOfBoundException;

public  class Util {
	public static void validate(Object obj) throws InvalidException{
		if(obj==null){
			throw new InvalidException ("Error:Invalid input");
		}
	}
	
	public static void indexCheck(int stringLen,int value)throws OutOfBoundException{
		if(value<0 || value>stringLen){
			throw new OutOfBoundException("Error:Input is out of valid input range");
		}
	}
}

	