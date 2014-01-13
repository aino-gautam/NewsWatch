package com.common.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;


public class UserHistoryPanel extends Composite {
	
	private VerticalPanel vpBasePanel = new VerticalPanel();
    private VerticalPanel vpUserBasicInfoPanel = new VerticalPanel();
    private VerticalPanel vpUserExtensionInfoPanel = new VerticalPanel();
    private VerticalPanel vpUserSubscriptionDetail = new VerticalPanel();
    private VerticalPanel vpUserItemAccessDetail = new VerticalPanel();
    private VerticalPanel vpUserLoginAccessDetail = new VerticalPanel();
    private Image imgleft = new Image("images/trial/blue_left.gif");
    private Image imgright = new Image("images/trial/blue_right.gif");
    FlexTable flex = new FlexTable();
    private Image imgup;
    private int userid,industryid;
     		
	public UserHistoryPanel(int userId,int industryid,Image imgUp){
		imgup = imgUp;
		this.userid = userId;
		this.industryid = industryid;
		getUserHistory(userId);
	    initWidget(vpBasePanel);
	}
	
	private Label createUserBasicInfoLabel(String text){
		Label lbl = new Label(text);
		DOM.setStyleAttribute(lbl.getElement(), "verticalAlign", "bottom");
		return lbl;
	}
	
	private Label createLabelAsSentence(String text){
		Label lbl = new Label(text);
		DOM.setStyleAttribute(lbl.getElement(), "fontSize", "13px");
		DOM.setStyleAttribute(lbl.getElement(), "marginLeft", "10px");
		return lbl;
	}
	
	private Label createBoldLabel(String text){
		Label  lbl = new Label(text);
		DOM.setStyleAttribute(lbl.getElement(), "fontSize", "15px");
		DOM.setStyleAttribute(lbl.getElement(), "fontWeight", "bold");
		DOM.setStyleAttribute(lbl.getElement(), "verticalAlign", "bottom");
		DOM.setStyleAttribute(lbl.getElement(), "marginLeft", "10px");
		return lbl;
	}
	private void getUserHistory(int userId){
		CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		ServiceDefTarget target = (ServiceDefTarget)service;
		String moduleRelativeUrl = GWT.getModuleBaseURL()+"common";
		target.setServiceEntryPoint(moduleRelativeUrl);
	    AsyncCallback<ArrayList<UserHistory>> callback = new AsyncCallback<ArrayList<UserHistory>>(){

		@Override
		public void onFailure(Throwable arg0) {
					
		}

		@Override
		public void onSuccess(ArrayList<UserHistory> list) {
		      createUI(list);
		}
	  };
	  service.getUserAccountHistory(userId, callback);
	}

	private void createUI(ArrayList<UserHistory> list) {
		VerticalPanel vpContent = new VerticalPanel();
		DateTimeFormat dtfDate = DateTimeFormat.getFormat("dd MMM, yyyy");
		DateTimeFormat dtfTime = DateTimeFormat.getFormat("hh:mm a");
		imgup.setStylePrimaryName("clickable");
		if(list.size() > 0){
			Iterator<UserHistory> itr = list.iterator();
			while(itr.hasNext()){
				UserHistory userhistory = itr.next();
			
					flex.setWidget(0, 0, imgleft);
					HorizontalPanel hp = new HorizontalPanel();
					hp.add(createBoldLabel(userhistory.getFirstname()+" "+userhistory.getLastname()));
					hp.add(createUserBasicInfoLabel("("+userhistory.getEmail()+")"));
					hp.add(createBoldLabel("NewsCatalyst: "+userhistory.getNewscatalystname()));
					hp.setSpacing(4);
					flex.setCellPadding(0);
					flex.setCellSpacing(0);
					flex.setWidget(0, 1,hp);
					flex.setWidget(0, 2, imgup);
					flex.setWidget(0, 3, imgright);
					flex.getFlexCellFormatter().setStylePrimaryName(0, 2, "showhistoryflex");
					flex.getFlexCellFormatter().setStylePrimaryName(0, 1, "showhistoryflex");
					flex.getFlexCellFormatter().setWidth(0, 1, "100%");
					flex.setWidth("100%");
					vpBasePanel.add(flex);
			
				
				if(vpUserBasicInfoPanel.getWidgetCount() == 0){
					vpUserBasicInfoPanel.add(createLabelAsSentence("User created by "+userhistory.getUserCreatedByName()+" on "+dtfDate.format(userhistory.getOperationDate())+" at "+dtfTime.format(userhistory.getOperationDate())+" for the duration of "+userhistory.getDuration()+ " days"));
					if(userhistory.isSubscribed()){
						if(userhistory.getNewsletterperiod() == 1)
						    vpUserBasicInfoPanel.add(createLabelAsSentence("Subscribed to daily newsletter"));
						else
							vpUserBasicInfoPanel.add(createLabelAsSentence("Subscribed to weekly newsletter"));
					}
				}
				
				if(userhistory.getOperation().equals("extends")){
					if(vpUserExtensionInfoPanel.getWidgetCount() == 0){
					 vpUserExtensionInfoPanel.add(createBoldLabel("Extensions"));
					}
					if(userhistory.getExtendedDuration() == 1){
					    vpUserExtensionInfoPanel.add(createLabelAsSentence("Duration extended on "+dtfDate.format(userhistory.getOperationDate())+" at "+dtfTime.format(userhistory.getOperationDate())+" by "+userhistory.getOperationPerformedBy()+ " for "+userhistory.getExtendedDuration()+ " day"));
					}
					else{
						vpUserExtensionInfoPanel.add(createLabelAsSentence("Duration extended on "+dtfDate.format(userhistory.getOperationDate())+" at "+dtfTime.format(userhistory.getOperationDate())+" by "+userhistory.getOperationPerformedBy()+ " for "+userhistory.getExtendedDuration()+ " days"));	
					}
				}
				
				if(userhistory.getOperation().equals("subscribed")){
					vpBasePanel.add(createBoldLabel("Subscription"));
					if(userhistory.getMessage() != null){
						vpUserSubscriptionDetail.add(createLabelAsSentence(userhistory.getMessage()+ " by "+userhistory.getOperationPerformedBy()+ " on "+dtfDate.format(userhistory.getOperationDate())+" at "+dtfTime.format(userhistory.getOperationDate())+" for the duration of "+userhistory.getDuration()+ " days"));
					}
					if(userhistory.isSubscribed()){
						if(userhistory.getNewsletterperiod() == 1)
							vpUserSubscriptionDetail.add(createLabelAsSentence("User subscribed to daily newsletter for "+userhistory.getNewscatalystname()));
						else
							vpUserSubscriptionDetail.add(createLabelAsSentence("User subscribed to weekly newsletter for "+userhistory.getNewscatalystname()));
					}
				}
				
		         /*  if(userhistory.getUserItemAccessStats()!=null){
					vpUserItemAccessDetail.add(createBoldLabel("ItemAccess"));
					 if(userhistory.getUserItemAccessStats().size() > 0){
					 	Iterator iterator = userhistory.getUserItemAccessStats().iterator();
					    while(iterator.hasNext()){
						UserItemAccessStats useritemaccess = (UserItemAccessStats)iterator.next();
							if(useritemaccess.getNewscatalystitemcount() != 0){
							    vpUserItemAccessDetail.add(createUserBasicInfoLabel("User viewed "+useritemaccess.getNewsitemtitle()+" for "+useritemaccess.getNewscatalystitemcount()+" time from Newscatalyst"));
							}
							if(useritemaccess.getNewsletteritemcount() != 0){
								vpUserItemAccessDetail.add(createUserBasicInfoLabel("User viewed "+useritemaccess.getNewsitemtitle()+" for "+useritemaccess.getNewsletteritemcount()+" times from Newsletter"));
						 	}
					   }
					}
					else{
						vpUserItemAccessDetail.add(createUserBasicInfoLabel(" - "));
					}
				   }*/
				
				/*if(userhistory.getUserLoginStats()!=null){
					vpUserLoginAccessDetail.add(createBoldLabel("Login Access Since last 4 days"));
					if(userhistory.getUserLoginStats().size() > 0){
						Iterator iteratorLoginStats = userhistory.getUserLoginStats().iterator();
						while(iteratorLoginStats.hasNext()){
							vpUserLoginAccessDetail.add(createUserBasicInfoLabel("User access on "+iteratorLoginStats.next()));
						}
					}
					else{
						vpUserLoginAccessDetail.add(createUserBasicInfoLabel(" - "));
					}
				}*/
			}
			// vpUserItemAccessDetail.add(createBoldLabel("ItemAccess"));
			 UserItemAccessWidget useritemaccess = new UserItemAccessWidget();
			 useritemaccess.getPaging().setPagelimit(5);
			 useritemaccess.setNameDisplay(false);
			 useritemaccess.setUserid(userid);
			 useritemaccess.getUserAccessStatistics();
			 vpUserItemAccessDetail.add(useritemaccess);
			 
			 UserLogStats userloginstats = new UserLogStats();
			 userloginstats.getPaging().setPagelimit(5);
			 userloginstats.setNamedisplay(false);
			 userloginstats.setUserid(userid);
			 userloginstats.setIndustryId(industryid);
			 userloginstats.getLoginStatistics();
			 vpUserLoginAccessDetail.add(userloginstats);
			 vpUserLoginAccessDetail.setWidth("100%");
			 
			vpContent.add(vpUserBasicInfoPanel);
			vpContent.add(vpUserExtensionInfoPanel);
			vpContent.add(vpUserSubscriptionDetail);
			vpContent.add(vpUserItemAccessDetail);
			vpContent.add(vpUserLoginAccessDetail);
			vpContent.setSpacing(10);
			vpContent.setWidth("100%");
			vpBasePanel.add(vpContent);
			vpBasePanel.setWidth("100%");
			DOM.setStyleAttribute(vpContent.getElement(), "backgroundColor", "#f2f2f2");
		}
		
	}

	public int getIndustryid() {
		return industryid;
	}

	public void setIndustryid(int industryid) {
		this.industryid = industryid;
	}
}
