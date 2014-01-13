package com.appUtils.client;

public class ValidationException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
