package com.newscenter.server.newsdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
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
import com.newscenter.client.news.NewsProviderService;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.categorydb.ItemProviderServiceImpl;
import com.newscenter.server.newsdb.newsdbhelper.NewsItemHelper;

public class NewsProviderServiceImpl extends RemoteServiceServlet implements NewsProviderService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	String connectionUrl;
	String driverClassName;
	String username;
	String password;
	protected String tomcatpath;
	ServletContext context;

	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		//ServletContext context=getServletContext();
		context=getServletContext();
		connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(AllocateResources.USERNAME);
		password =(String)context.getAttribute(AllocateResources.PASSWORD);
		tomcatpath=(String)context.getAttribute(AllocateResources.TOMCATPATH);
	}

	public ArrayList getadmininformation() 
	{
		context.log("In getadmininformation in NewsProviderServiceImpl");
		HttpServletRequest req = this.getThreadLocalRequest();
		HttpSession session = req.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int userid = userInformation.getUserId();
		String useremail = userInformation.getEmail();
		CategoryMap currentCategoryMap = (CategoryMap)session.getAttribute(ItemProviderServiceImpl.CURRENTMAP); 
		NewsItemHelper newshelper = new NewsItemHelper(currentCategoryMap,tomcatpath);
		ArrayList list = newshelper.getadmininformation(userid, useremail);
		context.log("Number of news items returned getadmininformation ::::" +list.size());
		System.out.println("Number of news items returned getadmininformation ::::" +list.size());
		newshelper.closeConnection();
		return list;

	}

	public NewsItemList getMatchingNewsItems() {
		try{
			context.log("In getMatchingNewsItems in NewsProviderServiceImpl");
			System.out.println("In getMatchingNewsItems in NewsProviderServiceImpl");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			if(session!= null){
				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
				CategoryMap currentCategoryMap = (CategoryMap)session.getAttribute(ItemProviderServiceImpl.CURRENTMAP); 
				NewsItemHelper newshelper = new NewsItemHelper(currentCategoryMap,tomcatpath);
				NewsItemList newslist = newshelper.getMatchingNewsItems();
				context.log("Number of news items returned getMatchingNewsItems::::" +newslist.size());
				System.out.println("Number of news items returned getMatchingNewsItems ::::" +newslist.size());
				newshelper.closeConnection();
				return newslist;
			}
			else{
				NewsItemList newsList = new NewsItemList();
				newsList.setRedirect(true);
				return newsList;
			}
		}
		catch(Exception ex){
			context.log("Exception ....................  "+ex.getMessage());
			System.out.println("Exception ....................  "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
	
	public NewsItemList getPage(PageCriteria criteria, int newsmode) {
		try{
			context.log("In getPage in NewsProviderServiceImpl");
			System.out.println("In getPage in NewsProviderServiceImpl");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			if(session!=null){
				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
				int userid = userInformation.getUserId();
				CategoryMap currentCategoryMap = (CategoryMap)session.getAttribute(ItemProviderServiceImpl.CURRENTMAP); 
				NewsItemHelper newshelper = new NewsItemHelper(currentCategoryMap,tomcatpath);
				NewsItemList newslist = newshelper.getPage(criteria, newsmode, userid);
				context.log("Number of news items returned getPage::::" +newslist.size());
				System.out.println("Number of news items returned getPage::::" +newslist.size());
				newshelper.closeConnection();
				return newslist;
			}
			else{
				NewsItemList newsList = new NewsItemList();
				newsList.setRedirect(true);
				return newsList;
			}
		}
		catch(Exception ex){
			context.log("Exception ....................  "+ex.getMessage());
			System.out.println("Exception ....................  "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	public NewsItemList getAndPage(PageCriteria criteria, int newsmode) {
		try{
			context.log("In getAndPage in NewsProviderServiceImpl");
			System.out.println("In getAndPage in NewsProviderServiceImpl");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			if(session!=null){
				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
				int userid = userInformation.getUserId();
				CategoryMap currentCategoryMap = (CategoryMap)session.getAttribute(ItemProviderServiceImpl.CURRENTMAP); 
				NewsItemHelper newshelper = new NewsItemHelper(currentCategoryMap,tomcatpath);
				NewsItemList newslist = newshelper.getAndPage(criteria, newsmode, userid);
				context.log("Number of news items returned getAndPage::::" +newslist.size());
				System.out.println("Number of news items returned getAndPage::::" +newslist.size());
				newshelper.closeConnection();
				return newslist;
			}
			else{
				NewsItemList newsList = new NewsItemList();
				newsList.setRedirect(true);
				return newsList;
			}
		}
		catch(Exception ex){
			context.log("Exception ....................  "+ex.getMessage());
			System.out.println("Exception ....................  "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
	
	public NewsItemList getAllNewsforTag(TagItem tagitem, PageCriteria criteria) {
		try{
			context.log("In getAllNewsforTag in NewsProviderServiceImpl");
			System.out.println("In getAllNewsforTag in NewsProviderServiceImpl");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			if(session!=null){
				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
				CategoryMap currentCategoryMap = (CategoryMap)session.getAttribute(ItemProviderServiceImpl.CURRENTMAP); 
				NewsItemHelper newshelper = new NewsItemHelper(currentCategoryMap,tomcatpath);
				NewsItemList newslist = newshelper.getAllNewsforTag(tagitem, criteria);
				context.log("Number of news items returned getAllNewsforTag::::" +newslist.size());
				System.out.println("Number of news items returned getAllNewsforTag::::" +newslist.size());
				newshelper.closeConnection();
				return newslist;
			}
			else{
				NewsItemList newsList = new NewsItemList();
				newsList.setRedirect(true);
				return newsList;
			}
		}
		catch(Exception ex){
			context.log("Exception ....................  "+ex.getMessage());
			System.out.println("Exception ....................  "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}


	public NewsItemList getAllNewsForCategory(CategoryItem categoryItem, PageCriteria criteria) {
		try{
			context.log("In getAllNewsForCategory in NewsProviderServiceImpl");
			System.out.println("In getAllNewsForCategory in NewsProviderServiceImpl");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			if(session!=null){
				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
				CategoryMap currentCategoryMap = (CategoryMap)session.getAttribute(ItemProviderServiceImpl.CURRENTMAP); 
				NewsItemHelper newshelper = new NewsItemHelper(currentCategoryMap,tomcatpath);
				NewsItemList newslist = newshelper.getAllNewsForCategory(categoryItem, criteria);
				context.log("Number of news items returned getAllNewsForCategory::::" +newslist.size());
				System.out.println("Number of news items returned getAllNewsForCategory::::" +newslist.size());
				newshelper.closeConnection();
				return newslist;
			}
			else{
				NewsItemList newsList = new NewsItemList();
				newsList.setRedirect(true);
				return newsList;
			}
		}
		catch(Exception ex){
			context.log("Exception ....................  "+ex.getMessage());
			System.out.println("Exception ....................  "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
	

	public NewsItemList getAllNewsItemsForUser(CategoryMap currentCategoryMap, int userid, int filtermode,Timestamp datetime) {
		try{
			context.log("In getAllNewsItemsForUser in NewsProviderServiceImpl");
			System.out.println("In getAllNewsItemsForUser in NewsProviderServiceImpl");
			NewsItemHelper newshelper = new NewsItemHelper(currentCategoryMap,tomcatpath);
			NewsItemList newslist = newshelper.getAllNewsItemsForUser(userid,filtermode, datetime);
			context.log("Number of news items returned getAllNewsItemsForUser::::" +newslist.size());
			System.out.println("Number of news items returned getAllNewsItemsForUser::::" +newslist.size());
			newshelper.closeConnection();
			return newslist;
		}
		catch(Exception ex){
			context.log("Exception ....................  "+ex.getMessage());
			System.out.println("Exception ....................  "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	public boolean saveNewsletterPreference(String choice) {
		try{
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			boolean bool = false;
			if(session!=null){
				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
				int userid = userInformation.getUserId();
				int ncid = userInformation.getUserSelectedNewsCenterID();
				
				CategoryMap currentCategoryMap = (CategoryMap)session.getAttribute(ItemProviderServiceImpl.CURRENTMAP); 
				NewsItemHelper newshelper = new NewsItemHelper(currentCategoryMap,tomcatpath);
				bool = newshelper.saveNewsletterPreference(choice,userid,ncid);
				newshelper.closeConnection();
			}
			return bool;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	public int saveNewsFilterModePreference(String choice) {
		try{
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			int bool = 0;
			if(session!=null){
				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
				int userid = userInformation.getUserId();
				int ncid = userInformation.getUserSelectedNewsCenterID();
				
				CategoryMap currentCategoryMap = (CategoryMap)session.getAttribute(ItemProviderServiceImpl.CURRENTMAP); 
				NewsItemHelper newshelper = new NewsItemHelper(currentCategoryMap,tomcatpath);
				bool = newshelper.saveNewsFilterModePreference(choice,userid,ncid);
				newshelper.closeConnection();
			}
			return bool;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
	}
}
