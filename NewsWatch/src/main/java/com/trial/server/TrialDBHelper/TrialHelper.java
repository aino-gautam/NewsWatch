package com.trial.server.TrialDBHelper;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.admin.server.MailClient;
import com.common.client.PageResult;
import com.login.client.UserInformation;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.server.db.NewsletterHelper;
import com.trial.client.TrialAccount;

public class TrialHelper {
	
	private Connection conn = null;
	private String driverClassName;
	private String connectionUrl;
	private String username;
	private String password;
	protected String smtpusername;
	protected String smtppassword;
	protected String smtphost;
	protected String smtpport;
	private HttpServletRequest request = null;
	private ServletContext context;
	private ServletConfig config;
	
	
	public TrialHelper(HttpServletRequest req,ServletContext context,ServletConfig config,String connectionUrl, String driverClassName, String username, String password)
	{
		try 
		{
			this.request = req;
			this.context = context;
			this.config = config;
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
	
	public HashMap<Integer,String> getCatalyst(){
		HashMap<Integer,String> map = new HashMap<Integer, String>();
		String query = "select IndustryEnumId,Name from industryenum where IndustryEnumId != 3";
		try {
			Statement stmt = conn.createStatement() ;
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				map.put(rs.getInt("IndustryEnumId"), rs.getString("Name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
		
	}
	
	public UserInformation getuserinformation(int userid,String email)
	{
		UserInformation user = new UserInformation();
		try
		{
			String query = "SELECT u.isAdmin, u.FirstName, u.LastName, u.email,u.createdBy, u.signature, us.Period, us.NewsFilterMode from user u, usersubscription us where u.UserId = us.UserId and u.email= '"+email+"'";
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				user.setIsAdmin(rs.getInt("isAdmin"));
				user.setFirstname(rs.getString("FirstName"));
				user.setLastname(rs.getString("LastName"));
				user.setEmail(rs.getString("email"));
				user.setPeriod(rs.getInt("Period"));
				user.setNewsFilterMode(rs.getInt("NewsFilterMode"));
				user.setUserId(userid);
				user.setUserCreatedBy(rs.getInt("createdBy"));
				user.setSignature(rs.getString("signature"));
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return user;
	}
	
	public boolean createTrialAccount(TrialAccount trialuserinfo,String mailformat,String subject){
		String query;
		int userid = 0;
		Timestamp usercreationdate = null;
		String subscriptionDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd"); 
		try{
			HttpSession session = request.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			Statement stmt = getConnection().createStatement();
					
			query = "insert into user(email, password, isAdmin, isApproved, IndustryEnumId, FirstName, LastName,createdBy) " +
			"values('"+trialuserinfo.getUseremail()+"','123qwe',3,1,"+trialuserinfo.getNewscenterid()+",'"+trialuserinfo.getFirstname()+"','"+trialuserinfo.getLastname()+"',"+trialuserinfo.getCreatedBy()+")";
			context.log(query);
			stmt.executeUpdate(query);
			
			query = "select UserId,UserCreationDate from user where email = '"+trialuserinfo.getUseremail()+"'";
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
			 userid = rs.getInt("UserId");	
			 usercreationdate = rs.getTimestamp("UserCreationDate");
			}
			 if(trialuserinfo.isEmailAlert())
				 subscriptionDate = "'"+sdfdate.format(new Date())+"'";
			 else
				 subscriptionDate = null;
           				 
			 query = "insert into usersubscription(UserId,Duration,NewsCenterId,ApprovalDate,isSubscribed,Period,SubscriptionDate,NewsFilterMode,DurationLeft) " +
		 		"values('"+userid+"',3,"+trialuserinfo.getNewscenterid()+",'"+sdfdate.format(new Date())+"',"+trialuserinfo.isEmailAlert()+",1,"+subscriptionDate+",0,3)";
			 stmt.executeUpdate(query);
			 
			 DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 query = "insert into useraccounthistory(userID,operation,operationDate,operationPerformBy) values("+userid+",'create','"+df.format(new Date())+"',"+userInformation.getUserId()+")";
			 stmt.executeUpdate(query);
			 
			 query = "SELECT TagItemId FROM tagitem t where ParentId in( select TagItemId from tagitem where name like '%Countr%' and IndustryId = "+trialuserinfo.getNewscenterid()+")";
			 stmt = getConnection().createStatement();
			 ResultSet rss = stmt.executeQuery(query);
			 while(rss.next()){
				 Statement stmt1 = getConnection().createStatement();
				 query = "insert into usertagselection(UserId,TagItemId,NewsCenterId,TagSelectionStatus) values("+userid+","+rss.getInt("TagItemId")+","+trialuserinfo.getNewscenterid()+",1)";
				 stmt1.executeUpdate(query);
			 }
			 
			 stmt.close();
			 
			 MailClient mailclient = new MailClient(getSmtpusername(),getSmtppassword(),getSmtpport(),getSmtphost());
			 mailclient.sendMessage("no_reply@newscatalyst.com", trialuserinfo.getUseremail(), subject, mailformat);
			 
			 
			 NewsletterHelper newsletterhelper = new NewsletterHelper(context,config);
			 newsletterhelper.fetchNewsForSingleUser(userid, usercreationdate);
			 closeConnection();
			 return true;
		}
		catch(Exception e){
			e.printStackTrace();
			context.log("In catch: "+e.getLocalizedMessage());
			return false;
		}
		
	}

	public PageResult getTrialAccounts(PageCriteria crt) {
		PageResult pageresult = new PageResult();
		ArrayList list = new ArrayList();
		String query;
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		int durationLeft;
		try{
			stmt = getConnection().createStatement();
			query = "SELECT u.UserId,u.firstname,u.lastname,u.email,u.Signature,us.ApprovalDate,us.DurationLeft,us.NewsCenterId,u.createdBy,us.isSubscribed,u.UserCreationDate FROM usersubscription as us, user as u where u.UserId = us.UserId and u.isAdmin = 3 order by u.UserCreationDate desc";
			ResultSet rss = stmt.executeQuery(query);
			listSize = rss.last() ? rss.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
			
			
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			
			
			query = "SELECT u.UserId ,u.firstname,u.lastname,u.email,u.password,u.Signature,us.isExtended,us.ExtensionDate,us.ApprovalDate,us.DurationLeft,us.NewsCenterId,u.createdBy,us.isSubscribed,u.UserCreationDate FROM usersubscription as us, user as u where u.UserId = us.UserId and u.isAdmin = 3 order by u.UserCreationDate desc LIMIT "+startRecord+","+pagesize+";";
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				TrialAccount trialaccount = new TrialAccount();
				trialaccount.setUserid(rs.getInt("UserId"));
				trialaccount.setStartdate(rs.getTimestamp("UserCreationDate"));
				trialaccount.setPassword(rs.getString("password"));
				durationLeft = rs.getInt("DurationLeft");
				trialaccount.setDurationLeft(durationLeft);
				Calendar cal = Calendar.getInstance();
				cal.set(rs.getTimestamp("UserCreationDate").getYear() + 1900, rs.getTimestamp("UserCreationDate").getMonth(), rs.getTimestamp("UserCreationDate").getDate(),rs.getTimestamp("UserCreationDate").getHours(),rs.getTimestamp("UserCreationDate").getMinutes(),rs.getTimestamp("UserCreationDate").getSeconds());
				cal.add(Calendar.DATE, 3);
				
				trialaccount.setEnddate(cal.getTime());
				trialaccount.setEmailAlert(rs.getBoolean("isSubscribed"));
				trialaccount.setCreatedOn(rs.getDate("UserCreationDate"));
				trialaccount.setSignature(rs.getString("Signature"));
				int createdBy = rs.getInt("createdBy");
				int newscenterid = rs.getInt("NewsCenterId");
				
				String name = rs.getString("firstname")+ " " +rs.getString("lastname");
				trialaccount.setFirstname(rs.getString("firstname"));
				trialaccount.setLastname(rs.getString("lastname"));
				trialaccount.setUsername(name);
				trialaccount.setUseremail(rs.getString("email"));
			
					
				query = "select firstname,lastname from user where UserId = "+createdBy+"";
				Statement stmt2 = getConnection().createStatement();
				ResultSet rs2 = stmt2.executeQuery(query);
				while(rs2.next()){
					String salesExecutiveName = rs2.getString("firstname")+ " " +rs2.getString("lastname");
					trialaccount.setSalesExecutiveName(salesExecutiveName);
				}
				rs2.close();
				
				query = "select * from industryenum where IndustryEnumId = "+newscenterid+"";
				Statement stmt3 = getConnection().createStatement();
				ResultSet rs3 = stmt3.executeQuery(query);
				while(rs3.next()){
					trialaccount.setNewscenterid(newscenterid);
					trialaccount.setNewscatalystName(rs3.getString("Name"));
				}
				rs3.close();
				
				list.add(trialaccount);
		
			}
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(list);
			rs.close();
			closeConnection();
			}
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
		return pageresult;
	}

	public PageResult getSortTrialAccount(PageCriteria crt, String columname,String mode) {
		PageResult pageresult = new PageResult();
		ArrayList list = new ArrayList();
		String query;
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		int durationLeft;
		try{
			stmt = getConnection().createStatement();
			query = "SELECT u.UserId,u.firstname,u.lastname,u.email,us.ApprovalDate,us.DurationLeft,us.NewsCenterId,u.createdBy,us.isSubscribed,u.UserCreationDate FROM usersubscription as us, user as u where us.NewsCenterId > 3 and u.UserId = us.UserId and u.isAdmin = 3 order by "+columname+" "+mode+"";
			ResultSet rss = stmt.executeQuery(query);
			listSize = rss.last() ? rss.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
			
			
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			
			
			query = "SELECT u.UserId,u.firstname,u.lastname,u.email,u.password,u.Signature,us.isExtended,us.ExtensionDate,us.ApprovalDate,us.DurationLeft,us.NewsCenterId,u.createdBy,us.isSubscribed,u.UserCreationDate FROM usersubscription as us, user as u where us.NewsCenterId > 3 and u.UserId = us.UserId and u.isAdmin = 3 order by "+columname+" "+mode+" LIMIT "+startRecord+","+pagesize+";";
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				TrialAccount trialaccount = new TrialAccount();
				trialaccount.setUserid(rs.getInt("UserId"));
				trialaccount.setStartdate(rs.getTimestamp("UserCreationDate"));
				durationLeft = rs.getInt("DurationLeft");
				trialaccount.setDurationLeft(durationLeft);
				Calendar cal = Calendar.getInstance();
				cal.set(rs.getTimestamp("UserCreationDate").getYear() + 1900, rs.getTimestamp("UserCreationDate").getMonth(), rs.getTimestamp("UserCreationDate").getDate(),rs.getTimestamp("UserCreationDate").getHours(),rs.getTimestamp("UserCreationDate").getMinutes(),rs.getTimestamp("UserCreationDate").getSeconds());
				cal.add(Calendar.DATE, 3);
				
				trialaccount.setEnddate(cal.getTime());
				trialaccount.setEmailAlert(rs.getBoolean("isSubscribed"));
				trialaccount.setCreatedOn(rs.getDate("UserCreationDate"));
				trialaccount.setPassword(rs.getString("password"));
				int createdBy = rs.getInt("createdBy");
				int newscenterid = rs.getInt("NewsCenterId");
				
				String name = rs.getString("firstname")+ " " +rs.getString("lastname");
				trialaccount.setUsername(name);
				trialaccount.setUseremail(rs.getString("email"));
			    trialaccount.setSignature(rs.getString("Signature"));
					
				query = "select firstname,lastname from user where UserId = "+createdBy+"";
				Statement stmt2 = getConnection().createStatement();
				ResultSet rs2 = stmt2.executeQuery(query);
				while(rs2.next()){
					String salesExecutiveName = rs2.getString("firstname")+ " " +rs2.getString("lastname");
					trialaccount.setSalesExecutiveName(salesExecutiveName);
				}
				rs2.close();
				
				query = "select * from industryenum where IndustryEnumId = "+newscenterid+"";
				Statement stmt3 = getConnection().createStatement();
				ResultSet rs3 = stmt3.executeQuery(query);
				while(rs3.next()){
					trialaccount.setNewscenterid(newscenterid);
					trialaccount.setNewscatalystName(rs3.getString("Name"));
				}
				rs3.close();
				
				list.add(trialaccount);
		
			}
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(list);
			rs.close();
			closeConnection();
			}
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
		return pageresult;
	}

	public PageResult getSearchData(PageCriteria crt, String columname,	String searchString) {
		PageResult pageresult = new PageResult();
		ArrayList list = new ArrayList();
		String query;
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		int durationLeft;
		try{
			stmt = getConnection().createStatement();
			
			if(columname.equals("firstName")){
				String[] split = searchString.split(" ");
				int len = split.length;
	            if(len == 1){
				    query = "SELECT u.UserId,u.firstname,u.lastname,u.email,u.Signature,us.ApprovalDate,us.DurationLeft,us.NewsCenterId,u.createdBy,us.isSubscribed,u.UserCreationDate FROM usersubscription as us, user as u where us.NewsCenterId > 3 and u.userid = us.userid and u.isAdmin = 3 and us.UserId in (SELECT  distinct(u.userid) FROM usersubscription as us, user as u where u.UserId = us.UserId and u.isAdmin = 3 and firstName like '%"+searchString+"%' or lastName like '%"+searchString+"%')";
	            }
	            else{
	            	query = "SELECT u.UserId,u.firstname,u.lastname,u.email,u.Signature,us.ApprovalDate,us.DurationLeft,us.NewsCenterId,u.createdBy,us.isSubscribed,u.UserCreationDate FROM usersubscription as us, user as u where us.NewsCenterId > 3 and u.userid = us.userid and u.isAdmin = 3 and us.UserId in (SELECT  distinct(u.userid) FROM usersubscription as us, user as u where u.UserId = us.UserId and u.isAdmin = 3 and firstName like '%"+split[0]+"%' or lastName like '%"+split[1]+"%')";	
	            }
			}
			else{
			    query = "SELECT u.UserId,u.firstname,u.lastname,u.email,u.Signature,us.ApprovalDate,us.DurationLeft,us.NewsCenterId,u.createdBy,us.isSubscribed,u.UserCreationDate FROM usersubscription as us, user as u where us.NewsCenterId > 3 and u.UserId = us.UserId and u.isAdmin = 3 and "+columname+" like '%"+searchString+"%'";
			}
			
			ResultSet rss = stmt.executeQuery(query);
			listSize = rss.last() ? rss.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
			
			
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			
			if(columname.equals("firstName")){
				String[] split = searchString.split(" ");
				int len = split.length;
	            if(len == 1){
				  query = "SELECT u.UserId,u.firstname,u.lastname,u.email,u.password,u.Signature,us.ApprovalDate,us.DurationLeft,us.NewsCenterId,u.createdBy,us.isSubscribed,u.UserCreationDate FROM usersubscription as us, user as u where us.NewsCenterId > 3 and u.isAdmin = 3 and u.userid = us.userid and us.UserId in (SELECT  distinct(u.userid) FROM usersubscription as us, user as u where u.UserId = us.UserId and u.isAdmin = 3 and firstName like '%"+searchString+"%' or lastName like '%"+searchString+"%')";
	            }
	            else{
	            	query = "SELECT u.UserId,u.firstname,u.lastname,u.email,u.password,u.Signature,us.ApprovalDate,us.DurationLeft,us.NewsCenterId,u.createdBy,us.isSubscribed,u.UserCreationDate FROM usersubscription as us, user as u where us.NewsCenterId > 3 and u.isAdmin = 3 and u.userid = us.userid and us.UserId in (SELECT  distinct(u.userid) FROM usersubscription as us, user as u where u.UserId = us.UserId and u.isAdmin = 3 and firstName like '%"+split[0]+"%' or lastName like '%"+split[1]+"%')";	
	            }
			}
			else{
			    query = "SELECT u.UserId,u.firstname,u.lastname,u.email,u.password,u.Signature,us.ApprovalDate,us.DurationLeft,us.NewsCenterId,u.createdBy,us.isSubscribed,u.UserCreationDate FROM usersubscription as us, user as u where us.NewsCenterId > 3 and u.UserId = us.UserId and u.isAdmin = 3 and "+columname+" like '%"+searchString+"%' LIMIT "+startRecord+","+pagesize;
			}
			
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				TrialAccount trialaccount = new TrialAccount();
				trialaccount.setUserid(rs.getInt("UserId"));
				trialaccount.setStartdate(rs.getTimestamp("UserCreationDate"));
				trialaccount.setPassword(rs.getString("password"));
				durationLeft = rs.getInt("DurationLeft");
				trialaccount.setDurationLeft(durationLeft);
				Calendar cal = Calendar.getInstance();
				cal.set(rs.getTimestamp("UserCreationDate").getYear() + 1900, rs.getTimestamp("UserCreationDate").getMonth(), rs.getTimestamp("UserCreationDate").getDate(),rs.getTimestamp("UserCreationDate").getHours(),rs.getTimestamp("UserCreationDate").getMinutes(),rs.getTimestamp("UserCreationDate").getSeconds());
				cal.add(Calendar.DATE, 3);
				
				trialaccount.setEnddate(cal.getTime());
				trialaccount.setEmailAlert(rs.getBoolean("isSubscribed"));
				trialaccount.setCreatedOn(rs.getDate("UserCreationDate"));
				trialaccount.setSignature(rs.getString("Signature"));
				int createdBy = rs.getInt("createdBy");
				int newscenterid = rs.getInt("NewsCenterId");
				
				String name = rs.getString("firstname")+ " " +rs.getString("lastname");
				trialaccount.setFirstname(rs.getString("firstname"));
				trialaccount.setLastname(rs.getString("lastname"));
				trialaccount.setUsername(name);
				trialaccount.setUseremail(rs.getString("email"));
			
					
				query = "select firstname,lastname from user where UserId = "+createdBy+"";
				Statement stmt2 = getConnection().createStatement();
				ResultSet rs2 = stmt2.executeQuery(query);
				while(rs2.next()){
					String salesExecutiveName = rs2.getString("firstname")+ " " +rs2.getString("lastname");
					trialaccount.setSalesExecutiveName(salesExecutiveName);
				}
				rs2.close();
				
				query = "select * from industryenum where IndustryEnumId = "+newscenterid+"";
				Statement stmt3 = getConnection().createStatement();
				ResultSet rs3 = stmt3.executeQuery(query);
				while(rs3.next()){
					trialaccount.setNewscenterid(newscenterid);
					trialaccount.setNewscatalystName(rs3.getString("Name"));
				}
				rs3.close();
				
				list.add(trialaccount);
		
			}
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(list);
			rs.close();
			closeConnection();
			}
				
				
				
				
				/*TrialAccount trialaccount = new TrialAccount();
				trialaccount.setUserid(rs.getInt("UserId"));
				trialaccount.setStartdate(rs.getTimestamp("UserCreationDate"));
				durationLeft = rs.getInt("DurationLeft");
				trialaccount.setDurationLeft(durationLeft);
				Calendar cal = Calendar.getInstance();
				cal.set(rs.getTimestamp("UserCreationDate").getYear() + 1900, rs.getTimestamp("UserCreationDate").getMonth(), rs.getTimestamp("UserCreationDate").getDate(),rs.getTimestamp("UserCreationDate").getHours(),rs.getTimestamp("UserCreationDate").getMinutes(),rs.getTimestamp("UserCreationDate").getSeconds());
				cal.add(Calendar.DATE, 3);
				
				trialaccount.setEnddate(cal.getTime());
				trialaccount.setEmailAlert(rs.getBoolean("isSubscribed"));
				trialaccount.setCreatedOn(rs.getDate("UserCreationDate"));
				trialaccount.setPassword(rs.getString("password"));
				int createdBy = rs.getInt("createdBy");
				int newscenterid = rs.getInt("NewsCenterId");
				
				String name = rs.getString("firstname")+ " " +rs.getString("lastname");
				trialaccount.setUsername(name);
				trialaccount.setUseremail(rs.getString("email"));
			    trialaccount.setSignature(rs.getString("Signature"));
					
				query = "select firstname,lastname from user where UserId = "+createdBy+"";
				Statement stmt2 = getConnection().createStatement();
				ResultSet rs2 = stmt2.executeQuery(query);
				while(rs2.next()){
					String salesExecutiveName = rs2.getString("firstname")+ " " +rs2.getString("lastname");
					trialaccount.setSalesExecutiveName(salesExecutiveName);
				}
				rs2.close();
				
				query = "select * from industryenum where IndustryEnumId = "+newscenterid+"";
				Statement stmt3 = getConnection().createStatement();
				ResultSet rs3 = stmt3.executeQuery(query);
				while(rs3.next()){
					trialaccount.setNewscenterid(newscenterid);
					trialaccount.setNewscatalystName(rs3.getString("Name"));
				}
				rs3.close();
				
				list.add(trialaccount);
		
			}
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(list);
			rs.close();
			closeConnection();
			}*/
			
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		
		return pageresult;
	}

	public void updateTrialAccountDuration(ArrayList<TrialAccount> trialaccountlist) {
		try{
			Iterator iter = trialaccountlist.iterator();
			while(iter.hasNext()){
				TrialAccount user = (TrialAccount)iter.next();
				int value = user.getDuration() - user.getDurationLeft();
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			    Date date = new Date();
			    String extendedDate = format.format(date);
			    
			    String querydl = "select DurationLeft from usersubscription where UserId ="+user.getUserid();
			    Statement stmtdl = conn.createStatement();
			    ResultSet rs = stmtdl.executeQuery(querydl);
			    int durationLeft = 0;
			    while(rs.next()){
			     durationLeft = rs.getInt("DurationLeft");
			    }
			    String query;
			    Statement stmt=conn.createStatement();
			    /*if(durationLeft > 0){
			    	durationLeft = user.getDuration();
			    	query ="update usersubscription set Duration = Duration + "+value+",DurationLeft = "+durationLeft+",ExtensionDate = '"+extendedDate+"', isExtended = "+user.isExtension()+", ExtendedDuration = "+durationLeft+" where UserId ="+user.getUserid();
			    }
			    else*/
				    query ="update usersubscription set Duration = Duration + "+value+",DurationLeft = "+user.getDuration()+",ExtensionDate = '"+extendedDate+"', isExtended = "+user.isExtension()+", ExtendedDuration = "+user.getDuration()+" where UserId ="+user.getUserid();
    				stmt.executeUpdate(query);
    				
    				HttpSession session = request.getSession(false);
    				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
    				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    				query = "insert into useraccounthistory(userID,operation,operationDate,operationPerformBy,extendDuration) values("+user.getUserid()+",'extends','"+df.format(new Date())+"',"+userInformation.getUserId()+","+user.getDuration()+")";
    				stmt.executeUpdate(query);
				
				
				stmt.close();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void deleteTrialAccount(TrialAccount trialaccount) {
		try{
			String query;
			Statement stmt = getConnection().createStatement();
			query = "delete from loginStatistics where userId = "+trialaccount.getUserid()+"";
			stmt.executeUpdate(query);
			query = "delete from useraccounthistory where userId = "+trialaccount.getUserid()+"";
			stmt.executeUpdate(query);
			query = "delete from useritemaccessstats where userId = "+trialaccount.getUserid()+"";
			stmt.executeUpdate(query);
			query = "delete from usersubscription where userId = "+trialaccount.getUserid()+"";
			stmt.executeUpdate(query);
			query = "delete from usertagselection where userId = "+trialaccount.getUserid()+"";
			stmt.executeUpdate(query);
			query = "delete from user where userId = "+trialaccount.getUserid()+"";
			stmt.executeUpdate(query);
			
			closeConnection(); 
		}
 		catch(Exception e){
            e.printStackTrace();			
		}
		
	}

	public String getSmtpusername() {
		return smtpusername;
	}

	public void setSmtpusername(String smtpusername) {
		this.smtpusername = smtpusername;
	}

	public String getSmtppassword() {
		return smtppassword;
	}

	public void setSmtppassword(String smtppassword) {
		this.smtppassword = smtppassword;
	}

	public String getSmtphost() {
		return smtphost;
	}

	public void setSmtphost(String smtphost) {
		this.smtphost = smtphost;
	}

	public String getSmtpport() {
		return smtpport;
	}

	public void setSmtpport(String smtpport) {
		this.smtpport = smtpport;
	}

	public void reinitiateTrialAccount(TrialAccount user) {
	  String query;
	  try{
		Statement stmt=conn.createStatement();
	  	DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date = new Date();
	    String extendedDate = format.format(date);
	       
	    query ="update usersubscription set Duration = 3,DurationLeft = 3,ExtensionDate = '"+extendedDate+"', isExtended = 1, ExtendedDuration = 3 where UserId ="+user.getUserid();
		stmt.executeUpdate(query);
			
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		query = "insert into useraccounthistory(userID,operation,operationDate,operationPerformBy,extendDuration) values("+user.getUserid()+",'extends','"+df.format(new Date())+"',"+userInformation.getUserId()+",3)";
		stmt.executeUpdate(query);
		
		Date dt = df.parse(df.format(new Date())); 
		Timestamp timestamp = new java.sql.Timestamp(dt.getTime()); 
		sendMails(user,timestamp,null); 
		
	  }
	  catch(Exception e){
		  e.printStackTrace();
	  }
	  
	}
	
	private void sendMails(TrialAccount trialuserinfo, Timestamp usercreationdate,String mailformat){
		try{
		MailClient mailclient;
		if(mailformat != null){
		    mailclient = new MailClient(getSmtpusername(),getSmtppassword(),getSmtpport(),getSmtphost());
		}
		else{
			mailformat = getEmailTemplate(trialuserinfo.getNewscenterid());
			mailformat = mailformat.replace("#*username*#", trialuserinfo.getFirstname());
			mailformat = mailformat.replace("#*emailusername*#", trialuserinfo.getUseremail());
			HttpSession session = request.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			mailformat = mailformat.replace("#*executive*#", userInformation.getFirstname()+" "+userInformation.getLastname());
			mailformat = mailformat.replace("#*signature*#", userInformation.getSignature());
			mailclient = new MailClient(getSmtpusername(),getSmtppassword(),getSmtpport(),getSmtphost());
		}
		 mailclient.sendMessage("no_reply@newscatalyst.com", trialuserinfo.getUseremail(), "Trial account credentials", mailformat);
		 
		/* String query = "select * from usertagselection where userId = "+trialuserinfo.getUserid()+"";
		 Statement stmt = conn.createStatement();
		 ResultSet rs = stmt.executeQuery(query);
		 boolean data = rs.next();
		 if(data){
			 NewsletterHelper newsletterhelper = new NewsletterHelper(context,config);
			 newsletterhelper.fetchNewsForSingleUser(trialuserinfo.getUserid(), usercreationdate);
		 }
		 else{
			 query = "SELECT TagItemId FROM tagitem t where ParentId in( select TagItemId from tagitem where name like '%Country%' and IndustryId = "+trialuserinfo.getNewscenterid()+")";
			 stmt = conn.createStatement();
			 ResultSet rss = stmt.executeQuery(query);
			 while(rss.next()){
				 Statement stmt1 = conn.createStatement();
				 query = "insert into usertagselection(UserId,TagItemId,NewsCenterId,TagSelectionStatus) values("+trialuserinfo.getUserid()+","+rss.getInt("TagItemId")+","+trialuserinfo.getNewscenterid()+",1)";
				 stmt1.executeUpdate(query);
			 }
			 
			 NewsletterHelper newsletterhelper = new NewsletterHelper(context,config);
			 newsletterhelper.fetchNewsForSingleUser(trialuserinfo.getUserid(), usercreationdate);
		 }*/
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getEmailTemplate(int newscenterid) {
		String emailTemplate = null;
		try {
			Statement stmt = conn.createStatement();
			String query = "select emailTemplate from industryenum as ie,newscenter as n where ie.IndustryEnumId = n.IndustryEnumId and n.NewsCenterId = "+newscenterid+"";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
            	emailTemplate = rs.getString("emailTemplate");
            }
          
		} catch (Exception e) {
			e.printStackTrace();
		}
		String strbuilder = new String(emailTemplate);
		
		return strbuilder;
	}
}
