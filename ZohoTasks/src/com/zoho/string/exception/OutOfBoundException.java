package com.zoho.string.exception;


public class OutOfBoundException extends Exception{
	public OutOfBoundException(String message){
		super(message);
	}
	public OutOfBoundException(String message, Throwable cause){
		super(message, cause);
	}
}