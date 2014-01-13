package com.lighthouse.comment.server;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.lighthouse.comment.client.CommentList;
import com.lighthouse.comment.client.exception.CommentException;
import com.lighthouse.comment.client.service.ItemCommentService;
import com.lighthouse.comment.server.db.ItemCommentHelper;
import com.lighthouse.login.user.client.domain.LhUser;

/**
 * 
 * @author kiran@ensarm.com
 * 
 */
public class ItemCommentServiceImpl extends RemoteServiceServlet implements
		ItemCommentService {

	
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(ItemCommentServiceImpl.class.getName());
	/**
	 * 
	 */
	@Override
	public CommentList postComment(String commentText, int newsItemId) throws CommentException {
		logger.log(Level.INFO,
		"[ItemCommentServiceImpl ::: CommentList ::: postComment(String commentText, int newsItemId)]");
		
		HttpServletRequest req = this.getThreadLocalRequest();
		HttpSession session = req.getSession(false);
		LhUser userInformation = (LhUser) session.getAttribute("userInfo");
		int userId=userInformation.getUserId();
		CommentList commentList = new CommentList();
		try {

			ItemCommentHelper commentHelper = new ItemCommentHelper();
			commentList = commentHelper.postComment(commentText, newsItemId,userId);
			commentHelper.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return commentList;
	}

	/**
	 * 
	 */
	@Override
	public boolean updateComment(int commentId, String commentText) throws CommentException {
		try {
			logger.log(Level.INFO, "[ItemCommentServiceImpl ::: boolean ::: updateComment(int commentId, String commentText)]");
			ItemCommentHelper commentHelper = new ItemCommentHelper();
			commentHelper.updateComment(commentId, commentText);
			commentHelper.closeConnection();
			return true;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return false;
	}

	/**
     * 
     */
	@Override
	public boolean deleteComment(int commentId) throws CommentException {
		try {
			logger.log(Level.INFO,
			"[ItemCommentServiceImpl ::: boolean ::: deleteComment(int commentId)]");
			ItemCommentHelper commentHelper = new ItemCommentHelper();
			commentHelper.deleteComment(commentId);
			commentHelper.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return true;
	}

	/**
     * 
     */
	@Override
	public CommentList getCommentListForItem(int itemId)
			throws CommentException {
		logger.log(Level.INFO,
		"[ItemCommentServiceImpl ::: CommentList ::: getCommentListForItem(int itemId)]");
		ItemCommentHelper commentHelper = new ItemCommentHelper();
		ArrayList cList = commentHelper.getCommentListForItem(itemId);
		CommentList commentList = new CommentList();
		commentList.addAll(cList);
		commentHelper.closeConnection();
		return commentList;
	}

	@Override
	public void getSession() {
		// TODO Auto-generated method stub

	}

}
