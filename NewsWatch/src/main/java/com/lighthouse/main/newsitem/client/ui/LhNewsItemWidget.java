package com.lighthouse.main.newsitem.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.appUtils.client.exception.FeatureNotSupportedException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.client.ui.CommentsPanel;
import com.lighthouse.main.client.ui.ReportDeadLinkPanel;
import com.lighthouse.main.client.ui.SharePanel;
import com.lighthouse.main.client.ui.ViewsPanel;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
import com.newscenter.client.ui.AppliedTagsLabel;
import com.newscenter.client.ui.HorizontalFlowPanel;

/**
 * Represents a single news item widget
 * 
 * @author nairutee & kiran
 * 
 */
public class LhNewsItemWidget extends FlexTable {

	protected NewsItems newsItem;
	protected VerticalPanel vpPanel = new VerticalPanel();
	protected LhUser lhUser;
	protected LhNewsItemPresenter newsPresenter;
	
	protected HorizontalPanel commentsAndViewsHp;
	
	/**
	 * constructor
	 */
	public LhNewsItemWidget() {
		setStylePrimaryName("newsitemview");
	}

	/**
	 * Constructor
	 * 
	 * @param newsItem
	 *            - takes a NewsItems object
	 */
	public LhNewsItemWidget(NewsItems newsItem, LhUser lhUser) {
		this.lhUser = lhUser;
		this.newsItem = newsItem;
		setStylePrimaryName("newsitemview");
	}

	public LhNewsItemWidget(NewsItems newsItem, LhUser lhUser, LhNewsItemPresenter newsItemPresenter) {
		this(newsItem, lhUser);
		this.newsPresenter = newsItemPresenter;
	} 
	
	public NewsItems getNewsItem() {
		return newsItem;
	}

	public void setNewsItem(NewsItems newsItem) {
		this.newsItem = newsItem;
	}

	/**
	 * creates the UI for the news item widget
	 */
	public void createNewsItemWidgetUI() {
		try {
			boolean isURLNull = false;
			FlexCellFormatter formatter = getFlexCellFormatter();
			createNewsItemImagePanel();
			LhNewsItemTitle newsTitle = new LhNewsItemTitle(getNewsItem());
			newsTitle.createUI();
			setWidget(0, 0, newsTitle);
			String link = newsItem.getUrl();
			if(link==null){
				isURLNull = true;
			}
			else{
				if(link.equals(""))
					isURLNull = true;
			}
			
			formatter.setWordWrap(1, 0, true);
			formatter.setWidth(1, 0, "90%");
			setWidget(1, 0, createNewsItemAbstract(isURLNull));
			setWidget(3, 0, createGeneralDetailsPanel());
			if(getNewsItem().getAuthor()!=null){
				setWidget(4, 0, createAuthorDetailsPanel());
				setWidget(5, 0, createNewsItemRelatedTagsPanel());
				setWidget(6, 0, createCommentsAndViewsPanel());
				setWidget(7, 0, vpPanel);
			}else{
				setWidget(4, 0, createNewsItemRelatedTagsPanel());
				setWidget(5, 0, createCommentsAndViewsPanel());
				setWidget(6, 0, vpPanel);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * creates the abstract of the news item widget
	 * 
	 * @return Label
	 */
	public HTML createNewsItemAbstract(boolean isURLNull) {
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
							newsPresenter.refreshWidget(getNewsItem());
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
							newsPresenter.refreshWidget(getNewsItem());
							readMore.removeFromParent();
						}
					});
				}
		}
		HTML abstractLabel = new HTML (abstractnews);
		abstractLabel.setStylePrimaryName("lblAbstract");
		return abstractLabel;
	}

	/**
	 * creates a panel to hold general details of the news item like published
	 * date, source etc
	 * 
	 * @return
	 */
	public HorizontalPanel createGeneralDetailsPanel() {
		HorizontalPanel generalDetailsHp = new HorizontalPanel();
		NewsItems newsitems = getNewsItem();
		generalDetailsHp.add(createLabel("PUBLISHED: "
				+ newsitems.getNewsDate()));
		generalDetailsHp.add(new Image("images/verticalSeparator.JPG", 0, 0, 6,
				13));
		generalDetailsHp
				.add(createLabel("SOURCE: " + newsitems.getNewsSource()));
		generalDetailsHp.setStylePrimaryName("newsItemGeneralDetailsPanel");
		return generalDetailsHp;
	}

	/**
	 * create the label to hold general details of the news item like published
	 * date, source
	 * 
	 * @param string
	 * @return
	 */
	public Label createLabel(String string) {
		Label label = new Label(string);
		label.setStylePrimaryName("newsItemGeneralLbl");
		return label;
	}

	public HorizontalPanel createAuthorDetailsPanel() {
		HorizontalPanel authhorDetailsHp = new HorizontalPanel();
		NewsItems newsitems = getNewsItem();
		if(newsitems.getAuthor()!=null){
			if(!newsitems.getAuthor().equalsIgnoreCase("")){
			authhorDetailsHp.add(createLabel("AUTHOR: "+ newsitems.getAuthor()));
			}
		}
		return authhorDetailsHp;
	}
	
	/**
	 * creates the related tags panel of the news item
	 * 
	 * @return HorizontalPanel
	 */
	public HorizontalFlowPanel createNewsItemRelatedTagsPanel() {
		/*HorizontalPanel relatedTagsHp = new HorizontalPanel();*/
		HorizontalFlowPanel relatedTagsHp = new HorizontalFlowPanel();
		relatedTagsHp.setWidth("100%");
		NewsItems newsitems = getNewsItem();
		relatedTagsHp.add(createLabel("TAGS: "));
		ArrayList arraylisttags = newsitems.getAssociatedTagList();
		Iterator iter = arraylisttags.iterator();
		int count = 0;
		while (iter.hasNext()) {
			if (count != arraylisttags.size() - 1) {
				TagItem tag = (TagItem) iter.next();
				relatedTagsHp.add(createLblRelatedTag(tag));
				Image img = new Image("images/verticalSeparator.JPG", 0, 0, 6, 13);
				relatedTagsHp.add(img);
				count++;
			} else {
				TagItem tag = (TagItem) iter.next();
				relatedTagsHp.add(createLblRelatedTag(tag));
				break;
			}
		}
		return relatedTagsHp;
	}

	/**
	 * create the related tag label of the news item
	 * 
	 * @param tagitem
	 * @return
	 */

	public Label createLblRelatedTag(TagItem tagitem) {
		AppliedTagsLabel appliedtaglabel = new AppliedTagsLabel(tagitem);
		appliedtaglabel.setText(tagitem.getTagName());
		appliedtaglabel.setWordWrap(false);
		appliedtaglabel.setStylePrimaryName("lblRelatedTag");
		if (tagitem.isPrimary())
			appliedtaglabel.addStyleName("primaryTag");
		appliedtaglabel
				.setTitle("View news related to " + tagitem.getTagName());
		return appliedtaglabel;
	}

	/**
	 * creates the panel displaying the no. of comments and no. of views that
	 * the news item has
	 * 
	 * @return
	 */
	public HorizontalPanel createCommentsAndViewsPanel() {
		commentsAndViewsHp = new HorizontalPanel();
		
		getCommentsPanel();
		getViewsPanel();
		getSharePanel();
		getReportLinkPanel();
		commentsAndViewsHp.setStylePrimaryName("newsItemCommentsPanel");
		return commentsAndViewsHp;
	}
	
	/**
	 * creates the comments panel
	 */
	public void getCommentsPanel(){
		try {
			CommentsPanel commentsPanel = new CommentsPanel(lhUser.getUserPermission(), getNewsItem());
			commentsAndViewsHp.add(commentsPanel);
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * creates the views panel
	 */
	public void getViewsPanel(){
		try {
			ViewsPanel viewsPanel = new ViewsPanel(lhUser.getUserPermission(), getNewsItem());
			commentsAndViewsHp.add(viewsPanel);
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * creates the share panel
	 * @param flagForCloseImg 
	 */
	public void getSharePanel(){
		try {
			SharePanel sharePanel = new SharePanel(lhUser.getUserPermission(), getNewsItem());
			commentsAndViewsHp.add(sharePanel);
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * creates the report dead link panel
	 * @param flagForCloseImg 
	 */
	public void getReportLinkPanel(){
		ReportDeadLinkPanel reportLinkPanel = new ReportDeadLinkPanel(lhUser.getUserPermission(), newsItem, lhUser.getEmail());
		commentsAndViewsHp.add(reportLinkPanel);
	}

	/**
	 * creates the panel to display the image for the news item
	 * 
	 * @return HorizontalPanel
	 */
	public void createNewsItemImagePanel() {
		NewsItems newsitems = getNewsItem();
		if (newsitems.getImageUrl() != null) {
			if (!newsitems.getImageUrl().equals("")) {
				HorizontalPanel imagePanel = new HorizontalPanel();
				FlexCellFormatter formatter = getFlexCellFormatter();
				formatter.setRowSpan(0, 2, 5);
				imagePanel.setStylePrimaryName("imageBorder");
				imagePanel.add(createImage(newsitems.getImageUrl()));
				formatter.setWidth(0, 2, "10%");
				setWidget(0, 2, imagePanel);
				formatter.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
			}
		}
	}

	/**
	 * create the Image for the news item
	 * 
	 * @param imageUrl
	 * @return
	 */
	public Image createImage(String imageUrl) {
		String urlClient = GWT.getModuleBaseURL();
		String[] mainurl = new String[5];
		mainurl = urlClient.split("/");
		String urlPort = mainurl[0] + "//" + mainurl[2];
		String imageurls = /* urlPort+"/NewsCenter/"+ */imageUrl;
		Image image = new Image(imageurls);
		image.setStylePrimaryName("newsimage");
		return image;

	}

	public LhUser getLhUser() {
		return lhUser;
	}

	public void setLhUser(LhUser lhUser) {
		this.lhUser = lhUser;
	}
}
