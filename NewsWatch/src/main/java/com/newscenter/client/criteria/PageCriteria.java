package com.newscenter.client.criteria;

/**
 * The PageCriteria class is used to set the pageSize of the MainNewsPresenter i.e the number of NewsItems
 * that will be visible to the user at a time and based on that setting the start index.
 * @author nairutee
 *
 */
public class PageCriteria extends Criteria {

	private int startIndex = -1;
	private int pageSize = -1;
	private int minPageSize;
	private int maxPageSize;
	private int currentPage = 1;

	public int getMinPageSize() {
		return minPageSize;
	}

	public void setMinPageSize(int minPageSize) {
		this.minPageSize = minPageSize;
	}

	public int getMaxPageSize() {
		return maxPageSize;
	}

	public void setMaxPageSize(int maxPageSize) {
		this.maxPageSize = maxPageSize;
	}

	
	public PageCriteria(int startindex, int pagesize){
		setStartIndex(startindex);
		setPageSize(pagesize);
	}
	
	public PageCriteria(){
		
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
}
