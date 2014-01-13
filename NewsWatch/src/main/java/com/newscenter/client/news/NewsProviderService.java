package com.newscenter.client.news;

import java.util.ArrayList;


import com.google.gwt.user.client.rpc.RemoteService;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.TagItem;

public interface NewsProviderService extends RemoteService 
{
	
	public NewsItemList getMatchingNewsItems();
	
	public NewsItemList getPage(PageCriteria criteria, int newsmode);
	
	public NewsItemList getAndPage(PageCriteria criteria, int newsmode);
	
	public NewsItemList getAllNewsforTag(TagItem tagitem, PageCriteria criteria);
	
	public NewsItemList getAllNewsForCategory(CategoryItem categoryItem, PageCriteria criteria);
	
	public ArrayList getadmininformation();
	
	public boolean saveNewsletterPreference(String choice);
	
	public int saveNewsFilterModePreference(String choice);
	
}
