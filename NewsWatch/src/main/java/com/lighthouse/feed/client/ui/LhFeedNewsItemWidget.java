package com.lighthouse.feed.client.ui;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.main.newsitem.client.ui.LhNewsItemWidget;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
import com.newscenter.client.ui.HorizontalFlowPanel;
/**
 * creating the news item details widget 
 * @author kiran@ensarm.com
 *
 */
public class LhFeedNewsItemWidget extends LhNewsItemWidget {
	
	private  LhFeedNewsItemPresenter feedNewsPresenter;
	
	public LhFeedNewsItemWidget(NewsItems newsItems , LhFeedNewsItemPresenter feedNewsPresenter ) {
		super.newsItem =newsItems;
		this.feedNewsPresenter = feedNewsPresenter;
	}
	
	 /**
     * This method is responsible for creating the list of  tags for particular news item
	 */
	@Override
	public Label createLblRelatedTag(TagItem tagitem) {
		Label label = new Label(tagitem.getTagName());
		label.setStylePrimaryName("newsItemGeneralLbl");
		return label;
		
	}

	@Override
	public void createNewsItemWidgetUI() {
		try {
			boolean isURLNull = false;
			FlexCellFormatter formatter = getFlexCellFormatter();
			
			String link = getNewsItem().getUrl();
			if(link==null){
				isURLNull = true;
			}else{
				if(link.equals(""))
					isURLNull=true;
			}
			
			createNewsItemImagePanel();
			LhFeedNewsItemTitle newsTitle = new LhFeedNewsItemTitle(getNewsItem(), feedNewsPresenter);
			newsTitle.createUI();
			createNewsItemImagePanel();
			setWidget(0, 0, newsTitle);
		
			formatter.setWordWrap(1, 0, true);
			formatter.setWidth(1, 0, "90%");
			setWidget(1, 0, createNewsItemAbstractHTML(isURLNull));
			setWidget(3, 0, createGeneralDetailsPanel());
			if(getNewsItem().getAuthor()!=null){
				setWidget(4, 0, createAuthorDetailsPanel());
				setWidget(5, 0, createNewsItemRelatedTagsPanel());
				setWidget(6, 0, vpPanel);
			}else{
				setWidget(4, 0, createNewsItemRelatedTagsPanel());
				setWidget(5, 0, vpPanel);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		
	}
	
	public HTML createNewsItemAbstractHTML(boolean isURLNull) {
		NewsItems newsitems = getNewsItem();
		String abstractnews = newsitems.getAbstractNews();
		boolean isFullTextEnabled=newsitems.isDisplayFullAbstract();
		if(isURLNull){
			if(isFullTextEnabled && abstractnews.length()>0){
				// clip the abstract news string upto 200 char and add a read more link 
				if(getNewsItem().isDisplayFullAbstract()){
					final Anchor readLess=new Anchor("Read Less");
					setWidget(2, 0, readLess);
					readLess.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							getNewsItem().setDisplayFullAbstract(false);
							feedNewsPresenter.refreshWidget(getNewsItem());
							readLess.removeFromParent();
						}
					});
				}
			}else{
					if(abstractnews.length()>200)
						abstractnews=abstractnews.substring(0, 200)+"....";
					
					final Anchor readMore=new Anchor("Read More");
					setWidget(2, 0, readMore);
					readMore.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							getNewsItem().setDisplayFullAbstract(true);
							feedNewsPresenter.refreshWidget(getNewsItem());
							readMore.removeFromParent();
						}
					});
				}
		}
		HTML abstractLabel = new HTML (abstractnews);
		abstractLabel.setStylePrimaryName("lblAbstract");
		return abstractLabel;
	}
	
	public HorizontalPanel createGeneralDetailsPanel() {
		HorizontalPanel generalDetailsHp = new HorizontalPanel();
		NewsItems newsitems = getNewsItem();
		generalDetailsHp.add(createLabel("PUBLISHED: "
				+ newsitems.getNewsDate()));
		generalDetailsHp.add(new Image("images/verticalSeparator.JPG", 0, 0, 6,
				13));
		generalDetailsHp.add(createLabel("UPLOADED: "
				+ newsitems.getNewsUploadedAt()));
		generalDetailsHp.add(new Image("images/verticalSeparator.JPG", 0, 0, 6,
				13));
		generalDetailsHp
				.add(createLabel("SOURCE: " + newsitems.getNewsSource()));
		generalDetailsHp.setStylePrimaryName("newsItemGeneralDetailsPanel");
		return generalDetailsHp;
	}

}
