package com.newscenter.client.news;

import java.io.Serializable;
import java.util.ArrayList;

import com.newscenter.client.tags.TagItem;

/**
 * This class is used to represent one news record of the database.
 *
 */
public class NewsItems implements Serializable {
	
	private int newsId;
	private String abstractNews="";
	private String url;
	private String newsDate;
	private String newsUploadedAt;
	private String newsTitle="";
	private String imageUrl="";
	private String newsContent="";
	private String newsSource="";
	private ArrayList<TagItem> associatedTagList = new ArrayList<TagItem>();
	private int commentsCount;
	private int viewsCount;
	private int isLocked;
	private int isReportItem; 
	private String newsPriority="";
	private boolean isMarkedAsTop;
	private boolean displayFullAbstract;
	private String author;

	/**
	 * This method is used to compare two news items to check which one of them is latest by date so 
	 * as to insert the news item at a proper position while sorting.
	 * @param newsitem - the newsitem with which the newsitem which calls the method will be compared
	 * @return - true if the passed news item object is latest than the one which calls the method else
	 * return false
	 */
	/*public boolean compareLatestNewsItem(NewsItems newsitem){
		return this.getNewsDate().after(newsitem.getNewsDate());
	}*/
	
	public boolean isMarkedAsTop() {
		return isMarkedAsTop;
	}

	public void setMarkedAsTop(boolean isMarkedAsTop) {
		this.isMarkedAsTop = isMarkedAsTop;
	}
	
	public void addTagforNews(TagItem tagitem){
		associatedTagList.add(tagitem);
	}
	
	public void removeTagforNews(TagItem tagitem){
		associatedTagList.remove(tagitem);
	}
	public int getNewsId() {
		return newsId;
	}
	
	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}
	
	public String getAbstractNews() {
		return abstractNews;
	}
	
	public void setAbstractNews(String abstractNews) {
		this.abstractNews = abstractNews;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	public String getNewsDate() {
		return newsDate;
	}
	
	public void setNewsDate(String newsDate) {
		this.newsDate = newsDate;
	}
	
	public String getNewsTitle() {
		return newsTitle;
	}
	
	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getNewsContent() {
		return newsContent;
	}
	
	public void setNewsContent(String newsContent) {
		this.newsContent = newsContent;
	}

	public ArrayList<TagItem> getAssociatedTagList() {
		return associatedTagList;
	}

	public void setAssociatedTagList(ArrayList<TagItem> associatedTagList) {
		this.associatedTagList = associatedTagList;
	}


	public String getNewsSource() {
		return newsSource;
	}


	public void setNewsSource(String newsSource) {
		this.newsSource = newsSource;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public int getViewsCount() {
		return viewsCount;
	}

	public void setViewsCount(int viewsCount) {
		this.viewsCount = viewsCount;
	}
	
	public int getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(int isLocked) {
		this.isLocked = isLocked;
	}

	public String getNewsPriority() {
		return newsPriority;
	}

	public void setNewsPriority(String newsPriority) {
		this.newsPriority = newsPriority;
	}
	
	public int getIsReportItem() {
		return isReportItem;
	}

	public void setIsReportItem(int isReport) {
		this.isReportItem = isReport;
	}

	public boolean isDisplayFullAbstract() {
		return displayFullAbstract;
	}

	public void setDisplayFullAbstract(boolean displayFullAbstract) {
		this.displayFullAbstract = displayFullAbstract;
	}
	
	public String getNewsUploadedAt() {
		return newsUploadedAt;
	}

	public void setNewsUploadedAt(String newsUploadedAt) {
		this.newsUploadedAt = newsUploadedAt;
	}
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
