package com.appUtils.client.exception;


public class FeatureNotSupportedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message = "Feature not supported";
	
	public FeatureNotSupportedException(String string){
		message = string;
	}
	
	public String getDisplayMessage(){
		return message;
	}
}
