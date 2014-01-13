package com.newscenter.client.events;

public class NewsStoreEvent extends BaseEvent {

	public static final int NEWSITEMSAVAILABLE = 1;
	
	public NewsStoreEvent(Object source, int evttype, Object data){
		super(source);
		eventType = evttype;
		eventData = data;
	}
	public NewsStoreEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

}
