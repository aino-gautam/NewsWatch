package com.lighthouse.newspage.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lighthouse.newspage.client.domain.CommentedNewsItem;
/**
 * 
 * @author kiran@ensarm.com
 *
 */
public interface NewsItemHomeServiceAsync {

	void loadNewsItemContent(int newsId, AsyncCallback<CommentedNewsItem> callback);

	


}
