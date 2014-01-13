package com.common.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.admin.server.db.UserHelperAdmin;
import com.common.client.CommonInformationService;
import com.common.client.PageResult;
import com.common.client.UserHistory;
import com.common.server.db.UserHelperForCommon;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.login.server.db.AllocateResources;
import com.newscenter.client.criteria.PageCriteria;

public class CommonInformationServiceImpl extends RemoteServiceServlet implements CommonInformationService 
{
	protected Connection conn = null;
	protected Statement stmt = null;
	protected ResultSet rs = null;
	protected String connectionUrl;
	protected String driverClassName;
	protected String username;
	protected String password;	
	
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		ServletContext context=getServletContext();
		connectionUrl =(String)context.getAttribute(/*"connectionUrl"*/AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(/*"driverClassName"*/AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(/*"userName"*/AllocateResources.USERNAME); 
		password =(String)context.getAttribute(/*"password"*/AllocateResources.PASSWORD);
	}


	public void removeFromSession() 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
			helper.removeFromSession();
			helper.closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	public String[] getIndustryNameFromSession() {
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
			String[] strarray = helper.getIndustryNameFromSession();
			helper.closeConnection();
			return strarray;
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}


	
	public ArrayList getUserInformationObject() 
	{
		
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
			ArrayList list = helper.getUserInformationObjec();
			helper.closeConnection();
			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public void changeuserdetails(String userpassword,String useremail) {
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
			helper.changeuserdetails(userpassword, useremail);
			helper.closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}


	@Override
	public ArrayList<UserHistory> getUserAccountHistory(int userId) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
		ArrayList<UserHistory> userhistory = helper.getUserAccountHistory(userId);
		helper.closeConnection();
		return userhistory;
	}


	@Override
	public String getEmailTemplate(int newscenterid) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
		String emailTemplate =  helper.getEmailTemplate(newscenterid);
		helper.closeConnection();
		return emailTemplate;
	}


	@Override
	public void saveSignature(int userid,String signature) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
		helper.saveSignature(userid,signature);
		helper.closeConnection();
	}


	@Override
	public String getSignature(int userid) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
		String signature = helper.getSignature(userid);
		helper.closeConnection();
		return signature;
	}
	
	@Override
	public PageResult getLoginStatistics(PageCriteria crt,int userid,int industryid) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
		PageResult list = helper.getLoginStatistics(crt,userid,industryid);
		helper.closeConnection();
		return list;
	}


	@Override
	public PageResult getSearchLoginStatistics(PageCriteria crt,int userid,	int industryid, String searchColumnName, String searchString) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
		PageResult list = helper.getSearchLoginStatistics(crt,userid,industryid,searchColumnName,searchString);
		helper.closeConnection();
		return list;
	}


	@Override
	public PageResult getSortedLoginStatistics(PageCriteria crt,int userid,	int industryid, String columnname, String mode) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
		PageResult list = helper.getSortedLoginStatistics(crt,userid,industryid,columnname,mode);
		helper.closeConnection();
		return list;
	}
	
	@Override
	public PageResult getSearchUserItemAccessStats(PageCriteria crt,int userid,	int industryid, String searchColumnName, String searchString) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
		PageResult pageresult = helper.getSearchUserItemAccessStats(crt,userid,industryid,searchColumnName,searchString); 
		helper.closeConnection();
		return pageresult;
	}


	@Override
	public PageResult getSortedUserItemAccessStats(PageCriteria crt,int userid,int industryid, String columnname, String mode) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
		PageResult pageresult = helper.getSortedUserItemAccessStats(crt,userid,industryid,columnname,mode); 
		helper.closeConnection();
		return pageresult;
	}


	@Override
	public PageResult getUserItemAccessStats(PageCriteria crt,int userid, int industryid) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperForCommon helper = new UserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
		PageResult pageresult = helper.getUserItemAccessStats(crt,userid,industryid); 
		helper.closeConnection();
		return pageresult;
	}
}
