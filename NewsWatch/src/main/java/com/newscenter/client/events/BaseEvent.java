package com.newscenter.client.events;

public class BaseEvent extends java.util.EventObject{
	
	protected int eventType;;
	protected Object eventData = null;
	protected boolean historyEnabled;
	
	public BaseEvent(Object source, int type, Object data) {
		super(source);
		eventType = type;
		eventData = data;
	}
	public BaseEvent(Object source) {
		super(source);
		
	}
	
	public int getEventType() {
		return eventType;
	}
	
	public void setEventType(int eventType) {
		this.eventType = eventType;
	}
	
	public Object getEventData() {
		return eventData;
	}
	
	public void setEventData(Object eventData) {
		this.eventData = eventData;
	}
	public boolean isHistoryEnabled() {
		return historyEnabled;
	}
	public void setHistoryEnabled(boolean historyEnabled) {
		this.historyEnabled = historyEnabled;
	}
	
}
