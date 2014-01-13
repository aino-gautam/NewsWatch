package com.newscenter.server;

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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.newscenter.client.obsolete.CategoryProviderService;
import com.newscenter.client.obsolete.NewsSelectionItem;
import com.newscenter.client.obsolete.SelectionItem;
import com.newscenter.server.db.UserHelperNewsCenter;

public class CategoryProviderServiceImpl extends RemoteServiceServlet implements CategoryProviderService
{
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	String connectionUrl;
	String driverClassName;
	String username;
	String password;

	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		ServletContext context=getServletContext();
		connectionUrl =(String)context.getAttribute("connectionUrl");
		driverClassName =(String)context.getAttribute("driverClassName");
		username =(String)context.getAttribute("userName");
		password =(String)context.getAttribute("password");
	}

	public HashMap<Integer, String> getSelectionCategories(int industryId)
	{
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req , res,connectionUrl,driverClassName,username,password);
			return helper.getSelectionCategories(industryId);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
			return null;
		}
	}

	public HashMap<Integer, SelectionItem> getSelectionItems(int parentId) {
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req , res,connectionUrl,driverClassName,username,password);
			return helper.getSelectionItems(parentId);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
			return null;
		}
	}

	public HashMap<Integer, SelectionItem> getAllTags(int parentId){
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req , res,connectionUrl,driverClassName,username,password);
			return helper.getAllTags(parentId);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
			return null;
		}
	}

	public ArrayList getSelectionNewsItems(ArrayList arraylist) {
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req , res,connectionUrl,driverClassName,username,password);
			return helper.getSelectionNewsItems(arraylist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving from db");
			return null;
		}
	}

	public HashMap<Integer, NewsSelectionItem> getNewsItemContent(int newsid) {
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req , res,connectionUrl,driverClassName,username,password);
			return helper.getNewsItemContent(newsid);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving from db");
			return null;
		}
	}

	public boolean validateUser() 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req,res,connectionUrl,driverClassName,username,password);
			return helper.validateUser();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public void removeFromSession() 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req,res,connectionUrl,driverClassName,username,password);
			helper.removeFromSession();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
		}
	}
	public void saveUserSelection(ArrayList list){
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req , res,connectionUrl,driverClassName,username,password);
			helper.saveUserSelection(list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
		}
	}

	
	public ArrayList getInformationFromSession() 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req,res,connectionUrl,driverClassName,username,password);
			return helper.getInformationFromSession();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	
	public ArrayList getAllCategoryForIndustry(String industry) 
	{
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req,res,connectionUrl,driverClassName,username,password);
			return helper.getAllCategoryForIndustry(industry);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	
	public ArrayList getAllTagsForIndustry(String industry) 
	{
		
		try
		{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req,res,connectionUrl,driverClassName,username,password);
			return helper.getAllTagsForIndustry(industry);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public int getisadminInformation() {
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req , res,connectionUrl,driverClassName,username,password);
			return helper.getadminInformation();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
			return 0;
		}
//		return 0;
	}

	
	public ArrayList getTagIdsToServeNewsItems(HashMap map) 
	{
		try {
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			UserHelperNewsCenter helper = new UserHelperNewsCenter( req , res,connectionUrl,driverClassName,username,password);
			return helper.getTagIdsToServeNewsItems(map);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
			return null;
		}
	}
}