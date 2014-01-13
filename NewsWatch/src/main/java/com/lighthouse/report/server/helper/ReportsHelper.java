package com.lighthouse.report.server.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.admin.client.TagItemInformation;
import com.lighthouse.admin.client.AdminReportItemList;
import com.lighthouse.admin.client.domain.AdminReportItem;

import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.server.helper.GroupHelper;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.server.helper.LhNewsItemHelper;
import com.lighthouse.report.client.domain.ReportItem;
import com.lighthouse.report.client.domain.ReportItemList;
import com.login.client.UserInformation;


import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.TagItem;
import com.newscenter.client.ui.MainNewsPresenter;
import com.newscenter.server.categorydb.CategoryHelper;
import com.newscenter.server.db.DBHelper;
import com.newscenter.server.exception.CategoryHelperException;

public class ReportsHelper extends DBHelper {

	public static boolean flag = true;
	Logger logger = Logger.getLogger(ReportsHelper.class.getName());
	int tagItemId, newsItemId;

	@Deprecated
	public ReportItemList getAllReportItem() {
		ReportItemList reportItemList = new ReportItemList();

		try {
			Connection connection = (Connection) getConnection();
			String sql = "SELECT NewsItemId,Content,Title,Abstract,URL,ItemDate,UploadedAt,NewsImages,Source,reportMimeType,reportLink,reportLifeSpan,isReport FROM newsitem where isReport=1 and CURRENT_DATE <= reportLifeSpan";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ReportItem reportItem = new ReportItem();
				reportItem.setNewsId(rs.getInt("NewsItemId"));
				reportItem.setNewsContent(rs.getString("Content"));
				reportItem.setNewsTitle(rs.getString("Title"));
				reportItem.setAbstractNews(rs.getString("Abstract"));
				reportItem.setUrl(rs.getString("URL"));
				reportItem.setNewsDate(rs.getString("ItemDate"));
				reportItem.setImageUrl(rs.getString("NewsImages"));
				reportItem.setNewsSource(rs.getString("Source"));

				/* reportItem.setReportContent(rs.getString("reportContent")); */
				reportItem.setReportMimeType(rs.getString("reportMimeType"));
				reportItem.setReportLink(rs.getString("reportLink"));
				reportItem.setReportLifeSpan(rs.getString("reportLifeSpan"));
				reportItem.setIsReport(rs.getString("isReport"));

				reportItemList.add(reportItem);
			}
			rs.close();
			stmt.close();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able to connect to the database");
			e.printStackTrace();
		}

		return reportItemList;
	}

	
	public NewsItemList getReportItem(int reportId) {
		logger.log(Level.INFO, "In getReportItem() method : reportid : "+reportId);
		NewsItemList itemList = new NewsItemList();
		ReportItem reportItem = new ReportItem();
		try {
			Connection connection = (Connection) getConnection();
			String sql = "SELECT NewsItemId,Content,Title,Abstract,URL,ItemDate,UploadedAt,NewsImages,Source,reportContent,reportMimeType,reportLink,reportLifeSpan,isReport FROM newsitem where NewsItemId ="
					+ reportId;
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {

				reportItem.setNewsId(rs.getInt("NewsItemId"));
				reportItem.setNewsContent(rs.getString("Content"));
				reportItem.setNewsTitle(rs.getString("Title"));
				reportItem.setAbstractNews(rs.getString("Abstract"));
				reportItem.setUrl(rs.getString("URL"));
				reportItem.setNewsDate(rs.getString("ItemDate"));
				reportItem.setImageUrl(rs.getString("NewsImages"));
				reportItem.setNewsSource(rs.getString("Source"));

				reportItem.setReportContent(rs.getString("reportContent"));
				reportItem.setReportMimeType(rs.getString("reportMimeType"));
				reportItem.setReportLink(rs.getString("reportLink"));
				reportItem.setReportLifeSpan(rs.getString("reportLifeSpan"));
				reportItem.setIsReport(rs.getString("isReport"));
			}
			rs.close();
			stmt.close();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able to connect to the database");
			logger.log(Level.INFO, "In getReportItem() method : Error : "+e);
			e.printStackTrace();
		}
		itemList.add(reportItem);
		logger.log(Level.INFO, "In getReportItem() method : task completed  ");
		return itemList;

	}

	

	public ArrayList<Object> getFileToDownload(int reportId) {
		logger.log(Level.INFO, "In getFileToDownload() method : reportid : "+reportId);
		ArrayList<Object> downloadfileInfo = new ArrayList<Object>();
		String mimeType = null;
		InputStream inputStream = null;
		Blob downlaodfile = null;
		try {
			Connection connection = (Connection) getConnection();
			String sql = "SELECT reportContent,reportMimeType FROM newsitem where NewsItemId ="
					+ reportId;
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {

				downlaodfile = rs.getBlob("reportContent");
				mimeType = rs.getString("reportMimeType");

				inputStream = downlaodfile.getBinaryStream();

				downloadfileInfo.add(inputStream);
				downloadfileInfo.add(mimeType);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			logger.log(Level.INFO, "In getFileToDownload() method : Error : "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "In getFileToDownload() method : task completed  ");
		return downloadfileInfo;
	}

	public void setAllNewsreportsFields(ReportItem reportItem,
			InputStream inputstream, HttpServletRequest request) {
		logger.log(Level.INFO, "In setAllNewsreportsFields() method : ");
		PreparedStatement prestmt = null;
		try {
			Connection conn = (Connection) getConnection();
			String query = "insert into newsitem(Title,Abstract,itemDate,URL,Source,NewsImages,Content,reportContent,reportMimeType,reportLink,reportLifeSpan,isReport) values(?,?,?,?,?,?,?,?,?,?,?,?)";

			prestmt = (PreparedStatement) conn.prepareStatement(query);
			prestmt.setString(1, reportItem.getNewsTitle());
			prestmt.setString(2, reportItem.getAbstractNews());
			prestmt.setString(3, reportItem.getNewsDate());
			prestmt.setString(4, reportItem.getUrl());
			prestmt.setString(5, reportItem.getReportLink());
			prestmt.setString(6, reportItem.getImageUrl());
			prestmt.setString(7, reportItem.getNewsContent());
			prestmt.setBinaryStream(8, inputstream, inputstream.available());
			prestmt.setString(9, reportItem.getReportMimeType());
			prestmt.setString(10, reportItem.getReportLink());
			prestmt.setString(11, reportItem.getReportLifeSpan());
			prestmt.setString(12, "1");
			prestmt.executeUpdate();
			prestmt.close();
			setTagIdAndReportId(reportItem, request);
		} catch (Exception e) {
			logger.log(Level.INFO, "In setAllNewsreportsFields() method : Error : "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "In setAllNewsreportsFields() method : task completed  ");
		prestmt = null;
	}

	public void setTagIdAndReportId(ReportItem item, HttpServletRequest request) {
		logger.log(Level.INFO, "In setTagIdAndReportId() method : ");
		Connection conn = (Connection) getConnection();
		String col = "NewsItemId";
		String tableName = "newsitem";
		HttpSession session = request.getSession(false);
		UserInformation userInformation = (UserInformation) session
				.getAttribute("userInfo");
		int industryN = userInformation.getUserSelectedIndustryID();
		String industry = userInformation.getIndustryNewsCenterName();
		ArrayList listtagName = item.getAssociatedTagList();

		Iterator iterate = listtagName.iterator();
		int maxCount = getMaxValue(col, tableName);
		try {
			while (iterate.hasNext()) {
				String tagName = (String) iterate.next();
				int tagId = getTagId(industry, tagName);
				String query = "insert into newstagitem(NewsItemId,TagItemId,IndustryEnumId)values("
						+ maxCount + "," + tagId + "," + industryN + ")";
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(query);
				stmt.close();
			}

		} catch (Exception ex) {
			logger.log(Level.INFO, "In setTagIdAndReportId() method : Error : "+ex);
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "In setTagIdAndReportId() method : task completed  ");
	}

	public int getMaxValue(String col, String tableName) {
		logger.log(Level.INFO, "In getMaxValue() method : ");
		int maxValue = 0;
		try {
			Connection conn = (Connection) getConnection();
			String query = "SELECT max(" + col + ") from " + tableName;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				maxValue = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		} catch (Exception ex) {
			logger.log(Level.INFO, "In getMaxValue() method : Error : "+ex);
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "In getMaxValue() method : task completed  ");
		return maxValue;
	}

	public int getTagId(String industry, String tag) {
		logger.log(Level.INFO, "In getTagId() method : ");
		int id = 0;
		try {
			Connection conn = (Connection) getConnection();
			String query = "SELECT TagItemId from tagitem where Name = '"
					+ tag
					+ "' and IndustryId=(SELECT IndustryEnumId from industryenum where Name='"
					+ industry + "')";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				id = rs.getInt("TagItemId");
			}
			rs.close();
			stmt.close();
		} catch (Exception ex) {
			logger.log(Level.INFO, "In getTagId() method : Error : "+ex);
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "In getTagId() method : task completed  ");
		return id;
	}

	public boolean saveNewTags(TagItemInformation tagItem, String parentName,
			boolean isCategory, UserInformation userInfo) {
		try {
			logger.log(Level.INFO, "In saveNewTags() method : ");
			int parentId = 0;
			Connection conn = (Connection) getConnection();

			if (!isCategory) {
				String query2 = "select TagItemId from tagitem where Name='"
						+ parentName
						+ "' and IndustryId = "
						+ userInfo.getUserSelectedIndustryID()
						+ " and ParentId = (select TagItemId from tagitem where Name = '"
						+ userInfo.getIndustryNewsCenterName() + "')";
				Statement stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery(query2);
				while (rs2.next()) {
					parentId = rs2.getInt("TagItemId");
				}
				rs2.close();
				stmt2.close();
			} else {
				parentId = tagItem.getParentId();
			}
			String query1 = "select * from tagitem where Name='"
					+ tagItem.getTagName() + "' and ParentId=" + parentId
					+ " and IndustryId=" + tagItem.getIndustryId();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query1);
			if (rs.next() == true) {
				flag = false;
			} else {
				String query = "insert into tagitem(Name,ParentId,IndustryId,UserId,isPrimary) values('"
						+ tagItem.getTagName()
						+ "',"
						+ parentId
						+ ","
						+ tagItem.getIndustryId()
						+ ","
						+ userInfo.getUserId()
						+ "," + tagItem.isIsprimary() + ")";
				stmt.executeUpdate(query);
				flag = true;
			}
			rs.close();
			stmt.close();
		} catch (Exception ex) {
			logger.log(Level.INFO, "In saveNewTags() method : Error : "+ex);
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "In saveNewTags() method : task completed  ");
		return flag;
	}

	public void editNewsReportFields(ReportItem reportItem,	InputStream inputstream, HttpServletRequest req) {
		PreparedStatement prestmt = null;
		logger.log(Level.INFO, "In editNewsReportFields() method : ");
		try {
			Connection conn = (Connection) getConnection();
			if(inputstream.available()==0){
				String query = "update newsitem set Title=?,Abstract=?,URL=?,itemDate=?,reportLink=?,reportLifeSpan=? where NewsItemId="
					+ reportItem.getNewsId();
				prestmt = (PreparedStatement) conn.prepareStatement(query);
				prestmt.setString(1, reportItem.getNewsTitle());
				prestmt.setString(2, reportItem.getAbstractNews());
				prestmt.setString(3, reportItem.getUrl());
				prestmt.setString(4, reportItem.getNewsDate());
				prestmt.setString(5, reportItem.getReportLink());
				prestmt.setString(6, reportItem.getReportLifeSpan());
				prestmt.executeUpdate();
				prestmt.close();
				editReportTags(reportItem,req);
			}
			else{
				String query = "update newsitem set Title=?,Abstract=?,URL=?,itemDate=?,reportContent=?,reportMimeType=?,reportLink=?,reportLifeSpan=? where NewsItemId="
						+ reportItem.getNewsId();
				prestmt = (PreparedStatement) conn.prepareStatement(query);
				prestmt.setString(1, reportItem.getNewsTitle());
				prestmt.setString(2, reportItem.getAbstractNews());
				prestmt.setString(3, reportItem.getUrl());
				prestmt.setString(4, reportItem.getNewsDate());
				prestmt.setBinaryStream(5, inputstream, inputstream.available());
				prestmt.setString(6, reportItem.getReportMimeType());
				prestmt.setString(7, reportItem.getReportLink());
				prestmt.setString(8, reportItem.getReportLifeSpan());
				prestmt.executeUpdate();
				prestmt.close();
				editReportTags(reportItem,req);
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "In editNewsReportFields() method : Error : "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "In editNewsReportFields() method : task completed  ");
		prestmt = null;
	}
	
	public void editReportTags(ReportItem reportItem, HttpServletRequest req){
		logger.log(Level.INFO, "In editReportTags() method : ");
		int newsid = reportItem.getNewsId();
		Connection conn = (Connection) getConnection();
		try{
			String query = "delete from newstagitem where NewsItemId = "+newsid;
			Statement stmt = conn.createStatement() ;
			stmt.executeUpdate(query);
			stmt.close();
		}
		catch(Exception ex){
			logger.log(Level.INFO, "In editReportTags() method : Error : "+ex);
			ex.printStackTrace();
		}
		HttpSession session = req.getSession(false);
		LhUser userInformation  = (LhUser)session.getAttribute("userInfo");
		int industryN = userInformation.getUserSelectedIndustryID();
		String industry = userInformation.getIndustryNewsCenterName();
		ArrayList listtagName = reportItem.getAssociatedTagList();
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
			logger.log(Level.INFO, "In editReportTags() method : Error : "+ex);
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "In editReportTags() method : task completed  ");
	}

	public void deleteSelectedReport(HashMap hashmap) {
		logger.log(Level.INFO, "In deleteSelectedReport() method : ");
		try {
			Connection conn = (Connection) getConnection();
			for (Object obj : hashmap.keySet()) {
				Statement stmt = conn.createStatement();
				Long id = (Long) obj;
				String query = "DELETE from newsitem where NewsItemId=" + id;
				stmt.executeUpdate(query);
				stmt.close();
			}
		} catch (Exception ex) {
			logger.log(Level.INFO, "In deleteSelectedReport() method : Error : "+ex);
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "In deleteSelectedReport() method : task completed  ");
	}

	public AdminReportItemList getAllReportItems(String industryName,
			String tagName) {
		logger.log(Level.INFO, "In deleteSelectedReport() method : ");
		AdminReportItem report = null;
		TagItemInformation tagitem;
		AdminReportItemList reportItemList = new AdminReportItemList();
		try {
			Connection conn = (Connection) getConnection();
			if (tagName != null) {
				int newsid = 0;
				int idNewsItem = getReportItemId(industryName, tagName);
				String query = "select * from newsitem N INNER JOIN newstagitem NT on N.NewsItemId = NT.NewsItemId where TagItemId ="
						+ idNewsItem
						+ " and isReport=1 and CURRENT_DATE <= reportLifeSpan";
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					report = new AdminReportItem();
					newsid = rs.getInt("NewsItemId");
					String query1 = "select * from tagitem where TagItemId in(select TagItemId from newstagitem where NewsItemId="
							+ newsid + ")";
					Statement stmt1 = conn.createStatement();
					ResultSet rs1 = stmt1.executeQuery(query1);
					while (rs1.next()) {
						tagitem = new TagItemInformation();
						tagitem.setTagItemId(rs1.getInt("TagItemId"));
						tagitem.setTagName(rs1.getString("Name"));
						tagitem.setParentId(rs1.getInt("ParentId"));
						tagitem.setIndustryId(rs1.getInt("IndustryId"));
						tagitem.setIsprimary(rs1.getBoolean("isPrimary"));
						report.addTagforNews(tagitem);
					}
					rs1.close();
					stmt1.close();
					report.setNewsId(rs.getInt("NewsItemId"));
					report.setNewsContent(rs.getString("Content"));
					report.setNewsTitle(rs.getString("Title"));
					report.setAbstractNews(rs.getString("Abstract"));
					report.setUrl(rs.getString("URL"));
					report.setNewsDate(convertStringToSqlDate(rs
							.getString("ItemDate")));
					report.setImageUrl(rs.getString("NewsImages"));
					report.setNewsSource(rs.getString("Source"));
					report.setReportContent(rs.getString("reportContent"));
					report.setReportMimeType(rs.getString("reportMimeType"));
					report.setReportLink(rs.getString("reportLink"));
					report.setReportLifeSpan(rs.getString("reportLifeSpan"));
					report.setIsReport(rs.getString("isReport"));

					reportItemList.add(report);
				}
				rs.close();
				stmt.close();
			}
		} catch (Exception ex) {
			logger.log(Level.INFO, "In deleteSelectedReport() method : Error : "+ex);
			ex.printStackTrace();
		}
		
		logger.log(Level.INFO, "List size for edit news:::: "
				+ reportItemList.size());
		logger.log(Level.INFO, "In deleteSelectedReport() method : task completed  ");
		return reportItemList;
	}

	private Date convertStringToSqlDate(String dateStr) {
		logger.log(Level.INFO, "In convertStringToSqlDate() method : ");
		Date convertedDate = null;
		try {
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date result = formater.parse(dateStr);
			convertedDate = new Date(result.getTime());
		} catch (Exception e) {
			logger.log(Level.INFO, "In convertStringToSqlDate() method : Error : "+e);
		
			e.printStackTrace();
		}
		logger.log(Level.INFO, "In convertStringToSqlDate() method : task completed  ");
		return convertedDate;
	}

	public int getReportItemId(String industryName, String tagName) {
		int id = 0;
		try {
			String query = "select TagItemId from tagitem where Name= '"
					+ tagName
					+ "' and IndustryId =(SELECT IndustryEnumId from industryenum where Name = '"
					+ industryName + "')";
			Connection conn = (Connection) getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				id = rs.getInt("TagItemId");
			}
			rs.close();
			stmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return id;
	}

	public ReportItemList getAllReportItem(Group group, int newsmode, GroupPageCriteria criteria, Long userid) {
		ReportItemList reportItemList = new ReportItemList();
		ArrayList<TagItem> selectedtaglist = new ArrayList<TagItem>();
		ReportItem reportItem;
		logger.log(Level.INFO, "In getAllReportItem() method : ");
		try {
			if (newsmode == MainNewsPresenter.OR) {
				selectedtaglist = group.getGroupCategoryMap().getSelectedTags();
				Iterator iter = selectedtaglist.iterator();
				String selectNewsQuery = "SELECT distinct n.NewsItemId,n.Content,n.Title,n.Abstract,n.URL,n.ItemDate,n.UploadedAt,n.NewsImages,n.Source,n.reportMimeType,n.reportLink,n.reportLifeSpan,n.isReport from newsitem n INNER JOIN newstagitem nt where n.isReport=1 and CURRENT_DATE <= n.reportLifeSpan and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
				while (iter.hasNext()) {
					TagItem tag = (TagItem) iter.next();
					int tagid = tag.getTagId();
					selectNewsQuery += "," + tagid;
				}
				selectNewsQuery += ")";

				Statement stmt = getConnection().createStatement();
				ResultSet rs = stmt.executeQuery(selectNewsQuery);
				while (rs.next()) {

					reportItem = new ReportItem();

					reportItem.setNewsId(rs.getInt("NewsItemId"));
					reportItem.setNewsContent(rs.getString("Content"));
					reportItem.setNewsTitle(rs.getString("Title"));
					reportItem.setAbstractNews(rs.getString("Abstract"));
					reportItem.setUrl(rs.getString("URL"));
					reportItem.setNewsDate(rs.getString("ItemDate"));
					reportItem.setImageUrl(rs.getString("NewsImages"));
					reportItem.setNewsSource(rs.getString("Source"));

					reportItem
							.setReportMimeType(rs.getString("reportMimeType"));
					reportItem.setReportLink(rs.getString("reportLink"));
					reportItem
							.setReportLifeSpan(rs.getString("reportLifeSpan"));
					reportItem.setIsReport(rs.getString("isReport"));

					reportItemList.add(reportItem);
				}
				rs.close();
				stmt.close();
				reportItemList.setAndNews(false);

			} else if (newsmode == MainNewsPresenter.AND) {
				ArrayList<Integer> reportitemidList = new ArrayList<Integer>();
				selectedtaglist = group.getGroupCategoryMap()
						.getSelectedTagsByCategory(); // the selected tags on
														// the client side are
														// in selectedtaglist
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
													// selected for AND criteria
													// (3663,613|)
								i++;
							} else
								csv += tagid + "|";
						}
					}
					csv = csv.substring(0, csv.length() - 1); // removed the |
					proc = getConnection().prepareCall(
							"{ call SP_GETNEWSREPORTS(?, ?, ?, ?) }");

					String tbname1 = "temp1_" + userid;
					String tbname2 = "temp2_" + userid;
					proc.setString(1, csv);
					proc.setInt(2, totalcategories);
					proc.setString(3, tbname1);
					proc.setString(4, tbname2);
					boolean isresultset = proc.execute();
					if (isresultset) {
						ResultSet rs = proc.getResultSet();

						while (rs.next()) {
							reportItem = new ReportItem();
							int newsid = rs.getInt("NewsItemId");
							reportItem.setNewsId(newsid);
							reportItem.setNewsId(rs.getInt("NewsItemId"));
							reportItem.setNewsContent(rs.getString("Content"));
							reportItem.setNewsTitle(rs.getString("Title"));
							reportItem
									.setAbstractNews(rs.getString("Abstract"));
							reportItem.setUrl(rs.getString("URL"));
							reportItem.setNewsDate(rs.getString("ItemDate"));
							reportItem.setImageUrl(rs.getString("NewsImages"));
							reportItem.setNewsSource(rs.getString("Source"));

							reportItem.setReportMimeType(rs
									.getString("reportMimeType"));
							reportItem
									.setReportLink(rs.getString("reportLink"));
							reportItem.setReportLifeSpan(rs
									.getString("reportLifeSpan"));
							reportItem.setIsReport(rs.getString("isReport"));
							if (!reportitemidList.contains(reportItem
									.getNewsId())) {
								reportItemList.add(reportItem);
								reportitemidList.add(reportItem.getNewsId());
							}
						}
						rs.close();

					}
					proc.close();
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able to connect to the database");
			logger.log(Level.INFO, "In getAllReportItem() method : Error : "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "In getAllReportItem() method : rask completed : ");
		return reportItemList;
	}

	public ReportItemList getUserAlertReportItemsForAndMode(Group group, int userid){
		logger.log(Level.INFO, "[ ReportsHelper --- getUserAlertReportItems initiated ]");
		 long start = System.currentTimeMillis();
		ReportItemList reportItemList = new ReportItemList();
		ReportItem reportItem;
		ArrayList<TagItem> selectedtaglist = new ArrayList<TagItem>();
		try {
			ArrayList<Integer> reportitemidList = new ArrayList<Integer>();
			selectedtaglist = group.getGroupCategoryMap().getSelectedTagsByCategory(); // the selected tags on
													// the client side are
													// in selectedtaglist
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
												// selected for AND criteria
												// (3663,613|)
							i++;
						} else
							csv += tagid + "|";
					}
				}
				csv = csv.substring(0, csv.length() - 1); // removed the |
				proc = getConnection().prepareCall(
						"{ call SP_GETNEWSREPORTS(?, ?, ?, ?) }");

				String tbname1 = "temp1_" + userid;
				String tbname2 = "temp2_" + userid;
				proc.setString(1, csv);
				proc.setInt(2, totalcategories);
				proc.setString(3, tbname1);
				proc.setString(4, tbname2);
				boolean isresultset = proc.execute();
				if (isresultset) {
					ResultSet rs = proc.getResultSet();

					while (rs.next()) {
						reportItem = new ReportItem();
						int newsid = rs.getInt("NewsItemId");
						reportItem.setNewsId(newsid);
						reportItem.setNewsId(rs.getInt("NewsItemId"));
						reportItem.setNewsContent(rs.getString("Content"));
						reportItem.setNewsTitle(rs.getString("Title"));
						reportItem
								.setAbstractNews(rs.getString("Abstract"));
						reportItem.setUrl(rs.getString("URL"));
						reportItem.setNewsDate(rs.getString("ItemDate"));
						reportItem.setImageUrl(rs.getString("NewsImages"));
						reportItem.setNewsSource(rs.getString("Source"));

						reportItem.setReportMimeType(rs
								.getString("reportMimeType"));
						reportItem
								.setReportLink(rs.getString("reportLink"));
						reportItem.setReportLifeSpan(rs
								.getString("reportLifeSpan"));
						reportItem.setIsReport(rs.getString("isReport"));
						if (!reportitemidList.contains(reportItem
								.getNewsId())) {
							reportItemList.add(reportItem);
							reportitemidList.add(reportItem.getNewsId());
						}
					}
					rs.close();

				}
				proc.close();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able to connect to the database");
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[ ReportsHelper --- getUserAlertReportItems completed ]");
		long elapsed = System.currentTimeMillis() - start;
		logger.log(Level.INFO,"TIME REQUIRED TO GET AND MODE REOPRT ITEM FOR USER ALERT  = " + elapsed + "ms");
		return reportItemList;
	}
	
	public ReportItemList getUserAlertReportItems(String concatenatedReportQuery) {
		logger.log(Level.INFO, "[ ReportsHelper --- getUserAlertReportItems initiated ]");
		long start = System.currentTimeMillis();
		ReportItemList reportItemList = new ReportItemList();
		ReportItem reportItem;
		try {
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(concatenatedReportQuery);
			while (rs.next()) {
				reportItem = new ReportItem();

				reportItem.setNewsId(rs.getInt("NewsItemId"));
				reportItem.setNewsContent(rs.getString("Content"));
				reportItem.setNewsTitle(rs.getString("Title"));
				reportItem.setAbstractNews(rs.getString("Abstract"));
				reportItem.setUrl(rs.getString("URL"));
				reportItem.setNewsDate(rs.getString("ItemDate"));
				reportItem.setImageUrl(rs.getString("NewsImages"));
				reportItem.setNewsSource(rs.getString("Source"));

				reportItem.setReportMimeType(rs.getString("reportMimeType"));
				reportItem.setReportLink(rs.getString("reportLink"));
				reportItem.setReportLifeSpan(rs.getString("reportLifeSpan"));
				reportItem.setIsReport(rs.getString("isReport"));

				reportItemList.add(reportItem);
			}
			rs.close();
			stmt.close();
			reportItemList.setAndNews(false);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "not able to connect to the database");
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[ ReportsHelper --- getUserAlertReportItems completed ]");
		long elapsed = System.currentTimeMillis() - start;
		logger.log(Level.INFO,"TIME REQUIRED TO GET OR MODE REOPRT ITEM FOR USER ALERT  = " + elapsed + "ms");
		return reportItemList;
	}
	/**
	 * This method will  concatenate all the report queries for the list of groups
	 * @param groupList
	 * @param ncid
	 * @param userId
	 * @param datetime
	 * @return
	 * @throws CategoryHelperException
	 */
	public HashMap<String, String> concateGroupsReportQuery(ArrayList<Group> groupList,int ncid,int userId) throws CategoryHelperException{
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: concateGroupsReportQuery()  initiated for USERID:: "+userId+" and NCID :: "+ncid+" ]");
		CategoryHelper catHelper=new CategoryHelper(ncid, userId);
		String concatedReportsQuery="";
		Boolean concatFlag=false;
		Boolean updateQueryFlag=false;
		LhNewsItemHelper newsHelper=null;
		HashMap<String, String> groupQueryMap = new HashMap<String, String>();
		
		try{
			for(Group group:groupList){
				CategoryMap categoryMap = catHelper.getCategories();
				GroupHelper grpHelper=new GroupHelper();
				GroupCategoryMap groupCategoryMap = grpHelper.getGroupCategoryMapWithSelections(group, categoryMap);
				group.setGroupCategoryMap(groupCategoryMap);
				int filterMode = group.getNewsFilterMode();
							
				HashMap<String, String> queryMap = generateReportsQueryMapForAllNewsItems(groupCategoryMap,userId, filterMode, updateQueryFlag, ncid);
				
				if(queryMap.size()>0){
					updateQueryFlag=true;
				
					String reportsQuery=queryMap.get("reportsQuery");
				
					if(concatedReportsQuery.equalsIgnoreCase("")){
						concatedReportsQuery=reportsQuery;
					}else{
						if(!concatFlag){
							concatedReportsQuery="("+concatedReportsQuery+") UNION ("+reportsQuery+")";
							concatFlag=true;
						}
						else{
							concatedReportsQuery=concatedReportsQuery+" UNION ("+reportsQuery+")";
						}
					}
				}
		    }	
			groupQueryMap=new HashMap<String, String>();
			if(!concatedReportsQuery.equalsIgnoreCase(""))
				groupQueryMap.put("reportsQuery", concatedReportsQuery);
		}catch (Exception e) {
			logger.log(Level.INFO," [ Concatenation of the  groupsReportsQuery failed  ] ");
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: concateGroupsReportQuery()  completed for USERID:: "+userId+" and NCID :: "+ncid+" ]");
		return groupQueryMap;
	}
	

	/**
	 * generates the reports fetching query
	 * @param userid
	 * @param filtermode
	 * @param updateQueryFlag
	 * @param ncid
	 * @return
	 */
	public HashMap<String, String> generateReportsQueryMapForAllNewsItems(GroupCategoryMap gcmap, int userid, int filtermode,Boolean updateQueryFlag,int ncid) {

		ArrayList selectedTagsList = null;
		GroupCategoryMap groupCategoryMap = gcmap;
		HashMap<String, String> queryMap = new HashMap<String, String>();
		
		if (groupCategoryMap.size() != 0) {
			try {
				selectedTagsList = groupCategoryMap.getSelectedTags();
				if (selectedTagsList.size() > 0) {
					Iterator iter = selectedTagsList.iterator();
					String selectNewsQuery = "SELECT distinct n.NewsItemId,n.Content,n.Title,n.Abstract,n.URL,n.ItemDate,n.UploadedAt,n.NewsImages,n.Source,n.reportMimeType,n.reportLink,n.reportLifeSpan,n.isReport from newsitem n INNER JOIN newstagitem nt where n.isReport=1 and CURRENT_DATE <= n.reportLifeSpan and n.NewsItemId = nt.NewsItemId and nt.TagItemId in(0";
					while (iter.hasNext()) {
						TagItem tag = (TagItem) iter.next();
						int tagid = tag.getTagId();
						selectNewsQuery += "," + tagid;
					}
					selectNewsQuery += ")";

					queryMap.put("reportsQuery", selectNewsQuery);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return queryMap;

	}
	
}
