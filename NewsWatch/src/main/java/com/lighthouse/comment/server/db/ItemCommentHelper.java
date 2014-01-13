package com.lighthouse.comment.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lighthouse.comment.client.CommentList;
import com.lighthouse.comment.client.domain.ItemComment;
import com.newscenter.server.db.DBHelper;

/**
 * 
 * @author kiran@ensarm.com Helper class for itemComment
 * 
 */
public class ItemCommentHelper extends DBHelper {

	Logger logger = Logger.getLogger(ItemCommentHelper.class.getName());
	public ItemCommentHelper() {

		super();

	}

	/**
	 * The postComment method for post comment for particular news by specific
	 * User.
	 * 
	 * @param commentText
	 * @param newsItemId
	 * @return boolean value true/false
	 */
	public CommentList postComment(String commentText, int newsItemId,
			int userId) {
		logger.log(Level.INFO,
		"[ItemCommentHelper ::: CommentList ::: postComment()]");
		ArrayList<ItemComment> arrayList = new ArrayList<ItemComment>();
		CommentList commentList = new CommentList();
		try {
			int groupId = 1;
			Date date = new Date();

			ResultSet resultSet;
			java.sql.Timestamp convertedDate = new java.sql.Timestamp(
					date.getTime());

			Connection con = getConnection();
			String query = "insert into itemcomment(id,text,commentTime,newsItemId,userId)values(null,'"
					+ commentText
					+ "','"
					+ convertedDate
					+ "',"
					+ newsItemId
					+ "," + userId + ")";
			String queryForList = "select distinct * from itemcomment i where i.newsItemId = "
					+ newsItemId + " " + " order by  i.commentTime desc";
			Statement stmt = con.createStatement();
			Statement stmtForList = con.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			resultSet = stmtForList.executeQuery(queryForList);
			while (resultSet.next()) {
				ItemComment itemComment = new ItemComment();
				itemComment.setItemCommentId(resultSet.getInt("id"));
				itemComment.setText(resultSet.getString("text"));
				itemComment.setUserId(resultSet.getInt("UserId"));

				itemComment.setCommentTime(resultSet
						.getTimestamp("commentTime"));
				itemComment.setNewsItemId(resultSet.getInt("newsItemId"));

				arrayList.add(itemComment);
				commentList.add(itemComment);
			}
			resultSet.close();
			stmtForList.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return commentList;

	}

	/**
	 * This method for update comment of the Particular NewsItem.
	 * 
	 * @param commentid
	 * @param commentText
	 * @return boolean value true/false
	 */

	public boolean updateComment(int commentid, String updateComment) {
		try {
			logger.log(Level.INFO,
			"[ItemCommentHelper ::: boolean ::: updateComment(int commentid, String updateComment)]");
			Connection con = getConnection();
			Date date = new Date();
			java.sql.Timestamp convertedDate = new java.sql.Timestamp(
					date.getTime());
			String query = "update itemcomment set text='" + updateComment
					+ "',commentTime='" + convertedDate + "' where id = "
					+ commentid + "";

			Statement stmt = con.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * This method used for delete particular comment of news.
	 * 
	 * @param commentId
	 * @return boolean value true/false
	 */

	public boolean deleteComment(int commentId) {

		try {
			logger.log(Level.INFO,
			"[ItemCommentHelper ::: boolean ::: deleteComment(int commentId)]");
			Connection con = getConnection();
			String query = "delete from itemcomment where  id= '" + commentId
					+ "'";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return false;
	}

	/**
	 * This method gives all comment for particular news.
	 * 
	 * @param newsid
	 * @return list of comment
	 */
	public ArrayList getCommentListForItem(int newsid) {
		logger.log(Level.INFO,
		"[ItemCommentHelper ::: ArrayList ::: getCommentListForItem(int newsid)]");
		ArrayList arrayList = new ArrayList();
		try {
			Connection con = getConnection();

			String query = "select distinct * from itemcomment i where i.newsItemId = "
					+ newsid + " " + " order by  i.commentTime desc";

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {

				rs.getInt(1);
				rs.getString(2);
				rs.getInt(3);
				rs.getInt(4);
				arrayList.add(rs);

			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return arrayList;
	}

}
