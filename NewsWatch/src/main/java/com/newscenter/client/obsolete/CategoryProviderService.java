package com.newscenter.client.obsolete;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CategoryProviderService extends RemoteService 
{
	public HashMap<Integer, String> getSelectionCategories(int industryId);
	
	public HashMap<Integer, SelectionItem> getSelectionItems(int parentId);
	
	public ArrayList getSelectionNewsItems(ArrayList arraylist);
	
	public HashMap<Integer, SelectionItem> getAllTags(int parentId);
	
	public HashMap<Integer, NewsSelectionItem> getNewsItemContent(int newsid);

	public boolean validateUser();
	
	public void removeFromSession();
	
	public void saveUserSelection(ArrayList list);
	
	public ArrayList getInformationFromSession();
	
	public ArrayList getAllCategoryForIndustry(String industry);
	
	public ArrayList getAllTagsForIndustry(String industry);
	
	public int getisadminInformation();
	
	public ArrayList getTagIdsToServeNewsItems(HashMap map);
	
}