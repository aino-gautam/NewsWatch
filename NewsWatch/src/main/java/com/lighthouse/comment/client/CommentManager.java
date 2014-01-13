package com.lighthouse.comment.client;

import com.google.gwt.core.client.GWT;
import com.lighthouse.comment.client.service.ItemCommentService;
import com.lighthouse.comment.client.service.ItemCommentServiceAsync;

/**
 * 
 * @author kiran@ensarm.com
 * 
 */
public class CommentManager {

	private static ItemCommentServiceAsync commentServiceAsync = null;

	public static ItemCommentServiceAsync postComment() {

		commentServiceAsync = GWT.create(ItemCommentService.class);

		return commentServiceAsync;

	}

}
