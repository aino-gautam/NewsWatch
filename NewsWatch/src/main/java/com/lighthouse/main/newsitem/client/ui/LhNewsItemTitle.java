package com.lighthouse.main.newsitem.client.ui;

import java.util.ArrayList;

import com.appUtils.client.NotificationPopup;
import com.appUtils.client.NotificationPopup.NotificationType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

public class LhNewsItemTitle extends Composite implements ClickHandler,SubmitCompleteHandler{
	
	protected NewsItems newsItem;
	protected HorizontalPanel titlePanel;
	private Hidden txtnewsInformation = new Hidden();
	private String newsItemInformation = "";
	private String newsItemTags = "";
	private Hidden txtNewsItemTags = new Hidden();
	int clickedTop;
    int clickedLeft;
    NotificationPopup ntp;
    
	public LhNewsItemTitle(NewsItems newsItem) {
		this.newsItem = newsItem;
		titlePanel = new HorizontalPanel();
		//createUI();
		initWidget(titlePanel);
		
	}

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
	}

	@Override
	public void onClick(final ClickEvent event) {
		if(event.getSource() instanceof HTML){
			clickedTop = event.getY()+titlePanel.getAbsoluteTop();
		    clickedLeft = event.getX();
			if(newsItem.getIsLocked()==1)
				showAccessPopup();
			else{
				Hidden hiddenNewsItemID = new Hidden();
				hiddenNewsItemID.setName("newsitemid");
				hiddenNewsItemID.setDefaultValue(String.valueOf(newsItem.getNewsId()));
				String url = GWT.getHostPageBaseURL()
						+ "com.lighthouse.login.lhlogin/itemView"; // to itemViewServlet
				String URL = GWT.getModuleBaseURL() + "itemView";
				//String url = GWT.getHostPageBaseURL() + "com.login.login/itemView";
				// String url =
				// GWT.getHostPageBaseURL()+"com.login.login/emailitemView";
				FormPanel form = new FormPanel();
				titlePanel.add(form);
				form.setEncoding(FormPanel.ENCODING_MULTIPART);
				form.setMethod(FormPanel.METHOD_POST);
				form.setAction(URL);
				form.add(hiddenNewsItemID);
				form.submit();
			}
		
		}else if(event.getSource() instanceof Image)
			showAccessPopup();
		else if(event.getSource() instanceof Button){
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
			String url =  GWT.getHostPageBaseURL()+"com.lighthouse.main.lhmain/accessLockedNewsItem";
			FormPanel form = new FormPanel();
			titlePanel.add(form);
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

	private void showAccessPopup(){
		String message =" The item clicked by you is marked as locked by admin. Please click on button below to request permission.";
		String btnText = "Request access";
		ntp = new NotificationPopup(message, btnText, NotificationType.CUSTOMNOTIFICATION, this);
		ntp.createUI();
		ntp.setPopupPosition(titlePanel.getAbsoluteLeft()+50, titlePanel.getAbsoluteTop());
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
}
