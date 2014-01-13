package com.admin.client;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

public class NewsItemsAdminInformation implements Serializable {
	String industryName="";
	String tagName="";
	String newsTitle="";
	String content="";
	String abstractNews="";
	String url="";
	String source="";
	String author="";
	//String date="";
	Date date;
	int newsItemId;
	String imageurl = "";
	int filelength;
	ArrayList arrayTagList=null;
	
	
	public String getIndustryName() {
		return industryName;
	}
	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getNewsTitle() {
		return newsTitle;
	}
	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getNewsItemId() {
		return newsItemId;
	}
	public void setNewsItemId(int newsItemId) {
		this.newsItemId = newsItemId;
	}
	public ArrayList getArrayTagList() {
		return arrayTagList;
	}
	public void setArrayTagList(ArrayList arrayTagList) {
		this.arrayTagList = arrayTagList;
	}
	public String getImageurl() {
		return imageurl;
	}
	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}
	
	public int getFilelength() {
		return filelength;
	}
	public void setFilelength(int filelength) {
		this.filelength = filelength;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
}
