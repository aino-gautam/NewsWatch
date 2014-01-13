package com.login.client;

public class ValidationException extends Exception{
	private String display = "Invalid data";

	public ValidationException(String string) 
	{
		display=string;
	}

	public String getDisplayMessage()
	{
		return display;
	}
}