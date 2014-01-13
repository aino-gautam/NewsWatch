package com.lighthouse.statistics.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.appUtils.client.NotificationPopup;
import com.appUtils.client.NotificationPopup.NotificationType;
import com.appUtils.client.exception.FeatureNotSupportedException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.statistics.client.domain.NewsStatistics;
import com.lighthouse.statistics.client.service.StatisticsService;
import com.lighthouse.statistics.client.service.StatisticsServiceAsync;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

/**
 * creates the pulse widget to be displayed on the right section of the
 * newspresenter
 * 
 * @author nairutee & kiran@ensarm.com
 * 
 */
public class NewsStatisticsWidget extends Composite implements ClickHandler,
		MouseOverHandler, MouseOutHandler, SubmitCompleteHandler {

	private NewsItems newsItem;
	private HashMap<HTML, NewsItems> mapOfLinkVsNewsitem = new HashMap<HTML, NewsItems>();
	private VerticalPanel mainBasePanel = new VerticalPanel();
	NewsStatistics newsStatistics;
	private Group group;
	private String groupName;
	VerticalPanel vp = new VerticalPanel();
	private LhUserPermission lhUserPermission;
	private HorizontalPanel titlePanel;
	private int groupId;
	private Hidden txtnewsInformation = new Hidden();
	private String newsItemInformation = "";
	private String newsItemTags = "";
	private Hidden txtNewsItemTags = new Hidden();
	int clickedTop;
	int clickedLeft;
	NotificationPopup ntp;

	/**
	 * constructor
	 * 
	 * @param groupId
	 */
	public NewsStatisticsWidget(int groupId, String groupName, LhUserPermission lhUserPermission) throws FeatureNotSupportedException {
		if (lhUserPermission.isPulsePermitted() == 1) {
			this.groupName = groupName;
			this.lhUserPermission = lhUserPermission;
			this.groupId = groupId;
			titlePanel = new HorizontalPanel();
			initWidget(mainBasePanel);
			mainBasePanel.setWidth("100%");
		} else
			throw new FeatureNotSupportedException("Pulse not supported");
	}

	public NewsStatisticsWidget(Group group, LhUserPermission lhUserPermission) throws FeatureNotSupportedException{
		if (lhUserPermission.isPulsePermitted() == 1) {
			this.lhUserPermission = lhUserPermission;
			this.group = group;
			titlePanel = new HorizontalPanel();
			initWidget(mainBasePanel);
			mainBasePanel.setWidth("100%");
		} else
			throw new FeatureNotSupportedException("Pulse not supported");
	}
	/**
	 * Initializes the widget by fetching data from server
	 * 
	 * @param groupId
	 */
	public void initialize() {
		getNewsStatisticsData();
	}

	/**
	 * This method for changing the most read in group and all groups news items
	 * from 2/3 part of the pulse section based on the tag changed
	 * @param newsmode 
	 */
	public void refresh(int newsmode) {
		StatisticsServiceAsync serviceAsync = (StatisticsServiceAsync) GWT
				.create(StatisticsService.class);
		serviceAsync.getRefreshNewsStatisticsData(/*groupId*/group,
				newsmode, new AsyncCallback<NewsStatistics>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

					}

					@Override
					public void onSuccess(NewsStatistics result) {
						createUI(result);

					}
				});
	}

	/**
	 * This method for get News Statistics Data for e.g most read in group and
	 * all groups,most discussed in group and all group
	 * 
	 * @param groupId
	 */
	private void getNewsStatisticsData() {
		StatisticsServiceAsync serviceAsync = (StatisticsServiceAsync) GWT
				.create(StatisticsService.class);
		serviceAsync.getNewsStatisticsData(/*groupId*/group,
				new AsyncCallback<NewsStatistics>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(NewsStatistics result) {
						createUI(result);
					}

				});
	}

	/**
	 * creates the UI of the widget
	 * 
	 * @param result
	 */
	public void createUI(NewsStatistics result) {
		if (lhUserPermission.isGroupsPermitted() == 1) {
			Label label = new Label(" Activities in the past 7 days");
			label.setStylePrimaryName("titleLabelAct");
			vp.add(label);
			if(lhUserPermission.isCommentsPermitted() ==1){
				createStatisticsFlex("Most discussed items in " + group.getGroupName(),
						result.getMostDiscussedInGroupNews());
			}
			if(lhUserPermission.isViewsPermitted()==1){
				createStatisticsFlex("Most read items in " + group.getGroupName(),
						result.getMostReadInGroupNews());
			}
		}
		if(lhUserPermission.isCommentsPermitted() ==1)
			createStatisticsFlex("Most discussed items in portal",result.getMostDiscussedInAllGroupsNews());
		if(lhUserPermission.isViewsPermitted()==1)
			createStatisticsFlex("Most read items in portal",result.getMostReadInAllGroupsNews());
		
		mainBasePanel.add(vp);
	}

	/**
	 * This method will create a flex table for a type of stats data that needs
	 * to be displayed
	 * 
	 * @param flexTitle
	 *            - the title of the table
	 * @param newsList
	 *            - the data to be displayed
	 * @return FlexTable
	 */
	private void createStatisticsFlex(String flexTitle, List<NewsItems> newsList) {
		FlexTable flex = new FlexTable();
		Label titleLabel = new Label(flexTitle);
		Label msgLabel = new Label("No News For " + " " + flexTitle);
		titleLabel.setStylePrimaryName("titleLabel");
		flex.setWidget(0, 0, titleLabel);
		int row = 1;
		if (newsList.isEmpty()) {
			flex.setWidget(row, 0, msgLabel);
		}
		for (NewsItems newsItem : newsList) {

			flex.setWidget(row, 0, createNewsItemTitle(newsItem));
			// flex.setCellSpacing(0);

			row += 2;

		}
		flex.setStylePrimaryName("newsStatisticsFlex");

		vp.add(flex);

	}

	/**
	 * creates the title of the news item widget
	 * 
	 * @return HTML
	 */
	private HTML createNewsItemTitle(NewsItems newsItem) {
		int newsId = newsItem.getNewsId();
		HorizontalPanel titlePanelHp = new HorizontalPanel();
		HTML titleHtml = new HTML();
		String link = newsItem.getUrl();
		String text = newsItem.getNewsTitle();
		if (newsItem.getIsLocked() != 1) {
			if (link.startsWith("http://")) {
				titleHtml
						.setHTML("<a href='"
								+ link
								+ "' target=\"_new\" style=\" font-family:Arial; font-weight:bold; font-size:105%; color:#2854BB; text-decoration:none; text-align:left; \"><strong class=\"newslink\">"
								+ text + "</strong></font></a>");
				mapOfLinkVsNewsitem.put(titleHtml, newsItem);
			} else {
				link = "http://" + link;
				titleHtml
						.setHTML("<a href='"
								+ link
								+ "' target=\"_new\" style=\" font-family:Arial; font-weight:bold; font-size:105%; color:#2854BB; text-decoration:none; text-align:left; \"><strong class=\"newslink\">"
								+ text + "</strong></font></a>");
				mapOfLinkVsNewsitem.put(titleHtml, newsItem);
			}
			titleHtml.setWordWrap(true);
			titleHtml.addClickHandler(this);
		} else {
			if (link.startsWith("http://")) {
				titleHtml
						.setHTML("<strong style=\" font-family:Arial; font-weight:bold; font-size:105%; color:#2854BB; text-decoration:none; text-align:left; \"><strong class=\"newslink\">"
								+ text + "</strong></font>");
				mapOfLinkVsNewsitem.put(titleHtml, newsItem);
			} else {
				link = "http://" + link;
				titleHtml
						.setHTML("<strong style=\" font-family:Arial; font-weight:bold; font-size:105%; color:#2854BB; text-decoration:none; text-align:left; \"><strong class=\"newslink\">"
								+ text + "</strong></font>");
				mapOfLinkVsNewsitem.put(titleHtml, newsItem);
			}
			titleHtml.setWordWrap(true);
			titleHtml.addClickHandler(this);

		}

		titleHtml.addMouseOutHandler(this);
		titleHtml.addMouseOverHandler(this);
		titlePanel.add(titlePanelHp);

		return titleHtml;
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		if (sender instanceof HTML) {
			HTML html = (HTML) sender;
			NewsItems newsitems = mapOfLinkVsNewsitem.get(html);
			this.newsItem = newsitems;
			clickedTop = event.getY() + titlePanel.getAbsoluteTop();
			clickedLeft = event.getX();
			if (newsitems.getIsLocked() == 1)
				showAccessPopup();
			else {

				Hidden hiddenNewsItemID = new Hidden();
				hiddenNewsItemID.setName("newsitemid");
				hiddenNewsItemID.setDefaultValue(String.valueOf(newsitems
						.getNewsId()));

				String url = GWT.getHostPageBaseURL()
						+ "com.lighthouse.login.lhlogin/itemView";
				
				String URL = GWT.getModuleBaseURL() + "itemView";
				// String url = GWT.getHostPageBaseURL() +
				// "com.login.login/itemView";
				// String url =
				// GWT.getHostPageBaseURL()+"com.login.login/emailitemView";
				FormPanel form = new FormPanel();
				mainBasePanel.add(form);
				// vpPanel.add(form);
				form.setEncoding(FormPanel.ENCODING_MULTIPART);
				form.setMethod(FormPanel.METHOD_POST);
				form.setAction(URL);
				// form.add(infohidden);
				form.add(hiddenNewsItemID);
				form.submit();
				form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
					}
				});
			}
		}

		else if (event.getSource() instanceof Button) {
						
			VerticalPanel panel = new VerticalPanel();
			txtnewsInformation.setName("newsInformation");
			txtNewsItemTags.setName("txtNewsItemTags");
			String newsSource = newsItem.getNewsSource();
			if(newsSource.equals("")||newsSource==null)
				newsSource="-";
			String imgUrl = newsItem.getImageUrl();
			if(imgUrl.equals("")||imgUrl==null)
				imgUrl="-";
			newsItemInformation = String.valueOf(newsItem.getNewsId())+"$|$"+newsItem.getNewsTitle()+"$|$"+newsItem.getAbstractNews()+"$|$"+
					newsSource+"$|$"+newsItem.getUrl()+"$|$"+newsItem.getNewsDate()+"$|$"+imgUrl;
			txtnewsInformation.setValue(newsItemInformation);
			ArrayList list = new ArrayList();
			list = newsItem.getAssociatedTagList();
			newsItemTags="";
			for(int i=0;i<list.size();i++){
				TagItem item = new TagItem();
				item = (TagItem) list.get(i);
				newsItemTags = newsItemTags + ";" + item.getTagName();
			}
			
			txtNewsItemTags.setValue(newsItemTags);
			String url =  GWT.getModuleBaseURL()+"accessLockedNewsItem ";
			FormPanel form = new FormPanel();
			mainBasePanel.add(form);
			form.addSubmitCompleteHandler(this);
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
			form.setAction(url);
			panel.add(txtNewsItemTags);
			panel.add(txtnewsInformation);
			form.add(panel);
			form.submit();
			
		}

	}

	private void showAccessPopup() {
		String message = " The item clicked by you is marked as locked by admin. Please click on button below to request permission.";
		String btnText = "Request access";
		ntp = new NotificationPopup(message, btnText,
				NotificationType.CUSTOMNOTIFICATION, this);
		ntp.createUI();
		ntp.setPopupPosition(titlePanel.getAbsoluteLeft() + 750,
				titlePanel.getAbsoluteTop() + 250);
		ntp.setStylePrimaryName("searchPopup");
		ntp.show();
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		if (event.getSource() instanceof HTML) {
			HTML label = (HTML) event.getSource();
			label.removeStyleName("lblUrlHover");
		}
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		if (event.getSource() instanceof HTML) {
			HTML label = (HTML) event.getSource();
			label.addStyleName("lblUrlHover");
		}
	}

	public NewsStatistics getNewsStatistics() {
		return newsStatistics;
	}

	public void setNewsStatistics(NewsStatistics newsStatistics) {
		this.newsStatistics = newsStatistics;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		ntp.clearUI();
		ntp.setNotificationType(NotificationType.NOTIFICATION);
		ntp.setNotificationMessage("Your request to access the locked item has been sent to admin. Please expect response in your email. Thank you..");
		ntp.createUI();

	}
}