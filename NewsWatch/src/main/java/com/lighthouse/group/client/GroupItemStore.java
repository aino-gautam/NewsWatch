package com.lighthouse.group.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.client.service.ManageGroupService;
import com.lighthouse.group.client.service.ManageGroupServiceAsync;
import com.lighthouse.group.client.ui.GroupPresenter;
import com.lighthouse.main.client.LhMain;
import com.lighthouse.main.client.service.LhNewsProviderService;
import com.lighthouse.main.client.service.LhNewsProviderServiceAsync;
import com.lighthouse.main.client.ui.LhNewsPresenter;
import com.newscenter.client.ItemStore;
import com.newscenter.client.ServiceUtils;

import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.NewsEvent;
import com.newscenter.client.events.TagEvent;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsProviderServiceAsync;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.ItemProviderServiceAsync;
import com.newscenter.client.tags.TagItem;


/**
 * This class representes a group item store
 * @author Nairutee
 *
 */
public class GroupItemStore extends ItemStore {

	private Group group;
	private static GroupItemStore groupItemStore;
	private static HashMap<Integer, GroupItemStore> groupStoreMap = new HashMap<Integer, GroupItemStore>();
	private GroupPresenter parentGroupPresenter;
	
	private GroupItemStore(){
		
	}
	
	public static GroupItemStore getInstance(){
		Group currentActiveGroup = GroupManager.getCurrentActiveGroup();
		if(currentActiveGroup == null){
			groupItemStore = new GroupItemStore();
			return groupItemStore;
		}
		return getInstance(currentActiveGroup.getGroupId());
		
	}
	
	/**
	 * returns an instance of the groupItemStore for the particular groupId 
	 * @param id - group id
	 * @return GroupItemStore instance
	 */
	public static GroupItemStore getInstance(int id){
		if(groupStoreMap.containsKey(id))
			groupItemStore = groupStoreMap.get(id);
		else{
			groupItemStore = new GroupItemStore();
			groupStoreMap.put(id, groupItemStore);
		}
		return groupItemStore;
	}
	
	/**
	 * overrides super class initialize method
	 */
	@Override
	public void initialize(){
		AppEventManager.getInstance().addTagEventListener(this);
	}
	
	/**
	 * overrides super class getVisibleCategories. Returns a list of CategoryItem from the 
	 * GroupCategoryMap of the current active group
	 */
	@Override
	public ArrayList<CategoryItem> getVisibleCategories(){
		if(getGroup() == null)	
			setGroup(GroupManager.getCurrentActiveGroup());
		GroupCategoryMap groupCategoryMap = getGroup().getGroupCategoryMap();
		ArrayList<CategoryItem> groupCategoryList = new ArrayList<CategoryItem>();
		for(Object object : groupCategoryMap.keySet()){
			CategoryItem categoryItem = (CategoryItem)groupCategoryMap.get(object);
			groupCategoryList.add(categoryItem);
		}
		return groupCategoryList;
	}
	
	/**
	 * This method is called when the user clicks on Clear All tags. It is used to remove all user selections.
	 */
	@Override
	public void clearSelection(){
		GroupCategoryMap groupCategoryMap = GroupManager.getCurrentActiveGroup().getGroupCategoryMap();
		ManageGroupServiceAsync groupService = GWT.create(ManageGroupService.class);
		groupService.clearAllSelection(groupCategoryMap, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Void result) {
			}
		});
	}
	
	/**
	 * This method is called when the user selects any of the applied tags of a news. It fetches all the news related to the selected tag from the db
	 * @param tag - the TagItem for which more news have to be fetched.
	 */
	public void getNews(TagItem tag, GroupPageCriteria criteria){
		LhNewsProviderServiceAsync newsProviderService = GWT.create(LhNewsProviderService.class);
		newsProviderService.getAllNewsforTag(tag,criteria,this);
	}
	
	/**
	 * overrides the super class updateSessionCategoryMap method. Fetches the GroupCategoryMap of the
	 * curren active group and updates it with the sessions list of groups maintained.
	 */
	@Override
	public void updateSessionCategoryMap() {
		GroupCategoryMap groupCategoryMap = GroupManager.getCurrentActiveGroup().getGroupCategoryMap();
		ManageGroupServiceAsync groupService = GWT.create(ManageGroupService.class);
		groupService.updateSessionGroupCategoryMap(groupCategoryMap,getCriteria(), 
				getNewsmode(),new AsyncCallback<NewsItemList>(){

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(NewsItemList result) {
						System.out.println("News arrived");
						NewsEvent evt = new NewsEvent(GroupManager.getActiveGroupPresenter(),NewsEvent.NEWSARRIVED,result);
						AppEventManager.getInstance().fireEvent(evt);
					}
		});
	}
	
	
	/**
	 * This method is called when user closes the window or logs out. It saves the user tag selection into the db
	 */
	@Override
	public void saveUserSelection(){
		List<Group> groupsList = GroupManager.getInstance().getUserGroupList();
		int ncid = GroupManager.getInstance().getLhUser().getUserSelectedNewsCenterID();
		int userId = GroupManager.getInstance().getLhUser().getUserId();
		ManageGroupServiceAsync groupService = GWT.create(ManageGroupService.class);
		groupService.saveUserGroupItemsSelections(groupsList, ncid, userId, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Void result) {
			}
		});
	}
	
	/**
	 * uses the super class method sortItemMap to sort the tags of a GroupCategoryMap
	 * @param groupCategoryMap
	 * @return
	 */
	public GroupCategoryMap sortCategoryTags(GroupCategoryMap groupCategoryMap){
		GroupCategoryMap gcmap = new GroupCategoryMap();
		gcmap.setGroupId(groupCategoryMap.getGroupId());
		for(Object obj : groupCategoryMap.keySet()){
			CategoryItem catitem = (CategoryItem)groupCategoryMap.get(obj);
			HashMap hashmap = sortItemMap(catitem.getItemMap());
			hashmap = sortItemMap(hashmap); 
			catitem.setItemMap(hashmap);
			gcmap.put(obj, catitem);
		}
		return gcmap;
	}

	@Override
	public boolean onEvent(TagEvent evt) {
		
		int evttype = evt.getEventType();
			switch(evttype){
				case (TagEvent.TAGSELECTED):{
					LhMain.tagSelectionChanged = true;
					LhNewsPresenter.getLoader().setLoadingMessage("Updating news...");
					LhNewsPresenter.getLoader().enable();
					if(parentGroupPresenter == GroupManager.getActiveGroupPresenter())
						updateSessionCategoryMap();
					break;
				}
				case (TagEvent.TAGDESELECTED):{
					LhMain.tagSelectionChanged = true;
					LhNewsPresenter.getLoader().setLoadingMessage("Updating news...");
					LhNewsPresenter.getLoader().enable();
					if(parentGroupPresenter == GroupManager.getActiveGroupPresenter())
						updateSessionCategoryMap();
					break;
				}
				case (TagEvent.CATEGORYITEMSELECTED):{
					LhMain.tagSelectionChanged = true;
					LhNewsPresenter.getLoader().setLoadingMessage("Updating news...");
					LhNewsPresenter.getLoader().enable();
					if(parentGroupPresenter == GroupManager.getActiveGroupPresenter())
						updateSessionCategoryMap();
					break;
				}
				case (TagEvent.CATEGORYITEMDESELECTED):{
					LhMain.tagSelectionChanged = true;
					LhNewsPresenter.getLoader().setLoadingMessage("Updating news...");
					LhNewsPresenter.getLoader().enable();
					if(parentGroupPresenter == GroupManager.getActiveGroupPresenter())
						updateSessionCategoryMap();
					break;
				}
			}
		return false;
	}
	
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public GroupPresenter getParentGroupPresenter() {
		return parentGroupPresenter;
	}

	public void setParentGroupPresenter(GroupPresenter parentGroupPresenter) {
		this.parentGroupPresenter = parentGroupPresenter;
	}

}
