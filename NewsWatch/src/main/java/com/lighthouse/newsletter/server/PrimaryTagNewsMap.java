package com.lighthouse.newsletter.server;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

public class PrimaryTagNewsMap extends LinkedHashMap<TagItem, List<NewsItems>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TagItem primaryTag;
	private NewsItemList newsItemList;
	
	
	public TagItem getPrimaryTag() {
		return primaryTag;
	}
	public void setPrimaryTag(TagItem primaryTag) {
		this.primaryTag = primaryTag;
	}
	public NewsItemList getNewsItemList() {
		return newsItemList;
	}
	public void setNewsItemList(NewsItemList newsItemList) {
		this.newsItemList = newsItemList;
	}
	
	public void addPrimaryTagNewsMap(TagItem primaryTag){
		try {
			newsItemList = new NewsItemList();
			this.primaryTag = primaryTag;
			if (!isPrimaryTagExist(primaryTag)) {
				put(primaryTag, newsItemList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private boolean isPrimaryTagExist(TagItem primaryTag) {
		try{
			for(TagItem tag:this.keySet()){
				if(tag.getTagName().equals(primaryTag.getTagName()))
						return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean addNewsItem(TagItem pTag,NewsItems news){
		try{
			
			for(TagItem tag:this.keySet()){
				if(tag.getTagName().equals(pTag.getTagName())){
						get(tag).add(news);
						return true;
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	
	
}
