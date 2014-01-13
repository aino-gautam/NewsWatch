package com.lighthouse.main.newsitem.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.FlexTable;
import com.lighthouse.login.user.client.domain.LhUser;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;

/**
 * Represents a multiple news item widget
 * 
 * @author kiran@ensarm.com
 * 
 */
public class LhNewsItemPresenter extends FlexTable {

	//private LhNewsItemWidget lhNewsItemWidget;
	protected LhUser lhUser;
	protected HashMap<Integer, HashMap<Integer, LhNewsItemWidget>> newsWidgetMap;
	
	public LhNewsItemPresenter(){
		setWidth("100%");
	}
	
	public LhNewsItemPresenter(LhUser lhUser) {
		this.lhUser = lhUser;
		setWidth("100%");

	}

	/**
	 * populate the multiple news item on widget
	 * 
	 * @param newslist
	 */
	public void populateNewsItems(ArrayList newslist) {
		clear();
		NewsItemList cache = (NewsItemList) newslist;
		Iterator iter = cache.iterator();
		int row = 0;
		int col = 0;
		newsWidgetMap = new HashMap<Integer, HashMap<Integer, LhNewsItemWidget>>();
		
		while (iter.hasNext()) {
			HashMap<Integer, LhNewsItemWidget> rowNewsWidgetMap = new HashMap<Integer, LhNewsItemWidget>();
			
			NewsItems newsitem = (NewsItems) iter.next();
			LhNewsItemWidget lhNewsItemWidget = new LhNewsItemWidget(newsitem, lhUser, this);
			lhNewsItemWidget.createNewsItemWidgetUI();
			setWidget(row, col, lhNewsItemWidget);
			
			rowNewsWidgetMap.put(row, lhNewsItemWidget);
			newsWidgetMap.put(newsitem.getNewsId(), rowNewsWidgetMap);
			
			row++;
		}

	}

	/**
	 * refreshes the widget with the updated news item
	 * @param newsitem
	 */
	public void refreshWidget(NewsItems newsitem ){
		HashMap<Integer, LhNewsItemWidget> rowFeedWidgetMap =  newsWidgetMap.get(newsitem.getNewsId());
		for(int row : rowFeedWidgetMap.keySet()){
			LhNewsItemWidget lhNewsItemWidget = rowFeedWidgetMap.get(row);
			lhNewsItemWidget.setNewsItem(newsitem);
			lhNewsItemWidget.createNewsItemWidgetUI();
			setWidget(row, 0, lhNewsItemWidget);
		}
	}
	
	public LhUser getLhUser() {
		return lhUser;
	}

	public void setLhUser(LhUser lhUser) {
		this.lhUser = lhUser;
	}

}
