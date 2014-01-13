package com.lighthouse.feed.client.domain;

import java.io.Serializable;

import com.newscenter.client.news.NewsItems;

public class FeedNewsItem extends NewsItems implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long feedId;
	private String feedContent;
	private String feedSourceUrl;
	private String reportUrl;
	
	public long getFeedId() {
		return feedId;
	}
	public void setFeedId(long feedId) {
		this.feedId = feedId;
	}
	public String getFeedContent() {
		return feedContent;
	}
	public void setFeedContent(String feedContent) {
		this.feedContent = feedContent;
	}
	public String getFeedSourceUrl() {
		return feedSourceUrl;
	}
	public void setFeedSourceUrl(String feedSourceUrl) {
		this.feedSourceUrl = feedSourceUrl;
	}
	public String getReportUrl() {
		return reportUrl;
	}
	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
	
	

}
