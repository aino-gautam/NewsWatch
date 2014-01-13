package com.lighthouse.statistics.client.domain;

import java.io.Serializable;
import java.util.List;

import com.newscenter.client.news.NewsItems;

public class NewsStatistics implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<NewsItems> mostReadInGroupNews;
	private List<NewsItems> mostDiscussedInGroupNews;
	private List<NewsItems> mostReadInAllGroupsNews;
	private List<NewsItems> mostDiscussedInAllGroupsNews;
	
	public List<NewsItems> getMostReadInGroupNews() {
		return mostReadInGroupNews;
	}
	
	public void setMostReadInGroupNews(List<NewsItems> mostReadInGroupNews) {
		this.mostReadInGroupNews = mostReadInGroupNews;
	}

	public List<NewsItems> getMostDiscussedInGroupNews() {
		return mostDiscussedInGroupNews;
	}

	public void setMostDiscussedInGroupNews(List<NewsItems> mostDiscussedInGroupNews) {
		this.mostDiscussedInGroupNews = mostDiscussedInGroupNews;
	}

	public List<NewsItems> getMostReadInAllGroupsNews() {
		return mostReadInAllGroupsNews;
	}

	public void setMostReadInAllGroupsNews(List<NewsItems> mostReadInAllGroupsNews) {
		this.mostReadInAllGroupsNews = mostReadInAllGroupsNews;
	}

	public List<NewsItems> getMostDiscussedInAllGroupsNews() {
		return mostDiscussedInAllGroupsNews;
	}

	public void setMostDiscussedInAllGroupsNews(
			List<NewsItems> mostDiscussedInAllGroupsNews) {
		this.mostDiscussedInAllGroupsNews = mostDiscussedInAllGroupsNews;
	}
}
