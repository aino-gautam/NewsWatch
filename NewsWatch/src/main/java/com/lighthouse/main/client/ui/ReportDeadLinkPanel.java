package com.lighthouse.main.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.newscenter.client.news.NewsItems;

public class ReportDeadLinkPanel extends Composite{

	private NewsItems newsItem;
	private Image image;
	private String userEmail;
	HorizontalPanel reportLinkPanel;
	
	
	public ReportDeadLinkPanel(LhUserPermission lhUserPermission, NewsItems newsItem, String userEmail){
		reportLinkPanel = new HorizontalPanel();
		
		image = new Image("images/brokenLink.png", 0, -1, 20, 16);
		this.newsItem = newsItem;
		this.userEmail = userEmail;
		createUI();
		initWidget(reportLinkPanel);
	}
	
	public ReportDeadLinkPanel(LhUserPermission lhUserPermission, NewsItems newsItem, Image image, String userEmail){
		reportLinkPanel = new HorizontalPanel();
		this.image = image;
		this.newsItem = newsItem;
		this.userEmail = userEmail;
		createUI();
		initWidget(reportLinkPanel);
	}
	
	private void createUI(){
		Label label = new Label(" Report dead link");
		label.setStylePrimaryName("newsItemCommentsLabel");
		label.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String url = newsItem.getUrl();
				String title = newsItem.getNewsTitle();
				ReportDeadLinkPopup deadLinkPopup = new ReportDeadLinkPopup(url,title, userEmail); 
				deadLinkPopup.setGlassEnabled(true);
				deadLinkPopup.show();
				deadLinkPopup.center();
			}
		});
		
		label.addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				Label lb = (Label) event.getSource();
				lb.addStyleName("newsItemCommentsLabelHover");
			}
		});
		label.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				Label lb = (Label) event.getSource();
				lb.removeStyleName("newsItemCommentsLabelHover");
			}
		});
		
		reportLinkPanel.add(image);
		reportLinkPanel.add(label);
	}

	
	
}
