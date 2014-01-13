package com.common.client;

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

public class UserItemAccessWidget extends Composite {
	
	private VerticalPanel vpBase = new VerticalPanel();
	private DecoratorPanel decorator = new DecoratorPanel();
	private Label lblheading = new Label("User item access statistics");
	private TableCollection tbcollection;
	private PagingPanel paging = new PagingPanel();
	private int noOfPages;
	protected String columnName;
	protected String sortMode;
	public int industryId;
	protected String searchString;
	private int userid = -1;
	private Boolean nameDisplay = true, isSortMode = false, isSearchMode = false, isNormalMode = false;
	
	public UserItemAccessWidget(){
		setStyles();
		paging.setCutomWidget(lblheading);
		vpBase.add(paging);
		vpBase.add(decorator);
		tbcollection = new TableCollection();
		tbcollection.setWidth("100%");
		tbcollection.setUserItemAccessRef(this);
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
						getUserAccessStatistics();
					else if(isSearchMode)
						getSearchUserItemAccessStats();
					else if(isSortMode)
						getSortUserItemAccesStats();
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
						getUserAccessStatistics();
					else if(isSearchMode)
						getSearchUserItemAccessStats();
					else if(isSortMode)
						getSortUserItemAccesStats();
				}
			}
		});
		
		initWidget(vpBase);
	}

	private void setStyles() {
		DOM.setStyleAttribute(lblheading.getElement(), "fontWeight", "bold");
		DOM.setStyleAttribute(lblheading.getElement(), "fontSize", "12pt");
	}
	
	public void getUserAccessStatistics() {
		isNormalMode = true;
		isSearchMode = false;
		isSortMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				arg0.getLocalizedMessage();
			}

			@Override
			public void onSuccess(PageResult list) {
				addDataToGrid(list);
			}
		};
		getService().getUserItemAccessStats(crt,getUserid(),getIndustryId(), callback);
		
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
			this.columnName = "u.FirstName";
		else if(orderColumnName.equals("NewsItem"))
			this.columnName = "ni.Title";
		else if(orderColumnName.equals("Access from newscatalyst"))
			this.columnName = "NewscatalystItemCount";
		else if(orderColumnName.equals("Access from newsletter"))
			this.columnName = "NewsletterItemCount";
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	public int getIndustryId() {
		return industryId;
	}

	public void setIndustryId(int industryId) {
		this.industryId = industryId;
	}

	public void getSortUserItemAccesStats() {
		isNormalMode = false;
		isSearchMode = false;
		isSortMode = true;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				arg0.getLocalizedMessage();
			}

			@Override
			public void onSuccess(PageResult list) {
				addDataToGrid(list);
			}
		};
		getService().getSortedUserItemAccessStats(crt,getUserid(),getIndustryId(),getColumnName(),getSortMode(),callback);
	}
	
	public void getSearchUserItemAccessStats(){
		isNormalMode = false;
		isSearchMode = true;
		isSortMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				arg0.getLocalizedMessage();
			}

			@Override
			public void onSuccess(PageResult list) {
				addDataToGrid(list);
			}
		};
		getService().getSearchUserItemAccessStats(crt, getUserid(), getIndustryId(), getColumnName(), getSearchString(), callback);
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
		    	if(noOfPages == 0){
		    		paging.getLblprev().setStylePrimaryName("pagingdisable");
					paging.getLblnext().setStylePrimaryName("pagingdisable");
		    	}
		    	else{
					paging.getLblnext().setStylePrimaryName("pagingenable");
					paging.getLblprev().setStylePrimaryName("pagingdisable");
		    	}
		   }
		}
						
		if(tbcollection.getRowCount() > 0){
		 int rowcnt = tbcollection.getRowCount();
		 for(int i=1; i< rowcnt ;i++){
			 tbcollection.removeRow(1);	 
		 }
		 
		}
		tbcollection.addDataUserItemAccessStats(list.getList(),nameDisplay);
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public PagingPanel getPaging() {
		return paging;
	}

	public void setPaging(PagingPanel paging) {
		this.paging = paging;
	}

	public Boolean getNameDisplay() {
		return nameDisplay;
	}

	public void setNameDisplay(Boolean nameDisplay) {
		this.nameDisplay = nameDisplay;
	}
}
