package com.lighthouse.comment.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lighthouse.comment.client.CommentList;

/**
 * 
 * @author kiran@ensarm.com
 * 
 */
public interface ItemCommentServiceAsync {

	void postComment(String commentText, int newsItemId, 
			AsyncCallback<CommentList> asyncCallback);

	void updateComment(int commentId, String commentText,
			AsyncCallback<Boolean> callback);

	void deleteComment(int commentId, AsyncCallback<Boolean> callback);

	void getCommentListForItem(int itemId, AsyncCallback<CommentList> callback);

	void getSession(AsyncCallback<Void> callback);

}
