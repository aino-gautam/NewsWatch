package com.login.server.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gwt.core.client.GWT;
import com.login.client.UserInformation;

public class UserHelper extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{
	private Connection conn = null;
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private String driverClassName;
	private String connectionUrl;
	private String username;
	private String password;
	
	private static final String USER_INFO = "userInfo";
	
	public UserHelper()
	{}
	
	public UserHelper(HttpServletRequest req, HttpServletResponse res, String connectionUrl, String driverClassName, String username, String password)
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

	public void closeConnection(){
		try{
			if(conn != null){
				System.out.println("Closing connection for login");
				conn.close();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
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
		return null ;
	}

	public String saveUserRecord(UserInformation userinfo) 
	{
		try
		{
			String query = "insert into user (FirstName, LastName, Company, title, PhoneNo, email, password,IndustryEnumId) values ('"+userinfo.getFirstname()+"', '"+userinfo.getLastname()+"', '"+userinfo.getCompanyname()+"', '"+userinfo.getTitle()+"', '"+userinfo.getPhoneno()+"', '"+userinfo.getEmail()+"','"+userinfo.getPassword()+"','"+userinfo.getUserSelectedIndustryID()+"')";
			Statement stmt = conn.createStatement() ;
			stmt.executeUpdate(query);
			stmt.close();
			return "Success";
		} 
		catch(Exception ex)
		{
			return "Failed";
		}
	}

	/**
	 * validates the user trying to login
	 * @param userinfo UserInformation object
	 * @return
	 */
	
	/**
	 * validates the user trying to login
	 * @param userinfo UserInformation object
	 * @return
	 */
	public ArrayList validateUser(UserInformation userinfo)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ArrayList list = new ArrayList();
		try
		{
			String query1 = "select U.UserId,U.FirstName,U.LastName,U.isAdmin,U.isApproved,U.Signature,NC.NewsCenterId,US.isSubscribed,US.NewsFilterMode from user as U,newscenter as NC,usersubscription US where U.email = '"+userinfo.getEmail()+"' and U.password = '"+userinfo.getPassword()+"' and NC.IndustryEnumId = "+userinfo.getUserSelectedIndustryID()+" and U.UserId = US.UserId";
			Statement stmt=conn.createStatement();
			ResultSet rs1 = stmt.executeQuery(query1);
			while(rs1.next()){
				if(rs1.getInt("isAdmin") == 1 || rs1.getInt("isAdmin") == 2){ // if user is an admin or sales executive
					list.add("true");
					
					HttpSession mySes = request.getSession(true); 
					userinfo.setUserId(rs1.getInt("UserId"));
					userinfo.setUserSelectedNewsCenterID(rs1.getInt("NewsCenterId"));
					userinfo.setUserSelectedIndustryID(userinfo.getUserSelectedIndustryID());
					userinfo.setIsAdmin(rs1.getInt("isAdmin"));
					userinfo.setApproved(rs1.getInt("isApproved"));
					userinfo.setIsSubscribed(rs1.getInt("isSubscribed"));
					userinfo.setNewsFilterMode(rs1.getInt("NewsFilterMode"));
					userinfo.setSignature(rs1.getString("Signature"));
					userinfo.setFirstname(rs1.getString("FirstName"));
					userinfo.setLastname(rs1.getString("LastName"));
					
					list.add(userinfo);
					
					mySes.setAttribute(UserHelper.USER_INFO,userinfo);
					Date date = new Date();
					
					stmt=conn.createStatement();
					query1 = "insert into loginStatistics(userId,industryEnumId,timeOfLogin) values("+userinfo.getUserId()+","+userinfo.getUserSelectedIndustryID()+",'"+df.format(date)+"')";
					stmt.executeUpdate(query1);
					System.out.println("User log: - UserEmail = "+userinfo.getEmail()+" NewsCenterID = "+userinfo.getUserSelectedIndustryID()+" Login Time = "+date);
				}
				else{
				String query = "select U.*,NC.Name,NC.NewsCenterId,US.Duration,US.DurationLeft,US.isSubscribed,US.NewsFilterMode from newscenter as NC,user as U,usersubscription as US where U.email = '"+userinfo.getEmail()+"' and U.password = '"+userinfo.getPassword()+"' and U.IndustryEnumId = NC.IndustryEnumId and U.UserId = US.UserId";
				stmt=conn.createStatement();
				HttpSession mySes = request.getSession(true); 
				ResultSet rs = stmt.executeQuery(query);
					if(rs.next())
					{   // for normal user (subscriber)
						list.add("true");
						
						userinfo.setUserId(rs.getInt("UserId"));
						userinfo.setUserSelectedIndustryID(userinfo.getUserSelectedIndustryID());
						userinfo.setUserSelectedNewsCenterID(rs.getInt("NewsCenterId"));
						userinfo.setIsAdmin(rs.getInt("isAdmin"));
						userinfo.setApproved(rs.getInt("isApproved"));
						userinfo.setDurationLeft(rs.getInt("DurationLeft"));
						userinfo.setIsSubscribed(rs1.getInt("isSubscribed"));
						userinfo.setNewsFilterMode(rs1.getInt("NewsFilterMode"));
						userinfo.setSignature(rs.getString("Signature"));
						userinfo.setFirstname(rs1.getString("FirstName"));
						userinfo.setLastname(rs1.getString("LastName"));
						
						list.add(userinfo);
						
						mySes.setAttribute(UserHelper.USER_INFO,userinfo);	
						Date date = new Date();
						stmt=conn.createStatement();
						query1 = "insert into loginStatistics(userId,industryEnumId,timeOfLogin) values("+userinfo.getUserId()+","+userinfo.getUserSelectedIndustryID()+",'"+df.format(date)+"')";
						stmt.executeUpdate(query1);
						System.out.println("User log: - UserEmail = "+userinfo.getEmail()+" NewsCenterID = "+userinfo.getUserSelectedNewsCenterID()+" Login Time = "+date);
					    
					}
					else
					{ // user not found (email / pass does not match)
						list.add("false");
						
					}
					rs.close();
				}
			}
			rs1.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in validateUser IN UserInformatonServiceImpl");
			list.add("false");
		    return list;
		}
		return list;
	}
	
	
	
	/*public ArrayList validateUser(UserInformation userinfo)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ArrayList list = new ArrayList();
		try
		{
			String query1 = "select U.UserId,U.FirstName,U.LastName,U.isAdmin,U.isApproved,U.Signature,NC.NewsCenterId,US.isSubscribed,US.NewsFilterMode from user as U,newscenter as NC,usersubscription US where U.email = '"+userinfo.getEmail()+"' and U.password = '"+userinfo.getPassword()+"' and NC.IndustryEnumId = "+userinfo.getUserSelectedIndustryID()+" and U.UserId = US.UserId";
			Statement stmt=conn.createStatement();
			ResultSet rs1 = stmt.executeQuery(query1);
			while(rs1.next()){
				if(rs1.getInt("isAdmin") == 1 || rs1.getInt("isAdmin") == 2){ // if user is an admin or sales executive
					list.add("true");
					list.add(rs1.getInt("isAdmin"));
					list.add(rs1.getInt("isApproved"));
					list.add(rs1.getInt("isSubscribed"));
					list.add(rs1.getInt("NewsFilterMode"));
					
					HttpSession mySes = request.getSession(true); 
					userinfo.setUserId(rs1.getInt("UserId"));
					userinfo.setUserSelectedNewsCenterID(rs1.getInt("NewsCenterId"));
					userinfo.setUserSelectedIndustryID(userinfo.getUserSelectedIndustryID());
					userinfo.setIsAdmin(rs1.getInt("isAdmin"));
					userinfo.setIsSubscribed(rs1.getInt("isSubscribed"));
					userinfo.setNewsFilterMode(rs1.getInt("NewsFilterMode"));
					userinfo.setSignature(rs1.getString("Signature"));
					userinfo.setFirstname(rs1.getString("FirstName"));
					userinfo.setLastname(rs1.getString("LastName"));
					
					mySes.setAttribute("userInfo",userinfo);
					Date date = new Date();
					
					stmt=conn.createStatement();
					query1 = "insert into loginStatistics(userId,industryEnumId,timeOfLogin) values("+userinfo.getUserId()+","+userinfo.getUserSelectedIndustryID()+",'"+df.format(date)+"')";
					stmt.executeUpdate(query1);
					System.out.println("User log: - UserEmail = "+userinfo.getEmail()+" NewsCenterID = "+userinfo.getUserSelectedIndustryID()+" Login Time = "+date);
				}
				else{
				String query = "select U.*,NC.Name,NC.NewsCenterId,US.Duration,US.DurationLeft,US.isSubscribed,US.NewsFilterMode from newscenter as NC,user as U,usersubscription as US where U.email = '"+userinfo.getEmail()+"' and U.password = '"+userinfo.getPassword()+"' and U.IndustryEnumId = NC.IndustryEnumId and U.UserId = US.UserId";
				stmt=conn.createStatement();
				HttpSession mySes = request.getSession(true); 
				ResultSet rs = stmt.executeQuery(query);
					if(rs.next())
					{   // for normal user (subscriber)
						list.add("true");
						list.add(rs.getInt("isAdmin")); // admin or not
						list.add(rs.getInt("isApproved")); // approved or not
						list.add(rs.getInt("IndustryEnumId")); // industryid
						list.add(rs.getInt("DurationLeft")); // durationLeft
						list.add(rs1.getInt("isSubscribed"));
						list.add(rs1.getInt("NewsFilterMode"));	
						
						userinfo.setUserId(rs.getInt("UserId"));
						userinfo.setUserSelectedIndustryID(userinfo.getUserSelectedIndustryID());
						userinfo.setUserSelectedNewsCenterID(rs.getInt("NewsCenterId"));
						userinfo.setIsSubscribed(rs1.getInt("isSubscribed"));
						userinfo.setNewsFilterMode(rs1.getInt("NewsFilterMode"));
						userinfo.setSignature(rs.getString("Signature"));
						userinfo.setFirstname(rs1.getString("FirstName"));
						userinfo.setLastname(rs1.getString("LastName"));
						
						mySes.setAttribute("userInfo",userinfo);	
						Date date = new Date();
						stmt=conn.createStatement();
						query1 = "insert into loginStatistics(userId,industryEnumId,timeOfLogin) values("+userinfo.getUserId()+","+userinfo.getUserSelectedIndustryID()+",'"+df.format(date)+"')";
						stmt.executeUpdate(query1);
						System.out.println("User log: - UserEmail = "+userinfo.getEmail()+" NewsCenterID = "+userinfo.getUserSelectedNewsCenterID()+" Login Time = "+date);
					    
					}
					else
					{ // user not registered
						list.add("false");
						
					}
					rs.close();
				}
			}
			rs1.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in validateUser IN UserInformatonServiceImpl");
			list.add("false");
		    return list;
		}
		return list;
	}*/

	public String forgotPasswordRetrieve(UserInformation userinfo) 
	{
		try 
		{
			String passwordOfUser = "";
			String query = "select password from user where email = '"+userinfo.getEmail()+"'";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				passwordOfUser = rs.getString("password");
				userinfo.setPassword(passwordOfUser);
				System.out.println("The forgotten password of the user is "+passwordOfUser);
			}
			rs.close();
			stmt.close();
			return passwordOfUser;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
			return null;
		}
	}

	public String updateRecord(UserInformation userinfo,String colName, String email){
		if(colName.equals("email")){
			try{
				String query = "update user set " +colName+ "='" +userinfo.getEmail()+ "' where email='"+email+"'";
				Statement stmt = conn.createStatement() ;
				stmt.close();
				return "Success";
			}
			catch(Exception ex){
				return "Failed";
			}
		}
		else if(colName.equals("password")){
			try{
				String query = "update user set " +colName+ "='" +userinfo.getPassword()+ "' where email='"+email+"'";
				Statement stmt=conn.createStatement();
				stmt.executeUpdate(query);
				stmt.close();
				return "Success";
			}
			catch(Exception ex){
				return "Failed";
			}
		}
		return "Failed";
	}
	
	public String getTheLink(String newscenter)
	{
		int newscenterId = Integer.parseInt(newscenter);
		String link="";
		if(newscenterId == 1)
		{
			String path = GWT.getModuleBaseURL();
			link = path+"login.html";
		}
		else if(newscenterId == 2)
		{
			String path = GWT.getModuleBaseURL();
			link = path+"lonzaNewsceter.html";
			
		}
		return link;
	}

	public void setValuesToSession(int ncId ,String ncName){
		UserInformation userinfo = new UserInformation();
		userinfo.setUserSelectedIndustryID(ncId);
		userinfo.setIndustryNewsCenterName(ncName);
	}

	public int emailapprovalCheck(String email){
		int isapproved = 0;
		try{
			String query = "select isApproved from user where email='"+email+"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				isapproved = rs.getInt("isApproved");
			}
			rs.close();
			stmt.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return isapproved;
	}

	public ArrayList checkUserLogin(){
		ArrayList arraylist = new ArrayList();
		HttpSession session = request.getSession(false);
		int userSelectedIndusId =-1;
		String userSelectedIndus="";
		if(session!=null){
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			if(userInformation!=null){
				userSelectedIndusId = userInformation.getUserSelectedIndustryID();
				userSelectedIndus = userInformation.getIndustryNewsCenterName();
				arraylist.add(userSelectedIndusId);
				arraylist.add(userSelectedIndus);
				return arraylist;
			}
			else{
				return null;
			}
		}
		else{
			return null;
		}
	}
}