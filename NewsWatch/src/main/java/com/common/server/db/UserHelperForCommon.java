package com.common.server.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.common.client.PageResult;
import com.common.client.UserHistory;
import com.common.client.UserItemAccessStats;
import com.common.client.UserLoginStats;
import com.login.client.UserInformation;
import com.newscenter.client.criteria.PageCriteria;

public class UserHelperForCommon extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	protected Connection conn = null;
	protected HttpServletRequest request = null;
	protected HttpServletResponse response = null;
	protected String driverClassName;
	protected String connectionUrl;
	protected String username;
	protected String password;
	
	public UserHelperForCommon(HttpServletRequest req  , HttpServletResponse res,String connectionUrl,String driverClassName,String username,String password)
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

	protected Connection getConnection()
	{
		try {
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
			System.out.println("Exception in getConnection!revins");
			ex.printStackTrace();
		}
		return null ;
	}
	
	public void closeConnection(){
		try{
			if(conn != null)
				conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void removeFromSession() 
	{
		HttpSession session = request.getSession(false);
		if(session!=null)
		{
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			session.removeAttribute("userInfo");
		}
	}
	
	public String[] getIndustryNameFromSession()
	{
		//String industryName="";
		String industryName[]= new String[4];  
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int industryN = userInformation.getUserSelectedIndustryID();
		String useremail= userInformation.getEmail();
		String userSelectedIndustry = userInformation.getIndustryNewsCenterName();
		
		//industryName[0] = getUserName(useremail);
		industryName[2] = String.valueOf(industryN);
		industryName[3] = userSelectedIndustry;

		//int industryN = (Integer)session.getAttribute("industryId");
		try
		{
			String query="SELECT Name from industryenum where IndustryEnumId= 1"; //+industryN;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				industryName[1] = rs.getString(1);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
		return industryName;
	}
	
	public ArrayList getUserInformationObjec()
	{
		java.util.ArrayList arrayList = new ArrayList();
		HttpSession session = request.getSession(false);
		if(session!=null)
		{
			UserInformation userInformation = (UserInformation)session.getAttribute("userInfo");
			arrayList.add(userInformation);
			
		}
		return arrayList;
	}
	
	public void changeuserdetails(String userpassword,String useremail)
	{
		try{
			String query = "update user set password ='"+userpassword+"' where email='"+useremail+"'";
			Statement stmt = conn.createStatement() ;
			stmt.executeUpdate(query);
			stmt.close();
			HttpSession session = request.getSession(false);
			UserInformation userInformation = (UserInformation)session.getAttribute("userInfo");
			session.removeAttribute("userInfo");
			//userInformation.setPassword("");
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public ArrayList<UserHistory> getUserAccountHistory(int userId) {
		ArrayList<UserHistory> list = new ArrayList<UserHistory>();
		int industryenumid = 0;
		String firstname = null, lastname = null, email = null, newscatalystname = null; 
		ResultSet rs = null,rsuser = null,rscreatedby = null,rsusersub = null,rsoperationperformby = null,rsindustry = null;
	    try{
	    	Statement stmt = getConnection().createStatement();
	    	String query = "select * from useraccounthistory where UserId = "+userId+"";
	    	rs = stmt.executeQuery(query);
	    	while(rs.next()){
	    		UserHistory userhistory = new UserHistory();
	    		userhistory.setOperationDate(rs.getTimestamp("operationDate"));
	    		String operation = rs.getString("operation");
	    		userhistory.setOperation(operation);
	    		userhistory.setId(rs.getInt("useraccounthistoryid"));
	    		userhistory.setUserid(userId);

	    	
	    		query = "select * from user where UserId = "+userId+"";
	    		stmt = getConnection().createStatement();
	    		rsuser = stmt.executeQuery(query);
	    		 while(rsuser.next()){
	    			industryenumid = rsuser.getInt("IndustryEnumId");
	    			firstname = rsuser.getString("FirstName");
	    			lastname = rsuser.getString("LastName");
	    			email = rsuser.getString("email");
	    			userhistory.setFirstname(firstname);
	    			userhistory.setLastname(lastname);
	    			userhistory.setEmail(email);
	    			userhistory.setUserCreatedBy(rsuser.getInt("createdBy"));
	    			
	    			query = "select Name from industryenum i, user u where i.IndustryEnumId = u.IndustryEnumId and u.IndustryEnumId = "+industryenumid+" ";
	    			stmt = getConnection().createStatement();
	    			rsindustry = stmt.executeQuery(query);
	    			while(rsindustry.next()){
	    				newscatalystname = rsindustry.getString("Name");
	    				userhistory.setNewscatalystname(newscatalystname);
	    			}
	    			rsindustry.close();
	    			
	    			query = "select FirstName,LastName from user where UserId = "+userhistory.getUserCreatedBy()+"";
	    			stmt = getConnection().createStatement();
	    			rscreatedby = stmt.executeQuery(query);
	    			while(rscreatedby.next()){
	    			    	userhistory.setUserCreatedByName(rscreatedby.getString("FirstName")+ " "+rscreatedby.getString("LastName"));
	    			}
	    			rscreatedby.close();
	    		
	    			query = "select * from usersubscription where UserId = "+userId+" ";
	    			stmt = getConnection().createStatement();
	    			rsusersub = stmt.executeQuery(query);
	    			while(rsusersub.next()){
	                   	 userhistory.setDuration(rsusersub.getInt("Duration"));
	                   	 userhistory.setApprovalDate(rsusersub.getDate("ApprovalDate"));
	                   	 userhistory.setSubscribed(rsusersub.getBoolean("isSubscribed"));
	                   	 userhistory.setNewsletterperiod(rsusersub.getInt("period"));
                       
	                   	 if(operation.equals("extends")) {
	                   		 userhistory.setExtendedDate(rsusersub.getDate("ExtensionDate"));
	                   		 userhistory.setExtendedDuration(rs.getInt("extendDuration"));
	                   		 query = "select FirstName,LastName from user where UserID = "+rs.getInt("operationPerformBy")+"";
	                   		 stmt = getConnection().createStatement();
	                   		 rsoperationperformby = stmt.executeQuery(query);
	                   		 while(rsoperationperformby.next()){
	                   		   userhistory.setOperationPerformedBy(rsoperationperformby.getString("FirstName")+ " "+rsoperationperformby.getString("LastName"));
	                   		 }
	                   		 rsoperationperformby.close();
	                   	 }
	                }
	    			rsusersub.close();
	    			list.add(userhistory);	
	    		 }
	    		 rsuser.close();
	       	}
	    	rs.close();
	    	stmt.close();
	    	/*UserHistory userhist = new UserHistory();
	    	ArrayList<UserItemAccessStats> listOfUserItemAccess = new ArrayList<UserItemAccessStats>();
			query = "SELECT u.FirstName, u.LastName, ni.Title, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.userId = "+userId+" and uias.newscenterid = "+industryenumid+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid order by timeOfAccess desc";
			stmt = getConnection().createStatement();
			ResultSet rsuseraccessitem = stmt.executeQuery(query);
			while(rsuseraccessitem.next()){
				UserItemAccessStats useritemaccess = new UserItemAccessStats();
				useritemaccess.setFirstname(rsuseraccessitem.getString("FirstName"));
				useritemaccess.setLastname(rsuseraccessitem.getString("LastName"));
				useritemaccess.setNewsitemtitle(rsuseraccessitem.getString("Title"));
				useritemaccess.setNewscatalystitemcount(rsuseraccessitem.getInt("NewscatalystItemCount"));
				useritemaccess.setNewsletteritemcount(rsuseraccessitem.getInt("NewsletterItemCount"));
				listOfUserItemAccess.add(useritemaccess);
			}
			userhist.setFirstname(firstname);
			userhist.setLastname(lastname);
			userhist.setEmail(email);
			userhist.setNewscatalystname(newscatalystname);
			userhist.setUserItemAccessStats(listOfUserItemAccess);
			userhist.setOperation("itemaccess");
			list.add(userhist);*/
			
			/*UserHistory userloginhistory = new UserHistory();
			ArrayList<String> listOfUserLogin = new ArrayList<String>();
			query = "SELECT DATE_FORMAT(timeOfLogin, '%D %M %Y, at %r') as dt FROM loginStatistics WHERE timeOfLogin BETWEEN SYSDATE() - INTERVAL 4 DAY AND SYSDATE() and userId = "+userId+" and industryEnumId = "+industryenumid+" order by timeOFLogin desc";
			stmt = getConnection().createStatement();
			ResultSet rsloginstats = stmt.executeQuery(query);
			while(rsloginstats.next()){
				String dateformat = rsloginstats.getString("dt");
				listOfUserLogin.add(dateformat);
			}
			userloginhistory.setFirstname(firstname);
			userloginhistory.setLastname(lastname);
			userloginhistory.setEmail(email);
			userloginhistory.setNewscatalystname(newscatalystname);
			userloginhistory.setUserLoginStats(listOfUserLogin);
			userloginhistory.setOperation("loginaccess");
			list.add(userloginhistory);*/
			
	    }
	    catch(Exception e){
	    	e.printStackTrace();
	    }
	    
		return list;
	}

	public String getEmailTemplate(int newscenterid) {
		String emailTemplate = null;
		try {
			Statement stmt = getConnection().createStatement();
			String query = "select emailTemplate from industryenum as ie,newscenter as n where ie.IndustryEnumId = n.IndustryEnumId and n.NewsCenterId = "+newscenterid+"";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
            	emailTemplate = rs.getString("emailTemplate");
            }
            closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String strbuilder = new String(emailTemplate);
		
		return strbuilder;
	}

	public void saveSignature(int userid,String signature) {
		try{
		 String query = "update user set Signature = '"+signature+"' where UserId = "+userid+"";
		 Statement stmt = getConnection().createStatement();
		 stmt.executeUpdate(query);
		 stmt.close();
		 closeConnection();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getSignature(int userid) {
		String signature = null;
		try {
			Statement stmt = getConnection().createStatement();
			String query = "select Signature from user where UserId = "+userid+"";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
            	signature = rs.getString("Signature");
            }
            closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signature;
	}
	
	public PageResult getUserItemAccessStats(PageCriteria crt, int userid, int industryid) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int userSelectedIndurtyId = userInformation.getUserSelectedIndustryID();
		UserItemAccessStats userItemAccessStats;
		ArrayList userlist= new ArrayList();
		try{
			String query;
			if(userid == -1){
			    query = "SELECT u.firstname, u.lastname, ni.Title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterid = "+userSelectedIndurtyId+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid order by timeOfAccess desc";
			}
			else{
				query = "SELECT u.firstname, u.lastname, ni.Title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.userId = "+userid+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid order by timeOfAccess desc";	
			}
			stmt=getConnection().createStatement();
			ResultSet rs =stmt.executeQuery(query);
			listSize = rs.last() ? rs.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
			
			
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			
			if(userid == -1){
			  query = "SELECT u.FirstName, u.LastName, ni.Title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterid = "+userSelectedIndurtyId+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid order by timeOfAccess desc LIMIT "+startRecord+","+pagesize+"";
			}
			else{
			  query = "SELECT u.FirstName, u.LastName, ni.Title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.userId = "+userid+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid order by timeOfAccess desc LIMIT "+startRecord+","+pagesize+"";	
			}
			ResultSet rss = stmt.executeQuery(query);
			
			while(rss.next()){
				userItemAccessStats = new UserItemAccessStats();
				userItemAccessStats.setFirstname(rss.getString("FirstName"));
				userItemAccessStats.setLastname(rss.getString("LastName"));
				userItemAccessStats.setNewsitemtitle(rss.getString("Title"));
				userItemAccessStats.setNewsitemdate(rss.getDate("ItemDate"));
				userItemAccessStats.setNewscatalystitemcount(rss.getInt("NewscatalystItemCount"));
				userItemAccessStats.setNewsletteritemcount(rss.getInt("NewsletterItemCount"));
				userlist.add(userItemAccessStats);
			}
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(userlist);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("problem in getUserInformation()");
		}
		
		return pageresult;
	}
	
	
	public PageResult getSearchUserItemAccessStats(PageCriteria crt,int userid,int industryid, String searchColumnName, String searchString) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int userSelectedIndurtyId = userInformation.getUserSelectedIndustryID();
		UserItemAccessStats userItemAccessStats;
		ArrayList userlist= new ArrayList();
		try{
			String query;
			if(userid == -1){
			   query = "SELECT u.firstname, u.lastname, ni.title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterid = "+userSelectedIndurtyId+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid and "+searchColumnName+" like '%"+searchString+"%' order by timeOfAccess desc";
			}
			else{
			   query = "SELECT u.firstname, u.lastname, ni.title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.userId = "+userid+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid and "+searchColumnName+" like '%"+searchString+"%' order by timeOfAccess desc";
			}
			stmt=getConnection().createStatement();
			ResultSet rs =stmt.executeQuery(query);
			listSize = rs.last() ? rs.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
			
			
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			
			if(userid == -1){
			  query = "SELECT u.FirstName, u.LastName, ni.Title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterid = "+userSelectedIndurtyId+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid and "+searchColumnName+" like '%"+searchString+"%' order by timeOfAccess desc LIMIT "+startRecord+","+pagesize+"";
			}
			else{
			  query = "SELECT u.FirstName, u.LastName, ni.Title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.userId = "+userid+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid and "+searchColumnName+" like '%"+searchString+"%' order by timeOfAccess desc LIMIT "+startRecord+","+pagesize+"";
			}
			ResultSet rss = stmt.executeQuery(query);
			
			while(rss.next()){
				userItemAccessStats = new UserItemAccessStats();
				userItemAccessStats.setFirstname(rss.getString("FirstName"));
				userItemAccessStats.setLastname(rss.getString("LastName"));
				userItemAccessStats.setNewsitemtitle(rss.getString("Title"));
				userItemAccessStats.setNewsitemdate(rss.getDate("ItemDate"));
				userItemAccessStats.setNewscatalystitemcount(rss.getInt("NewscatalystItemCount"));
				userItemAccessStats.setNewsletteritemcount(rss.getInt("NewsletterItemCount"));
				userlist.add(userItemAccessStats);
			}
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(userlist);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("problem in getUserInformation()");
		}
		return pageresult;
	}

	public PageResult getSortedUserItemAccessStats(PageCriteria crt,int userid,int industryid, String columnname, String mode) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int userSelectedIndurtyId = userInformation.getUserSelectedIndustryID();
		UserItemAccessStats userItemAccessStats;
		ArrayList userlist= new ArrayList();
		try{
			String query;
			if(userid == -1){
			 query = "SELECT u.firstname, u.lastname, ni.title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterid = "+userSelectedIndurtyId+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid";
			}
			else{
			 query = "SELECT u.FirstName, u.LastName, ni.Title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.userId = "+userid+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid order by timeOfAccess desc";
			}
			stmt=getConnection().createStatement();
			ResultSet rs =stmt.executeQuery(query);
			listSize = rs.last() ? rs.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
						
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			
			if(userid == -1){
			    query = "SELECT u.FirstName, u.LastName, ni.Title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterid = "+userSelectedIndurtyId+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid order by "+columnname+" "+mode+" LIMIT "+startRecord+","+pagesize+"";
			}
			else{
				query = "SELECT u.FirstName, u.LastName, ni.Title, ni.ItemDate, uias.NewscatalystItemCount,uias.NewsletterItemCount FROM newsitem as ni, useritemaccessstats as uias, user as u where uias.timeOfAccess BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.userId = "+userid+" and u.userid = uias.userid and ni.newsitemid = uias.newsitemid order by "+columnname+" "+mode+" LIMIT "+startRecord+","+pagesize+"";	
			}
			ResultSet rss = stmt.executeQuery(query);
			
			while(rss.next()){
				userItemAccessStats = new UserItemAccessStats();
				userItemAccessStats.setFirstname(rss.getString("FirstName"));
				userItemAccessStats.setLastname(rss.getString("LastName"));
				userItemAccessStats.setNewsitemtitle(rss.getString("Title"));
				userItemAccessStats.setNewsitemdate(rss.getDate("ItemDate"));
				userItemAccessStats.setNewscatalystitemcount(rss.getInt("NewscatalystItemCount"));
				userItemAccessStats.setNewsletteritemcount(rss.getInt("NewsletterItemCount"));
				userlist.add(userItemAccessStats);
			}
			rss.close();
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(userlist);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("problem in getUserInformation()");
		}
		return pageresult;
	}
	
	public PageResult getLoginStatistics(PageCriteria crt, int userid, int industryid) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		ArrayList<UserLoginStats> list = new ArrayList<UserLoginStats>();
		String query;
		if(userid == -1){
		    query = "select * from loginStatistics where timeOfLogin BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and industryEnumId = "+industryid+"";
		}
		else{
			query = "select * from loginStatistics where timeOfLogin BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and userId = "+userid+"";	
		}
		try {
			stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			listSize = rs.last() ? rs.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
			
			
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			if(userid == -1){
			  query = "select * from loginStatistics where timeOfLogin BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and industryEnumId = "+industryid+" order by timeOfLogin desc LIMIT "+startRecord+","+pagesize+"";
			}
			else{
				query = "select * from loginStatistics where timeOfLogin BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and userId = "+userid+" order by timeOfLogin desc LIMIT "+startRecord+","+pagesize+"";	
			}
			ResultSet rss = stmt.executeQuery(query);
			while(rss.next()){
				UserLoginStats userloginstats = new UserLoginStats();
				userloginstats.setUserid(rss.getInt("userId"));
				userloginstats.setTimeOfLogin(rss.getTimestamp("timeOfLogin"));
				
				query = "select * from user where UserId = "+userloginstats.getUserid()+"";
				stmt = getConnection().createStatement();
				ResultSet rs1 = stmt.executeQuery(query);
				while(rs1.next()){
				 userloginstats.setEmail(rs1.getString("email"));
				 userloginstats.setUsername(rs1.getString("FirstName")+ " "+rs1.getString("LastName"));
				 int roleid = rs1.getInt("isAdmin");
				 if(roleid == 0)
					 userloginstats.setRole("Subscriber");
				 else if(roleid == 1)
					 userloginstats.setRole("Admin");
				 else if(roleid == 2)
					 userloginstats.setRole("Executive");
				 else if(roleid == 3)
					 userloginstats.setRole("Trial");
				 
				}
				rs1.close();
				list.add(userloginstats);
			}
			rss.close();
			stmt.close();
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(list);
			closeConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pageresult;
	}

	public PageResult getSearchLoginStatistics(PageCriteria crt, int userid, int industryid, String searchColumn, String searchString) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		String query = null;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		int industryenumid = 0;
		ArrayList<UserLoginStats> list = new ArrayList<UserLoginStats>();
			
		if(searchColumn.equals("timeOfLogin")){
		   String[] dateSplit = searchString.split(","); 
		   if(userid == -1){
		       query = "select * from loginStatistics where industryEnumId = "+industryid+" and  DATE(timeOfLogin)>='"+dateSplit[0]+"' and DATE(timeOfLogin)<='"+dateSplit[1]+"'";
		   }
		   else{
			   query = "select * from loginStatistics where industryEnumId = "+industryid+" and userId = "+userid+" and  DATE(timeOfLogin)>='"+dateSplit[0]+"' and DATE(timeOfLogin)<='"+dateSplit[1]+"'";
		   }
		}
		else {
		   query = "select * from loginStatistics where timeOfLogin BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and industryEnumId = "+industryid+" and userId in (select UserId from user where "+searchColumn+" like '%"+searchString+"%')";
		}
		
		try {
			stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			listSize = rs.last() ? rs.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
			
			
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			
			if(searchColumn.equals("timeOfLogin")){
			  String[] dateSplit = searchString.split(","); 
			  if(userid == -1){
		 	      query = "select * from loginStatistics where industryEnumId = "+industryid+" and  DATE(timeOfLogin)>='"+dateSplit[0]+"' and DATE(timeOfLogin)<='"+dateSplit[1]+"' order by timeOfLogin asc LIMIT "+startRecord+","+pagesize+"";
			  }
			  else{
				  query = "select * from loginStatistics where industryEnumId = "+industryid+" and userid = "+userid+" and DATE(timeOfLogin)>='"+dateSplit[0]+"' and DATE(timeOfLogin)<='"+dateSplit[1]+"' order by timeOfLogin asc LIMIT "+startRecord+","+pagesize+"";
			  }
			}
			else {
			  query = "select * from loginStatistics where timeOfLogin BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and industryEnumId = "+industryid+" and userId in (select UserId from user where "+searchColumn+" like '%"+searchString+"%') order by timeOfLogin desc LIMIT "+startRecord+","+pagesize+"";
			}
			ResultSet rss = stmt.executeQuery(query);
			while(rss.next()){
				UserLoginStats userloginstats = new UserLoginStats();
				userloginstats.setUserid(rss.getInt("userId"));
				userloginstats.setTimeOfLogin(rss.getTimestamp("timeOfLogin"));
				
				query = "select * from user where UserId = "+userloginstats.getUserid()+"";
				stmt = getConnection().createStatement();
				ResultSet rs1 = stmt.executeQuery(query);
				while(rs1.next()){
				 userloginstats.setEmail(rs1.getString("email"));
				 userloginstats.setUsername(rs1.getString("FirstName")+ " "+rs1.getString("LastName"));
				 int roleid = rs1.getInt("isAdmin");
				 if(roleid == 0)
					 userloginstats.setRole("Subscriber");
				 else if(roleid == 1)
					 userloginstats.setRole("Admin");
				 else if(roleid == 2)
					 userloginstats.setRole("Executive");
				 else if(roleid == 3)
					 userloginstats.setRole("Trial");
				}
				rs1.close();
				list.add(userloginstats);
			}
			rss.close();
			stmt.close();
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(list);
			closeConnection();
		  }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pageresult;
	}
	
	public PageResult getSortedLoginStatistics(PageCriteria crt, int userid, int industryid, String sortcolumn, String sortmode) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		ArrayList<UserLoginStats> list = new ArrayList<UserLoginStats>();
		String query;
		if(userid == -1){
		    query = "SELECT u.userId,u.FirstName,u.LastName,u.email,l.timeOflogin FROM loginStatistics l, user  u where timeOfLogin BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and l.industryEnumId = "+industryid+" and l.UserId = u.UserId order by "+sortcolumn+" "+sortmode+"";
		}
		else{
			query = "SELECT u.userId,u.FirstName,u.LastName,u.email,l.timeOflogin FROM loginStatistics l, user  u where timeOfLogin BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and l.userId = "+userid+" and l.UserId = u.UserId order by "+sortcolumn+" "+sortmode+"";
		}
		try {
			stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			listSize = rs.last() ? rs.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
			
			
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			if(userid == -1){
			    query = "SELECT u.userId,u.isAdmin,u.FirstName,u.LastName,u.email,l.timeOflogin FROM loginStatistics l, user  u where timeOfLogin BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and l.industryEnumId = "+industryid+" and l.UserId = u.UserId order by "+sortcolumn+" "+sortmode+" LIMIT "+startRecord+","+pagesize+"";
			}
			else{
				query = "SELECT u.userId,u.isAdmin,u.FirstName,u.LastName,u.email,l.timeOflogin FROM loginStatistics l, user  u where timeOfLogin BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and l.userId = "+userid+" and l.UserId = u.UserId order by "+sortcolumn+" "+sortmode+" LIMIT "+startRecord+","+pagesize+"";
			}
			ResultSet rss = stmt.executeQuery(query);
			while(rss.next()){
				UserLoginStats userloginstats = new UserLoginStats();
				userloginstats.setUserid(rss.getInt("userId"));
				userloginstats.setTimeOfLogin(rss.getTimestamp("timeOfLogin"));
				userloginstats.setUsername(rss.getString("FirstName")+" "+rss.getString("LastName"));
				userloginstats.setEmail(rss.getString("email"));
				int roleid = rss.getInt("isAdmin");
				 if(roleid == 0)
					 userloginstats.setRole("Subscriber");
				 else if(roleid == 1)
					 userloginstats.setRole("Admin");
				 else if(roleid == 2)
					 userloginstats.setRole("Executive");
				 else if(roleid == 3)
					 userloginstats.setRole("Trial");
				list.add(userloginstats);
			}
			rss.close();
			stmt.close();
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(list);
			closeConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pageresult;
	}
}
