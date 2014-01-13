package com.newscenter.server.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.login.client.UserInformation;

import com.newscenter.client.obsolete.NewsSelectionItem;
import com.newscenter.client.obsolete.SelectionItem;

public class UserHelperNewsCenter extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private Connection conn = null;
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private String driverClassName;
	private String connectionUrl;
	private String username;
	private String password;

	public UserHelperNewsCenter(HttpServletRequest req  , HttpServletResponse res,String connectionUrl,String driverClassName,String username,String password)
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
		return null;
	}

	public HashMap<Integer, String> getSelectionCategories(int industryId){
		HashMap<Integer, String> map = new HashMap<Integer, String>();

		try
		{
			String query = "select TagItemId, Name from tagitem where ParentId = 0";
			Statement stmt=conn.createStatement();
			HttpSession mySes = request.getSession(false);
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				map.put(rs.getInt(1), rs.getString(2));
			}
			rs.close();
			stmt.close();
		}catch(Exception ex){
			System.out.println("Error is retrieving categories");
			ex.printStackTrace();
		}
		return map;
	}

	public HashMap<Integer, SelectionItem> getSelectionItems(int parentId) {
		HashMap<Integer, SelectionItem> map = new HashMap<Integer, SelectionItem>();
		ArrayList list = new ArrayList();
		SelectionItem selItem;

		try
		{
			//String query = "select TagItemId,Name,ParentId from tagitem where parentId='"+parentId+"' limit 9";
			String query = "select TagItemId,Name,ParentId from tagitem where parentid = '"+parentId+"'ORDER BY Name ASC limit 9";
			Statement stmt=conn.createStatement();
			HttpSession mySes = request.getSession(false);
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				selItem = new SelectionItem();
				selItem.setTagId(rs.getInt(1));
				selItem.setTagName(rs.getString(2));
				selItem.setParentId(rs.getInt(3));
				//list.add(selItem);
				//list.add(rs.getInt(3));
				map.put(rs.getInt(1),selItem);
			}
			rs.close();
			stmt.close();
		}catch(Exception ex){
			System.out.println("Error is retrieving categories");
			ex.printStackTrace();
		}
		return map;
	}

	public HashMap<Integer, SelectionItem> getAllTags(int parentId){
		HashMap<Integer, SelectionItem> map = new HashMap<Integer, SelectionItem>();
		ArrayList list = new ArrayList();
		SelectionItem selItem;
		int tagCount = 0;

		try
		{
			String query1 = "select count(*) from tagitem where parentid ="+parentId;
			Statement stmt= conn.createStatement();
			HttpSession mySes1 = request.getSession(false); 
			ResultSet rs1 = stmt.executeQuery(query1);
			while(rs1.next()){
				tagCount = rs1.getInt(1);
			}
			rs1.close();
			String query = "select TagItemId,Name,ParentId from tagitem where parentId='"+parentId+"' ORDER BY Name ASC limit 8,"+tagCount;
			//String query = "select TagItemId,Name,ParentId from tagitem where industryId='"+industryId+"'";
			//Statement stmt=conn.createStatement();
			HttpSession mySes = request.getSession(false);
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				selItem = new SelectionItem();
				selItem.setTagId(rs.getInt(1));
				selItem.setTagName(rs.getString(2));
				selItem.setParentId(rs.getInt(3));
				map.put(rs.getInt(1),selItem); 
			}
			rs.close();
			stmt.close();
		}catch(Exception ex){
			System.out.println("Error is retrieving categories");
			ex.printStackTrace();
		}
		return map;
	}

	public ArrayList getSelectionNewsItems(ArrayList arraylist){
		ArrayList list = new ArrayList();
		NewsSelectionItem newsitem;
		Iterator iter = arraylist.iterator();
		try{
			while(iter.hasNext()){
				//newsitem = (NewsSelectionItem)iter.next();
				String query = "select N.ItemDate, N.Title, N.Abstract, N.Url, N.NewsItemId from newsitem N INNER JOIN newstagitem NT on N.NewsItemId = NT.NewsItemId where TagItemId ="+iter.next().toString();
				Statement stmt = conn.createStatement();
				HttpSession mySes = request.getSession(false);
				ResultSet rs = stmt.executeQuery(query);
				
				while(rs.next()){
					newsitem = new NewsSelectionItem();
					newsitem.setItemdate(rs.getDate(1));
					newsitem.setTitle(rs.getString(2));
					newsitem.setAbstract(rs.getString(3));
					newsitem.setUrl(rs.getString(4));
					newsitem.setNewsitemId(rs.getInt(5));
					list.add(newsitem);
				}
				rs.close();
				stmt.close();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}

	public HashMap<Integer, NewsSelectionItem> getNewsItemContent(int newsid){
		HashMap<Integer, NewsSelectionItem> map = new HashMap<Integer, NewsSelectionItem>();
		NewsSelectionItem newsitem;
		try{
			String query = "select ItemDate, Title, Abstract, Content, URL from newsitem where NewsItemId = "+newsid;
			Statement stmt = conn.createStatement();
			HttpSession mySes = request.getSession();
			ResultSet rs = stmt.executeQuery(query);

			while(rs.next()){
				newsitem = new NewsSelectionItem();
				newsitem.setItemdate(rs.getDate(1));
				newsitem.setTitle(rs.getString(2));
				newsitem.setAbstract(rs.getString(3));
				newsitem.setContent(rs.getString(4));
				newsitem.setUrl(rs.getString(5));
				map.put(newsid, newsitem);
			}
			rs.close();
			stmt.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return map;
	}

	public boolean validateUser()
	{
		HttpSession session = request.getSession(false);
		if(session!=null)
		{
			//String email =	(String)session.getAttribute("email");
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			if(userInformation==null)//.length()==0)
			{
				return false;
			}
			
			return true;

		}
		else
		{
			return false;
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
	
	public void saveUserSelection(ArrayList list){
	
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int industryid = userInformation.getUserSelectedIndustryID();
		int userid = userInformation.getUserId();
		int newscenterid = userInformation.getUserSelectedNewsCenterID();
		Iterator iter = list.iterator();
		
		try{
			while(iter.hasNext()){
				String query = "insert into usertagselection(UserId,TagItemId,NewsCenterId) values("+userid+","+iter.next().toString()+","+newscenterid+")";
				Statement stmt = conn.createStatement();
				HttpSession mySes = request.getSession();
	     		stmt.executeUpdate(query);
				stmt.close();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
	}
	
	public ArrayList getInformationFromSession()
	{
		ArrayList list = new ArrayList();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int industryId = userInformation.getUserSelectedIndustryID();
		String industryName = "";
		try
		{
			//String query="SELECT Name from industryenum where IndustryEnumId="+industryId;
			String query = "select NC.newscenterid, NC.name, IE.name from newscenter as NC, industryenum as IE where IE.industryenumid =" +industryId;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				int newscenterid = rs.getInt(1);
				String newscentername = rs.getString(2);
				industryName = rs.getString(3);
				list.add(industryId);
				list.add(industryName);
				list.add(newscenterid);
				list.add(newscentername);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	
	public ArrayList getAllCategoryForIndustry(String industry) 
	{
		int industryId = getIndustryId(industry);
		ArrayList categoryArray = new ArrayList();
		ArrayList array;
		
		try
		{
			String query ="SELECT TagItemId,Name from tagitem where ParentId =(select TagItemId from tagitem where Name ='"+industry+"') and IndustryId ="+1;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				array = new ArrayList();
				array.add(rs.getInt(1));
				array.add(rs.getString(2));
				categoryArray.add(array);
				
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return categoryArray;
	}
	
	public ArrayList getAllTagsForIndustry(String industry) 
	{
		int industryId = getIndustryId(industry);
		ArrayList tagArray = new ArrayList();
		ArrayList array;
		try
		{
			String query ="SELECT ParentId,Name from tagitem where IndustryId="+industryId;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				array = new ArrayList();
				array.add(rs.getInt(1));
				array.add(rs.getString(2));
				tagArray.add(array);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return tagArray;
	}
	
	public int getIndustryId(String industry)
	{
		int id = -1;
		try
		{
			String query = "SELECT IndustryEnumId from industryenum where name ='"+industry+"'";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				id = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return id;
	}
	

	public int getadminInformation(){
		int isadmin = 0;
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		String mail =  userInformation.getEmail();
		try{
			String query = "select isadmin from user where email = '"+mail+"'";
			Statement stmt = conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				isadmin = rs.getInt(1);
				userInformation.setIsAdmin(isadmin);
			}
			rs.close();
			stmt.close();
			return isadmin;
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
//		return isadmin;
	}
	
	public ArrayList getTagIdsToServeNewsItems(HashMap map) 
	{
		ArrayList array = new ArrayList();
		try
		{
			Iterator iter = map.keySet().iterator();
			while(iter.hasNext())
			{
				String tagname =(String)iter.next();
				int id = (Integer)map.get(tagname);
				String query ="SELECT TagItemId from tagitem where Name= '"+tagname+"' and ParentId="+id;
				Statement stmt = conn.createStatement();
				ResultSet rs =stmt.executeQuery(query);
				while(rs.next())
				{
					array.add(rs.getInt(1));
				}
				rs.close();
				stmt.close();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return array;
	}
	
}