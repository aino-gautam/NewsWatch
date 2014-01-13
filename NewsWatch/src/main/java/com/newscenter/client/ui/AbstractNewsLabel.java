package com.newscenter.client.ui;

import com.google.gwt.user.client.ui.Label;
import com.newscenter.client.news.NewsItems;

public class AbstractNewsLabel extends Label {
	private int newsid;
	private String newstext="";
	private NewsItems newsitem;
	public int getNewsid() {
		return newsid;
	}
	public void setNewsid(int newsid) {
		this.newsid = newsid;
	}
	public String getNewstext() {
		return newstext;
	}
	public void setNewstext(String newstext) {
		this.newstext = newstext;
	}
	public NewsItems getNewsitem() {
		return newsitem;
	}
	public void setNewsitem(NewsItems newsitem) {
		this.newsitem = newsitem;
	}
}
