package com.newscenter.client.obsolete;

import com.google.gwt.user.client.Random;

public class ItemSelectionEvent extends java.util.EventObject
{
	protected String historyToken = null;
	protected String eventType = null;
	protected Object eventData = null;

	public ItemSelectionEvent(Object source, String type, Object data) {
	super(source);
	eventType = type;
	eventData = data;
	updateHistoryToken();
	// TODO Auto-generated constructor stub
	}

	public ItemSelectionEvent(Object source) {
	super(source);
	setHistoryToken(source.getClass().getName());
	// TODO Auto-generated constructor stub
	}

	public String getHistoryToken() {
	return historyToken;
	}

	public void setHistoryToken(String token) {

	historyToken = token;
	}

	public String getEventType() {
	return eventType;
	}

	public void setEventType(String eventType) {
	this.eventType = eventType;
	updateHistoryToken();
	}

	public Object getEventData() {
	return eventData;
	}

	public void setEventData(Object data) {
	this.eventData = data;
	updateHistoryToken();
	}

	private void updateHistoryToken() {
	int random = Random.nextInt();
	setHistoryToken(new Integer(random).toString());
	}
	}

	


