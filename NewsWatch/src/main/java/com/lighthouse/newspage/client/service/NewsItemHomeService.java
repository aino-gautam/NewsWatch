package com.lighthouse.newspage.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.lighthouse.newspage.client.domain.CommentedNewsItem;
/**
 * 
 * @author kiran@ensarm.com
 *
 */
@RemoteServiceRelativePath("newsItemHome")
public interface NewsItemHomeService extends RemoteService{

	public CommentedNewsItem loadNewsItemContent(int newsId);

	
}
