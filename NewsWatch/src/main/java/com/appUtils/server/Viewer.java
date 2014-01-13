package com.appUtils.server;

/**
 * 
 * @author nairutee
 *
 */
public class Viewer {
	
	private static final String PASS_KEY = "" ;// write some passkey
	
	public Viewer(){
		
	}
	
	public String encrypt(String passKey){
		String encryptedStr = null;
		if(passKey.equals(PASS_KEY)){
			// process the request
		}
		return encryptedStr;
	}

	public String decrypt(String passKey){
		String decryptedStr = null;
		if(passKey.equals(PASS_KEY)){
			// process the request
		}
		return decryptedStr;
	}
}
