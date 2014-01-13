package com.newscenter.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.newscenter.client.news.NewsProviderService;
import com.newscenter.client.news.NewsProviderServiceAsync;
import com.newscenter.client.tags.ItemProviderService;
import com.newscenter.client.tags.ItemProviderServiceAsync;

public class ServiceUtils {

	private static NewsProviderServiceAsync newsProviderAsync = null;
	private static ItemProviderServiceAsync itemProviderAsync = null;
	
	public static NewsProviderServiceAsync getNewsProviderServiceAsync(){
		/*if(newsProviderAsync != null)
			return newsProviderAsync;*/
		
		newsProviderAsync = (NewsProviderServiceAsync) GWT.create(NewsProviderService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) newsProviderAsync;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "NewsSelection";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		return newsProviderAsync;
	}
	
	public static ItemProviderServiceAsync getItemProviderServiceAsync(){
		if(itemProviderAsync != null)
			return itemProviderAsync;
		
		itemProviderAsync = (ItemProviderServiceAsync) GWT.create(ItemProviderService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) itemProviderAsync;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "TagSelection";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		return itemProviderAsync;
		
	}
}
