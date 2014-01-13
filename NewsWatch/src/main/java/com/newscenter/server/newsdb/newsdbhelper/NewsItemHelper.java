package com.newscenter.server.newsdb.newsdbhelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletConfig;

import com.lighthouse.newsletter.client.domain.UserNewsletterAlertConfig;
import com.lighthouse.newsletter.server.LhNewsletterHelper;
import com.login.client.UserInformation;
import com.mysql.jdbc.Blob;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.TagItem;
import com.newscenter.client.ui.MainNewsPresenter;
import com.newscenter.server.categorydb.ItemProviderServiceImpl;
import com.newscenter.server.db.DBHelper;

public class NewsItemHelper extends DBHelper {

	private CategoryMap currentMap;
	protected File file;
	static protected String folderurl;
	protected File folderimage;

	// private PageCriteria pageCriteria;

	/**
	 * 
	 * @param categoryMap
	 *            - current categorymap for the logged in user
	 */
	public NewsItemHelper(CategoryMap categoryMap, String tomcatpath) {
		super();
		setCurrentMap(categoryMap);
		setFolderurl(tomcatpath + "/imagefolder");
		//setFolderurl("imagefolder");
		setFolderimage(new File(getFolderurl()));
	}

	
	/**
	 * This method gets the newsitems based on the tags that have been selected
	 * in the categoryMap
	 * 
	 * @return an arraylist of newsitems
	 */
	public NewsItemList getMatchingNewsItems() {
		NewsItemList newslist = new NewsItemList();
		ArrayList selectedtaglist;
		NewsItems newsitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		CategoryMap currentmap = getCurrentMap();

		if (currentmap.size() != 0) {
			try {
				selectedtaglist = currentmap.getSelectedTags();
				Iterator iter = selectedtaglist.iterator();
				String query = "SELECT distinct n.NewsItemId,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source from newsitem n INNER JOIN newstagitem nt where n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
				while (iter.hasNext()) {
					TagItem tag = (TagItem) iter.next();
					int tagid = tag.getTagId();
					query += "," + tagid;
				}
				query += ")order by n.ItemDate desc";
				Statement stmt = getConnection().createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {

					newsitem = new NewsItems();
					int newsid = rs.getInt("NewsItemId");
					newsitem.setNewsId(newsid);

					String query1 = "SELECT TagItemId from newstagitem where NewsItemId="
							+ newsid;
					stmt = getConnection().createStatement();
					ResultSet rs1 = stmt.executeQuery(query1);
					while (rs1.next()) {
						for (Object obj : currentmap.keySet()) {
							HashMap map = ((CategoryItem) currentmap.get(obj))
									.getItemMap();
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
					String newsAbstract = new String(rs.getString("Abstract")
							.getBytes("utf-8"), "utf-8");
					newsitem.setAbstractNews(newsAbstract);
					newsitem.setUrl(rs.getString("URL"));
					newsitem.setNewsDate(rs.getDate("ItemDate").toString());
					newsitem.setNewsSource(rs.getString("Source"));
					file = new File(folderimage + "//" + newsid + ".jpg");
					String imageurlTomcatFolder = "imagefolder";
					// String imageurl = folderurl+"\\"+newsid+".jpg";
					String imageurl = imageurlTomcatFolder + "/" + newsid
							+ ".jpg";

					Blob blobim = (Blob) rs.getBlob("NewsImages");
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
					if (size != 0) {
						newsitem.setImageUrl(imageurl);
						OutputStream out = new FileOutputStream(file);
						byte b[] = new byte[size];
						x.read(b);
						out.write(b);
					}
					newslist.add(newsitem);
				}
				rs.close();
				stmt.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return newslist;
	}

	/**
	 * This method gets a page of news based on the criteria i.e start index and
	 * pagesize and the mode of news filtering chosen by the user. If the mode
	 * is OR- all news containing the selected tags are fetched. If the mode is
	 * AND - news containing any of the selected tags of a category and a
	 * selected tag of different categories
	 * 
	 * @return a page i.e a newslist of the no. of news items specified by the
	 *         pagesize.
	 */
	public NewsItemList getPage(PageCriteria criteria, int newsmode, int userid) {

		NewsItemList newspage = new NewsItemList();
		ArrayList selectedtaglist;
		NewsItems newsitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		CategoryMap currentmap = getCurrentMap();

		if (currentmap.size() != 0) {
			try {
				if (newsmode == MainNewsPresenter.OR) {
					// newspage.setAndNews(false);
					selectedtaglist = currentmap.getSelectedTags();
					Iterator iter = selectedtaglist.iterator();
					String query2 = "SELECT count(distinct n.NewsItemId) from newsitem n INNER JOIN newstagitem nt where n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
					String query = "SELECT distinct n.NewsItemId,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source from newsitem n INNER JOIN newstagitem nt where n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
					while (iter.hasNext()) {
						TagItem tag = (TagItem) iter.next();
						int tagid = tag.getTagId();
						query += "," + tagid;
						query2 += "," + tagid;
					}
					query += ")order by n.ItemDate desc LIMIT "
							+ criteria.getStartIndex() + ","
							+ criteria.getPageSize();
					query2 += ")order by n.ItemDate desc";

					Statement stmt2 = getConnection().createStatement();
					ResultSet rs2 = stmt2.executeQuery(query2);
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
					ResultSet rs = stmt.executeQuery(query);
					while (rs.next()) {

						newsitem = new NewsItems();
						int newsid = rs.getInt("NewsItemId");
						newsitem.setNewsId(newsid);

						String query1 = "SELECT TagItemId from newstagitem where NewsItemId="+ newsid;

						stmt = getConnection().createStatement();
						ResultSet rs1 = stmt.executeQuery(query1);

						while (rs1.next()) {
							for (Object obj : currentmap.keySet()) {
								HashMap map = ((CategoryItem) currentmap.get(obj)).getItemMap();// take the related tags of the current category
								if (map.containsKey(rs1.getInt("TagItemId"))) {
									TagItem tagitem = (TagItem) map.get(rs1.getInt("TagItemId"));
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
						newsitem.setNewsSource(rs.getString("Source"));
						file = new File(folderimage + "//" + newsid + ".jpg");
						String imageurlTomcatFolder = "imagefolder";
						// String imageurl = folderurl+"\\"+newsid+".jpg";
						String imageurl = imageurlTomcatFolder + "/" + newsid
								+ ".jpg";

						if (rs.getBlob("NewsImages") != null) {
							Blob blobim = (Blob) rs.getBlob("NewsImages");
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
				} else if (newsmode == MainNewsPresenter.AND) {
					// newspage.setAndNews(true);
					selectedtaglist = currentmap.getSelectedTagsByCategory();
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

								String query1 = "SELECT TagItemId from newstagitem where NewsItemId="
										+ newsid;
								Statement stmt = getConnection()
										.createStatement();
								ResultSet rs1 = stmt.executeQuery(query1);
								while (rs1.next()) {
									for (Object obj : currentmap.keySet()) {
										HashMap map = ((CategoryItem) currentmap.get(obj)).getItemMap();
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
								String imageurlTomcatFolder = "imagefolder";
								// String imageurl =
								// folderurl+"\\"+newsid+".jpg";
								String imageurl = imageurlTomcatFolder + "/"
										+ newsid + ".jpg";

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
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		newspage.setTagNews(false);
		return newspage;
	}

	public NewsItemList getAndPage(PageCriteria criteria, int newsmode,
			int userid) {
		NewsItemList newspage = new NewsItemList();
		ArrayList selectedtaglist;
		NewsItems newsitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		CategoryMap currentmap = getCurrentMap();

		if (currentmap.size() != 0) {
			try {
				if (newsmode == MainNewsPresenter.OR) {
					selectedtaglist = currentmap.getSelectedTags();
					Iterator iter = selectedtaglist.iterator();
					String query2 = "SELECT count(distinct n.NewsItemId) from newsitem n INNER JOIN newstagitem nt where n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
					String query = "SELECT distinct n.NewsItemId,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source from newsitem n INNER JOIN newstagitem nt where n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
					while (iter.hasNext()) {
						TagItem tag = (TagItem) iter.next();
						int tagid = tag.getTagId();
						query += "," + tagid;
						query2 += "," + tagid;
					}
					query += ")order by n.ItemDate desc LIMIT "
							+ criteria.getStartIndex() + ","
							+ criteria.getPageSize();
					query2 += ")order by n.ItemDate desc";

					Statement stmt2 = getConnection().createStatement();
					ResultSet rs2 = stmt2.executeQuery(query2);
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
					ResultSet rs = stmt.executeQuery(query);
					while (rs.next()) {

						newsitem = new NewsItems();
						int newsid = rs.getInt("NewsItemId");
						newsitem.setNewsId(newsid);

						String query1 = "SELECT TagItemId from newstagitem where NewsItemId="
								+ newsid;
						stmt = getConnection().createStatement();
						ResultSet rs1 = stmt.executeQuery(query1);
						while (rs1.next()) {
							for (Object obj : currentmap.keySet()) {
								HashMap map = ((CategoryItem) currentmap
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
						newsitem.setNewsSource(rs.getString("Source"));
						file = new File(folderimage + "//" + newsid + ".jpg");
						String imageurlTomcatFolder = "imagefolder";
						// String imageurl = folderurl+"\\"+newsid+".jpg";
						String imageurl = imageurlTomcatFolder + "/" + newsid
								+ ".jpg";

						Blob blobim = (Blob) rs.getBlob("NewsImages");
						InputStream x = blobim.getBinaryStream();
						int size = x.available();
						if (size != 0) {
							newsitem.setImageUrl(imageurl);
							OutputStream out = new FileOutputStream(file);
							byte b[] = new byte[size];
							x.read(b);
							out.write(b);
						}
						newspage.add(newsitem);
					}
					rs.close();
					stmt.close();
					newspage.setAndNews(false);
				} else if (newsmode == MainNewsPresenter.AND) {
					selectedtaglist = currentmap.getSelectedTagsByCategory();
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

								String query1 = "SELECT TagItemId from newstagitem where NewsItemId="
										+ newsid;
								Statement stmt = getConnection()
										.createStatement();
								ResultSet rs1 = stmt.executeQuery(query1);
								while (rs1.next()) {
									for (Object obj : currentmap.keySet()) {
										HashMap map = ((CategoryItem) currentmap
												.get(obj)).getItemMap();
										if (map.containsKey(rs1
												.getInt("TagItemId"))) {
											TagItem tagitem = (TagItem) map
													.get(rs1.getInt("TagItemId"));
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
								String imageurlTomcatFolder = "imagefolder";
								// String imageurl =
								// folderurl+"\\"+newsid+".jpg";
								String imageurl = imageurlTomcatFolder + "/"
										+ newsid + ".jpg";

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
	public NewsItemList getAllNewsforTag(TagItem tagitem, PageCriteria criteria) {
		NewsItemList tagnews = new NewsItemList();
		NewsItems newsitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		CategoryMap currentmap = getCurrentMap();
		try {
			int tagid = tagitem.getTagId();
			String query2 = "SELECT count(distinct n.NewsItemId) from newsitem n INNER JOIN newstagitem nt where n.NewsItemId = nt.NewsItemId and nt.TagItemId="
					+ tagid + " order by n.ItemDate desc";
			String query = "SELECT distinct n.NewsItemId,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source from newsitem n INNER JOIN newstagitem nt where n.NewsItemId = nt.NewsItemId and nt.TagItemId="
					+ tagid
					+ " order by n.ItemDate desc LIMIT "
					+ criteria.getStartIndex() + "," + criteria.getPageSize();

			Statement stmt2 = getConnection().createStatement();
			ResultSet rs2 = stmt2.executeQuery(query2);
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
			ResultSet rs = stmt.executeQuery(query);

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
				newsitem.setNewsSource(rs.getString("Source"));
				String query1 = "SELECT TagItemId from newstagitem where NewsItemId="
						+ newsid;
				stmt = getConnection().createStatement();
				ResultSet rs1 = stmt.executeQuery(query1);
				while (rs1.next()) {
					for (Object obj : currentmap.keySet()) {
						HashMap map = ((CategoryItem) currentmap.get(obj))
								.getItemMap();
						if (map.containsKey(rs1.getInt("TagItemId"))) {
							TagItem tag = (TagItem) map.get(rs1
									.getInt("TagItemId"));
							newsitem.addTagforNews(tag);
						}
					}

				}

				rs1.close();
				file = new File(folderimage + "//" + newsid + ".jpg");
				String imageurlTomcatFolder = "imagefolder";
				// String imageurl = folderurl+"\\"+newsid+".jpg";
				String imageurl = imageurlTomcatFolder + "/" + newsid + ".jpg";

				Blob blobim = (Blob) rs.getBlob("NewsImages");
				InputStream x = blobim.getBinaryStream();
				int size = x.available();
				if (size != 0) {
					newsitem.setImageUrl(imageurl);
					OutputStream out = new FileOutputStream(file);
					byte b[] = new byte[size];
					x.read(b);
					out.write(b);
				}

				tagnews.add(newsitem);
			}
			rs.close();
			stmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		tagnews.setTagNews(true);
		return tagnews;
	}

	/**
	 * This method fetches all the newsitems for those tags in the categoryitem
	 * which are selected and returns an arraylist of newsitems
	 * 
	 * @param categoryItem
	 *            - the category for whose tagitems the newsitems need to be
	 *            fetched
	 * @return an arraylist of newsitems for the selected tags of the specified
	 *         categoryitem
	 */
	public NewsItemList getAllNewsForCategory(CategoryItem categoryItem,
			PageCriteria criteria) {
		NewsItemList categorynews = new NewsItemList();
		CategoryMap currentmap = getCurrentMap();
		NewsItems newsitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		HashMap map = categoryItem.getItemMap();
		String query2 = "SELECT count(distinct n.NewsItemId) from newsitem n INNER JOIN newstagitem nt where n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
		String query = "SELECT distinct n.NewsItemId,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source from newsitem n INNER JOIN newstagitem nt where n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
		for (Object obj : map.keySet()) {
			TagItem tagitem = (TagItem) map.get(obj);
			if (tagitem.isSelected()) {
				int tagid = tagitem.getTagId();
				query += "," + tagid;
				query2 += "," + tagid;
			}
		}
		query += ")order by n.ItemDate desc LIMIT " + criteria.getStartIndex()
				+ "," + criteria.getPageSize();
		query2 += ")";
		try {
			Statement stmt2 = getConnection().createStatement();
			ResultSet rs2 = stmt2.executeQuery(query2);
			while (rs2.next()) {
				int numitems = rs2.getInt(1);
				categorynews.setNumItems(numitems);
				int pages = 0;
				if (numitems % criteria.getPageSize() == 0)
					pages = numitems / criteria.getPageSize();
				else
					pages = (numitems / criteria.getPageSize() + 1);
				categorynews.setTotalPages(pages);
			}
			rs2.close();
			stmt2.close();

			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				newsitem = new NewsItems();
				int newsid = rs.getInt("NewsItemId");
				newsitem.setNewsId(newsid);

				String query1 = "SELECT TagItemId from newstagitem where NewsItemId="
						+ newsid;
				stmt = getConnection().createStatement();
				ResultSet rs1 = stmt.executeQuery(query1);
				while (rs1.next()) {
					for (Object obj : currentmap.keySet()) {
						HashMap hashmap = ((CategoryItem) currentmap.get(obj))
								.getItemMap();
						if (hashmap.containsKey(rs1.getInt("TagItemId"))) {
							TagItem tagitem = (TagItem) hashmap.get(rs1
									.getInt("TagItemId"));
							newsitem.addTagforNews(tagitem);
						}
					}
				}

				rs1.close();
				String newsTitle = new String(rs.getString("Title").getBytes(
						"utf-8"), "utf-8");
				newsitem.setNewsTitle(newsTitle);
				String newsAbstract = new String(rs.getString("Abstract")
						.getBytes("utf-8"), "utf-8");
				newsitem.setAbstractNews(newsAbstract);

				newsitem.setUrl(rs.getString("URL"));
				newsitem.setNewsDate(rs.getDate("ItemDate").toString());
				newsitem.setNewsSource(rs.getString("Source"));

				file = new File(folderimage + "//" + newsid + ".jpg");
				String imageurlTomcatFolder = "imagefolder";
				// String imageurl = folderurl+"\\"+newsid+".jpg";
				String imageurl = imageurlTomcatFolder + "/" + newsid + ".jpg";

				Blob blobim = (Blob) rs.getBlob("NewsImages");
				InputStream x = blobim.getBinaryStream();
				int size = x.available();
				if (size != 0) {
					newsitem.setImageUrl(imageurl);
					OutputStream out = new FileOutputStream(file);
					byte b[] = new byte[size];
					x.read(b);
					out.write(b);
				}
				categorynews.add(newsitem);
			}
			rs.close();
			stmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		categorynews.setTagNews(false);
		return categorynews;
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
	public NewsItemList getAllNewsItemsForUser(int userid, int filtermode,
			Timestamp datetime) {
		NewsItemList newslist = new NewsItemList();
		ArrayList selectedtaglist;
		NewsItems newsitem;
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		CategoryMap currentmap = getCurrentMap();

		if (currentmap.size() != 0) {
			try {

				selectedtaglist = currentmap.getSelectedTags();
				if (selectedtaglist.isEmpty())
					newslist.setNoTagSelected(true);
				else {
					if (filtermode == 0) {
						Iterator iter = selectedtaglist.iterator();
						String query = "SELECT distinct n.NewsItemId,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source from newsitem n INNER JOIN newstagitem nt where n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 3 DAY) and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
						String query2 = "SELECT distinct n.NewsItemId,n.Title,n.Abstract,n.URL,n.ItemDate,n.NewsImages,n.Source from newsitem n INNER JOIN newstagitem nt where n.ItemDate >= DATE_SUB(CURDATE(),INTERVAL 3 DAY) and n.UploadedAt >= '"
								+ datetime
								+ "' and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
						String query3 = "update usertagselection set TagSelectionStatus = 0 where UserId = "
								+ userid + " and TagItemId in(0";
						while (iter.hasNext()) {
							TagItem tag = (TagItem) iter.next();
							int tagid = tag.getTagId();
							if (tag.isTagSelectionStatus() == true) {
								query += "," + tagid;
								query3 += "," + tagid;
								tag.setTagSelectionStatus(false);
							} else
								query2 += "," + tagid;
						}
						query += ")order by n.ItemDate desc";
						query2 += ")order by n.ItemDate desc";
						query3 += ")";

						Statement stmt = getConnection().createStatement();
						ResultSet rs = stmt.executeQuery(query);
						while (rs.next()) {

							newsitem = new NewsItems();
							int newsid = rs.getInt("NewsItemId");
							newsitem.setNewsId(newsid);

							String query1 = "SELECT TagItemId from newstagitem where NewsItemId="
									+ newsid;
							Statement stmt2 = getConnection().createStatement();
							ResultSet rs1 = stmt2.executeQuery(query1);
							while (rs1.next()) {
								for (Object obj : currentmap.keySet()) {
									HashMap map = ((CategoryItem) currentmap
											.get(obj)).getItemMap();
									if (map.containsKey(rs1.getInt("TagItemId"))) {
										TagItem tagitem = (TagItem) map.get(rs1
												.getInt("TagItemId"));
										newsitem.addTagforNews(tagitem);
									}
								}

							}

							rs1.close();
							stmt2.close();
							String newsTitle = new String(rs.getString("Title")
									.getBytes("utf-8"), "utf-8");
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
							String imageurlTomcatFolder = "imagefolder";
							// String imageurl = folderurl+"\\"+newsid+".jpg";
							String imageurl = imageurlTomcatFolder + "/"
									+ newsid + ".jpg";

							Blob blobim = (Blob) rs.getBlob("NewsImages");
							InputStream x = blobim.getBinaryStream();
							int size = x.available();
							if (size != 0) {
								newsitem.setImageUrl(imageurl);
								OutputStream out = new FileOutputStream(file);
								byte b[] = new byte[size];
								x.read(b);
								out.write(b);
							}
							newslist.add(newsitem);
						}
						rs.close();
						stmt.close();

						Statement stmt3 = getConnection().createStatement();
						ResultSet rs2 = stmt3.executeQuery(query2);
						while (rs2.next()) {
							newsitem = new NewsItems();
							int newsid = rs2.getInt("NewsItemId");
							newsitem.setNewsId(newsid);

							String query1 = "SELECT TagItemId from newstagitem where NewsItemId="
									+ newsid;
							Statement stmt4 = getConnection().createStatement();
							ResultSet rs1 = stmt4.executeQuery(query1);
							while (rs1.next()) {
								for (Object obj : currentmap.keySet()) {
									HashMap map = ((CategoryItem) currentmap
											.get(obj)).getItemMap();
									if (map.containsKey(rs1.getInt("TagItemId"))) {
										TagItem tagitem = (TagItem) map.get(rs1
												.getInt("TagItemId"));
										newsitem.addTagforNews(tagitem);
									}
								}
							}
							rs1.close();
							stmt4.close();
							String newsTitle = new String(rs2
									.getString("Title").getBytes("utf-8"),
									"utf-8");
							newsitem.setNewsTitle(newsTitle);
							String newsAbstract = new String(rs2.getString(
									"Abstract").getBytes("utf-8"), "utf-8");
							newsitem.setAbstractNews(newsAbstract);
							newsitem.setUrl(rs2.getString("URL"));
							newsitem.setNewsDate(rs2.getDate("ItemDate")
									.toString());
							newsitem.setNewsSource(rs2.getString("Source"));
							file = new File(folderimage + "//" + newsid
									+ ".jpg");
							String imageurlTomcatFolder = "imagefolder";
							// String imageurl = folderurl+"\\"+newsid+".jpg";
							String imageurl = imageurlTomcatFolder + "/"
									+ newsid + ".jpg";

							Blob blobim = (Blob) rs2.getBlob("NewsImages");
							InputStream x = blobim.getBinaryStream();
							int size = x.available();
							if (size != 0) {
								newsitem.setImageUrl(imageurl);
								OutputStream out = new FileOutputStream(file);
								byte b[] = new byte[size];
								x.read(b);
								out.write(b);
							}
							newslist.add(newsitem);
						}
						rs2.close();
						stmt3.close();
						Statement stmt5 = getConnection().createStatement();
						stmt5.executeUpdate(query3);
						stmt5.close();
					} else if (filtermode == 1) {
						CallableStatement proc = null;
						String csv1 = "";
						String csv2 = "";
						int totalcategoriescsv1 = 0;
						int totalcategoriescsv2 = 0;
						boolean flagcsv1 = false;
						boolean flagcsv2 = false;
						int count = 0;
						String query3 = "update usertagselection set TagSelectionStatus = 0 where UserId = "
								+ userid + " and TagItemId in(0";
						selectedtaglist = currentmap
								.getSelectedTagsByCategory();
						Iterator iter = selectedtaglist.iterator();
						while (iter.hasNext()) {

							int i = 0;
							ArrayList list = (ArrayList) iter.next();
							Iterator itt = list.iterator();
							while (itt.hasNext()) {
								TagItem tag = (TagItem) itt.next();
								int tagid = tag.getTagId();
								if (tag.isTagSelectionStatus() == true) {
									// totalcategoriescsv1++;
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
									// totalcategoriescsv2++;
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
									newsitem = new NewsItems();
									int newsid = rs.getInt("NewsItemId");
									newsitem.setNewsId(newsid);

									String query1 = "SELECT TagItemId from newstagitem where NewsItemId="
											+ newsid;
									Statement stmt = getConnection()
											.createStatement();
									ResultSet rs1 = stmt.executeQuery(query1);
									while (rs1.next()) {
										for (Object obj : currentmap.keySet()) {
											HashMap map = ((CategoryItem) currentmap
													.get(obj)).getItemMap();
											if (map.containsKey(rs1
													.getInt("TagItemId"))) {
												TagItem tagitem = (TagItem) map
														.get(rs1.getInt("TagItemId"));
												newsitem.addTagforNews(tagitem);
											}
										}
									}

									rs1.close();
									String newsTitle = new String(rs.getString(
											"Title").getBytes("utf-8"), "utf-8");
									newsitem.setNewsTitle(newsTitle);
									String newsAbstract = new String(rs
											.getString("Abstract").getBytes(
													"utf-8"), "utf-8");
									newsitem.setAbstractNews(newsAbstract);
									newsitem.setUrl(rs.getString("URL"));
									newsitem.setNewsDate(rs.getDate("ItemDate")
											.toString());
									newsitem.setNewsSource(rs
											.getString("Source"));
									file = new File(folderimage + "//" + newsid
											+ ".jpg");
									String imageurlTomcatFolder = "imagefolder";
									// String imageurl =
									// folderurl+"\\"+newsid+".jpg";
									String imageurl = imageurlTomcatFolder
											+ "/" + newsid + ".jpg";

									Blob blobim = (Blob) rs
											.getBlob("NewsImages");
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
									newslist.add(newsitem);
								}
								rs.close();
							}
						}

						if (csv2.length() != 0) {
							csv2 = csv2.substring(0, csv2.length() - 1);
							proc = getConnection()
									.prepareCall(
											"{ call SP_GETANDREGULARNEWS(?, ?, ?, ?, ?, ?) }");
							String tbname1 = "temp1_" + userid;
							String tbname2 = "temp2_" + userid;
							proc.setString(1, csv2);
							proc.setInt(2, totalcategoriescsv2);
							proc.registerOutParameter(3, Types.INTEGER);
							proc.setString(4, tbname1);
							proc.setString(5, tbname2);
							proc.setTimestamp(6, datetime);
							boolean isresultset = proc.execute();
							if (isresultset) {
								ResultSet rs = proc.getResultSet();
								while (rs.next()) {
									count++;
									newsitem = new NewsItems();
									int newsid = rs.getInt("NewsItemId");
									newsitem.setNewsId(newsid);

									String query1 = "SELECT TagItemId from newstagitem where NewsItemId="
											+ newsid;
									Statement stmt = getConnection()
											.createStatement();
									ResultSet rs1 = stmt.executeQuery(query1);
									while (rs1.next()) {
										for (Object obj : currentmap.keySet()) {
											HashMap map = ((CategoryItem) currentmap
													.get(obj)).getItemMap();
											if (map.containsKey(rs1
													.getInt("TagItemId"))) {
												TagItem tagitem = (TagItem) map
														.get(rs1.getInt("TagItemId"));
												newsitem.addTagforNews(tagitem);
											}
										}
									}

									rs1.close();
									String newsTitle = new String(rs.getString(
											"Title").getBytes("utf-8"), "utf-8");
									newsitem.setNewsTitle(newsTitle);
									String newsAbstract = new String(rs
											.getString("Abstract").getBytes(
													"utf-8"), "utf-8");
									newsitem.setAbstractNews(newsAbstract);
									newsitem.setUrl(rs.getString("URL"));
									newsitem.setNewsDate(rs.getDate("ItemDate")
											.toString());
									newsitem.setNewsSource(rs
											.getString("Source"));
									file = new File(folderimage + "//" + newsid
											+ ".jpg");
									String imageurlTomcatFolder = "imagefolder";
									// String imageurl =
									// folderurl+"\\"+newsid+".jpg";
									String imageurl = imageurlTomcatFolder
											+ "/" + newsid + ".jpg";

									Blob blobim = (Blob) rs
											.getBlob("NewsImages");
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
									newslist.add(newsitem);
								}
								rs.close();
							}
						}

						query3 += ")";
						Statement stmt5 = getConnection().createStatement();
						stmt5.executeUpdate(query3);
						stmt5.close();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return newslist;
	}

	public ArrayList getadmininformation(int userid, String email) {
		ArrayList array = new ArrayList();
		UserInformation user = new UserInformation();
		try {
			String query = "SELECT u.UserId,u.isAdmin, u.FirstName, u.LastName, u.email, us.Period, us.NewsFilterMode from user u, usersubscription us where u.UserId = us.UserId and u.email= '"
					+ email + "'";
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				user.setUserId(rs.getInt("UserId"));
				user.setIsAdmin(rs.getInt("isAdmin"));
				user.setFirstname(rs.getString("FirstName"));
				user.setLastname(rs.getString("LastName"));
				user.setEmail(rs.getString("email"));
				user.setPeriod(rs.getInt("Period"));
				user.setNewsFilterMode(rs.getInt("NewsFilterMode"));
				array.add(user);
			}
			rs.close();
			stmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return array;
	}

	/**
	 * This method saves the user preferences for the newsletter i.e either
	 * daily or weekly newsletter.
	 * 
	 * @param choice
	 *            - daily or weekly
	 * @param userid
	 *            - id of the user
	 * @param ncid
	 *            - the newscenter id for which the newsletter is subscribed
	 * @return true if the choice was properly saved otherwise false
	 */
	public boolean saveNewsletterPreference(String choice, int userid, int ncid) {
		try {
			String query = "";
			if (choice.equals("Daily"))
				query = "update usersubscription set isSubscribed = 1,Period = 1,SubscriptionDate = CURDATE() where UserId ="
						+ userid + " and NewsCenterId = " + ncid;
			else if (choice.equals("Weekly"))
				query = "update usersubscription set isSubscribed = 1,Period = 2,SubscriptionDate = CURDATE() where UserId ="
						+ userid + " and NewsCenterId = " + ncid;
			else if (choice.equals("unsubscribe"))
				query = "update usersubscription set isSubscribed = 0,Period = NULL,SubscriptionDate = NULL where UserId ="
						+ userid + " and NewsCenterId = " + ncid;

			Statement stmt = getConnection().createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}


	
	/**
	 * This method is used to save the user's choice of viewing news items i.e
	 * the type of news filtering chosen by the user - OR / AND
	 * 
	 * @param choice
	 *            - OR/AND
	 * @param userid
	 *            - id of the logged in user
	 * @param ncid
	 *            - newscenter into which the user has logged in
	 * @return true if the choice is saved properly otherwise false
	 */
	public int saveNewsFilterModePreference(String choice, int userid, int ncid) {
		int result = 0;
		try {
			String query = "";
			if (choice.equals("OR"))
				query = "update usersubscription set NewsFilterMode = 0 where UserId ="
						+ userid + " and NewsCenterId = " + ncid;
			else if (choice.equals("AND"))
				query = "update usersubscription set NewsFilterMode = 1 where UserId ="
						+ userid + " and NewsCenterId = " + ncid;

			Statement stmt = getConnection().createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
		if (choice.equals("OR"))
			result = 1;
		else if (choice.equals("AND"))
			result = 2;
		return result;
	}

	public CategoryMap getCurrentMap() {
		return currentMap;
	}

	public void setCurrentMap(CategoryMap currentMap) {
		this.currentMap = currentMap;
	}

	public String getFolderurl() {
		return folderurl;
	}

	public void setFolderurl(String folderurl) {
		this.folderurl = folderurl;
	}

	public File getFolderimage() {
		return folderimage;
	}

	public void setFolderimage(File folderimage) {
		this.folderimage = folderimage;
	}

}
