package com.newscenter.client.obsolete;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CategoryProviderServiceAsync 
{
	void getSelectionCategories(int industryId,AsyncCallback  callback);
	
	void getSelectionNewsItems(ArrayList arraylist,AsyncCallback  callback);
	
	void getSelectionItems(int parentId,AsyncCallback  callback);
	
	void getAllTags(int parentId, AsyncCallback  callback);
	
	void getNewsItemContent(int newsid, AsyncCallback  callback);

	public void validateUser(AsyncCallback  callback);
	
	public void removeFromSession(AsyncCallback  callback);
	
    void saveUserSelection(ArrayList list, AsyncCallback callback);
	
	public void getInformationFromSession(AsyncCallback  callback);
	
	public void getAllCategoryForIndustry(String industry,AsyncCallback  callback);
	
	public void getAllTagsForIndustry(String industry,AsyncCallback  callback);

	public void getisadminInformation(AsyncCallback  callback);
	
	public void getTagIdsToServeNewsItems(HashMap map,AsyncCallback  callback);
}