package com.lighthouse.group.client.criteria;

import com.lighthouse.group.client.GroupManager;
import com.lighthouse.group.client.exception.GroupException;
import com.newscenter.client.criteria.PageCriteria;
/**
 * 
 * @author prachi
 * This class is used to set the pageSize of the LhNewsPresenter i.e the number of NewsItems
 * that will be visible to the user at a time and based on that setting the start index.
 *
 */
public class GroupPageCriteria extends PageCriteria {

	private static final long serialVersionUID = 1L;
	private int groupId ;
	private GroupException grpException;

	
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
    
	public GroupPageCriteria() {
	  super();
	  this.setGroupId(groupId);
	  
	}
    
	public GroupPageCriteria(int grpId){
		super();
		if(this.groupId == -1)
			 throw grpException;
		else
			setGroupId(grpId);
	}
}
