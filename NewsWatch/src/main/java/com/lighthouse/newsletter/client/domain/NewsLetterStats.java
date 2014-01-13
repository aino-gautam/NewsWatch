package com.lighthouse.newsletter.client.domain;

import java.io.Serializable;
import java.util.Date;

public class NewsLetterStats implements Serializable {
	private Long ncId;
	private Long userId;
	private String newsSentDate;
	private Date newsOpenedDate;
	private String firstName;
	private String email;

	public NewsLetterStats() {
	}

	public NewsLetterStats(Long ncId, Long userId, String newsSentDate,
			Date newsOpenedDate, String firstName, String email) {
		super();
		this.ncId = ncId;
		this.userId = userId;
		this.newsSentDate = newsSentDate;
		this.newsOpenedDate = newsOpenedDate;
		this.firstName = firstName;
		this.email = email;
	}

	public NewsLetterStats(Long ncId, Long userId, String newsSentDate,
			Date newsOpenedDate) {
		super();
		this.ncId = ncId;
		this.userId = userId;
		this.newsSentDate = newsSentDate;
		this.newsOpenedDate = newsOpenedDate;
	}

	public Long getNcId() {
		return ncId;
	}

	public void setNcId(Long ncId) {
		this.ncId = ncId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getNewsSentDate() {
		return newsSentDate;
	}

	public void setNewsSentDate(String newsSentDate) {
		this.newsSentDate = newsSentDate;
	}

	public Date getNewsOpenedDate() {
		return newsOpenedDate;
	}

	public void setNewsOpenedDate(Date newsOpenedDate) {
		this.newsOpenedDate = newsOpenedDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
