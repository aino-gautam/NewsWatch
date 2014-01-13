package com.lighthouse.search.client.event;

import com.newscenter.client.events.BaseEvent;

public class SearchEvent extends BaseEvent {
	
	/**This event gets fired search is initiated when the search button is clicked*/
	public final static int SEARCHINITIATED = 1;
	
	
	/**This event gets fired when the search is completed and the search result is available*/
	public final static int  SEARCHCOMPLETED = 2;
	
	/**This event gets fired when we want to return to group manager*/
	public final static int SHOWGROUPS = 3;
	
	/**This event gets fired when we want to show the search results*/
	public final static int SHOWSEARCHRESULTS = 4;
	

	public SearchEvent(Object source, int type, Object data) {
		super(source, type, data);
		eventType = type;
		eventData = data;
	}
	
	public SearchEvent(Object source) {
		super(source);
	}


}
