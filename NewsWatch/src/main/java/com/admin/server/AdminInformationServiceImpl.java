package com.admin.server;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.admin.client.AdminInformationService;
import com.admin.client.AdminRegistrationInformation;
import com.admin.client.NewsItems;
import com.admin.client.NewsItemsAdminInformation;
import com.admin.client.TagItemInformation;
import com.admin.client.UserAdminInformation;
import com.admin.server.db.UserHelperAdmin;
import com.common.client.PageResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.login.client.UserInformation;
import com.login.server.db.AllocateResources;
import com.newscenter.client.criteria.PageCriteria;


public class AdminInformationServiceImpl extends RemoteServiceServlet implements AdminInformationService
{
	
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	String connectionUrl;
	String driverClassName;
	String username;
	String password;
	String tomcatpath;
	
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		ServletContext context=getServletContext();
		connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(AllocateResources.USERNAME);
		password =(String)context.getAttribute(AllocateResources.PASSWORD);
		tomcatpath = (String)context.getAttribute(AllocateResources.TOMCATPATH);
	}

	
	public ArrayList getUserInformation(int industryid) 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			ArrayList list = helper.getUserInformation(industryid);
			helper.closeConnection();
			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
			//System.out.println("There is problem in getUserInformation()");
		}
	}
	public void saveApprovedUserInfo(HashMap hashmap,String newsCenterName){
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			helper.saveApprovedUserInfo(hashmap,newsCenterName);
			helper.closeConnection();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public PageResult getUserInfoToModify(PageCriteria crt){
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			PageResult result = helper.getUserInfoToModify(crt);
			helper.closeConnection();
			return result;
			/*ArrayList list =  helper.getUserInfoToModify();
			helper.closeConnection();
			return list;*/
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	public void deleteUser(HashMap hashmap){
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			helper.deleteUser(hashmap);
			helper.closeConnection();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public ArrayList getIndustryName(){
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			ArrayList list = helper.getIndustryName();
			helper.closeConnection();
			return list;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	public ArrayList getTagNames(String industryName,String categoryName) 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			ArrayList list = helper.getTagName(industryName,categoryName);
			helper.closeConnection();
			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
			
		}
	}


	public void setAllNewsItemFields(NewsItemsAdminInformation newsitemInfo) 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			helper.setAllNewsItemFields(newsitemInfo);
			helper.closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}

	public  ArrayList getAllFieldOfNewsItems(int industryId) 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			ArrayList list = helper.getAllFieldOfNewsItems(industryId);
			helper.closeConnection();
			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		
	}

	public void deleteSelectedNewsItems(HashMap hashmap) 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			helper.deleteSelectedNewsItems(hashmap);
			helper.closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
		}	
	}

	public boolean validateUser() 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			boolean bool = helper.validateUser();
			helper.closeConnection();
			return bool;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public void uploadTagItems(String fileName) {
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			helper.uploadTagItems(fileName);
			helper.closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
	}
	
	public String convertFile(String file){
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			String str = helper.convertFile(file);
			helper.closeConnection();
			return str;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public ArrayList getUserApprovalItems(ArrayList list){
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			ArrayList userlist = helper.getUserApprovalItems(list);
			helper.closeConnection();
			return userlist;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	public ArrayList getNewsItemForThisTag(String industryName, String tagName) 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			ArrayList list = helper.getNewsItemForThisTag(industryName, tagName);
			helper.closeConnection();
			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public void editNewsItemFields(NewsItemsAdminInformation newsitemInfo) 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			helper.editNewsItemFields(newsitemInfo);
			helper.closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public ArrayList getCategoryNames(int industryid,String industryName){
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			ArrayList list = helper.getCategoryNames(industryid,industryName);
			helper.closeConnection();
			return list;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public int getParentId(int industryid,String industryName){
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			int id = helper.getParentId(industryid,industryName);
			helper.closeConnection();
			return id;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	public void removeFromSession() 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			helper.removeFromSession();
			helper.closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public boolean saveNewTag(TagItemInformation tagItem,String parentName, boolean isCategory){
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			boolean bool = helper.saveNewTag(tagItem, parentName, isCategory);
			helper.closeConnection();
			return bool;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean sendMailForApproval(UserAdminInformation userinfo) {
		return false;
	}

	public HashMap<Integer, TagItemInformation> getCategoryTagsInfo(String userSelectedIndustryName,int userSelectedIndustryId,int parentId,boolean isCategory){
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin(req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			HashMap<Integer, TagItemInformation> hashmap = helper.getCategoryTagsInfo(userSelectedIndustryName,userSelectedIndustryId,parentId, isCategory);
			helper.closeConnection();
			return hashmap;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	public String setUserInfoAdminRegistration(AdminRegistrationInformation adminregInfo)
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			 
			String strstatus = helper.setUserInfoAdminRegistration(adminregInfo);
			helper.closeConnection();
			if(strstatus.equals("true"))
			return "true";
			else
				return "false";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return "false";
		}
	}

	public String[] getIndustryNameFromSession() 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
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
	
	public void deleteTags(HashMap hashmap, boolean isCategory){
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			helper.deleteTags(hashmap, isCategory);
			helper.closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public void updateTags(HashMap<Integer,TagItemInformation> hashmap){
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			helper.updateTags(hashmap);
			helper.closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}


	
	public HashMap<Integer, TagItemInformation> getCategoryTagsInformation(
			String categoryName, String userSelectedIndustryName,
			int userSelectedIndustryId, int parentId, boolean isCategory)
			{
	try
	{
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperAdmin helper = new UserHelperAdmin(req,res,connectionUrl,driverClassName,username,password, tomcatpath);
		HashMap<Integer, TagItemInformation> map = helper.getCategoryTagsInformation(categoryName, userSelectedIndustryName, userSelectedIndustryId, parentId, isCategory);
		helper.closeConnection();
		return map;
	}
	catch(Exception ex){
		ex.printStackTrace();
		return null;
	}
	}

	public int getCategoryId(String categoryName) {
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
			int id = helper.getCategoryId(categoryName);
			helper.closeConnection();
			return id;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
		
	}

	public void updateSubscriptionDuration(ArrayList<AdminRegistrationInformation> userlist) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
		helper.updateSubscriptionDuration(userlist);
		helper.closeConnection();
		
	}

	public void deleteUserSubscription(AdminRegistrationInformation user) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
		helper.deleteUserSubscription(user);
		helper.closeConnection();
		
	}

	@Override
	public void saveEmailTemplate(String emailtemplate, int newscenterid) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
		helper.saveEmailTemplate(emailtemplate,newscenterid);
		helper.closeConnection();
	}


	@Override
	public PageResult getSearchedUserInfoModify(PageCriteria crt,String columnname, String search) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
		PageResult result = helper.getSearchedUserInfoModify(crt,columnname,search);
		helper.closeConnection();
		return result;
	}


	@Override
	public PageResult getSortedUserInfoModify(PageCriteria crt,	String columnname, String mode) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		UserHelperAdmin helper = new UserHelperAdmin( req,res,connectionUrl,driverClassName,username,password, tomcatpath);
		PageResult result = helper.getSortedUserInfoModify(crt,columnname,mode);
		helper.closeConnection();
		return result;
	}
}