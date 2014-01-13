package com.lighthouse.feed.client.domain;

import java.io.Serializable;

public class Feed implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String feedName;
	private String feedDescription;
	private String feedUrl;
	private String feedPTag;
	private long feedPollFrequency;
	private String feedProcessor;
	
	public String getFeedName() {
		return feedName;
	}
	
	public void setFeedName(String feedName) {
		this.feedName = feedName;
	}
	
	public String getFeedDescription() {
		return feedDescription;
	}
	
	public void setFeedDescription(String feedDescription) {
		this.feedDescription = feedDescription;
	}
	
	public String getFeedUrl() {
		return feedUrl;
	}
	
	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}
	
	public long getFeedPollFrequency() {
		return feedPollFrequency;
	}
	
	public void setFeedPollFrequency(long feedPollFrequency) {
		this.feedPollFrequency = feedPollFrequency;
	}
	
	public String getFeedProcessor() {
		return feedProcessor;
	}

	public void setFeedProcessor(String feedProcessor) {
		this.feedProcessor = feedProcessor;
	}

	public String getFeedPTag() {
		return feedPTag;
	}

	public void setFeedPTag(String feedPTag) {
		this.feedPTag = feedPTag;
	}
	
}
