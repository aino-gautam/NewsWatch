package com.newscenter.client.tags;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsItemList;

public interface ItemProviderService extends RemoteService 
{
	//public void getTagItems();
	
	public CategoryMap getCategories();
	
	public CategoryMap getUserSelectionCategories();
	
	public void updateUserItemSelection(TagItem tagItem, boolean selectionStatus);
	
	public void updateUserItemSelection(ArrayList tagList);
	
	public void updateUserItemSelection(CategoryMap categoryMap);
	
	public void refreshUserSelection(CategoryMap categoryMap);

	public NewsItemList updateSessionCategoryMap(CategoryMap categoryMap, PageCriteria criteria, int newsmode);
}
