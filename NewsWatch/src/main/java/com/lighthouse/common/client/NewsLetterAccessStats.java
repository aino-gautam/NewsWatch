package com.lighthouse.common.client;


import com.admin.client.AdminInformationService;
import com.admin.client.AdminInformationServiceAsync;
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
import com.lighthouse.admin.client.LHTableCollection;
import com.lighthouse.newsletter.client.UserNewsletterAlertConfigService;
import com.lighthouse.newsletter.client.UserNewsletterAlertConfigServiceAsync;
import com.newscenter.client.criteria.PageCriteria;

public class NewsLetterAccessStats extends Composite{
	
	private VerticalPanel vpBase = new VerticalPanel();
	private DecoratorPanel decorator = new DecoratorPanel();
	private Label lblheading = new Label("NewsLetter access statistics:");
	private LHTableCollection tbcollection;
	private PagingPanel paging = new PagingPanel();
	private int noOfPages;
	protected String columnName;
	protected String sortMode;
	public int industryId;
	protected String searchString;
	protected String toDate = "", fromDate = "";
	private int userid = -1;
	private Boolean isSortMode = false, isSearchMode = false, isNormalMode = false;
				
	public NewsLetterAccessStats(){
		setStyles();
		paging.setCutomWidget(lblheading);
		vpBase.add(paging);
		vpBase.add(decorator);
		tbcollection = new LHTableCollection();
		tbcollection.setWidth("100%");
		tbcollection.setNewsLetterStats(this);
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
				        getNewsLetterAccessStatistics();
					else if(isSearchMode)
						getSearchData();
					else if(isSortMode)
						getSortedNewsLetterAccessStats();
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
						getNewsLetterAccessStatistics();
					else if(isSearchMode)
						getSearchData();
					else if(isSortMode)
						getSortedNewsLetterAccessStats();
				}
			}
		});
		initWidget(vpBase);
	}
	
	public void getNewsLetterAccessStatistics(){
		isNormalMode = true;
		isSearchMode = false;
		isSortMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		
		LHCommonInformationServiceAsync  service = (LHCommonInformationServiceAsync )GWT.create(LHCommonInformationService.class);
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(PageResult list) {
				addDataToGrid(list);
			}
		};
		service.getNewsLetterAccessStats(crt,getUserid(),getIndustryId(), callback);
	}
	
	public void getSortedNewsLetterAccessStats(){
		isSortMode = true;
		isNormalMode = false;
		isSearchMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		
		LHCommonInformationServiceAsync service = (LHCommonInformationServiceAsync)GWT.create(LHCommonInformationService.class);
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(PageResult list) {
				addDataToGrid(list);
			}
		};
		service.getSortedNewsLetterAccessStats(crt, getUserid(),getIndustryId(), getColumnName(), getSortMode(), callback);
	}
	
	public void getSearchData(){
		isSearchMode = true;
		isNormalMode = false;
		isSortMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		
		LHCommonInformationServiceAsync service = (LHCommonInformationServiceAsync)GWT.create(LHCommonInformationService.class);
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(PageResult list) {
				addDataToGrid(list);
			}
		};
		service.getSearchNewsLetterAccessStatistics(crt,getUserid(),getIndustryId(),getColumnName(),getSearchString(), callback);
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
		tbcollection.addDataInNewsLetterStats(list.getList());
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
		else if(orderColumnName.equals("NewsLetter Sent On"))
			this.columnName = "sentOn";
		else if(orderColumnName.equals("NewsLetter Accessed On"))
			this.columnName = "accessedOn";
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
}
