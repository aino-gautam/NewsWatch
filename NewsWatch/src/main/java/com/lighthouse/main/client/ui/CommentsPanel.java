package com.lighthouse.main.client.ui;

import com.appUtils.client.exception.FeatureNotSupportedException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.newscenter.client.news.NewsItems;

/**
 * Class to display the number of comments and a clickable label
 * @author nairutee
 *
 */
public class CommentsPanel extends Composite {

	private NewsItems newsItem;
	private Image image;
	private HorizontalPanel commentsPanel;
	
	/**
	 * Constructor
	 * @param lhUserPermission - the user permission object 
	 * @param newsItem - the news item object
	 */
	public CommentsPanel(LhUserPermission lhUserPermission, NewsItems newsItem) throws FeatureNotSupportedException{
		if(lhUserPermission.isCommentsPermitted() == 1){
			commentsPanel = new HorizontalPanel();
			image = new Image("images/comment.png", 0, -2, 14, 15);
			this.newsItem = newsItem;
			createUI();
			initWidget(commentsPanel);
			
		}else
			throw new FeatureNotSupportedException("Comments not supported");
	}
	
	/**
	 * Constructor
	 * @param lhUserPermission - the user permission object
	 * @param newsItem - the news item object 
	 * @param image - the image to appear in the panel
	 */
	public CommentsPanel(LhUserPermission lhUserPermission, NewsItems newsItem, Image image) throws FeatureNotSupportedException{
		if(lhUserPermission.isCommentsPermitted() == 1){
			commentsPanel = new HorizontalPanel();
			this.image = image;
			this.newsItem = newsItem;
			createUI();
			initWidget(commentsPanel);
		}else
			throw new FeatureNotSupportedException("Comments not supported");
	}
	
	/**
	 * creates the UI
	 */
	private void createUI(){
		Label label = new Label(newsItem.getCommentsCount()+" Comments");
		label.setStylePrimaryName("newsItemCommentsLabel");
		label.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int id = newsItem.getNewsId();
				String urlQueryString = Window.Location.getQueryString();
				if(urlQueryString.equals("") || urlQueryString == null)
					urlQueryString+="?nid="+id;
				else
					urlQueryString+="&nid="+id;
				String url = GWT.getHostPageBaseURL() + "newshome.html"+urlQueryString;
				Window.open(url, "_blank", "");
				
				
				//Window.open("newshome.html?nid="+ id, "_blank", "");
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
		
		commentsPanel.add(image);
		commentsPanel.add(label);
	}
}
