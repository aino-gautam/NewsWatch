package com.lighthouse.feed.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.lighthouse.feed.client.ui.body.IBodyView.IBodyPresenter;
import com.newscenter.client.news.NewsItems;
/**
 * class for presenting edit link for particular news item 
 * @author kiran@ensarm.com
 *
 */
public class EditLinkPanel extends ReverseCompositeView<IBodyPresenter>{
	
	private NewsItems newsItems;
	private Image editImage = new Image("images/feed/edit-icon.png");
	private Anchor editLabel = new Anchor("Edit");
	private HorizontalPanel mainPanel= new HorizontalPanel();
	private EditLinkPopup editLinkPopup;
	private LhFeedNewsItemPresenter feedNewsPresenter;
	
	public EditLinkPanel(){
		
	}
	public EditLinkPanel(NewsItems newsItems,LhFeedNewsItemPresenter feedNewsPresenter) {
		this.newsItems = newsItems;
		this.feedNewsPresenter = feedNewsPresenter;
		createUi(); 
		initWidget(mainPanel);
	}
	private void createUi() {
		//editLabel.setStylePrimaryName("newsItemCommentsLabel");
		//editLabel.setWidth("100%");
		DOM.setStyleAttribute(mainPanel.getElement(), "margin", "0px 3px");
		DOM.setStyleAttribute(editImage.getElement(), "marginRight", "2px");
		editLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
			    editLinkPopup = new EditLinkPopup(newsItems, feedNewsPresenter); 
				editLinkPopup.center();
				editLinkPopup.show();
			}
		});
		mainPanel.add(editImage);
		mainPanel.add(editLabel);
	}

   
}
