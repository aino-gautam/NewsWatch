package com.newscenter.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.NewsEvent;
import com.newscenter.client.events.TagEvent;
import com.newscenter.client.events.TagEventListener;
import com.newscenter.client.news.NewsCache;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.news.NewsProviderServiceAsync;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.ItemProviderServiceAsync;
import com.newscenter.client.tags.TagItem;

/**
 * Manages tag items and synchronization of tag selections / de-selections with a session category map
 * @author nairutee
 *
 */
public class ItemStore implements AsyncCallback, TagEventListener{
	
	private CategoryMap map = null;
	private static ItemStore globalItemStore;
	private NewsCache currentNewsCache;
	private PageCriteria criteria;
	private int newsmode;
	private NewsCenterMain ncMainRef;
	
	public NewsCenterMain getNcMainRef() {
		return ncMainRef;
	}

	public void setNcMainRef(NewsCenterMain ncMainRef) {
		this.ncMainRef = ncMainRef;
	}

	public ItemStore(){
		
	}
	
	public static ItemStore getInstance(){
		if(globalItemStore == null)
			globalItemStore = GWT.create(ItemStore.class);
		
		return globalItemStore;
	}
	
	/**
	 * Gets a list of categories from the category map and indexes them to facilitate the display of
	 * categories in the tag presenter
	 * @return
	 */
	public ArrayList<CategoryItem> getVisibleCategories(){
		int ind = 1;
		ArrayList<CategoryItem> categoryList = new ArrayList<CategoryItem>();
		if(getMap() == null){
			initialize();
			
		}
		if(getMap().size() != 0){
			for(Object ob : getMap().keySet()){
				CategoryItem citem = (CategoryItem)getMap().get(ob);
				citem.setIndexId(ind);
				categoryList.add(citem);
				ind++;
			}
		}
		return categoryList;
	}
	
	/**
	 * initializes the ItemStore object by registering it as a handler for tag events and makes a server call
	 * to fetch a category map containing the tag selections for the current logged in user.
	 */
	public void initialize(){
		AppEventManager eventmanager = AppEventManager.getInstance();
		eventmanager.addTagEventListener(this);
		ItemProviderServiceAsync itemprovider = ServiceUtils.getItemProviderServiceAsync();
		itemprovider.getUserSelectionCategories(this);
	}

	public void onFailure(Throwable arg0) {
	
	}

	/**
	 * Overridden method for AsyncCallbackHandler
	 */
	public void onSuccess(Object arg0) {
		if(arg0 instanceof CategoryMap){
			map = (CategoryMap)arg0;
			ArrayList list = map.getSelectedTags();
			CategoryMap cmap = new CategoryMap();
			for(Object obj : map.keySet()){
				CategoryItem catitem = (CategoryItem)map.get(obj);
				HashMap hashmap = sortItemMap(catitem.getItemMap());
				hashmap = sortItemMap(hashmap); 
				catitem.setItemMap(hashmap);
				cmap.put(obj, catitem);
			}
			setMap(cmap);
			getNcMainRef().initialize();
			TagEvent evt = new TagEvent(this,TagEvent.TAGSAVAILABLE,getMap());
			AppEventManager.getInstance().fireEvent(evt);
		}
		
		else if(arg0 instanceof NewsItemList){
			NewsItemList list = (NewsItemList)arg0;
			if(list.isTagNews()){
				list.sortList();
				NewsEvent evt = new NewsEvent(this,NewsEvent.TAGNEWSARRIVED,list);
				AppEventManager.getInstance().fireEvent(evt);
			}
			else{
				NewsStore.getInstance().initialize(list);
			}

		}
	}
	
	/**
	 * Sorts the tag items of each category of a categoryMap in alphabetical order 
	 * @param hashmap
	 * @return
	 */
	public HashMap sortItemMap(HashMap hashmap){
		HashMap map = new LinkedHashMap();
		List yourMapKeys = new ArrayList(hashmap.keySet());
		List yourMapValues = new ArrayList(hashmap.values());
		
		final Comparator<TagItem> ALPHABETICAL_ORDER = new Comparator<TagItem>(){

			public int compare(TagItem t1, TagItem t2) {
				
				return t1.getTagName().compareTo(t2.getTagName());
			}
		};
		
		TreeSet sortedSet = new TreeSet(ALPHABETICAL_ORDER);
		Iterator iter = yourMapValues.iterator();
		while(iter.hasNext()){
			sortedSet.add(iter.next());
		}
		Object[] sortedArray = sortedSet.toArray();
		int size = sortedArray.length;

		for (int i=0; i<size; i++) {
		   map.put(yourMapKeys.get(yourMapValues.indexOf(sortedArray[i])),sortedArray[i]);
		}
		return map;
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
	 * This method is called once the categoryMap has been loaded and is kept in the session object. 
	 */
	public void updateSessionCategoryMap(){
		
		ItemProviderServiceAsync itemprovider = ServiceUtils.getItemProviderServiceAsync();
		itemprovider.updateSessionCategoryMap(getMap(), getCriteria(),getNewsmode(), this);
	}
	
	/**
	 * This method is called when the user clicks on Clear All tags. It is used to remove all user selections.
	 */
	public void clearSelection(){
		ItemProviderServiceAsync itemprovider = ServiceUtils.getItemProviderServiceAsync();
		itemprovider.refreshUserSelection(getMap(), this);
	}
	
	/**
	 * This method is called when user closes the window or logs out. It saves the user tag selection into the db
	 */
	public void saveUserSelection(){
		ItemProviderServiceAsync itemprovider = ServiceUtils.getItemProviderServiceAsync();
		itemprovider.updateUserItemSelection(getMap(), this);
	}

	/**
	 * Associates the tags for every news item
	 * @param newscache
	 */
	public void associateTagNews(NewsCache newscache){
		NewsCache tempcache = new NewsCache();
		if(getCurrentNewsCache() == null){
			setCurrentNewsCache(newscache);
			tempcache = newscache;
		}
		else{
			NewsCache cache = getCurrentNewsCache();
			
			Iterator it = newscache.iterator();
			while(it.hasNext()){
				boolean flag = false;
				NewsItems news = (NewsItems)it.next();
				
				Iterator itt = cache.iterator();
				while(itt.hasNext()){
					NewsItems newsitem = (NewsItems)itt.next();
					if(news.getNewsId() == newsitem.getNewsId()){
						flag = true;
					}
				}
				if(flag == false){
					tempcache.add(news);
					cache.add(news);
				}
			}
			setCurrentNewsCache(cache);
		}
		
		if(getMap().size()!=0){
			if(tempcache != null){
			Iterator iter = tempcache.iterator();
			ArrayList<TagItem> newtaglist;
			while(iter.hasNext()){
				newtaglist = new ArrayList<TagItem>();
				NewsItems newsitem = (NewsItems)iter.next();
				ArrayList list = newsitem.getAssociatedTagList();
				Iterator it = list.iterator();
				while(it.hasNext()){
					TagItem tag = (TagItem)it.next();
					for(Object obj : getMap().keySet()){
						HashMap hashmap = ((CategoryItem)getMap().get(obj)).getItemMap();
						if(hashmap.containsKey(tag.getTagId())){
							TagItem tagitem = (TagItem)hashmap.get(tag.getTagId());
							tagitem.addNewsforTag(newsitem);
							newtaglist.add(tagitem);
						}
					}
				}
				newsitem.setAssociatedTagList(newtaglist);
			}
			setCurrentNewsCache(newscache);
		   }
			else{
				setCurrentNewsCache(getCurrentNewsCache());
			}
		}
	}
	
	public boolean onEvent(TagEvent evt) {
		int evttype = evt.getEventType();
			switch(evttype){
				case (TagEvent.TAGSELECTED):
				{	
					ItemStore.getInstance().updateSessionCategoryMap();
					break;
				}
				case (TagEvent.TAGDESELECTED):
				{
					ItemStore.getInstance().updateSessionCategoryMap();
					break;
				}
				case (TagEvent.CATEGORYITEMSELECTED):
				{
					ItemStore.getInstance().updateSessionCategoryMap();
					break;
				}
				case (TagEvent.CATEGORYITEMDESELECTED):
				{
					ItemStore.getInstance().updateSessionCategoryMap();
					break;
				}
			}
		return false;
	}

	public CategoryMap getMap() {
		return map;
	}

	public void setMap(CategoryMap map) {
		this.map = map;
	}

	public NewsCache getCurrentNewsCache() {
		return currentNewsCache;
	}

	public void setCurrentNewsCache(NewsCache currentNewsCache) {
		this.currentNewsCache = currentNewsCache;
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


}
