package com.lighthouse.feed.client.ui.footer;

import com.lighthouse.feed.client.FeedEventBus;
import com.lighthouse.feed.client.ui.footer.IFooterView.IFooterPresenter;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;
/**
 * 
 * @author kiran@ensarm.com
 *
 */
@Presenter(view = FooterView.class)
public class FooterPresenter extends BasePresenter<FooterView, FeedEventBus > implements IFooterPresenter{

}
