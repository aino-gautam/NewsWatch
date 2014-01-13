package com.lighthouse.main.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Label;
import com.lighthouse.login.user.client.domain.LhUser;
import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.NewsEvent;
import com.newscenter.client.tags.TagItem;

public class PrimaryCategoryLabel extends Label implements ClickHandler,
		MouseOutHandler, MouseOverHandler {

	private LhUser lhUser;
	private TagItem tagItem;

	public PrimaryCategoryLabel(TagItem tag, LhUser lhUser) {

		super(tag.getTagName());
		setTitle("Click here to read more about " + tag.getTagName());
		this.lhUser = lhUser;
		setTagItem(tag);
		addClickHandler(this);
		addMouseOutHandler(this);
		addMouseOverHandler(this);
		setStylePrimaryName("primaryTagLabel");

	}

	public TagItem getTagItem() {
		return tagItem;
	}

	public void setTagItem(TagItem tagItem) {
		this.tagItem = tagItem;
	}

	@Override
	public void onClick(ClickEvent event) {

		NewsEvent evt = new NewsEvent(this, NewsEvent.NEWSTAGSELECTED,
				getTagItem());
		AppEventManager.getInstance().fireEvent(evt);

	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		addStyleName("primaryTagLabelHover");
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		removeStyleName("primaryTagLabelHover");
	}

}
