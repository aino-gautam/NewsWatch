package com.lighthouse.group.client.event;

import java.util.HashMap;

import com.lighthouse.search.client.event.SearchEvent;
import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.BaseEvent;
import com.newscenter.client.events.BaseEventListener;
import com.newscenter.client.events.BaseEventStore;
import com.newscenter.client.events.NewsEvent;

public class GroupEventManager extends AppEventManager{
	
	private static GroupEventManager groupEventManager;
	private static HashMap<Integer, GroupEventManager> groupEventMgrMap = new HashMap<Integer, GroupEventManager>();
	protected static BaseEventStore<SearchEvent> searchEventStore = new BaseEventStore<SearchEvent>();
	
	
	private GroupEventManager(){
		
	}
	
	public static GroupEventManager getInstance(){
		if(groupEventManager == null)
			groupEventManager = new GroupEventManager();
			
		return groupEventManager;
	}
	
	@Override
	public <T extends BaseEvent> boolean fireEvent(T evt) {
		if ( evt instanceof SearchEvent)
			return searchEventStore.fireEvent((SearchEvent)evt);
		else
			return super.fireEvent(evt);
	}


	public static GroupEventManager getInstance(int id){
		if(groupEventMgrMap.containsKey(id))
			groupEventManager = groupEventMgrMap.get(id);
		else{
			groupEventManager = new GroupEventManager();
			groupEventMgrMap.put(id, groupEventManager);
		}
		return groupEventManager;
	}

	@Override
	public void addNewsEventListener(BaseEventListener<NewsEvent> newsevtListener){
		newsEventStore.addEventListener(newsevtListener);
	}
	
	public void addSearchEventHandler(BaseEventListener<SearchEvent> searchEvtListener){
		searchEventStore.addEventListener(searchEvtListener);
	}

	public void removeSearchEventHandler(BaseEventListener<SearchEvent> searchEvtlistener){
		searchEventStore.removeEventListener(searchEvtlistener);
	}
}
