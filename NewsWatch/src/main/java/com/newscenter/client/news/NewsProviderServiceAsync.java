package com.newscenter.client.news;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.TagItem;

public interface NewsProviderServiceAsync 
{

	void getMatchingNewsItems(AsyncCallback callback);
	
	void getPage(PageCriteria criteria, int newsmode, AsyncCallback callback);
	
	void getAndPage(PageCriteria criteria, int newsmode, AsyncCallback callback);
	
	void getAllNewsforTag(TagItem tagitem, PageCriteria criteria, AsyncCallback callback);
	
	void getAllNewsForCategory(CategoryItem categoryItem, PageCriteria criteria, AsyncCallback callback);
	
	public void getadmininformation(AsyncCallback callback);
	
	void saveNewsletterPreference(String choice, AsyncCallback callback);
	
	void saveNewsFilterModePreference(String choice, AsyncCallback callback);
}
