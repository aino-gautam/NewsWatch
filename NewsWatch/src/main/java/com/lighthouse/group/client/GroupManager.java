package com.lighthouse.group.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.appUtils.client.ProgressIndicatorWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.client.service.ManageGroupService;
import com.lighthouse.group.client.service.ManageGroupServiceAsync;
import com.lighthouse.group.client.ui.GroupPresenter;
import com.lighthouse.group.client.ui.GroupTabWidget;
import com.lighthouse.group.client.ui.MoreGroupsMenu;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.client.LhMain;
import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.TagEvent;

/**
 * 
 * @author Nairutee & prachi@ensarm.com
 * 
 * This class is responsible to manage the full group UI
 * 
 */
public class GroupManager extends Composite{

	private LhMain lhMainRef;
	private LhUser lhUser;
	private static GroupManager groupManager;
	private TabPanel tabPanel;
	private List<Group> userGroupList;
	private int customGroupsCount = 0;
	private HorizontalPanel hp = new HorizontalPanel();
	private VerticalPanel groupManagerVp = new VerticalPanel();
	private HashMap<Group, GroupPresenter> tabGroupPresenterMap = new HashMap<Group, GroupPresenter>(); 
	private HashMap<Group, GroupTabWidget> tabGroupWidgetMap = new HashMap<Group, GroupTabWidget>(); 
	private HashMap<Integer, String> tabMap = new HashMap<Integer, String>();
	private static Group currentActiveGroup;
	private static GroupPresenter activeGroupPresenter;
	private Image createGroupImage = new Image();
	private PopupPanel createGroupPopup;
	private ProgressIndicatorWidget progressIndicator = new ProgressIndicatorWidget();
	
	private int maxGroupsToBeDisplayed;
	private HashMap<Group, Integer> mostClickedGroupsMap = new HashMap<Group, Integer>();
	private HashMap<Group, Integer> visibleGroupsMap = new HashMap<Group, Integer>();
	private MoreGroupsMenu subMenu;
	private MoreGroupsMenu mainMenu;
	private Command menuCommand;
	
	/**
	 * private constructor
	 */
	private GroupManager() {
		progressIndicator.setVisible(false);
		groupManagerVp.add(progressIndicator);
		groupManagerVp.setCellHorizontalAlignment(progressIndicator, HasHorizontalAlignment.ALIGN_CENTER);
		groupManagerVp.add(hp);
		hp.setWidth("100%");
		initWidget(groupManagerVp);
	}

	/**
	 * gets a singleton instance of the groupmanager
	 * 
	 * @return GroupManager
	 */
	public static GroupManager getInstance() {
		if (groupManager == null)
			groupManager = new GroupManager();

		return groupManager;
	}

	private void calculateDisplayedGroupsNumber() {
		int width = hp.getOffsetWidth();
		maxGroupsToBeDisplayed = width/130;
	}
	
	/**
	 * initializes the group datastructure. Fetches user group information 
	 */
	public void initialize() {
		progressIndicator.setVisible(true);
		Integer userId = lhUser.getUserId();
		Integer ncid = lhUser.getUserSelectedNewsCenterID();
		ManageGroupServiceAsync groupService = GWT.create(ManageGroupService.class);
		groupService.getUserGroupsList(userId, ncid, new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				progressIndicator.setVisible(false);
			}

			@Override
			public void onSuccess(List<Group> result) {
				setUserGroupList(result);
				createLayout();
			}
		});
	}

	/**
	 * creates the groups UI
	 */
	public void createLayout() {
		hp.clear();
		if(userGroupList == null){
			initialize();
			return;
		}
		
		if(lhUser.getUserPermission() == null){
			Label noPermissionLbl = new Label("You do not have permissions to view this silo. Please contact the administrator to provide permissions");
			hp.add(noPermissionLbl);
			progressIndicator.setVisible(false);
		}
		if(lhUser.getUserPermission().isGroupsPermitted() == 1)
			createTabbedUI();
		else
			createUI();
	}
	
	/**
	 * creates the simple UI
	 */
	private void createUI(){
		for (Group group : userGroupList) {
			if(group.getIsDefaultGroup() == 1){
				GroupPresenter groupPresenter = new GroupPresenter(group, lhUser);
				tabGroupPresenterMap.put(group, groupPresenter);
				if(!groupPresenter.isInitialized()){
					GroupCategoryMap gcmap = GroupItemStore.getInstance(group.getGroupId()).sortCategoryTags(group.getGroupCategoryMap());
					group.setGroupCategoryMap(gcmap);
                    setCurrentActiveGroup(group);
            
					groupPresenter.createUI();
					setActiveGroupPresenter(groupPresenter);
					TagEvent evt = new TagEvent(getActiveGroupPresenter(),TagEvent.TAGSAVAILABLE,group.getGroupCategoryMap());
					AppEventManager.getInstance().fireEvent(evt);
				}else{
					setCurrentActiveGroup(group);
					setActiveGroupPresenter(groupPresenter);
				}
				hp.add(groupPresenter);
				break;
			}
		}
		progressIndicator.setVisible(false);
	}
	
	/**
	 * creates the groups tabbed UI
	 */
	private void createTabbedUI(){
		createGroupPopup = new PopupPanel(true);
		createGroupPopup.setStylePrimaryName("searchPopup");
		createGroupPopup.setAnimationEnabled(true);
		
		createGroupImage.setUrl("images/plus-icon.png");
		createGroupImage.addStyleName("newGroupImage");
		createGroupImage.setTitle("click to create group");
		
		tabPanel = new TabPanel();
		tabPanel.setStylePrimaryName("groupTab");
		tabPanel.setWidth("100%");
		tabPanel.setAnimationEnabled(false);
		tabPanel.getTabBar().setWidth("100%");
		calculateDisplayedGroupsNumber();
		int index = 0;
		for (Group group : userGroupList) {
			if(index < maxGroupsToBeDisplayed){
				if(group.getIsMandatory() == 0 && group.getIsFavorite() == 0)
					customGroupsCount++; // counter for user defined groups
				
				GroupPresenter groupPresenter = new GroupPresenter(group, lhUser);
				int tabcount = tabPanel.getTabBar().getTabCount();
				tabMap.put(tabcount, group.getGroupName());
				GroupTabWidget groupTab = new GroupTabWidget(lhUser,group,tabcount);
				groupTab.createUI();
				tabPanel.add(groupPresenter, groupTab);
				tabGroupPresenterMap.put(group, groupPresenter);
				tabGroupWidgetMap.put(group, groupTab);
				visibleGroupsMap.put(group, tabcount);
				mostClickedGroupsMap.put(group,0);
				index++;
			}else
				break;
		}
		GroupPresenter blankPresenter = new GroupPresenter();
		addImageHandler(createGroupImage);
		tabPanel.add(blankPresenter, createGroupImage);
		VerticalPanel vp = new VerticalPanel();
		tabPanel.add(vp, createMenu());
		if(userGroupList.size() <= maxGroupsToBeDisplayed)
			hideMenu();
		
		tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				if(event.getItem() >= userGroupList.size()){
					event.cancel();
				}
			}
		});

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				/*if(event.getSelectedItem() < userGroupList.size()){*/
				if(event.getSelectedItem() < maxGroupsToBeDisplayed){
					int index = tabPanel.getTabBar().getSelectedTab();
					String tabName = tabMap.get(index);
					for (Group group : tabGroupPresenterMap.keySet()) {
						GroupTabWidget groupTab = tabGroupWidgetMap.get(group);
						groupTab.removeStyleName("favgroupTabWidgetBgSelected");
						groupTab.removeStyleName("groupTabWidgetBgSelected");
						if (group.getGroupName().equals(tabName)) {
							int clickCount = mostClickedGroupsMap.get(group);
							mostClickedGroupsMap.put(group, (clickCount+1));
							int id = group.getGroupId();
							if(group.getIsFavorite() == 1)
								groupTab.addStyleName("favgroupTabWidgetBgSelected");
							else
								groupTab.addStyleName("groupTabWidgetBgSelected");
							GroupPresenter groupPresenter = tabGroupPresenterMap.get(group);
							if(!groupPresenter.isInitialized()){
								GroupCategoryMap gcmap = GroupItemStore.getInstance(id).sortCategoryTags(group.getGroupCategoryMap());
								group.setGroupCategoryMap(gcmap);
			                    setCurrentActiveGroup(group);
			            
								groupPresenter.createUI();
								setActiveGroupPresenter(groupPresenter);
								TagEvent evt = new TagEvent(getActiveGroupPresenter(),TagEvent.TAGSAVAILABLE,group.getGroupCategoryMap());
								AppEventManager.getInstance().fireEvent(evt);
							}else{
								
								groupPresenter.resetUI();
								setCurrentActiveGroup(group);
								setActiveGroupPresenter(groupPresenter);
							}
						}
					}
				}
			}
		});
		
		hp.add(tabPanel);
		progressIndicator.setVisible(false);
		tabPanel.selectTab(0);
	}
	
	private void showMenu(){
		mainMenu.setVisible(true);
	}
	
	private void hideMenu(){
		mainMenu.setVisible(false);
	}
	
	private MoreGroupsMenu createMenu(){
		GroupImageResources groupResources = GWT.create(GroupImageResources.class);
		mainMenu = new MoreGroupsMenu(true,groupResources);
		subMenu = new MoreGroupsMenu(true);
		menuCommand = new Command() {
			
			@Override
			public void execute() {
				Integer leastCount = -1;
		    	Integer maxCount = -1;
		    	Group leastClickedGroup  = null;
		    	
				/*
				 * Step 2: get the selected group from the menu
				 */
				Group selectedGroup= subMenu.getGroup(subMenu.getSelectedItem());
				
				/*
				 * Step 2: find the least clicked group
				 */
		    	for(Group group : visibleGroupsMap.keySet()){
		    		if(group.getIsFavorite() == 0){
		    			int clickCount = mostClickedGroupsMap.get(group);
			    		if(leastCount == -1 && maxCount == -1){
			    			leastCount = clickCount;
			    			maxCount = clickCount;
			    			leastClickedGroup = group;
			    		}else if(clickCount < leastCount && clickCount > maxCount){
			    			maxCount = clickCount;
			    		}else if(clickCount < leastCount && clickCount < maxCount){
			    			leastCount = clickCount;
			    			leastClickedGroup = group;
			    		}
		    		}
		    	 }
		    	
		    	/*
		    	 * Step 3: replace least clicked group to submenu
		    	 */
		    	 subMenu.removeGroup(subMenu.getSelectedItem().getText());
		    	 subMenu.setGroup(leastClickedGroup, leastClickedGroup.getGroupName());
				 subMenu.getSelectedItem().setText(leastClickedGroup.getGroupName());
				 subMenu.setGroupMenuItem(leastClickedGroup, subMenu.getSelectedItem());
				 
		    	 /*
		    	  * Step 4: find tab index
		    	  */
		    	 int index = visibleGroupsMap.get(leastClickedGroup);
		    	 
		    	/*
		    	 * Step 5: create a grouppresenter for the new selected group
		    	 */
		    	 GroupPresenter groupPresenter = new GroupPresenter(selectedGroup, lhUser);
				 GroupTabWidget groupTab = new GroupTabWidget(lhUser,selectedGroup,index);
				 groupTab.createUI();
				 
				 /*
				  * Step 6: remove least clicked group
				  */
				 tabMap.remove(index);
				 tabGroupPresenterMap.remove(leastClickedGroup);
				 tabGroupWidgetMap.remove(leastClickedGroup);
				 visibleGroupsMap.remove(leastClickedGroup);
				 
				 /*
				  * Step 7: add the selected group to the various maps
				  */
				 tabMap.put(index, selectedGroup.getGroupName());
				 tabGroupPresenterMap.put(selectedGroup, groupPresenter);
				 tabGroupWidgetMap.put(selectedGroup, groupTab);
				 visibleGroupsMap.put(selectedGroup, index);
				 
				 /*
				  * Step 8: remove old group from tabpanel and add the new one
				  */
				 tabPanel.remove(index);
				 tabPanel.insert(groupPresenter, groupTab, index);
			
				 /*
				  * Step 9: select the new tab index
				  */
				 tabPanel.selectTab(index);
			}
		};
		mainMenu.addItem("<img src=\"images/more.png\">", true, subMenu);
		for(int i = (maxGroupsToBeDisplayed); i<userGroupList.size(); i++){
				Group group = userGroupList.get(i);
				subMenu.addItem(group.getGroupName(), group, menuCommand);
				mostClickedGroupsMap.put(group,0);
		}
		DOM.setStyleAttribute(mainMenu.getElement(), "marginBottom", "3px");
		mainMenu.setStylePrimaryName("moreGroupsMenu");
		return mainMenu;
		
	}
	
	private void addImageHandler(Image plusMinusImage2) {
		plusMinusImage2.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				createGroupPopup.clear();
				createGroupPopup.setWidget(createNewGroupPopupUI());
				Widget arg0 = (Widget) event.getSource();
				int left = arg0.getAbsoluteLeft()-25;
				int top = arg0.getAbsoluteTop()+25;
				if(left >= (hp.getOffsetWidth()-300))
					left = arg0.getAbsoluteLeft()-300;
				createGroupPopup.setPopupPosition(left, top);
				createGroupPopup.show();
			}
		});
	}
 
	private VerticalPanel createNewGroupPopupUI(){
		final RadioButton rbCreateNew = new RadioButton("createGroup","Create new group");
		final RadioButton rbFromExisting = new RadioButton("createGroup","Create group from");
		final RadioButton rbCreateFavorite = new RadioButton("createGroup","Create favorite group");
		boolean favGroupExists = false;
		
		rbCreateNew.setValue(true, false);
		
		final Label grpChoiceErrorLbl = new Label("Select a group from the list");
		
		final ListBox lbGroups = new ListBox();
		lbGroups.addItem(" -- Existing groups -- ");
		for(Group group : userGroupList){
			lbGroups.addItem(group.getGroupName());
			if(group.getIsFavorite() == 1)
				favGroupExists = true;
		}
		
		lbGroups.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				grpChoiceErrorLbl.setVisible(false);
				rbFromExisting.setValue(true);
				
			}
		});
		VerticalPanel vp = new VerticalPanel();
		HorizontalPanel existingGroupsHp = new HorizontalPanel();
		existingGroupsHp.add(rbFromExisting);
		existingGroupsHp.add(lbGroups);
		DOM.setStyleAttribute(lbGroups.getElement(), "margin", "0px 3px");
		final Label grpNameErrorLbl = new Label();
		
		
		HorizontalPanel hp = new HorizontalPanel();
		Label lblGrpName = new Label("Group Name");
		final TextBox tbGroupName = new TextBox();
		tbGroupName.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				grpNameErrorLbl.setVisible(false);
			}
		});
		
		hp.add(lblGrpName);
		hp.add(tbGroupName);
		DOM.setStyleAttribute(lblGrpName.getElement(), "margin", "0px 3px 3px 20px");
		DOM.setStyleAttribute(tbGroupName.getElement(), "margin", "0px 3px 3px 3px");
		
		grpNameErrorLbl.setVisible(false);
		DOM.setStyleAttribute(grpNameErrorLbl.getElement(), "color", "red");
		grpChoiceErrorLbl.setVisible(false);
		DOM.setStyleAttribute(grpChoiceErrorLbl.getElement(), "color", "red");
		
		Button createButton = new Button("Create Group");
		createButton.addStyleName("loginButton");
		createButton.setWidth("50%");
		createButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final String newGroupName = tbGroupName.getText().trim();
				if(newGroupName.equals("")){
					grpNameErrorLbl.setText("Enter a group name");
					grpNameErrorLbl.setVisible(true);
				}else if(rbFromExisting.getValue() && lbGroups.getSelectedIndex() == 0 ){
					grpChoiceErrorLbl.setVisible(true);
				}else{
					ManageGroupServiceAsync service = GWT.create(ManageGroupService.class);
					service.getBlankGroupCategoryMap(new AsyncCallback<GroupCategoryMap>() {
						@Override
						public void onFailure(Throwable caught) {
						}
	
						@Override
						public void onSuccess(GroupCategoryMap result) {
							for(Group grp : userGroupList){
								if(grp.getGroupName().equalsIgnoreCase(newGroupName)){
									grpNameErrorLbl.setText("Group name exists.");
									grpNameErrorLbl.setVisible(true);
									return;
								}
									
							}
							Group newGroup = new Group();
							newGroup.setGroupName(newGroupName);
							newGroup.setUserId(lhUser.getUserId());
							newGroup.setIsMandatory(0);
							newGroup.setNewsFilterMode(0);
							newGroup.setNewsCenterId(lhUser.getUserSelectedNewsCenterID());
							
							//if from existing group
							if(rbFromExisting.getValue()){
								if(lbGroups.getSelectedIndex() != 0){
									String groupName = lbGroups.getItemText(lbGroups.getSelectedIndex());
									
									for(Group grp : userGroupList){
										if(grp.getGroupName().equals(groupName)){
											newGroup.setGroupParentId(grp.getGroupId());
											ArrayList selectedTagsList = grp.getGroupCategoryMap().getSelectedTags();
											result.selectTags(selectedTagsList);
											newGroup.setGroupCategoryMap(result);
											break;
										}
									}
									saveGroup(newGroup);
								}
								
							}else{
								newGroup.setGroupParentId(null);
								newGroup.setGroupCategoryMap(result);
								if(rbCreateFavorite.getValue())
									newGroup.setIsFavorite(1);
								else
									newGroup.setIsFavorite(0);
								saveGroup(newGroup);
							}
						}
					});
					
				}
			}
		});
		
		vp.add(rbCreateNew);
		vp.add(grpChoiceErrorLbl);
		vp.add(existingGroupsHp);
		if(favGroupExists == false){
			if(lhUser.getUserPermission().isFavoriteGroupPermitted() == 1)
				vp.add(rbCreateFavorite);
		}
		vp.add(grpNameErrorLbl);
		vp.add(hp);
		vp.add(createButton);
		vp.setCellHorizontalAlignment(createButton, HasHorizontalAlignment.ALIGN_LEFT);
		vp.setSpacing(5);
		return vp;
	}
	
	/**
	 * saves the newly created group
	 * @param group - Group
	 */
	private void saveGroup(final Group group){
		ManageGroupServiceAsync service = GWT.create(ManageGroupService.class);
		service.createGroup(group, new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				createGroupPopup.hide();
			}

			@Override
			public void onSuccess(Integer result) {
				createGroupPopup.hide();
				
				group.setGroupId(result);
				if(visibleGroupsMap.size() < maxGroupsToBeDisplayed ){
					if(group.getIsFavorite() == 1){
						userGroupList.add(0,group);
						createLayout();
					}else{
						userGroupList.add(group);
						int tabcount = tabPanel.getTabBar().getTabCount();
						tabMap.put(tabcount-2, group.getGroupName());
						
						GroupPresenter groupPresenter = new GroupPresenter(group, lhUser);
						GroupTabWidget groupTab = new GroupTabWidget(lhUser,group,tabcount-2);
						groupTab.createUI();
						
						tabPanel.insert(groupPresenter, groupTab, tabcount-2);
						tabGroupPresenterMap.put(group, groupPresenter);
						tabGroupWidgetMap.put(group, groupTab);
						mostClickedGroupsMap.put(group,0);
						tabPanel.selectTab(tabcount-2);
					}
				}else{
					if(group.getIsFavorite() == 1){
						userGroupList.add(0,group);
						createLayout();
					}else{
					userGroupList.add(group);
						showMenu();
						MenuItem item = subMenu.addItem(group.getGroupName(), group, menuCommand);
						mostClickedGroupsMap.put(group,0);
						subMenu.selectItem(item);
						item.getCommand().execute();
					}
				}
			}
		});
	}
	
	public void resetSize(int width, int height){
		GroupPresenter groupPresenter = getActiveGroupPresenter();
		groupPresenter.resetSize(width, height);
	}
	
	public List<Group> getUserGroupList() {
		return userGroupList;
	}

	public void setUserGroupList(List<Group> userGroupList) {
		this.userGroupList = userGroupList;
	}
	public static Group getCurrentActiveGroup() {
		return currentActiveGroup;
	}

	public static void setCurrentActiveGroup(Group currentActiveGroup) {
		GroupManager.currentActiveGroup = currentActiveGroup;
	}

	public static GroupPresenter getActiveGroupPresenter() {
		return activeGroupPresenter;
	}

	public static void setActiveGroupPresenter(GroupPresenter activeGroupPresenter) {
		GroupManager.activeGroupPresenter = activeGroupPresenter;
	}

	public LhMain getLhMainRef() {
		return lhMainRef;
	}

	public void setLhMainRef(LhMain lhMainRef) {
		this.lhMainRef = lhMainRef;
	}

	public LhUser getLhUser() {
		return lhUser;
	}

	public void setLhUser(LhUser lhUser) {
		this.lhUser = lhUser;
	}

	/**
	 * refreshes the UI
	 * @param groupNameTemp
	 */
	public void refreshUi(Group deletedGroup){
		String groupNameTemp = deletedGroup.getGroupName();
		int index = 99 ;
		Group tempGroup = null;
		
		for(Integer ind : tabMap.keySet()){
			if(groupNameTemp.matches(tabMap.get(ind))){
				index= ind;
				break;
			}
		}
		String groupName = tabMap.get(index);
		try{
			for(Group group: userGroupList){
				if(group.getGroupName().matches(groupName)){
					tempGroup = group;
					break;
				}
			}
			if(tempGroup.getIsMandatory() == 0)
				customGroupsCount-=1;
			
			userGroupList.remove(tempGroup);
			tabMap.remove(index);
			tabGroupPresenterMap.remove(tempGroup);
			tabGroupWidgetMap.remove(tempGroup);
			visibleGroupsMap.remove(tempGroup);
			 
			if(deletedGroup.getIsFavorite() == 1){
				createLayout();
			}else{
				HashMap<Integer,String> tempMap  = new HashMap<Integer,String>();
				
				for(Integer ind : tabMap.keySet()){
					if(ind>index){
						tempMap.put(ind, tabMap.get(ind));
					}
				}
				
				for(Integer ind : tempMap.keySet()){
					tabMap.remove(ind);
				}
				
				for(Integer ind : tempMap.keySet()){
					tabMap.put((ind-1), tempMap.get(ind));
				}
				tabPanel.remove(index);
				
				addnewTab();
				
				// checks if the >> image needs to be hidden
				if(userGroupList.size() <= maxGroupsToBeDisplayed)
					hideMenu();
				
				int indexToBeSelected = index-1;
				
				if(indexToBeSelected==-1)
					tabPanel.selectTab(index);
				else
					tabPanel.selectTab(indexToBeSelected);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addnewTab(){
		for(Group userGroup : userGroupList){
			boolean groupVisible = false;
			for(Group group : visibleGroupsMap.keySet()){
				if(userGroup.getGroupName().equals(group.getGroupName())){
					groupVisible = true;
					break;
				}
			}
			if(groupVisible == false){
				int tabcount = tabPanel.getTabBar().getTabCount();
				tabMap.put(tabcount-2, userGroup.getGroupName());
				
				GroupPresenter groupPresenter = new GroupPresenter(userGroup, lhUser);
				GroupTabWidget groupTab = new GroupTabWidget(lhUser,userGroup,tabcount-2);
				groupTab.createUI();
				
				tabGroupPresenterMap.put(userGroup, groupPresenter);
				tabGroupWidgetMap.put(userGroup, groupTab);
				visibleGroupsMap.put(userGroup, tabcount-2);
				subMenu.removeItem(subMenu.getGroupMenuItem(userGroup));
				subMenu.removeGroup(userGroup.getGroupName());
				tabPanel.insert(groupPresenter, groupTab, tabcount-2);
				break;
			}
		}
	}
	public int getDisplayedGroupsNumber() {
		return maxGroupsToBeDisplayed;
	}

	public void setDisplayedGroupsNumber(int displayedGroupsNumber) {
		this.maxGroupsToBeDisplayed = displayedGroupsNumber;
	}
}