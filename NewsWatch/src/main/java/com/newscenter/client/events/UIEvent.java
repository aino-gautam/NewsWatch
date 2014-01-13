package com.newscenter.client.events;

public class UIEvent extends BaseEvent {

	public final static int VIEWSNIPPETS = 1;
	public final static int VIEWCATEGORY = 2;
	
	public UIEvent(Object source, int evttype, Object data){
		super(source);
		eventType = evttype;
		eventData = data;
	}
	public UIEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

}
