package com.admin.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.client.Footer;
import com.common.client.Header;
import com.common.client.LogoutPage;
import com.common.client.PageResult;
import com.common.client.PagingPanel;
import com.common.client.PopUpForForgotPassword;
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
import com.newscenter.client.criteria.PageCriteria;

public class ManageUsers extends Composite implements ClickHandler 
{
	private ScrollPanel scroller = new ScrollPanel();
	protected TableCollection tbcollection;
	private HorizontalPanel hpanelButtons = new HorizontalPanel();
	private VerticalPanel container = new VerticalPanel();
	private DockPanel dock = new DockPanel();
	protected HashMap hashmap = new HashMap();
	private DecoratorPanel decoratorflex = new DecoratorPanel();
	RootPanel panel;
	int clientWidth;
	int clientHeight;
	private int noOfPages;
	public Button deletebtn = new Button("Delete");
	private PagingPanel paging = new PagingPanel();

	public ManageUsers(){
		getUserInfo();
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		tbcollection = new TableCollection();
		decoratorflex.add(tbcollection);
		scroller.add(tbcollection);
		scroller.setHeight(clientHeight-100+"px");
		scroller.setWidth(clientWidth-100+"px");
		decoratorflex.add(scroller);
		hpanelButtons.setSpacing(10);
		hpanelButtons.add(createButton("Delete"));
	
		dock.setSpacing(7);

		dock.add(paging, DockPanel.NORTH);
		dock.add(decoratorflex,DockPanel.CENTER);
		dock.add(hpanelButtons,DockPanel.SOUTH);
		
		paging.getLblnext().addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent arg0) {
				if ((paging.getCurrentpage() + 1) > noOfPages) {
				}
				else {
					paging.getLblprev().setStylePrimaryName("pagingenable");
					paging.setCurrentpage(paging.getCurrentpage() + 1);
					getUserInfo();
				}
			}
			
		});
		
		paging.getLblprev().addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent arg0) {
				if ((paging.getCurrentpage() - 1) < 1) {
				}
				else {
					paging.getLblnext().setStylePrimaryName("pagingenable");
					paging.setCurrentpage(paging.getCurrentpage() - 1);
						getUserInfo();
				}
				
			}
			
		});

		initWidget(dock);
	}

	public void getUserInfo(){
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(PageResult list) {
				try{
					noOfPages = list.getTotalNoOfPages();
					paging.setCurrentpage(list.getCurrentPage());
					paging.getCurrentPageNumber().setText(""+list.getCurrentPage());
					paging.getPagesize().setText(" of "+list.getTotalNoOfPages());
					int currentPage = list.getCurrentPage();
					if((currentPage == 1) && (noOfPages == currentPage)){
						paging.getLblprev().setStylePrimaryName("pagingdisable");
						paging.getLblnext().setStylePrimaryName("pagingdisable");
					}
					
					if((currentPage > 1) && (noOfPages == currentPage)){
						paging.getLblnext().setStylePrimaryName("pagingdisable");
						paging.getLblprev().setStylePrimaryName("pagingenable");
					}
					
					if(noOfPages != currentPage){
						if(currentPage > 1){ 
							paging.getLblnext().setStylePrimaryName("pagingenable");
							paging.getLblprev().setStylePrimaryName("pagingenable");
					    }
					    else{
							paging.getLblnext().setStylePrimaryName("pagingenable");
							paging.getLblprev().setStylePrimaryName("pagingdisable");
					   }
					}
									
					
					if(tbcollection.getRowCount() > 0){
					 int rowcnt = tbcollection.getRowCount();
					 for(int i=1; i< rowcnt ;i++){
						 tbcollection.removeRow(1);	 
					 }
					 
					}
					
					tbcollection.addInTableforManageUser(list.getList());
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in gerUserInfo()");
				}
			}
		};
		service.getUserInfoToModify(crt,callback);
	}
	
	
	public Button createButton(String text){
		Button button = new Button(text);
		button.setStylePrimaryName("buttonOkAdmin");
		//button.addClickListener(this);
		button.addClickHandler(this);
		return button;
	}

	public void onModuleLoad(){
		 panel = RootPanel.get();
		 panel.clear();
		 panel.add(this);
	}

	public void onClick(Widget sender){
		/*if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Delete")){
				button.setEnabled(true);
				hashmap = tbcollection.getItemMap();
				if(!hashmap.isEmpty()){
					button.setEnabled(true);
					deleteUser(hashmap);
				}
				else{
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select user to delete.");
					popup.setPopupPosition(600, 300);
					popup.show();
					button.setEnabled(true);
				}
			}
			else if(button.getText().equals("Add User")){
				AdminRegistration registration = new AdminRegistration();
				registration.onModuleLoad();
			}
			else if(button.getText().equals("Back"))
			{
				AdminPage admin = new AdminPage();
				admin.onModuleLoad();
			}
		}
		else if(sender instanceof Label)
		{
			Label label = (Label) sender;
			if(label.getText().equals("logout"))
			{
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);
			}
		}*/
		
	}
	public void deleteUser(HashMap hashmap)
	{
		final HashMap map = hashmap;
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
					tbcollection.disableCheckBox(map);
					System.out.println("Successful in deleteUser ");
					PopUpForForgotPassword popup = new PopUpForForgotPassword("The users has been deleted");
					popup.setPopupPosition(600, 300);
					popup.show();
					deletebtn.setEnabled(false);
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in addApprovedUserInfo()");
				}
			}
		};
		service.deleteUser(hashmap, callback);
	}

	@Override
	public void onClick(ClickEvent event) {
		
		Widget sender = (Widget)event.getSource();
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Delete")){
				button.setEnabled(true);
				hashmap = tbcollection.getItemMap();
				if(!hashmap.isEmpty()){
					button.setEnabled(true);
					deleteUser(hashmap);
				}
				else{
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select user to delete.");
					popup.setPopupPosition(600, 300);
					popup.show();
					button.setEnabled(true);
				}
			}
			else if(button.getText().equals("Add User")){
				AdminRegistration registration = new AdminRegistration();
				registration.onModuleLoad();
			}
			else if(button.getText().equals("Back"))
			{
				AdminPage admin = new AdminPage();
				admin.onModuleLoad();
			}
		}
		else if(sender instanceof Label)
		{
			Label label = (Label) sender;
			if(label.getText().equals("logout"))
			{
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);
			}
		}
		
	}
}