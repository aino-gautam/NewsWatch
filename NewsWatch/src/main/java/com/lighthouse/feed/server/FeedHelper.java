package com.lighthouse.feed.server;

/**
 * author sachin@ensarm.com
 */

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.w3c.dom.Element;

import com.lighthouse.feed.client.domain.Feed;
import com.lighthouse.feed.client.domain.FeedNewsItem;
import com.mysql.jdbc.Blob;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.db.DBHelper;

public class FeedHelper extends DBHelper {
	
 
	private File file;
	private String folderUrl;
	private File folderImage;
	
	public  String getFolderUrl() {
		return folderUrl;
	}
	public void setFolderUrl(String folderurl) {
		this.folderUrl = folderurl;
	}
	public File getFolderimage() {
		return folderImage;
	}
	public void setFolderimage(File folderimage) {
		this.folderImage = folderimage;
	}

	public Logger logger = Logger.getLogger(FeedHelper.class.getName());
	
	public  FeedHelper() {
		
	}
	
	public FeedHelper(String tomcatpath) {
		setFolderUrl(tomcatpath + "/imagefolder");
		setFolderimage(new File(getFolderUrl()));
		folderImage.mkdir();
	}
	
	public void saveNewsFeed(ArrayList<FeedNewsItem> newsItemList,int ncid, int userId,Feed feed){
		logger.log(Level.INFO,"[----- In FeedHelper saveNewsFeed() initiated for FEEDNAME ::: "+feed.getFeedName()+" -----]");
		int cnt=0;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		XMLConfigFileParser configFileParser=new XMLConfigFileParser();
		String feedQuery=null;
		try{
			Element catalystElmt=configFileParser.getCatalystElement(ncid);
			String pTag=configFileParser.getTagValueFromXml("ptag", catalystElmt);
			for(FeedNewsItem newsItem:newsItemList){
				BufferedImage bi=null;
				Boolean	exist=false;
				Long newsItemId=null;
				Long tagItemId=null;
				String imgUrl=newsItem.getImageUrl();
				if(imgUrl!=null){
					if(imgUrl.length()>0){
						URL iurl = new URL(imgUrl);
						bi = ImageIO.read(iurl);
					}
				}
				String title = newsItem.getNewsTitle();
				StringBuffer titleBuffer = new StringBuffer (title);
				int indexForTitle = 0;
			    int t = 0;
				while((indexForTitle = title.indexOf("'", indexForTitle)) != -1) {
				    indexForTitle = title.indexOf("'", indexForTitle);
				    indexForTitle = indexForTitle + t;
				    titleBuffer.insert(indexForTitle,"\\");
				    indexForTitle++;
				    t++;
				}
				title = titleBuffer.toString();
						
				String newsAbstract = newsItem.getAbstractNews();
				
				String abstractArray [] = newsAbstract.split("<br/>");
				String abstrct = null;
				if(abstractArray.length>1){
				for(int i=0; i<1; i++){
					abstrct = abstractArray[i] + "<br/> " + abstractArray[i+1].trim();
					newsAbstract = abstrct;
					
				}
				}
				int indexForAbstract = 0;
				StringBuffer abstractBuffer = new StringBuffer(newsAbstract);
				int a = 0;
				while((indexForAbstract = newsAbstract.indexOf("'", indexForAbstract)) != -1) {
				    indexForAbstract = newsAbstract.indexOf("'", indexForAbstract);
				    indexForAbstract = indexForAbstract + a;
				    abstractBuffer.insert(indexForAbstract,"\\");
				    indexForAbstract++;
				    a++;
				}
				newsAbstract = abstractBuffer.toString();
				if(newsItem.getFeedId()!=0)
					
				   feedQuery="select n.feedId from newsitem n where n.feedId="+newsItem.getFeedId()+" and n.feedSourceUrl='"+feed.getFeedUrl()+"'"+" OR  n.feedSourceUrl='"+feed.getFeedUrl()+"'"+" and n.Title='"+title+"' " +
				   "OR  n.Abstract='"+newsAbstract+"' n.Title='"+title+"'";	
				else
					feedQuery="select n.NewsItemId from newsitem n where n.url='"+newsItem.getUrl()+"' and n.Title='"+title+"' OR  n.feedSourceUrl='"+feed.getFeedUrl()+"'"+" and n.Title='"+title+"' " +
							"OR n.Title='"+title+"' and n.Abstract='"+newsAbstract+"'";				
				
				Connection connection=getConnection();
				connection.setAutoCommit(false);
				Statement stmt1=connection.createStatement();
				   ResultSet rs=stmt1.executeQuery(feedQuery);
				   while(rs.next()){
					   exist=true;
				   }
				   stmt1.close();
				  
				   rs.close();
				connection.commit();
				   
				   if(!exist){
					   String sql = "insert into newsitem(NewsItemId,Content,Title,Abstract,URL,ItemDate,UploadedAt,NewsImages,Source,reportContent,reportMimeType,reportLink,reportLifeSpan,isReport,isLocked,feedId,feedContent,priorityLevel,feedSourceUrl,feedSourceName,author) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					 
					   Connection tempCon=getConnection();
					   tempCon.setAutoCommit(false);
					   PreparedStatement stmt = tempCon.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
					   stmt.setNull(1, Types.BIGINT);
					   stmt.setString(2,newsItem.getNewsContent());
					   stmt.setString(3,newsItem.getNewsTitle());
					   stmt.setString(4,newsItem.getAbstractNews());
					   stmt.setString(5,newsItem.getUrl());
					   String dateString=newsItem.getNewsDate();
					   java.sql.Date itemDate=new Date(formatter.parse(dateString).getTime());
					   stmt.setDate(6,itemDate);
					   java.util.Date date=new java.util.Date();
					   java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
					   stmt.setTimestamp(7, timeStampDate);
					   ByteArrayOutputStream baos = new ByteArrayOutputStream();
					   if(bi!=null){
						   ImageIO.write( bi, "jpg", baos );
						   baos.flush();
						   byte[] imageInByte = baos.toByteArray();
						   baos.close();
						   stmt.setBinaryStream(8, new ByteArrayInputStream(imageInByte),(int)imageInByte.length);
					   }else{
						   stmt.setNull(8, Types.BLOB);
					   }
					   stmt.setString(9,newsItem.getNewsSource());
					   stmt.setNull(10, Types.BLOB);
					   stmt.setNull(11, Types.VARCHAR);
					   stmt.setString(12,newsItem.getReportUrl());
					   stmt.setNull(13, Types.DATE);
					  // stmt.setInt(14,isReport);
					   stmt.setInt(14,0);
					   stmt.setBoolean(15, false);
					   stmt.setLong(16,newsItem.getFeedId());
					   stmt.setString(17,newsItem.getFeedContent());
					   stmt.setInt(18,5); // default priority set i.e : 5(high) ..
					   stmt.setString(19,feed.getFeedUrl()); 
					   stmt.setString(20,feed.getFeedName());
					   //stmt.setNull(21, Types.VARCHAR); // setting author null
					   stmt.setString(21,newsItem.getAuthor());
					   
					   stmt.execute();
					   cnt++;
					   ResultSet res=stmt.getGeneratedKeys();
					   while(res.next()){
						  newsItemId=res.getLong(1);
					   }
					   res.next();
					   stmt.close();
					   tempCon.commit();
					  
					    ArrayList<TagItem> associatedtagList=newsItem.getAssociatedTagList();
					   
						 ArrayList<TagItem> feedPTagList=new ArrayList<TagItem>();
						 TagItem feedPTagItem=new TagItem();
						 feedPTagItem.setTagName(feed.getFeedPTag());
						 feedPTagList.add(feedPTagItem);
					   
					   if (associatedtagList != null) {
							if(associatedtagList.size() > 0){
							
								if(!isPrimaryTagPresent(associatedtagList,ncid)){
									
									if(isPrimaryTagPresent(feedPTagList,ncid))
										tagItemId=getTagItemId(feed.getFeedPTag(), ncid);
									else								
										tagItemId=getTagItemId(pTag, ncid);
									
									
									if (newsItemId != null && tagItemId != null) {
										associateNewsTagItem(newsItemId, tagItemId,ncid);
									}
								}  
								
								for (TagItem tag : associatedtagList) {
									tagItemId=getTagItemId(tag.getTagName(), ncid);
									
									if (newsItemId != null && tagItemId != null) {
										associateNewsTagItem(newsItemId, tagItemId,ncid);
									}
								}
							}else{
								if(isPrimaryTagPresent(feedPTagList,ncid))
									tagItemId=getTagItemId(feed.getFeedPTag(), ncid);
								else								
									tagItemId=getTagItemId(pTag, ncid);
								if (newsItemId != null && tagItemId != null) {
									associateNewsTagItem(newsItemId, tagItemId,ncid);
								}
							}
						}else{
							if(isPrimaryTagPresent(feedPTagList,ncid))
								tagItemId=getTagItemId(feed.getFeedPTag(), ncid);
							else								
								tagItemId=getTagItemId(pTag, ncid);
							if (newsItemId != null && tagItemId != null) {
								associateNewsTagItem(newsItemId, tagItemId,ncid);
							}
						}
			}
		}
			
			saveFeedStatistics(userId, cnt, feed.getFeedUrl(), ncid);
			
			logger.log(Level.INFO,"[----- In FeedHelper saveNewsFeed() completed for FEEDNAME ::: "+feed.getFeedName()+" -----]");
			logger.log(Level.INFO,feed.getFeedUrl()+" ==> NO. OF NEWSITEMS SYNCED :  "+ cnt);
			
		}catch (Exception e) {
			logger.log(Level.INFO, "[--FeedHelper--] Exception in method saveNewsFeed() for FEEDNAME ::: "+feed.getFeedName()+" \n", e);
		}
	}
   
	public Long getTagItemId(String tag,Integer ncid){
		Long tagItemId=null;
		try{
			//connection=getConnection();
			String query = "SELECT tagitemId FROM tagitem where name='"+ tag + "' and IndustryId="+ncid;
			Connection connection=getConnection();
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				tagItemId = resultSet.getLong(1);
			}
			resultSet.close();
			statement.close();
			connection.commit();
		}catch (Exception e) {
			logger.log(Level.INFO,"Exception in getTagItemId.... \n",e);
		}
		return tagItemId;
	}
	

	public void associateNewsTagItem(Long newsId, Long tagItemId,Integer ncid) {
		logger.log(Level.INFO,"[----- In associateNewsTagItem from FeedHelper -----]");
		boolean exist=false;
		try{
			String existQuery="select NewsTagItemId from newstagitem where NewsItemId="+newsId+" and TagItemId="+tagItemId+" and IndustryEnumId="+ncid;
			Connection connection=getConnection();
			 Statement stmt=connection.createStatement();
			 connection.setAutoCommit(false);
			   ResultSet rs=stmt.executeQuery(existQuery);
			   while(rs.next()){
				   exist=true;
			   }
			   stmt.close();
			   rs.close();
			   connection.commit();
			   if(!exist){
					String query="insert into newstagitem values(null,"+newsId+","+tagItemId+","+ncid+")";
					Connection tempCon=getConnection();
					tempCon.setAutoCommit(false);
				    Statement statement=tempCon.createStatement();
				    statement.executeUpdate(query);
				    statement.close();
				    tempCon.commit();
			   }
		}
		catch (Exception e) {
			logger.log(Level.INFO,"Exception in method associateNewsTagItem().... \n",e);
		}
	}
	
	public NewsItemList getNewsItemforFeedSource(String feedSourceUrl,String feedSourceName,Integer ncid) throws SQLException{
	
		NewsItemList newsItemList=new NewsItemList();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		NewsItems newsitem=null;
		try{
			Timestamp lastDeliveryTime= getLastNewsLetterDelivery(ncid) ; //getEditorialLastDeliveryTime(ncid);
			String query="SELECT distinct n.NewsItemId,n.UploadedAt,n.author,n.Title,n.Content,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source,n.isLocked,n.priorityLevel from newsitem n where n.feedSourceUrl='"+feedSourceUrl+"' and feedSourceName='"+feedSourceName+"' and UploadedAt > '"+lastDeliveryTime+"' order by ItemDate desc";
			Connection connection=getConnection();
			connection.setAutoCommit(false);
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) { 
				newsitem = new NewsItems();
				Integer newsId=rs.getInt("NewsItemId"); 
				newsitem.setNewsId(newsId);
				int newsPriority=rs.getInt("priorityLevel");
	 			String newsTagsQuery = "SELECT tagitemId,name,isPrimary FROM tagitem where tagitemid in (SELECT TagItemId from newstagitem where NewsItemId="
						+ newsId + ")";
	 			Connection connection1=getConnection();
				connection.setAutoCommit(false);
				Statement stmt2 = connection1.createStatement();
				ResultSet rs1 = stmt2.executeQuery(newsTagsQuery);
				
				//String etag = getETag(ncid);
				while (rs1.next()) {
					TagItem tagitem = new TagItem();
					tagitem.setTagName(rs1.getString(2));
					tagitem.setTagId(rs1.getInt(1));
					tagitem.setPrimary(rs1.getBoolean(3));
					//if(!(tagitem.getTagName().equalsIgnoreCase(etag)))
						newsitem.addTagforNews(tagitem);
				}
	
				stmt2.close();
				rs1.close();
				connection1.commit();
				String query2 = "SELECT level FROM news_priority where id = "+newsPriority;
				Connection connection2=getConnection();
				connection2.setAutoCommit(false);
				Statement stmt3 = connection2.createStatement();
				ResultSet rs2 = stmt3.executeQuery(query2);
				while(rs2.next()){
					newsitem.setNewsPriority(rs2.getString("level"));
				}
				stmt3.close();
				rs2.close();
				connection2.commit();
				String newsTitle = new String(rs.getString("Title").getBytes(
						"utf-8"), "utf-8");
				newsitem.setNewsTitle(newsTitle);
				String newsAbstract = new String(rs.getString("Abstract")
						.getBytes("utf-8"), "utf-8");
				newsitem.setAbstractNews(newsAbstract);
				newsitem.setNewsContent(rs.getString("Content"));
				String formattedDate = formatter.format(rs.getTimestamp("UploadedAt"));
				newsitem.setNewsUploadedAt(formattedDate);
				newsitem.setAuthor(rs.getString("author"));
				newsitem.setUrl(rs.getString("URL"));
				newsitem.setNewsDate(rs.getDate("ItemDate").toString());
				newsitem.setNewsSource(rs.getString("Source"));
				newsitem.setIsLocked(rs.getInt("isLocked"));
				if(isMarkedAsTopNews(newsitem.getNewsId(), ncid))
					newsitem.setMarkedAsTop(true);
				else
					newsitem.setMarkedAsTop(false);
				
				file = new File(folderImage+"//"+newsId+".jpg");
				logger.log(Level.INFO,"Image path "+file.getPath());
				String imagepath = "imagefolder/"+newsId+".jpg";
				if(rs.getBlob("NewsImages") != null){
				Blob blobim = (Blob) rs.getBlob("NewsImages");
				byte[] bdata = blobim.getBytes(1, (int)blobim.length());
				String imgUrl = new String(bdata);
				InputStream x = blobim.getBinaryStream();
				int size = x.available();
					if (size != 0) {
						newsitem.setImageUrl(imagepath);
						/*URL iurl = new URL(imgUrl);//image url
						BufferedImage bi = ImageIO.read(iurl);
						ImageIO.write(bi,"jpg",file);*/
						
						OutputStream out = new FileOutputStream(
								file);
						byte b[] = new byte[size];
						x.read(b);
						out.write(b);
						
					}
				}
				newsItemList.add(newsitem);
			}
			rs.close();
			stmt.close();
			connection.commit();
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed to get news items for feed source :"+feedSourceUrl,e);
			return null;
		}
		
		return newsItemList;
	}
	
	
	/*public Timestamp getEditorialLastDeliveryTime(Integer ncid){
		Timestamp lastDeliveryTime=null;
		try{
			String query = "SELECT lastDeliveryTime FROM editorialNewsletterDelivery where news_center="+ncid;
			connection=getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				lastDeliveryTime = resultSet.getTimestamp("lastDeliveryTime");
			}
			resultSet.close();
			statement.close();
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed in method getEditorialLastDeliveryTime()",e);
		}
		return lastDeliveryTime;
	}*/
	
	public Timestamp getLastNewsLetterDelivery(int ncid) throws SQLException{
		logger.log(Level.INFO," [ FeedHelper :::: getLastNewsLetterDelivery() initiated for ncid::: "+ncid+"  ] ");
		Timestamp delivery=null;
		Connection connection=null;
		try{
			connection=getConnection(); 
			connection.setAutoCommit(false);
			String query="SELECT delivery FROM newsletterdelivery where newsCenterId="+ncid;
			Statement statement=connection.createStatement();
			ResultSet resultSet=statement.executeQuery(query);
			while(resultSet.next()){
				delivery=resultSet.getTimestamp("delivery");
			}
			resultSet.close();
			statement.close();
			connection.commit();
		}catch (Exception e) {
			logger.log(Level.INFO," [ FeedHelper :::: getLastNewsLetterDelivery() EXCEPTION!!!! for ncid::: "+ncid+"  ] ");
			e.printStackTrace();
		}
		logger.log(Level.INFO," [ FeedHelper :::: getLastNewsLetterDelivery() completed for ncid::: "+ncid+"  ] ");
		return delivery;
	}
	
	public boolean saveFeedStatistics(long userId,int cnt,String url,int ncid){
		logger.log(Level.INFO,"[----- saveFeedStatistics() initiated from FeedHelper for url "+url+" -----]");
		try{
			String query="insert into feedstatistics values(?,?,?,?,?,?)";
			Connection connection=getConnection();
			connection.setAutoCommit(false);
			 PreparedStatement stmt = connection.prepareStatement(query);
			 stmt.setNull(1, Types.BIGINT);
			 stmt.setString(2, url);
			 java.util.Date date=new java.util.Date();
			 java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
			 stmt.setTimestamp(3, timeStampDate);
			 stmt.setInt(4,cnt);
			 stmt.setLong(5,userId);
			 stmt.setInt(6,ncid);
		     stmt.execute();
		     stmt.close();
		     connection.commit();
		     logger.log(Level.INFO,"[----- saveFeedStatistics() completed from FeedHelper for url "+url+" -----]");
		     return true;
		}catch (Exception e) {
			logger.log(Level.INFO,"Failed to save feed statistics for url "+url+" :: ",e);
		}
		logger.log(Level.INFO,"[----- saveFeedStatistics() completed from FeedHelper returning false for url "+url+" -----]");
		return false;
		
	}

	public NewsItemList getIndustryFeedItems(Integer ncid) {
		NewsItemList newsItemList=new NewsItemList();
		NewsItems newsitem=null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String feedUrlList=null;
		try{
			FeedManager feedManager=new FeedManager();
			ArrayList<Feed> siloFeedList = feedManager.getFeedUrlListForCatalyst(ncid);
			Timestamp lastDeliveryTime= getLastNewsLetterDelivery(ncid); //getEditorialLastDeliveryTime(ncid);
			
			for(Feed feed : siloFeedList){
				if(feedUrlList==null)
					feedUrlList="'"+feed.getFeedUrl()+"'";
				else{
					feedUrlList=feedUrlList+",'"+feed.getFeedUrl()+"'";
				}
			}
				String query="SELECT distinct n.NewsItemId,n.UploadedAt,n.author,n.Title,n.Content,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source,n.isLocked,n.priorityLevel from newsitem n where n.feedSourceUrl in ("+feedUrlList+") and UploadedAt > '"+lastDeliveryTime+"' order by ItemDate desc";
				Connection localConn = getConnection() ;
				localConn.setAutoCommit(false);
				Statement stmt = localConn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) { 
					newsitem = new NewsItems();
					Integer newsId=rs.getInt("NewsItemId"); 
					newsitem.setNewsId(newsId);
					int newsPriority=rs.getInt("priorityLevel");
					String newsTagsQuery = "SELECT tagitemId,name,isPrimary FROM tagitem where tagitemid in (SELECT TagItemId from newstagitem where NewsItemId="
							+ newsId + ")";
					Connection localConn1 = getConnection() ;
					localConn1.setAutoCommit(false);
					Statement stmt2 = localConn1.createStatement();
					ResultSet rs1 = stmt2.executeQuery(newsTagsQuery);
				//	String etag = getETag(ncid);
					while (rs1.next()) {
						TagItem tagitem = new TagItem();
						tagitem.setTagName(rs1.getString(2));
						tagitem.setTagId(rs1.getInt(1));
						tagitem.setPrimary(rs1.getBoolean(3));
					//	if(!(tagitem.getTagName().equalsIgnoreCase(etag)))
							newsitem.addTagforNews(tagitem);
					}
		
					stmt2.close();
					rs1.close();
					localConn1.commit();
					String query2 = "SELECT level FROM news_priority where id = "+newsPriority;
					Connection localConn2 = getConnection() ;
					localConn2.setAutoCommit(false);
					Statement stmt3 = localConn2.createStatement();
					ResultSet rs2 = stmt3.executeQuery(query2);
					while(rs2.next()){
						newsitem.setNewsPriority(rs2.getString("level"));
					}
					stmt3.close();
					rs2.close();
					localConn2.commit();
					String newsTitle = new String(rs.getString("Title").getBytes(
							"utf-8"), "utf-8");
					newsitem.setNewsTitle(newsTitle);
					String newsAbstract = new String(rs.getString("Abstract")
							.getBytes("utf-8"), "utf-8");
					newsitem.setAbstractNews(newsAbstract);
					newsitem.setNewsContent(rs.getString("Content"));
					newsitem.setUrl(rs.getString("URL"));
					newsitem.setNewsDate(rs.getDate("ItemDate").toString());
					String formattedDate = formatter.format(rs.getTimestamp("UploadedAt"));
					newsitem.setAuthor(rs.getString("author"));
					newsitem.setNewsUploadedAt(formattedDate);
					newsitem.setNewsSource(rs.getString("Source"));
					newsitem.setIsLocked(rs.getInt("isLocked"));
					if(isMarkedAsTopNews(newsitem.getNewsId(), ncid))
						newsitem.setMarkedAsTop(true);
					else
						newsitem.setMarkedAsTop(false);
					
					file = new File(folderImage+"//"+newsId+".jpg");
					logger.log(Level.INFO,"Image path "+file.getPath());
					String imagepath = "imagefolder/"+newsId+".jpg";
					if(rs.getBlob("NewsImages") != null){
					Blob blobim = (Blob) rs.getBlob("NewsImages");
					byte[] bdata = blobim.getBytes(1, (int)blobim.length());
					String imgUrl = new String(bdata);
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
						if (size != 0) {
							newsitem.setImageUrl(imagepath);
							/*URL iurl = new URL(imgUrl);//image url
							BufferedImage bi = ImageIO.read(iurl);
							ImageIO.write(bi,"jpg",file);*/
							
							OutputStream out = new FileOutputStream(
									file);
							byte b[] = new byte[size];
							x.read(b);
							out.write(b);
							
						}
					}
					
					newsItemList.add(newsitem);
				}
				rs.close();
				stmt.close();
				localConn.commit();
		//	}
		}catch (Exception e) {
			logger.log(Level.INFO,"Failed in method getIndustryFeedItems()  ...",e);
		}
		return newsItemList;
	}
	
	public boolean isPrimaryTagPresent(ArrayList<TagItem> tagItemList,Integer ncid){
		ResultSet resultSet = null;
		Statement statement =null;
		try{
			Connection connection=getConnection();
			connection.setAutoCommit(false);
			statement=connection.createStatement();
			
			for(TagItem tagItem:tagItemList){
				String tag=tagItem.getTagName();
				String query = "SELECT tagitemId FROM tagitem where name='"+ tag + "' and IndustryId="+ncid+" and isPrimary=1";
				resultSet=statement.executeQuery(query);
				if(resultSet.next()){
					resultSet.close();
					statement.close();
					connection.commit();
					return true;
				}
			}
			resultSet.close();
			statement.close();
			connection.commit();
		}catch (Exception e) {
			logger.log(Level.INFO,"Failed in method isPrimaryTagPresent()  ...",e);
		}
		return false;
	}

	public TagItem markAsTopFeed(Long newsId, Integer ncid, boolean markStatus) {
		try{
			String eTag=getETag(ncid);
			Long tagItemId=getTagItemId(eTag, ncid);
			TagItem tagitem = new TagItem();
			tagitem.setTagName(eTag);
			tagitem.setTagId(Integer.parseInt(tagItemId.toString()));
			tagitem.setPrimary(false);
			if(tagItemId!=null){
				if(markStatus)
					associateNewsTagItem(newsId, tagItemId,ncid);
				else{
					boolean result=removeNewsTagItemAssociation(newsId, tagItemId,ncid);
					if(result){
						tagitem.setSelected(false);
						return tagitem;
					}
				}
			}
			tagitem.setSelected(true);
			return tagitem;
			
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed to mark as top feed news..",e);
		}
		return null;
	}

	public boolean removeNewsTagItemAssociation(Long newsId, Long tagItemId,Integer ncid) {
		try{
			String query="delete from newstagitem where NewsItemId="+newsId+" and TagItemId="+tagItemId+" and IndustryEnumId="+ncid+"";
			Connection connection=getConnection();
			connection.setAutoCommit(false);
		    Statement statement=connection.createStatement();
		    int n=statement.executeUpdate(query);
			if(n>0){
				statement.close();
				connection.commit();
				return true;
			}
		    statement.close();
		    connection.commit();
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed in method removeNewsTagItemAssociation()..",e);
		}
		return false;
	}

	public NewsItemList getIndustryTopFeedNews(Integer ncid) {
		NewsItemList newsItemList=new NewsItemList();
		NewsItems newsitem=null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			FeedManager feedManager=new FeedManager();
			Element catalystElmt=feedManager.getCatalystElement(ncid);
			String eTag=feedManager.getTagValueFromXml("etag", catalystElmt);
			Long tagItemId=getTagItemId(eTag, ncid);
			if(tagItemId!=null){
				String query="SELECT distinct n.NewsItemId,n.UploadedAt,n.author,n.Title,n.Content,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source,n.isLocked,n.priorityLevel from newsitem n where n.NewsItemId in (SELECT NewsItemId from newstagitem where TagItemId="
							+ tagItemId + ") order by ItemDate desc";
				Connection connection=getConnection();
				connection.setAutoCommit(false);
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) { 
					newsitem = new NewsItems();
					Integer newsId=rs.getInt("NewsItemId"); 
					newsitem.setNewsId(newsId);
		
					String newsTagsQuery = "SELECT tagitemId,name FROM tagitem where tagitemid in (SELECT TagItemId from newstagitem where NewsItemId="
							+ newsId + ")";
					Connection connection1=getConnection();
					connection1.setAutoCommit(false);
					Statement stmt2 = connection1.createStatement();
					ResultSet rs1 = stmt2.executeQuery(newsTagsQuery);
				//	String etag = getETag(ncid);
					while (rs1.next()) {
						TagItem tagitem = new TagItem();
						tagitem.setTagName(rs1.getString(2));
						tagitem.setTagId(rs1.getInt(1));
					//	tagitem.setPrimary(rs1.getBoolean(3));
					//	if(!(tagitem.getTagName().equalsIgnoreCase(etag)))
							newsitem.addTagforNews(tagitem);
					}
		
					stmt2.close();
					rs1.close();
					connection1.commit();
					String newsTitle = new String(rs.getString("Title").getBytes(
							"utf-8"), "utf-8");
					newsitem.setNewsTitle(newsTitle);
					String newsAbstract = new String(rs.getString("Abstract")
							.getBytes("utf-8"), "utf-8");
					newsitem.setAbstractNews(newsAbstract);
					newsitem.setNewsContent(rs.getString("Content"));
					newsitem.setNewsPriority(rs.getString("priorityLevel"));
					newsitem.setUrl(rs.getString("URL"));
					newsitem.setNewsDate(rs.getDate("ItemDate").toString());
					String formattedDate = formatter.format(rs.getTimestamp("UploadedAt"));
					newsitem.setNewsUploadedAt(formattedDate);
					newsitem.setAuthor(rs.getString("author"));
					newsitem.setNewsSource(rs.getString("Source"));
					newsitem.setIsLocked(rs.getInt("isLocked"));
					if(isMarkedAsTopNews(newsitem.getNewsId(), ncid))
						newsitem.setMarkedAsTop(true);
					else
						newsitem.setMarkedAsTop(false);
					
					file = new File(folderImage+"//"+newsId+".jpg");
					logger.log(Level.INFO,"Image path "+file.getPath());
					String imagepath = "imagefolder/"+newsId+".jpg";
					if(rs.getBlob("NewsImages") != null){
					Blob blobim = (Blob) rs.getBlob("NewsImages");
					byte[] bdata = blobim.getBytes(1, (int)blobim.length());
					String imgUrl = new String(bdata);
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
						if (size != 0) {
							newsitem.setImageUrl(imagepath);
						/*	URL iurl = new URL(imgUrl);//image url
							BufferedImage bi = ImageIO.read(iurl);
							ImageIO.write(bi,"jpg",file);*/
							
							OutputStream out = new FileOutputStream(
									file);
							byte b[] = new byte[size];
							x.read(b);
							out.write(b);
							
						}
					}
					
					newsItemList.add(newsitem);
				}
				rs.close();
				stmt.close();
				connection.commit();
			}
			
		}catch (Exception e) {
			logger.log(Level.INFO,"Failed in method getIndustryTopFeedNews()  ...",e);
		}
		return newsItemList;
	}
	
	public boolean deleteNewsFeed(Long newsId){
		try{
			String query = "delete from newsitem where NewsItemId="+newsId;
			Connection connection=getConnection();
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			int n=statement.executeUpdate(query);
			if(n>0){
				statement.close();
				connection.commit();
				return true;
			}
			statement.close();
			connection.commit();
		}catch (Exception e) {
			logger.log(Level.INFO,"Failed to delete news feed  ...",e);
		}
		return false;
	}
	
	public boolean isMarkedAsTopNews(Integer newsId,Integer ncid) {
		try{
			
			String eTag = getETag(ncid);
			Long tagItemId=getTagItemId(eTag, ncid);
			if(tagItemId!=null){
				String query="select NewsTagItemId from newstagitem where NewsItemId="+newsId+" and TagItemId="+tagItemId+" and IndustryEnumId="+ncid+"";
				Connection connection=getConnection();
				connection.setAutoCommit(false);
			    Statement statement=connection.createStatement();
			    ResultSet rs=statement.executeQuery(query);
				if(rs.next()){
					statement.close();
					connection.commit();
					return true;
				}
			    statement.close();
			    connection.commit();
			}
			
			
		}catch (Exception e) {
			logger.log(Level.INFO,"Failed to delete news feed  ...",e);
		}
		return false;
		
	}
	
	private String getETag(int ncid){
		try{
			FeedManager feedManager=new FeedManager();
			Element catalystElmt=feedManager.getCatalystElement(ncid);
			String eTag=feedManager.getTagValueFromXml("etag", catalystElmt);
			return eTag;
		}catch (Exception e) {
			logger.log(Level.INFO,"Failed to delete news feed  ...",e);
			return null;
		}
	}
}
