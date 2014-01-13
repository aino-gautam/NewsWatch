package com.trial.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.admin.client.TableCollection;
import com.common.client.PageResult;
import com.common.client.PagingPanel;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsItems;

public class ExistingTrialAccounts extends Composite {
	
	private VerticalPanel vpBase = new VerticalPanel();
	private DecoratorPanel decorator = new DecoratorPanel();
	private Label lblheading = new Label("Existing trial accounts");
	private TableCollection tbcollection;
	private PagingPanel paging = new PagingPanel();
	private int noOfPages;
	protected String columnName;
	protected String sortMode;
	protected String searchString;
	private DeckPanel deck;
	private Boolean isSortMode = false, isSearchMode = false, isNormalMode = false;

	public ExistingTrialAccounts(){
		deck = new DeckPanel();
		setStyles();
		paging.setCutomWidget(lblheading);
		vpBase.add(paging);
		vpBase.add(decorator);
		tbcollection = new TableCollection();
		tbcollection.setWidth("100%");
		tbcollection.setExistingTrialAccountsRef(this);
		decorator.add(tbcollection);
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
						getTrialAccounts();
					else if(isSearchMode)
						getSearchData();
					else if(isSortMode)
						getSortedTrialAccount();
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
					getTrialAccounts();
				}
				
			}
			
		});
		
		deck.add(vpBase);
		deck.showWidget(0);
		initWidget(deck);
	}
	
	public void getTrialAccounts(){
		isNormalMode = true;
		isSearchMode = false;
		isSortMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		TrialInformationServiceAsync service = (TrialInformationServiceAsync) GWT.create(TrialInformationService.class);
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(PageResult list) {
				addData(list);
			}
		};
		service.getTrialAccounts(crt,callback);
	
	}
	
	public void getSortedTrialAccount(){
		isNormalMode = false;
		isSearchMode = false;
		isSortMode = true;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		TrialInformationServiceAsync service = (TrialInformationServiceAsync) GWT.create(TrialInformationService.class);
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(PageResult list) {
				 addData(list);
			}
		};
		service.getSortTrialAccounts(crt,getColumnName(),getSortMode(),callback);
	}
	
	public void getSearchData(){
		isNormalMode = false;
		isSearchMode = true;
		isSortMode = false;
		PageCriteria crt = new PageCriteria();
		crt.setCurrentPage(paging.getCurrentpage());
		crt.setPageSize(paging.getPagelimit());
		TrialInformationServiceAsync service = (TrialInformationServiceAsync) GWT.create(TrialInformationService.class);
		AsyncCallback<PageResult> callback = new AsyncCallback<PageResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(PageResult list) {
				addData(list);
			}
		};
		service.getSearchData(crt,getColumnName(),getSearchString(),callback);
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
		else if(orderColumnName.equals("Start Date"))
			this.columnName = "UserCreationDate";
		else if(orderColumnName.equals("NewsCatalyst"))
			this.columnName = "NewsCenterId";
		else if(orderColumnName.equals("Sales Executive"))
			this.columnName = "createdBy";
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	
	public DeckPanel getDeck() {
		return deck;
	}

	public void setDeck(DeckPanel deck) {
		this.deck = deck;
	}

	private void addData(PageResult list){
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
		tbcollection.addTrialUserInTable(list.getList());
	}
	
	public void deleteTrialAccount(TrialAccount trialaccount){
		TrialInformationServiceAsync service = (TrialInformationServiceAsync)GWT.create(TrialInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			@Override
			public void onFailure(Throwable arg0) {
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Failed to delete user");
				popup.setAnimationEnabled(true);
				popup.center();
				popup.show();				
			}

			@Override
			public void onSuccess(Object arg0) {
				 PopUpForForgotPassword popup = new PopUpForForgotPassword("User successfully deleted");
				 popup.setAnimationEnabled(true);
				 popup.center();
				 popup.show();
				 getTrialAccounts();
			}
			
		};
		service.deleteTrialAccount(trialaccount, callback);
	}
	
	public void reinitiateTrialAccount(final TrialAccount trialaccount,final int row){
		TrialInformationServiceAsync service = (TrialInformationServiceAsync)GWT.create(TrialInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			@Override
			public void onFailure(Throwable arg0) {
			
			}

			@Override
			public void onSuccess(Object arg0) {
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Update successful");
				popup.setPopupPosition(600, 300);
				popup.show();
				tbcollection.setText(row, 7, "3");
				trialaccount.setDurationLeft(3);
				//getTrialAccounts();
			}
			
		};
		service.reinitiateTrialAccount(trialaccount, callback);
	}
}
