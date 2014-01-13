package com.lighthouse.main.client.ui;

import com.appUtils.client.exception.FeatureNotSupportedException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.newscenter.client.news.NewsItems;

public class ViewsPanel extends Composite{

	private NewsItems newsItem;
	private Image image;
	HorizontalPanel viewsPanel;
	
	public ViewsPanel(LhUserPermission lhUserPermission, NewsItems newsItem)throws FeatureNotSupportedException{
		if(lhUserPermission.isViewsPermitted() == 1){
			viewsPanel = new HorizontalPanel();
			image = new Image("images/eye.png", 0, -2, 20, 15);
			this.newsItem = newsItem;
			createUI();
			initWidget(viewsPanel);
		}else
			throw new FeatureNotSupportedException("Views not supported");
	}
	
	public ViewsPanel(LhUserPermission lhUserPermission, NewsItems newsItem, Image image) throws FeatureNotSupportedException{
		if(lhUserPermission.isViewsPermitted() == 1){
			viewsPanel = new HorizontalPanel();
			this.image = image;
			this.newsItem = newsItem;
			createUI();
			initWidget(viewsPanel);
		}else
			throw new FeatureNotSupportedException("Views not supported");
	}
	
	private void createUI(){
		
		
		Label label = new Label(newsItem.getViewsCount()+" ");
		label.setStylePrimaryName("newsItemViewsLabel");
		
		viewsPanel.add(image);
		viewsPanel.add(label);
		
	}
}
