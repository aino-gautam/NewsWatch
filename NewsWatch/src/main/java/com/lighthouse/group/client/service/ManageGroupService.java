package com.lighthouse.group.client.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.domain.Group;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsItemList;


@RemoteServiceRelativePath("manageGroupService")
public interface ManageGroupService extends RemoteService {
	
	public int getCustomGroupsAllowed(int ncid); 

	public List<Group> getUserGroupsList(int userId, int ncid);

	public List<Group> getAvailableGroups();

	public void clearAllSelection(GroupCategoryMap groupCategoryMap);
		
	NewsItemList updateSessionGroupCategoryMap(GroupCategoryMap groupCategoryMap,PageCriteria criteria, int newsmode);
	
    public GroupCategoryMap getBlankGroupCategoryMap();
	
    public boolean deleteGroup(int groupId);
    
	public Integer createGroup(Group group);

	public void saveUserGroupItemsSelections(List<Group> groupList, int ncid, int userId);
	
	public int saveNewsFilterModePreference(String choice,int groupId);

	boolean updateMandatoryGroups(ArrayList<Group> groupselectionList,Group defaultGroup);
}
