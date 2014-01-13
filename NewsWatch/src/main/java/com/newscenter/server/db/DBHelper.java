package com.newscenter.server.db;

import java.sql.Connection;
import java.sql.DriverManager;

/*
 import java.sql.Driver;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashMap;

 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;

 import com.login.server.db.AllocateResources;
 */

public class DBHelper {
	
	  public static String driverClassName; 	
	  public static String connectionUrl;
	  public static String username;
	  public static String password; 
	  public static String tomcatpath; 
	  Connection connection; 			

	public DBHelper(String connectionurl, String driverClassname,
			String userName, String passWord, String tomcatrealpath) {

		driverClassName = driverClassname;
		connectionUrl = connectionurl;
		username = userName;
		password = passWord;
		tomcatpath = tomcatrealpath;

	}

	public DBHelper() {
	}

	protected void finalize() throws Throwable {
		super.finalize();
		if (connection != null)
			connection.close();
	}

	public Connection getConnection() {
		try {
			if (connection != null)
				return connection;
			System.out.println("Returning connection for "
					+ DBHelper.connectionUrl);

			if (DBHelper.connectionUrl != null) {
				 System.out.println("connectionUrl = " + DBHelper.connectionUrl);
				 Class.forName (driverClassName);
				connection = DriverManager.getConnection(connectionUrl,
						username, password);
			}
		}

		catch (Exception ex) {
			ex.printStackTrace();
		}
		return connection;

	}

	public void closeConnection() {
		try {
			if (connection != null) {
				System.out.println("Connection closed in DBHelper");
				connection.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
