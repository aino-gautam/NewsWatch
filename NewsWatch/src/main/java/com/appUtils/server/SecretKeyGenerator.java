package com.appUtils.server;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;



public class SecretKeyGenerator {

	private static SecretKey secretKey;
	private static String secretKeyValue="LIGHTHOUSEAPPLICATIONSECRETKEY";
	
	public static SecretKey getSecretKey() throws Exception {
		try{
			
			if(secretKey!=null){
				return secretKey;
			}
			else{
					generateSecretKey();
				return secretKey;
				
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
		
	}
	private static void generateSecretKey() throws Exception {
		
		try{
			
	
			byte[] bytearr = secretKeyValue.getBytes();
			BASE64Decoder decoder = new BASE64Decoder();
		    bytearr = decoder.decodeBuffer(secretKeyValue);
			DESKeySpec keySpec = new DESKeySpec(bytearr);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			secretKey = keyFactory.generateSecret(keySpec);	
		}
		catch(Exception ex){			
			ex.printStackTrace();
			throw ex;
		}
	}
	public static void setSecretKey(SecretKey secretKey) {
		SecretKeyGenerator.secretKey = secretKey;
	}
	
	
	
	
	
	
}
