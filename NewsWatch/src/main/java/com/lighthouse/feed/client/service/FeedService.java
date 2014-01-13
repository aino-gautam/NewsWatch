package com.lighthouse.feed.client.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.lighthouse.feed.client.domain.Feed;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
@RemoteServiceRelativePath("feedservice")
public interface FeedService extends RemoteService {

	ArrayList<Feed> getFeedUrlForCatalyst(Integer ncid);
	
	HashMap<String,ArrayList<NewsItems>> syncFeed(String url);
	
	NewsItemList getFeedSourceNewsItems(String feedSourceUrl,
			String feedSourceName, Integer ncid);
	
	NewsItemList getFeedItemsForReview(Integer ncid);
	ArrayList getCategoryNames(int industryid, String industryName);

	ArrayList fillprimaryTaglist(int industryId, String industryName);
	public ArrayList getTagNames(String industryName,String categoryName);
		
	public TagItem markAsTopNews(Long newsId,Integer ncid,boolean markStatus); 
	
	NewsItemList getTopFeedNews(Integer ncid);
	
	public boolean deleteFeedNews(Long newsId);
	
	public void removeFromSession();
	
	public String getSiloLogo();
	
	String getLastEditorialNewsletterDelivery(int ncid);
	
}
