package com.newscenter.client.obsolete;

import java.io.Serializable;

public class SelectionItem implements Serializable
{
	private String tagName=null;
	private int tagId;
	private int parentId;
	private int industryId;
	private String criteria=null;
	
	public String getTagName() {
		return tagName;
	}
	
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public int getTagId() {
		return tagId;
	}
	
	public void setTagId(int tagId) {
		this.tagId = tagId;
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
