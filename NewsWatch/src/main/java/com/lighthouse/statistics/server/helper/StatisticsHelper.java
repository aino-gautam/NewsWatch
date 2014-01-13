package com.lighthouse.statistics.server.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.server.helper.GroupHelper;
import com.lighthouse.main.server.helper.LhNewsItemHelper;
import com.lighthouse.newsletter.server.LhNewsletterHelper;
import com.mysql.jdbc.Blob;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.categorydb.CategoryHelper;
import com.newscenter.server.exception.CategoryHelperException;

/**
 * 
 * @author nairutee@ensarm.com & kiran@ensarm.com
 * 
 */
public class StatisticsHelper extends LhNewsItemHelper {

	Logger logger = Logger.getLogger(StatisticsHelper.class.getName());
	private static GroupCategoryMap currentMap;
	protected File file;
	protected File folderimage;

	public StatisticsHelper(String tomcatpath) {
		super(currentMap, tomcatpath);
	}

	public StatisticsHelper(GroupCategoryMap currentMap, String tomcatpath) {
		super(currentMap, tomcatpath);
		this.currentMap = currentMap;

	}

	/**
	 * gets the most read (viewed) news for the given group
	 * 
	 * @param group
	 *            - id of the group
	 * @param newsCenterId
	 * @param userId
	 * @return List<NewsItems>
	 */
	public List<NewsItems> getMostReadInGroupNews(Group group, int userId,int newsCenterId) {
		
		List<NewsItems> newsList = new ArrayList<NewsItems>();
		folderimage = super.getFolderimage();
		folderimage.mkdir();
		ResultSet resultSet = null;
		GroupCategoryMap groupCategoryMap = super.getCurrentMap();
		try {
			logger.log(Level.INFO,
					"[StatisticsHelper ::: fetching newsItems ::: getMostReadInGroupNews()]");

			Connection conn = (Connection) getConnection();
			/*
			 * String queryForMostReadInGroupNews =
			 * "SELECT ni.*, sum(us.NewscatalystItemCount + us.NewsletterItemCount) as viewCount"
			 * + " FROM newsitem ni, useritemaccessstats us" + " " +
			 * "where ni.newsItemId = us.newsItemId and us.newsItemId in" +
			 * "(select distinct n.newsItemId from newsitem n, newstagitem nt where n.newsItemId = nt.newsItemId and nt.tagItemId in"
			 * +
			 * "(select distinct t.tagItemId from tagitem t, usergrouptagselection ut where t.tagItemId = ut.tagItemId and ut.userId = 158 and ut.NewsCenterId = 5 and ut.groupId="
			 * + groupId + "))" +
			 * "group by us.newsItemId order by viewCount desc limit 0,3 ";
			 */
			
			String tagsQuery = "select distinct t.tagItemId from tagitem t, usergrouptagselection ut where t.tagItemId = ut.tagItemId and ut.NewsCenterId = "
					+ newsCenterId+ " and ut.groupId=" + group.getGroupId();
			if(group.getIsMandatory() == 0)
				tagsQuery+= " and ut.userId = "+ userId;
			
			String mostReadQuery = "SELECT ni.*, sum(us.NewscatalystItemCount + us.NewsletterItemCount) as viewCount " +
					"FROM newsitem ni, useritemaccessstats us where ni.newsItemId = us.newsItemId and  ni.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 7 DAY)" +
					" and us.newsItemId in(select distinct n.newsItemId from newsitem n, newstagitem nt where n.newsItemId = nt.newsItemId and" +
					" nt.tagItemId in(" +tagsQuery+
					"))group by us.newsItemId  order by viewCount desc limit 0,3";
			
			Statement stmtForNewsViews = conn.createStatement();
			ResultSet resSetForNewsviews = stmtForNewsViews
					.executeQuery(mostReadQuery);
			while (resSetForNewsviews.next()) {
				NewsItems newsItems = new NewsItems();
				newsItems.setNewsId(resSetForNewsviews.getInt("newsItemId"));
				newsItems.setNewsTitle(resSetForNewsviews.getString("Title"));
				newsItems.setUrl(resSetForNewsviews.getString("URL"));
				newsItems.setIsLocked(resSetForNewsviews.getInt("isLocked"));
				newsItems.setNewsDate(resSetForNewsviews.getString("itemDate"));
				newsItems.setNewsTitle(resSetForNewsviews.getString("Title"));
				newsItems.setAbstractNews(resSetForNewsviews.getString("Abstract"));
				newsItems.setUrl(resSetForNewsviews.getString("URL"));
				
				int newsid=newsItems.getNewsId();
				
				file = new File(folderimage + "//" + newsid + ".jpg");
				String imageurl = "imagefolder/"+newsid+".jpg";
				if (resSetForNewsviews.getBlob("NewsImages") != null) {
					Blob blobim = (Blob) resSetForNewsviews.getBlob("NewsImages");
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
					if (size != 0) {
						newsItems.setImageUrl(imageurl);
						OutputStream out = new FileOutputStream(
								file);
						byte b[] = new byte[size];
						x.read(b);
						out.write(b);
					}
				}
				
				String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="+ newsid;
				Statement stmt = conn.createStatement();
				stmt = getConnection().createStatement();
				ResultSet rs1 = stmt.executeQuery(newsTagsQuery);
	
				// add tags to a newsitem
				while (rs1.next()) {
					for (Object obj : groupCategoryMap.keySet()) {
						// groupCategoryMap has categoryItems, if in its
						// tags there is a tag selected by user ,then
						// add it in associatedtaglist
						HashMap map = ((CategoryItem) groupCategoryMap
								.get(obj)).getItemMap();
						if (map.containsKey(rs1.getInt("TagItemId"))) {
							TagItem tagitem = (TagItem) map.get(rs1
									.getInt("TagItemId"));
							newsItems.addTagforNews(tagitem);
						}
					}
				}
				newsList.add(newsItems);
				rs1.close();
				stmt.close();
			}
			resSetForNewsviews.close();
			stmtForNewsViews.close();
			return newsList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.INFO,"[StatisticsHelper ::: EXCEPTION!!! ::: getMostReadInGroupNews()]");
		}
		return null;
	}

	/**
	 * gets the most discussed (commented) news for the given group
	 * 
	 * @param groupId
	 *            - id of the group
	 * @param newsCenterId
	 * @param userId
	 * @return List<NewsItems>
	 */
	public List<NewsItems> getMostDiscussedInGroupNews(Group group, int userId,int newsCenterId) {
		List<NewsItems> newsList = new ArrayList<NewsItems>();
		folderimage = super.getFolderimage();
		folderimage.mkdir();
		ResultSet resultSet = null;
		GroupCategoryMap groupCategoryMap = super.getCurrentMap();
		try {
			logger.log(Level.INFO,"[StatisticsHelper ::: getMostDiscussedInGroupNews() initiated for GroupID :: "+group.getGroupId()+"]");
			Connection conn = (Connection) getConnection();

			String tagsQuery = "select distinct t.tagItemId"
					+ " from tagitem t, usergrouptagselection ut where t.tagItemId = ut.tagItemId"
					+ " and ut.NewsCenterId = "
					+ newsCenterId
					+ " and ut.groupId="
					+ group.getGroupId();
			if(group.getIsMandatory() == 0)
				tagsQuery+=" and ut.userId = "+ userId;
			
			String queryForMostDiscussedInGroupNews = "select n.*, count(i.id) from newsitem n, itemcomment i where n.newsItemId = i.newsItemId"
					+ " and i.newsItemId in(select distinct n.newsItemId from newsitem n, newstagitem nt"
					+ " where n.newsItemId = nt.newsItemId and nt.tagItemId in("
					+ tagsQuery
					+ ")) group by i.newsItemId "
					+ "order by count(i.id) desc limit 0,3";
			Statement stmt = conn.createStatement();
			resultSet = stmt.executeQuery(queryForMostDiscussedInGroupNews);
			while (resultSet.next()) {
				NewsItems newsItems = new NewsItems();

				newsItems.setNewsId(resultSet.getInt("newsItemId"));
				newsItems.setAbstractNews(resultSet.getString("Abstract"));
				// newsItems.setCommentsCount(resultSet.getInt("NewscatalystItemCount"));
				newsItems.setImageUrl(resultSet.getString("NewsImages"));
				newsItems.setNewsContent(resultSet.getString("Content"));
				newsItems.setNewsDate(resultSet.getString("ItemDate"));
				newsItems.setNewsTitle(resultSet.getString("Title"));
				newsItems.setUrl(resultSet.getString("URL"));
				newsItems.setCommentsCount(resultSet.getInt("count(i.id)"));
				newsItems.setNewsSource(resultSet.getString("Source"));
				newsItems.setIsLocked(resultSet.getInt("isLocked"));
				int newsid=newsItems.getNewsId();
				file = new File(folderimage + "//" + newsid + ".jpg");
				String imageurl = "imagefolder/"+newsid+".jpg";
				if (resultSet.getBlob("NewsImages") != null) {
					Blob blobim = (Blob) resultSet.getBlob("NewsImages");
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
					if (size != 0) {
						newsItems.setImageUrl(imageurl);
						OutputStream out = new FileOutputStream(
								file);
						byte b[] = new byte[size];
						x.read(b);
						out.write(b);
					}
				}
				
				String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="+ newsid;
				Statement stmt1 = conn.createStatement();
				stmt1 = getConnection().createStatement();
				ResultSet rs1 = stmt1.executeQuery(newsTagsQuery);
	
				// add tags to a newsitem
				while (rs1.next()) {
					for (Object obj : groupCategoryMap.keySet()) {
						// groupCategoryMap has categoryItems, if in its
						// tags there is a tag selected by user ,then
						// add it in associatedtaglist
						HashMap map = ((CategoryItem) groupCategoryMap
								.get(obj)).getItemMap();
						if (map.containsKey(rs1.getInt("TagItemId"))) {
							TagItem tagitem = (TagItem) map.get(rs1
									.getInt("TagItemId"));
							newsItems.addTagforNews(tagitem);
						}
					}
				}
				newsList.add(newsItems);
				rs1.close();
				stmt1.close();
			}
			resultSet.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.INFO,"[StatisticsHelper ::: EXCEPTION!! ::: getMostDiscussedInGroupNews()for GroupID :: "+group.getGroupId()+"]");
		}
		logger.log(Level.INFO,"[StatisticsHelper ::: getMostDiscussedInGroupNews() completed for GroupID :: "+group.getGroupId()+"]");
		return newsList;
	}

	/**
	 * gets the most read (viewed) news from all groups
	 * 
	 * @param newsCenterId
	 * 
	 * @return List<NewsItems>
	 */
	public List<NewsItems> getMostReadInAllGroupsNews(int newsCenterId) {

		List<NewsItems> newsList = new ArrayList<NewsItems>();
		folderimage = super.getFolderimage();
		folderimage.mkdir();
		ResultSet resultSet = null;
		GroupCategoryMap groupCategoryMap = super.getCurrentMap();
		try {
			logger.log(Level.INFO, "[StatisticsHelper ::: initiated ::: getMostReadInAllGroupsNews() for NCID ::"+newsCenterId+" ]");
			
			Connection conn = (Connection) getConnection();
			String queryForMostReadInAllGroupsNews = "SELECT ni.*, sum(us.NewscatalystItemCount + us.NewsletterItemCount) as viewCount"
					+ " "
					+ "FROM newsitem ni, useritemaccessstats us where ni.newsItemId = us.newsItemId and  ni.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 7 DAY) and"
					+ " "
					+ "us.newsItemId in(select distinct n.newsItemId from newsitem n, newstagitem nt"
					+ " "
					+ "where n.newsItemId = nt.newsItemId and nt.industryEnumId = "
					+ newsCenterId
					+ ")"
					+ " "
					+ "group by us.newsItemId order by viewCount desc limit 0,3;";
			Statement stmt = conn.createStatement();
			resultSet = stmt.executeQuery(queryForMostReadInAllGroupsNews);
			while (resultSet.next()) {
				NewsItems newsItems = new NewsItems();

				newsItems.setNewsId(resultSet.getInt("newsItemId"));
				newsItems.setAbstractNews(resultSet.getString("Abstract"));
				// newsItems.setCommentsCount(resultSet.getInt("NewscatalystItemCount"));
				
				newsItems.setNewsContent(resultSet.getString("Content"));
				newsItems.setNewsDate(resultSet.getString("ItemDate"));
				newsItems.setNewsTitle(resultSet.getString("Title"));
				newsItems.setUrl(resultSet.getString("URL"));
				newsItems.setViewsCount(resultSet.getInt("viewCount"));
				newsItems.setNewsSource(resultSet.getString("Source"));
				newsItems.setIsLocked(resultSet.getInt("isLocked"));
				
				int newsid=newsItems.getNewsId();
				file = new File(folderimage + "//" + newsid + ".jpg");
				String imageurl = "imagefolder/"+newsid+".jpg";
				if (resultSet.getBlob("NewsImages") != null) {
					Blob blobim = (Blob) resultSet.getBlob("NewsImages");
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
					if (size != 0) {
						newsItems.setImageUrl(imageurl);
						OutputStream out = new FileOutputStream(
								file);
						byte b[] = new byte[size];
						x.read(b);
						out.write(b);
					}
				}
				
				
				String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
					+ newsid;
				Statement stmt1 = conn.createStatement();
				stmt1 = getConnection().createStatement();
				ResultSet rs1 = stmt1.executeQuery(newsTagsQuery);
	
				// add tags to a newsitem
				while (rs1.next()) {
					for (Object obj : groupCategoryMap.keySet()) {
						// groupCategoryMap has categoryItems, if in its
						// tags there is a tag selected by user ,then
						// add it in associatedtaglist
						HashMap map = ((CategoryItem) groupCategoryMap
								.get(obj)).getItemMap();
						if (map.containsKey(rs1.getInt("TagItemId"))) {
							TagItem tagitem = (TagItem) map.get(rs1
									.getInt("TagItemId"));
							newsItems.addTagforNews(tagitem);
						}
					}
				}
				newsList.add(newsItems);
				rs1.close();
				stmt1.close();
			}
			resultSet.close();
			stmt.close();
			logger.log(Level.INFO, "[StatisticsHelper ::: completed ::: getMostReadInAllGroupsNews() for NCID ::"+newsCenterId+" ]");
			return newsList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.INFO,"[StatisticsHelper ::: EXCEPTION!! ::: getMostReadInAllGroupsNews()for NCID ::"+newsCenterId+" ]");
		}
		return null;
	}

	/**
	 * gets the most discussed (commented) news from all groups
	 * 
	 * @param newsCenterId
	 * 
	 * @return List<NewsItems>
	 */
	public List<NewsItems> getMostDiscussedInAllGroupsNews(int newsCenterId) {

		List<NewsItems> newsList = new ArrayList<NewsItems>();
		folderimage = super.getFolderimage();
		folderimage.mkdir();
		
		ResultSet resultSet = null;
		GroupCategoryMap groupCategoryMap = super.getCurrentMap();
		Connection conn = (Connection) getConnection();
		try {
			logger.log(Level.INFO, "[StatisticsHelper ::: initiated ::: getMostDiscussedInAllGroupsNews() for NCID ::"+newsCenterId+" ]");
			String queryForMostDiscussedInAllGroupsNews = "select n.*, count(i.id) from newsitem n, itemcomment i where n.newsItemId = i.newsItemId "
					+ " and i.newsItemId in(select distinct n.newsItemId from newsitem n, newstagitem nt"
					+ " where n.newsItemId = nt.newsItemId and nt.industryEnumId = "
					+ newsCenterId
					+ ")"
					+ "group by i.newsItemId order by count(i.id) desc limit 0,3 ";
			Statement stmt = conn.createStatement();
			resultSet = stmt.executeQuery(queryForMostDiscussedInAllGroupsNews);
			while (resultSet.next()) {
				NewsItems newsItems = new NewsItems();

				newsItems.setNewsId(resultSet.getInt("newsItemId"));
				newsItems.setAbstractNews(resultSet.getString("Abstract"));
				newsItems.setCommentsCount(resultSet.getInt("count(i.id)"));
				newsItems.setImageUrl(resultSet.getString("NewsImages"));
				newsItems.setNewsContent(resultSet.getString("Content"));
				newsItems.setNewsDate(resultSet.getString("ItemDate"));
				newsItems.setNewsTitle(resultSet.getString("Title"));
				newsItems.setUrl(resultSet.getString("URL"));
				// newsItems.setViewsCount(resultSet.getInt("viewCount"));
				newsItems.setNewsSource(resultSet.getString("Source"));
				newsItems.setIsLocked(resultSet.getInt("isLocked"));
				
				int newsid=newsItems.getNewsId();
				file = new File(folderimage + "//" + newsid + ".jpg");
				String imageurl = "imagefolder/"+newsid+".jpg";
				if (resultSet.getBlob("NewsImages") != null) {
					Blob blobim = (Blob) resultSet.getBlob("NewsImages");
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
					if (size != 0) {
						newsItems.setImageUrl(imageurl);
						OutputStream out = new FileOutputStream(
								file);
						byte b[] = new byte[size];
						x.read(b);
						out.write(b);
					}
				}
				
				
				String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
					+ newsid;
				Statement stmt1 = conn.createStatement();
				stmt1 = getConnection().createStatement();
				ResultSet rs1 = stmt1.executeQuery(newsTagsQuery);
	
				// add tags to a newsitem
				while (rs1.next()) {
					for (Object obj : groupCategoryMap.keySet()) {
						// groupCategoryMap has categoryItems, if in its
						// tags there is a tag selected by user ,then
						// add it in associatedtaglist
						HashMap map = ((CategoryItem) groupCategoryMap
								.get(obj)).getItemMap();
						if (map.containsKey(rs1.getInt("TagItemId"))) {
							TagItem tagitem = (TagItem) map.get(rs1
									.getInt("TagItemId"));
							newsItems.addTagforNews(tagitem);
						}
					}
				}
				newsList.add(newsItems);
				rs1.close();
				stmt1.close();
			}
			resultSet.close();
			stmt.close();
			logger.log(Level.INFO, "[StatisticsHelper ::: completed ::: getMostDiscussedInAllGroupsNews() for NCID ::"+newsCenterId+" ]");
			return newsList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.INFO, "[StatisticsHelper ::: EXCEPTION!!! ::: getMostDiscussedInAllGroupsNews() for NCID ::"+newsCenterId+" ]");
		}
		logger.log(Level.INFO, "[StatisticsHelper ::: completed Returning NULL!! ::: getMostDiscussedInAllGroupsNews() for NCID ::"+newsCenterId+" ]");
		return null;
	}

	/**
	 * 
	 * @param group
	 * @param newsmode
	 * @param userid
	 * @return
	 */
	public List<NewsItems> getRefreshMostReadInGroupNews(Group group, int newsmode, int userid) {
		List<NewsItems> newsList = new ArrayList<NewsItems>();
		folderimage = super.getFolderimage();
		folderimage.mkdir();
		
		GroupCategoryMap groupCategoryMap = getCurrentMap();
		try {
			Connection conn = (Connection) getConnection();
			
			if (newsmode == 0) {
				logger.log(Level.INFO, "[StatisticsHelper ::: initiated ::: getRefreshMostReadInGroupNews() OR Mode for GroupID :: "+group.getGroupId()+"]");
				ArrayList selectedtaglist = groupCategoryMap.getSelectedTags();
				Iterator iter = selectedtaglist.iterator();
				String queryForNews = "select distinct n.newsItemId from newsitem n, newstagitem nt where n.newsItemId = nt.newsItemId and nt.tagItemId in(0";
				while (iter.hasNext()) {
					TagItem tag = (TagItem) iter.next();
					int tagid = tag.getTagId();
					queryForNews += "," + tagid;
	
				}
				queryForNews += ") ";
				/*Statement stmtForNews = conn.createStatement();
				ResultSet resSetForNews = stmtForNews.executeQuery(queryForNews);*/
				
				String queryForViewsCount = "SELECT ni.*, sum(us.NewscatalystItemCount + us.NewsletterItemCount) as viewCount"
						+ " FROM newsitem ni, useritemaccessstats us"
						+ " where ni.newsItemId = us.newsItemId and  ni.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 7 DAY) and us.newsItemId in("
						+ queryForNews 
						+ ")group by us.newsItemId  order by viewCount desc limit 0,3 ";
						
				/*while (resSetForNews.next()) {
					NewsItems newsItems = new NewsItems();
					newsItems.setNewsId(resSetForNews.getInt(1));
					queryForViewsCount += "," + newsItems.getNewsId() + "";
	
				}
				queryForViewsCount += ")group by us.newsItemId  order by viewCount desc limit 0,3 ";
	*/			Statement stmtForNewsViews = conn.createStatement();
				ResultSet resSetForNewsviews = stmtForNewsViews.executeQuery(queryForViewsCount);
				while (resSetForNewsviews.next()) {
					NewsItems newsItems = new NewsItems();
					newsItems.setNewsId(resSetForNewsviews.getInt("newsItemId"));
					newsItems.setNewsTitle(resSetForNewsviews.getString("Title"));
					newsItems.setUrl(resSetForNewsviews.getString("URL"));
					newsItems.setIsLocked(resSetForNewsviews.getInt("isLocked"));
					newsItems.setNewsDate(resSetForNewsviews.getString("itemDate"));
					newsItems.setNewsTitle(resSetForNewsviews.getString("Title"));
					newsItems.setAbstractNews(resSetForNewsviews.getString("Abstract"));
					newsItems.setUrl(resSetForNewsviews.getString("URL"));
				
					int newsid=newsItems.getNewsId();
					file = new File(folderimage + "//" + newsid + ".jpg");
					String imageurl = "imagefolder/"+newsid+".jpg";
					if (resSetForNewsviews.getBlob("NewsImages") != null) {
						Blob blobim = (Blob) resSetForNewsviews.getBlob("NewsImages");
						InputStream x = blobim.getBinaryStream();
						int size = x.available();
						if (size != 0) {
							newsItems.setImageUrl(imageurl);
							OutputStream out = new FileOutputStream(
									file);
							byte b[] = new byte[size];
							x.read(b);
							out.write(b);
						}
					}
					
					String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
						+ newsid;
					Statement stmt = conn.createStatement();
					stmt = getConnection().createStatement();
					ResultSet rs1 = stmt.executeQuery(newsTagsQuery);
		
					// add tags to a newsitem
					while (rs1.next()) {
						for (Object obj : groupCategoryMap.keySet()) {
							// groupCategoryMap has categoryItems, if in its
							// tags there is a tag selected by user ,then
							// add it in associatedtaglist
							HashMap map = ((CategoryItem) groupCategoryMap
									.get(obj)).getItemMap();
							if (map.containsKey(rs1.getInt("TagItemId"))) {
								TagItem tagitem = (TagItem) map.get(rs1
										.getInt("TagItemId"));
								newsItems.addTagforNews(tagitem);
							}
						}
					}
					newsList.add(newsItems);
					rs1.close();
					stmt.close();
				}
				resSetForNewsviews.close();
				stmtForNewsViews.close();
				logger.log(Level.INFO, "[StatisticsHelper ::: completed ::: getRefreshMostReadInGroupNews() OR Mode for GroupID :: "+group.getGroupId()+"]");
				return newsList;
			}else if (newsmode == 1) {
				logger.log(Level.INFO, "[StatisticsHelper ::: initiated ::: getRefreshMostReadInGroupNews() AND Mode for GroupID :: "+group.getGroupId()+"]");
				ArrayList<NewsItems> newsItems = new ArrayList<NewsItems>();
				ArrayList selectedtaglist = groupCategoryMap.getSelectedTagsByCategory(); // the selected tagson the client side are in
																				// selectedtaglist
				if (selectedtaglist.size() != 0) {
					CallableStatement proc = null;
					String csv = "";
					int totalcategories = 0;
					int count = 0;

					Iterator iter = selectedtaglist.iterator();
					while (iter.hasNext()) {
						totalcategories++;
						int i = 0;
						ArrayList list = (ArrayList) iter.next();
						Iterator itt = list.iterator();
						while (itt.hasNext()) {
							TagItem tag = (TagItem) itt.next();
							int tagid = tag.getTagId();
							if (i != list.size() - 1) {
								csv += tagid + ","; // csv will contain the
													// string of all tags
													// selected for AND
													// criteria (3663,613|)
								i++;
							} else
								csv += tagid + "|";
						}
					}
					csv = csv.substring(0, csv.length() - 1); // removed the
																// |
					proc = getConnection().prepareCall(
							"{ call SP_GETNEWS(?, ?, ?, ?, ?,?,?) }");
					String tbname1 = "temp1_" + userid;
					String tbname2 = "temp2_" + userid;
					proc.setString(1, csv);
					proc.setInt(2, 0);
					proc.setInt(3, 15);
					proc.setInt(4, totalcategories);
					proc.registerOutParameter(5, Types.INTEGER);
					proc.setString(6, tbname1);
					proc.setString(7, tbname2);
					boolean isresultset = proc.execute();
					if (isresultset) {
						ResultSet rs = proc.getResultSet();
						while (rs.next()) {
							count++;
							NewsItems newsitem = new NewsItems();
							int newsid = rs.getInt("NewsItemId");
							newsitem.setNewsId(newsid);

							newsItems.add(newsitem);
							
						}
						rs.close();
					}
					proc.close();
					Iterator iterator = newsItems.iterator();
					String queryForViewsCount = "SELECT ni.*, sum(us.NewscatalystItemCount + us.NewsletterItemCount) as viewCount"
							+ " FROM newsitem ni, useritemaccessstats us"
							+ " "
							+ "where ni.newsItemId = us.newsItemId and  ni.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 7 DAY) and us.newsItemId in(0";
					while (iterator.hasNext()) {
						NewsItems newsItem = (NewsItems) iterator.next();

						queryForViewsCount += "," + newsItem.getNewsId() + "";

					}
					queryForViewsCount += ")group by us.newsItemId  order by viewCount desc limit 0,3 ";

					Statement stmtForNewsViews = conn.createStatement();
					ResultSet resSetForNewsviews = stmtForNewsViews
							.executeQuery(queryForViewsCount);
					while (resSetForNewsviews.next()) {
						NewsItems newsItem = new NewsItems();
						newsItem.setNewsId(resSetForNewsviews
								.getInt("newsItemId"));
						newsItem.setNewsTitle(resSetForNewsviews
								.getString("Title"));
						newsItem.setUrl(resSetForNewsviews.getString("URL"));
						newsItem.setIsLocked(resSetForNewsviews
								.getInt("isLocked"));
						newsItem.setNewsDate(resSetForNewsviews
								.getString("itemDate"));
						newsItem.setNewsTitle(resSetForNewsviews
								.getString("Title"));
						newsItem.setAbstractNews(resSetForNewsviews
								.getString("Abstract"));
						newsItem.setUrl(resSetForNewsviews.getString("URL"));
						newsItem.setViewsCount(resSetForNewsviews
								.getInt("viewCount"));

						int newsid = newsItem.getNewsId();
						file = new File(folderimage + "//" + newsid + ".jpg");
						String imageurl = "imagefolder/" + newsid + ".jpg";
						if (resSetForNewsviews.getBlob("NewsImages") != null) {
							Blob blobim = (Blob) resSetForNewsviews
									.getBlob("NewsImages");
							InputStream x = blobim.getBinaryStream();
							int size = x.available();
							if (size != 0) {
								newsItem.setImageUrl(imageurl);
								OutputStream out = new FileOutputStream(file);
								byte b[] = new byte[size];
								x.read(b);
								out.write(b);
							}
						}

						String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
								+ newsid;
						Statement stmt = conn.createStatement();
						stmt = getConnection().createStatement();
						ResultSet rs1 = stmt.executeQuery(newsTagsQuery);

						// add tags to a newsitem
						while (rs1.next()) {
							for (Object obj : groupCategoryMap.keySet()) {
								// groupCategoryMap has categoryItems, if in its
								// tags there is a tag selected by user ,then
								// add it in associatedtaglist
								HashMap map = ((CategoryItem) groupCategoryMap
										.get(obj)).getItemMap();
								if (map.containsKey(rs1.getInt("TagItemId"))) {
									TagItem tagitem = (TagItem) map.get(rs1
											.getInt("TagItemId"));
									newsItem.addTagforNews(tagitem);
								}
							}
						}
						newsList.add(newsItem);
					}
				}
				logger.log(Level.INFO, "[StatisticsHelper ::: completed ::: getRefreshMostReadInGroupNews() AND Mode for GroupID :: "+group.getGroupId()+"]");
				return newsList;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.INFO, "[StatisticsHelper ::: EXCEPTION!!! ::: getRefreshMostReadInGroupNews() for GroupID :: "+group.getGroupId()+"]");
		}
		logger.log(Level.INFO, "[StatisticsHelper ::: completed Returning NULL !!! ::: getRefreshMostReadInGroupNews() for GroupID :: "+group.getGroupId()+"]");
		return null;
	}

	/**
	 * 
	 * @param group
	 * @param newsmode
	 * @param userid
	 * @return
	 */
	public List<NewsItems> getRefreshMostDiscussedInGroupNews(Group group, int newsmode, int userid) {
		List<NewsItems> newsList = new ArrayList<NewsItems>();
		folderimage = super.getFolderimage();
		folderimage.mkdir();
		GroupCategoryMap groupCategoryMap = getCurrentMap();
		ResultSet resultSet = null;
		try {
			Connection conn = (Connection) getConnection();
			if (newsmode == 0) {
				logger.log(Level.INFO,"[StatisticsHelper ::: initiated ::: getRefreshMostDiscussedInGroupNews() OR Mode for GroupID :: "+group.getGroupId()+"]");
				ArrayList selectedtaglist = groupCategoryMap.getSelectedTags();
				Iterator iter = selectedtaglist.iterator();
				String queryForMostDiscussedInGroupNews = "select n.*, count(i.id) from newsitem n, itemcomment i where n.newsItemId = i.newsItemId"
						+ " "
						+ "and i.newsItemId in(select distinct n.newsItemId from newsitem n, newstagitem nt "
						+ "where n.newsItemId = nt.newsItemId and nt.tagItemId in(0";
				while (iter.hasNext()) {
					TagItem tag = (TagItem) iter.next();
					int tagid = tag.getTagId();
					queryForMostDiscussedInGroupNews += "," + tagid;
	
				}
	
				queryForMostDiscussedInGroupNews += ")) group by i.newsItemId "
						+ "order by count(i.id) desc limit 0,3";
	
				Statement stmt = conn.createStatement();
				resultSet = stmt.executeQuery(queryForMostDiscussedInGroupNews);
				while (resultSet.next()) {
					NewsItems newsItems = new NewsItems();
					newsItems.setNewsId(resultSet.getInt("newsItemId"));
					newsItems.setAbstractNews(resultSet.getString("Abstract"));
					// newsItems.setCommentsCount(resultSet.getInt("NewscatalystItemCount"));
					newsItems.setImageUrl(resultSet.getString("NewsImages"));
					newsItems.setNewsContent(resultSet.getString("Content"));
					newsItems.setNewsDate(resultSet.getString("ItemDate"));
					newsItems.setNewsTitle(resultSet.getString("Title"));
					newsItems.setUrl(resultSet.getString("URL"));
					newsItems.setCommentsCount(resultSet.getInt("count(i.id)"));
					newsItems.setNewsSource(resultSet.getString("Source"));
					newsItems.setIsLocked(resultSet.getInt("isLocked"));
					
					int newsid=newsItems.getNewsId();
					file = new File(folderimage + "//" + newsid + ".jpg");
					String imageurl = "imagefolder/"+newsid+".jpg";
					if (resultSet.getBlob("NewsImages") != null) {
						Blob blobim = (Blob) resultSet.getBlob("NewsImages");
						InputStream x = blobim.getBinaryStream();
						int size = x.available();
						if (size != 0) {
							newsItems.setImageUrl(imageurl);
							OutputStream out = new FileOutputStream(
									file);
							byte b[] = new byte[size];
							x.read(b);
							out.write(b);
						}
					}
					
					String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
						+ newsid;
					Statement stmt1 = conn.createStatement();
					stmt1 = getConnection().createStatement();
					ResultSet rs1 = stmt1.executeQuery(newsTagsQuery);
	
					// add tags to a newsitem
					while (rs1.next()) {
						for (Object obj : groupCategoryMap.keySet()) {
							// groupCategoryMap has categoryItems, if in its
							// tags there is a tag selected by user ,then
							// add it in associatedtaglist
							HashMap map = ((CategoryItem) groupCategoryMap
								.get(obj)).getItemMap();
							if (map.containsKey(rs1.getInt("TagItemId"))) {
								TagItem tagitem = (TagItem) map.get(rs1
									.getInt("TagItemId"));
								newsItems.addTagforNews(tagitem);
							}
						}
					}
					newsList.add(newsItems);
					rs1.close();
					stmt1.close();
				}
				resultSet.close();
				stmt.close();
				logger.log(Level.INFO,"[StatisticsHelper ::: completed ::: getRefreshMostDiscussedInGroupNews() OR Mode for GroupID :: "+group.getGroupId()+"]");
				return newsList;
			}else if (newsmode == 1) {
				logger.log(Level.INFO, "[StatisticsHelper ::: initated ::: getRefreshMostReadInGroupNews() AND Mode for GroupID :: "+group.getGroupId()+"]");
				ArrayList<NewsItems> newsItems = new ArrayList<NewsItems>();
				ArrayList selectedtaglist = groupCategoryMap.getSelectedTagsByCategory(); // the selected tags on the client side are
																				// in
																				// selectedtaglist
				if (selectedtaglist.size() != 0) {
					CallableStatement proc = null;
					String csv = "";
					int totalcategories = 0;
					int count = 0;

					Iterator iter = selectedtaglist.iterator();
					while (iter.hasNext()) {
						totalcategories++;
						int i = 0;
						ArrayList list = (ArrayList) iter.next();
						Iterator itt = list.iterator();
						while (itt.hasNext()) {
							TagItem tag = (TagItem) itt.next();
							int tagid = tag.getTagId();
							if (i != list.size() - 1) {
								csv += tagid + ","; // csv will contain the
													// string of all tags
													// selected for AND
													// criteria (3663,613|)
								i++;
							} else
								csv += tagid + "|";
						}
					}
					csv = csv.substring(0, csv.length() - 1); // removed the
																// |
					proc = getConnection().prepareCall(
							"{ call SP_GETNEWS(?, ?, ?, ?, ?,?,?) }");
					String tbname1 = "temp11_" + userid;
					String tbname2 = "temp22_" + userid;
					proc.setString(1, csv);
					proc.setInt(2, 0);
					proc.setInt(3, 15);
					proc.setInt(4, totalcategories);
					proc.registerOutParameter(5, Types.INTEGER);
					proc.setString(6, tbname1);
					proc.setString(7, tbname2);
					boolean isresultset = proc.execute();
					if (isresultset) {
						ResultSet rs = proc.getResultSet();
						while (rs.next()) {
							count++;
							NewsItems newsitem = new NewsItems();
							int newsid = rs.getInt("NewsItemId");
							newsitem.setNewsId(newsid);

							newsItems.add(newsitem);
							
						}
						rs.close();
					}
					

					proc.close();

					Iterator iterator = newsItems.iterator();
					String queryForCommentCount = "select n.*, count(i.id) from newsitem n, itemcomment i where n.newsItemId = i.newsItemId and i.newsItemId in(0";
					while (iterator.hasNext()) {
						NewsItems newsItem = (NewsItems) iterator.next();

						queryForCommentCount += "," + newsItem.getNewsId() + "";

					}
					queryForCommentCount += ")group by i.newsItemId	order by count(i.id) desc limit 0,3";

					Statement stmt = conn.createStatement();
					resultSet = stmt.executeQuery(queryForCommentCount);
					while (resultSet.next()) {
						NewsItems newsItem = new NewsItems();

						newsItem.setNewsId(resultSet.getInt("newsItemId"));
						newsItem.setAbstractNews(resultSet
								.getString("Abstract"));
						// newsItems.setCommentsCount(resultSet.getInt("NewscatalystItemCount"));
						newsItem.setImageUrl(resultSet.getString("NewsImages"));
						newsItem.setNewsContent(resultSet.getString("Content"));
						newsItem.setNewsDate(resultSet.getString("ItemDate"));
						newsItem.setNewsTitle(resultSet.getString("Title"));
						newsItem.setUrl(resultSet.getString("URL"));
						newsItem.setCommentsCount(resultSet
								.getInt("count(i.id)"));
						newsItem.setNewsSource(resultSet.getString("Source"));
						newsItem.setIsLocked(resultSet.getInt("isLocked"));

						int newsid = newsItem.getNewsId();
						file = new File(folderimage + "//" + newsid + ".jpg");
						String imageurl = "imagefolder/" + newsid + ".jpg";
						if (resultSet.getBlob("NewsImages") != null) {
							Blob blobim = (Blob) resultSet
									.getBlob("NewsImages");
							InputStream x = blobim.getBinaryStream();
							int size = x.available();
							if (size != 0) {
								newsItem.setImageUrl(imageurl);
								OutputStream out = new FileOutputStream(file);
								byte b[] = new byte[size];
								x.read(b);
								out.write(b);
							}
						}

						String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
								+ newsid;
						Statement stmt1 = conn.createStatement();
						stmt1 = getConnection().createStatement();
						ResultSet rs1 = stmt1.executeQuery(newsTagsQuery);

						// add tags to a newsitem
						while (rs1.next()) {
							for (Object obj : groupCategoryMap.keySet()) {
								// groupCategoryMap has categoryItems, if in its
								// tags there is a tag selected by user ,then
								// add it in associatedtaglist
								HashMap map = ((CategoryItem) groupCategoryMap
										.get(obj)).getItemMap();
								if (map.containsKey(rs1.getInt("TagItemId"))) {
									TagItem tagitem = (TagItem) map.get(rs1
											.getInt("TagItemId"));
									newsItem.addTagforNews(tagitem);
								}
							}
						}
						// newsItems.setAssociatedTagList(resultSet.getString(""));
						newsList.add(newsItem);
					}
				}
				logger.log(Level.INFO, "[StatisticsHelper ::: completed ::: getRefreshMostReadInGroupNews() AND Mode for GroupID :: "+group.getGroupId()+"]");
				return newsList;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.INFO,"[StatisticsHelper ::: EXCEPTION!!! ::: getRefreshMostDiscussedInGroupNews() for GroupID :: "+group.getGroupId()+"]");
		}
		logger.log(Level.INFO,"[StatisticsHelper ::: completed Returning NULL !! ::: getRefreshMostDiscussedInGroupNews() for GroupID :: "+group.getGroupId()+"]");
		return null;
	}
	
	/**
	 * get the news items from the favorite group . If group lastNewsletterDelivery is provided, then the latest 7 news items from the 
	 * lastNewsletterDelivery are to be fetched, else the latest 7 news items from the favorite group should be fetched.
	 * @param group - Favorite group ( group.isFavoriteGroup == 1)
	 * @param userId - logged user id
	 * @param ncid - logged newscenter id
	 * @param lastNewsletterDelivery - last newsletter delivery datetime
	 * @return List<NewsItems>
	 */
	public List<NewsItems> getFavoriteItems(Group group, int userId, int ncid, Timestamp lastNewsletterDelivery){
		logger.log(Level.INFO, "[ StatisticsHelper :: getFavoriteItems() initiated ]");
		long start = System.currentTimeMillis();
		List<NewsItems> newsList = new ArrayList<NewsItems>();
		folderimage = super.getFolderimage();
		folderimage.mkdir();
		GroupCategoryMap groupCategoryMap = super.getCurrentMap();
		try{
			Integer groupId = null;
			
			if(group == null){
				String favGroupQuery = "SELECT groupId FROM `group` where userId = "+userId+" and newsCenterId = "+ncid+" and isFavorite = 1";
				Statement stmt = getConnection().createStatement();
				ResultSet rs = stmt.executeQuery(favGroupQuery);
				while(rs.next()){
					groupId = rs.getInt("groupId");
				}
				rs.close();
				stmt.close();
			}else
				groupId = group.getGroupId();
			
			String tagsQuery = "select distinct t.tagItemId from tagitem t, usergrouptagselection ut where t.tagItemId = ut.tagItemId and ut.NewsCenterId = "
					+ ncid+ " and ut.groupId=" + groupId+ " and ut.userId = "+ userId;
			
			String favItemsQuery = "select distinct n.* from newsitem n, newstagitem nt where n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 7 DAY) and";
			if(lastNewsletterDelivery != null)
				favItemsQuery += " n.UploadedAt >= '"+ lastNewsletterDelivery + "' and" ;
			favItemsQuery += " n.newsItemId = nt.newsItemId and" +
					" nt.tagItemId in(" +tagsQuery+
					")order by n.ItemDate desc limit 0,7;";
			
			Statement stmtForNewsViews = getConnection().createStatement();
			ResultSet resSetForNewsviews = stmtForNewsViews.executeQuery(favItemsQuery);
			while (resSetForNewsviews.next()) {
				NewsItems newsItems = new NewsItems();
				newsItems.setNewsId(resSetForNewsviews.getInt("newsItemId"));
				newsItems.setNewsTitle(resSetForNewsviews.getString("Title"));
				newsItems.setUrl(resSetForNewsviews.getString("URL"));
				newsItems.setIsLocked(resSetForNewsviews.getInt("isLocked"));
				newsItems.setNewsDate(resSetForNewsviews.getString("itemDate"));
				newsItems.setNewsTitle(resSetForNewsviews.getString("Title"));
				newsItems.setAbstractNews(resSetForNewsviews.getString("Abstract"));
				newsItems.setUrl(resSetForNewsviews.getString("URL"));
				
				int newsid=newsItems.getNewsId();
				
				
				file = new File(folderimage + "//" + newsid + ".jpg");
				String imageurl = "imagefolder/"+newsid+".jpg";
				if (resSetForNewsviews.getBlob("NewsImages") != null) {
					Blob blobim = (Blob) resSetForNewsviews.getBlob("NewsImages");
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
					if (size != 0) {
						newsItems.setImageUrl(imageurl);
						OutputStream out = new FileOutputStream(file);
						byte b[] = new byte[size];
						x.read(b);
						out.write(b);
					}
				}
				
				String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="+ newsid;
				Statement stmt = getConnection().createStatement();
				ResultSet rs1 = stmt.executeQuery(newsTagsQuery);
	
				// add tags to a newsitem
				while (rs1.next()) {
					for (Object obj : groupCategoryMap.keySet()) {
						// groupCategoryMap has categoryItems, if in its
						// tags there is a tag selected by user ,then
						// add it in associatedtaglist
						HashMap map = ((CategoryItem) groupCategoryMap
								.get(obj)).getItemMap();
						if (map.containsKey(rs1.getInt("TagItemId"))) {
							TagItem tagitem = (TagItem) map.get(rs1
									.getInt("TagItemId"));
							newsItems.addTagforNews(tagitem);
						}
					}
				}
				newsList.add(newsItems);
				rs1.close();
				stmt.close();
			}
			resSetForNewsviews.close();
			stmtForNewsViews.close();
			logger.log(Level.INFO, "[ GroupHelper :: getFavoriteItems() completed ]");
			long elapsed = System.currentTimeMillis() - start;
			logger.log(Level.INFO,"TIME REQUIRED TO GET FAVORITE GROUP NEWSITEMS  = " + elapsed + "ms");
			return newsList;
		}catch (Exception e) {
			logger.log(Level.SEVERE, "GroupHelper --- getFavoriteItems() EXCPTION!!  Returning NULL!!  "+e.getMessage());
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * get the news items from the favorite group . If group lastNewsletterDelivery is provided, then the latest 7 news items from the 
	 * lastNewsletterDelivery are to be fetched, else the latest 7 news items from the favorite group should be fetched.
	 * @param group - Favorite group ( group.isFavoriteGroup == 1)
	 * @param userId - logged user id
	 * @param ncid - logged newscenter id
	 * @param lastNewsletterDelivery - last newsletter delivery datetime
	 * @return List<NewsItems>
	 */
	public List<NewsItems> getRefreshedFavoriteItems(GroupCategoryMap groupCategoryMap){
		logger.log(Level.INFO, "[ StatisticsHelper :: getRefreshedFavoriteItems() initiated ]");
		List<NewsItems> newsList = new ArrayList<NewsItems>();
		folderimage = super.getFolderimage();
		folderimage.mkdir();
		
		try{
			logger.log(Level.INFO, "[StatisticsHelper :: getRefreshedFavoriteItems() initiated ]"); 
			Connection conn = getConnection();

			String favItemsQuery = "select distinct n.* from newsitem n, newstagitem nt where n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 7 DAY) and" +
					" n.newsItemId = nt.newsItemId and nt.tagItemId in(0" ;
			ArrayList selectedtaglist = groupCategoryMap.getSelectedTags();
			Iterator iter = selectedtaglist.iterator();
			while (iter.hasNext()) {
				TagItem tag = (TagItem) iter.next();
				int tagid = tag.getTagId();
				favItemsQuery += "," + tagid;

			}
			favItemsQuery += ")order by n.ItemDate desc limit 0,7;";
			
			Statement stmtForNewsViews = conn.createStatement();
			ResultSet resSetForNewsviews = stmtForNewsViews.executeQuery(favItemsQuery);
			while (resSetForNewsviews.next()) {
				NewsItems newsItems = new NewsItems();
				newsItems.setNewsId(resSetForNewsviews.getInt("newsItemId"));
				newsItems.setNewsTitle(resSetForNewsviews.getString("Title"));
				newsItems.setUrl(resSetForNewsviews.getString("URL"));
				newsItems.setIsLocked(resSetForNewsviews.getInt("isLocked"));
				newsItems.setNewsDate(resSetForNewsviews.getString("itemDate"));
				newsItems.setNewsTitle(resSetForNewsviews.getString("Title"));
				newsItems.setAbstractNews(resSetForNewsviews.getString("Abstract"));
				newsItems.setUrl(resSetForNewsviews.getString("URL"));
				
				int newsid=newsItems.getNewsId();
				
				
				file = new File(folderimage + "//" + newsid + ".jpg");
				String imageurl = "imagefolder/"+newsid+".jpg";
				if (resSetForNewsviews.getBlob("NewsImages") != null) {
					Blob blobim = (Blob) resSetForNewsviews.getBlob("NewsImages");
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
					if (size != 0) {
						newsItems.setImageUrl(imageurl);
						OutputStream out = new FileOutputStream(file);
						byte b[] = new byte[size];
						x.read(b);
						out.write(b);
					}
				}
				
				String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="+ newsid;
				Statement stmt = conn.createStatement();
				stmt = getConnection().createStatement();
				ResultSet rs1 = stmt.executeQuery(newsTagsQuery);
	
				// add tags to a newsitem
				while (rs1.next()) {
					for (Object obj : groupCategoryMap.keySet()) {
						// groupCategoryMap has categoryItems, if in its
						// tags there is a tag selected by user ,then
						// add it in associatedtaglist
						HashMap map = ((CategoryItem) groupCategoryMap
								.get(obj)).getItemMap();
						if (map.containsKey(rs1.getInt("TagItemId"))) {
							TagItem tagitem = (TagItem) map.get(rs1
									.getInt("TagItemId"));
							newsItems.addTagforNews(tagitem);
						}
					}
				}
				newsList.add(newsItems);
				rs1.close();
				stmt.close();
			}
			resSetForNewsviews.close();
			stmtForNewsViews.close();
			logger.log(Level.INFO, "[ StatisticsHelper :: getRefreshedFavoriteItems() completed ]");
			return newsList;
		}catch (Exception e) {
			logger.log(Level.SEVERE, "StatisticsHelper --- getRefreshedFavoriteItems() EXCPTION!!  Returning NULL!!  "+e.getMessage());
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * This method will  concatenate all the report queries for the list of groups
	 * @param groupList
	 * @param ncid
	 * @param userId
	 * @return
	 * @throws CategoryHelperException
	 */
	public HashMap<String, String> concateGroupsPulseQuery(ArrayList<Group> groupList,int ncid,int userId) throws CategoryHelperException{
		logger.log(Level.INFO, "[ StatisticsHelper ::: concateGroupsPulseQuery()  initiated for USERID:: "+userId+" and NCID :: "+ncid+" ]");
		HashMap<String, String> groupQueryMap = new HashMap<String, String>();
		try {
			String tagsQuery = "select distinct t.tagItemId from tagitem t, usergrouptagselection ut where t.tagItemId = ut.tagItemId and ut.NewsCenterId = "
				+ ncid+ " and ut.groupId in(0";
						
			for(Group group:groupList){
				tagsQuery += ","+group.getGroupId();
			}	
			tagsQuery+=")";
			
			String groupMostReadQuery = "SELECT ni.*, sum(us.NewscatalystItemCount + us.NewsletterItemCount) as viewCount " +
					"FROM newsitem ni, useritemaccessstats us where ni.newsItemId = us.newsItemId and  ni.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 7 DAY)" +
					" and us.newsItemId in(select distinct n.newsItemId from newsitem n, newstagitem nt where n.newsItemId = nt.newsItemId and" +
					" nt.tagItemId in(" +tagsQuery+
					"))group by us.newsItemId  order by viewCount desc limit 0,3";
						

			String groupMostDiscussedQuery = "select n.*, count(i.id) from newsitem n, itemcomment i where n.newsItemId = i.newsItemId"
				+ " and i.newsItemId in(select distinct n.newsItemId from newsitem n, newstagitem nt"
				+ " where n.newsItemId = nt.newsItemId and nt.tagItemId in("+ tagsQuery
				+ ")) group by i.newsItemId "
				+ "order by count(i.id) desc limit 0,3";
						
			groupQueryMap=new HashMap<String, String>();
			groupQueryMap.put("groupMostReadQuery", groupMostReadQuery);
			groupQueryMap.put("groupMostDiscussedQuery", groupMostDiscussedQuery);
			groupQueryMap.put("portalMostReadQuery", getPortalMostReadNewsQuery(ncid));
			groupQueryMap.put("portalMostDiscussedQuery", getPortalMostDiscussedNewsQuery(ncid));
		}catch (Exception e) {
			logger.log(Level.INFO," [ StatisticsHelper ::: concateGroupsPulseQuery() :: EXCEPTION!!!  ] ");
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[ StatisticsHelper ::: concateGroupsPulseQuery()  completed for USERID:: "+userId+" and NCID :: "+ncid+" ]");
		return groupQueryMap;
	}
	
	public String getPortalMostReadNewsQuery(int ncid){
		String portalMostReadQuery = "SELECT ni.*, sum(us.NewscatalystItemCount + us.NewsletterItemCount) as viewCount"
				+ " FROM newsitem ni, useritemaccessstats us where ni.newsItemId = us.newsItemId and  ni.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 7 DAY) and"
				+ " us.newsItemId in(select distinct n.newsItemId from newsitem n, newstagitem nt"
				+ " where n.newsItemId = nt.newsItemId and nt.industryEnumId = "+ ncid
				+ ") group by us.newsItemId order by viewCount desc limit 0,3";
		return portalMostReadQuery;
	}
	
	public String getPortalMostDiscussedNewsQuery(int ncid){
		String portalMostDiscussedQuery = "select n.*, count(i.id) from newsitem n, itemcomment i where n.newsItemId = i.newsItemId "
				+ " and i.newsItemId in(select distinct n.newsItemId from newsitem n, newstagitem nt"
				+ " where n.newsItemId = nt.newsItemId and nt.industryEnumId = "+ ncid
				+ ") group by i.newsItemId order by count(i.id) desc limit 0,3 ";
		return portalMostDiscussedQuery;
	}
	public HashMap<String, List<NewsItems>> getUserAlertPulseItemsForAndMode(ArrayList<Group> groupList, int userid, int ncid) {
		logger.log(Level.INFO, "[StatisticsHelper :: getUserAlertPulseItems() initiated ]"); 
		long start = System.currentTimeMillis();
		HashMap<String, List<NewsItems>> pulseNewsMap = new HashMap<String, List<NewsItems>>();
		folderimage = super.getFolderimage();
		folderimage.mkdir();
		List<NewsItems> groupMostReadAndNewsList = new ArrayList<NewsItems>();
		List<NewsItems> groupMostDiscussedAndNewsList = new ArrayList<NewsItems>();
		
		for(Group group : groupList){
			GroupCategoryMap groupCategoryMap = group.getGroupCategoryMap();
			
			ArrayList<NewsItems> newsItems = new ArrayList<NewsItems>();
			ArrayList selectedtaglist = groupCategoryMap.getSelectedTagsByCategory(); // the selected tagson the client side are in
																			// selectedtaglist
			try{
				if (selectedtaglist.size() != 0) {
					CallableStatement proc = null;
					String csv = "";
					int totalcategories = 0;
					int count = 0;

					Iterator iter = selectedtaglist.iterator();
					while (iter.hasNext()) {
						totalcategories++;
						int i = 0;
						ArrayList list = (ArrayList) iter.next();
						Iterator itt = list.iterator();
						while (itt.hasNext()) {
							TagItem tag = (TagItem) itt.next();
							int tagid = tag.getTagId();
							if (i != list.size() - 1) {
								csv += tagid + ","; // csv will contain the
													// string of all tags
													// selected for AND
													// criteria (3663,613|)
								i++;
							} else
								csv += tagid + "|";
						}
					}
					csv = csv.substring(0, csv.length() - 1); // removed the
																// |
					proc = getConnection().prepareCall(
							"{ call SP_GETNEWS(?, ?, ?, ?, ?,?,?) }");
					String tbname1 = "temp1_" + userid;
					String tbname2 = "temp2_" + userid;
					proc.setString(1, csv);
					proc.setInt(2, 0);
					proc.setInt(3, 15);
					proc.setInt(4, totalcategories);
					proc.registerOutParameter(5, Types.INTEGER);
					proc.setString(6, tbname1);
					proc.setString(7, tbname2);
					boolean isresultset = proc.execute();
					if (isresultset) {
						ResultSet rs = proc.getResultSet();
						while (rs.next()) {
							count++;
							NewsItems newsitem = new NewsItems();
							int newsid = rs.getInt("NewsItemId");
							newsitem.setNewsId(newsid);

							newsItems.add(newsitem);
							
						}
						rs.close();
					}
					proc.close();
					Iterator iterator = newsItems.iterator();
					
					String queryForViewsCount = "SELECT ni.*, sum(us.NewscatalystItemCount + us.NewsletterItemCount) as viewCount"
							+ " FROM newsitem ni, useritemaccessstats us"
							+ " "
							+ "where ni.newsItemId = us.newsItemId and  ni.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 7 DAY) and us.newsItemId in(0";
					String queryForCommentCount = "select n.*, count(i.id) from newsitem n, itemcomment i where n.newsItemId = i.newsItemId and i.newsItemId in(0";
					
					while (iterator.hasNext()) {
						NewsItems newsItem = (NewsItems) iterator.next();
						queryForViewsCount += "," + newsItem.getNewsId() + "";
						queryForCommentCount += "," + newsItem.getNewsId() + "";
					}
					queryForViewsCount += ")group by us.newsItemId  order by viewCount desc limit 0,3 ";
					queryForCommentCount += ")group by i.newsItemId	order by count(i.id) desc limit 0,3";
					
				   
					List<NewsItems> tempgroupMostReadAndNewsList = populatePulseNewsList(queryForViewsCount);
					List<NewsItems> tempgroupMostDiscussedAndNewsList = populatePulseNewsList(queryForCommentCount);
					
					groupMostReadAndNewsList = getConcatenatedList(groupMostReadAndNewsList, tempgroupMostReadAndNewsList);
					groupMostDiscussedAndNewsList = getConcatenatedList(groupMostDiscussedAndNewsList, tempgroupMostDiscussedAndNewsList);
					
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}

		pulseNewsMap.put("groupMostReadNews", groupMostReadAndNewsList);
		pulseNewsMap.put("groupMostDiscussedNews", groupMostDiscussedAndNewsList);
		try{
			String portalMostReadQuery = "SELECT ni.*, sum(us.NewscatalystItemCount + us.NewsletterItemCount) as viewCount"
					+ " FROM newsitem ni, useritemaccessstats us where ni.newsItemId = us.newsItemId and  ni.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 7 DAY) and"
					+ " us.newsItemId in(select distinct n.newsItemId from newsitem n, newstagitem nt"
					+ " where n.newsItemId = nt.newsItemId and nt.industryEnumId = "+ ncid
					+ ") group by us.newsItemId order by viewCount desc limit 0,3";
							
		    String portalMostDiscussedQuery = "select n.*, count(i.id) from newsitem n, itemcomment i where n.newsItemId = i.newsItemId "
					+ " and i.newsItemId in(select distinct n.newsItemId from newsitem n, newstagitem nt"
					+ " where n.newsItemId = nt.newsItemId and nt.industryEnumId = "+ ncid
					+ ") group by i.newsItemId order by count(i.id) desc limit 0,3 ";
			
		    List<NewsItems> portalMostReadNewsList = populatePulseNewsList(portalMostReadQuery);
		    List<NewsItems> portalMostDiscussedNewsList = populatePulseNewsList(portalMostDiscussedQuery);
		    pulseNewsMap.put("portalMostReadNews", portalMostReadNewsList);
		    pulseNewsMap.put("portalMostDiscussedNews", portalMostDiscussedNewsList);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[StatisticsHelper ::: completed ::: getRefreshMostReadInGroupNews() AND Mode ]");
		long elapsed = System.currentTimeMillis() - start;
		logger.log(Level.INFO,"TIME TO GET AND MODE PULSE NEWSITEM FOR ALERT = " + elapsed + "ms");
		return pulseNewsMap;
	}
	
	public HashMap<String, List<NewsItems>> getUserAlertPulseItems(HashMap<String, String> pulseQueryMap){
		logger.log(Level.INFO, "[StatisticsHelper :: getUserAlertPulseItems() initiated ]"); 
		long start = System.currentTimeMillis();
		HashMap<String, List<NewsItems>> pulseNewsMap = new HashMap<String, List<NewsItems>>();
		try{
			folderimage = super.getFolderimage();
			folderimage.mkdir();
			String concatenatedGroupMostReadQuery = pulseQueryMap.get("groupMostReadQuery");
			String concatenatedGroupMostDiscussedQuery = pulseQueryMap.get("groupMostDiscussedQuery");
			String concatenatedPortalMostReadQuery = pulseQueryMap.get("portalMostReadQuery");
			String concatenatedPortalMostDiscussedQuery = pulseQueryMap.get("portalMostDiscussedQuery");
			
			List<NewsItems> groupMostReadNewsList = populatePulseNewsList(concatenatedGroupMostReadQuery);
			List<NewsItems> groupMostDiscussedNewsList = populatePulseNewsList(concatenatedGroupMostDiscussedQuery);
			List<NewsItems> portalMostReadNewsList = populatePulseNewsList(concatenatedPortalMostReadQuery);
			List<NewsItems> portalMostDiscussedNewsList = populatePulseNewsList(concatenatedPortalMostDiscussedQuery);
			
			pulseNewsMap.put("groupMostReadNews", groupMostReadNewsList);
			pulseNewsMap.put("groupMostDiscussedNews", groupMostDiscussedNewsList);
			pulseNewsMap.put("portalMostReadNews", portalMostReadNewsList);
			pulseNewsMap.put("portalMostDiscussedNews", portalMostDiscussedNewsList);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[StatisticsHelper :: getUserAlertPulseItems() completed ]"); 
		long elapsed = System.currentTimeMillis() - start;
		logger.log(Level.INFO,"TIME TO GET OR MODE PULSE NEWSITEM FOR ALERT = " + elapsed + "ms");
		return pulseNewsMap;
	}
	
	private List<NewsItems> populatePulseNewsList(String query){
		logger.log(Level.INFO, "[StatisticsHelper :: populatePulseNewsList() initiated ]"); 
		List<NewsItems> newsList = new ArrayList<NewsItems>();
		try{
			GroupCategoryMap groupCategoryMap = super.getCurrentMap();
			Statement stmt = getConnection().createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			while (resultSet.next()) {
				NewsItems newsItems = new NewsItems();
				newsItems.setNewsId(resultSet.getInt("newsItemId"));
				newsItems.setNewsTitle(resultSet.getString("Title"));
				newsItems.setUrl(resultSet.getString("URL"));
				newsItems.setIsLocked(resultSet.getInt("isLocked"));
				newsItems.setNewsDate(resultSet.getString("itemDate"));
				newsItems.setNewsTitle(resultSet.getString("Title"));
				newsItems.setAbstractNews(resultSet.getString("Abstract"));
				newsItems.setUrl(resultSet.getString("URL"));
				
				int newsid=newsItems.getNewsId();
				
				file = new File(folderimage + "//" + newsid + ".jpg");
				String imageurl = "imagefolder/"+newsid+".jpg";
				if (resultSet.getBlob("NewsImages") != null) {
					Blob blobim = (Blob) resultSet.getBlob("NewsImages");
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
					if (size != 0) {
						newsItems.setImageUrl(imageurl);
						OutputStream out = new FileOutputStream(file);
						byte b[] = new byte[size];
						x.read(b);
						out.write(b);
					}
				}
				
				String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="+ newsid;
				Statement stmt1 = getConnection().createStatement();
				ResultSet rs1 = stmt1.executeQuery(newsTagsQuery);
	
				// add tags to a newsitem
				while (rs1.next()) {
					if(groupCategoryMap!=null){
						for (Object obj : groupCategoryMap.keySet()) {
							// groupCategoryMap has categoryItems, if in its
							// tags there is a tag selected by user ,then
							// add it in associatedtaglist
							HashMap map = ((CategoryItem) groupCategoryMap
									.get(obj)).getItemMap();
							if (map.containsKey(rs1.getInt("TagItemId"))) {
								TagItem tagitem = (TagItem) map.get(rs1
										.getInt("TagItemId"));
								newsItems.addTagforNews(tagitem);
							}
						}
					}
				}
				rs1.close();
				stmt1.close();
				newsList.add(newsItems);
			}
			resultSet.close();
			stmt.close();
		}catch(Exception ex){
			ex.printStackTrace();
			logger.log(Level.INFO,"[ StatisticsHelper :: populatePulseNews() EXCEPTION!!! ]");
		}
		logger.log(Level.INFO, "[StatisticsHelper :: populatePulseNewsList() completed ]"); 
		return newsList;
	}
	
	public List<NewsItems> getConcatenatedList(List<NewsItems> list1, List<NewsItems> list2) {
		logger.log(Level.INFO, "[ StatisticsHelper ::: getConcatenatedList()  initiated  ]");
		List<NewsItems> mergedNewslist= new ArrayList<NewsItems>();
		ArrayList<Integer> mergedIdlist= new ArrayList<Integer>();
		try{
			if(list1 != null){
				for(NewsItems news : list1){
					mergedNewslist.add(news);
					mergedIdlist.add(news.getNewsId());
				}
			}
			if(list2 != null){
				for(NewsItems newsItem : list2){
					if(!(mergedIdlist.contains(newsItem.getNewsId()))){
						mergedNewslist.add(newsItem);
						mergedIdlist.add(newsItem.getNewsId());
					}
				}
			}
		}catch (Exception e) {
			logger.log(Level.INFO, "[ StatisticsHelper ::: getConcatenatedList()  EXCEPTION!!!!  ]");
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[ StatisticsHelper ::: getConcatenatedList()  completed :: mergeList Size :: "+mergedNewslist.size()+"  ]");
		return mergedNewslist;
	}
	
	public GroupCategoryMap getCurrentMap() {
		return currentMap;
	}

	public void setCurrentMap(GroupCategoryMap currentMap) {
		this.currentMap = currentMap;
	}

	public File getFolderimage() {
		return folderimage;
	}
}
