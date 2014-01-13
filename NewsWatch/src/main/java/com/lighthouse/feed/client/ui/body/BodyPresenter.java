package com.lighthouse.feed.client.ui.body;

import com.lighthouse.feed.client.FeedEventBus;
import com.lighthouse.feed.client.ui.body.IBodyView.IBodyPresenter;
import com.mvp4g.client.annotation.Presenter;

import com.mvp4g.client.presenter.BasePresenter;

/**
 * 
 * @author kiran@ensarm.com
 * 
 */
@Presenter(view = BodyView.class)
public class BodyPresenter extends BasePresenter<IBodyView, FeedEventBus>
		implements IBodyPresenter {

}
