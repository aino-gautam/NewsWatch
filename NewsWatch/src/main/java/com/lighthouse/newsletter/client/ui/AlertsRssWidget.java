package com.lighthouse.newsletter.client.ui;

import java.util.ArrayList;

import com.appUtils.client.exception.FeatureNotSupportedException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.newsletter.client.UserNewsletterAlertConfigService;
import com.lighthouse.newsletter.client.UserNewsletterAlertConfigServiceAsync;
import com.lighthouse.newsletter.client.domain.UserNewsletterAlertConfig;

public class AlertsRssWidget extends FlexTable{
	
	ArrayList<UserNewsletterAlertConfig> useralertsList;
	
	public AlertsRssWidget(LhUserPermission lhUserPermission, ArrayList<UserNewsletterAlertConfig> useralertsList) throws FeatureNotSupportedException{
		if(lhUserPermission.isRssPermitted() == 1){
			this.useralertsList = useralertsList;
			createUI();
		}else
			throw new FeatureNotSupportedException("Rss not supported");
	}
	
	private void createUI(){
		Image rssImg= new Image("images/rss.jpg");
		Label rssLabel=new Label("Generate Rss link for:");
		rssLabel.setStylePrimaryName("label");
		final Label errorLabel = new Label("Please select an alert from the list");
		errorLabel.setVisible(false);
		DOM.setStyleAttribute(errorLabel.getElement(), "color", "red");
		
		final ListBox alertList = new ListBox();
		alertList.addItem("-- Please select --");
		
		for(UserNewsletterAlertConfig alertItem: getUseralertsList()){
			alertList.addItem(alertItem.getAlertName());
		}
		
		Button rssButton= new Button("Generate Link");
		rssButton.setVisible(false);
		rssButton.addStyleName("loginButton");
		rssButton.setWidth("100%");
		if(getUseralertsList().size()>0)
			rssButton.setVisible(true);
		rssButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(event.getSource() instanceof Button){
					if(alertList.getSelectedIndex() != 0){
						errorLabel.setVisible(false);
						String alertName=alertList.getValue(alertList.getSelectedIndex());
						int alertId=getAlertId(alertName);
							
						UserNewsletterAlertConfigServiceAsync alertService = GWT.create(UserNewsletterAlertConfigService.class);
						alertService.generateRssLinkForAlert(alertId, new AsyncCallback<String>() {
	
							@Override
							public void onFailure(Throwable caught) {
								
								
							}
	
							@Override
							public void onSuccess(String result) {
								if(result!=null){
									
									String rssNewUrl=GWT.getHostPageBaseURL()+"alertRss?value="+result;
									
									HTML rssLink = new HTML("<a style=\"font-family: arial, sans-serif; font-size: 12px; word-wrap: break-word; width: 50px\" href="+rssNewUrl+">"+rssNewUrl+"</a>");
									rssLink.setWordWrap(true);
							               
							        Label lblheader = new Label("RSS Feed Link");
							        DOM.setStyleAttribute(lblheader.getElement(), "fontWeight", "bold");
							        DOM.setStyleAttribute(lblheader.getElement(), "fontSize", "9pt");
							        VerticalPanel vpbase = new VerticalPanel();
							        vpbase.add(lblheader);
							        vpbase.add(rssLink);
							        vpbase.setCellHorizontalAlignment(lblheader, HasHorizontalAlignment.ALIGN_CENTER);
							        DecoratedPopupPanel linkpopup = new DecoratedPopupPanel();
							        linkpopup.add(vpbase);
							        linkpopup.setAnimationEnabled(true);
							        linkpopup.setAutoHideEnabled(true);
							        linkpopup.center();
							        linkpopup.show();
							        linkpopup.setStylePrimaryName("searchPopup");
									
								}
							}
					});
					}else
						errorLabel.setVisible(true);
				}
				
			}

			
		});
		
		setWidget(0, 0, rssImg);
		setWidget(0, 1, rssLabel);
		getFlexCellFormatter().setColSpan(2, 0, 2);
		setWidget(2, 0, errorLabel);
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(alertList);
		hp.add(rssButton);
		hp.setCellVerticalAlignment(alertList, HasVerticalAlignment.ALIGN_MIDDLE);
		getFlexCellFormatter().setColSpan(3, 0, 2);
		setWidget(3, 0, hp);
	}

	private int getAlertId(String alertName) {
		
		for(UserNewsletterAlertConfig alertItem: useralertsList){
			
			if(alertItem.getAlertName().equalsIgnoreCase(alertName)){
				return alertItem.getAlertId();
			}
		}
		
		return -1;
	}

	public ArrayList<UserNewsletterAlertConfig> getUseralertsList() {
		return useralertsList;
	}

	public void setUseralertsList(
			ArrayList<UserNewsletterAlertConfig> useralertsList) {
		this.useralertsList = useralertsList;
	}
}
