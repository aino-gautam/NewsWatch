package com.newscenter.client.events;

public class TagEvent extends BaseEvent {

	/**
	 * type of event fired when a tag is selected. It is fired by the ItemTagLabel class.The tag presenter listens to this
	 */
	public final static int TAGSELECTED = 1;
	
	/**
	 * type of event fired when a tag is deselected.It is fired by the ItemTagLabel class. The tag presenter listens to this
	 */
	public final static int TAGDESELECTED = 2;
	
	/**
	 * type of event fired when the tags are available from the db. Fired by the Item store once it receives all the
	 * user selection category map. The tag presenter listens to it
	 */
	public final static int TAGSAVAILABLE = 3;
	
	public final static int CATEGORYSELECTED = 4 ;
	
	/**
	 * fired when the user tries to toggle(spin) the categories from the snippet title. The Snippet Title fires
	 * this event and the tag presenter listens to it.
	 */
	public final static int UPCATEGORYCHANGED	 = 5;
	
	/**
	 * fired when the user clicks on "Select All" label. That particular category and all the tags under it are
	 * selected. The ItemTagLabel fires this event and SnippetDetail listens to it.
	 */
	public final static int SELECTCATEGORYITEM = 6;
	
	/**
	 * fired when the user clicks on "Deselect All" label. That particular category and all the tags under it are
	 * deselected. The ItemTagLabel fires this event and SnippetDetail listens to it.
	 */
	public final static int DESELECTCATEGORYITEM = 7;
	
	/**
	 * fired when the user clicks on "..." label. That particular category is shown in the category view in
	 * the tagpresenter. The ItemTagLabel fires this event and tagpresenter listens to it.
	 */
	public final static int VIEWCATEGORY = 8;
	
	/**
	 * fired when the user clicks on "back" label. The tagpresenter goes back to the snippet view from the category
	 * view. The ItemTagLabel fires this event and tagpresenter listens to it.
	 */
	public final static int VIEWSNIPPETS = 9;
	
	/**
	 * fired when the user clicks outside the tag presenter. the tag presenter should listen to this event and 
	 * minimze itself.
	 */
	public final static int TAGPRESENTERMINIMIZED = 10;
	
	/**
	 * fired when the there are no news to display in the news presenter so that the user and veiw tags.
	 *  the tag presenter should listen to this event and maximize itself.
	 */
	public final static int TAGPRESENTERMAXIMIZED = 11;
	
	/**
	 * fired when the user clicks on select all in the snippet or category view. the news presenter should listen to it and display news which 
	 * are applied to that category(i.e. all tags under that category)
	 */
	public final static int CATEGORYITEMSELECTED = 12;
	
	/**
	 * fired when the user clicks on deselect all in the snippet or category view. the newspresenter should listen to it and remove the news items
	 * from the list of news which ar displayed to the user which have the tags under the category applied to them
	 */
	public final static int CATEGORYITEMDESELECTED = 13;
	

	public final static int DOWNCATEGORYCHANGED = 14;
	
	public final static int CLEARTAGS = 15;
	
	public TagEvent(Object source, int evttype, Object data) {
		super(source);
		eventType = evttype;
		eventData = data;
	}
	
	public TagEvent(Object source) {
		super(source);
	}

}
