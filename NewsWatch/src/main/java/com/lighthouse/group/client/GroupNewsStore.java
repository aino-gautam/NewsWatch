package com.lighthouse.group.client;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.client.service.ManageGroupService;
import com.lighthouse.group.client.service.ManageGroupServiceAsync;
import com.lighthouse.main.client.LhLogoutPage;
import com.lighthouse.main.client.service.LhNewsProviderService;
import com.lighthouse.main.client.service.LhNewsProviderServiceAsync;
import com.lighthouse.main.client.ui.LhNewsPresenter;
import com.newscenter.client.NewsStore;
import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.NewsEvent;
import com.newscenter.client.news.NewsCache;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

/**
 * This class represents a group news store
 * @author Nairutee & prachi
 *
 */

public class GroupNewsStore extends NewsStore {

	private Group group;
	private static GroupNewsStore groupNewsStore;
	private static HashMap<Integer, GroupNewsStore> groupNewsStoreMap = new HashMap<Integer, GroupNewsStore>();
	private GroupPageCriteria groupPageCriteria ;
	private GroupPageCriteria groupFullModeCriteria = new GroupPageCriteria();
	private int newsmode;
	
	
	
	private GroupNewsStore(){
		
	}
	
	public static GroupNewsStore getInstance(){
		Group currentActiveGroup = GroupManager.getCurrentActiveGroup();
		if(currentActiveGroup == null){
			groupNewsStore = new GroupNewsStore();
			return groupNewsStore;
		}
		return getInstance(currentActiveGroup.getGroupId());
	}
	
	/**
	 * returns an instance of the GroupNewsStore for the particular group id
	 * @param id - group id
	 * @return instance of GroupNewsStore
	 */
	public static GroupNewsStore getInstance(int id){
		if(groupNewsStoreMap.containsKey(id))
			groupNewsStore = groupNewsStoreMap.get(id);
		else{
			groupNewsStore = new GroupNewsStore();
			groupNewsStoreMap.put(id, groupNewsStore);
		}
		return groupNewsStore;
	}
	
	/**
	 * Called by the ItemStore on receiving NewsItems based on user selections in the CategoryMap. If an earlier NewsItemList is present, the newly arrived
	 * list is merged with the existing one, the merged list is sorted again and set as the globalNewsList. A method 'assocaiteTags' is called on the Item
	 * Store to associate the news to tags in the categoryMap and then the NEWSARRIVED event is fired which is heard by the MainNewsPresenter. 
	 * @param list -  a NewsItemList of NewsItems. 
	 */
	public void initialize(NewsItemList list){
		if(list.isRedirect() == true)
		{
			LhLogoutPage logoutPage = new LhLogoutPage(GroupManager.getInstance().getLhUser());
			logoutPage.redirectOnSessionClose();
		}
		list.sortList();
		setGlobalNewsItemList(list);
		NewsCache cache = new NewsCache();
		Iterator iter = getGlobalNewsItemList().iterator();
		while(iter.hasNext()){
			NewsItems news = (NewsItems)iter.next();
			cache.add(news);
		}
		setGlobalNewsCache(cache);
		//ItemStore.getInstance().associateTagNews(getGlobalNewsCache());
		NewsEvent evt = new NewsEvent(this,NewsEvent.NEWSARRIVED,getGlobalNewsItemList());
		AppEventManager.getInstance().fireEvent(evt);
	
	}
	
	/**
	 * This method is called when the user selects any of the applied tags of a news. It fetches all the news related to the selected tag from the db
	 * @param tag - the TagItem for which more news have to be fetched.
	 */
	public void getNews(TagItem tag, GroupPageCriteria criteria){
		LhNewsProviderServiceAsync newsProviderService = GWT.create(LhNewsProviderService.class);
		newsProviderService.getAllNewsforTag(tag, criteria, this);
	}
	
	
	/**
	 * This method is called when the news for the pages in paging panel is fetched 
	 */
	public void getNewsPage(GroupPageCriteria criteria, int newsmode){
		LhNewsProviderServiceAsync newsProviderService = GWT.create(LhNewsProviderService.class);
		newsProviderService.getPage(criteria, newsmode, this);
	}
	
	
	/**
	 * This method fetches the and news according to the group.
	 */
	public void getAndNewsPage(GroupPageCriteria criteria, int newsmode){
		LhNewsProviderServiceAsync newsProviderService = GWT.create(LhNewsProviderService.class);
		newsProviderService.getAndPage(criteria, newsmode,this);
	}
	
	/**
	 * Overrides the super class saveNewsFilterModPreference method. 
	 * Saves the news filter mode for a particular group (current active group)
	 */
	@Override
	public void saveNewsFilterModPreference(String choice, int newsmode,PopupPanel popup){
		setPopupPanel(popup);
		setUserChoice(choice);
		int groupId = GroupManager.getCurrentActiveGroup().getGroupId();
		ManageGroupServiceAsync groupService = GWT.create(ManageGroupService.class);
		groupService.saveNewsFilterModePreference(choice, groupId, this);
	}
	
	public void onFailure(Throwable arg0) {
		arg0.printStackTrace();
	}

	/**
	 * Overrides the super class method for AsyncCallbackHandler 
	 */
	public void onSuccess(Object arg0) {

		if(arg0 instanceof NewsItemList){
			NewsItemList list = (NewsItemList)arg0;
			list.sortList();
			if(list.isTagNews()){
				NewsEvent evt = new NewsEvent(this,NewsEvent.NEWSAVAILABLE,list);
				AppEventManager.getInstance().fireEvent(evt);
			}
			else if(list.isAndNews()){
				NewsEvent evt = new NewsEvent(this,NewsEvent.ANDNEWSAVAILABLE,list);
				AppEventManager.getInstance().fireEvent(evt);
			}
			else{
				NewsEvent evt = new NewsEvent(this,NewsEvent.PAGEAVAILABLE,list);
				AppEventManager.getInstance().fireEvent(evt);
			}
		}
		if(arg0 instanceof Boolean){
			Boolean bool = (Boolean) arg0;
			PopupPanel popup = getPopupPanel();
			pop = new PopupPanel();
			pop.clear();
			VerticalPanel vp = new VerticalPanel();
			Label lb = new Label();
			if(bool){
				if(getUserChoice().equalsIgnoreCase("unsubscribe"))
					lb.setText("You have now been unsubscribed to the newsletter");
				if(getUserChoice().equalsIgnoreCase("Daily"))
					lb.setText("You now subscribe to the daily newsletter");
				if(getUserChoice().equalsIgnoreCase("Weekly"))
					lb.setText("You now subscribe to the weekly newsletter");
			}
			else
				lb.setText("Your preference could not be saved.Please try again later.");
				vp.setSpacing(5);
			vp.add(lb);
			vp.add(okButton);
			okButton.setSize("40px", "25px");
			okButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					hidePopup();
					
				}
			});
			pop.add(vp);
			int left = popup.getPopupLeft();
			int top = popup.getPopupTop();
			popup.hide();
			pop.setPopupPosition(left, top);
			pop.setStylePrimaryName("newsletterPopup");
			pop.setAnimationEnabled(true);
			pop.setTitle("Click outside to close");
			pop.show();
		}
		if(arg0 instanceof Integer){
			final int bool = (Integer)arg0;
			PopupPanel popup = getPopupPanel();
			Button okBtn = new Button("OK");
			pop = new PopupPanel();
			pop.clear();
			VerticalPanel vp = new VerticalPanel();
			Label lb = new Label();
			if(bool == 1)
				lb.setText("Your preference has been saved. Please be aware that you have chosen \n the 'OR' criteria for news filtering");
			else if(bool == 2)
				lb.setText("You preference has been saved. Please be aware that you have chosen \n the 'AND' criteria for news filtering");
			else
				lb.setText("Your preference could not be saved.Please try again later.");
				vp.setSpacing(5);
			vp.add(lb);
			vp.add(okBtn);
			okBtn.setSize("40px", "25px");
			okBtn.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					
					hidePopup();
					LhNewsPresenter.getLoader().setLoadingMessage("Refreshing News...");
					LhNewsPresenter.getLoader().enable();
										
					GroupItemStore.getInstance().updateSessionCategoryMap();
					
					/*if(bool == 1)
						GroupItemStore.getInstance().updateSessionCategoryMap();
					else if(bool == 2)
						GroupNewsStore.getInstance().getAndNewsPage(getGroupFullModeCriteria(),getNewsmode());*/
						
					
				}
			});
			pop.add(vp);
			int left = popup.getPopupLeft();
			int top = popup.getPopupTop();
			popup.hide();
			pop.setPopupPosition(left, top);
			pop.setStylePrimaryName("newsletterPopup");
			pop.setAnimationEnabled(true);
			pop.setTitle("Click outside to close");
			pop.show();
		}
	}

	
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public GroupPageCriteria getGroupPageCriteria() {
		return groupPageCriteria;
	}

	public void setGroupPageCriteria(GroupPageCriteria groupPageCriteria) {
		this.groupPageCriteria = groupPageCriteria;
	}

	public int getNewsmode() {
		return newsmode;
	}

	public void setNewsmode(int newsmode) {
		this.newsmode = newsmode;
	}

	public GroupPageCriteria getGroupFullModeCriteria() {
		return groupFullModeCriteria;
	}

	public void setGroupFullModeCriteria(GroupPageCriteria groupFullModeCriteria) {
		this.groupFullModeCriteria = groupFullModeCriteria;
	}

	
}
