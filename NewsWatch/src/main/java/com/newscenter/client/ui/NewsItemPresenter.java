package com.newscenter.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.ui.FlexTable;

import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;

public class NewsItemPresenter extends FlexTable {
	
	private NewsItemView newsitemview;
		
	public NewsItemPresenter(){ 
		setWidth("100%");
	}

	public void populateNewsItems(ArrayList newslist){
		clear();
		NewsItemList cache = (NewsItemList)newslist;
		Iterator iter = cache.iterator();
		int row = 0;
		int col = 0;
			
		while(iter.hasNext()){
				NewsItems newsitem = (NewsItems)iter.next();
				newsitemview = new NewsItemView(newsitem);
				newsitemview.createNewsView();
				setWidget(row, col, newsitemview);
				row++;
		}
		
	}
}
