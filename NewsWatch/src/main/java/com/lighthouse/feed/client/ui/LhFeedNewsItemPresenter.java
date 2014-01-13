package com.lighthouse.feed.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.Label;
import com.lighthouse.main.newsitem.client.ui.LhNewsItemPresenter;
import com.lighthouse.main.newsitem.client.ui.LhNewsItemWidget;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
/**
 * Displaying the all news items
 * @author kiran@ensarm.com
 *
 */
public class LhFeedNewsItemPresenter extends LhNewsItemPresenter{
	
	//private HashMap<Integer, HashMap<Integer, LhFeedNewsItemWidget>> feedWidgetMap;
	
	public LhFeedNewsItemPresenter() {
		super();
	}
    
	public void populateNewsItems(ArrayList newslist) {
		clear();
		NewsItemList newsItemList = (NewsItemList) newslist;
		if(newsItemList.size() == 0){
			Label noItemsLabel = new Label("There are no news items to review.");
			noItemsLabel.setStylePrimaryName("labelfont");
			setWidget(0, 0, noItemsLabel);
			setWidth("100%");
		}else{
			Iterator iter = newsItemList.iterator();
			int row = 0;
			int col = 0;
			newsWidgetMap = new HashMap<Integer, HashMap<Integer, LhNewsItemWidget>>();
			//feedWidgetMap = new HashMap<Integer, HashMap<Integer, LhFeedNewsItemWidget>>();
			 
			while (iter.hasNext()) {
				//HashMap<Integer, LhFeedNewsItemWidget> rowFeedWidgetMap = new HashMap<Integer, LhFeedNewsItemWidget>();
				HashMap<Integer, LhNewsItemWidget> rowNewsWidgetMap = new HashMap<Integer, LhNewsItemWidget>();
				
				NewsItems newsitem = (NewsItems) iter.next();
				LhFeedNewsItemWidget lhNewsItemWidget = new LhFeedNewsItemWidget(newsitem, this);
				lhNewsItemWidget.createNewsItemWidgetUI();
				setWidget(row, col, lhNewsItemWidget);
				rowNewsWidgetMap.put(row, lhNewsItemWidget);
				newsWidgetMap.put(newsitem.getNewsId(), rowNewsWidgetMap);
				row++;
			}
		}
	}
	
	/**
	 * refreshes the widget with the updated news item
	 * @param newsitem
	 */
	@Override
	public void refreshWidget(NewsItems newsitem ){
		HashMap<Integer, LhNewsItemWidget> rowNewsWidgetMap =  newsWidgetMap.get(newsitem.getNewsId());
		for(int row : rowNewsWidgetMap.keySet()){
			LhFeedNewsItemWidget lhNewsItemWidget = (LhFeedNewsItemWidget)rowNewsWidgetMap.get(row);
			lhNewsItemWidget.setNewsItem(newsitem);
			lhNewsItemWidget.createNewsItemWidgetUI();
			setWidget(row, 0, lhNewsItemWidget);
		}
	}
}
