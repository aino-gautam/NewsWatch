package com.lighthouse.admin.client;

import java.util.ArrayList;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.login.user.client.service.LhUserService;
import com.lighthouse.login.user.client.service.LhUserServiceAsync;

/**
 * 
 * @author pallavi
 *
 */
public class EditLhUserPermission extends Composite implements ClickHandler{
	
	private FlexTable flex = new FlexTable();
	private DecoratorPanel decorator = new DecoratorPanel();
	private DockPanel dock = new DockPanel();
	private HorizontalPanel hpbuttonPanel = new HorizontalPanel();
	private HorizontalPanel hpaneldecorator = new HorizontalPanel();
	private HorizontalPanel hpanel = new HorizontalPanel();
	private Button btnSelect=new Button("Select");
	RootPanel panel;
	private Button savebtn = new Button("Save");
	private SuggestBox	emailSuggestionTextbox;
	private final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
	int userSelectedIndustryId;
	int userId;
	CheckBox cbMailAlerts=new CheckBox("");
	CheckBox cbGroups=new CheckBox("");
	CheckBox cbReports=new CheckBox("");
	CheckBox cbComments=new CheckBox("");
	CheckBox cbViews=new CheckBox("");
	CheckBox cbSearch=new CheckBox("");
	CheckBox cbPrimaryHeadLine=new CheckBox("");
	CheckBox cbRss=new CheckBox("");
	CheckBox cbShare=new CheckBox("");
	CheckBox cbPulse=new CheckBox("");
	CheckBox cbFavoriteGroup=new CheckBox("");
	
	
	public EditLhUserPermission(int userIndustryId){
		userSelectedIndustryId = userIndustryId;
		createSuggestionWidget();
		hpanel.add(createLabel("Please select a user"));
		hpanel.add(emailSuggestionTextbox);
		hpanel.add(btnSelect);
		hpanel.setSpacing(2);
		flex.setWidget(0, 0, hpanel);
		flex.setWidget(1, 0,createLabel("Feature"));
		flex.setWidget(1, 1,createLabelTitle("Activate"));
		savebtn.addClickHandler(this);
		btnSelect.addClickHandler(this);
		savebtn.setStylePrimaryName("buttnOkAdmin");
		addInFlex();
		decorator.add(flex);
		hpaneldecorator.add(decorator);
		dock.setSpacing(9);
		hpbuttonPanel.setSpacing(9);
		hpbuttonPanel.add(savebtn);
		hpbuttonPanel.setCellHorizontalAlignment(savebtn, HasHorizontalAlignment.ALIGN_CENTER);
		dock.add(hpaneldecorator, DockPanel.CENTER);
		dock.add(hpbuttonPanel, DockPanel.SOUTH);
		cbMailAlerts.addClickHandler(this);
		cbGroups.addClickHandler(this);
		cbReports.addClickHandler(this);
		cbComments.addClickHandler(this);
		cbViews.addClickHandler(this);
		cbSearch.addClickHandler(this);
		cbPrimaryHeadLine.addClickHandler(this);
		cbShare.addClickHandler(this);
		cbPulse.addClickHandler(this);
		cbFavoriteGroup.addClickHandler(this);
		
		initWidget(dock);
	}
	
	public Label createLabel(String text){
		Label label = new Label(text);
		label.setStylePrimaryName("labelTitle");
		return label;
	}
	
	public Label createLabelTitle(String text){
		Label label = new Label(text);
		label.setStylePrimaryName("labelTitle");
		return label;
	}
		
	public void onModuleLoad() {
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}

	public void addInFlex()
	{		
		flex.setWidget(2,0,new Label("Mail Alert"));
		flex.setWidget(3,0,new Label("Groups"));
        flex.setWidget(4,0,new Label("Reports"));
		flex.setWidget(5,0,new Label("Comments"));
		flex.setWidget(6,0,new Label("Views"));
		flex.setWidget(7,0,new Label("Search"));
		flex.setWidget(8,0,new Label("PrimaryHeadLine"));
		flex.setWidget(9,0,new Label("Rss"));
		flex.setWidget(10,0,new Label("Share"));
		flex.setWidget(11,0,new Label("Pulse"));
		flex.setWidget(12,0,new Label("Favorite Group"));
		flex.getCellFormatter().setAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(5, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(6, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(7, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(8, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(9, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(10, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(11, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(12, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		
		
		flex.setWidget(2,1,cbMailAlerts);
		flex.getCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flex.setWidget(3,1,cbGroups);
		flex.getCellFormatter().setHorizontalAlignment(3, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flex.setWidget(4,1,cbReports);
		flex.getCellFormatter().setHorizontalAlignment(4, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flex.setWidget(5,1,cbComments);
		flex.getCellFormatter().setHorizontalAlignment(5, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flex.setWidget(6,1,cbViews);
		flex.getCellFormatter().setHorizontalAlignment(6, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flex.setWidget(7,1,cbSearch);
		flex.getCellFormatter().setHorizontalAlignment(7, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flex.setWidget(8,1,cbPrimaryHeadLine);
		flex.getCellFormatter().setHorizontalAlignment(8, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flex.setWidget(9,1,cbRss);
		flex.getCellFormatter().setHorizontalAlignment(9, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flex.setWidget(10,1,cbShare);
		flex.getCellFormatter().setHorizontalAlignment(10, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flex.setWidget(11,1,cbPulse);
		flex.getCellFormatter().setHorizontalAlignment(11, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flex.setWidget(12,1,cbFavoriteGroup);
		flex.getCellFormatter().setHorizontalAlignment(12, 1, HasHorizontalAlignment.ALIGN_CENTER);
	}
	
	public void setUserPermissions(LhUserPermission permission){
		userId=permission.getUserId();
		cbMailAlerts.setValue(setCheckboxValue(permission.isMailAlertPermitted()));
		cbGroups.setValue(setCheckboxValue(permission.isGroupsPermitted()));
		cbReports.setValue(setCheckboxValue(permission.isReportsPermitted()));
		cbComments.setValue(setCheckboxValue(permission.isCommentsPermitted()));
		cbViews.setValue(setCheckboxValue(permission.isViewsPermitted()));
		cbSearch.setValue(setCheckboxValue(permission.isSearchPermitted()));
		cbRss.setValue(setCheckboxValue(permission.isRssPermitted()));
		cbPrimaryHeadLine.setValue(setCheckboxValue(permission.isPrimaryHeadLinePermitted()));
		cbShare.setValue(setCheckboxValue(permission.isSharePermitted()));
		cbPulse.setValue(setCheckboxValue(permission.isPulsePermitted()));
		cbFavoriteGroup.setValue(setCheckboxValue(permission.isFavoriteGroupPermitted()));
	}
	public boolean setCheckboxValue(int val){
		if(val==0)
			return false;
		else
			return true;
	}
	
	public int getCheckboxValue(boolean val){
		if(val)
			return 1;
		else
			return 0;
	}

	private Label createLabelsmall(String text){
		Label label = new Label(text);
		label.setStylePrimaryName("ThirdLoginTitle");
		return label;
	}

	/**
	 * clear all fields
	 */
	public void clearFields(){
		emailSuggestionTextbox.setText("");
		cbMailAlerts.setValue(false);
		cbGroups.setValue(false);
		cbReports.setValue(false);
		cbComments.setValue(false);
		cbViews.setValue(false);
		cbSearch.setValue(false);
		cbPrimaryHeadLine.setValue(false);
		cbRss.setValue(false);
		cbShare.setValue(false);
		cbPulse.setValue(false);
		cbFavoriteGroup.setValue(false);		
	}
	
	public void clearAllCheckboxFields(){
		
		cbMailAlerts.setValue(false);
		cbGroups.setValue(false);
		cbReports.setValue(false);
		cbComments.setValue(false);
		cbViews.setValue(false);
		cbSearch.setValue(false);
		cbPrimaryHeadLine.setValue(false);
		cbRss.setValue(false);
		cbShare.setValue(false);
		cbPulse.setValue(false);
		cbFavoriteGroup.setValue(false);		
	}
	
	public boolean validate(String text)
	{
		if(text.matches("[0-9][0-9]*"))
		{
			return true;
		}
		else
			return false;
	}

	public boolean validation(){
		String email = emailSuggestionTextbox.getText();
	 if(emailSuggestionTextbox.getText().equals(""))
		{
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter required fields");
			popup.setPopupPosition(600, 300);
			popup.show();
			return false;
		}
		return true;
	}

	public SuggestBox getEmailTextbox() {
		return emailSuggestionTextbox;
	}

	public void setEmailTextbox(SuggestBox emailSuggestionTextbox) {
		this.emailSuggestionTextbox = emailSuggestionTextbox;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		 if (event.getSource() == btnSelect){
				boolean validate=validation(); 
				if(validate){
				String email = emailSuggestionTextbox.getText();
				LhUserServiceAsync service = (LhUserServiceAsync)GWT.create(LhUserService.class);
				AsyncCallback callback = new AsyncCallback(){

					@Override
					public void onFailure(Throwable arg0) {
										
					}

					@Override
					public void onSuccess(Object arg0) {
						
				   LhUserPermission	lhUserPermission=(LhUserPermission)arg0;
				   if(lhUserPermission==null)
				   {
					   PopUpForForgotPassword popup = new PopUpForForgotPassword("Not a valid user to set the permissions");
						popup.setPopupPosition(600, 300);
						popup.show();
						clearAllCheckboxFields();
					   userId=0;
				   }
				   else{
					   setUserPermissions(lhUserPermission);
				   }
					}
				};
				service.getUserPermissions(userSelectedIndustryId, email, callback);
				}
			}
		 else if (event.getSource() == savebtn){
			boolean validate=validation(); 
			if(validate){	
				if(userId!=0){
					LhUserPermission permission=new LhUserPermission();
					permission.setUserId(userId);
					permission.setNewsCenterId(userSelectedIndustryId);
					permission.setMailAlertPermitted(getCheckboxValue(cbMailAlerts.getValue()));
					permission.setGroupsPermitted(getCheckboxValue(cbGroups.getValue()));
					permission.setReportsPermitted(getCheckboxValue(cbReports.getValue()));
					permission.setCommentsPermitted(getCheckboxValue(cbComments.getValue()));
					permission.setRssermitted(getCheckboxValue(cbRss.getValue()));
					permission.setViewsPermitted(getCheckboxValue(cbViews.getValue()));
					permission.setSearchPermitted(getCheckboxValue(cbSearch.getValue()));
					permission.setPrimaryHeadLinePermitted(getCheckboxValue(cbPrimaryHeadLine.getValue()));
					permission.setSharePermitted(getCheckboxValue(cbShare.getValue()));
					permission.setPulsePermitted(getCheckboxValue(cbPulse.getValue()));
					permission.setFavoriteGroupPermitted(getCheckboxValue(cbFavoriteGroup.getValue()));
			 
					LhUserServiceAsync service = (LhUserServiceAsync)GWT.create(LhUserService.class);
					AsyncCallback callback = new AsyncCallback(){
	
						@Override
						public void onFailure(Throwable arg0) {
											
						}
	
						@Override
						public void onSuccess(Object arg0) {
							boolean val=(Boolean)arg0;
							if(val){
								PopUpForForgotPassword popup = new PopUpForForgotPassword("User Permissions Saved");
								popup.setPopupPosition(600, 300);
								popup.show();
								clearFields();
							}
							else{  
								PopUpForForgotPassword popup = new PopUpForForgotPassword("User Permissions not saved");
								popup.setPopupPosition(600, 300);
								popup.show();
							}
						}
					};
					service.saveUserPermissions(permission,callback);
				}else{
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Not a valid user to set the permissions");
					popup.setPopupPosition(600, 300);
					popup.show();
				}
			}
		 }
	}		
	
	public void getEntityList(){
		LhUserServiceAsync service = (LhUserServiceAsync) GWT
				.create(LhUserService.class);
		AsyncCallback callback = new AsyncCallback() {

			@Override
			public void onFailure(Throwable arg0) {
				arg0.printStackTrace();
			}
			@Override
			public void onSuccess(Object arg0) {
				ArrayList list = (ArrayList) arg0;

				for (int i = 0; i < list.size(); i++) {
					oracle.add((String) list.get(i));
				}
			}

		};
		service.getEntityList(userSelectedIndustryId, callback);
	}
	
	private void createSuggestionWidget() {
		getEntityList();
		emailSuggestionTextbox = new SuggestBox(oracle);
		emailSuggestionTextbox.setAnimationEnabled(true);
		emailSuggestionTextbox.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode()==13)
				{
					clearAllCheckboxFields();
					boolean validate=validation(); 
					if(validate){
					String email = emailSuggestionTextbox.getText();
					LhUserServiceAsync service = (LhUserServiceAsync)GWT.create(LhUserService.class);
					AsyncCallback callback = new AsyncCallback(){

						@Override
						public void onFailure(Throwable arg0) {
								arg0.printStackTrace();			
						}

						@Override
						public void onSuccess(Object arg0) {
							
					   LhUserPermission	lhUserPermission=(LhUserPermission)arg0;
					   if(lhUserPermission==null) {
							clearAllCheckboxFields();
						   userId=0;
					   }
					   else{
						   setUserPermissions(lhUserPermission);
					   }
					}
					};
					service.getUserPermissions(userSelectedIndustryId, email, callback);
					}
				}
			}
		});
		emailSuggestionTextbox.addSelectionHandler(new SelectionHandler() {
			
			@Override
			public void onSelection(SelectionEvent event) {
				MultiWordSuggestOracle.MultiWordSuggestion suggestion = (MultiWordSuggestion) event.getSelectedItem();
				String value = suggestion.getReplacementString();
				
			}
		});
	}
}