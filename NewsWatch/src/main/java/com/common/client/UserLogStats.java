package com.common.client;

import com.admin.client.AdminInformationService;
import com.admin.client.AdminInformationServiceAsync;
import com.admin.client.TableCollection;
import com.common.client.PageResult;
import com.common.client.PagingPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.newscenter.client.criteria.PageCriteria;

public class UserLogStats extends Composite {
	
	private VerticalPanel vpBase = new VerticalPanel();
	private DecoratorPanel decorator = new DecoratorPanel();
	private Label lblheading = new Label("User login statistics");
	private TableCollection tbcollection;
	private PagingPanel paging = new PagingPanel();
	private int noOfPages;
	protected String columnName;
	protected String sortMode;
	public int industryId;
	protected String searchString;
	protected String toDate = "", fromDate = "";
	private int userid = -1;
	private Boolean namedisplay = true, isSortMode = false, isSearchMode = false, isNormalMode = false;
				
	public UserLogStats(){
		setStyles();
		paging.setCutomWidget(lblheading);
		vpBase.add(paging);
		vpBase.add(decorator);
		tbcollection = new TableCollection();
		tbcollection.setWidth("100%");
		tbcollection.setUserLogsStatsRef(this);
		decorator.add(tbcollection);
		decorator.setWidth("100%");
		vpBase.setSpacing(6);
		vpBase.setWidth("100%");
		
		paging.getLblnext().addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent arg0) {
				if ((paging.getCurrentpage() + 1) > noOfPages) {
				}
				else {
					paging.getLblprev().setStylePrimaryName("pagingenable");
					paging.setCurrentpage(paging.getCurrentpage() + 1);
					if(isNormalMode)
				        getLoginStatistics();
					else if(isSearchMode)
						getSearchData();
					else if(isSortMode)
						getSortedUserLoginStats();
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
				        getLoginStatistics();
					else if(isSearchMode)
						getSearchData();
					else if(isSortMode)
						getSortedUserLoginStats();
				}
				
			}
			
		});
		
		initWidget(vpBase);
	}
	
	public void getLoginStatistics(){
		isNormalMode = true;
		isSearchMode = false;
		isSortMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
			
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(PageResult list) {
				addDataToGrid(list);
			}
		};
		getService().getLoginStatistics(crt,getUserid(),getIndustryId(), callback);
	}
	
	public void getSortedUserLoginStats(){
		isSortMode = true;
		isNormalMode = false;
		isSearchMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(PageResult list) {
				addDataToGrid(list);
			}
		};
		getService().getSortedLoginStatistics(crt, getUserid(),getIndustryId(), getColumnName(), getSortMode(), callback);
	}
	
	public void getSearchData(){
		isSearchMode = true;
		isNormalMode = false;
		isSortMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(PageResult list) {
				addDataToGrid(list);
			}
		};
		if(getColumnName().equals("timeOfLogin")){
			setSearchString(fromDate+","+toDate);
		}
		getService().getSearchLoginStatistics(crt,getUserid(),getIndustryId(),getColumnName(),getSearchString(), callback);
	}

	private CommonInformationServiceAsync getService(){
		CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "common";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		return service;
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
		tbcollection.addDataInLoginStats(list.getList(),namedisplay);
	}
	
	private void setStyles() {
		DOM.setStyleAttribute(lblheading.getElement(), "fontWeight", "bold");
		DOM.setStyleAttribute(lblheading.getElement(), "fontSize", "12pt");
	}

	public String getSortMode() {
		return sortMode;
	}

	public void setSortMode(String sortMode) {
		this.sortMode = sortMode;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String orderColumnName) {
		if(orderColumnName.equals("Name"))
			this.columnName = "firstName";
		else if(orderColumnName.equals("email"))
			this.columnName = "email";
		else if(orderColumnName.equals("Role"))
			this.columnName = "isAdmin";
		else if(orderColumnName.equals("Time of login"))
			this.columnName = "timeOfLogin";
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
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
					int industryid = Integer.parseInt(Industry[2]);
					String userSelectedIndustryName = Industry[3];
					setIndustryId(industryid);
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in getIndustryNameFromSession()");
				}
			}
		};
		service.getIndustryNameFromSession(callback);
	}

	public int getIndustryId() {
		return industryId;
	}

	public void setIndustryId(int industryId) {
		this.industryId = industryId;
	}

	public void setFromDate(String text) {
		this.fromDate = text;
	}

	public void setToDate(String text) {
	    this.toDate = text;
	}

	public String getToDate() {
		return toDate;
	}

	public String getFromDate() {
		return fromDate;
	}

	public PagingPanel getPaging() {
		return paging;
	}

	public void setPaging(PagingPanel paging) {
		this.paging = paging;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public Boolean getNamedisplay() {
		return namedisplay;
	}

	public void setNamedisplay(Boolean namedisplay) {
		this.namedisplay = namedisplay;
	}
}