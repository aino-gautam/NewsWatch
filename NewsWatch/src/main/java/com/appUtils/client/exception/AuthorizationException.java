package com.appUtils.client.exception;

import java.io.Serializable;

public class AuthorizationException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int INVALIDUSER=1;
	public static final int SERVICEEXPIRED=2;
	public static final int SERVICENOTACTIVATED=3;
	public static final int INVALIDKEY=4;

	
	private int authExceptionType;
	
	
	public AuthorizationException(){
		
	}
	
	
	public AuthorizationException(int authExceptionType) {
		super();
		this.authExceptionType = authExceptionType;
	}


	public int getAuthExceptionType() {
		return authExceptionType;
	}

	public void setAuthExceptionType(int authExceptionType) {
		this.authExceptionType = authExceptionType;
	}
}
