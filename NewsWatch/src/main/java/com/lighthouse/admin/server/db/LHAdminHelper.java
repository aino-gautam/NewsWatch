package com.lighthouse.admin.server.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.admin.client.AdminRegistrationInformation;
import com.admin.client.TagItemInformation;
import com.admin.client.UserAdminInformation;
import com.appUtils.server.helper.AppUtilsHelper;
import com.lighthouse.admin.client.domain.LHNewsItems;
import com.lighthouse.admin.client.domain.LHNewsItemsAdminInformation;
import com.lighthouse.login.user.client.domain.LhUser;
import com.login.client.UserInformation;
import com.login.server.db.AllocateResources;



import com.newscenter.server.db.DBHelper;

public class LHAdminHelper extends DBHelper {
	
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private ServletContext context;
	private String tomcatpath;
	File file;
	String folderUrl;
	byte[] b;
	private UserAdminInformation useradmininfo = new UserAdminInformation();
	public static boolean flag = true;
	Logger logger = Logger.getLogger(LHAdminHelper.class.getName());
	
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
	
	public LHAdminHelper(){
		
	}
	
	public LHAdminHelper(HttpServletRequest req  , HttpServletResponse res){
		try {
			request = req ;
			response = res ;
			context = req.getSession().getServletContext();
			tomcatpath = (String)context.getAttribute(AllocateResources.TOMCATPATH);
			setFolderUrl(tomcatpath+"/imagefolder");
			//setFolderImage(new File(getFolderUrl()));
		}
		catch(Exception ex){
			logger.log(Level.SEVERE, "Exception in LHAdminHelper constructor!" + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/**
	 * validates the user
	 * @return LhUser
	 */
	public LhUser validateUser(){
		logger.log(Level.INFO, "[LHAdminHelper :: validateUser() initiated ]");
		HttpSession session = request.getSession(false);
		if(session!=null){
			LhUser userInformation  = (LhUser)session.getAttribute("userInfo");
			if(userInformation==null)
				return null;
			return userInformation;
		}
		else
			return null;
	}
	
	public void setAllNewsItemFields(LHNewsItemsAdminInformation newsitemInfo, InputStream inputstream){
		logger.log(Level.INFO, "[LHAdminHelper :: setAllNewsItemFields() initiated ]");
		PreparedStatement  prestmt = null;
		try{
			Connection conn = (Connection) getConnection();
			
			String query1 = "SELECT id FROM news_priority where level='"+newsitemInfo.getNewsPriority()+"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query1);
			int newsPriority = 0;
			while(rs.next()){
				newsPriority = rs.getInt("id");
			}
			rs.close();
			stmt.close();
			
			String query ="insert into newsitem(Content,Title,Abstract,URL,ItemDate,Source,NewsImages,isLocked,priorityLevel,author) values(?,?,?,?,?,?,?,?,?,?)";
			prestmt = (PreparedStatement)conn.prepareStatement(query);
			prestmt.setString(1, newsitemInfo.getContent());
			prestmt.setString(2, newsitemInfo.getNewsTitle());
			prestmt.setString(3, newsitemInfo.getAbstractNews());
			prestmt.setString(4, newsitemInfo.getUrl());
			java.sql.Date sqlDate = new java.sql.Date(newsitemInfo.getDate().getTime());
			prestmt.setDate(5, sqlDate);
			prestmt.setString(6, newsitemInfo.getSource());
			prestmt.setBinaryStream( 7, inputstream, (int)newsitemInfo.getFilelength());
			prestmt.setInt(8, newsitemInfo.getIsLocked());
			prestmt.setInt(9, newsPriority);
			prestmt.setString(10, newsitemInfo.getAuthor());
			prestmt.executeUpdate();
			inputstream.close();
			prestmt.close();
			prestmt = null;
			setTagIdAndNewsItemId(newsitemInfo);
			logger.log(Level.INFO, "[LHAdminHelper :: setAllNewsItemFields() completed ]");
		}
		catch(Exception ex){
			logger.log(Level.SEVERE, "[LHAdminHelper :: setAllNewsItemFields() Exception!!! ] "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void setTagIdAndNewsItemId(LHNewsItemsAdminInformation newsitemInfo){
		logger.log(Level.INFO, "[LHAdminHelper :: setTagIdAndNewsItemId() iniitated ]");
		String col="NewsItemId";
		String tableName ="newsitem";
		HttpSession session = request.getSession(false);
		Connection conn = (Connection) getConnection();
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int industryN = userInformation.getUserSelectedIndustryID();
		String industry = userInformation.getIndustryNewsCenterName();
		ArrayList listtagName = newsitemInfo.getArrayTagList();
		
		Iterator iterate = listtagName.iterator();
		
		int maxCount = getMaxValue(col,tableName);
		try{
			while(iterate.hasNext()){
					String tagName = (String)iterate.next();
					int tagId = getTagId(industry,tagName);
					String query ="insert into newstagitem(NewsItemId,TagItemId,IndustryEnumId)values("+maxCount+","+tagId+","+industryN+")";
					Statement stmt = conn.createStatement() ;
					stmt.executeUpdate(query);
					stmt.close();
			}
					
		}
		catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: setTagIdAndNewsItemId() Exception"+ex.getMessage()+" ]");
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: setTagIdAndNewsItemId() completed ]");
	}
	
	public int getTagId(String industry,String tag){
		logger.log(Level.INFO, "[LHAdminHelper :: getTagId() initiated ]");
		int id=0;
		try{
			Connection conn = (Connection) getConnection();
			String query ="SELECT TagItemId from tagitem where Name = '"+tag+"' and IndustryId=(SELECT IndustryEnumId from industryenum where Name='"+industry+"')";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				id = rs.getInt("TagItemId");
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			logger.log(Level.SEVERE," LHAdminHelper Exception in getTagId "+ex.getMessage());
			ex.printStackTrace(); 
		}
		logger.log(Level.INFO, "[LHAdminHelper :: getTagId() completed ]");
		return id;
	}
	
	public int getMaxValue(String col,String tableName){
		logger.log(Level.INFO, "[LHAdminHelper :: getMaxValue() initiated ]");
		int maxValue=0;
		try{
			Connection conn = (Connection) getConnection();
			String query ="SELECT max("+col+") from "+tableName;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				maxValue = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			logger.log(Level.SEVERE," LHAdminHelper Exception in getMaxValue "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: getMaxValue() completed ]");
		return maxValue;
	}
	
	public void deleteSelectedTags(HashMap hashmap, boolean isCategory){
		logger.log(Level.INFO, "[LHAdminHelper :: deleteSelectedTags() initiated ]");
		try{
			Connection conn = (Connection) getConnection();
			HttpSession session = request.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			int newscenterid = userInformation.getUserSelectedNewsCenterID();
			int userid = userInformation.getUserId();
			String newscentername = userInformation.getIndustryNewsCenterName();
			
			for(Object obj:hashmap.keySet()){
				Statement stmt = conn.createStatement();
				int id = (Integer)obj;
				if(!isCategory){
					String query1 = "delete from newstagitem where TagItemId="+id;
					stmt.executeUpdate(query1);
				}
				else{
					String query1 = "delete from tagitem where ParentId="+id;
					stmt.executeUpdate(query1);
				}
				String query2 = "delete from tagitem where TagItemId="+id;
				stmt = conn.createStatement();
				stmt.executeUpdate(query2);	
				stmt.close();
			}
		}
		catch(Exception ex){
			logger.log(Level.SEVERE, "[LHAdminHelper ::Exception in deleteSelectedTags "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: deleteSelectedTags() completed ]");
	}
	
	public HashMap<Integer, TagItemInformation> getCategoryTagsInfo(String userSelectedIndustryName,int userSelectedIndustryId,int parentId, boolean isCategory){
		logger.log(Level.INFO, "[LHAdminHelper :: getCategoryTagsInfo() initiated ]");
		TagItemInformation tagItem;
		HashMap<Integer, TagItemInformation> tagInfoMap = new HashMap<Integer, TagItemInformation>();
		String query;
		try{
			Connection conn = (Connection) getConnection();
			if(parentId == 0 && !isCategory)
				query = "Select TagItemId,Name,ParentId,IndustryId,isPrimary from tagitem where ParentId>(SELECT TagItemId from tagitem where Name ='"+userSelectedIndustryName+"' and ParentId is null) and IndustryId="+userSelectedIndustryId;
			else
				query ="Select TagItemId,Name,ParentId,IndustryId,isPrimary from tagitem where ParentId="+parentId+" and IndustryId="+userSelectedIndustryId;
			Statement stmt = conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next())
			{
				tagItem = new TagItemInformation();
				tagItem.setTagItemId(rs.getInt("TagItemId"));
				tagItem.setTagName(rs.getString("Name"));
				tagItem.setParentId(rs.getInt("ParentId"));
				tagItem.setIndustryId(rs.getInt("IndustryId"));
				tagItem.setIsprimary(rs.getBoolean("isPrimary"));
				tagInfoMap.put(rs.getInt("TagItemId"), tagItem);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in getCategoryTagsInfo "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: getCategoryTagsInfo() completed ]");
		return tagInfoMap;
	}
	
	public HashMap<String, Serializable> getCategoryNames(ServletContext context2){
		logger.log(Level.INFO, "[LHAdminHelper :: getCategoryNames() initiated ]");
		Connection conn = (Connection) getConnection();
		HttpSession session = request.getSession(false);
		UserInformation userinfo  = (UserInformation)session.getAttribute("userInfo");
		int industryID = userinfo.getUserSelectedIndustryID();
		String industryName =  userinfo.getIndustryNewsCenterName();
		HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		ArrayList<TagItemInformation> tagitemList = new ArrayList<TagItemInformation>();
		int parentId = -1;
		boolean val = false,isPrimaryDefined = false;
		try{
			String query1 = "Select TagItemId,isPrimary from tagitem where Name='"+industryName+"' and IndustryId ="+industryID;
			Statement stmt = conn.createStatement();
			ResultSet rs1 = stmt.executeQuery(query1);
			while(rs1.next()){
				parentId = rs1.getInt("TagItemId");
			}
			rs1.close();
			String query = "SELECT TagItemId,Name,ParentId,isPrimary from tagitem where ParentId="+parentId+" and IndustryId ="+industryID;
			stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
				while (rs.next()) {
					TagItemInformation tagitem = new TagItemInformation();
					tagitem.setTagItemId(rs.getInt("TagItemId"));
					tagitem.setTagName(rs.getString("Name"));
					tagitem.setParentId(rs.getInt("ParentId"));
					tagitem.setIndustryId(industryID);
					val = rs.getBoolean("isPrimary");
					tagitem.setIsprimary(val);
					tagitemList.add(tagitem);
					if(val)
						isPrimaryDefined = true;
				}
				int count = getTagsCount(industryName);
				map.put("isPrimaryDefined", isPrimaryDefined);
				map.put("tagList", tagitemList);
				map.put("tagsCount", count);
				map.put("tagsLimit",new Integer(context2.getInitParameter("tagLimitForPrimaryCategory")));
				rs.close();
				stmt.close();
		}catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in getCategoryNames "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: getCategoryNames() completed ]");
		return map;
	}
	
	private int getTagsCount(String industryName) {
		logger.log(Level.INFO, "[LHAdminHelper :: getTagsCount() initiated ]");
		int tagCount=0;
		ResultSet rs;
		try {
			Connection conn = (Connection) getConnection();
			String query = "select count(TagItemId) from tagitem where ParentId in(select distinct TagItemId from tagitem where ParentId=(select TagItemId from tagitem where Name = '"+industryName+"' and ParentId is null) and isPrimary=1)";
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()){
				tagCount = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "[LHAdminHelper :: Exception in getTagsCount "+e.getMessage());
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: getTagsCount() completed ]");
		return tagCount;
	}
	
	public void editNewsItemFields(LHNewsItemsAdminInformation newsitemInfo, InputStream inputstream){
		logger.log(Level.INFO, "[LHAdminHelper :: editNewsItemFields() initiated ]");
		PreparedStatement preparedstmt = null;
		try{
			Connection conn = (Connection) getConnection();
			String query = "";
			
			String query1 = "SELECT id FROM news_priority where level='"+newsitemInfo.getNewsPriority()+"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query1);
			int newsPriority = 0;
			while(rs.next()){
				newsPriority = rs.getInt("id");
			}
			rs.close();
			stmt.close();
			
			if(newsitemInfo.getFilelength()!=0){
				query ="update newsitem set Content=?,Title=?,Abstract=?,URL=?,ItemDate=?,Source=?,NewsImages=?,isLocked=?, priorityLevel=?,author=? where NewsItemId = ?";
				preparedstmt = (PreparedStatement)conn.prepareStatement(query);
				preparedstmt.setString(1, newsitemInfo.getContent());
				preparedstmt.setString(2, newsitemInfo.getNewsTitle());
				preparedstmt.setString(3, newsitemInfo.getAbstractNews());
				preparedstmt.setString(4, newsitemInfo.getUrl());
				java.sql.Date sqlDate = new java.sql.Date(newsitemInfo.getDate().getTime());
				preparedstmt.setDate(5, sqlDate);
				preparedstmt.setString(6, newsitemInfo.getSource());
				preparedstmt.setBinaryStream(7, inputstream, newsitemInfo.getFilelength());
				preparedstmt.setInt(8, newsitemInfo.getIsLocked());
				preparedstmt.setInt(9, newsPriority);
				preparedstmt.setString(10, newsitemInfo.getAuthor());
				preparedstmt.setInt(11, newsitemInfo.getNewsItemId());
				
			}
			else{
				query ="update newsitem set Content=?,Title=?,Abstract=?,URL=?,ItemDate=?,Source=?,isLocked=?, priorityLevel=?,author=? where NewsItemId = ?";
				preparedstmt = (PreparedStatement)conn.prepareStatement(query);
				preparedstmt.setString(1, newsitemInfo.getContent());
				preparedstmt.setString(2, newsitemInfo.getNewsTitle());
				preparedstmt.setString(3, newsitemInfo.getAbstractNews());
				preparedstmt.setString(4, newsitemInfo.getUrl());
				java.sql.Date sqlDate = new java.sql.Date(newsitemInfo.getDate().getTime());
				preparedstmt.setDate(5, sqlDate);
				preparedstmt.setString(6, newsitemInfo.getSource());
				preparedstmt.setInt(7, newsitemInfo.getIsLocked());
				preparedstmt.setInt(8, newsPriority);
				preparedstmt.setString(9, newsitemInfo.getAuthor());
				preparedstmt.setInt(10, newsitemInfo.getNewsItemId());
			}
			preparedstmt.executeUpdate();
			preparedstmt.close();
			editTags(newsitemInfo);
		}
		catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in editNewsItemFields "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: editNewsItemFields() completed ]");
	}
	
	public void editTags(LHNewsItemsAdminInformation newsitemInfo){
		logger.log(Level.INFO, "[LHAdminHelper :: editTags() initiated ]");
		int newsid = newsitemInfo.getNewsItemId();
		Connection conn = (Connection) getConnection();
		try{
			String query = "delete from newstagitem where NewsItemId = "+newsid;
			Statement stmt = conn.createStatement() ;
			stmt.executeUpdate(query);
			stmt.close();
		}
		catch(Exception ex){
			context.log("Exception in editTags "+ex.getMessage());
			ex.printStackTrace();
		}
		
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int industryN = userInformation.getUserSelectedIndustryID();
		String industry = userInformation.getIndustryNewsCenterName();
		ArrayList listtagName = newsitemInfo.getArrayTagList();
		Iterator iterate = listtagName.iterator();
		try{
			while(iterate.hasNext()){
				String tagName = (String)iterate.next();
				int tagId = getTagId(industry,tagName);
				String query ="insert into newstagitem(NewsItemId,TagItemId,IndustryEnumId)values("+newsid+","+tagId+","+industryN+")";
				Statement stmt = conn.createStatement() ;
				stmt.executeUpdate(query);
				stmt.close();
			}
		}
		catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in editTags "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: editTags() completed ]");
	}

	public ArrayList getNewsItemForThisTag(String industryName, String tagName) {
		logger.log(Level.INFO, "[LHAdminHelper :: getNewsItemForThisTag() initiated ]");
		int idNewsItem = getNewsItemId(industryName,tagName);
		LHNewsItems newsItem;
		TagItemInformation tagitem;
		ArrayList list = new ArrayList();
		AppUtilsHelper appUtilsHelper = new AppUtilsHelper();
		
		if(!appUtilsHelper.alreadyExists(getFolderUrl())){
			File folderImage = new File(getFolderUrl());
			folderImage.mkdir();
		}
		
		
		try{
			Connection conn = (Connection) getConnection();
			int newsid = 0;
			String query = "select * from newsitem N INNER JOIN newstagitem NT on N.NewsItemId = NT.NewsItemId where TagItemId ="+idNewsItem+" and N.isReport=0";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				newsItem = new LHNewsItems();
				newsid = rs.getInt("NewsItemId");
				int newsPriority = rs.getInt("priorityLevel");
				
				String query1 = "select * from tagitem where TagItemId in(select TagItemId from newstagitem where NewsItemId="+newsid+")";
				Statement stmt1=conn.createStatement();
				ResultSet rs1=stmt1.executeQuery(query1);
				while(rs1.next()){
					tagitem = new TagItemInformation();
					tagitem.setTagItemId(rs1.getInt("TagItemId"));
					tagitem.setTagName(rs1.getString("Name"));
					tagitem.setParentId(rs1.getInt("ParentId"));
					tagitem.setIndustryId(rs1.getInt("IndustryId"));
					tagitem.setIsprimary(rs1.getBoolean("isPrimary"));
					newsItem.addTagforNews(tagitem);
				}
				rs1.close();
				stmt1.close();
				
				String query2 = "SELECT level FROM news_priority where id = "+newsPriority;
				Statement stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery(query2);
				while(rs2.next()){
					newsItem.setNewsPriority(rs2.getString("level"));
				}
				
				newsItem.setNewsId(newsid);
				newsItem.setNewsTitle(rs.getString("Title"));
				newsItem.setNewsContent(rs.getString("Content"));
				newsItem.setAbstractNews(rs.getString("Abstract"));
				newsItem.setUrl(rs.getString("URL"));
				newsItem.setAuthor(rs.getString("author"));
				newsItem.setNewsDate(rs.getDate("ItemDate"));
				newsItem.setNewsSource(rs.getString("Source"));
				newsItem.setIsLocked(rs.getInt("isLocked"));
				
				file = new File(folderUrl+File.separator+newsid+".jpg");
				context.log("file path in userhelperadmin "+file.getPath());
				System.out.println("file path in userhelperadmin "+file.getPath());
				String imageurlTomcatFolder = "imagefolder";
				//	String imageurl = folderUrl+"\\"+newsid+".jpg";
				String imageurl = imageurlTomcatFolder+"/"+newsid+".jpg";
				context.log("image url :::: " +imageurl);
				System.out.println("image url :::: " +imageurl);
				newsItem.setImageUrl(imageurl);
				if(rs.getBlob("NewsImages") != null){
					try{
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
					catch (Exception e) {
					//	logger.log(Level.INFO, "[LHAdminHelper :: getNewsItemForThisTag :: Exception ",e);
					}
				}
				list.add(newsItem);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in getNewsItemForThisTag "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper ::List size for edit news:::: "+list.size());
		logger.log(Level.INFO, "[LHAdminHelper :: getNewsItemForThisTag() completed ]");
		return list;
	}
	
	public int getNewsItemId(String industryName,String tagName){
		logger.log(Level.INFO, "[LHAdminHelper :: getNewsItemId() initiated ]");
		int id = 0;
		try{
			Connection conn = (Connection) getConnection();
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
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in getNewsItemId "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: getNewsItemId() completed ]");
		return id;
	}

	public ArrayList getCategoryNames(int industryid, String industryName) {
		logger.log(Level.INFO, "[LHAdminHelper :: getCategoryNames() initiated ]");
		ArrayList list = new ArrayList();
		TagItemInformation tagitem;
		ArrayList array;
		int parentId = -1;
		try{
			Connection conn = (Connection) getConnection();
			String query1 = "Select TagItemId from tagitem where Name='"+industryName+"' and IndustryId ="+industryid+" and ParentId is null";
			Statement stmt = conn.createStatement();
			ResultSet rs1 = stmt.executeQuery(query1);
			while(rs1.next()){
				parentId = rs1.getInt("TagItemId");
			}
			rs1.close();
			String query = "SELECT TagItemId,Name,ParentId,isPrimary from tagitem where ParentId="+parentId+" and IndustryId ="+industryid;
			stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while (rs.next()) {
				tagitem = new TagItemInformation();
				tagitem.setTagItemId(rs.getInt("TagItemId"));
				tagitem.setTagName(rs.getString("Name"));
				tagitem.setParentId(rs.getInt("ParentId"));
				tagitem.setIsprimary(rs.getBoolean("isPrimary"));
				list.add(tagitem);
			}
			rs.close();
			stmt.close();
		}catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in getCategoryNames "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: getCategoryNames() completed ]");
		return list;
	}

	public ArrayList getTagName(String industryName, String categoryName) {
		logger.log(Level.INFO, "[LHAdminHelper :: getTagName() initiated ]");
		ArrayList list = new ArrayList();
		ArrayList array;
		int categoryparentid = 0;
		try{
			Connection conn = (Connection) getConnection();
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
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in getTagName "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: getTagName() completed ]");
		return list;
	}

	public void deleteNewsItems(HashMap hashmap) {
		logger.log(Level.INFO, "[LHAdminHelper :: deleteNewsItems() initiated ]");
		try{
			Connection conn = (Connection) getConnection();
			for(Object obj:hashmap.keySet()){
				Statement stmt=conn.createStatement();
				Long id = (Long)obj;
				String query = "delete from useritemaccessstats where newsitemId = "+id;
				stmt.executeUpdate(query);
				query ="DELETE news,tag from newsitem AS news,newstagitem AS tag where news.NewsItemId = tag.NewsItemId and news.NewsItemId="+id;
				stmt.executeUpdate(query);
				stmt.close();
			}
		}catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper ::  Exception in deleteNewsItems "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: deleteNewsItems() completed ]");
	}

	public ArrayList fillprimaryTaglist(int industryId, String industryName) {
		logger.log(Level.INFO, "[LHAdminHelper :: fillprimaryTaglist() initiated ]");
		ArrayList list = new ArrayList();
		TagItemInformation tagitem;
		ArrayList array;
		int parentId = -1;
		try{
			Connection conn = (Connection) getConnection();
			Statement stmt = conn.createStatement();
			//String query = "SELECT TagItemId,Name,ParentId,isPrimary from tagitem where isPrimary=1 and Name='"+industryName+"' and IndustryId = "+industryId;
			String query ="SELECT * FROM tagitem t where isPrimary=1 and ParentId in(select TagItemId from tagitem where ParentId in(select TagItemId from tagitem where IndustryId = "+industryId+ " and Name='"+industryName+"')) order by t.orderid desc";
			stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
	
				while (rs.next()) {
					tagitem = new TagItemInformation();
					tagitem.setTagItemId(rs.getInt("TagItemId"));
					tagitem.setTagName(rs.getString("Name"));
					tagitem.setParentId(rs.getInt("ParentId"));
					tagitem.setIsprimary(rs.getBoolean("isPrimary"));
					list.add(tagitem);
				}
				rs.close();
				stmt.close();
		}catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in fillprimaryTaglist "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: fillprimaryTaglist() completed ]");
		return list;
	}

	public boolean checkprimarycategory(String parentName) {
		logger.log(Level.INFO, "[LHAdminHelper :: checkprimarycategory() initiated ]");
		boolean val=false;
		try {
			Connection connection = (Connection) getConnection();
			String sql = "SELECT isPrimary from tagitem where Name='"+parentName +"'";
			Statement stmt = connection.createStatement();
			ResultSet rs;
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if(rs.getString("isPrimary").equals("0"))
					val= false;
				else
					val=true;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in checkprimarycategory "+e.getMessage());
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: checkprimarycategory() completed ]");
		return val;
	}

	public void saveApprovedUserInfo(HashMap hashmap, LhUser userInformation) {
		logger.log(Level.INFO, "[LHAdminHelper :: saveApprovedUserInfo() initiated ]");
		try{
			Connection conn = (Connection) getConnection();
			for(Object obj:hashmap.keySet()){
				int id = (Integer)obj;
				int durationvalue = (Integer)hashmap.get(id);
				String newsCenterName = userInformation.getIndustryNewsCenterName();
				savedurationAndnewsCenterName(id,durationvalue,newsCenterName);

			    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			    Date date = new Date();
			    String str = format.format(date);
			    System.out.println("Today is " + str);
			    
				String query ="update user set isApproved=1,createdBy="+userInformation.getUserId()+" where UserId="+id ;
				Statement stmt=conn.createStatement();
				stmt.executeUpdate(query);

				String query1 = "update usersubscription set ApprovalDate='"+str+"' where UserId="+id+ " and NewsCenterId="+userInformation.getUserSelectedNewsCenterID() ;
				stmt.executeUpdate(query1);
				stmt.close();
				saveUserPermission(id,userInformation.getUserSelectedNewsCenterID());
			}
		}
		catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in saveApprovedUserInfo "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: saveApprovedUserInfo() completed ]");
	}
	
	public void savedurationAndnewsCenterName(int id, int durationvalue,String newsCenterName){
		logger.log(Level.INFO, "[LHAdminHelper :: savedurationAndnewsCenterName() initiated ]");
		String newsCenter = newsCenterName.replace(" ", "");
		int newsCenterId = getnewsCenterId(newsCenter+".NewsCenter.com");
		try{
			Connection conn = (Connection) getConnection();
			String query = "insert into usersubscription (UserId, Duration, DurationLeft,NewsCenterId) values ('"+id+"','"+durationvalue+"','"+durationvalue+"','"+newsCenterId+"')";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		}
		catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in savedurationAndnewsCenterName "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: savedurationAndnewsCenterName() completed ]");
	}
	
	public int getnewsCenterId(String newsCenterName){
		logger.log(Level.INFO, "[LHAdminHelper :: getnewsCenterId() initiated ]");
		int newsId=0;
		try{
			Connection conn = (Connection) getConnection();
			String query = "SELECT NewsCenterId from newscenter where Name ='" +newsCenterName+"'";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				newsId= rs.getInt("NewsCenterId");
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex)	{
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in getnewsCenterId "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: getnewsCenterId() completed ]");
		return newsId;
	}
	
	public void saveUserPermission(int userId, int newsCenterId) {
		logger.log(Level.INFO, "[LHAdminHelper :: saveUserPermission() initiated ]");
		try{
			Connection conn = (Connection) getConnection();
			String query = "insert into user_permission (userId,newsCenterId) values("+userId+","+newsCenterId+")";
			Statement stmt = conn.createStatement() ;
			stmt.executeUpdate(query);
			stmt.close();
		}catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in getnewsCenterId "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: saveUserPermission() completed ]");
	}
	
	public String setUserInfoAdminRegistration(AdminRegistrationInformation adminregInfo){
		logger.log(Level.INFO, "[LHAdminHelper :: setUserInfoAdminRegistration() initiated ]");
		try{
			Connection conn = (Connection) getConnection();
			HttpSession session = request.getSession(false); 
			LhUser userInformation  = (LhUser)session.getAttribute("userInfo");
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
				if(adminregInfo.getIsAdmin() == 2)
					query = "insert into user(FirstName, LastName, Company, title, PhoneNo, email, password, isApproved, IndustryEnumId,isAdmin,CreatedBy,Signature) values('"+adminregInfo.getName()+"', '"+adminregInfo.getLastname()+"', '"+adminregInfo.getCompanynm()+"', '"+adminregInfo.getTitle()+"', '"+adminregInfo.getPhoneno()+"', '"+adminregInfo.getEmail()+"', '"+adminregInfo.getPassword()+"', "+1+", "+industryId+", "+adminregInfo.getIsAdmin()+","+userInformation.getUserId()+",'MarketScape')";
				else
					query = "insert into user(FirstName, LastName, Company, title, PhoneNo, email, password, isApproved, IndustryEnumId,isAdmin,CreatedBy) values('"+adminregInfo.getName()+"', '"+adminregInfo.getLastname()+"', '"+adminregInfo.getCompanynm()+"', '"+adminregInfo.getTitle()+"', '"+adminregInfo.getPhoneno()+"', '"+adminregInfo.getEmail()+"', '"+adminregInfo.getPassword()+"', "+1+", "+industryId+", "+adminregInfo.getIsAdmin()+","+userInformation.getUserId()+")";		
			 
			 stmt.executeUpdate(query);
			 setUserInfoSubscription(false,adminregInfo);
			}
			rs.close();
			stmt.close();
			logger.log(Level.INFO, "[LHAdminHelper :: setUserInfoAdminRegistration() completed ]");
			return "true";
		}
		catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in saveUserPermission "+ex.getMessage());
			ex.printStackTrace();
			return "false";
		}
	}
	
	public void setUserInfoSubscription(Boolean isExisting, AdminRegistrationInformation adminregInfo){
		logger.log(Level.INFO, "[LHAdminHelper :: setUserInfoSubscription() initiated ]");
		try{
			Connection conn = (Connection) getConnection();
			String col="UserId";
			String tableName = "user";
			HttpSession session = request.getSession(false); 
			LhUser userInformation  = (LhUser)session.getAttribute("userInfo");
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
		    	query = "update usersubscription set Duration = "+adminregInfo.getDuration()+", ApprovalDate = '"+str+"' , ExtensionDate = NULL, isExtended = NULL, ExtendedDuration = NULL, DurationLeft = "+adminregInfo.getDuration()+", NewsCenterId = "+adminregInfo.getIndustryid()+" where userid = "+adminregInfo.getUserid()+" and NewsCenterId = "+adminregInfo.getIndustryid();
		    	stmt.executeUpdate(query);

		    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		    	query = "insert into useraccounthistory(userID,message,operation,operationDate,operationPerformBy) values("+adminregInfo.getUserid()+",'Trial user converted into subscribed user','subscribed','"+df.format(new Date())+"',"+userInformation.getUserId()+")";
		    	stmt.executeUpdate(query);
		    }
			stmt.close();
			saveUserPermission(userId, newsCenterId);
			logger.log(Level.INFO, "[LHAdminHelper :: setUserInfoSubscription() completed ]");
		}
		catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in setUserInfoSubscription "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public ArrayList getUserApprovalItems(ArrayList list){
		logger.log(Level.INFO, "[LHAdminHelper :: getUserApprovalItems() initiated ]");
		Iterator iter = list.iterator();
		ArrayList<UserAdminInformation> array = new ArrayList<UserAdminInformation>();
		while(iter.hasNext()){
			useradmininfo = (UserAdminInformation)iter.next();
			try{
				Connection conn = (Connection) getConnection();
				String query = "select U.UserId,U.FirstName,U.email,U.password,US.Duration,N.Name,N.NewsCenterId from user U, usersubscription US, newscenter N where US.NewsCenterId = N.NewsCenterId and U.UserId = US.UserId and U.UserId = '"+useradmininfo.getUserId()+"'";
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()){
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
				logger.log(Level.INFO, "[LHAdminHelper :: Exception in getUserApprovalItems "+ex.getMessage());
				ex.printStackTrace();
			}
		}
		logger.log(Level.INFO, "[LHAdminHelper :: getUserApprovalItems() completed ]");
		return array;
	}
	
	public ArrayList getAllFieldOfNewsItems(int industryId){
		logger.log(Level.INFO, "[LHAdminHelper :: getAllFieldOfNewsItems() initiated ]");
		ArrayList newsitemIds = getNewsItemIdsForThisIndustryId(industryId);
		Iterator iterate = newsitemIds.iterator();
		ArrayList list = new ArrayList();
		LHNewsItems news;
		try{
			Connection conn = (Connection) getConnection();
			while(iterate.hasNext()){
				int newsId = (Integer)iterate.next();
				String query = "Select NewsItemId,Title,Abstract,Content,URL,ItemDate from newsitem where NewsItemId ="+newsId+" and isReport=0";
				Statement stmt=conn.createStatement();
				ResultSet rs =stmt.executeQuery(query);
				while(rs.next()){
					news = new LHNewsItems();
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
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in getSearchedUserInfoModify "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[LHAdminHelper :: getAllFieldOfNewsItems() completed ]");
		return list;
	}
	
	public ArrayList getNewsItemIdsForThisIndustryId(int industryId){
		logger.log(Level.INFO, "[LHAdminHelper :: getNewsItemIdsForThisIndustryId() initiated ]");
		ArrayList list = new ArrayList();
		try{
			Connection conn = (Connection) getConnection();
			String query = "SELECT distinct NewsItemId from newstagitem where IndustryEnumId="+industryId;
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				list.add(rs.getInt("NewsItemId"));
			}
			rs.close();
			stmt.close();
			logger.log(Level.INFO, "[LHAdminHelper :: getNewsItemIdsForThisIndustryId() completed ]");
			return list;
		}catch(Exception ex){
			logger.log(Level.INFO, "[LHAdminHelper :: Exception in getAllFieldOfNewsItems "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
}
