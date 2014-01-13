package com.lighthouse.report.client.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public class ReportItemList extends ArrayList<ReportItem>{
	private int startIndex;
	private int totalPages;
	private boolean redirect = false;
	private boolean noTagSelected = false;
	private int numItems;
	private int currentPageNo;
	private boolean tagNews = false;
	private boolean andNews = false;
	
	public ReportItemList() {
	}

	/**
	 * Sorts the news items list received from the Item store according to the latest published news items
	 * @return an arraylist of news items sorted according to the date of publishing of the news items.
	 */
	public void sortList(){
		final Comparator<ReportItem> LATESTUPDATED_ORDER = new Comparator<ReportItem>(){

			public int compare(ReportItem n1, ReportItem n2) {
				
				return n2.getNewsDate().compareTo(n1.getNewsDate());
			}
		};
		
		Collections.sort(this,LATESTUPDATED_ORDER);
	}
	
	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	
	public boolean isRedirect() {
		return redirect;
	}

	public void setRedirect(boolean redirect) {
		this.redirect = redirect;
	}

	public boolean isNoTagSelected() {
		return noTagSelected;
	}

	public void setNoTagSelected(boolean noTagSelected) {
		this.noTagSelected = noTagSelected;
	}
	
	public int getNumItems() {
		return numItems;
	}

	public void setNumItems(int numItems) {
		this.numItems = numItems;
	}

	public int getCurrentPageNo() {
		return currentPageNo;
	}

	public void setCurrentPageNo(int currentPage) {
		this.currentPageNo = currentPage;
	}

	public boolean isTagNews() {
		return tagNews;
	}

	public void setTagNews(boolean tagNews) {
		this.tagNews = tagNews;
	}

	public boolean isAndNews() {
		return andNews;
	}

	public void setAndNews(boolean andNews) {
		this.andNews = andNews;
	}

}
