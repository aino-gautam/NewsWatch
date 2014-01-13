package com.newscenter.client;

import java.util.ArrayList;
import java.util.Iterator;

//import com.common.client.LogoutPage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.NewsEvent;
import com.newscenter.client.news.NewsCache;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.news.NewsProviderServiceAsync;
import com.newscenter.client.tags.TagItem;
import com.newscenter.client.ui.MainNewsPresenter;

/**
 * Manages fetching of news items 
 * @author nairutee
 *
 */
public class NewsStore implements AsyncCallback {

	private static NewsStore globalNewsStore = new NewsStore();
	private NewsCache globalNewsCache = new NewsCache();
	private NewsItemList globalNewsItemList;
	private PopupPanel popupPanel;
	protected PopupPanel pop;
	protected Button okButton = new Button("OK");
	private PageCriteria criteria;
	private int newsmode;
	private int popupLeftPosition,popupTopPosition;
	private String userChoice;
	
	public NewsStore(){
		
	}
	
	/**
	 * used to refer to a single instance of NewsStore through out the application
	 * @return a static instance of NewsStore
	 */
	public static NewsStore getInstance(){
		if(globalNewsStore == null)
			globalNewsStore = GWT.create(NewsStore.class);
		
		return globalNewsStore;
	}
	
	/**
	 * Called by the ItemStore on receiving NewsItems based on user selections in the CategoryMap. If an earlier NewsItemList is present, the newly arrived
	 * list is merged with the existing one, the merged list is sorted again and set as the globalNewsList. A method 'assocaiteTags' is called on the Item
	 * Store to associate the news to tags in the categoryMap and then the NEWSARRIVED event is fired which is heard by the MainNewsPresenter. 
	 * @param list -  a NewsItemList of NewsItems. 
	 */
	public void initialize(NewsItemList list){
		if(list.isRedirect() == true)
		{
			/*LogoutPage logoutPage = new LogoutPage();
			logoutPage.redirectOnSessionClose();*/
		}
		list.sortList();
		setGlobalNewsItemList(list);
		NewsCache cache = new NewsCache();
		Iterator iter = getGlobalNewsItemList().iterator();
		while(iter.hasNext()){
			NewsItems news = (NewsItems)iter.next();
			cache.add(news);
		}
		setGlobalNewsCache(cache);
		//ItemStore.getInstance().associateTagNews(getGlobalNewsCache());
		NewsEvent evt = new NewsEvent(this,NewsEvent.NEWSARRIVED,getGlobalNewsItemList());
		AppEventManager.getInstance().fireEvent(evt);
	
	}
	
	/**
	 * This method is called whenever a tag is deselected by the user.Based on the tag deselected, the news items related to that tag are removed from
	 * the global news list and the NEWSDELETED event is fired which is heard by the MainNewsPresenter
	 * @param list - the list of news items which need to be deleted from the global news list.
	 */
	public void updateNewsList(ArrayList list){
		Iterator iter = list.iterator();
		ArrayList templist = new ArrayList();
		while(iter.hasNext()){
			boolean flag = false;
			NewsItems newsitem = (NewsItems)iter.next();
			NewsItemList newsitemlist = getGlobalNewsItemList();
			Iterator it = newsitemlist.iterator();
			while(it.hasNext()){
				NewsItems news = (NewsItems)it.next();
				if(newsitem.getNewsId() == news.getNewsId()){
					if(newsitem.getAssociatedTagList().size()<=1){
						templist.add(newsitem);
						flag = true;
					}
					else{
						ArrayList<TagItem> taglist = newsitem.getAssociatedTagList();
						Iterator itt = taglist.iterator();
						while(itt.hasNext()){
							TagItem tag = (TagItem)itt.next();
							if(tag.isSelected()){
								flag = false;
								break;
							}
							else{
								flag = true;
							}
						}
						if(flag == true)
							templist.add(newsitem);
					}		
				}
			}
		}
		Iterator itr = templist.iterator();
		NewsItemList newslist = getGlobalNewsItemList();
		NewsCache newscache = getGlobalNewsCache();
		while(itr.hasNext()){
			NewsItems news = (NewsItems)itr.next();
			newslist.remove(news);
			newscache.remove(news);
		}
		
		setGlobalNewsItemList(newslist);
		setGlobalNewsCache(newscache);
		if(getGlobalNewsItemList().size() != 0){
			NewsEvent evt = new NewsEvent(this,NewsEvent.NEWSDELETED,getGlobalNewsItemList());
			AppEventManager.getInstance().fireEvent(evt);
		}
		else{
			NewsEvent evt = new NewsEvent(this,NewsEvent.NEWSDELETED,null);
			AppEventManager.getInstance().fireEvent(evt);
		}
	}
	
	/**
	 * This method is called when the user selects any of the applied tags of a news. It fetches all the news related to the selected tag from the db
	 * @param tag - the TagItem for which more news have to be fetched.
	 */
	public void getNews(TagItem tag, PageCriteria criteria){
		//MainNewsPresenter.setLoadingMessage("Fetching more news...");
		NewsProviderServiceAsync newsprovider = ServiceUtils.getNewsProviderServiceAsync();
		newsprovider.getAllNewsforTag(tag, criteria, this);
	}
	
	/**
	 * fetches a page of newsItems when the filter criteria is OR
	 * @param criteria - PageCriteria
	 * @param newsmode - OR 
	 */
	public void getNewsPage(PageCriteria criteria, int newsmode){
		NewsProviderServiceAsync newsprovider = ServiceUtils.getNewsProviderServiceAsync();
		newsprovider.getPage(criteria, newsmode, this);
	}
	
	/**
	 * fetches a page of newsItems when the filter criteria is AND
	 * @param criteria - PageCriteria
	 * @param newsmode - AND
	 */
	public void getAndNewsPage(PageCriteria criteria, int newsmode){
		NewsProviderServiceAsync newsprovider = ServiceUtils.getNewsProviderServiceAsync();
		newsprovider.getAndPage(criteria, newsmode, this);
	}
	
	/**
	 * saves the newsletter preference - whether a user wants to subscribe to the newsletter / unsubscribe
	 * @param choice - subscribe / unsubscribe
	 * @param popup
	 */
	public void saveNewsletterPreference(String choice, PopupPanel popup){
		setPopupPanel(popup);
		setUserChoice(choice);
		NewsProviderServiceAsync newsprovider = ServiceUtils.getNewsProviderServiceAsync();
		newsprovider.saveNewsletterPreference(choice, this);
	}
	
	/**
	 * saves the news filter mode - And / Or
	 * @param choice
	 * @param newsmode
	 * @param popup
	 */
	public void saveNewsFilterModPreference(String choice, int newsmode, PopupPanel popup){
		setPopupPanel(popup);
		setNewsmode(newsmode);
		NewsProviderServiceAsync newsprovider = ServiceUtils.getNewsProviderServiceAsync();
		newsprovider.saveNewsFilterModePreference(choice, this);
	}
	public void onFailure(Throwable arg0) {

	}

	/**
	 * Overridden method for the AsyncCallback Handler
	 */
	public void onSuccess(Object arg0) {

		if(arg0 instanceof NewsItemList){
			NewsItemList list = (NewsItemList)arg0;
			list.sortList();
			if(list.isTagNews()){
				NewsEvent evt = new NewsEvent(this,NewsEvent.NEWSAVAILABLE,list);
				AppEventManager.getInstance().fireEvent(evt);
			}
			else if(list.isAndNews()){
				NewsEvent evt = new NewsEvent(this,NewsEvent.ANDNEWSAVAILABLE,list);
				AppEventManager.getInstance().fireEvent(evt);
			}
			else{
				NewsEvent evt = new NewsEvent(this,NewsEvent.PAGEAVAILABLE,list);
				AppEventManager.getInstance().fireEvent(evt);
			}
		}
		if(arg0 instanceof Boolean){
			Boolean bool = (Boolean) arg0;
			PopupPanel popup = getPopupPanel();
			pop = new PopupPanel();
			pop.clear();
			VerticalPanel vp = new VerticalPanel();
			Label lb = new Label();
			if(bool){
				if(getUserChoice().equalsIgnoreCase("unsubscribe"))
					lb.setText("You have now been unsubscribed to the newsletter");
				if(getUserChoice().equalsIgnoreCase("Daily"))
					lb.setText("You now subscribe to the daily newsletter");
				if(getUserChoice().equalsIgnoreCase("Weekly"))
					lb.setText("You now subscribe to the weekly newsletter");
			}
			else
				lb.setText("Your preference could not be saved.Please try again later.");
			lb.setStylePrimaryName("popupLabel");
			vp.setSpacing(5);
			vp.add(lb);
			vp.add(okButton);
			okButton.addStyleName("popupLabel");
			okButton.setSize("40px", "25px");
			/*okButton.addClickListener(new ClickListener(){
				public void onClick(Widget arg0) {
					hidePopup();
				}
				
			});*/
			okButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					hidePopup();
					
				}
			});
			pop.add(vp);
			int left = popup.getPopupLeft();
			int top = popup.getPopupTop();
			popup.hide();
			//pop.center();
			pop.setPopupPosition(left, top);
			pop.setStylePrimaryName("newsletterPopup");
			pop.setAnimationEnabled(true);
			pop.setTitle("Click outside to close");
			pop.show();
		}
		if(arg0 instanceof Integer){
			final int bool = (Integer)arg0;
			PopupPanel popup = getPopupPanel();
			pop = new PopupPanel();
			pop.clear();
			VerticalPanel vp = new VerticalPanel();
			Label lb = new Label();
			if(bool == 1)
				lb.setText("Your preference has been saved. Please be aware that you have chosen \n the 'OR' criteria for news filtering");
			else if(bool == 2)
				lb.setText("You preference has been saved. Please be aware that you have chosen \n the 'AND' criteria for news filtering");
			else
				lb.setText("Your preference could not be saved.Please try again later.");
			lb.setStylePrimaryName("popupLabel");
			vp.setSpacing(5);
			vp.add(lb);
			vp.add(okButton);
			okButton.addStyleName("popupLabel");
			okButton.setSize("40px", "25px");
			/*okButton.addClickListener(new ClickListener(){

				public void onClick(Widget arg0) {
					hidePopup();
					MainNewsPresenter.setLoadingMessage("Refreshing News...");
					if(bool == 1)
						ItemStore.getInstance().updateSessionCategoryMap();//getNewsPage(getCriteria(), getNewsmode());
					else if(bool == 2)
						//getNewsPage(getCriteria(), getNewsmode());
						getAndNewsPage(getCriteria(), getNewsmode());
				}
				
			});*/
			
			okButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					
					hidePopup();
					MainNewsPresenter.setLoadingMessage("Refreshing News...");
					if(bool == 1)
						ItemStore.getInstance().updateSessionCategoryMap();//getNewsPage(getCriteria(), getNewsmode());
					else if(bool == 2)
						//getNewsPage(getCriteria(), getNewsmode());
						getAndNewsPage(getCriteria(), getNewsmode());
					
				}
			});
			pop.add(vp);
			int left = popup.getPopupLeft();
			int top = popup.getPopupTop();
			popup.hide();
			//pop.center();
			pop.setPopupPosition(left, top);
			pop.setStylePrimaryName("newsletterPopup");
			pop.setAnimationEnabled(true);
			pop.setTitle("Click outside to close");
			pop.show();
		}
	}

	public void onClick(Widget arg0){
		hidePopup();
		NewsProviderServiceAsync newsprovider = ServiceUtils.getNewsProviderServiceAsync();
		newsprovider.getPage(getCriteria(), getNewsmode(), this);
	}
	
	public void hidePopup(){
		pop.hide();
	}
	
	public NewsCache getGlobalNewsCache() {
		return globalNewsCache;
	}

	public void setGlobalNewsCache(NewsCache globalNewsCache) {
		this.globalNewsCache = globalNewsCache;
	}

	public NewsItemList getGlobalNewsItemList() {
		return globalNewsItemList;
	}

	public void setGlobalNewsItemList(NewsItemList newsItemList) {
		this.globalNewsItemList = newsItemList;
	}


	public PopupPanel getPopupPanel() {
		return popupPanel;
	}

	public void setPopupPanel(PopupPanel popupPanel) {
		this.popupPanel = popupPanel;
	}

	public PageCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(PageCriteria criteria) {
		this.criteria = criteria;
	}

	public int getNewsmode() {
		return newsmode;
	}

	public void setNewsmode(int newsmode) {
		this.newsmode = newsmode;
	}

	public int getPopupLeftPosition() {
		return popupLeftPosition;
	}

	public void setPopupLeftPosition(int popupLeftPosition) {
		this.popupLeftPosition = popupLeftPosition;
	}

	public int getPopupTopPosition() {
		return popupTopPosition;
	}

	public void setPopupTopPosition(int popupTopPosition) {
		this.popupTopPosition = popupTopPosition;
	}

	public String getUserChoice() {
		return userChoice;
	}

	public void setUserChoice(String userChoice) {
		this.userChoice = userChoice;
	}
}
