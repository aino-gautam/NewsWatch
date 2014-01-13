package com.admin.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.common.client.Footer;
import com.common.client.Header;
import com.common.client.PopUpForForgotPassword;
import com.common.client.LogoutPage;

public class DeleteNewsItems extends Composite implements ClickHandler  {
	private TableCollection tbcollection;
	private DockPanel dock = new DockPanel();
	private HorizontalPanel hpanel = new HorizontalPanel();
	private Label label = new Label("Please check to delete");
	private DecoratorPanel decoratorforlabel = new DecoratorPanel();
	private ScrollPanel scroller = new ScrollPanel();
	private DecoratorPanel decoratorflex = new DecoratorPanel();
	private HorizontalPanel hpbuttonPanel = new HorizontalPanel();
	private Button button = new Button("Delete");
	private Button backButton = new Button("Back");
	private HashMap hashmap = new HashMap();
	private VerticalPanel vpanel = new VerticalPanel();

	String userSelectedIndustryName = "";
	String userIndustryname;
	int userIndustryid;
	RootPanel panel;
	int clientWidth;
	int clientHeight;

	public DeleteNewsItems(String userIndustryName,int industryId){
		dock.clear();
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		userIndustryname = userIndustryName;
		userIndustryid = industryId;
		getIndustryNameFromSession();
		getStyleNames();
		dock.setSpacing(30);
		//scroller.setHeight("450px");
		//scroller.setStylePrimaryName("scrollerforDecorator");
		
		scroller.setWidth(clientWidth-100+"px");
		scroller.setHeight(clientHeight-100+"px");
		
		tbcollection = new TableCollection();
		decoratorforlabel.add(label);
		//button.addClickListener(this);
		button.addClickHandler(this);
		
		//backButton.addClickListener(this);
		backButton.addClickHandler(this);
		vpanel.setSpacing(7);
		vpanel.add(decoratorforlabel);
		
		hpbuttonPanel.setSpacing(7);
		hpbuttonPanel.add(button);
//		hpbuttonPanel.add(backButton);
		scroller.add(tbcollection);
		decoratorflex.add(scroller);
		dock.add(vpanel,DockPanel.NORTH);
		dock.add(decoratorflex,DockPanel.CENTER);
		dock.add(hpbuttonPanel,DockPanel.SOUTH);
//		getAllFieldOfNewsItems();
		initWidget(dock);
	}

	public void getStyleNames()
	{
		vpanel.setStylePrimaryName("hpanelbuttonAndVpanel");
		button.setStylePrimaryName("buttonOkAdmin");
		backButton.setStylePrimaryName("buttonOkAdmin");
		hpanel.setStylePrimaryName("hpanelAdmin");
		label.setStylePrimaryName("labelNewUser");
		decoratorforlabel.setStylePrimaryName("decoratorlabelNewUserApproval");
	}

	public void onModuleLoad(){
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}

	public void getIndustryNameFromSession(){
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				try{
					String Industry[] = (String[])result;
					String name = Industry[0];
					int industryid = Integer.parseInt(Industry[2]);
					String userSelectedIndustryName = Industry[3];
					userSelectedIndustryName = userSelectedIndustryName;
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryNameFromSession()");
				}
			}
		};
		service.getIndustryNameFromSession(callback);
	}

	public void getAllFieldOfNewsItems(){
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				try{
					ArrayList list = (ArrayList)result;
					tbcollection.addNewsItemFieldsInTabel(list);
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getUserInfo()");
				}
			}
		};
		service.getAllFieldOfNewsItems(userIndustryid,callback);
	}

	public void deleteSelectedNewsItems(HashMap hashmap){
		final HashMap selectedTagMap = hashmap;
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				PopUpForForgotPassword popup = new PopUpForForgotPassword("There is some problem in deletion.");
				popup.setPopupPosition(600, 300);
				popup.show();
			}

			public void onSuccess(Object result) {
				try{
					tbcollection.disableCheckBox(selectedTagMap);
					PopUpForForgotPassword popup = new PopUpForForgotPassword("The following News items are deleted.");
					popup.setPopupPosition(600, 300);
					popup.show();
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getUserInfo()");
				}
			}
		};
		service.deleteSelectedNewsItems(hashmap, callback);
	}

	public void onClick(Widget sender){
		/*if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Delete")){
				hashmap = tbcollection.getItemMap();
				if(!hashmap.isEmpty()){
					deleteSelectedNewsItems(hashmap);
				}
				else{
					button.setEnabled(true);
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please check to delete.");
					popup.setPopupPosition(600, 300);
					popup.show();
				}
			}
		}*/
	}

	@Override
	public void onClick(ClickEvent event) {
		
		Widget sender = (Widget)event.getSource();
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Delete")){
				hashmap = tbcollection.getItemMap();
				if(!hashmap.isEmpty()){
					deleteSelectedNewsItems(hashmap);
				}
				else{
					button.setEnabled(true);
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please check to delete.");
					popup.setPopupPosition(600, 300);
					popup.show();
				}
			}
		}
		
		
	}

	
}