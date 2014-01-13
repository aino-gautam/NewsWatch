package com.lighthouse.newspage.server.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lighthouse.comment.client.CommentList;
import com.lighthouse.comment.client.domain.ItemComment;
import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.main.server.helper.LhNewsItemHelper;
import com.lighthouse.newspage.client.domain.CommentedNewsItem;

import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.TagItem;

/**
 * 
 * @author kiran@ensarm.com
 * 
 *         This Helper class for doing all database related function for
 *         NewsItemHome
 * 
 */
public class NewsItemHomeHelper extends LhNewsItemHelper {

	Logger logger = Logger.getLogger(NewsItemHomeHelper.class.getName());
	 
	public NewsItemHomeHelper() {
		super(currentMap, folderurl);
	}

	/**
	 * This method is for getting all information about newsitem like
	 * newsTitle,Content,associated tags and comment list.
	 * 
	 * @param newsId
	 * @return
	 */
	public ArrayList getNewsItemContent(int newsId) {
		folderurl = getFolderurl();
		folderimage = getFolderimage();
		folderimage.mkdir();
		GroupCategoryMap groupCategoryMap = getCurrentMap();
		try {
			logger.log(Level.INFO,
			"[NewsItemHomeHelper ::: ArrayList ::: getNewsItemContent(int newsid)]");
			ArrayList<CommentedNewsItem> commentedNewsItemsList = new ArrayList<CommentedNewsItem>();
			ArrayList<TagItem> tagItemsList = new ArrayList<TagItem>();
			CommentList commentsList = new CommentList();
			ResultSet resultSet;
			ResultSet resultSetForTags, resultSetForComments, resultSetForUserName = null, resultSetForViewsCount;
			Connection con = (Connection) getConnection();

			String query = " select n.NewsItemId,n.Content,n.Title,n.Abstract,n.URL,n.itemDate ,n.UploadedAt,n.NewsImages,n.Source,n.isLocked"+" "
					+ " from"
					+ " newsitem n where n.NewsItemId ="
					+ newsId + "";

			String queryForTags = "select t.TagItemId,t.Name from tagitem t,newstagitem n where t.TagItemId = n.TagItemId and n.NewsItemId = "
					+ newsId + "";

			String queryForComments = "select distinct * from itemcomment i where i.newsItemId = "
					+ newsId + " " + " order by  i.commentTime desc";

			Statement stmt = (Statement) con.createStatement();
			Statement stmt3 = (Statement) con.createStatement();
			Statement stmt2 = (Statement) con.createStatement();
			Statement stmtForViewsCount = (Statement) con.createStatement();
			resultSetForComments = stmt.executeQuery(queryForComments);
			while (resultSetForComments.next()) {
				ItemComment itemComment = new ItemComment();
				itemComment.setItemCommentId(resultSetForComments.getInt("id"));
				itemComment.setText(resultSetForComments.getString("text"));
				itemComment.setUserId(resultSetForComments.getInt("UserId"));
				int userId = resultSetForComments.getInt("UserId");
				itemComment.setCommentTime(resultSetForComments
						.getTimestamp("commentTime"));
				itemComment.setNewsItemId(resultSetForComments
						.getInt("newsItemId"));
				String queryForUserName = "select FirstName from lighthouse.user where UserId="
						+ userId + "";
				resultSetForUserName = stmt2.executeQuery(queryForUserName);
				while (resultSetForUserName.next()) {
					itemComment.setUserName(resultSetForUserName
							.getString("FirstName"));
				}

				commentsList.add(itemComment);
				resultSetForUserName.close();
			}

			resultSetForTags = stmt.executeQuery(queryForTags);
			while (resultSetForTags.next()) {
				if(groupCategoryMap!=null){
					for (Object obj : groupCategoryMap.keySet()) {
						// groupCategoryMap has categoryItems, if in
						// its tags there is a tag selected by user
						// ,then add it in associatedtaglist
						HashMap map = ((CategoryItem) groupCategoryMap
								.get(obj)).getItemMap();
						if (map.containsKey(resultSetForTags.getInt("TagItemId"))) {
					TagItem tagItem = new TagItem();
					tagItem.setTagName(resultSetForTags.getString("Name"));
					tagItemsList.add(tagItem);
						}
					}
				}
				TagItem tagItem = new TagItem();
				tagItem.setTagName(resultSetForTags.getString("Name"));
				tagItemsList.add(tagItem);
			}
			resultSetForTags.close();

			resultSet = stmt3.executeQuery(query);
			while (resultSet.next()) {
				
				CommentedNewsItem commentedNewsItem = new CommentedNewsItem();
				int newsid = resultSet.getInt("NewsItemId");
				commentedNewsItem.setNewsId(resultSet.getInt("NewsItemId"));
				commentedNewsItem.setNewsTitle(resultSet.getString("Title"));
				commentedNewsItem
						.setNewsContent(resultSet.getString("Content"));
				commentedNewsItem.setAbstractNews(resultSet
						.getString("Abstract"));
				//commentedNewsItem.setImageUrl(resultSet.getString("URL"));
				commentedNewsItem.setImageUrl(resultSet.getString("NewsImages"));
				commentedNewsItem.setNewsDate(resultSet.getString("itemDate"));
				commentedNewsItem.setNewsSource(resultSet.getString("Source"));
				commentedNewsItem.setUrl(resultSet.getString("URL"));
				commentedNewsItem.setAssociatedTagList(tagItemsList);
				commentedNewsItem.setIsLocked(resultSet.getInt("isLocked"));
				file = new File(folderimage + "//" + newsid + ".jpg");
				String imageurlTomcatFolder = "imagefolder";
				// String imageurl = folderurl+"\\"+newsid+".jpg";
				String imageurl = imageurlTomcatFolder + "/" + newsid
						+ ".jpg";
				if (resultSet.getBlob("NewsImages") != null) {
					Blob blobim = (Blob) resultSet.getBlob("NewsImages");
					InputStream x = blobim.getBinaryStream();
					int size = x.available();
					if (size != 0) {
						commentedNewsItem.setImageUrl(imageurl);
						OutputStream out = new FileOutputStream(file);
						byte b[] = new byte[size];
						x.read(b);
						out.write(b);
					}
				}

				commentedNewsItem.setCommentsList(commentsList);
				String queryForViewsCount = "Select sum(NewscatalystItemCount+NewsletterItemCount) from useritemaccessstats u where newsitemId="
						+ newsId + "";
				resultSetForViewsCount = stmtForViewsCount
						.executeQuery(queryForViewsCount);
				while (resultSetForViewsCount.next()) {
					commentedNewsItem.setViewsCount(resultSetForViewsCount
							.getInt(1));
				}

				commentedNewsItemsList.add(commentedNewsItem);
			}
			resultSet.close();

			stmt.close();
			con.close();
			logger.log(Level.INFO,"[NewsItemHomeHelper :getNewsItemContent Completed]");
			return commentedNewsItemsList;

		} catch (Exception e) {
			e.printStackTrace();
			logger.log(
					Level.SEVERE,
					"NewsItemHelpher ::: Unable to fetch the List :: "
							+ e.getMessage());
		}
		return null;

	}

	


}