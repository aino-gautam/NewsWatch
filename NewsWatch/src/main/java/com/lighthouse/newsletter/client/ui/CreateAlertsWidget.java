package com.lighthouse.newsletter.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.appUtils.client.ProgressIndicatorWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.client.service.ManageGroupService;
import com.lighthouse.group.client.service.ManageGroupServiceAsync;
import com.lighthouse.newsletter.client.UserNewsletterAlertConfigService;
import com.lighthouse.newsletter.client.UserNewsletterAlertConfigServiceAsync;
import com.lighthouse.newsletter.client.domain.NewsletterGroupSelectionConfig;
import com.lighthouse.newsletter.client.domain.UserNewsletterAlertConfig;

public class CreateAlertsWidget extends Composite implements ClickHandler{
	
	private VerticalPanel baseVp;
	private List<Group> availableGroupsList;
	private HashMap<CheckBox, Group> mandatoryGroupCBMap;
	private HashMap<CheckBox, Group> userDefinedGroupCBMap;
	private UserNewsletterAlertConfig userAlert;
	private NewsletterGroupSelectionConfig groupSelectionConfig;
	private AlertsConfigurationWidget parentWidget;
	private String selectedGroupName;
	private ListBox frequencyBox;
	//private ListBox alertChoiceBox;
	//private ListBox prefDeviceBox;
	private TextBox alertBox;
	CheckBox checkBox;
	private static final String CREATE_ALERT = "Create alert";
	private static final String RESET = "Reset";
	private static final String UPDATE = "Update";
	private static final String DELETE = "Delete";
	private boolean isInitialized = false;
	private HorizontalPanel loaderPanel = new HorizontalPanel();
	private Label errorLabel = new Label();
	private Label notificationLabel = new Label();
	/*Image loadingImg = new Image("images/circle_loader.gif");
	Label loadingMsg = new Label("Creating alert...");*/
	private ProgressIndicatorWidget loader = new ProgressIndicatorWidget(true);
	private VerticalPanel vpHolder = new VerticalPanel();
	private DeckPanel groupNamesDeckPanel;
	private HorizontalPanel hpMandatoryGroup;
	
	
	public CreateAlertsWidget(AlertsConfigurationWidget parentWidget){
		baseVp = new VerticalPanel();
		baseVp.setWidth("100%");
		baseVp.setHeight("99%");
		this.parentWidget = parentWidget;
		initWidget(baseVp);
	}

	/**
	 * initializes the widget. Fetches a list of all groups available for user
	 */
	public void initialize(){
		ManageGroupServiceAsync groupService = GWT.create(ManageGroupService.class);
		groupService.getAvailableGroups(new AsyncCallback<List<Group>>() {
			
			@Override
			public void onSuccess(List<Group> result) {
				isInitialized = true;
				setAvailableGroupsList(result);
				createUI();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}
	
	/**
	 * creates the UI of the page
	 */
	public void createUI(){
		baseVp.clear();
		vpHolder.clear();
		vpHolder.setWidth("100%");
		
		vpHolder.add(notificationLabel);
		notificationLabel.setVisible(false);
		notificationLabel.setStylePrimaryName("notificationLabel");
		
		errorLabel.setVisible(false);
		errorLabel.setStylePrimaryName("notificationLabel");
		
		createGroupsSelectionPanel();
		
		vpHolder.add(createHeader("Preference settings for alerts", "These settings control how you receive your alerts"));
		FlexTable preferencesFlex = createPreferencesComponent();
		vpHolder.add(preferencesFlex);
		vpHolder.setCellHorizontalAlignment(preferencesFlex, HasHorizontalAlignment.ALIGN_CENTER);
		
		HorizontalPanel buttonPanel = createButtonPanel();
		vpHolder.add(buttonPanel);
		vpHolder.setCellHorizontalAlignment(buttonPanel, HasHorizontalAlignment.ALIGN_CENTER);
		vpHolder.add(loaderPanel);
		vpHolder.setCellHorizontalAlignment(loaderPanel, HasHorizontalAlignment.ALIGN_CENTER);
		//loaderPanel.setVisible(false);
		
		loaderPanel.add(loader);
		//loaderPanel.add(loadingMsg);
		
		baseVp.add(vpHolder);
	
	}
	
	private void createGroupsSelectionPanel(){
		if(parentWidget.getLhUserPermission().isGroupsPermitted() == 1){
			vpHolder.add(createHeader("Group selection for alerts", "Select the groups you want as a part of the alert"));
			HorizontalPanel groupSelectionHp = createGroupSelectionComponent();
			vpHolder.add(groupSelectionHp);
			hpMandatoryGroup.addStyleName("groupSelected");
			groupNamesDeckPanel.showWidget(1);
			vpHolder.setCellHorizontalAlignment(groupSelectionHp, HasHorizontalAlignment.ALIGN_CENTER);
		}
		
	}
	/**
	 * creates a header panel
	 * @param headerText - the header text of the panel
	 * @param subHeaderText - the subheader text of the panel
	 * @return HorizontalPanel
	 */
	private VerticalPanel createHeader(String headerText, String subHeaderText){
		VerticalPanel headerVPanel = new VerticalPanel();
		headerVPanel.setWidth("100%");
		Label headerLabel = new Label(headerText);
		Label subheaderLabel = new Label(subHeaderText);
		headerLabel.setStylePrimaryName("headerText");
		subheaderLabel.setStylePrimaryName("suggestionText");
		headerVPanel.add(headerLabel);
		headerVPanel.add(subheaderLabel);
		headerVPanel.setStylePrimaryName("headerPanel");
		return headerVPanel;
		
	}
	
	/**
	 * creates the group selections component
	 * @return
	 */
	private HorizontalPanel createGroupSelectionComponent(){
		mandatoryGroupCBMap = new HashMap<CheckBox, Group>();
		userDefinedGroupCBMap = new HashMap<CheckBox, Group>();
		
		HorizontalPanel hpGroupSelection = new HorizontalPanel();
		
		groupNamesDeckPanel = new DeckPanel();
		Label instructionLabel = new Label("Click on a group type to view the groups available");
		instructionLabel.setStylePrimaryName("instructionLabel");
		
		groupNamesDeckPanel.add(instructionLabel);
		groupNamesDeckPanel.add(createGroupFlexComponent(true));
		groupNamesDeckPanel.add(createGroupFlexComponent(false));
		groupNamesDeckPanel.showWidget(0);
		// if the user has a group selected then mark the checkbox 
		
		if(getUserAlert() != null){
			ArrayList<Group> alertGroups = getUserAlert().getAlertGroupList();
			for(CheckBox cb : mandatoryGroupCBMap.keySet()){
				Group group = mandatoryGroupCBMap.get(cb);
				for(Group grp : alertGroups){
					if(grp.getGroupId() == group.getGroupId()){
						cb.setValue(true);
						break;
					}
				}
			}
			
			for(CheckBox cb : userDefinedGroupCBMap.keySet()){
				Group group = userDefinedGroupCBMap.get(cb);
				for(Group grp : alertGroups){
					if(grp.getGroupId() == group.getGroupId()){
						cb.setValue(true);
						break;
					}
				}
			}
			groupNamesDeckPanel.showWidget(1);
		}
		VerticalPanel vpGroupType = new VerticalPanel();
		
		Label mandatoryGroupLb = new Label("Locked groups");
		mandatoryGroupLb.setStylePrimaryName("groupHeaderText");
		hpMandatoryGroup = new HorizontalPanel();
		hpMandatoryGroup.add(mandatoryGroupLb);
		hpMandatoryGroup.setCellHorizontalAlignment(mandatoryGroupLb, HasHorizontalAlignment.ALIGN_CENTER);
		hpMandatoryGroup.setWidth("100%");
		
		Label userDefinedGroupLb = new Label("Your groups");
		userDefinedGroupLb.setStylePrimaryName("groupHeaderText");
		final HorizontalPanel hpUserDefinedGroup = new HorizontalPanel();
		hpUserDefinedGroup.add(userDefinedGroupLb);
		hpUserDefinedGroup.setCellHorizontalAlignment(userDefinedGroupLb, HasHorizontalAlignment.ALIGN_CENTER);
		hpUserDefinedGroup.setWidth("100%");
		
		mandatoryGroupLb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hpMandatoryGroup.addStyleName("groupSelected");
				hpUserDefinedGroup.removeStyleName("groupSelected");
				groupNamesDeckPanel.showWidget(1);
			}
		});
		
		userDefinedGroupLb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hpUserDefinedGroup.addStyleName("groupSelected");
				hpMandatoryGroup.removeStyleName("groupSelected");
				groupNamesDeckPanel.showWidget(2);
			}
		});
		
		vpGroupType.add(hpMandatoryGroup);
		vpGroupType.setCellVerticalAlignment(hpMandatoryGroup, HasVerticalAlignment.ALIGN_MIDDLE);
	
		vpGroupType.add(hpUserDefinedGroup);
		vpGroupType.setCellVerticalAlignment(hpUserDefinedGroup, HasVerticalAlignment.ALIGN_MIDDLE);
		
		vpGroupType.setStylePrimaryName("vpGroup");
		vpGroupType.setHeight("100%");
		vpGroupType.setWidth("100%");
		
		hpGroupSelection.add(vpGroupType);
		hpGroupSelection.setCellWidth(vpGroupType, "45%");
		hpGroupSelection.add(groupNamesDeckPanel);
		hpGroupSelection.setStylePrimaryName("groupSelectionPanel");
		hpGroupSelection.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		
		hpGroupSelection.setWidth("75%");
		hpGroupSelection.setHeight("100px");
		return hpGroupSelection;
	}
	
	/**
	 * creates the flex table component for group listing
	 * @param isMandatory - whether the group isMandatory or not
	 * @return FlexTable
	 */
	private FlexTable createGroupFlexComponent(boolean isMandatory){
		FlexTable groupsFlex = new FlexTable();
		
		if(isMandatory){
			int row = 0;
			for(Group group : availableGroupsList){
				if(group.getIsMandatory() == 1){
					checkBox = new CheckBox();
					Label groupNameLabel = new Label(group.getGroupName());
					//groupNameLabel.addClickHandler(this);
					groupNameLabel.setStylePrimaryName("groupNames");
					
					mandatoryGroupCBMap.put(checkBox, group);
					
					HorizontalPanel hp = new HorizontalPanel();
					hp.setSpacing(0);
					hp.add(checkBox);
					hp.add(groupNameLabel);
					hp.setCellVerticalAlignment(groupNameLabel, HasVerticalAlignment.ALIGN_MIDDLE);
					groupsFlex.setWidget(row, 0, hp);
					row+=2;
				}
			}
		}
		else{
			int row = 0;
			for(Group group : availableGroupsList){
				if(group.getIsMandatory()==0){
					checkBox = new CheckBox();
					Label groupNameLabel = new Label(group.getGroupName());
					//groupNameLabel.addClickHandler(this);
					groupNameLabel.setStylePrimaryName("groupNames");
					userDefinedGroupCBMap.put(checkBox, group);
					/*groupsFlex.setWidget(row, 0, checkBox);
					groupsFlex.setWidget(row, 1, groupNameLabel);*/
					
					HorizontalPanel hp = new HorizontalPanel();
					hp.setSpacing(0);
					hp.add(checkBox);
					hp.add(groupNameLabel);
					hp.setCellVerticalAlignment(groupNameLabel, HasVerticalAlignment.ALIGN_MIDDLE);
					groupsFlex.setWidget(row, 0, hp);
					
					row+=2;
				}
			}
		}
		return groupsFlex;
	}
	
	private void clear(){
		alertBox.setText("");
		errorLabel.setVisible(false);
		notificationLabel.setVisible(false);
		
		for(CheckBox cb : mandatoryGroupCBMap.keySet()){
			cb.setValue(false);
		}
		for(CheckBox cb : userDefinedGroupCBMap.keySet()){
			cb.setValue(false);
		}
	}
	/**
	 * creates the component for preference selection
	 * @return FlexTable
	 */
	private FlexTable createPreferencesComponent(){
		FlexTable preferencesFlex = new FlexTable();
		// if user alert object is not null then those values should be prepopulated.
		
		HTML instructionHTML = new HTML("<p>Multiple alerts can be combined into one newsletter by selecting <i>\"Send me a single combined mail\"</i> under the tab <i>\"All alerts\"</i></p>");
		instructionHTML.setWordWrap(true);
		instructionHTML.setStylePrimaryName("instructionLabel");
		
		Label frequencyName=new Label("Frequency of alerts:");
		frequencyName.setStylePrimaryName("label");
		frequencyBox=new ListBox();
		frequencyBox.addItem("Daily");
		frequencyBox.addItem("Weekly");
		
		/*Label choiceOfAlert=new Label("Choice of alert:");
		choiceOfAlert.setStylePrimaryName("label");
		alertChoiceBox=new ListBox();
		alertChoiceBox.addItem("Individual Alert");
		alertChoiceBox.addItem("Merged Alert");*/
		
		/*Label preferedDevice=new Label("Preferred news reading device:");
		preferedDevice.setStylePrimaryName("label");
		prefDeviceBox=new ListBox();
		prefDeviceBox.addItem("IPAD");
		prefDeviceBox.addItem("MAIL");*/
		
		Label alertName=new Label("Alert name:");
		alertName.setStylePrimaryName("label");
		alertBox=new TextBox();

		errorLabel.setVisible(false);
		
		if(getUserAlert()!=null){
			for(int i = 0; i<frequencyBox.getItemCount();i++){
				if(getUserAlert().getFrequency().equalsIgnoreCase(frequencyBox.getItemText(i))){
					frequencyBox.setItemSelected(i, true);
					break;
				}
			}
			/*if(getUserAlert().getIsSingle() == 0)
				frequencyBox.setItemSelected(1, true);
			else
				frequencyBox.setItemSelected(0, true);*/
			
			/*for(int i = 0; i<prefDeviceBox.getItemCount();i++){
				if(getUserAlert().getPrefDevice().equalsIgnoreCase(prefDeviceBox.getItemText(i))){
					prefDeviceBox.setItemSelected(i, true);
					break;
				}
			}*/
			alertBox.setText(getUserAlert().getAlertName());
		}
		
		preferencesFlex.getFlexCellFormatter().setColSpan(2, 0, 2);
		preferencesFlex.setWidget(2, 0, instructionHTML);
		
		preferencesFlex.setWidget(8,0,frequencyName);
		preferencesFlex.setWidget(8,1,frequencyBox);
		//preferencesFlex.setWidget(8,0,choiceOfAlert);
		//preferencesFlex.setWidget(8,1,alertChoiceBox);
		//preferencesFlex.setWidget(14,0,preferedDevice);
		//preferencesFlex.setWidget(14,1,prefDeviceBox);
		preferencesFlex.getFlexCellFormatter().setColSpan(14, 0, 2);
		preferencesFlex.setWidget(14,0,errorLabel);
		preferencesFlex.setWidget(15,0,alertName);
		preferencesFlex.setWidget(15,1,alertBox);
		
		return preferencesFlex;
	}
	
	private HorizontalPanel createButtonPanel(){
		HorizontalPanel buttonPanel = new HorizontalPanel();
		if(getUserAlert()!= null){
			Button updateButton = new Button(UPDATE);
			Button deleteButton = new Button(DELETE);
			updateButton.addStyleName("loginButton");
			deleteButton.addStyleName("loginButton");
			updateButton.addClickHandler(this);
			deleteButton.addClickHandler(this);
			buttonPanel.add(updateButton);
			buttonPanel.add(deleteButton);
			
		}else{
			Button resetButton = new Button(RESET);
			Button createAlertButton = new Button(CREATE_ALERT);
			createAlertButton.setWidth("100%");
			createAlertButton.addStyleName("loginButton");
			resetButton.addStyleName("loginButton");
			resetButton.addClickHandler(this);
			createAlertButton.addClickHandler(this);
			buttonPanel.add(createAlertButton);
			buttonPanel.add(resetButton);
		}
		buttonPanel.setSpacing(10);
		return buttonPanel;
		
	}
	
	public List<Group> getAvailableGroupsList() {
		return availableGroupsList;
	}

	public void setAvailableGroupsList(List<Group> availableGroupsList) {
		this.availableGroupsList = availableGroupsList;
	}

	public UserNewsletterAlertConfig getUserAlert() {
		return userAlert;
	}

	public void setUserAlert(UserNewsletterAlertConfig userAlert) {
		this.userAlert = userAlert;
	}

	public ArrayList<NewsletterGroupSelectionConfig> getSelectedGroupsForAlert(){
		ArrayList<NewsletterGroupSelectionConfig> groupsSelectionList = new ArrayList<NewsletterGroupSelectionConfig>();
		
		if(mandatoryGroupCBMap==null && userDefinedGroupCBMap ==null)
			return null;
			
		for(CheckBox cb : mandatoryGroupCBMap.keySet()){
			if(cb.getValue()){
				Group group = mandatoryGroupCBMap.get(cb);
				groupSelectionConfig=new NewsletterGroupSelectionConfig();
				groupSelectionConfig.setGroupId(group.getGroupId());
				groupSelectionConfig.setUserId(group.getUserId());
				groupSelectionConfig.setNewscenterId(group.getNewsCenterId());
				groupsSelectionList.add(groupSelectionConfig);
			}
		}
		for(CheckBox cb : userDefinedGroupCBMap.keySet()){
			if(cb.getValue()){
				Group group = userDefinedGroupCBMap.get(cb);
				groupSelectionConfig=new NewsletterGroupSelectionConfig();
				groupSelectionConfig.setGroupId(group.getGroupId());
				groupSelectionConfig.setUserId(group.getUserId());
				groupSelectionConfig.setNewscenterId(group.getNewsCenterId());
				groupsSelectionList.add(groupSelectionConfig);
			}
		}
		return groupsSelectionList;
	}
	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() instanceof Button){
			Button button = (Button)event.getSource();
			
			if(button.getText().equalsIgnoreCase(CREATE_ALERT)){
				loader.setLoadingMessage("Creating alert...");
				loader.enable();
				if(notificationLabel.isVisible())
					notificationLabel.setVisible(false);
				if(errorLabel.isVisible())
					errorLabel.setVisible(false);
				
				String alertName=alertBox.getText();
				if(alertName.equals("")){
					errorLabel.setText("Please Enter the Alert Name");
					errorLabel.setVisible(true);
					loaderPanel.setVisible(false);
					return;
				}
				String frequency=frequencyBox.getItemText(frequencyBox.getSelectedIndex());
			    userAlert=new UserNewsletterAlertConfig();
				userAlert.setAlertName(alertName);
				userAlert.setFrequency(frequency);
				userAlert.setIsSingle(0);

			ArrayList<NewsletterGroupSelectionConfig> groupsSelectionList = getSelectedGroupsForAlert();
			UserNewsletterAlertConfigServiceAsync async=GWT.create(UserNewsletterAlertConfigService.class);
			
			if(groupsSelectionList==null){
				
				async.createAlertAndAssociateDefaultGroup(userAlert, new AsyncCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean result) {
						loader.disable();
						if(result == false){
							errorLabel.setText("Alert name already exists");
							errorLabel.setVisible(true);
						}else{
							notificationLabel.setText("Alert successfully created");
							notificationLabel.setVisible(true);
							alertBox.setText("");
							errorLabel.setVisible(false);
							
						}
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						notificationLabel.setText("Unable to create alert");
						notificationLabel.setVisible(true);
						loader.disable();
						
					}
				});
				
				return;
			}
			
			if(groupsSelectionList.size()==0){
				errorLabel.setText("Please checked atleast one group for alert");
				errorLabel.setVisible(true);
				loader.disable();
				return;
			}
			
			async.createUserNewsletterAlert(userAlert,groupsSelectionList,-1,-1, new AsyncCallback<UserNewsletterAlertConfig>() {
			
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						notificationLabel.setText("Unable to create alert");
						notificationLabel.setVisible(true);
						loader.disable();
					}

					@Override
					public void onSuccess(UserNewsletterAlertConfig result) {
						loader.disable();
						if(result == null){
							errorLabel.setText("Alert name already exists");
							errorLabel.setVisible(true);
						}else{
							notificationLabel.setText("Alert successfully created");
							notificationLabel.setVisible(true);
							clear();
						}
						
					}
				});
			}else if(button.getText().equalsIgnoreCase(RESET)){
				clear();
				
			}else if(button.getText().equalsIgnoreCase(UPDATE)){
				loader.setLoadingMessage("Updating alert...");
				loader.enable();
				if(notificationLabel.isVisible())
					notificationLabel.setVisible(false);
				if(errorLabel.isVisible())
					errorLabel.setVisible(false);
				
				String alertName=alertBox.getText();
				String frequency=frequencyBox.getItemText(frequencyBox.getSelectedIndex());
			    getUserAlert().setAlertName(alertName);
			    getUserAlert().setFrequency(frequency);
				ArrayList<NewsletterGroupSelectionConfig> groupsSelectionList = getSelectedGroupsForAlert();
				
				UserNewsletterAlertConfigServiceAsync async=GWT.create(UserNewsletterAlertConfigService.class);
				async.updateUserNewsletterAlert(getUserAlert(),groupsSelectionList, new AsyncCallback<Boolean>() {

					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						notificationLabel.setText("Unable to update alert");
						notificationLabel.setVisible(true);
						loader.disable();
					}
					@Override
					public void onSuccess(Boolean result) {
						loader.disable();
						if(result == false){
							errorLabel.setText("Alert could not be updated");
							errorLabel.setVisible(true);
						}else{
							notificationLabel.setText("Alert successfully updated");
							notificationLabel.setVisible(true);
							clear();
							parentWidget.getAllAlertsWidget().setInitialized(false);
							parentWidget.toggleTab(1);
						}
					}
				});

			}else if(button.getText().equalsIgnoreCase(DELETE)){
				loader.setLoadingMessage("Deleting alert...");
				loader.enable();
				int alertId=getUserAlert().getAlertId();
				UserNewsletterAlertConfigServiceAsync async=GWT.create(UserNewsletterAlertConfigService.class);
				async.deleteAlert(alertId, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						notificationLabel.setText("Unable to delete alert");
						notificationLabel.setVisible(true);	
						loader.disable();
					}

					@Override
					public void onSuccess(Boolean result) {
						loader.disable();
						notificationLabel.setText("Alert successfully deleted");
						notificationLabel.setVisible(true);
						clear();
						parentWidget.getAllAlertsWidget().setInitialized(false);
						parentWidget.toggleTab(1);
					}
				});
			}
		}
		else if(event.getSource() instanceof Label){
			Label grpLabel=(Label) event.getSource();
			selectedGroupName=grpLabel.getText();
			checkBox.setValue(true);
		}

	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}
}
