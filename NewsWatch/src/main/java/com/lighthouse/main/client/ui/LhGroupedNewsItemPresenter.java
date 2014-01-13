package com.lighthouse.main.client.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.group.client.GroupItemStore;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.newsitem.client.ui.LhNewsItemPresenter;
import com.lighthouse.main.newsitem.client.ui.LhNewsItemWidget;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

/**
 * This class is for a headline/grouping of news,in the portal according to
 * primary tag
 * 
 * @author kiran@ensarm.com
 * 
 */
public class LhGroupedNewsItemPresenter extends LhNewsItemPresenter implements
		ClickHandler {

	private Image image = new Image("images/cross.png");
	private Label label = new Label();
	private boolean isTagNews = false;
	private FlexTable innerFlex;
	private HashMap<Integer, TagItem> primaryTagMap = new HashMap<Integer, TagItem>();
	private HashMap<TagItem, FlexTable> innerFlexMap = new HashMap<TagItem, FlexTable>();
	private HashMap<TagItem, HashMap<Integer, HashMap<Integer, LhNewsItemWidget>>> primaryTagNewsWidgetMap = new HashMap<TagItem, HashMap<Integer, HashMap<Integer, LhNewsItemWidget>>>();
	
	public LhGroupedNewsItemPresenter(LhUser lhUser) {
		super(lhUser);
	}

	@Override
	public void populateNewsItems(ArrayList newslist) {
		Iterator<HashMap<TagItem, ArrayList>> iterator = newslist.iterator();
		ArrayList<NewsItems> newslist1;
		while (iterator.hasNext()) {
			int row = 0;
			HashMap<TagItem, ArrayList> hashMap = iterator.next();

			if (isTagNews) {
				for (TagItem tag : hashMap.keySet()) {
					newslist1 = (ArrayList<NewsItems>) hashMap.get(tag);
					System.out.println("-----------------***" + newslist1);
					setWidget(row, 0, createGroupedNewsForParticularPt(tag, newslist1));
					getFlexCellFormatter().setWidth(row, 0, "100%");
					row += 2;
				}
			} else {
				HashMap<TagItem, ArrayList> hashMapSorted = sortHashMap(hashMap);
				for (TagItem tag : hashMapSorted.keySet()) {
					newslist1 = (ArrayList<NewsItems>) hashMap.get(tag);
					System.out.println("-----------------***" + newslist1);
					setWidget(row, 0, createGroupedNews(tag, newslist1));
					getFlexCellFormatter().setWidth(row, 0, "100%");
					row += 2;
				}
			}
		}
	}

	private Widget createGroupedNewsForParticularPt(TagItem primaryTag, ArrayList<NewsItems> newslist1) {

		VerticalPanel vpanel = new VerticalPanel();
		innerFlex = new FlexTable();
		HorizontalPanel horizontalPanel = new HorizontalPanel();

		if (!newslist1.isEmpty()) {
			PrimaryCategoryLabel primaryCategoryLabel = new PrimaryCategoryLabel(
					primaryTag, lhUser);
			primaryCategoryLabel.setWidth("98%");
			horizontalPanel.add(primaryCategoryLabel);
			horizontalPanel.setCellWidth(primaryCategoryLabel, "98%");

			image.addClickHandler(this);

			horizontalPanel.add(image);
			horizontalPanel.setCellWidth(image, "2%");
			image.setStylePrimaryName("primaryTagLabel");
			horizontalPanel.setCellVerticalAlignment(primaryCategoryLabel,
					HasVerticalAlignment.ALIGN_MIDDLE);
			horizontalPanel.setStylePrimaryName("primaryTagPanel");
			horizontalPanel.setWidth("100%");

			vpanel.add(horizontalPanel);

		
			int row = 0;
			int col = 0;
			newsWidgetMap = new HashMap<Integer, HashMap<Integer, LhNewsItemWidget>>();
			
			// iterate the news list
			Iterator iter = newslist1.iterator();
			while (iter.hasNext()) {
				HashMap<Integer, LhNewsItemWidget> rowNewsWidgetMap = new HashMap<Integer, LhNewsItemWidget>();
				
				NewsItems newsitem = (NewsItems) iter.next();
				LhNewsItemWidget lhNewsItemWidget = new LhNewsItemWidget(newsitem, lhUser, this);
				lhNewsItemWidget.createNewsItemWidgetUI();
				innerFlex.setWidget(row, col, lhNewsItemWidget);
				//vpanel.add(lhNewsItemWidget);
				rowNewsWidgetMap.put(row, lhNewsItemWidget);
				newsWidgetMap.put(newsitem.getNewsId(), rowNewsWidgetMap);
				
				row++;
			}
			vpanel.add(innerFlex);
		}
		vpanel.setWidth("100%");
		return vpanel;
	}

	private HashMap<TagItem, ArrayList> sortHashMap(
			HashMap<TagItem, ArrayList> hashMap) {
		HashMap<TagItem, ArrayList> hashMapSorted = new LinkedHashMap<TagItem, ArrayList>();
		HashMap map = new LinkedHashMap();
		List mapKeys = new ArrayList(hashMap.keySet());
		List mapValues = new ArrayList(hashMap.values());

		final Comparator<TagItem> ALPHABETICAL_ORDER = new Comparator<TagItem>() {

			public int compare(TagItem t1, TagItem t2) {

				return t1.getTagName().compareTo(t2.getTagName());
			}
		};

		TreeSet sortedSet = new TreeSet(ALPHABETICAL_ORDER);
		Iterator iter = mapKeys.iterator();
		while (iter.hasNext()) {
			sortedSet.add(iter.next());
		}
		Object[] sortedArray = sortedSet.toArray();
		int size = sortedArray.length;

		for (int i = 0; i < size; i++) {
			map.put(mapKeys.get(mapKeys.indexOf(sortedArray[i])),
					sortedArray[i]);

			ArrayList<NewsItems> newslist = new ArrayList<NewsItems>();
			for (Object tag : map.keySet()) {
				newslist = (ArrayList<NewsItems>) hashMap.get(tag);
				hashMapSorted.put((TagItem) tag, newslist);
			}

		}
		return hashMapSorted;

	}

	private VerticalPanel createGroupedNews(TagItem primaryTag,
			ArrayList newslist1) {

		VerticalPanel vpanel = new VerticalPanel();
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		innerFlex = new FlexTable();
		
		if (!newslist1.isEmpty()) {
			PrimaryCategoryLabel primaryCategoryLabel = new PrimaryCategoryLabel(primaryTag, lhUser);
			primaryCategoryLabel.setWidth("100%");
			horizontalPanel.add(primaryCategoryLabel);

			horizontalPanel.setCellVerticalAlignment(primaryCategoryLabel,
					HasVerticalAlignment.ALIGN_MIDDLE);
			horizontalPanel.setStylePrimaryName("primaryTagPanel");
			horizontalPanel.setWidth("100%");

			vpanel.add(horizontalPanel);

			int row = 0;
			int col = 0;
		//	primaryTagMap = new HashMap<Integer, TagItem>();
		//	innerFlexMap = new HashMap<TagItem, FlexTable>();
		//	primaryTagNewsWidgetMap = new HashMap<TagItem, HashMap<Integer, HashMap<Integer, LhNewsItemWidget>>>();
			newsWidgetMap = new HashMap<Integer, HashMap<Integer, LhNewsItemWidget>>();
			
			// iterate the news list
			Iterator iter = newslist1.iterator();
			while (iter.hasNext()) {
				HashMap<Integer, LhNewsItemWidget> rowNewsWidgetMap = new HashMap<Integer, LhNewsItemWidget>();
				
				NewsItems newsitem = (NewsItems) iter.next();
				LhNewsItemWidget lhNewsItemWidget = new LhNewsItemWidget(newsitem, lhUser, this);
				lhNewsItemWidget.createNewsItemWidgetUI();
				innerFlex.setWidget(row, col, lhNewsItemWidget);
				//vpanel.add(lhNewsItemWidget);
				rowNewsWidgetMap.put(row, lhNewsItemWidget);
				primaryTagMap.put(newsitem.getNewsId(), primaryTag);
				newsWidgetMap.put(newsitem.getNewsId(), rowNewsWidgetMap);
				
				row++;
			}
			innerFlexMap.put(primaryTag, innerFlex);
			primaryTagNewsWidgetMap.put(primaryTag, newsWidgetMap);
			vpanel.add(innerFlex);
		}
		vpanel.setWidth("100%");
		return vpanel;
	}

	/**
	 * refreshes the widget with the updated news item
	 * @param newsitem
	 */
	@Override
	public void refreshWidget(NewsItems newsitem ){
		if(isTagNews){
			HashMap<Integer, LhNewsItemWidget> rowFeedWidgetMap =  newsWidgetMap.get(newsitem.getNewsId());
			for(int row : rowFeedWidgetMap.keySet()){
				LhNewsItemWidget lhNewsItemWidget = rowFeedWidgetMap.get(row);
				lhNewsItemWidget.setNewsItem(newsitem);
				lhNewsItemWidget.createNewsItemWidgetUI();
				innerFlex.setWidget(row, 0, lhNewsItemWidget);
			}
		}else{
			TagItem primaryTag = primaryTagMap.get(newsitem.getNewsId());
			FlexTable flex = innerFlexMap.get(primaryTag);
			HashMap<Integer, HashMap<Integer, LhNewsItemWidget>> widgetMap = primaryTagNewsWidgetMap.get(primaryTag);
			HashMap<Integer, LhNewsItemWidget> rowFeedWidgetMap =  widgetMap.get(newsitem.getNewsId());
			
			for(int row : rowFeedWidgetMap.keySet()){
				LhNewsItemWidget lhNewsItemWidget = rowFeedWidgetMap.get(row);
				lhNewsItemWidget.setNewsItem(newsitem);
				lhNewsItemWidget.createNewsItemWidgetUI();
				flex.setWidget(row, 0, lhNewsItemWidget);
			}
			
		}
	}
	
	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		if (sender instanceof Image) {
			GroupItemStore.getInstance().updateSessionCategoryMap();
		}
	}
	
	public boolean isTagNews() {
		return isTagNews;
	}

	public void setTagNews(boolean isTagNews) {
		this.isTagNews = isTagNews;
	}

}
