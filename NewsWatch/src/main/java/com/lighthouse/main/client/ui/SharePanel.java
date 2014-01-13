package com.lighthouse.main.client.ui;

import com.appUtils.client.exception.FeatureNotSupportedException;
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

public class SharePanel extends Composite{

	private NewsItems newsItem;
	private Image image;
	private HorizontalPanel sharePanel;
	
	/**
	 * Constructor
	 * @param lhUserPermission - the user permission object 
	 * @param newsItem - the news item object
	 * @param flagForCloseImg 
	 */
	public SharePanel(LhUserPermission lhUserPermission, NewsItems newsItem ) throws FeatureNotSupportedException{
		if(lhUserPermission.isSharePermitted() == 1){
			
			sharePanel = new HorizontalPanel();
			image = new Image("images/share_icon.png", 0, -1, 16, 15);
			this.newsItem = newsItem;
			createUI();
			initWidget(sharePanel);
			
		}else
			throw new FeatureNotSupportedException("Share not supported");
	}
	
	/**
	 * Constructor
	 * @param lhUserPermission - the user permission object
	 * @param newsItem - the news item object 
	 * @param image - the image to appear in the panel
	 */
	public SharePanel(LhUserPermission lhUserPermission, NewsItems newsItem, Image image) throws FeatureNotSupportedException{
		if(lhUserPermission.isSharePermitted() == 1){
			sharePanel = new HorizontalPanel();
			this.image = image;
			this.newsItem = newsItem;
			createUI();
			initWidget(sharePanel);
		}else
			throw new FeatureNotSupportedException("Share not supported");
	}
	
	/**
	 * creates the UI
	 */
	private void createUI(){
		Label label = new Label("Share");
		label.setStylePrimaryName("newsItemCommentsLabel");
	
		if(newsItem.getIsLocked()!=1){
			label.addClickHandler(new ClickHandler() {
			
				@Override
				public void onClick(ClickEvent event) {
					ShareStoryPopup shareStoryPopup = new ShareStoryPopup(newsItem); 
					shareStoryPopup.setGlassEnabled(true);
					shareStoryPopup.show();
					shareStoryPopup.center();
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
			sharePanel.add(image);
			sharePanel.add(label);
		}
		
	}

	
	
}
