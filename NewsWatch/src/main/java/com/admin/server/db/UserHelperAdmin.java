package com.admin.server.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.*;
import jxl.*;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.admin.client.AdminRegistrationInformation;
import com.admin.client.NewsItems;
import com.admin.client.NewsItemsAdminInformation;
import com.admin.client.TagItemInformation;
import com.admin.client.UserAdminInformation;
import com.admin.server.UserInformationforMail;
import com.common.client.PageResult;
import com.common.client.PopUpForForgotPassword;
import com.common.client.UserItemAccessStats;
import com.common.client.UserLoginStats;
import com.google.gwt.user.client.ui.TextBox;
import com.login.client.UserInformation;
import com.mysql.jdbc.Blob;
import com.mysql.jdbc.PreparedStatement;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.server.categorydb.ItemProviderServiceImpl;

public class UserHelperAdmin extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet{
	private Connection conn = null;
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private String driverClassName;
	private String connectionUrl;
	private String username;
	private String password;
	private InputStream inputstream;
	private String tomcatpath;
	File file;
	File folderImage;
	String folderUrl;
	byte[] b;
	public static boolean flag = true;
	private UserAdminInformation useradmininfo = new UserAdminInformation();
	private ArrayList<UserAdminInformation> array;
    private ServletContext context;
	public UserHelperAdmin(){
		
	}

	public UserHelperAdmin(HttpServletRequest req  , HttpServletResponse res,String connectionUrl,String driverClassName,String username,String password, String tomcatpath){
		try {
			request = req ;
			response = res ;
			this.connectionUrl =connectionUrl;
			this.driverClassName =driverClassName;
			this.username =username;
			this.password =password;
			context = req.getSession().getServletContext();
			setFolderUrl(tomcatpath+"/imagefolder");
			setFolderImage(new File(getFolderUrl()));
			getConnection();
		}
		catch(Exception ex){
			System.out.println("Exception in getConnection!");
			ex.printStackTrace();
		}
	}

	protected void finalize() throws Throwable{
		super.finalize();
		if (conn != null)
			conn.close();
	}

	public void closeConnection(){
		try{
			if(conn!=null){
				System.out.println("Closing connection for admin");
				conn.close();
			}
			}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	private Connection getConnection(){
		try {
			if(conn==null){
				System.out.println("Returning connection for admin module");
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

	public ArrayList getUserInformation(int industryid){
		ArrayList userlist= new ArrayList();
		AdminRegistrationInformation userinfo; 
	//	ArrayList array;
		try{
			String query = "select UserId, FirstName, LastName, Company, PhoneNo, email from user where isAdmin ="+0 +" and isApproved ="+0 +" and IndustryEnumId ="+industryid;
			//String query = "select UserId, FirstName, LastName, Company, PhoneNo, email from user where roleId in (select id from role where name = User) and isApproved ="+0 +" and IndustryEnumId ="+industryid;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);

			while(rs.next()){
				userinfo = new AdminRegistrationInformation(); 
				userinfo.setUserid(rs.getInt("UserId"));
				userinfo.setName(rs.getString("FirstName"));
				userinfo.setLastname(rs.getString("LastName"));
				userinfo.setCompanynm(rs.getString("Company"));
				userinfo.setPhoneno(rs.getLong("PhoneNo"));
				userinfo.setEmail(rs.getString("email"));
				int duration = getUserDuration(rs.getInt("UserId"));
				userinfo.setDuration(duration);
				/*array = new ArrayList();
				array.add(rs.getInt(1));
				array.add(rs.getString(2));
				array.add(rs.getString(3));*/
				userlist.add(userinfo);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
			System.out.println("problem in getUserInformation()");
		}
		return userlist;
	}

	public String getUserSelectedIndustry(int id){
		String industryName = new String();
		try{
			String query = "select Name from newscenter where NewsCenterId =(select NewsCenterId from usersubscription where UserId = "+id+")";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				industryName= rs.getString("Name");
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
			System.out.println("Problem in getUserSelectedIndustry()");
		}
		return industryName;
	}

	public void saveApprovedUserInfo(HashMap hashmap,String newsCenterName){
		try{
			for(Object obj:hashmap.keySet()){
				int id = (Integer)obj;
				int durationvalue = (Integer)hashmap.get(id);
				savedurationAndnewsCenterName(id,durationvalue,newsCenterName);

			    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			    Date date = new Date();
			    String str = format.format(date);
			    System.out.println("Today is " + str);
			    
			    HttpSession session = request.getSession(false);
			    UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
				String query ="update user set isApproved=1 and createdBy="+userInformation.getUserId()+" where UserId="+id;
				Statement stmt=conn.createStatement();
				stmt.executeUpdate(query);

				String query1 = "update usersubscription set ApprovalDate='"+str+"' where UserId="+id;
				//stmt=conn.createStatement();
				stmt.executeUpdate(query1);
				stmt.close();
			}
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void savedurationAndnewsCenterName(int id, int durationvalue,String newsCenterName){
		String newsCenter = newsCenterName.replace(" ", "");
		int newsCenterId = getnewsCenterId(newsCenter+".NewsCenter.com");
		try{
			String query = "insert into usersubscription (UserId, Duration, DurationLeft,NewsCenterId) values ('"+id+"','"+durationvalue+"','"+durationvalue+"','"+newsCenterId+"')";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
	}

	public int getnewsCenterId(String newsCenterName){
		int newsId=0;
		try{
			String query = "SELECT NewsCenterId from newscenter where Name ='" +newsCenterName+"'";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				newsId= rs.getInt("NewsCenterId");
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return newsId;
	}

	public PageResult getUserInfoToModify(PageCriteria crt) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		AdminRegistrationInformation userinfo;
		int industryId = userInformation.getUserSelectedIndustryID();
		String userSelectedIndustry;
		int duration;
		int durationleft;
		ArrayList userlist= new ArrayList();
		try{
			String query = "select UserId,FirstName,LastName,email,PhoneNo,Company,isApproved from user where isAdmin ="+0+" and IndustryEnumId="+industryId;
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
			
			query = "select UserId,FirstName,LastName,email,PhoneNo,Company,isApproved from user where isAdmin ="+0+" and IndustryEnumId="+industryId+" LIMIT "+startRecord+","+pagesize+"";
			ResultSet rss = stmt.executeQuery(query);
			
			while(rss.next()){
				userinfo = new AdminRegistrationInformation();
				userinfo.setUserid(rss.getInt("UserId"));
				userinfo.setName(rss.getString("FirstName"));
				userinfo.setLastname(rss.getString("LastName"));
				userinfo.setEmail(rss.getString("email"));
				userinfo.setPhoneno(rss.getLong("PhoneNo"));
				userinfo.setCompanynm(rss.getString("Company"));
				userinfo.setIsApproved(rss.getInt("isApproved"));

				userSelectedIndustry = getUserSelectedIndustry(rss.getInt("UserId"));
				duration = getUserDuration(rss.getInt("UserId"));
				durationleft = getUserDurationLeft(rss.getInt("userId"));
				userinfo.setDuration(duration);
				userinfo.setDurationLeft(durationleft);
				userlist.add(userinfo);
			}
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(userlist);
			rs.close();
			stmt.close();
			}
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
			System.out.println("problem in getUserInformation()");
		}
		
		return pageresult;
	}

	
	/*public ArrayList getUserInfoToModify() {
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		AdminRegistrationInformation userinfo;
		int industryId = userInformation.getUserSelectedIndustryID();
		String userSelectedIndustry;
		int duration;
		int durationleft;
		ArrayList userlist= new ArrayList();
		//ArrayList array;
		try{String smtpusername;
		String smtppassword;
		String smtphost;
		String smtpport;
			String query = "select UserId,FirstName,LastName,email,PhoneNo,Company,isApproved from user where isAdmin ="+0+" and IndustryEnumId="+industryId;
			//String query = "select UserId,FirstName,LastName,email,PhoneNo,Company,isApproved from user where roleId in (select id from role where name = User) and IndustryEnumId="+industryId;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);

			while(rs.next()){
				userinfo = new AdminRegistrationInformation();
				userinfo.setUserid(rs.getInt("UserId"));
				userinfo.setName(rs.getString("FirstName"));
				userinfo.setLastname(rs.getString("LastName"));
				userinfo.setEmail(rs.getString("email"));
				userinfo.setPhoneno(rs.getLong("PhoneNo"));
				userinfo.setCompanynm(rs.getString("Company"));
				userinfo.setIsApproved(rs.getInt("isApproved"));

				userSelectedIndustry = getUserSelectedIndustry(rs.getInt("UserId"));
				duration = getUserDuration(rs.getInt("UserId"));
				durationleft = getUserDurationLeft(rs.getInt("userId"));
				userinfo.setDuration(duration);
				userinfo.setDurationLeft(durationleft);
				userlist.add(userinfo);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("problem in getUserInformation()");
		}
		return userlist;
	}*/
	
	
	
	public int getUserDuration(int id){
		int durationUser=0;
		try{
			String query = "SELECT Duration from usersubscription where UserId="+id;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				durationUser= rs.getInt("Duration");
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
			System.out.println("Problem in getUserDuration()");
		}
		return durationUser;
	}
	
	public int getUserDurationLeft(int id){
		int durationleft=0;
		try{
			String query = "SELECT DurationLeft from usersubscription where UserId="+id;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				durationleft= rs.getInt("DurationLeft");
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
			System.out.println("Problem in getUserDuration()");
		}
		return durationleft;
	}
	public void deleteUser(HashMap hashmap)
	{
		try{
			for(Object obj:hashmap.keySet())
			{
				int id = (Integer)obj;
				Statement stmt=conn.createStatement();
				String query = "delete from loginStatistics where userId = "+id;
				stmt.executeUpdate(query);
				query = "delete from useraccounthistory where userId = "+id;
				stmt.executeUpdate(query);
				query = "delete from useritemaccessstats where userId = "+id;
				stmt.executeUpdate(query);
				query = "delete from usersubscription where userId = "+id;
				stmt.executeUpdate(query);
				query = "delete from usertagselection where userId = "+id;
				stmt.executeUpdate(query);
				query ="DELETE from user where UserId="+id;
				
				stmt.executeUpdate(query);
				stmt.close();
			}
		}
		catch(Exception ex)
		{
			context.log("Exception in deleteUser "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public ArrayList getIndustryName()
	{
		ArrayList list = new ArrayList();
		ArrayList array;
		try
			{
				String query = "SELECT IndustryEnumId,Name from industryenum";
				Statement stmt=conn.createStatement();
				ResultSet rs =stmt.executeQuery(query);
				while(rs.next()){
					array = new ArrayList();
					array.add(rs.getInt("IndustryEnumId"));
					array.add(rs.getString("Name"));
					list.add(array);
				}
				rs.close();
				stmt.close();
			}
		catch(Exception ex)
			{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
				ex.printStackTrace();
			}
		return list;
	}

	public ArrayList getTagName(String industryName,String categoryName){
		ArrayList list = new ArrayList();
		ArrayList array;
		int categoryparentid = 0;
		try{
			HttpSession session = request.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			String newscentername = userInformation.getIndustryNewsCenterName();
			
			String query1 = "Select TagItemId from tagitem where Name = '"+newscentername+"'";
			Statement stmt=conn.createStatement();
			ResultSet rs1 =stmt.executeQuery(query1);
			while(rs1.next()){
				categoryparentid = rs1.getInt("TagItemId");
			}
			rs1.close();
			String query = "SELECT TagItemId,Name from tagitem where ParentId =(SELECT TagItemId from tagitem where Name='"+categoryName+"' and ParentId = "+categoryparentid+" and IndustryId =(SELECT IndustryEnumId from industryenum where Name='"+industryName+"'))";
		    stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				TagItemInformation tagiteminfo = new TagItemInformation();
				tagiteminfo.setTagItemId(rs.getInt("TagItemId"));
				tagiteminfo.setTagName(rs.getString("Name"));
				list.add(tagiteminfo);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return list;
	}

	public void setAllNewsItemFields(NewsItemsAdminInformation newsitemInfo) 
	{
		PreparedStatement  prestmt = null;
		try{
		
			String query ="insert into newsitem(Content,Title,Abstract,URL,ItemDate,Source,NewsImages) values(?,?,?,?,?,?,?)";
			
			prestmt = (PreparedStatement)conn.prepareStatement(query);
			prestmt.setString(1, newsitemInfo.getContent());
			prestmt.setString(2, newsitemInfo.getNewsTitle());
			prestmt.setString(3, newsitemInfo.getAbstractNews());
			prestmt.setString(4, newsitemInfo.getUrl());
			//prestmt.setString(5, newsitemInfo.getDate());
			java.sql.Date sqlDate = new java.sql.Date(newsitemInfo.getDate().getTime());
			prestmt.setDate(5, sqlDate);
			prestmt.setString(6, newsitemInfo.getSource());
			prestmt.setBinaryStream( 7, getInputstream(), (int)newsitemInfo.getFilelength());
			prestmt.executeUpdate();
			getInputstream().close();
			prestmt.close();
			prestmt = null;
			
			setTagIdAndNewsItemId(newsitemInfo);
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	public void setTagIdAndNewsItemId(NewsItemsAdminInformation newsitemInfo)
	{
		String col="NewsItemId";
		String tableName ="newsitem";
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int industryN = userInformation.getUserSelectedIndustryID();
		String industry = userInformation.getIndustryNewsCenterName();
		ArrayList listtagName = newsitemInfo.getArrayTagList();
		
		Iterator iterate = listtagName.iterator();
		
		int maxCount = getMaxValue(col,tableName);
		try
		{
			while(iterate.hasNext())
			{
					String tagName = (String)iterate.next();
					int tagId = getTagId(industry,tagName);
					String query ="insert into newstagitem(NewsItemId,TagItemId,IndustryEnumId)values("+maxCount+","+tagId+","+industryN+")";
					Statement stmt = conn.createStatement() ;
					stmt.executeUpdate(query);
					stmt.close();
			}
					
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public int getTagId(String industry,String tag)
	{
		int id=0;
		try
		{
			String query ="SELECT TagItemId from tagitem where Name = '"+tag+"' and IndustryId=(SELECT IndustryEnumId from industryenum where Name='"+industry+"')";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				id = rs.getInt("TagItemId");
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return id;
	}
	public int getMaxValue(String col,String tableName)
	{
		int maxValue=0;
		try
		{
			String query ="SELECT max("+col+") from "+tableName;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				maxValue = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return maxValue;
	}

	public ArrayList getAllFieldOfNewsItems(int industryId){
		ArrayList newsitemIds = getNewsItemIdsForThisIndustryId(industryId);
		Iterator iterate = newsitemIds.iterator();
		ArrayList list = new ArrayList();
		NewsItems news;
		try{
			while(iterate.hasNext()){
				int newsId = (Integer)iterate.next();
				String query = "Select NewsItemId,Title,Abstract,Content,URL,ItemDate from newsitem where NewsItemId ="+newsId;
				Statement stmt=conn.createStatement();
				ResultSet rs =stmt.executeQuery(query);
				while(rs.next()){
					news = new NewsItems();
					news.setNewsId(rs.getInt("NewsItemId"));
					news.setNewsTitle(rs.getString("Title"));
					news.setAbstractNews(rs.getString("Abstract"));
					news.setNewsContent(rs.getString("Content"));
					news.setUrl(rs.getString("URL"));
					news.setNewsDate(rs.getDate("ItemDate"));
					list.add(news);
				}
				rs.close();
				stmt.close();
			}
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return list;
	}

	public ArrayList getNewsItemIdsForThisIndustryId(int industryId){
		ArrayList list = new ArrayList();
		
		try
		{
			String query = "SELECT distinct NewsItemId from newstagitem where IndustryEnumId="+industryId;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				list.add(rs.getInt("NewsItemId"));
			}
			rs.close();
			stmt.close();
			return list;
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
	
	public void deleteSelectedNewsItems(HashMap hashmap)
	{
		try{
			for(Object obj:hashmap.keySet())
			{
				Statement stmt=conn.createStatement();
				int id = (Integer)obj;
				String query = "delete from useritemaccessstats where newsitemId = "+id;
				stmt.executeUpdate(query);
				query ="DELETE news,tag from newsitem AS news,newstagitem AS tag where news.NewsItemId = tag.NewsItemId and news.NewsItemId="+id;
				stmt.executeUpdate(query);
				stmt.close();
			}
		}
		catch(Exception ex)
		{
			context.log("Exception in deleteSelectedNewsItems "+ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	public boolean validateUser()
	{
		HttpSession session = request.getSession(false);
		if(session!=null)
		{
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

	public void uploadTagItems(String fileName){
		try{
			String file = fileName.replace("\\", "/");
	        String query ="LOAD DATA LOCAL INFILE '"+file+"' INTO TABLE tagitem FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r\n' (Name, ParentId,IndustryId)";
			Statement stmt = conn.createStatement() ;
			stmt.executeUpdate(query);
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
	}

	public ArrayList getUserApprovalItems(ArrayList list){//userAdminInformation useradmininfo
		Iterator iter = list.iterator();
		ArrayList<UserAdminInformation> array = new ArrayList<UserAdminInformation>();
		while(iter.hasNext()){
			useradmininfo = (UserAdminInformation)iter.next();
			try{
				String query = "select U.UserId,U.FirstName,U.email,U.password,US.Duration,N.Name,N.NewsCenterId from user U, usersubscription US, newscenter N where US.NewsCenterId = N.NewsCenterId and U.UserId = US.UserId and U.UserId = '"+useradmininfo.getUserId()+"'";
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				while(rs.next()){
//					array = new ArrayList<UserAdminInformation>();
					useradmininfo.setUserId(rs.getInt("UserId"));
					useradmininfo.setName(rs.getString("FirstName"));
					useradmininfo.setEmail(rs.getString("email"));
					useradmininfo.setPassword(rs.getString("password"));
					useradmininfo.setDuration(rs.getInt("Duration"));
					array.add(useradmininfo);
				}
				rs.close();
				stmt.close();
			}catch(Exception ex){
				context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
				ex.printStackTrace();
			}
		}
		return array;
	}	

	public ArrayList getNewsItemForThisTag(String industryName,String tagName){
		int idNewsItem = getNewsItemId(industryName,tagName);
		NewsItems newsItem;
		TagItemInformation tagitem;
		ArrayList list = new ArrayList();
		ArrayList array=null;
		folderUrl = getFolderUrl();
		folderImage = getFolderImage();
		folderImage.mkdir();
		try{
			int newsid = 0;
			//String query = "SELECT Title,Abstract,Content,URL,ItemDate from newsitem where NewsItemId ="+idNewsItem;
			String query = "select N.Title,N.Content, N.Abstract, N.Url,N.ItemDate,N.Source, N.NewsImages, N.NewsItemId from newsitem N INNER JOIN newstagitem NT on N.NewsItemId = NT.NewsItemId where TagItemId ="+idNewsItem;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				newsItem = new NewsItems();
				newsid = rs.getInt("NewsItemId");
				
				//new
				String query1 = "select * from tagitem where TagItemId in(select TagItemId from newstagitem where NewsItemId="+newsid+")";
				Statement stmt1=conn.createStatement();
				ResultSet rs1=stmt1.executeQuery(query1);
				while(rs1.next()){
					tagitem = new TagItemInformation();
					tagitem.setTagItemId(rs1.getInt("TagItemId"));
					tagitem.setTagName(rs1.getString("Name"));
					tagitem.setParentId(rs1.getInt("ParentId"));
					tagitem.setIndustryId(rs1.getInt("IndustryId"));
					newsItem.addTagforNews(tagitem);
				}
				rs1.close();
				stmt1.close();
				
				newsItem.setNewsId(newsid);
				newsItem.setNewsTitle(rs.getString("Title"));
				newsItem.setNewsContent(rs.getString("Content"));
				newsItem.setAbstractNews(rs.getString("Abstract"));
				newsItem.setUrl(rs.getString("URL"));
				newsItem.setNewsDate(rs.getDate("ItemDate"));
				newsItem.setNewsSource(rs.getString("Source"));

				file = new File(folderImage+"//"+newsid+".jpg");
				context.log("file path in userhelperadmin "+file.getPath());
				System.out.println("file path in userhelperadmin "+file.getPath());
				String imageurlTomcatFolder = "imagefolder";
			//	String imageurl = folderUrl+"\\"+newsid+".jpg";
				String imageurl = imageurlTomcatFolder+"/"+newsid+".jpg";
				context.log("image url :::: " +imageurl);
				System.out.println("image url :::: " +imageurl);
				newsItem.setImageUrl(imageurl);
				if(rs.getBlob("NewsImages") != null){
					context.log("image present");
					System.out.println("image present");
				Blob blobimg = (Blob)rs.getBlob("NewsImages");
				 
				InputStream x = blobimg.getBinaryStream();

				int size = x.available();
				OutputStream out=new FileOutputStream(file);
				byte b[]= new byte[size];
				x.read(b);
				out.write(b);
				}
				list.add(newsItem);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			context.log(ex.getMessage());
			ex.printStackTrace();
		}
		context.log("List size for edit news:::: "+list.size());
		System.out.println("List size for edit news:::: "+list.size());
		return list;
	}

	public int getNewsItemId(String industryName,String tagName){
		int id = 0;
		try{
			//String query ="SELECT NewsItemId from newstagitem where TagItemId= (SELECT TagItemId from tagitem where Name = '"+tagName+"' and IndustryId=(SELECT IndustryEnumId from industryenum where Name='"+industryName+"'))";
			String query ="select TagItemId from tagitem where Name= '"+tagName+"' and IndustryId =(SELECT IndustryEnumId from industryenum where Name = '"+industryName+"')";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				id = rs.getInt("TagItemId");
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return id;
	}

	public void editNewsItemFields(NewsItemsAdminInformation newsitemInfo){
		PreparedStatement preparedstmt = null;
		try{
			String query = "";
			if(newsitemInfo.getFilelength()!=0){
				query ="update newsitem set Content=?,Title=?,Abstract=?,URL=?,ItemDate=?,Source=?,NewsImages=? where NewsItemId = ?";
				preparedstmt = (PreparedStatement)conn.prepareStatement(query);
				preparedstmt.setString(1, newsitemInfo.getContent());
				preparedstmt.setString(2, newsitemInfo.getNewsTitle());
				preparedstmt.setString(3, newsitemInfo.getAbstractNews());
				preparedstmt.setString(4, newsitemInfo.getUrl());
				//preparedstmt.setString(5, newsitemInfo.getDate());
				java.sql.Date sqlDate = new java.sql.Date(newsitemInfo.getDate().getTime());
				preparedstmt.setDate(5, sqlDate);
				preparedstmt.setString(6, newsitemInfo.getSource());
				preparedstmt.setBinaryStream(7, getInputstream(), newsitemInfo.getFilelength());
				preparedstmt.setInt(8, newsitemInfo.getNewsItemId());
			}
			else{
				query ="update newsitem set Content=?,Title=?,Abstract=?,URL=?,ItemDate=?,Source=? where NewsItemId = ?";
				preparedstmt = (PreparedStatement)conn.prepareStatement(query);
				preparedstmt.setString(1, newsitemInfo.getContent());
				preparedstmt.setString(2, newsitemInfo.getNewsTitle());
				preparedstmt.setString(3, newsitemInfo.getAbstractNews());
				preparedstmt.setString(4, newsitemInfo.getUrl());
				//preparedstmt.setString(5, newsitemInfo.getDate());
				java.sql.Date sqlDate = new java.sql.Date(newsitemInfo.getDate().getTime());
				preparedstmt.setDate(5, sqlDate);
				preparedstmt.setString(6, newsitemInfo.getSource());
				preparedstmt.setInt(7, newsitemInfo.getNewsItemId());
			}
			preparedstmt.executeUpdate();
			preparedstmt.close();
			//conn.close();
			editTags(newsitemInfo);
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void editTags(NewsItemsAdminInformation newsitemInfo){
		int newsid = newsitemInfo.getNewsItemId();
		try{
		
			String query = "delete from newstagitem where NewsItemId = "+newsid;
			Statement stmt = conn.createStatement() ;
			stmt.executeUpdate(query);
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int industryN = userInformation.getUserSelectedIndustryID();
		String industry = userInformation.getIndustryNewsCenterName();
		ArrayList listtagName = newsitemInfo.getArrayTagList();
		
		Iterator iterate = listtagName.iterator();
		try
		{
			while(iterate.hasNext())
			{
					String tagName = (String)iterate.next();
					int tagId = getTagId(industry,tagName);
					String query ="insert into newstagitem(NewsItemId,TagItemId,IndustryEnumId)values("+newsid+","+tagId+","+industryN+")";
					Statement stmt = conn.createStatement() ;
					stmt.executeUpdate(query);
					stmt.close();
			}
					
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	public void removeFromSession(){
		HttpSession session = request.getSession(true);
		if(session!=null){
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			session.removeAttribute("userInfo");
		}
	}

	public String convertFile(String file){
		String convertedFileName = "C:/Program Files/input.csv";
		try {
			//File to store data in form of CSV
			File upfile = new File(convertedFileName);
			OutputStream os = (OutputStream)new FileOutputStream(upfile);
			String encoding = "UTF8";
			OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
		    BufferedWriter bw = new BufferedWriter(osw);
		    
		    //Excel document to be imported
		      String filename = file;
		      WorkbookSettings ws = new WorkbookSettings();
		      ws.setLocale(new Locale("en", "EN"));
		      Workbook w = Workbook.getWorkbook(new File(filename),ws);
		      
		      // Gets the sheets from workbook
		      for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++)
		      {
		        Sheet s = w.getSheet(sheet);

		        Cell[] row = null;
		        // Gets the cells from sheet
		        for (int i = 0 ; i < s.getRows() ; i++)
		        {
		          row = s.getRow(i);
		          if (row.length > 0)
		          {
		            bw.write(row[0].getContents());
		            for (int j = 1; j < row.length; j++)
		            {
		              bw.write(',');
		              bw.write(row[j].getContents());
		            }
		          }
		          bw.newLine();
		        }
		      }
		      bw.flush();
		      bw.close();
		}
		catch (FileNotFoundException e) {
			context.log("Exception in getSearchedUserInfoModify "+e.getMessage());
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
	    {
			context.log("Exception in getSearchedUserInfoModify "+e.getMessage());
	      System.err.println(e.toString());
	    }
	    catch (IOException e)
	    {
	    	context.log("Exception in getSearchedUserInfoModify "+e.getMessage());
	      System.err.println(e.toString());
	    }
	    catch (Exception e)
	    {
	    	context.log("Exception in getSearchedUserInfoModify "+e.getMessage());
	      System.err.println(e.toString());
	    }
	    return convertedFileName;
	}
	public ArrayList getCategoryNames(int industryId,String industryName)
	{
		ArrayList list = new ArrayList();
		TagItemInformation tagitem;
		ArrayList array;
		int parentId = -1;
		
		try
		{
			//if(industryId!=0){
			String query1 = "Select TagItemId from tagitem where Name='"+industryName+"' and IndustryId ="+industryId;
			Statement stmt = conn.createStatement();
			ResultSet rs1 = stmt.executeQuery(query1);
			while(rs1.next()){
				parentId = rs1.getInt("TagItemId");
			}
			rs1.close();
			String query = "SELECT TagItemId,Name,ParentId from tagitem where ParentId="+parentId+" and IndustryId ="+industryId;
			stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
	
				while (rs.next()) {
					tagitem = new TagItemInformation();
					tagitem.setTagItemId(rs.getInt("TagItemId"));
					tagitem.setTagName(rs.getString("Name"));
					tagitem.setParentId(rs.getInt("ParentId"));
					list.add(tagitem);
				}
				rs.close();
				stmt.close();
		}
	catch(Exception ex)
		{
		context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return list;
	}

	public int getParentId(int industryId,String industryName)
	{
		TagItemInformation tagitem;
		ArrayList array;
		int parentId = -1;
		
		try
		{
			//if(industryId!=0){
			String query1 = "Select TagItemId from tagitem where Name='"+industryName+"' and IndustryId ="+industryId;
			Statement stmt = conn.createStatement();
			ResultSet rs1 = stmt.executeQuery(query1);
			while(rs1.next()){
				parentId = rs1.getInt("TagItemId");
			}
			rs1.close();
			stmt.close();
		}
	catch(Exception ex)
		{
		context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return parentId;
	}
	
	public boolean saveNewTag(TagItemInformation tagItem,String parentName, boolean isCategory){
		try{
			int parentId = 0;
			HttpSession session = request.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			int industryid = userInformation.getUserSelectedIndustryID();
			String newscentername = userInformation.getIndustryNewsCenterName();
			int userid = userInformation.getUserId();
			if(!isCategory){
				String query2 = "select TagItemId from tagitem where Name='"+parentName+"' and IndustryId = "+industryid+" and ParentId = (select TagItemId from tagitem where Name = '"+newscentername+"')";
				Statement stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery(query2);
				while(rs2.next()){
					parentId = rs2.getInt("TagItemId");
				}
				rs2.close();
				stmt2.close();
			}
			else{
				parentId = tagItem.getParentId();
			}
			String query1 = "select * from tagitem where Name='"+tagItem.getTagName()+"' and ParentId="+parentId+" and IndustryId="+tagItem.getIndustryId();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query1);
			if(rs.next() == true){
				flag = false;
			}
			else{
				String query = "insert into tagitem(Name,ParentId,IndustryId,UserId) values('"+tagItem.getTagName()+"',"+parentId+","+tagItem.getIndustryId()+","+userid+")";
				//stmt = conn.createStatement();
				stmt.executeUpdate(query);
				flag = true;
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return flag;
	}

	public HashMap<Integer, TagItemInformation> getCategoryTagsInfo(String userSelectedIndustryName,int userSelectedIndustryId,int parentId, boolean isCategory){
		TagItemInformation tagItem;
		HashMap<Integer, TagItemInformation> tagInfoMap = new HashMap<Integer, TagItemInformation>();
		String query;
		try{
			if(parentId == 0 && !isCategory)
				query = "Select TagItemId,Name,ParentId,IndustryId from tagitem where ParentId>(SELECT TagItemId from tagitem where Name ='"+userSelectedIndustryName+"') and IndustryId="+userSelectedIndustryId;
			else
				query ="Select TagItemId,Name,ParentId,IndustryId from tagitem where ParentId="+parentId+" and IndustryId="+userSelectedIndustryId;
			Statement stmt = conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				tagItem = new TagItemInformation();
				tagItem.setTagItemId(rs.getInt("TagItemId"));
				tagItem.setTagName(rs.getString("Name"));
				tagItem.setParentId(rs.getInt("ParentId"));
				tagItem.setIndustryId(rs.getInt("IndustryId"));
				tagInfoMap.put(rs.getInt("TagItemId"), tagItem);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return tagInfoMap;
	}
	
	
	public HashMap<Integer, TagItemInformation> getCategoryTagsInformation(String categoryName,String userSelectedIndustryName,int userSelectedIndustryId,int parentId, boolean isCategory){
		TagItemInformation tagItem;
		HashMap<Integer, TagItemInformation> tagInfoMap = new HashMap<Integer, TagItemInformation>();
		String query;
		try{
			if(parentId == 0 && !isCategory)
				query = "Select TagItemId,Name,ParentId,IndustryId from tagitem where ParentId>(SELECT TagItemId from tagitem where Name ='"+userSelectedIndustryName+"') and IndustryId="+userSelectedIndustryId;
			else
				query ="Select TagItemId,Name,ParentId,IndustryId from tagitem where ParentId="+parentId+" and IndustryId="+userSelectedIndustryId;
			Statement stmt = conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				tagItem = new TagItemInformation();
				tagItem.setTagItemId(rs.getInt("TagItemId"));
				tagItem.setTagName(rs.getString("Name"));
				tagItem.setParentId(rs.getInt("ParentId"));
				tagItem.setIndustryId(rs.getInt("IndustryId"));
				tagInfoMap.put(rs.getInt("TagItemId"), tagItem);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return tagInfoMap;
	}

	public String setUserInfoAdminRegistration(AdminRegistrationInformation adminregInfo)
	{
		try
		{
			HttpSession session = request.getSession(false); 
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			int industryId = userInformation.getUserSelectedIndustryID();
			Statement stmt = conn.createStatement();
			String query = "select userid from user where email='"+adminregInfo.getEmail()+"'";
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()){
			 query = "update user set FirstName = '"+adminregInfo.getName()+"',LastName = '"+adminregInfo.getLastname()+"',Company = '"+adminregInfo.getCompanynm()+"',Title = '"+adminregInfo.getTitle()+"', PhoneNo = '"+adminregInfo.getPhoneno()+"', email = '"+adminregInfo.getEmail()+"', password = '"+adminregInfo.getPassword()+"', isApproved = 1, IndustryEnumId = "+adminregInfo.getIndustryid()+",isAdmin = 0,createdBy = "+userInformation.getUserId()+" where UserId = "+rs.getInt("UserId")+"";
			 stmt.executeUpdate(query);
			 setUserInfoSubscription(true,adminregInfo);
			}
			else{
				if(adminregInfo.getIsAdmin() == 2){
					query = "insert into user(FirstName, LastName, Company, title, PhoneNo, email, password, isApproved, IndustryEnumId,isAdmin,CreatedBy,Signature) values('"+adminregInfo.getName()+"', '"+adminregInfo.getLastname()+"', '"+adminregInfo.getCompanynm()+"', '"+adminregInfo.getTitle()+"', '"+adminregInfo.getPhoneno()+"', '"+adminregInfo.getEmail()+"', '"+adminregInfo.getPassword()+"', "+1+", "+industryId+", "+adminregInfo.getIsAdmin()+","+userInformation.getUserId()+",'MarketScape')";
				}
				else{
					query = "insert into user(FirstName, LastName, Company, title, PhoneNo, email, password, isApproved, IndustryEnumId,isAdmin,CreatedBy) values('"+adminregInfo.getName()+"', '"+adminregInfo.getLastname()+"', '"+adminregInfo.getCompanynm()+"', '"+adminregInfo.getTitle()+"', '"+adminregInfo.getPhoneno()+"', '"+adminregInfo.getEmail()+"', '"+adminregInfo.getPassword()+"', "+1+", "+industryId+", "+adminregInfo.getIsAdmin()+","+userInformation.getUserId()+")";		
				}
			 
			 stmt.executeUpdate(query);
			 setUserInfoSubscription(false,adminregInfo);
			}
			stmt.close();
			//setUserInfoSubscription(adminregInfo);
			return "true";
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			return "false";
		}
	}

	public void setUserInfoSubscription(Boolean isExisting, AdminRegistrationInformation adminregInfo)
	{
		try
		{
			String col="UserId";
			String tableName = "user";
			HttpSession session = request.getSession(false); 
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			String newsCenterName = userInformation.getIndustryNewsCenterName();
			int userId =getMaxValue(col,tableName);
			String newsCenter = newsCenterName.replace(" ", "");
			int newsCenterId = getnewsCenterId(newsCenter+".NewsCenter.com");
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		    Date date = new Date();
		    String str = format.format(date);
		    System.out.println("Today is " + str);
		    String query;
		    Statement stmt = conn.createStatement() ;
		    
		    if(!isExisting){
			  query = "insert into usersubscription(UserId,Duration,ApprovalDate,DurationLeft,NewsCenterId) values('"+userId+"',"+adminregInfo.getDuration()+",'"+str+"',"+adminregInfo.getDuration()+","+newsCenterId+")";
			  stmt.executeUpdate(query);
			  
			  DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			  query = "insert into useraccounthistory(userID,operation,operationDate,operationPerformBy) values("+userId+",'subscribed','"+df.format(new Date())+"',"+userInformation.getUserId()+")";
			  stmt.executeUpdate(query);
		    }
		    else{
		      query = "update usersubscription set Duration = "+adminregInfo.getDuration()+", ApprovalDate = '"+str+"' , ExtensionDate = NULL, isExtended = NULL, ExtendedDuration = NULL, DurationLeft = "+adminregInfo.getDuration()+", NewsCenterId = "+adminregInfo.getIndustryid()+" where userid = "+adminregInfo.getUserid()+"";
		      stmt.executeUpdate(query);
		      
		      DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			  query = "insert into useraccounthistory(userID,message,operation,operationDate,operationPerformBy) values("+adminregInfo.getUserid()+",'Trial user converted into subscribed user','subscribed','"+df.format(new Date())+"',"+userInformation.getUserId()+")";
			  stmt.executeUpdate(query);
		      
		    }
			
			
			stmt.close();
			
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public String[] getIndustryNameFromSession(){
		String industryName[]= new String[4];
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int industryN = userInformation.getUserSelectedIndustryID();
		String useremail= userInformation.getEmail();
		String userSelectedIndustry = userInformation.getIndustryNewsCenterName();
		
		industryName[0] = getUserName(useremail);
		industryName[2] = String.valueOf(industryN);
		industryName[3] = userSelectedIndustry;
		try
		{
			String query="SELECT Name from industryenum where IndustryEnumId= 1";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				industryName[1] = rs.getString("Name");
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return industryName;
	}

	public String getUserName(String email)
	{
		String name="";
		try
		{
			String query="SELECT FirstName from user where email='"+email+"'";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				name = rs.getString("FirstName");
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return name;
	}
	
	public void deleteTags(HashMap hashmap, boolean isCategory){
		try{
			HttpSession session = request.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			int newscenterid = userInformation.getUserSelectedNewsCenterID();
			int userid = userInformation.getUserId();
			String newscentername = userInformation.getIndustryNewsCenterName();
			
			for(Object obj:hashmap.keySet())
			{
				Statement stmt = conn.createStatement();
				int id = (Integer)obj;
				if(!isCategory){
					String query1 = "delete from newstagitem where TagItemId="+id;
					stmt.executeUpdate(query1);
					//stmt.close();

				}
				else{
					String query1 = "delete from tagitem where ParentId="+id;
					//Statement stmt = conn.createStatement();
					stmt.executeUpdate(query1);
					//stmt.close();
				}
				String query2 = "delete from tagitem where TagItemId="+id;
				stmt = conn.createStatement();
				stmt.executeUpdate(query2);	
				stmt.close();
			}
		}
		catch(Exception ex)
		{
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void updateTags(HashMap<Integer,TagItemInformation> hashmap){
		try{
			for(Object obj: hashmap.keySet()){
				int id = (Integer)obj;
				TagItemInformation tag = hashmap.get(obj);
				String query ="update tagitem set Name='"+tag.getTagName()+"'where TagItemId ="+id;
				Statement stmt=conn.createStatement();
				stmt.executeUpdate(query);
				stmt.close();
			}
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
	}

	public int getCategoryId(String categoryName){
		int parentId = 0;
		try{
			HttpSession session = request.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			
			String query = "select TagItemId from tagitem where Name='"+categoryName+"' and IndustryId="+userInformation.getUserSelectedIndustryID()+" and ParentId = (select TagItemId from tagitem where Name = '"+userInformation.getIndustryNewsCenterName()+ "')";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				parentId = rs.getInt("TagItemId");
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		return parentId;
	}
	
	public void updateSubscriptionDuration(ArrayList<AdminRegistrationInformation> userlist) {
		try{
			Iterator iter = userlist.iterator();
			while(iter.hasNext()){
				AdminRegistrationInformation user = (AdminRegistrationInformation)iter.next();
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
				    query ="update usersubscription set Duration = Duration = 3,DurationLeft = "+user.getDuration()+",ExtensionDate = '"+extendedDate+"', isExtended = "+user.isExtension()+", ExtendedDuration = "+user.getDuration()+" where UserId ="+user.getUserid();
				    stmt.executeUpdate(query);
				//String query ="insert into usersubscription(UserId,Duration,NewsCenterId,ApprovalDate) set Duration ='"+user.getDuration()+"'where UserId ="+user.getUserid();
				
				HttpSession session = request.getSession(false);
				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				query = "insert into useraccounthistory(userID,operation,operationDate,operationPerformBy) values("+user.getUserid()+",'extends','"+df.format(new Date())+"',"+userInformation.getUserId()+")";
				stmt.executeUpdate(query);
				
				stmt.close();
			}
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void deleteUserSubscription(AdminRegistrationInformation user) {
		try{
		String query = "select u.email, u.IndustryEnumId from user u, usersubscription us where u.UserId =" + user.getUserid() + " and u.UserId = us.UserId";
		Statement stmt = (Statement) conn.createStatement();
		ResultSet rs = (ResultSet) stmt.executeQuery(query);
		while(rs.next()){
			String email = rs.getString("email");
			int industryEnum = rs.getInt("IndustryEnumId");
			String query2 = "select Name from industryenum where IndustryEnumId= "+industryEnum;
			stmt = (Statement) conn.createStatement();
			ResultSet rs1 = (ResultSet) stmt.executeQuery(query2);
			while(rs1.next()){
				String newscenterName = rs1.getString("Name");
				//sendMail(email, newscenterName);
			}
			rs1.close();
		}
		stmt = (Statement) conn.createStatement();
		query = "delete from loginStatistics where userId = "+user.getUserid();
		stmt.executeUpdate(query);
		query = "delete from useraccounthistory where userId = "+user.getUserid();
		stmt.executeUpdate(query);
		query = "delete from useritemaccessstats where userId = "+user.getUserid();
		stmt.executeUpdate(query);
		query = "delete from usersubscription where userId = "+user.getUserid();
		stmt.executeUpdate(query);
		query = "delete from usertagselection where userId = "+user.getUserid();
		stmt.executeUpdate(query);
		query = "delete from user where userId = "+user.getUserid();
		stmt.executeUpdate(query);
		/*String delquery = "delete u, us from user u, usersubscription us where u.UserId=us.UserId and u.UserId="+user.getUserid();
		stmt.executeUpdate(delquery);*/
		rs.close();
		}
		catch(Exception ex){
			context.log("Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	public void sendMail(String email, String newscenterName){
		UserAdminInformation adminInformation = new UserAdminInformation();
		adminInformation.setBodyMail("Hi \n\n\n Your account for the NewsCatalyst "+newscenterName+" has expired.");
		adminInformation.setRecipientsMail(email);
		adminInformation.setSenderMail("no_reply@newscatalyst.com");
		adminInformation.setSubjectMail("NewsCatalyst Account validity");
		UserInformationforMail mailService = new UserInformationforMail();
		
		mailService.setSmtpusername(context.getInitParameter("smtpusername"));
		mailService.setSmtppassword(context.getInitParameter("smtppassword"));
		mailService.setSmtphost(context.getInitParameter("smtphost"));
		mailService.setSmtpport(context.getInitParameter("smtpport"));
		mailService.sendMailForApproval(adminInformation);
	}
	
	public InputStream getInputstream() {
		return inputstream;
	}

	public void setInputstream(InputStream inputstream) {
		this.inputstream = inputstream;
	}

	public File getFolderImage() {
		return folderImage;
	}

	public void setFolderImage(File folderImage) {
		this.folderImage = folderImage;
	}

	public String getFolderUrl() {
		return folderUrl;
	}

	public void setFolderUrl(String folderUrl) {
		this.folderUrl = folderUrl;
	}

	public byte[] getB() {
		return b;
	}

	public void setB(byte[] b) {
		this.b = b;
	}

	public void saveEmailTemplate(String emailtemplate, int newscenterid) {
		try{
		 Statement stmt = getConnection().createStatement();
		 String query = "update industryenum set emailTemplate = '"+emailtemplate+"' where IndustryEnumId = "+newscenterid+"";
		 stmt.executeUpdate(query);
		 stmt.close();
		}
		catch(Exception e){
			context.log("Exception in getSearchedUserInfoModify "+e.getMessage());
			e.printStackTrace();
		}
	  
		
	}

	public PageResult getSearchedUserInfoModify(PageCriteria crt,String columnname, String search) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		AdminRegistrationInformation userinfo;
		int industryId = userInformation.getUserSelectedIndustryID();
		String userSelectedIndustry;
		int duration;
		int durationleft;
		ArrayList userlist= new ArrayList();
		//ArrayList array;
		try{
			String smtpusername;
			String smtppassword;
			String smtphost;
			String smtpport;
			String query = "select UserId,FirstName,LastName,email,PhoneNo,Company,isApproved from user where isAdmin ="+0+" and IndustryEnumId="+industryId+ " and "+columnname+" like '%"+search+"%'";
			//String query = "select UserId,FirstName,LastName,email,PhoneNo,Company,isApproved from user where roleId in (select id from role where name = User) and IndustryEnumId="+industryId;
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
				
				query = "select UserId,FirstName,LastName,email,PhoneNo,Company,isApproved from user where isAdmin ="+0+" and IndustryEnumId="+industryId+" and "+columnname+" like '%"+search+"%' LIMIT "+startRecord+","+pagesize+"";
				ResultSet rss = stmt.executeQuery(query);
				
				while(rss.next()){
					userinfo = new AdminRegistrationInformation();
					userinfo.setUserid(rss.getInt("UserId"));
					userinfo.setName(rss.getString("FirstName"));
					userinfo.setLastname(rss.getString("LastName"));
					userinfo.setEmail(rss.getString("email"));
					userinfo.setPhoneno(rss.getLong("PhoneNo"));
					userinfo.setCompanynm(rss.getString("Company"));
					userinfo.setIsApproved(rss.getInt("isApproved"));
	
					userSelectedIndustry = getUserSelectedIndustry(rss.getInt("UserId"));
					duration = getUserDuration(rss.getInt("UserId"));
					durationleft = getUserDurationLeft(rss.getInt("userId"));
					userinfo.setDuration(duration);
					userinfo.setDurationLeft(durationleft);
					userlist.add(userinfo);
				}
				pageresult.setCurrentPage(currentPage);
				double divison = Math.ceil((double) listSize / (double) pagesize);
				pageresult.setTotalNoOfPages((int) divison);
				pageresult.setList(userlist);
				rs.close();
				stmt.close();
		     }
			else{
				pageresult.setList(userlist);
			}
			
			}
			catch(Exception e){
				context.log("Exception in getSearchedUserInfoModify "+e.getMessage());
				e.printStackTrace();
			}
			return pageresult;

	}

	public PageResult getSortedUserInfoModify(PageCriteria crt,String columnname, String mode) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		AdminRegistrationInformation userinfo;
		int industryId = userInformation.getUserSelectedIndustryID();
		String userSelectedIndustry;
		int duration;
		int durationleft;
		ArrayList userlist= new ArrayList();
		//ArrayList array;
		try{
			String query = "select UserId,FirstName,LastName,email,PhoneNo,Company,isApproved from user where isAdmin ="+0+" and IndustryEnumId="+industryId+ " order by "+columnname+" "+mode+"";
			//String query = "select UserId,FirstName,LastName,email,PhoneNo,Company,isApproved from user where roleId in (select id from role where name = User) and IndustryEnumId="+industryId;
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
			
			query = "select UserId,FirstName,LastName,email,PhoneNo,Company,isApproved from user where isAdmin ="+0+" and IndustryEnumId="+industryId+" order by "+columnname+" "+mode+" LIMIT "+startRecord+","+pagesize+"";
			ResultSet rss = stmt.executeQuery(query);
			
			while(rss.next()){
				userinfo = new AdminRegistrationInformation();
				userinfo.setUserid(rss.getInt("UserId"));
				userinfo.setName(rss.getString("FirstName"));
				userinfo.setLastname(rss.getString("LastName"));
				userinfo.setEmail(rss.getString("email"));
				userinfo.setPhoneno(rss.getLong("PhoneNo"));
				userinfo.setCompanynm(rss.getString("Company"));
				userinfo.setIsApproved(rss.getInt("isApproved"));

				userSelectedIndustry = getUserSelectedIndustry(rss.getInt("UserId"));
				duration = getUserDuration(rss.getInt("UserId"));
				durationleft = getUserDurationLeft(rss.getInt("userId"));
				userinfo.setDuration(duration);
				userinfo.setDurationLeft(durationleft);
				userlist.add(userinfo);
			}
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(userlist);
			}
			rs.close();
			stmt.close();
			}
			catch(Exception e){
				context.log("Exception in getSortedUserInfoModify "+e.getMessage());
				e.printStackTrace();
			}
			return pageresult;
	}
}