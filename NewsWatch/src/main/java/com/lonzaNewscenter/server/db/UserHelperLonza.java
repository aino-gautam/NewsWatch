package com.lonzaNewscenter.server.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.login.client.UserInformation;

public class UserHelperLonza extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet{

	private Connection conn = null;
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private String driverClassName;
	private String connectionUrl;
	private String username;
	private String password;

	public UserHelperLonza(HttpServletRequest req  , HttpServletResponse res,String connectionUrl,String driverClassName,String username,String password)
	{
		try 
		{
			request = req ;
			response = res ;
			this.connectionUrl =connectionUrl;
			this.driverClassName =driverClassName;
			this.username =username;
			this.password =password;
			getConnection();
		}
		catch(Exception ex)
		{
			System.out.println("Exception in getConnection!");
			ex.printStackTrace();
		}
	}

	protected void finalize() throws Throwable
	{
		super.finalize();
		if (conn != null)
			conn.close();
	}

	private Connection getConnection()
	{
		try
		{
			if(conn==null)
			{
				Driver drv =(Driver)Class.forName(driverClassName).newInstance();
				DriverManager.registerDriver(drv);
				conn=DriverManager.getConnection(connectionUrl,username,password);
				return conn;
			}
			else
				return conn;
		}
		catch(Exception ex){
			System.out.println("Exception in getConnection!");
			ex.printStackTrace();
		}
		return null;
	}

	public int getInformationFromSession()
	{
		ArrayList list = new ArrayList();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int id = userInformation.getUserId();
		return id;
	}

	public boolean validateUser()
	{
		HttpSession session = request.getSession(false);
		if(session!=null)
		{
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			if(userInformation==null)//.length()==0)
			{
				return false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public int getadminInformation(){
		int isadmin = 0;
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		String mail =  userInformation.getEmail();
		try{
			String query = "select isadmin from user where email = '"+mail+"'";
			Statement stmt = conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				isadmin = rs.getInt(1);
				userInformation.setIsAdmin(isadmin);
			}
			return isadmin;
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
	}
	public ArrayList getUserInformation()
	{
		ArrayList arraylist = new ArrayList();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		String useremail = userInformation.getEmail();
		String username = getusername(useremail);
		userInformation.setFirstname(username);
		arraylist.add(userInformation);
		return arraylist;
		
	}
	public String getusername(String useremail)
	{
		String username = new String();
		try
		{
			String query = "SELECT FirstName from user where email='"+useremail+"'";
			Statement stmt = conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				username = rs.getString("FirstName");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return username;
	}
}