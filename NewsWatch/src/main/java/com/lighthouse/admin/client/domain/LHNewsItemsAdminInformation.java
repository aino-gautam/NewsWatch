package com.lighthouse.admin.client.domain;

import com.admin.client.NewsItemsAdminInformation;

public class LHNewsItemsAdminInformation extends NewsItemsAdminInformation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int isLocked;
	private String newsPriority;
	
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
}
