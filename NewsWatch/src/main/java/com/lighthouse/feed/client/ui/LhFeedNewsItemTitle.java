package com.lighthouse.feed.client.ui;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.lighthouse.main.newsitem.client.ui.LhNewsItemTitle;
import com.newscenter.client.news.NewsItems;
/**
 * creating the news item title
 * @author kiran@ensarm.com
 *
 */
public class LhFeedNewsItemTitle extends LhNewsItemTitle{

	HorizontalPanel optionPanel;
	LhFeedNewsItemPresenter feedNewsPresenter;
	 
	public LhFeedNewsItemTitle(NewsItems newsItem, LhFeedNewsItemPresenter feedNewsPresenter) {
		super(newsItem);
		this.feedNewsPresenter = feedNewsPresenter;
	}
	
	@Override
	public void createUI(){
		int a=newsItem.getNewsId();
		HTML titleHTML = new HTML();
		String text = newsItem.getNewsTitle();
		String link = newsItem.getUrl();
		if(link!=null)
			link.trim();
		
		
		if (newsItem.getIsLocked()!=1) {
			if(link!=null){
				if(!link.equals("")){
					if (link.startsWith("http://")) {
						titleHTML
								.setHTML("<a href='"
										+ link
										+ "' target=\"_new\" style=\" font-family:Arial; font-weight:bold; font-size:125%; color:#2854BB; text-decoration:none; text-align:left; margin-top:2px; \"><strong class=\"newslink\">"
										+ text + "</strong></font></a> ");
					} else {
						link = "http://" + link;
						titleHTML
								.setHTML("<a href='"
										+ link
										+ "' target=\"_new\" style=\" font-family:Arial; font-weight:bold; font-size:125%; color:#2854BB; text-decoration:none; text-align:left; margin-top:2px;  \"><strong class=\"newslink\">"
										+ text + "</strong></font></a> ");
						//&nbsp edit &nbsp Mark as top News &nbsp Delete
					}
				
					titleHTML.setWordWrap(true);
					titleHTML.addClickHandler(this);
				}else{
					titleHTML.setHTML("<strong style=\" font-family:Arial; font-weight:bold; font-size:125%; color:#2854BB; text-decoration:none; text-align:left; margin-top:2px;\">"
							+ text + "</strong>");
					titleHTML.setWordWrap(true);
				}
			}else{
				titleHTML.setHTML("<strong style=\" font-family:Arial; font-weight:bold; font-size:125%; color:#2854BB; text-decoration:none; text-align:left; margin-top:2px;\">"
						+ text + "</strong>");
				titleHTML.setWordWrap(true);
			}
		}
		else{
			if(link!=null){
				if(!link.equals("")){
					if (link.startsWith("http://")) {
						titleHTML.setHTML("<strong style=\" font-family:Arial; font-weight:bold; font-size:125%; color:#2854BB; text-decoration:none; text-align:left; margin-top:2px; class=\"newslink\">"+ text + "</strong></font>");
					} else {
						link = "http://" + link;
						titleHTML.setHTML("<strong style=\" font-family:Arial; font-weight:bold; font-size:125%; color:#2854BB; text-decoration:none; text-align:left; margin-top:2px; class=\"newslink\">"+ text + "</strong></font>");
					}
					titleHTML.setWordWrap(true);
					titleHTML.addClickHandler(this);
				}else{
					titleHTML.setHTML("<strong style=\" font-family:Arial; font-weight:bold; font-size:125%; color:#2854BB; text-decoration:none; text-align:left; margin-top:2px;\">"
							+ text + "</strong>");
					titleHTML.setWordWrap(true);
				}
			}else{
				titleHTML.setHTML("<strong style=\" font-family:Arial; font-weight:bold; font-size:125%; color:#2854BB; text-decoration:none; text-align:left; margin-top:2px;\">"
						+ text + "</strong>");
				titleHTML.setWordWrap(true);
			}
			
		}
		titleHTML.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				HTML html = (HTML) event.getSource();
				html.removeStyleName("lblUrlHover");
			}
		});
		titleHTML.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				HTML html = (HTML) event.getSource();
				html.addStyleName("lblUrlHover");
				
			}
		});
		
		titlePanel.add(titleHTML);
		DOM.setStyleAttribute(titlePanel.getElement(), "paddingLeft", "3px");
		if(newsItem.getIsLocked() == 1){
			Image lockedNewsImg = new Image("images/key_gray.png");
			lockedNewsImg.addStyleName("clickable");
			DOM.setStyleAttribute(lockedNewsImg.getElement(), "marginLeft", "3px");
			lockedNewsImg.addClickHandler(this);
			titlePanel.add(lockedNewsImg);
			titlePanel.setCellVerticalAlignment(lockedNewsImg, HasVerticalAlignment.ALIGN_MIDDLE);
		}
		
		optionPanel = new HorizontalPanel();
		getEditLinkPanel();
		optionPanel.add(new Image("images/verticalSeparator.JPG",0,0,6,13));
		getMarkAsTopNewsLinkPanel();
		optionPanel.add(new Image("images/verticalSeparator.JPG",0,0,6,13));
		getDeleteLinkPanel();
		DOM.setStyleAttribute(optionPanel.getElement(), "marginLeft", "5px");
		titlePanel.add(optionPanel);
		titlePanel.setCellVerticalAlignment(optionPanel, HasVerticalAlignment.ALIGN_MIDDLE);
	}
	
	 /**
     * This method is responsible for creating the delete link for particular news item
	 */
	private void getDeleteLinkPanel() {
		try{
			DeleteLinkPanel deleteLinkPanel = new DeleteLinkPanel(newsItem);
			optionPanel.add(deleteLinkPanel);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


	 /**
     * This method is responsible for creating the mark ad top news link for particular news item
	 */
	private void getMarkAsTopNewsLinkPanel() {
		try{
			MarkAsTopNewsLinkPanel asTopNewsLinkPanel = new MarkAsTopNewsLinkPanel(newsItem,feedNewsPresenter);
			optionPanel.add(asTopNewsLinkPanel);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


    /**
     * This method is responsible for creating the edit link for particular news item
	 */
    private void getEditLinkPanel() {
		try{
			EditLinkPanel editLinkPanel = new EditLinkPanel(newsItem, feedNewsPresenter);
			optionPanel.add(editLinkPanel);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
