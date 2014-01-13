package com.newscenter.client.obsolete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.newscenter.client.obsolete.*;
import com.newscenter.client.news.NewsProviderService;
import com.newscenter.client.news.NewsProviderServiceAsync;
import com.newscenter.client.tags.CategoryMap;

public class NewsItemStore extends Composite implements ItemSelectionEventListener 
{
	//private NewsMapForTags newsmap = new NewsMapForTags();
	private ArrayList arrayoftagids = new ArrayList();
	public static ArrayList newscollection = new ArrayList();
	private NewsPresenterMain newspresenter;
	private EventStore objEventStore = new EventStore();
	
	
	public NewsItemStore()
	{
		//arrayoftagids = newsmap.gettagids();
		//arrayoftagids.add(5);
		//arrayoftagids.add(6);
		getnewsitemsforuser(arrayoftagids);
	}
	public NewsItemStore(NewsPresenterMain newsPresenterMain)
	{
		
		objEventStore.setItemSelectionEventListener(this);
		newspresenter = newsPresenterMain;
		//arrayoftagids.add(5);
		//arrayoftagids.add(6);
		getuserselectedtagid();
		getnewsitemsforuser(arrayoftagids);
		
	}
	public void getnewsitemsforuser(ArrayList arrayoftagids)
	{
		NewsProviderServiceAsync service = (NewsProviderServiceAsync)GWT.create(NewsProviderService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget)service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "NewsSelection";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		AsyncCallback callback = new AsyncCallback(){
			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
				System.out.println("There is some prob in receving newsitems");
			}

			public void onSuccess(Object result) {
				try{
					ArrayList arrayref = new ArrayList();
					arrayref = (ArrayList)result;
					setNewscollection(arrayref);
					newspresenter.getnewsitemcontent();
					
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("Problem in getnewsitemsforuser()");
				}	
			}
		};
		//service.getnewsitemsforuser(arrayoftagids, callback);
		
	}
	public static ArrayList getNewscollection() {
		return newscollection;
	}
	public static void setNewscollection(ArrayList newscollection) {
		NewsItemStore.newscollection = newscollection;
	}
	public void getuserselectedtagid()
	{
		NewsProviderServiceAsync service = (NewsProviderServiceAsync)GWT.create(NewsProviderService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget)service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "NewsSelection";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		AsyncCallback callback = new AsyncCallback(){
			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
				System.out.println("There is some prob in receving tagids");
			}

			public void onSuccess(Object result) {
				try{
					ArrayList arraylistoftags = (ArrayList)result;
					Iterator iter = arraylistoftags.iterator();
					while(iter.hasNext())
					{
						int tagid = (Integer)iter.next();
						arrayoftagids.add(tagid);
					}
					getnewsitemsforuser(arrayoftagids);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("Problem in getuserselectedtagid()");
				}	
			}
		};
		//service.getuserselectedtagid(callback);
	}
	
	public boolean itemSelectedEvent(ItemSelectionEvent evt) {
		
		arrayoftagids.clear();
		HashMap tagmap = (HashMap)evt.getEventData();
		for(Object obj:tagmap.keySet())
		{
			int tagid = (Integer)obj;
			String tagname = (String)tagmap.get(obj);
			arrayoftagids.add(tagid);
		}
		getnewsitemsforuser(arrayoftagids);
		//Window.alert("IN NewsItemStoreclass");
		return false;
	}
	
	
}
