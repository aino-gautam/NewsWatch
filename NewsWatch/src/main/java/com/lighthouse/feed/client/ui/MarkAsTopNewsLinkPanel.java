package com.lighthouse.feed.client.ui;


import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;

import com.google.gwt.user.client.ui.HorizontalPanel;

import com.lighthouse.feed.client.service.FeedService;
import com.lighthouse.feed.client.service.FeedServiceAsync;
import com.lighthouse.feed.client.ui.body.IBodyView.IBodyPresenter;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
/**
 * This class is for displaying the mark and unmark as top news link for particular news item
 * @author kiran@ensarm.com
 *
 */
public class MarkAsTopNewsLinkPanel extends ReverseCompositeView<IBodyPresenter> implements ClickHandler{
	
	private Anchor markAsTopNews = new Anchor("Mark as top news");
	private HorizontalPanel mainPanel= new HorizontalPanel();
	private NewsItems newsItems;
	private boolean markStatus =false;
	private Image topNewsImage = new Image();
	private static String MARKEDNEWS = "images/feed/starred_orange.png";
	private static String UNMARKEDNEWS = "images/feed/star-active.png";
	private TagItem markedTopTag;
	LhFeedNewsItemPresenter feedNewsPresenter;
	
 	public MarkAsTopNewsLinkPanel(NewsItems newsItems ,LhFeedNewsItemPresenter feedNewsPresenter) {
		this.newsItems = newsItems;
		this.feedNewsPresenter = feedNewsPresenter;
		mainPanel.add(topNewsImage);
		mainPanel.add(markAsTopNews);
		if(newsItems.isMarkedAsTop()){
			markAsTopNews.setText("Unmark as top news");
			markAsTopNews.addStyleName("markedItem");
			topNewsImage.setUrl(MARKEDNEWS);
		}else{
			markAsTopNews.setText("Mark as top news");
			markAsTopNews.removeStyleName("markedItem");
			topNewsImage.setUrl(UNMARKEDNEWS);
		}
		markAsTopNews.addClickHandler(this);
		topNewsImage.addClickHandler(this);
		topNewsImage.addStyleName("clickable");
		DOM.setStyleAttribute(mainPanel.getElement(), "margin", "0px 3px");
		mainPanel.setCellVerticalAlignment(topNewsImage, HasVerticalAlignment.ALIGN_TOP);
		topNewsImage.setHeight("17px");
		initWidget(mainPanel);
		
	}
 	
 	private void saveNewsAsTopNews(){
 		String ncIdStr=Window.Location.getParameter("NCID");
		int industryId=Integer.parseInt(ncIdStr);
		FeedServiceAsync service = GWT.create(FeedService.class);
		final Long newsId =(long) newsItems.getNewsId();
		
		if(markAsTopNews.getText().equals("Mark as top news")){
			markStatus =true;
			markAsTopNews.setText("Unmark as top news");
			markAsTopNews.addStyleName("markedItem");
			topNewsImage.setUrl(MARKEDNEWS);
		}else{
			markStatus =false;
			markAsTopNews.setText("Mark as top news");
			markAsTopNews.removeStyleName("markedItem");
			topNewsImage.setUrl(UNMARKEDNEWS);
		}
		service.markAsTopNews(newsId, industryId,markStatus,new AsyncCallback<TagItem>() {
			Iterator itr = newsItems.getAssociatedTagList().iterator(); 
			@Override
			public void onSuccess(TagItem tag) {
				if(tag == null){
					toggleMarkTopNewsLabel(markStatus);
					
				}else{
					if(markStatus == true){
						boolean exist=false;
						newsItems.setMarkedAsTop(true);
						while(itr.hasNext()) {
							TagItem element = (TagItem) itr.next(); 
						   if(element.getTagName().equals(tag.getTagName())){
							   exist=true;
							   break;
						   }
						} 
						if(!exist)
							newsItems.addTagforNews(tag);
						markedTopTag=tag;
						
					}else{
						markedTopTag=tag;
						newsItems.setMarkedAsTop(false);
						while(itr.hasNext()) {
						   TagItem element = (TagItem) itr.next(); 
						   if(element.getTagName().equals(tag.getTagName()))
							   itr.remove();
						} 
					}
				}
				feedNewsPresenter.refreshWidget(newsItems);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				toggleMarkTopNewsLabel(markStatus);
			}
		});
 	}
 	
 	private void toggleMarkTopNewsLabel(boolean markStatus){
 		if(markStatus == true){
			markAsTopNews.setText("Mark as top news");
			markAsTopNews.removeStyleName("markedItem");
			topNewsImage.setUrl(UNMARKEDNEWS);
			markStatus = false;
		}else{
			markAsTopNews.setText("Unmark as top news");
			markAsTopNews.addStyleName("markedItem");
			topNewsImage.setUrl(MARKEDNEWS);
			markStatus = true;
		}
 	}
 	
	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() instanceof Anchor)
			saveNewsAsTopNews();
		else if(event.getSource() instanceof Image)
			saveNewsAsTopNews();
	}

}
