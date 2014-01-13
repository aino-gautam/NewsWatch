package com.newscenter.client.tags;

import java.io.Serializable;
import java.util.ArrayList;

import com.newscenter.client.news.NewsItems;

/**
 * This class represents a tag record of the database.                     
 *
 */
public class TagItem implements Serializable 
{
	private String tagName="";
	private int tagId;
	private int parentId;
	private boolean selected ;
	private boolean isDirty;
	private boolean tagSelectionStatus;
	private boolean isPrimary = false;
	private int orderId;
	
	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public boolean isTagSelectionStatus() {
		return tagSelectionStatus;
	}

	public void setTagSelectionStatus(boolean tagSelectionStatus) {
		this.tagSelectionStatus = tagSelectionStatus;
	}

	private CategoryItem categoryItem;
	private ArrayList<NewsItems> associatedNewsList = new ArrayList<NewsItems>();
	
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
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean sel){
		selected = sel ;
	}
	
	public TagItem clone()
	{
		TagItem tagitem = new TagItem();
		tagitem.clone();
		return tagitem;
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}
	
	public CategoryItem getCategoryItem() {
		return categoryItem;
	}
	
	public void setCategoryItem(CategoryItem categoryItem) {
		this.categoryItem = categoryItem;
	}
	
	public ArrayList<NewsItems> getAssociatedNewsList() {
		return associatedNewsList;
	}
	
	public void setAssociatedNewsList(ArrayList<NewsItems> associatedNewsList) {
		this.associatedNewsList = associatedNewsList;
	}
	
	public void addNewsforTag(NewsItems news){
		associatedNewsList.add(news);
	}
	
	public void removeNewsforTag(NewsItems news){
		associatedNewsList.remove(news);
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

}
