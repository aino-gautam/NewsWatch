package com.newscenter.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.NewsEvent;
import com.newscenter.client.tags.TagItem;

public class AppliedTagsLabel extends Label implements ClickHandler,MouseOutHandler,MouseOverHandler {
	
	private TagItem tagItem;
	
	public AppliedTagsLabel(){
		
	}

	public AppliedTagsLabel(TagItem item){
		setTagItem(item);
		addClickHandler(this);
		addMouseOutHandler(this);
		addMouseOverHandler(this);
		
	}

	public TagItem getTagItem() {
		return tagItem;
	}

	public void setTagItem(TagItem tagItem) {
		this.tagItem = tagItem;
	}


	public void onMouseDown(Widget arg0, int arg1, int arg2) {
		
	}

	/*public void onMouseEnter(Widget arg0) {
		AppliedTagsLabel label = (AppliedTagsLabel)arg0;
		label.setStylePrimaryName("lblRelatedTagHover");
	}*/

	/*public void onMouseLeave(Widget arg0) {
		AppliedTagsLabel label = (AppliedTagsLabel)arg0;
		label.setStylePrimaryName("lblRelatedTag");
	}*/

	public void onMouseUp(Widget arg0, int arg1, int arg2) {
	
	}

	/*public void onClick(Widget arg0) {
		MainNewsPresenter.setLoadingMessage("Fetching News...");
		AppliedTagsLabel label = (AppliedTagsLabel)arg0;
		TagItem tag = label.getTagItem();
		NewsEvent evt = new NewsEvent(this,NewsEvent.NEWSTAGSELECTED, tag);
		AppEventManager.getInstance().fireEvent(evt);
	}*/

	

	@Override
	public void onClick(ClickEvent event) {
		Widget arg0=(Widget)event.getSource();
		MainNewsPresenter.setLoadingMessage("Fetching News...");
		AppliedTagsLabel label = (AppliedTagsLabel)arg0;
		TagItem tag = label.getTagItem();
		NewsEvent evt = new NewsEvent(this,NewsEvent.NEWSTAGSELECTED, tag);
		AppEventManager.getInstance().fireEvent(evt);
		
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		Widget arg0=(Widget)event.getSource();
		AppliedTagsLabel label = (AppliedTagsLabel)arg0;
		label.setStylePrimaryName("lblRelatedTagHover");
		
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		Widget arg0=(Widget)event.getSource();
		AppliedTagsLabel label = (AppliedTagsLabel)arg0;
		label.setStylePrimaryName("lblRelatedTag");
	}

	
}
