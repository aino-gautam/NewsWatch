package com.lighthouse.search.client;


import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.lighthouse.search.client.ui.SearchResultPresenter;
import com.lighthouse.search.client.ui.SearchWidget;

public class SearchEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
       
       // SearchResultPresenter.setLoadingMessage("Searching....");
		SearchResultPresenter resultPresenter = new SearchResultPresenter();
		String searchStrng =Window.Location.getParameter("searchStr"); 
		String dateString = Window.Location.getParameter("dateStr");  
		
		SearchResultPresenter.SEARCHEDSTR=searchStrng;
		SearchResultPresenter.DATESTR=dateString;
	
		if(searchStrng != null && dateString != null){
			RootPanel.get().clear();
			resultPresenter.performSearch(searchStrng,dateString);
		  // Window.alert("in the search method");
	        RootPanel.get().add(resultPresenter);
		}
	}
}
