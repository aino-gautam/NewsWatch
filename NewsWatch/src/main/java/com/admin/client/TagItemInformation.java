package com.admin.client;

import java.io.Serializable;

public class TagItemInformation implements Serializable {
	
	int tagItemId;
	String tagName = "";
	int parentId;
	int industryId;
	String criteria = "";
	boolean isprimary=false;
	
	public boolean isIsprimary() {
		return isprimary;
	}

	public void setIsprimary(boolean isprimary) {
		this.isprimary = isprimary;
	}

	public int getTagItemId() {
		return tagItemId;
	}
	
	public void setTagItemId(int tagItemId) {
		this.tagItemId = tagItemId;
	}
	
	public String getTagName() {
		return tagName;
	}
	
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public int getParentId() {
		return parentId;
	}
	
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	public int getIndustryId() {
		return industryId;
	}
	
	public void setIndustryId(int industryId) {
		this.industryId = industryId;
	}
	
	public String getCriteria() {
		return criteria;
	}
	
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	

}
