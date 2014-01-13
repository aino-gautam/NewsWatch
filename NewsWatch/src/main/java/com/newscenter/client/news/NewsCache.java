package com.newscenter.client.news;

import java.util.Collections;
import java.util.Comparator;


public class NewsCache extends NewsItemList {
	
	public NewsCache(){
		
	}
	
	/**
	 * should discard the current instance of news cache and create a new one with updated news items
	 */
	
	public void refresh(){
		
	}
	
	/**
	 * should update the current news cache instance to get the updated news items
	 */
	public void update(){
		
	}
	
	/**
	 * Sorts the news items list received from the Item store according to the latest published news items
	 * @return an arraylist of news items sorted according to the date of publishing of the news items.
	 */
	public void sortList(){
		final Comparator<NewsItems> LATESTUPDATED_ORDER = new Comparator<NewsItems>(){

			public int compare(NewsItems n1, NewsItems n2) {
				
				return n2.getNewsDate().compareTo(n1.getNewsDate());
			}
		};
		
		Collections.sort(this,LATESTUPDATED_ORDER);
	}
}
	
