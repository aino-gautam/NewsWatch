package com.lighthouse.feed.server;

import java.util.ArrayList;

import com.lighthouse.feed.client.domain.FeedNewsItem;

public interface FeedProcessor {

	public String getContentToBeParsed(String url);
	
	public ArrayList<FeedNewsItem> parseContent( String feedContent, String feedName );
}
