package com.newscenter.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.newscenter.client.ItemStore;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.events.*;

public class MainTagPresenter extends Composite implements TagEventListener,OpenHandler<DisclosurePanel>,CloseHandler<DisclosurePanel> {
	
	private HorizontalPanel basePanel = new HorizontalPanel();
	private VerticalPanel mainBasePanel = new VerticalPanel();
	private ItemTagLabel clearLbl = new ItemTagLabel();
	private HorizontalPanel tagDisclosureHeader = new HorizontalPanel();
	private DisclosurePanel tagDisclosure = new DisclosurePanel();
	private int displayedSnippetsNumber;
	private CategoryMap map = null;
	public static HashMap<CategoryItem, SnippetView> visibleSnippetMap = new HashMap<CategoryItem,SnippetView> ();
	private Image plusMinusImage = new Image("images/minus.gif");
	private Label lbSelectTags = new Label("Select Tags");
	private HorizontalPanel selectTagsPanel = new HorizontalPanel();
	private VerticalPanel vp;
	private int tagDisclosureWidth;
	private ArrayList categoryList;
	public HashMap<Integer, CategoryItem> allCategoriesMap = new HashMap<Integer,CategoryItem> ();
	
	
	public MainTagPresenter(){
		calculateSnippetNumber();
		lbSelectTags.setStylePrimaryName("headerLabels");
		plusMinusImage.setStylePrimaryName("headerImages");
		basePanel.setSpacing(0);
		selectTagsPanel.add(plusMinusImage);
		selectTagsPanel.add(lbSelectTags);
		
		tagDisclosureHeader.add(selectTagsPanel);
		tagDisclosureHeader.setStylePrimaryName("tagDisclosureHeader");
		tagDisclosureHeader.setWidth("100%");
		tagDisclosure.setHeader(tagDisclosureHeader);
		tagDisclosure.getHeader().setTitle("click to minimize");
		//AppEventManager.getInstance().addTagEventListener(this);
		tagDisclosure.setOpen(true);
		mainBasePanel.add(basePanel);
		clearLbl.setText("CLEAR ALL");
		clearLbl.setClicked(false);
		clearLbl.setStylePrimaryName("clearAllLabelStyle");
		clearLbl.setTitle("Clears all selections");
		mainBasePanel.add(clearLbl);
		mainBasePanel.setWidth("100%");
		basePanel.setWidth("100%");
		mainBasePanel.setCellHorizontalAlignment(clearLbl,HasHorizontalAlignment.ALIGN_RIGHT);
		tagDisclosure.setContent(mainBasePanel) ;
		//tagDisclosure.addEventHandler(this);
		tagDisclosure.addOpenHandler(this);
		tagDisclosure.addCloseHandler(this);
		tagDisclosure.setAnimationEnabled(true);
		initWidget(tagDisclosure);
	}

	public void setTagPresenterWidth(int width, int height){
		tagDisclosure.setPixelSize(width, height);
	}
	public void resetSize(int width, int height){
		tagDisclosure.setPixelSize(width, height);
		setTagDisclosureWidth(width);
		System.out.println("On resize tagpresenter width= " + width);
		if(width > 1400 ){
			refresh();
		}
	}
	private void calculateSnippetNumber() {
		if(Window.getClientWidth() > 1400){
			setDisplayedSnippetsNumber(5);
		}
		else{
			setDisplayedSnippetsNumber(4);
		}
	}

	public void initialize(){
		categoryList = ItemStore.getInstance().getVisibleCategories();
		Iterator itt = categoryList.iterator();
		int count = 0;
		int ind = 1;
		while(itt.hasNext()){
			CategoryItem citem = (CategoryItem)itt.next();
			if(citem.isSelected()){
				count++;
			}
			allCategoriesMap.put(ind, citem);
			ind++;
		}
		visibleSnippetMap.clear();
		basePanel.clear();
		SnippetView snip;
		int temp = getDisplayedSnippetsNumber();

		int size = categoryList.size();
		int indx= 0;
		Iterator iter = categoryList.iterator();
		while(iter.hasNext()){
			
			CategoryItem catitem = (CategoryItem)iter.next();
			if(indx < temp){
				snip = new SnippetView();
				catitem.setSelected(true); // means that the category is visible
				snip.setCategory(catitem);
				snip.createSnippet();
				visibleSnippetMap.put(catitem, snip);
				basePanel.add(snip);
				if(indx != temp-1){
					vp  = new VerticalPanel();
					vp.setHeight("240px");
					vp.setWidth("1px");
					vp.add(new Image("images/longsnippetseparator.gif"));
					vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
					basePanel.add(vp);
					basePanel.setCellHorizontalAlignment(vp, HasHorizontalAlignment.ALIGN_LEFT);
				}
				indx++;
			}
			else{
				break;
			}
		}
		if(!tagDisclosure.isOpen()){
			tagDisclosure.setOpen(true);
		}
	}
	
	public void refresh(){
		categoryList = ItemStore.getInstance().getVisibleCategories();
		Iterator itt = categoryList.iterator();
		int count = 0;
		int ind = 1;
		while(itt.hasNext()){
			CategoryItem citem = (CategoryItem)itt.next();
			if(citem.isSelected()){
				count++;
			}
			allCategoriesMap.put(ind, citem);
			ind++;
		}
		basePanel.clear();
		visibleSnippetMap.clear();
		calculateSnippetNumber();
		SnippetView snip;
		int temp = getDisplayedSnippetsNumber();

		int indx= 0;
		Iterator iter = categoryList.iterator();
		if(count>=temp){
		//if(count != 0){
			while(iter.hasNext()){
				CategoryItem catitem = (CategoryItem)iter.next();
				if(catitem.isSelected()){
					if(indx < temp){
						snip = new SnippetView();
						catitem.setSelected(true); // means that the category is visible
						catitem.setDirty(true);
						snip.setCategory(catitem);
						snip.createSnippet();
						visibleSnippetMap.put(catitem, snip);
						basePanel.add(snip);
						if(indx != temp-1){
							vp  = new VerticalPanel();
							vp.setHeight("240px");
							vp.setWidth("1px");
							vp.add(new Image("images/longsnippetseparator.gif"));
							vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
							basePanel.add(vp);
							basePanel.setCellHorizontalAlignment(vp, HasHorizontalAlignment.ALIGN_LEFT);
						}
						indx++;
					}
					else{
						break;
					}
				}
			}
		}
		else{
			while(iter.hasNext()){
				
				CategoryItem catitem = (CategoryItem)iter.next();
				if(indx < temp){
					snip = new SnippetView();
					catitem.setSelected(true); // means that the category is visible
					catitem.setDirty(true);
					snip.setCategory(catitem);
					snip.createSnippet();
					visibleSnippetMap.put(catitem, snip);
					basePanel.add(snip);
					if(indx != temp-1){
						vp  = new VerticalPanel();
						vp.setHeight("240px");
						vp.setWidth("1px");
						vp.add(new Image("images/longsnippetseparator.gif"));
						vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
						basePanel.add(vp);
						basePanel.setCellHorizontalAlignment(vp, HasHorizontalAlignment.ALIGN_LEFT);
					}
					indx++;
				}
				else{
					break;
				}
			}
		}
		if(!tagDisclosure.isOpen()){
			tagDisclosure.setOpen(true);
		}
	}
	

	public boolean onEvent(TagEvent evt){
		int evttype = evt.getEventType();
		
		switch(evttype){
		case (TagEvent.TAGSAVAILABLE):
		{
			map = (CategoryMap)evt.getEventData();
			refresh();
			//initialize();
			ItemStore.getInstance().updateSessionCategoryMap();
			return true;
		}	
		case (TagEvent.DOWNCATEGORYCHANGED):
		{
			if(categoryList.size() > getDisplayedSnippetsNumber()){
			CategoryItem item = (CategoryItem)evt.getEventData();
			SnippetView snippet = (SnippetView)evt.getSource();
			int index = item.getIndexId();
			while(index > 0){
				if(index <= allCategoriesMap.size()){
					CategoryItem catitem = allCategoriesMap.get(index);
					if(!visibleSnippetMap.containsKey(catitem)){
						snippet.setCategory(catitem);
						catitem.setSelected(true);
						catitem.setDirty(true);
						item.setSelected(false);
						item.setDirty(true);
						visibleSnippetMap.remove(item);
						snippet.createSnippet();
						visibleSnippetMap.put(catitem, snippet);
						break;
					}
					else{
						index++;
					}
				}
				else{
					index = 1;
				}
			}
			}
			return true;
		}
		case (TagEvent.UPCATEGORYCHANGED):
		{
			if(categoryList.size() > getDisplayedSnippetsNumber()){
			CategoryItem item = (CategoryItem)evt.getEventData();
			SnippetView snippet = (SnippetView)evt.getSource();
			int index = item.getIndexId();
			while(index >= 0){
				if(index >= 1){
					CategoryItem catitem = allCategoriesMap.get(index);
					if(!visibleSnippetMap.containsKey(catitem)){
						snippet.setCategory(catitem);
						catitem.setSelected(true);
						catitem.setDirty(true);
						item.setSelected(false);
						item.setDirty(true);
						visibleSnippetMap.remove(item);
						snippet.createSnippet();
						visibleSnippetMap.put(catitem, snippet);
						break;
					}
					else{
						index--;
					}
				}
				else{
					index = allCategoriesMap.size();
				}
			}
			}
			return true;
		}
		case (TagEvent.VIEWCATEGORY):
		{
			tagDisclosure.setOpen(false);
			
			basePanel.clear();
			CategoryItem item = (CategoryItem)evt.getEventData();
			CategoryView catview = new CategoryView(item);
			catview.createCategoryView();
			basePanel.add(catview);
			basePanel.setCellHorizontalAlignment(catview, HasHorizontalAlignment.ALIGN_CENTER);
			tagDisclosure.setOpen(true);
			return true;
		}
		case (TagEvent.VIEWSNIPPETS):
		{
			tagDisclosure.setOpen(false);
			refresh();
			return true;
		}
		case (TagEvent.TAGPRESENTERMINIMIZED):
		{
			tagDisclosure.setOpen(false);
			return true;
		}
		}
		return false;
	}

	public int getDisplayedSnippetsNumber() {
		return displayedSnippetsNumber;
	}

	public void setDisplayedSnippetsNumber(int displayedSnippetsNumber) {
		this.displayedSnippetsNumber = displayedSnippetsNumber;
	}

	/*public void onClose(DisclosureEvent arg0) {
		plusMinusImage.setUrl("images/plus.gif");
		tagDisclosure.getHeader().setTitle("click to maximize");
	}*/
	
	/*public void onOpen(DisclosureEvent arg0) {
		plusMinusImage.setUrl("images/minus.gif");
		tagDisclosure.getHeader().setTitle("click to minimize");
	}*/

	public int getTagDisclosureWidth() {
		return tagDisclosureWidth;
	}

	public void setTagDisclosureWidth(int tagDisclosureWidth) {
		this.tagDisclosureWidth = tagDisclosureWidth;
	}

	@Override
	public void onOpen(OpenEvent<DisclosurePanel> event) {
		plusMinusImage.setUrl("images/minus.gif");
		tagDisclosure.getHeader().setTitle("click to minimize");
		
	}

	@Override
	public void onClose(CloseEvent<DisclosurePanel> event) {
		plusMinusImage.setUrl("images/plus.gif");
		tagDisclosure.getHeader().setTitle("click to maximize");
		
	}

	
}

