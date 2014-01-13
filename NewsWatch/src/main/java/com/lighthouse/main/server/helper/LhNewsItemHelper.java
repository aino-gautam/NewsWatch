package com.lighthouse.main.server.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.newsletter.server.LhNewsletterHelper;
import com.lighthouse.newsletter.server.PrimaryTagNewsMap;
import com.login.client.UserInformation;
import com.mysql.jdbc.Blob;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.TagItem;
import com.newscenter.client.ui.MainNewsPresenter;
import com.newscenter.server.newsdb.newsdbhelper.NewsItemHelper;

/**
 * 
 * @author prachi This class will override the methods of newsItemHelper to
 *         fetch the newsList as per groupPageCriteria.
 * 
 */

public class LhNewsItemHelper extends NewsItemHelper {

	protected static GroupCategoryMap currentMap;
	Logger logger=Logger.getLogger(LhNewsItemHelper.class.getName());

	public LhNewsItemHelper(GroupCategoryMap groupCategoryMap, String tomcatpath) {
		super(groupCategoryMap, tomcatpath);
		setCurrentMap(groupCategoryMap);
	}

	public GroupCategoryMap getCurrentMap() {
		return currentMap;
	}

	public void setCurrentMap(GroupCategoryMap currentMap1) {
		currentMap = currentMap1	;
	}

	/**
	 * This method gets a page of news based on the criteria i.e start index and
	 * pagesize and the mode of news filtering chosen by the user. If the mode
	 * is OR- all news containing the selected tags are fetched.If the mode is
	 * AND - news containing any of the selected tags of a category and a
	 * selected tag of different categories.
	 * 
	 * @return a page i.e a newslist of the no. of news items specified by the
	 *         pagesize.
	 */
	public NewsItemList getPage(GroupPageCriteria criteria, int newsmode, int userid) {
		logger.log(Level.INFO, "[ LhNewsItemHelper getPage() initiated ]");
		NewsItemList newspage = new NewsItemList();
		ArrayList selectedtaglist;
		NewsItems newsitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		GroupCategoryMap groupCategoryMap = getCurrentMap();
		if (groupCategoryMap.size() != 0) {
			try {
				if (newsmode == MainNewsPresenter.OR) {
					// newspage.setAndNews(false);
					logger.log(Level.INFO, "[ LhNewsItemHelper getPage() newsmode OR ]");
					selectedtaglist = groupCategoryMap.getSelectedTags();
					Iterator iter = selectedtaglist.iterator();
					String newsIdCountQuery = "SELECT count(distinct n.NewsItemId) from newsitem n INNER JOIN newstagitem nt where n.isReport = 0 and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
					String selectNewsQuery = "SELECT distinct n.NewsItemId,n.author,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source, n.isLocked from newsitem n INNER JOIN newstagitem nt where n.isReport = 0 and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
					while (iter.hasNext()) {
						TagItem tag = (TagItem) iter.next();
						int tagid = tag.getTagId();
						selectNewsQuery += "," + tagid;
						newsIdCountQuery += "," + tagid;
					}
					selectNewsQuery += ")order by n.ItemDate desc LIMIT "
							+ criteria.getStartIndex() + ","
							+ criteria.getPageSize();
					newsIdCountQuery += ")order by n.ItemDate desc";

					// find no. of pages
					Statement stmt2 = getConnection().createStatement();
					ResultSet rs2 = stmt2.executeQuery(newsIdCountQuery);
					while (rs2.next()) {
						int numitems = rs2.getInt(1);
						newspage.setNumItems(numitems);
						int pages = 0;
						if (numitems % criteria.getPageSize() == 0)
							pages = numitems / criteria.getPageSize();
						else
							pages = (numitems / criteria.getPageSize() + 1);
						newspage.setTotalPages(pages);
					}
					rs2.close();
					stmt2.close();

					// fetches the news item records
					Statement stmt = getConnection().createStatement();
					ResultSet rs = stmt.executeQuery(selectNewsQuery);
					while (rs.next()) {

						newsitem = new NewsItems();
						int newsid = rs.getInt("NewsItemId");
						newsitem.setNewsId(newsid);

						/* query to find the tags for a given newsid */
						String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
								+ newsid;

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
									newsitem.addTagforNews(tagitem);
								}
							}
						}

						/* fetch comment count */
						String commentCountQuery = "select count(*) from itemcomment i where i.newsItemId = "
								+ newsid;
						ResultSet rs3 = stmt.executeQuery(commentCountQuery);
						while (rs3.next()) {
							newsitem.setCommentsCount(rs3.getInt(1));
						}

						// fetch views count
						// String viewCountQuery =
						// "SELECT NewscatalystItemCount FROM useritemaccessstats u where newsitemId="+newsid
						// ;
						String viewCountQuery = "SELECT sum(NewscatalystItemCount+NewsletterItemCount) as viewCount FROM useritemaccessstats where newsItemId = "
								+ newsid;
						ResultSet rs4 = stmt.executeQuery(viewCountQuery);
						while (rs4.next()) {
							newsitem.setViewsCount(rs4.getInt(1));
						}
						rs1.close();
						rs3.close();
						rs4.close();

						// populate the news item object
						String newsTitle = new String(rs.getString("Title")
								.getBytes("utf-8"), "utf-8");
						newsitem.setNewsTitle(newsTitle);
						String newsAbstract = new String(rs.getString(
								"Abstract").getBytes("utf-8"), "utf-8");
						newsitem.setAbstractNews(newsAbstract);
						newsitem.setUrl(rs.getString("URL"));
						newsitem.setNewsDate(rs.getDate("ItemDate").toString());
						newsitem.setAuthor(rs.getString("author"));
						newsitem.setNewsSource(rs.getString("Source"));
						newsitem.setIsLocked(rs.getInt("isLocked"));
						
						file = new File(folderimage+"//"+newsid+".jpg");
						System.out.println("file path in userhelperadmin "+file.getPath());
						String imageurl = "imagefolder/"+newsid+".jpg";
						System.out.println("image url :::: " +imageurl);
						
						if(rs.getBlob("NewsImages") != null){
							System.out.println("image present");
							Blob blobimg = (Blob)rs.getBlob("NewsImages");
							InputStream x = blobimg.getBinaryStream();
							int size = x.available();
							if (size != 0) {
								newsitem.setImageUrl(imageurl);
								OutputStream out = new FileOutputStream(
										file);
								byte b[] = new byte[size];
								x.read(b);
								out.write(b);
							}
						}
						newspage.add(newsitem);
					}
					rs.close();
					stmt.close();
				} else if (newsmode == MainNewsPresenter.AND) {
					// newspage.setAndNews(true);
					logger.log(Level.INFO, "[ LhNewsItemHelper getPage() AND mode ]");
					selectedtaglist = groupCategoryMap
							.getSelectedTagsByCategory(); // the selected tagson
															// the client side
															// are in
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
								"{ call SP_GETNEWS(?, ?, ?, ?, ?, ?, ?) }");
						String tbname1 = "temp1_" + userid;
						String tbname2 = "temp2_" + userid;
						proc.setString(1, csv);
						proc.setInt(2, criteria.getStartIndex());
						proc.setInt(3, criteria.getPageSize());
						proc.setInt(4, totalcategories);
						proc.registerOutParameter(5, Types.INTEGER);
						proc.setString(6, tbname1);
						proc.setString(7, tbname2);
						boolean isresultset = proc.execute();
						if (isresultset) {
							ResultSet rs = proc.getResultSet();
							while (rs.next()) {
								count++;
								newsitem = new NewsItems();
								int newsid = rs.getInt("NewsItemId");
								newsitem.setNewsId(newsid);

								String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
										+ newsid; // get the tag associated the
													// news
								Statement stmt = getConnection()
										.createStatement();
								ResultSet rs1 = stmt
										.executeQuery(newsTagsQuery);
								while (rs1.next()) {
									for (Object obj : groupCategoryMap.keySet()) {
										HashMap map = ((CategoryItem) groupCategoryMap
												.get(obj)).getItemMap(); // get
																			// tags
																			// of
																			// the
																			// category
										if (map.containsKey(rs1
												.getInt("TagItemId"))) {
											TagItem tagitem = (TagItem) map
													.get(rs1.getInt("TagItemId"));
											newsitem.addTagforNews(tagitem);
										}
									}
								}
								/* fetch comment count */
								String commentCountQuery = "select count(*) from itemcomment i where i.newsItemId = "
										+ newsid;
								ResultSet rs3 = stmt
										.executeQuery(commentCountQuery);
								while (rs3.next()) {
									newsitem.setCommentsCount(rs3.getInt(1));
								}

								// fetch views count
								// String viewCountQuery =
								// "SELECT NewscatalystItemCount FROM useritemaccessstats u where newsitemId="+newsid
								// ;
								String viewCountQuery = "SELECT sum(NewscatalystItemCount+NewsletterItemCount) as viewCount FROM useritemaccessstats where newsItemId = "
										+ newsid;
								ResultSet rs4 = stmt
										.executeQuery(viewCountQuery);
								while (rs4.next()) {
									newsitem.setViewsCount(rs4.getInt(1));
								}
								rs1.close();
								rs3.close();
								rs4.close();
								stmt.close();

								// populate news item
								String newsTitle = new String(rs.getString(
										"Title").getBytes("utf-8"), "utf-8");
								newsitem.setNewsTitle(newsTitle);
								String newsAbstract = new String(rs.getString(
										"Abstract").getBytes("utf-8"), "utf-8");
								newsitem.setAbstractNews(newsAbstract);
								newsitem.setUrl(rs.getString("URL"));
								newsitem.setNewsDate(rs.getDate("ItemDate")
										.toString());
								newsitem.setNewsSource(rs.getString("Source"));
								file = new File(folderimage + "//" + newsid
										+ ".jpg");
								String imageurl = "imagefolder/"+newsid+".jpg";
								Blob blobim = (Blob) rs.getBlob("NewsImages");
								if(blobim!=null){
									InputStream x = blobim.getBinaryStream();
									int size = x.available();
									if (size != 0) {
										newsitem.setImageUrl(imageurl);
										OutputStream out = new FileOutputStream(
												file);
										byte b[] = new byte[size];
										x.read(b);
										out.write(b);
									}
								}
								newspage.add(newsitem);
							}
							rs.close();
						}
						// calculate total pages
						int numitems = proc.getInt(5);
						newspage.setNumItems(numitems);
						int pages = 0;
						if (numitems % criteria.getPageSize() == 0)
							pages = numitems / criteria.getPageSize();
						else
							pages = (numitems / criteria.getPageSize() + 1);
						newspage.setTotalPages(pages);

						proc.close();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		newspage.setTagNews(false);
		return newspage;
	}

	/**
	 * This method is called when the criteria changes.
	 * 
	 * @param criteria
	 *            with the groupId in it
	 * @param newsmode
	 * @param userid
	 * @return {@link NewsItemList}
	 */
	public NewsItemList getAndPage(GroupPageCriteria criteria, int newsmode,int userid) {
		logger.log(Level.INFO, "[ LhNewsItemHelper getAndPage() initiated ]");
		NewsItemList newspage = new NewsItemList();
		ArrayList selectedtaglist;
		NewsItems newsitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		GroupCategoryMap groupCategoryMap = getCurrentMap();

		if (groupCategoryMap.size() != 0) {
			try {
				if (newsmode == MainNewsPresenter.OR) {
					logger.log(Level.INFO, "[ LhNewsItemHelper getAndPage() OR mode ]");
					selectedtaglist = groupCategoryMap.getSelectedTags();
					Iterator iter = selectedtaglist.iterator();
					String newsCountQuery = "SELECT count(distinct n.NewsItemId) from newsitem n INNER JOIN newstagitem nt where n.isReport = 0 and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
					String selectNewsQuery = "SELECT distinct n.NewsItemId,n.author,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source,n.isLocked from newsitem n INNER JOIN newstagitem nt where n.isReport = 0 and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
					while (iter.hasNext()) {
						TagItem tag = (TagItem) iter.next();
						int tagid = tag.getTagId();
						selectNewsQuery += "," + tagid;
						newsCountQuery += "," + tagid;
					}
					selectNewsQuery += ")order by n.ItemDate desc LIMIT "
							+ criteria.getStartIndex() + ","
							+ criteria.getPageSize();
					newsCountQuery += ")order by n.ItemDate desc";

					Statement stmt2 = getConnection().createStatement();
					ResultSet rs2 = stmt2.executeQuery(newsCountQuery);
					while (rs2.next()) {
						int numitems = rs2.getInt(1);
						newspage.setNumItems(numitems);
						int pages = 0;
						if (numitems % criteria.getPageSize() == 0)
							pages = numitems / criteria.getPageSize();
						else
							pages = (numitems / criteria.getPageSize() + 1);
						newspage.setTotalPages(pages);
					}
					rs2.close();
					stmt2.close();

					Statement stmt = getConnection().createStatement();
					ResultSet rs = stmt.executeQuery(selectNewsQuery);
					while (rs.next()) {

						newsitem = new NewsItems();
						int newsid = rs.getInt("NewsItemId");
						newsitem.setNewsId(newsid);

						String newsTagQuery = "SELECT TagItemId from newstagitem where NewsItemId="
								+ newsid;
						stmt = getConnection().createStatement();
						ResultSet rs1 = stmt.executeQuery(newsTagQuery);
						while (rs1.next()) {
							for (Object obj : groupCategoryMap.keySet()) {
								HashMap map = ((CategoryItem) groupCategoryMap
										.get(obj)).getItemMap();
								if (map.containsKey(rs1.getInt("TagItemId"))) {
									TagItem tagitem = (TagItem) map.get(rs1
											.getInt("TagItemId"));
									newsitem.addTagforNews(tagitem);
								}
							}
						}
						rs1.close();
						String newsTitle = new String(rs.getString("Title")
								.getBytes("utf-8"), "utf-8");
						newsitem.setNewsTitle(newsTitle);
						String newsAbstract = new String(rs.getString(
								"Abstract").getBytes("utf-8"), "utf-8");
						newsitem.setAbstractNews(newsAbstract);
						newsitem.setUrl(rs.getString("URL"));
						newsitem.setNewsDate(rs.getDate("ItemDate").toString());
						newsitem.setAuthor(rs.getString("author"));
						newsitem.setNewsSource(rs.getString("Source"));
						newsitem.setIsLocked(rs.getInt("isLocked"));
						file = new File(folderimage + "//" + newsid + ".jpg");
						String imageurl = "imagefolder/"+newsid+".jpg";

						Blob blobim = (Blob) rs.getBlob("NewsImages");
						if(blobim!=null){
							InputStream x = blobim.getBinaryStream();
							int size = x.available();
							if (size != 0) {
								newsitem.setImageUrl(imageurl);
								OutputStream out = new FileOutputStream(file);
								byte b[] = new byte[size];
								x.read(b);
								out.write(b);
							}
						}
						newspage.add(newsitem);
					}
					rs.close();
					stmt.close();
					newspage.setAndNews(false);
				} else if (newsmode == MainNewsPresenter.AND) {
					logger.log(Level.INFO, "[ LhNewsItemHelper getAndPage() AND mode ]");
					selectedtaglist = groupCategoryMap
							.getSelectedTagsByCategory();
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
									csv += tagid + ",";
									i++;
								} else
									csv += tagid + "|";
							}
						}
						csv = csv.substring(0, csv.length() - 1);
						proc = getConnection().prepareCall(
								"{ call SP_GETNEWS(?, ?, ?, ?, ?, ?, ?) }");
						String tbname1 = "temp1_" + userid;
						String tbname2 = "temp2_" + userid;
						proc.setString(1, csv);
						proc.setInt(2, criteria.getStartIndex());
						proc.setInt(3, criteria.getPageSize());
						proc.setInt(4, totalcategories);
						proc.registerOutParameter(5, Types.INTEGER);
						proc.setString(6, tbname1);
						proc.setString(7, tbname2);
						boolean isresultset = proc.execute();
						if (isresultset) {
							ResultSet rs = proc.getResultSet();
							while (rs.next()) {
								count++;
								newsitem = new NewsItems();
								int newsid = rs.getInt("NewsItemId");
								newsitem.setNewsId(newsid);

								String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
										+ newsid;
								Statement stmt = getConnection()
										.createStatement();
								ResultSet rs1 = stmt
										.executeQuery(newsTagsQuery);
								while (rs1.next()) {
									for (Object obj : groupCategoryMap.keySet()) {
										HashMap map = ((CategoryItem) groupCategoryMap.get(obj)).getItemMap();
										if (map.containsKey(rs1.getInt("TagItemId"))) {
											TagItem tagitem = (TagItem) map.get(rs1.getInt("TagItemId"));
											newsitem.addTagforNews(tagitem);
										}
									}
								}
								rs1.close();
								String newsTitle = new String(rs.getString(
										"Title").getBytes("utf-8"), "utf-8");
								newsitem.setNewsTitle(newsTitle);
								String newsAbstract = new String(rs.getString(
										"Abstract").getBytes("utf-8"), "utf-8");
								newsitem.setAbstractNews(newsAbstract);
								newsitem.setUrl(rs.getString("URL"));
								newsitem.setNewsDate(rs.getDate("ItemDate")
										.toString());
								newsitem.setNewsSource(rs.getString("Source"));
								file = new File(folderimage + "//" + newsid
										+ ".jpg");
								String imageurl = "imagefolder/"+newsid+".jpg";
								Blob blobim = (Blob) rs.getBlob("NewsImages");
								if(blobim!=null){
									InputStream x = blobim.getBinaryStream();
									int size = x.available();
									if (size != 0) {
										newsitem.setImageUrl(imageurl);
										OutputStream out = new FileOutputStream(
												file);
										byte b[] = new byte[size];
										x.read(b);
										out.write(b);
									}
								}
								newspage.add(newsitem);
							}
							rs.close();

						}
						int numitems = proc.getInt(5);
						newspage.setNumItems(numitems);
						int pages = 0;
						if (numitems % criteria.getPageSize() == 0)
							pages = numitems / criteria.getPageSize();
						else
							pages = (numitems / criteria.getPageSize() + 1);
						newspage.setTotalPages(pages);
						proc.close();
						newspage.setAndNews(true);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		newspage.setTagNews(false);
		logger.log(Level.INFO, "[ LhNewsItemHelper getAndPage() completed ]");
		return newspage;
	}

	/**
	 * This method fetches all the newsitems related to the specified tag and
	 * returns an arraylist of newsitems
	 * 
	 * @param tagitem
	 *            - the tag for which the corresponding news items need to be
	 *            fetched
	 * @return - an arraylist of newitems for the specified tagitem
	 */
	public NewsItemList getAllNewsforTag(TagItem tagitem, GroupPageCriteria criteria) {
		logger.log(Level.INFO, "[ LhNewsItemHelper getAllNewsforTag() initiated ]");
		HashMap<TagItem, ArrayList<NewsItems>> hashMap = new HashMap<TagItem, ArrayList<NewsItems>>();
		NewsItemList tagnews = new NewsItemList();
		NewsItemList newsList = new NewsItemList();
		NewsItems newsitem;
		ArrayList<TagItem> selectedtaglist;
		//TagItem tagItem = tagitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		// first query fetches the count and second gives the newsitems details
		// of the no of news from count
		GroupCategoryMap groupCategoryMap = getCurrentMap();
		if (groupCategoryMap.size() != 0) {
		try {
			selectedtaglist = groupCategoryMap.getSelectedTags();
			Iterator<TagItem> iter = selectedtaglist.iterator();
			int tagId = tagitem.getTagId();
			String newsCountQuery = "SELECT count(distinct n.NewsItemId) from newsitem n INNER JOIN newstagitem nt where n.isReport = 0 and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";

			String selectNewsQuery = "SELECT distinct n.NewsItemId,n.author,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source, n.isLocked from newsitem n INNER JOIN newstagitem nt "
					+ "where n.isReport = 0 and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";

			while (iter.hasNext()) {
				TagItem tag = (TagItem) iter.next();
				int tagid = tag.getTagId();
				selectNewsQuery += "," + tagid;
				newsCountQuery += "," + tagid;

			}
			selectNewsQuery += ")  and n.newsItemId in(select nti.newsitemid from newstagitem nti where nti.tagitemid="
					+ tagId
					+ ")order by n.ItemDate desc LIMIT "
					+ criteria.getStartIndex() + "," + criteria.getPageSize();
			newsCountQuery += ")and n.newsItemId in(select nti.newsitemid from newstagitem nti where nti.tagitemid="
					+ tagId + ")order by n.ItemDate desc ";

			Statement stmt2 = getConnection().createStatement();
			ResultSet rs2 = stmt2.executeQuery(newsCountQuery);

			while (rs2.next()) {
				int numitems = rs2.getInt(1);
				tagnews.setNumItems(numitems);
				int pages = 0;
				if (numitems % criteria.getPageSize() == 0)
					pages = numitems / criteria.getPageSize();
				else
					pages = (numitems / criteria.getPageSize() + 1);
				tagnews.setTotalPages(pages);
			}
			rs2.close();
			stmt2.close();

			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(selectNewsQuery);

			while (rs.next()) {
				newsitem = new NewsItems();
				int newsid = rs.getInt("NewsItemId");
				newsitem.setNewsId(newsid);
				String newsTitle = new String(rs.getString("Title").getBytes(
						"utf-8"), "utf-8");
				newsitem.setNewsTitle(newsTitle);
				String newsAbstract = new String(rs.getString("Abstract")
						.getBytes("utf-8"), "utf-8");
				newsitem.setAbstractNews(newsAbstract);

				newsitem.setUrl(rs.getString("URL"));
				newsitem.setNewsDate(rs.getDate("ItemDate").toString());
				newsitem.setAuthor(rs.getString("author"));
				newsitem.setNewsSource(rs.getString("Source"));
				newsitem.setIsLocked(rs.getInt("isLocked"));
				String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
						+ newsid;
				/*
				 * String newsTagsQuery =
				 * "SELECT TagItemId from newstagitem where NewsItemId=" +
				 * newsid;
				 */// if this id is 5,getItemMap for this tag
				stmt = getConnection().createStatement();
				ResultSet rs1 = stmt.executeQuery(newsTagsQuery);
				while (rs1.next()) {
					for (Object obj : groupCategoryMap.keySet()) {
						HashMap map = ((CategoryItem) groupCategoryMap.get(obj))
								.getItemMap();
						if (map.containsKey(rs1.getInt("TagItemId"))) {
							TagItem tag = (TagItem) map.get(rs1
									.getInt("TagItemId"));
							/*
							 * if(rs1.getBoolean("isPrimary"))
							 * tagitem.setPrimary(true); else
							 * tagitem.setPrimary(false);
							 */
							newsitem.addTagforNews(tag);
						}
					}

				}

				String commentCountQuery = "select count(*) from itemcomment i where i.newsItemId = "
						+ newsid;
				ResultSet rs3 = stmt.executeQuery(commentCountQuery);
				while (rs3.next()) {
					newsitem.setCommentsCount(rs3.getInt(1));
				}

				// fetch views count
				// String viewCountQuery =
				// "SELECT NewscatalystItemCount FROM useritemaccessstats u where newsitemId="+newsid
				// ;
				String viewCountQuery = "SELECT sum(NewscatalystItemCount+NewsletterItemCount) as viewCount FROM useritemaccessstats where newsItemId = "
						+ newsid;
				ResultSet rs4 = stmt.executeQuery(viewCountQuery);
				while (rs4.next()) {
					newsitem.setViewsCount(rs4.getInt(1));
				}

				rs1.close();
				rs3.close();
				rs4.close();
				file = new File(folderimage + "//" + newsid + ".jpg");
				/*
				 * String imageurlTomcatFolder = "imagefolder"; String imageurl
				 * = imageurlTomcatFolder + "/" + newsid + ".jpg";
				 */
				String imageurl = "imagefolder/" + newsid + ".jpg";

				Blob blobim = (Blob) rs.getBlob("NewsImages");
				if(blobim!=null){
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
					if (size != 0) {
						newsitem.setImageUrl(imageurl);
						OutputStream out = new FileOutputStream(file);
						byte b[] = new byte[size];
						x.read(b);
						out.write(b);
					}
				}
				
				newsList.add(newsitem);
				

			}
			if(!newsList.isEmpty())
				hashMap.put(tagitem, newsList);
			rs.close();
			stmt.close();
			if(!(hashMap.size()==0))
			tagnews.add(hashMap);
		} catch (Exception ex) {
			logger.log(Level.INFO, "[ LhNewsItemHelper getAllNewsforTag() EXCEPTION!! ]");
			ex.printStackTrace();
		}
		tagnews.setTagNews(true);
		}
		logger.log(Level.INFO, "[ LhNewsItemHelper getAllNewsforTag() completed ]");
		return tagnews;
	}

	/**
	 * generates the news fetching query and populates in a map
	 * @param userid
	 * @param filtermode
	 * @param lastNewsDeliveryTime
	 * @param updateQueryFlag
	 * @param ncid
	 * @return
	 */
	public HashMap<String, String> generateNewsQueryMapForAllNewsItems(int userid, int filtermode, Timestamp lastNewsDeliveryTime, Boolean updateQueryFlag,int ncid, boolean isWeekly) {
		logger.log(Level.INFO, "[ LhNewsItemHelper generateNewsQueryMapForAllNewsItems() initiated ]");
		ArrayList selectedTagsList = null;
		GroupCategoryMap groupCategoryMap = getCurrentMap();
		String newTagsQuery = null;
		String existingTagsQuery = null;
		String updateNewTagsStatusQuery = null;
		HashMap<String, String> queryMap = new HashMap<String, String>();
		
		LhNewsletterHelper helper=new LhNewsletterHelper();
		int isReportPermitted=helper.getUserReportPermission(userid,ncid);
		
		if (groupCategoryMap.size() != 0) {
			try {

				selectedTagsList = groupCategoryMap.getSelectedTags();

				if (selectedTagsList.size() > 0) {
					// if (filtermode == 0) {
					Iterator iter = selectedTagsList.iterator();
					if(isReportPermitted==1){
						if(isWeekly){
							newTagsQuery = "SELECT distinct n.NewsItemId from newsitem n INNER JOIN newstagitem nt where  n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 10 DAY) and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
							existingTagsQuery = "SELECT distinct n.NewsItemId from newsitem n INNER JOIN newstagitem nt where  n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 10 DAY) and n.UploadedAt >= DATE_SUB('"
									+ lastNewsDeliveryTime
									+ "',INTERVAL 6 DAY) and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
						}else{
							newTagsQuery = "SELECT distinct n.NewsItemId from newsitem n INNER JOIN newstagitem nt where  n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 3 DAY) and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
							existingTagsQuery = "SELECT distinct n.NewsItemId from newsitem n INNER JOIN newstagitem nt where  n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 3 DAY) and n.UploadedAt >= '"
									+ lastNewsDeliveryTime
									+ "' and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
						}
					}
					else{
						if(isWeekly){
							newTagsQuery = "SELECT distinct n.NewsItemId from newsitem n INNER JOIN newstagitem nt where n.isReport=0 and n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 10 DAY) and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
							existingTagsQuery = "SELECT distinct n.NewsItemId from newsitem n INNER JOIN newstagitem nt where n.isReport=0 and n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 10 DAY) and n.UploadedAt >= DATE_SUB('"
									+ lastNewsDeliveryTime
									+ "',INTERVAL 6 DAY) and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
						}else{
							newTagsQuery = "SELECT distinct n.NewsItemId from newsitem n INNER JOIN newstagitem nt where n.isReport=0 and n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 3 DAY) and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
							existingTagsQuery = "SELECT distinct n.NewsItemId from newsitem n INNER JOIN newstagitem nt where n.isReport=0 and n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 3 DAY) and n.UploadedAt >= '"
									+ lastNewsDeliveryTime
									+ "' and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
						}
					}

					if (!updateQueryFlag)
						updateNewTagsStatusQuery = "update usergrouptagselection set TagSelectionStatus = 0 where UserId = "
								+ userid + " and TagItemId in(0";
					else
						updateNewTagsStatusQuery = "";

					while (iter.hasNext()) {
						TagItem tag = (TagItem) iter.next();
						int tagid = tag.getTagId();
						if (tag.isTagSelectionStatus() == true) {
							newTagsQuery += "," + tagid;
							updateNewTagsStatusQuery += "," + tagid;
							tag.setTagSelectionStatus(false);
						} else
							existingTagsQuery += "," + tagid;
					}
					newTagsQuery += ")order by prioritylevel desc, n.ItemDate desc";
					existingTagsQuery += ")order by prioritylevel desc, n.ItemDate desc";

					queryMap.put("newTagsQuery", newTagsQuery);
					queryMap.put("existingTagsQuery", existingTagsQuery);
					queryMap.put("updateNewTagsStatusQuery",
							updateNewTagsStatusQuery);
				}

				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.log(Level.INFO, "[ LhNewsItemHelper generateNewsQueryMapForAllNewsItems() completed ]");
		return queryMap;

	}
	
	/**
	 * This method gets all the news items based on the selections of the user
	 * for the newsletter subscribed to by the user. These news are fetched
	 * based on (i) if the user has subscribed to a daily or weekly newsletter
	 * (ii) if the user is receiving the newsletter for the first time - news
	 * dated 3 days back are fetched (iii) from the second time onwards only
	 * updated news items in the past 3 days are fetched while news dated 3 days
	 * back are fetched for newly selected tags (for which news were'nt sent
	 * earlier in the newsletter)
	 * 
	 * @param userid
	 *            - the id of the user
	 * @param datetime
	 *            - the time after which news were uploaded to find out only
	 *            those news items which are dated 3 days back but were uploaded
	 *            before this datetime
	 * @return a newslist of all news items
	 */

	/**
	 * This method will fetch the newsItems from the union queries generated .
	 */
	public HashMap<TagItem, List<NewsItems>> getAllNewsItemsForUser(String concatedNewTagsQuery,String concatedExistingTagsQuery,String concatedUpdateNewTagsStatusQuery, int ncid) {
		logger.log(Level.INFO, "[ LhNewsItemHelper getAllNewsItemsForUser() initiated ]");
		 long start = System.currentTimeMillis();
		NewsItemList newslist = new NewsItemList();
		NewsItems newsitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		
		HashMap<TagItem, List<NewsItems>> primaryTagNewsMap = new HashMap<TagItem, List<NewsItems>>();
		ArrayList<Integer> newsIdList = new ArrayList<Integer>();
		
		try {
			// code for newsTagsQuery
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(concatedNewTagsQuery);
			while (rs.next()) { // 1
				int newsid = rs.getInt("NewsItemId");
				newsIdList.add(newsid);
			} // 1
			rs.close();
			stmt.close();

			// code for existing tags query
			Statement stmt2 = getConnection().createStatement();
			ResultSet rs2 = stmt2.executeQuery(concatedExistingTagsQuery);
			while (rs2.next()) {
				int newsid = rs2.getInt("NewsItemId");
				newsIdList.add(newsid);
			}
			rs2.close();
			stmt2.close();
			
			primaryTagNewsMap = getPrimaryTagNews(newsIdList, ncid);
			
			Statement stmt5 = getConnection().createStatement();
			stmt5.executeUpdate(concatedUpdateNewTagsStatusQuery);
			stmt5.close();
		} // try ends
		catch (Exception e) {
			logger.log(Level.INFO, "[ LhNewsItemHelper getAllNewsItemsForUser() EXCEPTION!!! ]");
			e.printStackTrace();
		} // gcmp ends
		logger.log(Level.INFO, "[ LhNewsItemHelper getAllNewsItemsForUser() completed ]");
		long elapsed = System.currentTimeMillis() - start;
		logger.log(Level.INFO,"NEWS FETCHING TIME FOR OR MODE GROUP LIST OF ALERT  = " + elapsed + "ms");
		return primaryTagNewsMap;
	}

	public HashMap<TagItem, List<NewsItems>> getAllNewsItemsForUserAndMode(Group group, int userid, int ncid, Timestamp lastNewsDeliveryTime) {
		logger.log(Level.INFO, "[ LhNewsItemHelper getAllNewsItemsForUserAndMode() initiated ]");
		long start = System.currentTimeMillis();
		NewsItemList newslist = new NewsItemList();
		NewsItems newsitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		GroupCategoryMap groupCategoryMap = group.getGroupCategoryMap();
		
		HashMap<TagItem, List<NewsItems>> primaryTagNewsMap = new HashMap<TagItem, List<NewsItems>>();
		ArrayList<Integer> newsIdList = new ArrayList<Integer>();
		
		try {
			CallableStatement proc = null;
			String csv1 = "";
			String csv2 = "";
			int totalcategoriescsv1 = 0;
			int totalcategoriescsv2 = 0;
			boolean flagcsv1 = false;
			boolean flagcsv2 = false;
			int count = 0;
			
			String query3 = "update usergrouptagselection set TagSelectionStatus = 0 where UserId = "
					+ userid + " and TagItemId in(0";
			ArrayList selectedTagsList = groupCategoryMap.getSelectedTagsByCategory();

			Iterator iter = selectedTagsList.iterator();
			while (iter.hasNext()) {

				int i = 0;
				ArrayList list = (ArrayList) iter.next();
				Iterator itt = list.iterator();
				while (itt.hasNext()) {
					TagItem tag = (TagItem) itt.next();
					int tagid = tag.getTagId();
					if (tag.isTagSelectionStatus() == true) {

						if (i != list.size() - 1) {
							query3 += "," + tagid;
							csv1 += tagid + ",";
							flagcsv1 = true;
							i++;
						} else {
							csv1 += tagid + "|";
							flagcsv1 = true;
						}

					} else {

						if (i != list.size() - 1) {
							csv2 += tagid + ",";
							flagcsv2 = true;
							i++;
						} else {
							csv2 += tagid + "|";
							flagcsv2 = true;
						}

					}
				}
				if (csv1.length() != 0) {
					csv1 = csv1.substring(0, csv1.length() - 1);
					csv1 += "|";
				}
				if (csv2.length() != 0) {
					csv2 = csv2.substring(0, csv2.length() - 1);
					csv2 += "|";
				}
				if (flagcsv1 == true) {
					totalcategoriescsv1++;
					flagcsv1 = false;
				}

				if (flagcsv2 == true) {
					totalcategoriescsv2++;
					flagcsv2 = false;
				}
			}
			if (csv1.length() != 0) {
				csv1 = csv1.substring(0, csv1.length() - 1);
				proc = getConnection().prepareCall(
						"{ call SP_GETANDNEWS(?, ?, ?, ?, ?) }");
				String tbname1 = "temp1_" + userid;
				String tbname2 = "temp2_" + userid;
				proc.setString(1, csv1);
				proc.setInt(2, totalcategoriescsv1);
				proc.registerOutParameter(3, Types.INTEGER);
				proc.setString(4, tbname1);
				proc.setString(5, tbname2);
				boolean isresultset = proc.execute();
				if (isresultset) {
					ResultSet rs = proc.getResultSet();
					while (rs.next()) {
						count++;
						
						int newsid = rs.getInt("NewsItemId");
						newsIdList.add(newsid);
					}
					rs.close();
				}
			}

			if (csv2.length() != 0) {
				csv2 = csv2.substring(0, csv2.length() - 1);
				proc = getConnection().prepareCall(
						"{ call SP_GETANDREGULARNEWS(?, ?, ?, ?, ?, ?) }");
				String tbname1 = "temp1_" + userid;
				String tbname2 = "temp2_" + userid;
				proc.setString(1, csv2);
				proc.setInt(2, totalcategoriescsv2);
				proc.registerOutParameter(3, Types.INTEGER);
				proc.setString(4, tbname1);
				proc.setString(5, tbname2);
				proc.setTimestamp(6, lastNewsDeliveryTime);
				boolean isresultset = proc.execute();
				if (isresultset) {
					ResultSet rs = proc.getResultSet();
					while (rs.next()) {
						count++;
						
						int newsid = rs.getInt("NewsItemId");
						newsIdList.add(newsid);
					}
					rs.close();
				}
			}

			primaryTagNewsMap = getPrimaryTagNews(newsIdList, ncid);
			
			query3 += ")";
			Statement stmt5 = getConnection().createStatement();
			stmt5.executeUpdate(query3);
			stmt5.close();

		} catch (Exception e) {
			logger.log(Level.INFO , " [ LhNewsItemHelper getAllNewsItemsForUserAndMode()   Could not fetch the newsitems]  ");
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[ LhNewsItemHelper getAllNewsItemsForUserAndMode() completed ]");
		long elapsed = System.currentTimeMillis() - start;
		logger.log(Level.INFO,"NEWS FETCHING TIME FOR AND MODE GROUP LIST OF ALERT  = " + elapsed + "ms");
		return primaryTagNewsMap;
	}

	private HashMap<TagItem, List<NewsItems>> getPrimaryTagNews(ArrayList<Integer> newsIdList, int ncid){
		logger.log(Level.INFO, "[ LhNewsItemHelper getPrimaryTagNews() initiated ]");
		NewsItemList newslist = null;
		NewsItems newsitem = null;
		PrimaryTagNewsMap primaryTagNewsMap = new PrimaryTagNewsMap();
		
		try{
			String newsQuery = "SELECT distinct n.NewsItemId,n.author,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source,n.isLocked,n.isReport from newsitem n" +
					" where n.NewsItemId in(select nti.newsitemid from newstagitem nti where nti.newsItemId in(0";
			for(Integer newsid : newsIdList){
				newsQuery += ","+newsid;
			}
			newsQuery+=	") and nti.tagitemid=";
			
			// fetch primary tags
			String primaryTagsQuery = "SELECT * FROM tagitem t where t.ParentId in(select ti.TagItemId from tagitem ti where ti.isPrimary = 1 and ti.IndustryId = "
					+ ncid + ")";
			Statement stmt1 = getConnection().createStatement();
			ResultSet rs1 = stmt1.executeQuery(primaryTagsQuery);
			while (rs1.next()) {
				newslist = new NewsItemList();
				int tagId = rs1.getInt("TagItemId");
				TagItem primaryTagItem = new TagItem();
				primaryTagItem.setOrderId(rs1.getInt("orderId"));
				primaryTagItem.setTagId(tagId);
				primaryTagItem.setTagName(rs1.getString("Name"));
				primaryTagItem.setParentId(rs1.getInt("ParentId"));
				
				String finalNewsQuery = newsQuery + tagId +")";
				
				Statement stmt2 = getConnection().createStatement();
				ResultSet rs2 = stmt2.executeQuery(finalNewsQuery);
				while(rs2.next()){
					newsitem = new NewsItems();
					
					int newsid = rs2.getInt("NewsItemId");
					newsitem.setNewsId(newsid);
	
					String newsTagsQuery = "SELECT tagitemId,name FROM tagitem where tagitemid in (SELECT TagItemId from newstagitem where NewsItemId="
							+ newsid + ")";
					Statement stmt5 = getConnection().createStatement();
					ResultSet rs5 = stmt5.executeQuery(newsTagsQuery);
					while (rs5.next()) {
						TagItem tagitem = new TagItem();
						tagitem.setTagName(rs5.getString(2));
						tagitem.setTagId(rs5.getInt(1));
						newsitem.addTagforNews(tagitem);
					}
	
					String newsTitle = new String(rs2.getString("Title").getBytes("utf-8"), "utf-8");
					newsitem.setNewsTitle(newsTitle);
					String newsAbstract = new String(rs2.getString("Abstract").getBytes("utf-8"), "utf-8");
					newsitem.setAbstractNews(newsAbstract);
					newsitem.setUrl(rs2.getString("URL"));
					newsitem.setNewsDate(rs2.getDate("ItemDate").toString());
					newsitem.setNewsSource(rs2.getString("Source"));
					newsitem.setIsLocked(rs2.getInt("isLocked"));
					newsitem.setAuthor(rs2.getString("author"));
					newsitem.setIsReportItem(rs2.getInt("isReport"));
					file = new File(folderimage + "//" + newsid + ".jpg");
	
					String imageurl = "imagefolder/"+newsid+".jpg";
					Blob blobim = (Blob) rs2.getBlob("NewsImages");
					if(blobim!=null){
						InputStream x = blobim.getBinaryStream();
						int size = x.available();
						if (size != 0) {
							newsitem.setImageUrl(imageurl);
							OutputStream out = new FileOutputStream(file);
							byte b[] = new byte[size];
							x.read(b);
							out.write(b);
						}
					}
					newslist.add(newsitem);
				}
				rs2.close();
				stmt2.close();
				if(newslist.size() > 0)
					primaryTagNewsMap.put(primaryTagItem, newslist);
			}
			rs1.close();
			stmt1.close();
			
		//	primaryTagNewsMap=sortHashMap(primaryTagNewsMap);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[ LhNewsItemHelper getPrimaryTagNews() completed ]");
		return primaryTagNewsMap;
	}
	
	public NewsItemList getGroupedNewsList(int newsmode, int userId, int industryId) {
		logger.log(Level.INFO, "[ LhNewsItemHelper getGroupedNewsList() initiated ]");
		// SELECT distinct
		// n.NewsItemId,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source
		// from newsitem n INNER JOIN newstagitem nt where n.NewsItemId =
		// nt.NewsItemId and nt.TagItemId in(0,266,893) and n.newsItemId
		// in(select nti.newsitemid from newstagitem nti where nti.tagitemid =
		// 4164)order by n.ItemDate desc
		HashMap<TagItem, ArrayList<NewsItems>> hashMap = new HashMap<TagItem, ArrayList<NewsItems>>();
		NewsItemList newsList;
		NewsItemList newsItemList = new NewsItemList();
		ArrayList<TagItem> selectedtaglist;
		NewsItems newsitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		GroupCategoryMap groupCategoryMap = getCurrentMap();
		if (groupCategoryMap.size() != 0) {
			try {
				if (newsmode == MainNewsPresenter.OR) {
					logger.log(Level.INFO, "[ LhNewsItemHelper getGroupedNewsList() OR mode ]");
					selectedtaglist = groupCategoryMap.getSelectedTags();
					Iterator<TagItem> iter = selectedtaglist.iterator();

					// create news fetching query
					String selectNewsQuery1 = "SELECT distinct n.NewsItemId,n.author,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source, n.isLocked from newsitem n INNER JOIN newstagitem nt where n.isReport = 0 and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
					while (iter.hasNext()) {
						TagItem tagItem = (TagItem) iter.next();
						int tagId = tagItem.getTagId();
						selectNewsQuery1 += "," + tagId;
					}
					selectNewsQuery1 += ") and n.newsItemId in(select nti.newsitemid from newstagitem nti where nti.tagitemid=";

					// fetch primary tags
					String primaryTagsQuery = "SELECT * FROM tagitem t where t.ParentId in(select ti.TagItemId from tagitem ti where ti.isPrimary = 1 and ti.IndustryId = "
							+ industryId + ")";
					Statement stmt1 = getConnection().createStatement();
					ResultSet primaryTagsRs = stmt1.executeQuery(primaryTagsQuery);
					while (primaryTagsRs.next()) {
						newsList = new NewsItemList();
						int tagId = primaryTagsRs.getInt("TagItemId");
						TagItem tagItem = new TagItem();
						tagItem.setTagId(tagId);
						tagItem.setTagName(primaryTagsRs.getString("Name"));
						tagItem.setParentId(primaryTagsRs.getInt("ParentId"));

						String selectNewsQuery2 = "" + tagId;
						String selectNewsQuery3 = ")order by n.ItemDate desc LIMIT 0,3";
						String selectNewsQuery = selectNewsQuery1
								+ selectNewsQuery2 + selectNewsQuery3;
						Statement stmt = getConnection().createStatement();
						ResultSet rs = stmt.executeQuery(selectNewsQuery);
						while (rs.next()) {
							newsitem = new NewsItems();
							int newsid = rs.getInt("NewsItemId");
							newsitem.setNewsId(newsid);

							// query to find the tags for a given newsid
							String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
									+ newsid;

							stmt = getConnection().createStatement();
							ResultSet rs1 = stmt.executeQuery(newsTagsQuery);

							// add tags to a newsitem
							while (rs1.next()) {
								for (Object obj : groupCategoryMap.keySet()) {
									// groupCategoryMap has categoryItems, if in
									// its tags there is a tag selected by user
									// ,then add it in associatedtaglist
									HashMap map = ((CategoryItem) groupCategoryMap
											.get(obj)).getItemMap();
									if (map.containsKey(rs1.getInt("TagItemId"))) {
										TagItem tagitem = (TagItem) map.get(rs1
												.getInt("TagItemId"));

										newsitem.addTagforNews(tagitem);
									}
								}
							}
							
							// fetch comment count
							String commentCountQuery = "select count(*) from itemcomment i where i.newsItemId = "
									+ newsid;
							ResultSet rs3 = stmt.executeQuery(commentCountQuery);
							while (rs3.next()) {
								newsitem.setCommentsCount(rs3.getInt(1));
							}
							
							// fetch views count
							// String viewCountQuery =
							// "SELECT NewscatalystItemCount FROM useritemaccessstats u where newsitemId="+newsid
							// ;
							String viewCountQuery = "SELECT sum(NewscatalystItemCount+NewsletterItemCount) as viewCount FROM useritemaccessstats where newsItemId = "
									+ newsid;
							ResultSet rs4 = stmt.executeQuery(viewCountQuery);
							while (rs4.next()) {
								newsitem.setViewsCount(rs4.getInt(1));
							}
							rs1.close();
							rs3.close();
							rs4.close();

							// populate the news item object
							String newsTitle = new String(rs.getString("Title")
									.getBytes("utf-8"), "utf-8");
							newsitem.setNewsTitle(newsTitle);
							String newsAbstract = new String(rs.getString(
									"Abstract").getBytes("utf-8"), "utf-8");
							newsitem.setAbstractNews(newsAbstract);
							newsitem.setUrl(rs.getString("URL"));
							newsitem.setAuthor(rs.getString("author"));
							newsitem.setNewsDate(rs.getDate("ItemDate")
									.toString());
							newsitem.setNewsSource(rs.getString("Source"));
							newsitem.setIsLocked(rs.getInt("isLocked"));
							file = new File(folderimage + "//" + newsid + ".jpg");
							String imageurl = "imagefolder/"+newsid+".jpg";
							if (rs.getBlob("NewsImages") != null) {
								Blob blobim = (Blob) rs.getBlob("NewsImages");
								InputStream x = blobim.getBinaryStream();
								int size = x.available();
								if (size != 0) {
									newsitem.setImageUrl(imageurl);
									OutputStream out = new FileOutputStream(
											file);
									byte b[] = new byte[size];
									x.read(b);
									out.write(b);
								}
							}
							newsList.add(newsitem);
						}
						if (newsList.size() > 0)
							hashMap.put(tagItem, newsList);
						rs.close();
						stmt.close();
					}
					primaryTagsRs.close();
					stmt1.close();
					if (hashMap.size() > 0)
						newsItemList.add(hashMap);
				} else if (newsmode == MainNewsPresenter.AND) {
					logger.log(Level.INFO, "[ LhNewsItemHelper getGroupedNewsList() AND mode ]");
					// newspage.setAndNews(true);
					selectedtaglist = groupCategoryMap
							.getSelectedTagsByCategory(); // the selected tagson
															// the client side
															// are in
															// selectedtaglist
					if (selectedtaglist.size() != 0) {
						CallableStatement proc = null;
						String csv = "";
						int csvPT = 0;
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
						csv = csv.substring(0, csv.length() - 1);
						String primaryTagsQuery = "SELECT * FROM tagitem t where t.ParentId in(select ti.TagItemId from tagitem ti where ti.isPrimary = 1 and ti.IndustryId = "
								+ industryId + ")";

						Statement stmt1 = getConnection().createStatement();
						ResultSet primaryTagsRs = stmt1.executeQuery(primaryTagsQuery);
						int j = 1;
						while (primaryTagsRs.next()) {

							newsList = new NewsItemList();
							int tagId = primaryTagsRs.getInt("TagItemId");
							TagItem tagItem = new TagItem();
							tagItem.setTagId(tagId);
							tagItem.setTagName(primaryTagsRs.getString("Name"));
							tagItem.setParentId(primaryTagsRs
									.getInt("ParentId"));

							ArrayList<TagItem> list = new ArrayList<TagItem>();
							list.add(tagItem);

							Iterator itt = list.iterator();
							while (itt.hasNext()) {
								int i = 0;
								TagItem tag = (TagItem) itt.next();
								int tagid = tag.getTagId();

								csvPT = tagid;
							}

							proc = getConnection()
									.prepareCall(
											"{ call SP_GETGROUPNEWS(?, ?, ?, ?, ?, ?) }");
							String tbname1 = "temp" + (j + 1) + "_" + userId;
							String tbname2 = "temp" + (j + 1000) + "_" + userId;
							proc.setString(1, csv);
							proc.setInt(2, csvPT);
							// proc.setInt(3, industryId);
							/* proc.setInt(3, criteria.getPageSize()); */
							proc.setInt(3, totalcategories);
							proc.registerOutParameter(4, Types.ARRAY);
							proc.setString(5, tbname1);
							proc.setString(6, tbname2);
							boolean isresultset = proc.execute();
							if (isresultset) {
								ResultSet rs = proc.getResultSet();
								while (rs.next()) {
									count++;
									newsitem = new NewsItems();
									int newsid = rs.getInt("NewsItemId");
									newsitem.setNewsId(newsid);

									String newsTagsQuery = "SELECT TagItemId from newstagitem where NewsItemId="
											+ newsid; // get the tag associated
														// the news
									Statement stmt = getConnection()
											.createStatement();
									ResultSet rs1 = stmt
											.executeQuery(newsTagsQuery);
									while (rs1.next()) {
										for (Object obj : groupCategoryMap
												.keySet()) {
											HashMap map = ((CategoryItem) groupCategoryMap.get(obj)).getItemMap(); // get tags of the category
											if (map.containsKey(rs1.getInt("TagItemId"))) {
												TagItem tagitem = (TagItem) map.get(rs1.getInt("TagItemId"));
												newsitem.addTagforNews(tagitem);
											}
										}
									}
									
									/* fetch comment count */
									String commentCountQuery = "select count(*) from itemcomment i where i.newsItemId = "
											+ newsid;
									ResultSet rs3 = stmt
											.executeQuery(commentCountQuery);
									while (rs3.next()) {
										newsitem.setCommentsCount(rs3.getInt(1));
									}

									// fetch views count
									// String viewCountQuery =
									// "SELECT NewscatalystItemCount FROM useritemaccessstats u where newsitemId="+newsid
									// ;
									String viewCountQuery = "SELECT sum(NewscatalystItemCount+NewsletterItemCount) as viewCount FROM useritemaccessstats where newsItemId = "
											+ newsid;
									ResultSet rs4 = stmt
											.executeQuery(viewCountQuery);
									while (rs4.next()) {
										newsitem.setViewsCount(rs4.getInt(1));
									}
									rs1.close();
									rs3.close();
									rs4.close();
									stmt.close();

									// populate news item
									String newsTitle = new String(rs.getString(
											"Title").getBytes("utf-8"), "utf-8");
									newsitem.setNewsTitle(newsTitle);
									String newsAbstract = new String(rs
											.getString("Abstract").getBytes(
													"utf-8"), "utf-8");
									newsitem.setAbstractNews(newsAbstract);
									newsitem.setUrl(rs.getString("URL"));
									newsitem.setAuthor(rs.getString("author"));
									newsitem.setNewsDate(rs.getDate("ItemDate")
											.toString());
									newsitem.setNewsSource(rs
											.getString("Source"));
									file = new File(folderimage + "//" + newsid
											+ ".jpg");
									String imageurl = "imagefolder/"+newsid+".jpg";
									Blob blobim = (Blob) rs
											.getBlob("NewsImages");
									if(blobim!=null){
										InputStream x = blobim.getBinaryStream();
										int size = x.available();
										if (size != 0) {
											newsitem.setImageUrl(imageurl);
											OutputStream out = new FileOutputStream(
													file);
											byte b[] = new byte[size];
											x.read(b);
											out.write(b);
										}
									}
									newsList.add(newsitem);
								}
								if (newsList.size() > 0)
									hashMap.put(tagItem, newsList);
								rs.close();
							}
							// hashMap.put(tagItem, newsList);
							if (hashMap.size() > 0)
								newsItemList.add(hashMap);

							j++;
						}
						primaryTagsRs.close();
						stmt1.close();
						proc.close();
					}

				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		logger.log(Level.INFO, "[ LhNewsItemHelper getGroupedNewsList() completed ]");
		return newsItemList;
	}
	
	public NewsItems getNewsItem(Integer newsId) throws SQLException{
		logger.log(Level.INFO, "[ LhNewsItemHelper getNewsItem() initiated]");
		
		NewsItems newsitem=null;
		try{
		String query="SELECT distinct n.Title,n.author,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source,n.isLocked from newsitem n where n.NewsItemId="+newsId;
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) { // 1

				newsitem = new NewsItems();
		//	int newsid = rs.getInt("NewsItemId");
			newsitem.setNewsId(newsId);

			String newsTagsQuery = "SELECT tagitemId,name FROM tagitem where tagitemid in (SELECT TagItemId from newstagitem where NewsItemId="
					+ newsId + ")";
			Statement stmt2 = getConnection().createStatement();
			ResultSet rs1 = stmt2.executeQuery(newsTagsQuery);
			while (rs1.next()) {
				TagItem tagitem = new TagItem();
				tagitem.setTagName(rs1.getString(2));
				tagitem.setTagId(rs1.getInt(1));
				newsitem.addTagforNews(tagitem);
			}

			stmt2.close();
			rs1.close();
			String newsTitle = new String(rs.getString("Title").getBytes(
					"utf-8"), "utf-8");
			newsitem.setNewsTitle(newsTitle);
			String newsAbstract = new String(rs.getString("Abstract")
					.getBytes("utf-8"), "utf-8");
			newsitem.setAbstractNews(newsAbstract);
			newsitem.setUrl(rs.getString("URL"));
			newsitem.setNewsDate(rs.getDate("ItemDate").toString());
			newsitem.setAuthor(rs.getString("author"));
			newsitem.setNewsSource(rs.getString("Source"));
			newsitem.setIsLocked(rs.getInt("isLocked"));
			Blob blobim = (Blob) rs.getBlob("NewsImages");
			if(blobim!=null){
				InputStream x = blobim.getBinaryStream();
				int size = x.available();
				if (size != 0) {
					newsitem.setImageUrl("-");
				}
			}
		}
		rs.close();
		stmt.close();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		logger.log(Level.INFO, "[ LhNewsItemHelper getNewsItem() completed]");
		return newsitem;
	}
	
	public UserInformation getUser(Integer UserId){
		logger.log(Level.INFO, "[ LhNewsItemHelper getUser() initiated]");
		UserInformation user=null;
		try{
			String query="SELECT * FROM `user` where userid="+UserId;
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) { 
				user=new UserInformation();
				user.setFirstname(rs.getString("FirstName"));
				user.setLastname(rs.getString("LastName"));
				user.setApproved(Integer.parseInt(rs.getString("isApproved")));
				user.setUserSelectedIndustryID(Integer.parseInt(rs.getString("IndustryEnumId")));
				user.setEmail(rs.getString("email"));
			}
			rs.close();
			stmt.close();
		}catch (Exception e) {
			logger.log(Level.INFO, "[ LhNewsItemHelper getUser() EXCEPTION!!!!]");
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[ LhNewsItemHelper getUser() completed]");
		return user;
	}
	
	public PrimaryTagNewsMap sortHashMap(HashMap<TagItem, List<NewsItems>> input){
		  
		  // not yet sorted
	    List<TagItem> sortedByName = new ArrayList<TagItem>(input.keySet());

	    Collections.sort(sortedByName, new Comparator<TagItem>() {

	        public int compare(TagItem o1, TagItem o2) {
	       
	        int i1=o1.getOrderId();
            int i2=o2.getOrderId();
              
           //   if(i1==-1 || i2==-1)
           //   	return 1;
              if(i1==-1 && i2 >-1)
	                	return 1;
              if(i2==-1 && i1 >-1)
              	return -1;
               if(i1<i2)
              	 return -1;
               else if (i1==i2)
              	 return 0;
               else 
              	 return 1;
	        }
	    });
	    
	    PrimaryTagNewsMap sortedMap = new PrimaryTagNewsMap();
	    
	    
	    for (int i=0; i<sortedByName.size(); i++){
	    	
	    	sortedMap.put(sortedByName.get(i),input.get(sortedByName.get(i)));
	        
	    }
		return sortedMap;
	
	   
	}
}