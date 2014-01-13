package com.login.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.login.client.UserInformation;
import com.login.client.UserInformationService;
import com.login.server.db.AllocateResources;
import com.login.server.db.UserHelper;

public class UserInformationServiceImpl extends RemoteServiceServlet implements UserInformationService
{
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	String connectionUrl;
	String driverClassName;
	String username;
	String password;	

	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		ServletContext context=getServletContext();
		connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(AllocateResources.USERNAME); 
		password =(String)context.getAttribute(AllocateResources.PASSWORD);
	}

	public String saveUserRecord(UserInformation userinfo){
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelper helper = new UserHelper( req,res,connectionUrl,driverClassName,username,password);
			String status = helper.saveUserRecord(userinfo);
			helper.closeConnection();
			if(status.equals("Success"))
				return "Success";
			else
				return "Failed";
		}
		catch(Exception ex){
			return "Failed";
		}
	}

	public ArrayList validateUser(UserInformation userinfo){
		ArrayList list = new ArrayList();
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();

			UserHelper helper = new UserHelper( req , res,connectionUrl,driverClassName,username,password);
			ArrayList arraylist = helper.validateUser(userinfo);
			helper.closeConnection();
			return arraylist;
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Error in validateUser IN UserInformatonServiceImpl");
			list.add("false");
			return list;
		}
	}

	public String forgotPasswordRetrieve(UserInformation userinfo) 
	{
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelper helper = new UserHelper( req , res,connectionUrl,driverClassName,username,password);
			String str = helper.forgotPasswordRetrieve(userinfo);
			helper.closeConnection();
			return str;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
			return null;
		}
	}

	public String updateRecord(UserInformation userinfo, String colName,String email) {
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
		
			UserHelper helper = new UserHelper( req , res,connectionUrl,driverClassName,username,password);
			String status = helper.updateRecord(userinfo,colName,email);
			helper.closeConnection();
			if(status.equals("Success"))
				return "Success";
			else
				return "Failed";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in updating record");
			return "Failed";
		}
	}

	public boolean sendMailForForgotPassword(UserInformation userinfo) {

		return false;
	}

	
	public void setValuesToSession(int ncId,String ncName) 
	{
		try{
		
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelper helper = new UserHelper( req , res,connectionUrl,driverClassName,username,password);
		helper.setValuesToSession(ncId,ncName);
		helper.closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
			
		}
	}

	public ArrayList checkUserLogin() 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelper helper = new UserHelper( req , res,connectionUrl,driverClassName,username,password);
			ArrayList list = helper.checkUserLogin();
			helper.closeConnection();
			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		
	}

	public int emailapprovalCheck(String email) {
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelper helper = new UserHelper( req , res,connectionUrl,driverClassName,username,password);
			int count = helper.emailapprovalCheck(email);
			helper.closeConnection();
			return count;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
	}
}