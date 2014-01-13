package com.lighthouse.group.client.domain;

import java.io.Serializable;

import com.lighthouse.group.client.GroupCategoryMap;



/** 
 * @author prachi@ensarm.com
 * 
 * This is the domain class for group **/

public class Group implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int groupId;
	private String groupName = "";
	private int isMandatory = 0;
	private Integer groupParentId;
	private int newsCenterId;
	private int userId;
	private int newsFilterMode;
	private int isDefaultGroup;
	private int isFavorite;
	
	public int getIsDefaultGroup() {
		return isDefaultGroup;
	}

	public void setIsDefaultGroup(int isDefaultGroup) {
		this.isDefaultGroup = isDefaultGroup;
	}

	private GroupCategoryMap groupCategoryMap;
	
    
	public Group() {
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(int isMandatory) {
		this.isMandatory = isMandatory;
	}

	public Integer getGroupParentId() {
		return groupParentId;
	}

	public void setGroupParentId(Integer groupParentId) {
		this.groupParentId = groupParentId;
	}

	public int getNewsCenterId() {
		return newsCenterId;
	}

	public void setNewsCenterId(int newsCenterId) {
		this.newsCenterId = newsCenterId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public GroupCategoryMap getGroupCategoryMap() {
		return groupCategoryMap;
	}

	public void setGroupCategoryMap(GroupCategoryMap groupCategoryMap) {
		this.groupCategoryMap = groupCategoryMap;
	}

	public int getNewsFilterMode() {
		return newsFilterMode;
	}

	public void setNewsFilterMode(int newsFilterMode) {
		this.newsFilterMode = newsFilterMode;
	}

	public int getIsFavorite() {
		return isFavorite;
	}

	public void setIsFavorite(int isFavorite) {
		this.isFavorite = isFavorite;
	}
}
