package com.admin.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.login.server.db.AllocateResources;

public class AccountDurationServlet extends HttpServlet implements Servlet, Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		//super.doGet(req, resp);
	}

	Thread thread;
	protected String connectionUrl;
	protected String driverClassName;
	protected String username;
	protected String password;

	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		ServletContext context = getServletContext();
		connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(AllocateResources.USERNAME);
		password =(String)context.getAttribute(AllocateResources.PASSWORD);
		thread = new Thread(this);
		thread.start();
	}

	public void run(){
		try{
			while(true){
				calculateDuration();
				thread.sleep(86400000);
				//thread.sleep(900000);
			}
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void calculateDuration(){
		Date date = new Date();
		System.out.println("$$$$$$$$$$$$ IN ACCOUNT DURATION SERVLET $$$$$$$$$$$$$  "+date);
		try{
			Driver drv =(Driver)Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(drv);
			Connection conn = (Connection)DriverManager.getConnection(connectionUrl,username,password);

			/*String query = "select UserId,Duration,DurationLeft,ExtensionDate,isExtended,ExtendedDuration from usersubscription where DurationLeft > 0 and NewsCenterId > 3";*/
			String query = "select UserId,Duration,DurationLeft,ExtensionDate,isExtended,ExtendedDuration from usersubscription where DurationLeft > 0";
			Statement stmt1 = conn.createStatement();
			ResultSet rs = stmt1.executeQuery(query);
			while(rs.next()){
				int userid = rs.getInt("UserId");
				int duration = rs.getInt("Duration");
				int extendedDuration = rs.getInt("ExtendedDuration");
				if(userid == 685)
					System.out.println("User id: "+userid);
				boolean isExtended = rs.getBoolean("isExtended");
				String selectdatequery;
				if(isExtended)
					selectdatequery = "SELECT DATEDIFF(curdate(),ExtensionDate) from  usersubscription where UserId = "+ userid;
				else
					selectdatequery = "SELECT DATEDIFF(curdate(),ApprovalDate) from  usersubscription where UserId = "+ userid;
				Statement stmt2 = conn.createStatement();
				ResultSet rs1 = stmt2.executeQuery(selectdatequery);
				while(rs1.next()){
					String updatedatequery="";
					int datedifference = rs1.getInt(1);
					if(isExtended){
						if(datedifference > extendedDuration){
							updatedatequery = "update usersubscription set DurationLeft = 0 where UserId = "+userid;
						}
						else{
							updatedatequery = "update usersubscription set DurationLeft = ExtendedDuration - "+datedifference+" where UserId = "+userid;
						}
					}
					else{

						if(datedifference > duration){
							updatedatequery = "update usersubscription set DurationLeft = 0 where UserId = "+userid;
						}
						else{

							/*if(isExtended)
			    			updatedatequery = "update usersubscription set DurationLeft = ExtendedDuration - "+datedifference+" where UserId = "+userid;
			    		else*/
							updatedatequery = "update usersubscription set DurationLeft = Duration - "+datedifference+" where UserId = "+userid;
						}
					}
					Statement stmt3 = conn.createStatement();
					stmt3.executeUpdate(updatedatequery);
					stmt3.close();
				}
				rs1.close();
				stmt2.close();
			}
			rs.close();
			stmt1.close();
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.println("$$$$$$$$$$$$ OUT OF ACCOUNT DURATION SERVLET $$$$$$$$$$$$$  "+date);
	}
}