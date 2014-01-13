package com.newscenter.client.events;

import com.google.gwt.core.client.GWT;

/**
 * This class is an event manager class. It consists of BaseEventStores specific to the various events. This 
 * class is used to register eventListeners for the various events and also fire events.
 *
 */
public class AppEventManager {
	
	private BaseEventStore<TagEvent> tagEventStore = new BaseEventStore<TagEvent>();
	protected BaseEventStore<NewsEvent> newsEventStore = new BaseEventStore<NewsEvent>();
	private static AppEventManager globalEventManager;
	
	public AppEventManager(){
		
	}
	public static AppEventManager getInstance(){
		if(globalEventManager == null)
			globalEventManager = GWT.create(AppEventManager.class);
		
		return globalEventManager;
	}
	
	public void addTagEventListener(BaseEventListener<TagEvent> tagevtlistener){
		tagEventStore.addEventListener(tagevtlistener);
	}
	
	public void removeTagEventListener(BaseEventListener<TagEvent> tagevtlistener){
		tagEventStore.removeEventListener(tagevtlistener);
	}
		
	public void addNewsEventListener(BaseEventListener<NewsEvent> newsevtListener){
		newsEventStore.addEventListener(newsevtListener);
	}
	
	public void removeNewsEventListener(BaseEventListener<NewsEvent> newsevtListener){
		newsEventStore.removeEventListener(newsevtListener);
	}
	
	/**
	 * 
	 * @param <T> - extends BaseEvent i.e any event class which extends BaseEvent Class
	 * @param evt - event of type T
	 * @return - true if the event listener who had heard that type of event doesn't want any other
	 * event listener after it to listen to this event.Otherwise false
	 */
	public <T extends BaseEvent> boolean fireEvent(T evt){
		if ( evt instanceof NewsEvent)
			return newsEventStore.fireEvent((NewsEvent)evt);
		else if (evt instanceof TagEvent)
			return tagEventStore.fireEvent((TagEvent)evt);
		
		return false ;
	}
}
