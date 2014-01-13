package com.lonzaNewscenter.server;

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
import com.login.server.db.AllocateResources;
import com.lonzaNewscenter.client.ServiceProvider;
import com.lonzaNewscenter.server.db.UserHelperLonza;
import com.newscenter.server.db.UserHelperNewsCenter;

public class ServiceProviderImpl extends RemoteServiceServlet implements ServiceProvider{
	
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
		connectionUrl =(String)context.getAttribute(/*"connectionUrl"*/AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(/*"driverClassName"*/AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(/*"userName"*/AllocateResources.USERNAME);
		password =(String)context.getAttribute(/*"password"*/AllocateResources.PASSWORD);
		/*connectionUrl =(String)context.getAttribute("connectionUrl");
		driverClassName =(String)context.getAttribute("driverClassName");
		username =(String)context.getAttribute("userName");
		password =(String)context.getAttribute("password");*/
	}

	public int getInformationFromSession() {
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperLonza helper = new UserHelperLonza( req,res,connectionUrl,driverClassName,username,password);
			return helper.getInformationFromSession();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	public boolean validateUser() 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperLonza helper = new UserHelperLonza( req,res,connectionUrl,driverClassName,username,password);
			return helper.validateUser();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public int getisadminInformation() {
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req , res,connectionUrl,driverClassName,username,password);
			return helper.getadminInformation();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
			return 0;
		}
	}

	
	public ArrayList getUserInformation() 
	{
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperLonza helper = new UserHelperLonza( req,res,connectionUrl,driverClassName,username,password);
			return helper.getUserInformation();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
			return null;
		}
	}
}