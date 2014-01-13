 package com.login.server.db;

import java.sql.Driver;
import java.sql.DriverManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.mysql.jdbc.Connection;
import com.newscenter.server.db.DBHelper;

public class AllocateResources extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
		Connection connection = null;
		public static final String DBCONNECTIONURL = "ConnectionUrl";
		public static final String DRIVERCLASSNAME = "driverClass";
		public static final String USERNAME = "UserName";
		public static final String PASSWORD = "Password";
		static final long serialVersionUID = 1L;
		
		public static final String TOMCATPATH= "tomcatpath";
		
		public void init(ServletConfig config) throws ServletException
		{
		super.init(config);
		try
		{
			System.out.println("########## IN ALLOCATE RESOURCES ##########");
			String connectionUrl = config.getInitParameter(DBCONNECTIONURL);
			String driverClassName = config.getInitParameter(DRIVERCLASSNAME);
			String username = config.getInitParameter(USERNAME);
			String password = config.getInitParameter(PASSWORD);
			
			ServletContext context=getServletContext();
			context.setAttribute(DBCONNECTIONURL,connectionUrl);
			context.setAttribute(DRIVERCLASSNAME,driverClassName);
			context.setAttribute(USERNAME,username);
			context.setAttribute(PASSWORD,password);
			
			String realPath =  getServletContext().getRealPath("");
			context.setAttribute(TOMCATPATH, realPath);
			System.out.println("The realpath of the folder is...."+realPath);
            context.log("The realpath of the folder is...."+realPath);
			System.out.println("connection strings available in the servlet context");
			
			/**This piece of code is use to intialize the static variables of
			 * the DBHelper */
			DBHelper.driverClassName = driverClassName;
			
			Driver drv =(Driver)Class.forName(DBHelper.driverClassName).newInstance();
		  	DriverManager.registerDriver(drv); 
		
		  	DBHelper.connectionUrl = connectionUrl;
			DBHelper.username = username;
			DBHelper.password = password;
			DBHelper.tomcatpath = realPath;
		
		}
		catch (Exception e)
		{
		System.out.println("exception could not retrieve connection strings");
		e.printStackTrace();
		}
		}
}
