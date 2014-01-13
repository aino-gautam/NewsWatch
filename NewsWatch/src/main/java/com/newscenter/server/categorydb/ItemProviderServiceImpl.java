package com.newscenter.server.categorydb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.login.client.UserInformation;
import com.login.server.db.AllocateResources;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.ItemProviderService;
import com.newscenter.client.tags.TagItem;
import com.newscenter.client.ui.MainNewsPresenter;
import com.newscenter.server.db.DBHelper;
import com.newscenter.server.exception.CategoryHelperException;
import com.newscenter.server.newsdb.NewsProviderServiceImpl;
import com.newscenter.server.newsdb.newsdbhelper.NewsItemHelper;


public class ItemProviderServiceImpl extends RemoteServiceServlet implements ItemProviderService {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	String connectionUrl;
	String driverClassName;
	String username;
	String password;
	protected String tomcatpath;
	public static String CURRENTMAP = "currentCategoryMap";
	private int userselectedncid;
	private int user;
	private ServletConfig servletconfig;
	private ServletContext context;
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		servletconfig = config;
		//ServletContext context=getServletContext();
		context=getServletContext();
		connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(AllocateResources.USERNAME);
		password =(String)context.getAttribute(AllocateResources.PASSWORD);
		tomcatpath = (String)context.getAttribute(AllocateResources.TOMCATPATH);
	}

	public CategoryMap getCategories() {
		try{
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			setUserselectedncid(userInformation.getUserSelectedNewsCenterID());
			setUser(userInformation.getUserId());
			
			DBHelper helper = new DBHelper(connectionUrl,driverClassName,username,password, tomcatpath);
			CategoryHelper cathelper = new CategoryHelper(userselectedncid, user);
			CategoryMap cmap = cathelper.getCategories();
			cathelper.closeConnection();
			return cmap;
		}
		catch (CategoryHelperException e) {
			e.printStackTrace();
			return null;
		}
	}

	public CategoryMap getUserSelectionCategories() {
		try {
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			setUserselectedncid(userInformation.getUserSelectedNewsCenterID());
			setUser(userInformation.getUserId());

			DBHelper helper = new DBHelper(connectionUrl,driverClassName,username,password, tomcatpath);
			//CategoryHelper cathelper = new CategoryHelper(getUserselectedncid(), getUser());
			CategoryHelper cathelper = new CategoryHelper(userInformation.getUserSelectedIndustryID(), getUser());
			session.setAttribute(CURRENTMAP, cathelper.getUserSelectionCategories());
			CategoryMap cmap = cathelper.getUserSelectionCategories();
			cathelper.closeConnection();
			return cmap;
		} 
		catch (CategoryHelperException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void refreshUserSelection(CategoryMap categoryMap) {
		try {
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			int userselectedncid = userInformation.getUserSelectedNewsCenterID();
			int user = userInformation.getUserId();
			//categoryMap = session.getAttribute(CURRENTMAP);
		
			DBHelper helper = new DBHelper(connectionUrl,driverClassName,username,password, tomcatpath);
			CategoryHelper cathelper = new CategoryHelper(userselectedncid, user);
			cathelper.refreshUserSelection(categoryMap);
			session.setAttribute(CURRENTMAP, categoryMap);
			categoryMap.getSelectedTags();
			cathelper.closeConnection();
		} 
		catch (CategoryHelperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateUserItemSelection(TagItem tagItem, boolean selectionStatus) {
		try {
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			int userselectedncid = userInformation.getUserSelectedNewsCenterID();
			int user = userInformation.getUserId();
		
			DBHelper helper = new DBHelper(connectionUrl,driverClassName,username,password, tomcatpath);
			CategoryHelper cathelper = new CategoryHelper(userselectedncid, user);
			cathelper.updateUserItemSelection(tagItem, selectionStatus);
			cathelper.closeConnection();		
		} 
		catch (CategoryHelperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void updateUserItemSelection(ArrayList tagList) {
		try {
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			int userselectedncid = userInformation.getUserSelectedNewsCenterID();
			int user = userInformation.getUserId();
		
			DBHelper helper = new DBHelper(connectionUrl,driverClassName,username,password, tomcatpath);
			CategoryHelper cathelper = new CategoryHelper(userselectedncid, user);
			cathelper.updateUserItemSelection(tagList);
			cathelper.closeConnection();
		} 
		catch (CategoryHelperException e) {
			e.printStackTrace();
		}
	}


	public void updateUserItemSelection(CategoryMap categoryMap) {
		try {
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");

			DBHelper helper = new DBHelper(connectionUrl,driverClassName,username,password, tomcatpath);
			CategoryHelper cathelper = new CategoryHelper(getUserselectedncid(), getUser());
			cathelper.updateUserItemSelection(categoryMap);
			//cathelper.closeConnection();
		} 
		catch (CategoryHelperException e) {
			e.printStackTrace();
		}
	}
	
	public NewsItemList updateSessionCategoryMap(CategoryMap categoryMap, PageCriteria criteria,int newsmode){
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			session.setAttribute(CURRENTMAP, categoryMap);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			int userid = userInformation.getUserId();
			DBHelper helper = new DBHelper(connectionUrl,driverClassName,username,password, tomcatpath);
			context.log("Tomcat path:" +tomcatpath);
			System.out.println("Tomcat path:" +tomcatpath);
			NewsItemHelper newshelper = new NewsItemHelper(categoryMap,tomcatpath);
			NewsItemList newslist = null;
			//if(newsmode == MainNewsPresenter.OR)
				newslist = newshelper.getPage(criteria, newsmode,userid);
				context.log("In updateSessionCategoryMap - IPSI newsreturned: "+newslist.size());
				System.out.println("In updateSessionCategoryMap  - IPSI newsreturned: "+newslist.size());
			//else if(newsmode == MainNewsPresenter.AND)
			//	newslist = newshelper.getAndPage(criteria, newsmode,userid);
			newshelper.closeConnection();
			return newslist;
	}
	
	public HashMap<UserInformation, NewsItemList> getAllUserSelectionMaps(Timestamp datetime,int usrid) {
		try {
			HashMap<UserInformation, NewsItemList> newsMap = new HashMap<UserInformation, NewsItemList>();
			UserInformation user;
			NewsItemList newslist;
			DBHelper helper = new DBHelper(connectionUrl,driverClassName,username,password, tomcatpath);
			
			String query = "";
			String query2 = "select DAYNAME(CURDATE())";
			Statement stmt2 = helper.getConnection().createStatement();
			ResultSet rs2 = stmt2.executeQuery(query2);
			while(rs2.next()){
				String day = rs2.getString(1);
				if(day.equalsIgnoreCase("Saturday")){
				 if(usrid == -1){	
					query = "select UserId,NewsCenterId,Duration,DurationLeft,isSubscribed,Period,SubscriptionDate,NewsFilterMode from usersubscription where isSubscribed= 1";
				 }
				 else{
					 query = "select UserId,NewsCenterId,Duration,DurationLeft,isSubscribed,Period,SubscriptionDate,NewsFilterMode from usersubscription where isSubscribed= 1 and UserId = "+usrid+""; 
				 }
				}
				else{
					if(usrid == -1){
						query = "select UserId,NewsCenterId,Duration,DurationLeft,isSubscribed,Period,SubscriptionDate,NewsFilterMode from usersubscription where isSubscribed= 1 and Period = 1";
					}
					else{
						query = "select UserId,NewsCenterId,Duration,DurationLeft,isSubscribed,Period,SubscriptionDate,NewsFilterMode from usersubscription where isSubscribed= 1 and Period = 1 and UserID = "+usrid+"";
					}
				}
			}
			rs2.close();
			stmt2.close();
			//String query = "select UserId,NewsCenterId,Duration,isSubscribed,Period,SubscriptionDate from usersubscription where isSubscribed= 1";
			Statement stmt = helper.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				int userid = rs.getInt("UserId");
				int ncid =rs.getInt("NewsCenterId");
				int duration = rs.getInt("Duration");
				int durationleft = rs.getInt("DurationLeft");
				int period = rs.getInt("Period");
				int filtermode = rs.getInt("NewsFilterMode");
				
				if(durationleft > 0 ){
					String query1 = "select u.UserId, u.IndustryEnumId, u.email, u.FirstName, u.LastName, ie.Name, ie.Description from user u,industryenum ie where u.IndustryEnumId = ie.IndustryEnumId and u.UserId = "+userid;
					Statement stmt1 = helper.getConnection().createStatement();
					ResultSet rs1 = stmt1.executeQuery(query1);
					while(rs1.next()){
						user = new UserInformation();
						user.setEmail(rs1.getString("email"));
						user.setFirstname(rs1.getString("FirstName"));
						user.setLastname(rs1.getString("LastName"));
						user.setIndustryNewsCenterName(rs1.getString("Name"));
						user.setPeriod(period);
						user.setUserSelectedNewsCenterURL(rs1.getString("Description"));
						user.setUserId(rs1.getInt("UserId"));
						user.setUserSelectedIndustryID(rs1.getInt("IndustryEnumId"));
						
						CategoryHelper cathelper = new CategoryHelper(ncid, userid);
						CategoryMap cmap = cathelper.getAllUserSelection();
						NewsProviderServiceImpl newsprovider = new NewsProviderServiceImpl();
						try {
							newsprovider.init(servletconfig);
						} catch (ServletException e) {
							e.printStackTrace();
						}
						newslist = new NewsItemList();
						newslist = newsprovider.getAllNewsItemsForUser(cmap,userid,filtermode,datetime);
						
						cathelper.closeConnection();
						newsMap.put(user, newslist);
						
					}
					rs1.close();
					stmt1.close();
				}	
			}
			rs.close();
			stmt.close();
			helper.closeConnection();
			return newsMap;
		} 
		catch (CategoryHelperException e) {
			e.printStackTrace();
			return null;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		} 
	}

	public Timestamp getNewsletterDeliveryTime(){
		try{
			Timestamp datetime = null;
			DBHelper helper = new DBHelper(connectionUrl,driverClassName,username,password, tomcatpath);
			String query = "select delivery from newsletterdelivery;";
			Statement stmt = helper.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				datetime = rs.getTimestamp(1);
			}
			rs.close();
			stmt.close();
			
			String query1 = "update newsletterdelivery set delivery = CURRENT_TIMESTAMP";
			Statement stmt1 = helper.getConnection().createStatement();
			stmt1.executeUpdate(query1);
			stmt1.close();
			helper.closeConnection();
			return datetime;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	public int getUserselectedncid() {
		return userselectedncid;
	}

	public void setUserselectedncid(int userselectedncid) {
		this.userselectedncid = userselectedncid;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}
}
