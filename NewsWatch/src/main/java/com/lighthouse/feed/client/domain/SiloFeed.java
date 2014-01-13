package com.lighthouse.feed.client.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class SiloFeed implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ncid;
	private String ncName;
	private long siloPollFrequency;
	private String etag;
	private String ptag;
	private boolean autoSyncEnabled;
	private int feedUserId;
	private ArrayList<Feed> feedUrlList;
	
	public int getNcid() {
		return ncid;
	}
	
	public void setNcid(int ncid) {
		this.ncid = ncid;
	}
	
	public String getNcName() {
		return ncName;
	}
	
	public void setNcName(String ncName) {
		this.ncName = ncName;
	}
	
	public long getSiloPollFrequency() {
		return siloPollFrequency;
	}
	
	public void setSiloPollFrequency(long siloPollFrequency) {
		this.siloPollFrequency = siloPollFrequency;
	}
	
	public String getEtag() {
		return etag;
	}
	
	public void setEtag(String etag) {
		this.etag = etag;
	}
	
	public String getPtag() {
		return ptag;
	}
	
	public void setPtag(String ptag) {
		this.ptag = ptag;
	}
	
	public boolean isAutoSyncEnabled() {
		return autoSyncEnabled;
	}
	
	public void setAutoSyncEnabled(boolean autoSyncEnabled) {
		this.autoSyncEnabled = autoSyncEnabled;
	}
	
	public int getFeedUserId() {
		return feedUserId;
	}
	
	public void setFeedUserId(int feedUserId) {
		this.feedUserId = feedUserId;
	}
	
	public ArrayList<Feed> getFeedUrlList() {
		return feedUrlList;
	}
	
	public void setFeedUrlList(ArrayList<Feed> feedUrlList) {
		this.feedUrlList = feedUrlList;
	}
	
}
