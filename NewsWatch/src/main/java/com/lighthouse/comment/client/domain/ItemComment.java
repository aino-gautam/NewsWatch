package com.lighthouse.comment.client.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author kiran@ensarm.com
 * Domain class of the itemComment entity.
 */
public class ItemComment implements Serializable {

	private static final long serialVersionUID = 1L;
	private int itemCommentId;
	private String text;
	private Date commentTime;
	private int groupId;
	private int newsItemId;
	private int userId;
	private String userName;

	public int getItemCommentId() {
		return itemCommentId;
	}

	public void setItemCommentId(int itemCommentId) {
		this.itemCommentId = itemCommentId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(Date commentTime) {
		this.commentTime = commentTime;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getNewsItemId() {
		return newsItemId;
	}

	public void setNewsItemId(int newsItemId) {
		this.newsItemId = newsItemId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
