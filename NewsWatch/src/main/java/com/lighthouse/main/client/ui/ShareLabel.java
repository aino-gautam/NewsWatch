package com.lighthouse.main.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.newscenter.client.news.NewsItems;

/**
 * 
 * @author kiran@ensarm.com
 * 
 */
public class ShareLabel extends Label implements ClickHandler, MouseOutHandler,
		MouseOverHandler {

	private NewsItems newsItems;
	public ShareLabel() {

	}

	public ShareLabel(NewsItems newsItems) {
		setNewsItems(newsItems);
		addClickHandler(this);
		addMouseOutHandler(this);
		addMouseOverHandler(this);

	}
	
	@Override
	public void onClick(ClickEvent event) {
		
		Widget arg0=(Widget) event.getSource();
		NewsItems newsItems = getNewsItems();
		ShareLabel label = (ShareLabel) arg0;
		ShareStoryPopup shareStoryPopup = new ShareStoryPopup(newsItems); 
		shareStoryPopup.setGlassEnabled(true);
		shareStoryPopup.show();
		shareStoryPopup.center();
		
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		// TODO Auto-generated method stub

	}

	public NewsItems getNewsItems() {
		return newsItems;
	}

	public void setNewsItems(NewsItems newsItems) {
		this.newsItems = newsItems;
	}

}
