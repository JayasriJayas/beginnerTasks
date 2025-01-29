package com.zoho.hashmap.util;

import com.zoho.hashmap.exception.InvalidException;



public class Util{
	public  static void validate(Object obj)throws InvalidException{
		if(obj == null){
			System.out.println("Error: Invalid");
		}
	}
	
	
	
}