package com.lighthouse.newspage.client.ui;

import java.util.HashMap;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.newsitem.client.ui.LhNewsItemTitle;
import com.lighthouse.main.newsitem.client.ui.LhNewsItemWidget;
import com.lighthouse.newspage.client.domain.CommentedNewsItem;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

/**
 * 
 * @author kiran@ensarm.com This class for creating the particular newsItem for
 *         NewsItemHome
 * 
 */
public class NewsItemHomeWidget extends LhNewsItemWidget {

	private HashMap<HTML,CommentedNewsItem > mapOfLinkVsNewsitem = new HashMap<HTML, CommentedNewsItem >();
	private CommentedNewsItem commentedNewsItem;
	
	public NewsItemHomeWidget(CommentedNewsItem commentedNewsItem,LhUser lhUser) {
		super(commentedNewsItem, lhUser);
		this.commentedNewsItem= commentedNewsItem;
		setWidth("95%");
	}

	@Override
	public void createNewsItemWidgetUI() {
		try {
			FlexCellFormatter formatter = getFlexCellFormatter();
			createNewsItemImagePanel();
			LhNewsItemTitle newsTitle = new LhNewsItemTitle(getNewsItem());
			newsTitle.createUI();
			setWidget(0, 0, newsTitle);
			formatter.setWordWrap(1, 0, true);
			formatter.setWidth(1, 0, "75%");
			setWidget(1, 0, createNewsItemAbstract(false));
			setWidget(3, 0, createGeneralDetailsPanel());
			setWidget(4, 0, createNewsItemRelatedTagsPanel());
			setWidget(5, 0, createCommentsAndViewsPanel());
			setWidget(6, 0, vpPanel);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	@Override
	public void createNewsItemImagePanel() {
		NewsItems newsitems = getNewsItem();
		if (newsitems.getImageUrl() != null) {
			if (!newsitems.getImageUrl().equals("")) {
				HorizontalPanel imagePanel = new HorizontalPanel();
				FlexCellFormatter formatter = getFlexCellFormatter();
				formatter.setRowSpan(0, 2, 5);
				imagePanel.setStylePrimaryName("imageBorder");
				imagePanel.add(createImage(newsitems.getImageUrl()));
				
				setWidget(0, 2, imagePanel);
				formatter.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
			}
		}
	}
	
	@Override
	public HorizontalPanel createCommentsAndViewsPanel() {
		commentsAndViewsHp = new HorizontalPanel();
		getViewsPanel();
		getSharePanel();
		getReportLinkPanel();
		commentsAndViewsHp.setStylePrimaryName("newsItemCommentsPanel");
		return commentsAndViewsHp;
	}
	
	@Override
	public Label createLblRelatedTag(TagItem tagitem) {
		Label label = new Label(tagitem.getTagName());
		label.setStylePrimaryName("newsItemGeneralLbl");
		return label;
	}

	
	public CommentedNewsItem getCommentedNewsItem() {
		return commentedNewsItem;
	}

	public void setCommentedNewsItem(CommentedNewsItem commentedNewsItem) {
		this.commentedNewsItem = commentedNewsItem;
	}

}
