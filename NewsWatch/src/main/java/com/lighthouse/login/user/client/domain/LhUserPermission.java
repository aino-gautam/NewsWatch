package com.lighthouse.login.user.client.domain;

import java.io.Serializable;

public class LhUserPermission implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3007835411178895005L;
	private int id;
	private int userId;
	private int newsCenterId;
	private int mailAlert;
	private int groups;
	private int reports;
	private int comments;
	private int views;
	private int search;
	private int primaryHeadLine;
	private int rss;
	private int share;
	private int pulse;
	private int favoriteGroup;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getNewsCenterId() {
		return newsCenterId;
	}
	
	public void setNewsCenterId(int newsCenterId) {
		this.newsCenterId = newsCenterId;
	}
	
	public int isMailAlertPermitted() {
		return mailAlert;
	}
	
	public void setMailAlertPermitted(int mailAlert) {
		this.mailAlert = mailAlert;
	}
	
	public int isGroupsPermitted() {
		return groups;
	}
	
	public void setGroupsPermitted(int groups) {
		this.groups = groups;
	}
	
	public int isReportsPermitted() {
		return reports;
	}
	
	public void setReportsPermitted(int reports) {
		this.reports = reports;
	}
	
	public int isCommentsPermitted() {
		return comments;
	}
	
	public void setCommentsPermitted(int comments) {
		this.comments = comments;
	}
	
	public int isViewsPermitted() {
		return views;
	}
	
	public void setViewsPermitted(int views) {
		this.views = views;
	}
	
	public int isSearchPermitted() {
		return search;
	}
	
	public void setSearchPermitted(int search) {
		this.search = search;
	}
	
	public int isPrimaryHeadLinePermitted() {
		return primaryHeadLine;
	}
	
	public void setPrimaryHeadLinePermitted(int primaryHeadLine) {
		this.primaryHeadLine = primaryHeadLine;
	}
	
	public int isRssPermitted() {
		return rss;
	}
	
	public void setRssermitted(int rss) {
		this.rss = rss;
	}

	public int isSharePermitted() {
		return share;
	}

	public void setSharePermitted(int share) {
		this.share = share;
	}
	
	public int isPulsePermitted() {
		return pulse;
	}

	public void setPulsePermitted(int pulse) {
		this.pulse = pulse;
	}
	
	public int isFavoriteGroupPermitted() {
		return favoriteGroup;
	}

	public void setFavoriteGroupPermitted(int favoriteGroup) {
		this.favoriteGroup = favoriteGroup;
	}
}
