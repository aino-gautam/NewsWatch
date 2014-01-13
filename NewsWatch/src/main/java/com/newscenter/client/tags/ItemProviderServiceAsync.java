package com.newscenter.client.tags;

import java.util.ArrayList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.newscenter.client.criteria.PageCriteria;


public interface ItemProviderServiceAsync 
{
	void getCategories(AsyncCallback callback);
	
	void getUserSelectionCategories(AsyncCallback callback);
	
	void updateUserItemSelection(TagItem tagItem, boolean selectionStatus, AsyncCallback callback);
	
	void updateUserItemSelection(ArrayList tagList, AsyncCallback callback);	
	
	void updateUserItemSelection(CategoryMap categoryMap, AsyncCallback callback);
	
	void refreshUserSelection(CategoryMap categoryMap, AsyncCallback callback);
	
	void updateSessionCategoryMap(CategoryMap categoryMap,PageCriteria criteria, int newsmode, AsyncCallback callback);
}
