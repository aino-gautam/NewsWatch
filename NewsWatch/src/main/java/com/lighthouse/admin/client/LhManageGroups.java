/**
 * 
 */
package com.lighthouse.admin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.client.service.ManageGroupService;
import com.lighthouse.group.client.service.ManageGroupServiceAsync;



/**
 * 
 * @author prachi@ensarm.com
 *
 */
public class LhManageGroups extends Composite implements ClickHandler{
    
	private String userSelectedIndustry="";
	private String userSelectedIndustryName;
	private int userSelectedIndustryId;
	
	private HashMap<CheckBox, Group> mandatoryGroupCBMap;
	private HashMap<RadioButton, Group> mandatoryGroupRBMap;
	private List<Group> availableGroupsList;
	private VerticalPanel baseVp;
	private boolean isInitialized = false;
	CheckBox checkBox;
	RadioButton radioButton;
	 
	private Group selectedGroup;
	RootPanel rootPanel;
	Logger logger = Logger.getLogger(LhManageGroups.class.getName());
	 
	public LhManageGroups(String userIndustryName, int userIndustryId) {
		baseVp = new VerticalPanel();
		baseVp.clear();
		baseVp.setWidth("100%");
		baseVp.setHeight("99%");
		
		userSelectedIndustryName = userIndustryName;
		userSelectedIndustryId = userIndustryId;
		setUserSelectedIndustryId(userIndustryId);
		
		initWidget(baseVp);
		
		
	}
    
	public void initialise(){
		ManageGroupServiceAsync groupService = GWT.create(ManageGroupService.class);
		groupService.getAvailableGroups(new AsyncCallback<List<Group>>() {
			
			@Override
			public void onSuccess(List<Group> result) {
				isInitialized = true;
				setAvailableGroupsList(result); 
				baseVp.clear();
				createUI();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
		
	}
	
	public void createUI(){
		Label grpLabel1 = new Label("Please select mandatory groups for the portal from the list below");
		grpLabel1.setStylePrimaryName("labelSelection");
		
		VerticalPanel vPanel1 = new VerticalPanel();
		vPanel1.add(grpLabel1);
		vPanel1.add(createLabelsmall("*Please select atleast one default group and click save"));
		vPanel1.setSpacing(15);
		
		baseVp.add(vPanel1);
		baseVp.add(createFlexGroupComponent());
		Button saveButton1 = new Button();
		saveButton1.addClickHandler(this);
		saveButton1.setText("SAVE");
		saveButton1.setStylePrimaryName("buttonOkSave");
		
		HorizontalPanel hButtonPanel1 = new HorizontalPanel();
		hButtonPanel1.setSpacing(20);
		hButtonPanel1.add(saveButton1);
		hButtonPanel1.setCellHorizontalAlignment(saveButton1, HasHorizontalAlignment.ALIGN_CENTER);
		hButtonPanel1.setWidth("40%");
		baseVp.add(hButtonPanel1);
	}
	
	
	public Label createLabel(String text){
		Label label = new Label(text);
		label.setStylePrimaryName("labelTitle");
		return label;
	}
	
	
	private Label createLabelsmall(String text){
		Label label = new Label(text);
		label.setStylePrimaryName("ThirdLoginTitleLeft");
		return label;
	}
	
	/**
	 * This method will create the flex component required to manage groups  
	 * @return
	 */
	private FlexTable createFlexGroupComponent(){
        mandatoryGroupCBMap = new HashMap<CheckBox, Group>();
        mandatoryGroupRBMap = new HashMap<RadioButton, Group>(); 
		FlexTable flexGroupTable = new FlexTable();
		flexGroupTable.setWidth("40%");
		int row = 0;
		
		flexGroupTable.setWidget(0, 0, createLabel(" Mandatory "));
		flexGroupTable.setWidget(0, 4, createLabel(" Group "));
		flexGroupTable.setWidget(0, 8, createLabel(" Default "));
		
		
		flexGroupTable.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		flexGroupTable.getCellFormatter().setAlignment(0, 8, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
		
		
		flexGroupTable.getCellFormatter().setWidth(0, 0,"100px");
		flexGroupTable.getCellFormatter().setWidth(0, 4,"100px");
		flexGroupTable.getCellFormatter().setWidth(0, 8,"100px");
		
		for(Group group : availableGroupsList){
			int col =0;
			row +=2;
			checkBox = new CheckBox();
			flexGroupTable.getFlexCellFormatter().setHorizontalAlignment(row, col, HasHorizontalAlignment.ALIGN_CENTER);
			flexGroupTable.setWidget(row, col, checkBox);
			 
			if(group.getIsMandatory() == 1)
				checkBox.setValue(true);
			mandatoryGroupCBMap.put(checkBox, group);
			
			col +=4;
			Label groupNameLabel = new Label(group.getGroupName());
			flexGroupTable.getFlexCellFormatter().setHorizontalAlignment(row, col, HasHorizontalAlignment.ALIGN_LEFT);
			flexGroupTable.setWidget(row, col, groupNameLabel);
			
			
			col +=4;
			radioButton = new RadioButton("group", "");
			
			if(group.getIsDefaultGroup() == 1)
				radioButton.setValue(true);
			
			flexGroupTable.getFlexCellFormatter().setHorizontalAlignment(row, col, HasHorizontalAlignment.ALIGN_CENTER);
			flexGroupTable.setWidget(row, col, radioButton);
			mandatoryGroupRBMap.put(radioButton, group);
		}
		
		
		return flexGroupTable;
	}
	
	/**
	 * Selects the groups which are checked from the map 
	 * @return selectedGroupList will return only the checked groups
	 */
	public ArrayList<Group> getSelectedMandatoryGroups(){
		ArrayList<Group> selectedGroupList = new ArrayList<Group>();
		
		for(CheckBox checkBox :mandatoryGroupCBMap.keySet()){
			if(checkBox.getValue()){
				Group group = mandatoryGroupCBMap.get(checkBox);
			    selectedGroup = new Group();
			    selectedGroup.setGroupId(group.getGroupId());
			    selectedGroup.setGroupName(group.getGroupName());
			    selectedGroup.setIsMandatory(1);
			    selectedGroupList.add(selectedGroup);
			}
		}
		
		return selectedGroupList;
		
	}
	
	public Group getDefaultSelectedGroup(){
		Group grpDefault = new Group();
		for(RadioButton radioButton : mandatoryGroupRBMap.keySet()){
			if(radioButton.getValue()){
				Group group = mandatoryGroupRBMap.get(radioButton);
				
				grpDefault.setGroupId(group.getGroupId());
				grpDefault.setGroupName(group.getGroupName());
				grpDefault.setIsDefaultGroup(1);
				
			}
		}
		return grpDefault;
	}
	
	
	boolean checkConditionsForDefaultAndMandatory(ArrayList<Group> groupselectionList, Group defaultGroup){
		    try{
			if(defaultGroup.getGroupId()==0 || groupselectionList.size()<0){               //No default group selected
				PopUpForForgotPassword popup = new PopUpForForgotPassword(" Please select atleast one default group ");
				popup.setPopupPosition(600, 300);
				popup.show();
				return false;
			}
		    if(groupselectionList.size()>0 && defaultGroup.getGroupId()==0){     //mandatory selected, default not selected
		    	PopUpForForgotPassword popup = new PopUpForForgotPassword(" Please select atleast one default group ");
				popup.setPopupPosition(600, 300);
				popup.show();
				return false;
		    }
		    if(groupselectionList.size()<=0 && defaultGroup.getGroupId()!=0){    //mandatory not selected, default selected
		    	/*PopUpForForgotPassword popup = new PopUpForForgotPassword();
		    	popup.createPopUpForForgotPasswordChanged(" You have not selected mandatory groups.Would you like to continue ?");
		    	popup.setPopupPosition(600, 300);
				popup.show();*/
				
				return true;
		    }
		    }
		    catch(Exception e){
		    	logger.log(Level.INFO , " [ Check for groups failed ] ");
		    	e.printStackTrace();
		    }
			return true;
	}
	
	
	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() instanceof Button){
			Button button = (Button)event.getSource();
			if(button.getText().equals("SAVE")){
				ArrayList<Group> groupselectionList = getSelectedMandatoryGroups();
				Group defaultGroup = getDefaultSelectedGroup();
				
				boolean check = checkConditionsForDefaultAndMandatory(groupselectionList,defaultGroup);   
				if(check){
				ManageGroupServiceAsync groupService = GWT.create(ManageGroupService.class);
                groupService.updateMandatoryGroups(groupselectionList,defaultGroup,new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
					 caught.printStackTrace();
					 logger.log(Level.INFO, "[ Updation failed ]"); 	
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result == true){
							logger.log(Level.INFO, "[ Updated successfully ]");
							PopUpForForgotPassword popup = new PopUpForForgotPassword("Your selections have been saved successfully");
							popup.setPopupPosition(600, 300);
							popup.show();
							
						}
						
					}
				
                });
				}	
				
				
			}
			
		}
			
	}
    
	public String getUserSelectedIndustry() {
		return userSelectedIndustry;
	}

	public void setUserSelectedIndustry(String userSelectedIndustry) {
		this.userSelectedIndustry = userSelectedIndustry;
	}

	public String getUserSelectedIndustryName() {
		return userSelectedIndustryName;
	}

	public void setUserSelectedIndustryName(String userSelectedIndustryName) {
		this.userSelectedIndustryName = userSelectedIndustryName;
	}

	public int getUserSelectedIndustryId() {
		return userSelectedIndustryId;
	}

	public void setUserSelectedIndustryId(int userSelectedIndustryId) {
		this.userSelectedIndustryId = userSelectedIndustryId;
	}

	public List<Group> getAvailableGroupsList() {
		return availableGroupsList;
	}

	public void setAvailableGroupsList(List<Group> availableGroupsList) {
		this.availableGroupsList = availableGroupsList;
	}

	
	

}
