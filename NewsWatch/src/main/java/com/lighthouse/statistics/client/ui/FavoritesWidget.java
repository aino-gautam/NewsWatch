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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.statistics.client.service.StatisticsService;
import com.lighthouse.statistics.client.service.StatisticsServiceAsync;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

public class FavoritesWidget extends Composite implements ClickHandler,MouseOverHandler, MouseOutHandler, SubmitCompleteHandler  {

	private VerticalPanel vpMainBasePanel = new VerticalPanel();
	private VerticalPanel vpHolder = new VerticalPanel();
	private Group group;
	private LhUserPermission lhUserPermission;
	private HashMap<HTML, NewsItems> mapOfLinkVsNewsitem = new HashMap<HTML, NewsItems>();
	private HorizontalPanel titlePanel;
	NotificationPopup ntp;
	private Hidden txtnewsInformation = new Hidden();
	private String newsItemInformation = "";
	private String newsItemTags = "";
	private Hidden txtNewsItemTags = new Hidden();
	private NewsItems newsItem;
	
	public FavoritesWidget(Group group, LhUserPermission lhUserPermission) throws FeatureNotSupportedException {
		if (lhUserPermission.isFavoriteGroupPermitted() == 1) {
			this.group = group;
			this.lhUserPermission = lhUserPermission;
			//getFavoriteItems();
			initWidget(vpMainBasePanel);
			vpMainBasePanel.setWidth("100%");
		} else
			throw new FeatureNotSupportedException("Favorite group not supported");
	}
	
	private void createUI(){
		titlePanel = new HorizontalPanel();
		HorizontalPanel hpHeader = new HorizontalPanel();
		Label headerLabel = new Label(group.getGroupName());
		headerLabel.setStylePrimaryName("ReportsHeaderLabel");
		hpHeader.add(headerLabel);
		hpHeader.setCellHorizontalAlignment(headerLabel, HorizontalPanel.ALIGN_LEFT);
		hpHeader.setWidth("100%");
		hpHeader.setStylePrimaryName("reportsHeaderPanel");
		
		vpMainBasePanel.add(hpHeader);
		vpMainBasePanel.add(vpHolder);

		vpMainBasePanel.setStylePrimaryName("favGroupBg");
		vpMainBasePanel.setCellHorizontalAlignment(hpHeader,HorizontalPanel.ALIGN_LEFT);

		vpMainBasePanel.setCellHorizontalAlignment(vpHolder,HorizontalPanel.ALIGN_LEFT);

		vpMainBasePanel.setWidth("100%");
	}
	
	private void populateNews(List<NewsItems> newsList){
		if(newsList.size() > 0 ){
			FlexTable flex = new FlexTable();
			int row = 1;
			for (NewsItems newsItem : newsList) {
				flex.setWidget(row, 0, createNewsItemTitle(newsItem));
				row += 2;
			}
			flex.setStylePrimaryName("newsStatisticsFlex");
			vpHolder.add(flex);
		}else{
			Label noReports = new Label("No favorite news items");
			noReports.setStylePrimaryName("reportLink");
			vpHolder.add(noReports);
		}
	}
	
	public void getFavoriteItems(boolean isRefreshed){
		StatisticsServiceAsync service = GWT.create(StatisticsService.class);
		service.getFavoriteItems(group, isRefreshed, new AsyncCallback<List<NewsItems>>() {
			
			@Override
			public void onSuccess(List<NewsItems> result) {
				createUI();
				if (result == null) {
					Label noReports = new Label("No favorite news items");
					noReports.setStylePrimaryName("reportLink");
					vpHolder.add(noReports);
				} else
					populateNews(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
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
				FormPanel form = new FormPanel();
				vpMainBasePanel.add(form);
				form.setEncoding(FormPanel.ENCODING_MULTIPART);
				form.setMethod(FormPanel.METHOD_POST);
				form.setAction(URL);
				form.add(hiddenNewsItemID);
				form.submit();
				form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
					}
				});
			}
		}else if (event.getSource() instanceof Button) {
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
			vpMainBasePanel.add(form);
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
	public void onSubmitComplete(SubmitCompleteEvent event) {
		ntp.clearUI();
		ntp.setNotificationType(NotificationType.NOTIFICATION);
		ntp.setNotificationMessage("Your request to access the locked item has been sent to admin. Please expect response in your email. Thank you..");
		ntp.createUI();

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
}
