package com.lighthouse.feed.client.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lighthouse.feed.client.domain.Feed;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

public interface FeedServiceAsync {

	void getFeedUrlForCatalyst(Integer ncid, AsyncCallback<ArrayList<Feed>> callback);

	void syncFeed(String url, AsyncCallback<HashMap<String, ArrayList<NewsItems>>> callback);

	void getFeedSourceNewsItems(String feedSourceUrl,String feedSourceName,Integer ncid,AsyncCallback<NewsItemList> callback);

	void getFeedItemsForReview(Integer ncid,
			AsyncCallback<NewsItemList> callback);
	
	void markAsTopNews(Long newsId, Integer ncid, boolean markStatus,
			AsyncCallback<TagItem> callback);
	
	void getCategoryNames(int industryid, String industryName,
			AsyncCallback<ArrayList> callback);
	void fillprimaryTaglist(int industryId, String industryName,
			AsyncCallback<ArrayList> callback);

	void getTagNames(String industryName, String categoryName,
			AsyncCallback<ArrayList> callback);

	void getTopFeedNews(Integer ncid, AsyncCallback<NewsItemList> callback);

	void deleteFeedNews(Long newsId, AsyncCallback<Boolean> callback);

	void removeFromSession(AsyncCallback<Void> callback);

	void getSiloLogo(AsyncCallback<String> callback);

	void getLastEditorialNewsletterDelivery(int ncid,
			AsyncCallback<String> callback);

}
