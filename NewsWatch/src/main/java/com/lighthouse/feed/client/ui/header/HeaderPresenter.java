package com.lighthouse.feed.client.ui.header;

import com.google.gwt.core.client.GWT;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.lighthouse.feed.client.FeedEventBus;
import com.lighthouse.feed.client.service.FeedService;
import com.lighthouse.feed.client.service.FeedServiceAsync;
import com.lighthouse.feed.client.ui.header.IHeaderView.IHeaderPresenter;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;
/**
 * Presenting the header part of the Review feed
 * @author kiran@ensarm.com
 *
 */
@Presenter(view = HeaderView.class)
public class HeaderPresenter extends BasePresenter<HeaderView, FeedEventBus>
		implements IHeaderPresenter {

	@Override
	public void OnManageClick() {

		String urlQueryString = Window.Location.getQueryString();
		String url = GWT.getHostPageBaseURL() + "lhadmin.html" + urlQueryString;
		Window.open(url, "_self", null);

	}

	@Override
	public void OnNewsCenterClick() {

		String urlQueryString = Window.Location.getQueryString();
		String url = GWT.getHostPageBaseURL() + "LhMain.html" + urlQueryString;
		Window.open(url, "_self", null);

	}

	@Override
	public void OnLogoutClick() {
		removeFromSession();
	
	}
    /**
     * This method is responsible for remove the user from session and redirect to login module 
     */
	private void removeFromSession() {
		FeedServiceAsync serviceAsync = (FeedServiceAsync)GWT.create(FeedService.class);
		serviceAsync.removeFromSession(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				String urlQueryString = Window.Location.getQueryString();
				String url = GWT.getHostPageBaseURL() + "lhlogin.html" + urlQueryString;
				Window.Location.replace(url);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace(); 
				
			}
		});
	}

}
