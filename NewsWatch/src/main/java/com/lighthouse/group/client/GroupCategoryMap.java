package com.lighthouse.group.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.TagItem;

public class GroupCategoryMap extends CategoryMap {

	/**
	 * @author prachi@ensarm.com
	 */
	private static final long serialVersionUID = 1L;
	private int groupId;

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public GroupCategoryMap() {
		super();
		groupId = -1;
	}

	public GroupCategoryMap(int ncid, int userId, int grpId) {
		super(ncid, userId);
		grpId = this.groupId;

	}

	public void selectTags(ArrayList selectedTagsList){
		for(Object obj : this.keySet()){
			CategoryItem citem = (CategoryItem)this.get(obj);
			HashMap map = citem.getItemMap();
			for(Object o : map.keySet()){
				TagItem tagitem = (TagItem)map.get(o);
				Iterator iter = selectedTagsList.iterator();
				while(iter.hasNext()){
					TagItem selectedTag = (TagItem)iter.next();
					if(selectedTag.getTagId() == tagitem.getTagId()){
						tagitem.setSelected(true);
						tagitem.setDirty(true);
						break;
					}
				}
			}
		}
	}
}
