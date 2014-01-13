package com.newscenter.client.events;

public class ItemStoreEvent extends BaseEvent {

	public static final int CATEGORYMAPUPDATED = 1;
	
	public ItemStoreEvent(Object source, int evttype, Object data){
		super(source);
		eventType = evttype;
		eventData = data;
	}
	public ItemStoreEvent(Object source) {
		super(source);
	}
}
