package com.lighthouse.search.client.domain;

import java.util.ArrayList;
import java.util.HashMap;


import com.newscenter.client.news.NewsItems;

public class SearchResultList extends ArrayList<NewsItems> {

	/*
	 * private NewsItems newsitem;
	 * 
	 * public void setNewsitem(NewsItems newsitem) { this.newsitem = newsitem; }
	 * 
	 * public NewsItems getNewsitem() { return newsitem; }
	 */

//	private HashMap<String, Long> tagCloud = new HashMap<String, Long>();
	


	private int startIndex;
	private int totalPages;
	private int numItems;
	private int currentPageNo;

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setNumItems(int numItems) {
		this.numItems = numItems;
	}

	public int getNumItems() {
		return numItems;
	}

	public void setCurrentPageNo(int currentPageNo) {
		this.currentPageNo = currentPageNo;
	}

	public int getCurrentPageNo() {
		return currentPageNo;
	}

	/*public void setTagCloud(HashMap<String, Long> tagCloud) {
		this.tagCloud = tagCloud;
	}

	public HashMap<String, Long> getTagCloud() {
		return tagCloud;
	}
*/	

}
