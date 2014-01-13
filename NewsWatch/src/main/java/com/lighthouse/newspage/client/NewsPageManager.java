package com.lighthouse.newspage.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.newspage.client.domain.CommentedNewsItem;
import com.lighthouse.newspage.client.service.NewsItemHomeService;
import com.lighthouse.newspage.client.service.NewsItemHomeServiceAsync;
import com.lighthouse.newspage.client.ui.NewsItemHomeFlexPresenter;
import com.lighthouse.newspage.client.ui.NewsItemHomePresenter;
import com.lighthouse.newspage.client.ui.NewsItemHomeWidget;

/**
 * 
 * @author kiran@ensarm.com
 * 
 */
public class NewsPageManager {


	private LhUser lhUser;
	
	public NewsPageManager(LhUser lhUser){
		this.lhUser = lhUser;
	}
	
	/**
	 * loads news content for the id
	 * @param newsId
	 */
	public void loadNewsItemContent(int newsId) {

		NewsItemHomeServiceAsync newsItemHomeService = (NewsItemHomeServiceAsync) GWT
				.create(NewsItemHomeService.class);
		newsItemHomeService.loadNewsItemContent(newsId,
				new AsyncCallback<CommentedNewsItem>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(CommentedNewsItem result) {

						NewsItemHomeWidget itemHomeWidget = new NewsItemHomeWidget(result, lhUser);
						itemHomeWidget.createNewsItemWidgetUI();

						NewsItemHomePresenter newsItemHomePresenter = new NewsItemHomePresenter(itemHomeWidget, result);
						
						/*CommentedNewsItem commentedNewsItem = new CommentedNewsItem();
						commentedNewsItem.setCommentsList(result.getCommentsList());*/
						
						
						NewsItemHomeFlexPresenter homeFlexPresenter = new NewsItemHomeFlexPresenter(newsItemHomePresenter);
						DOM.setStyleAttribute(homeFlexPresenter.getElement(), "marginRight", "5px");
						homeFlexPresenter.setStylePrimaryName("NCMainBasePanel");
						RootPanel.get().add(homeFlexPresenter,70,10);
					}

				});

	}
}
