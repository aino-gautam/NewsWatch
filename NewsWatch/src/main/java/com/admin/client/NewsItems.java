package com.admin.client;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;


public class NewsItems implements Serializable{
	private int newsId;
	private String abstractNews="";
	private String url;
	private Date newsDate;
	private String newsTitle="";
	private String imageUrl="";
	private String newsSource="";
	private String newsContent="";
	private String author="";
	private ArrayList<TagItemInformation> associatedTagList = new ArrayList<TagItemInformation>();

	
	public void addTagforNews(TagItemInformation tagitem){
		associatedTagList.add(tagitem);
	}
	
	public void removeTagforNews(TagItemInformation tagitem){
		associatedTagList.remove(tagitem);
	}
	
	public ArrayList<TagItemInformation> getAssociatedTagList() {
		return associatedTagList;
	}

	public void setAssociatedTagList(ArrayList<TagItemInformation> associatedTagList) {
		this.associatedTagList = associatedTagList;
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

	public Date getNewsDate() {
		return newsDate;
	}

	public void setNewsDate(Date newsDate) {
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

	public String getNewsSource() {
		return newsSource;
	}

	public void setNewsSource(String newsSource) {
		this.newsSource = newsSource;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}