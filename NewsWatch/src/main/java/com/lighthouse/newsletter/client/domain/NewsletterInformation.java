package com.lighthouse.newsletter.client.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lighthouse.group.client.domain.Group;
import com.lighthouse.report.client.domain.ReportItemList;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

/**
 * POJO for newsletter items like news, pulse, favorites, reports
 * @author nairutee
 *
 */
public class NewsletterInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HashMap<TagItem, List<NewsItems>> newsMap; // map of primary headline vs news item list
	private ReportItemList reportsList; // list of reports
	private List<NewsItems> favoriteItems;
	private List<NewsItems> mostReadInGroupNews; 
	private List<NewsItems> mostDiscussedInGroupNews;
	private List<NewsItems> mostReadInAllGroupsNews;
	private List<NewsItems> mostDiscussedInAllGroupsNews;
	private boolean noTagSelected = false;
	private Group favoriteGroup;
	
	public HashMap<TagItem, List<NewsItems>> getNewsMap() {
		return newsMap;
	}
	
	public void setNewsMap(HashMap<TagItem, List<NewsItems>> newsMap) {
		this.newsMap = newsMap;
	}

	public ReportItemList getReportsList() {
		return reportsList;
	}

	public void setReportsList(ReportItemList reportsList) {
		this.reportsList = reportsList;
	}

	public List<NewsItems> getFavoriteItems() {
		return favoriteItems;
	}

	public void setFavoriteItems(List<NewsItems> favoriteItems) {
		this.favoriteItems = favoriteItems;
	}

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
	
	public boolean isNoTagSelected() {
		return noTagSelected;
	}

	public void setNoTagSelected(boolean noTagSelected) {
		this.noTagSelected = noTagSelected;
	}

	public Group getFavoriteGroup() {
		return favoriteGroup;
	}

	public void setFavoriteGroup(Group favoriteGroup) {
		this.favoriteGroup = favoriteGroup;
	}
}
