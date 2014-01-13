package com.admin.client;

import java.util.ArrayList;
import java.util.HashMap;

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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.newscenter.client.criteria.PageCriteria;


public class ApprovedUsers extends Composite implements ClickHandler{
	
	private ScrollPanel scroller = new ScrollPanel();
	private TableCollection tbcollection;
	private HorizontalPanel hpanelButtons = new HorizontalPanel();
	private VerticalPanel container = new VerticalPanel();
	private DockPanel dock = new DockPanel();
	private HashMap hashmap = new HashMap();
	private DecoratorPanel decoratorflex = new DecoratorPanel();
	private PagingPanel paging = new PagingPanel();
	private int noOfPages;
	protected String columnName;
	protected String searchString;
	protected String sortMode;
	RootPanel panel;
	int clientWidth;
	int clientHeight;
	private Boolean isSortMode = false, isSearchMode = false, isNormalMode = false;
	
	public ApprovedUsers(){
		getUserInfo();
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		tbcollection = new TableCollection();
		tbcollection.setApprovedUsersRef(this);
		
		decoratorflex.add(tbcollection);
		scroller.add(tbcollection);
		scroller.setHeight(clientHeight-100+"px");
		scroller.setWidth(clientWidth-100+"px");
		decoratorflex.add(scroller);
		hpanelButtons.setSpacing(10);
		hpanelButtons.add(createButton("Extend Duration"));
	
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
					if(isNormalMode)
						getUserInfo();
					else if(isSearchMode)
						getSearchApprovedUsers();
					else if(isSortMode)
						getSortedApprovedUsers();
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
					if(isNormalMode)
						getUserInfo();
					else if(isSearchMode)
						getSearchApprovedUsers();
					else if(isSortMode)
						getSortedApprovedUsers();
				}
				
			}
			
		});
		
		initWidget(dock);
	}
	
	public void getUserInfo(){
		isNormalMode = true;
		isSearchMode = false;
		isSortMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(PageResult list) {
				try{
				   addDataToGrid(list);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		getService().getUserInfoToModify(crt,callback);
	}
	
	public void getSortedApprovedUsers() {
		isNormalMode = false;
		isSearchMode = false;
		isSortMode = true;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(PageResult list) {
				try{
			    	addDataToGrid(list);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		getService().getSortedUserInfoModify(crt, getColumnName(), getSortMode(), callback);
	}

	public void getSearchApprovedUsers() {
		isNormalMode = false;
		isSearchMode = true;
		isSortMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(PageResult list) {
				try{
					addDataToGrid(list);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		getService().getSearchedUserInfoModify(crt, getColumnName(), getSearchString(), callback);
		
	}
	
	/*public void getUserInfo(){
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
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
					tbcollection.addApprovedUsers(list);
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in gerUserInfo()");
				}
			}
		};
		service.getUserInfoToModify(callback);
	}*/

	public Button createButton(String text){
		Button button = new Button(text);
		button.setStylePrimaryName("extendButton");
		//button.addClickListener(this);
		button.addClickHandler(this);
		return button;
	}

	public void onClick(Widget sender) {
		/*if(sender instanceof Button){
			Button button = (Button)sender;
			ArrayList userlist = new ArrayList();
			if(button.getText().equals("Extend Duration")){
				hashmap = tbcollection.getExtendedDurationMap();
				if(!hashmap.isEmpty()){
					HashMap map = new HashMap();
					for(Object obj : hashmap.keySet()){
						TextBox tb = (TextBox)hashmap.get(obj);
						AdminRegistrationInformation user = (AdminRegistrationInformation)obj;
						user.setDuration(Integer.parseInt(tb.getText()));
						user.setExtension(true);
						userlist.add(user);
					}
					updateDuration(userlist);
				}
				else{
					PopUpForForgotPassword popup = new PopUpForForgotPassword("No changes were made");
					popup.setPopupPosition(600, 300);
					popup.show();
				}
			}
		
		}*/
	}
	
	public void updateDuration(ArrayList userlist) {
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}
			public void onSuccess(Object result) {
				try{
					System.out.println("Successful in updateDuration");
					tbcollection.disableTextBox(tbcollection.getExtendedDurationMap());
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Update successful");
					popup.setPopupPosition(600, 300);
					popup.show();
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in addApprovedUserInfo()");
				}
			}
		};
		getService().updateSubscriptionDuration(userlist, callback);
	}
	
	public void deleteUserSubscription(AdminRegistrationInformation user){
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}
			public void onSuccess(Object result) {
				try{
					System.out.println("Successful in deleting subscription");
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Delete successful. An email has been sent to the user");
					popup.setPopupPosition(600, 300);
					popup.show();
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in deleteUserSubscription");
				}
			}
		};
		getService().deleteUserSubscription(user, callback);
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		if(columnName.equals("Name"))
			this.columnName = "firstName";
		else if(columnName.equals("email"))
			this.columnName = "email";
		else if(columnName.equals("Company"))
			this.columnName = "Company";
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String getSortMode() {
		return sortMode;
	}

	public void setSortMode(String sortMode) {
		this.sortMode = sortMode;
	}
	
	private void addDataToGrid(PageResult list){
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
		tbcollection.addApprovedUsers(list.getList());
	}

	private AdminInformationServiceAsync getService(){
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		return service;
	}

	@Override
	public void onClick(ClickEvent event) {
		
		Widget sender = (Widget)event.getSource();
	  	if(sender instanceof Button){
			Button button = (Button)sender;
			ArrayList userlist = new ArrayList();
			if(button.getText().equals("Extend Duration")){
				hashmap = tbcollection.getExtendedDurationMap();
				if(!hashmap.isEmpty()){
					HashMap map = new HashMap();
					for(Object obj : hashmap.keySet()){
						TextBox tb = (TextBox)hashmap.get(obj);
						AdminRegistrationInformation user = (AdminRegistrationInformation)obj;
						user.setDuration(Integer.parseInt(tb.getText()));
						user.setExtension(true);
						userlist.add(user);
					}
					updateDuration(userlist);
				}
				else{
					PopUpForForgotPassword popup = new PopUpForForgotPassword("No changes were made");
					popup.setPopupPosition(600, 300);
					popup.show();
				}
			}
		
		}
	}
	
}