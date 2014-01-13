package com.lighthouse.newsletter.client.domain;

import java.io.Serializable;

/**
 * Domain class for newslettergroupselection 
 * @author pritam
 *
 */
public class NewsletterGroupSelectionConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9194692930543669914L;
	private int id;
	private Integer userId;
	private Integer alertId;
	private Integer newscenterId;
	private Integer groupId;
	
	public NewsletterGroupSelectionConfig(){
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getAlertId() {
		return alertId;
	}
	public void setAlertId(Integer alertId) {
		this.alertId = alertId;
	}
	public Integer getNewscenterId() {
		return newscenterId;
	}
	public void setNewscenterId(Integer newscenterId) {
		this.newscenterId = newscenterId;
	}
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
}
