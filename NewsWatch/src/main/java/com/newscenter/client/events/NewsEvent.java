package com.newscenter.client.events;

public class NewsEvent extends BaseEvent {

	/**
	 * fired when a newsitem is selected
	 */
	public final static int NEWSITEMSELECTED = 1;
	
	/**
	 * fired when a newsitem is deselected
	 */
	public final static int NEWSITEMDESELECTED = 2;

	/**
	 * fired when the news have been fetched and are available with the news store. The MainNewsPresenter listens 
	 * to this event while the NewsStore fires it.
	 */
	public final static int NEWSAVAILABLE = 3;
	
	/**
	 * fired when a tag is selected from the list of applied tags of a particular news item. The newspresenter should 
	 * listen to it and present the news related to that tag.
	 */
	public final static int NEWSTAGSELECTED = 4;
	
	
	/**
	 * fired when a user deselects a tag item resulting in the deletion of that news item from the new
	 * presenter
	 */
	public final static int NEWSDELETED =  5;
	
	/**
	 * fired when the news have been fetched as per the saved user selections and are available with the news store. The MainNewsPresenter listens 
	 * to this event while the NewsStore fires it.
	 */
	public final static int NEWSARRIVED = 6;
	
	/**
	 * fired when tag presenter is minimized or maximized
	 * 
	 */
	public final static int REFRESHNEWS = 7;
	
	/**
	 * fired when the page requested by the newspresenter is available with the news store. The MainNewsPresenter listens to this event while the
	 * NewsStore fires it.
	 */
	public final static int PAGEAVAILABLE = 8;
	
	/**
	 * fired when the previous page requested by the newspresenter is available with the news store. The MainNewsPresenter listens tot his event while
	 * the NewsStore fires it.
	 */
	public final static int PREVPAGEAVAILABLE = 9;
	
	/**
	 * fired when the news have been fetched as per the applied tag selected and are available with the news store. The MainNewsPresenter listens 
	 * to this event while the NewsStore fires it.
	 */
	public final static int TAGNEWSARRIVED = 10;
	
	public final static int ANDNEWSAVAILABLE = 11;
	
	public NewsEvent(Object source, int evttype, Object data){
		super(source);
		eventType = evttype;
		eventData = data;
	}
	public NewsEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	
}
