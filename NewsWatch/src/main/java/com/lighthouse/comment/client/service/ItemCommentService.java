package com.lighthouse.comment.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.lighthouse.comment.client.CommentList;
import com.lighthouse.comment.client.exception.CommentException;
/**
 * 
 * @author kiran@ensarm.com
 *
 */
@RemoteServiceRelativePath("comments")
public interface ItemCommentService extends RemoteService {

	CommentList postComment(String commentText, int newsItemId);

	boolean updateComment(int commentId, String commentText)
			throws CommentException;

	public boolean deleteComment(int commentId) throws CommentException;

	public CommentList getCommentListForItem(int itemId)
			throws CommentException;
	public void getSession();

}
