package com.newscenter.client.obsolete;

import java.io.Serializable;
import java.util.Date;

public class NewsSelectionItem implements Serializable{
	int newsitemId;
	String content;
	String title;
	String Abstract;
	String url;
	Date itemdate;
	Date uploadedat;

	public int getNewsitemId() {
		return newsitemId;
	}
	public void setNewsitemId(int newsitemId) {
		this.newsitemId = newsitemId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAbstract() {
		return Abstract;
	}
	public void setAbstract(String abstract1) {
		Abstract = abstract1;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getItemdate() {
		return itemdate;
	}
	public void setItemdate(Date itemdate) {
		this.itemdate = itemdate;
	}
	public Date getUploadedat() {
		return uploadedat;
	}
	public void setUploadedat(Date uploadedat) {
		this.uploadedat = uploadedat;
	}
}