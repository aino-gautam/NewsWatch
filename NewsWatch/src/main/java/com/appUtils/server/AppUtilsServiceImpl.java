package com.appUtils.server;

import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.appUtils.client.exception.AuthorizationException;
import com.appUtils.client.service.AppUtilsService;
import com.appUtils.server.helper.AppUtilsHelper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class AppUtilsServiceImpl extends RemoteServiceServlet implements AppUtilsService{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8297144300250801425L;
	Logger logger = Logger.getLogger(this.getClass().getName());

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	String connectionUrl;
	String driverClassName;
	String username;
	String password;	
	private String rssUrl="";
	//private String rssUrl="http://122.169.111.248:7080/Resource/";
	private String enpwd="7yj49IhFJAg=";
	
	/*public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		ServletContext context=getServletContext();
		connectionUrl =(String)context.getAttribute("connectionUrl"AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute("driverClassName"AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute("userName"AllocateResources.USERNAME); 
		password =(String)context.getAttribute("password"AllocateResources.PASSWORD);
	}
*/

	@Override
	public void removeFromSession() {
		try{
			HttpServletRequest req = this.getThreadLocalRequest();
			AppUtilsHelper helper = new AppUtilsHelper();
			helper.removeFromSession(req);
			helper.closeConnection();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean isValidAppAccess() throws AuthorizationException{
		try{
			HttpServletRequest req=getThreadLocalRequest();
			return isValidLicensing(req);
			
		}catch (AuthorizationException e) {
			    if(((AuthorizationException) e).getAuthExceptionType()==AuthorizationException.INVALIDKEY)
			    	throw new AuthorizationException(AuthorizationException.INVALIDKEY);
			    else if(((AuthorizationException) e).getAuthExceptionType()==AuthorizationException.SERVICEEXPIRED)
			    	throw new AuthorizationException(AuthorizationException.SERVICEEXPIRED);
			    else
			    	throw new AuthorizationException(AuthorizationException.SERVICENOTACTIVATED);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public void getAppKeyPath(HttpServletRequest req){
		try{
			rssUrl="http://www.ensarm.com/Resource/";
			StringBuffer reqURL=req.getRequestURL();
			String reqDomain=(String)reqURL.subSequence(0,reqURL.length()-(req.getServletPath().length()));
			if(reqDomain.contains("http://")){
				reqDomain=reqDomain.replace("http://", "");				
			}
			String domainArray[]=reqDomain.split("/");
			reqDomain=domainArray[0];
			logger.log(Level.INFO,"DOMAIN NAME : "+reqDomain);
			
			// For test
			reqDomain="www.marketscape1.net";
			
			String fileName=reqDomain.replace(".", "_");
			if(!rssUrl.contains(fileName))
				rssUrl=rssUrl+fileName+".txt";
			
		}catch (Exception e) {
			logger.log(Level.INFO, "AppUtilsServiceImpl --- getAppKeyPath() exception..",e);
		}
	}
	
	public boolean isValidLicensing(HttpServletRequest req) throws AuthorizationException{
		String encryptedData=null;
		String decryptedData=null;
		
		try{
			  
			 /* getAppKeyPath(req);
			  URL fURL=new URL(rssUrl); 
			  URLConnection connection=  fURL.openConnection();
		      Scanner scanner = new Scanner(connection.getInputStream());
		      scanner.useDelimiter("\\Z");
		      encryptedData = scanner.next();
		    
		      
		      if(encryptedData==null) throw new AuthorizationException(AuthorizationException.INVALIDKEY);
		    
		      decryptedData=getDecryptedData(encryptedData);
		    
		    if(decryptedData!=null)
		    	return  validateDecryptedData(decryptedData);*/
			return true;
		    
		}catch (Exception e) {
			logger.log(Level.INFO, "AppUtilsServiceImpl --- isValidLicensing() exception..",e);
			if(e instanceof AuthorizationException){
			    if(((AuthorizationException) e).getAuthExceptionType()==AuthorizationException.INVALIDKEY)
			    	throw new AuthorizationException(AuthorizationException.INVALIDKEY);
			    else if(((AuthorizationException) e).getAuthExceptionType()==AuthorizationException.SERVICEEXPIRED)
			    	throw new AuthorizationException(AuthorizationException.SERVICEEXPIRED);
			}
			    else
			    	throw new AuthorizationException(AuthorizationException.SERVICENOTACTIVATED);
			
		}
		return false;
	}

	
	private String getDecryptedData(String encryptedData) throws Exception {
		
		try{
			
			// To test
			
		  /* SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
			Date  expiredDate = formatter.parse("01/08/2022");
			String pwdHash = BCrypt.hashpw("123qwe", BCrypt.gensalt());
			String str=expiredDate.getTime()+"##"+pwdHash;
			
			SecretKey secretKey=SecretKeyGenerator.getSecretKey();
			
			 Cipher cipher1 = Cipher.getInstance("DES");
			 cipher1.init(Cipher.ENCRYPT_MODE, secretKey);
			
			 BASE64Encoder encoder=new BASE64Encoder();
			 byte[] encrypted=cipher1.doFinal(str.getBytes());
			 String finalEncryptedString=encoder.encodeBuffer(encrypted);
			 
			 System.out.println(finalEncryptedString);*/
			 
			
		
			 SecretKey secretKey=SecretKeyGenerator.getSecretKey();
			 Cipher cipher = Cipher.getInstance("DES");
			 cipher.init(Cipher.DECRYPT_MODE, secretKey);
			 
			 //get decrypted string
			 BASE64Decoder decoder= new BASE64Decoder();
			 byte[] encryptedDataBytes = decoder.decodeBuffer(encryptedData);
			 byte[] decrypted = null;
			 decrypted = cipher.doFinal(encryptedDataBytes);		
			 String decryptedString = new String(decrypted);
			
			 return decryptedString;			
			
		}
		catch(Exception ex){
			logger.log(Level.INFO, "AppUtilsServiceImpl --- getDecryptedData() exception..",ex);
			throw ex;
		}
	}
	
private boolean validateDecryptedData(String decryptedData) throws AuthorizationException {
		
		try{
				
			String[] arrDecryptedData = decryptedData.split("##");
			String expTime=arrDecryptedData[0];
			String encryptedHashPwd=arrDecryptedData[1];
			
			logger.log(Level.INFO,"[----- App Exp. Date ------] ==@> "+new Date(Long.valueOf(expTime)));
			Date currentDate= new Date();
			if(currentDate.getTime()>(new Date(Long.valueOf(expTime))).getTime())
				throw new AuthorizationException(AuthorizationException.SERVICEEXPIRED);
			
			SecretKey secretKey=SecretKeyGenerator.getSecretKey();
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			 
			
			 BASE64Decoder decoder= new BASE64Decoder();
			 byte[] encryptedDataBytes = decoder.decodeBuffer(enpwd);
			 byte[] decrypted = null;
			 decrypted = cipher.doFinal(encryptedDataBytes);		
			 String decryptedPwd = new String(decrypted);
			
			if(!BCrypt.checkpw(decryptedPwd, encryptedHashPwd)){
				throw new AuthorizationException(AuthorizationException.INVALIDUSER);
			}
			
		}
		catch(Exception ex){
			logger.log(Level.INFO, "AppUtilsServiceImpl --- validateDecryptedData() exception..",ex);
			if(ex instanceof AuthorizationException){
				  if(( ((AuthorizationException) ex).getAuthExceptionType()==AuthorizationException.SERVICEEXPIRED))
				    	throw new AuthorizationException(AuthorizationException.SERVICEEXPIRED);
				  else
					  throw new AuthorizationException(AuthorizationException.SERVICENOTACTIVATED);
			}
			else
				return false;
		}
		return true;
}

}
