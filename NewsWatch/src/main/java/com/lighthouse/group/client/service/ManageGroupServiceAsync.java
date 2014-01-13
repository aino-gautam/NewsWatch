package com.lighthouse.group.client.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.domain.Group;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsItemList;

public interface ManageGroupServiceAsync {

	void getUserGroupsList(int userId, int ncid, AsyncCallback<List<Group>> callback);

	void getAvailableGroups(AsyncCallback<List<Group>> callback);

	void updateSessionGroupCategoryMap(GroupCategoryMap groupCategoryMap,PageCriteria criteria, int newsmode,
			AsyncCallback<NewsItemList> callback);

	void clearAllSelection(GroupCategoryMap groupCategoryMap, AsyncCallback<Void> callback);
    
	void getBlankGroupCategoryMap(AsyncCallback<GroupCategoryMap> callback);

	void createGroup(Group group, AsyncCallback<Integer> callback);

	void saveUserGroupItemsSelections(List<Group> groupList, int ncid,
			int userId, AsyncCallback<Void> callback);
	
	void saveNewsFilterModePreference(String choice, int groupId,AsyncCallback<Integer> callback);

	void updateMandatoryGroups(ArrayList<Group> groupselectionList,	Group defaultGroup, AsyncCallback<Boolean> asyncCallback);

	void deleteGroup(int groupId, AsyncCallback<Boolean> callback);

	void getCustomGroupsAllowed(int ncid, AsyncCallback<Integer> callback);
   
   
}
