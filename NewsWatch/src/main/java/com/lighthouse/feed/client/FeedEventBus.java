package com.lighthouse.feed.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.lighthouse.feed.client.ui.base.BaseMainPresenter;
import com.lighthouse.feed.client.ui.body.BodyPresenter;
import com.lighthouse.feed.client.ui.footer.FooterPresenter;
import com.lighthouse.feed.client.ui.header.HeaderPresenter;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.annotation.Start;
import com.mvp4g.client.event.EventBus;
/**
 * 
 * @author kiran@ensarm.com
 *
 */
@Events(startPresenter = BaseMainPresenter.class)
public interface FeedEventBus extends EventBus {
	
	@Start
	@Event( bind = { BodyPresenter.class, FooterPresenter.class, HeaderPresenter.class }, handlers = BaseMainPresenter.class )
	void start();

	/*
	 * Layout events
	 */
	@Event( handlers = BaseMainPresenter.class )
	void setBody( IsWidget body );
	/*
	@Event( handlers = HeaderPresenter.class )
	void ManageClick();

	@Event( handlers = HeaderPresenter.class )
	void NewsCenterClick( String name );*/
	

}
