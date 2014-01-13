package com.newscenter.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.newscenter.client.ItemStore;
import com.newscenter.client.NewsCenterMain;
import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.NewsEvent;
import com.newscenter.client.events.TagEvent;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.TagItem;

public class ItemTagLabel extends Label implements ClickHandler,MouseOutHandler,MouseOverHandler {

	private int id;
	private boolean clicked = false;
	private HashMap<ItemTagLabel, TagItem> labelTagMap = new HashMap<ItemTagLabel, TagItem>();
	private SnippetDetail snippetDetail;
	private CategoryView categoryView;
	private TagItem labelTag;
	private CheckBoxImage cbImage;
	private Timer timer;
	static int count = 0;
	private boolean changed = false;;
	
	public ItemTagLabel(SnippetDetail snipDetail){
		setSnippetDetail(snipDetail);
		addClickHandler(this);
		addMouseOutHandler(this);
		addMouseOverHandler(this);
	}
	
	public ItemTagLabel(){
		addClickHandler(this);
		addMouseOutHandler(this);
		addMouseOverHandler(this);
		
	}
	
	public ItemTagLabel(CategoryView cview){
		setCategoryView(cview);
		addClickHandler(this);
		addMouseOutHandler(this);
		addMouseOverHandler(this);
	}
	public void setSelected(boolean bool){
		TagItem tag = getLabelTagMap().get(this);
		tag.setSelected(bool);
	}
	public void addTagItem(TagItem tag){
		labelTagMap.put(this, tag);
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isClicked() {
		return clicked;
	}

	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}
	
/*	public void onClick(Widget arg0) {
		if(arg0 instanceof ItemTagLabel){
			NewsCenterMain.tagSelectionChanged = true;
			ItemTagLabel label = (ItemTagLabel)arg0;
			if(label.isClicked()){
				TagItem tag = getLabelTagMap().get(label);

				if(label.getText().equals("SELECT ALL")){
					label.setClicked(false);
					label.getCbImage().uncheckImage();
					if(label.getSnippetDetail() != null){
						MainNewsPresenter.setLoadingMessage("Updating news...");
						TagEvent evt = new TagEvent(getSnippetDetail(),TagEvent.DESELECTCATEGORYITEM,tag);
						AppEventManager.getInstance().fireEvent(evt);
					}
					else{
						MainNewsPresenter.setLoadingMessage("Updating news...");
						TagEvent evt = new TagEvent(getCategoryView(),TagEvent.DESELECTCATEGORYITEM,tag);
						AppEventManager.getInstance().fireEvent(evt);
					}
				}
				else{
					setChanged(true);
					if(count == 0){
						count++;
						delay();
					}
					label.setClicked(false);
					label.getCbImage().uncheckImage();
					tag.setSelected(false);
					tag.setDirty(true);
					MainNewsPresenter.setLoadingMessage("Updating news...");
					if(label.getSnippetDetail() != null){
						Iterator itt = getSnippetDetail().getCbComponentArray().iterator();
							while(itt.hasNext()){
								CheckBoxComponent comp = (CheckBoxComponent)itt.next();
								if(comp.getItemLabel().getText().equals("SELECT ALL")){
									comp.getItemLabel().getCbImage().uncheckImage();
									comp.getItemLabel().setClicked(false);
								}
						}
						//TagEvent evt = new TagEvent(getSnippetDetail(),TagEvent.TAGDESELECTED,tag);
						//AppEventManager.getInstance().fireEvent(evt);
					}
					else{
						Iterator itt = getCategoryView().getCbComponentArray().iterator();
						while(itt.hasNext()){
							CheckBoxComponent comp = (CheckBoxComponent)itt.next();
							if(comp.getItemLabel().getText().equals("SELECT ALL")){
								comp.getItemLabel().getCbImage().uncheckImage();
								comp.getItemLabel().setClicked(false);
							}
						}
						//TagEvent evt = new TagEvent(getCategoryView(),TagEvent.TAGDESELECTED,tag);
						//AppEventManager.getInstance().fireEvent(evt);
					}
				}
			}
			else{
				TagItem tag = getLabelTagMap().get(label);
				if(label.getText().equals("SELECT ALL")){
					label.setClicked(true);
					label.getCbImage().checkImage();
					if(label.getSnippetDetail() != null){
						MainNewsPresenter.setLoadingMessage("Updating news...");
						TagEvent evt = new TagEvent(getSnippetDetail(),TagEvent.SELECTCATEGORYITEM,tag);
						AppEventManager.getInstance().fireEvent(evt);
					}
					else{
						MainNewsPresenter.setLoadingMessage("Updating news...");
						TagEvent evt = new TagEvent(getCategoryView(),TagEvent.SELECTCATEGORYITEM,tag);
						AppEventManager.getInstance().fireEvent(evt);
					}
				}
				else if(label.getText().equals("SHOW ALL")){
					TagEvent evt = new TagEvent(getSnippetDetail(),TagEvent.VIEWCATEGORY,tag);
					AppEventManager.getInstance().fireEvent(evt);
				}
				else if(label.getText().equals("CLEAR ALL")){
					ArrayList list = ItemStore.getInstance().getVisibleCategories();
					Iterator iter = list.iterator();
					while(iter.hasNext()){
						CategoryItem citem = (CategoryItem)iter.next();
						HashMap map = (HashMap)citem.getItemMap();
						for(Object obj : map.keySet()){
							TagItem tagitem = (TagItem)map.get(obj);
							tagitem.setDirty(true);
							tagitem.setSelected(false);
						}
					}
					NewsEvent evt = new NewsEvent(this,NewsEvent.NEWSDELETED,null);
					//TagEvent evt = new TagEvent(this,TagEvent.CLEARTAGS,null);
					AppEventManager.getInstance().fireEvent(evt);
				}
				else{
					setChanged(true);
					if(count == 0){
						count++;
						delay();
					}
					
					label.setClicked(true);
					label.getCbImage().checkImage();
					tag.setSelected(true);
					tag.setDirty(true);
					MainNewsPresenter.setLoadingMessage("Updating news...");
					CategoryItem citem = tag.getCategoryItem();
					if(label.getSnippetDetail() != null){
						if(citem.areAllChildrenSelected()){
							 Iterator itt = getSnippetDetail().getCbComponentArray().iterator();
							 while(itt.hasNext()){
								CheckBoxComponent comp = (CheckBoxComponent)itt.next();
								if(comp.getItemLabel().getText().equals("SELECT ALL")){
									comp.getItemLabel().getCbImage().checkImage();
									comp.getItemLabel().setClicked(true);
								}
							}
						}
						
						//TagEvent evt = new TagEvent(getSnippetDetail(),TagEvent.TAGSELECTED,tag);
						//AppEventManager.getInstance().fireEvent(evt);
					}
					else{
						if(citem.areAllChildrenSelected()){
							 Iterator itt = getCategoryView().getCbComponentArray().iterator();
							 while(itt.hasNext()){
								CheckBoxComponent comp = (CheckBoxComponent)itt.next();
								if(comp.getItemLabel().getText().equals("SELECT ALL")){
									comp.getItemLabel().getCbImage().checkImage();
									comp.getItemLabel().setClicked(true);
								}
							}
						}
						//TagEvent evt = new TagEvent(getCategoryView(),TagEvent.TAGSELECTED,tag);
						//AppEventManager.getInstance().fireEvent(evt);
					}
				}
				
			}
		}	
	}*/
	
	public void delay(){
		timer = new Timer() {
		      public void run() {
		    	  count = 0;
		    	  if(isChanged()){
		    		  setChanged(false);
		    		  TagEvent evt = new TagEvent(this,TagEvent.TAGSELECTED,null);
		    		  AppEventManager.getInstance().fireEvent(evt);
		    	  }
		      }
		    };
		 // Schedule the timer to run once in 2 seconds.
			timer.schedule(2000);
		
	}
	public void onMouseDown(Widget arg0, int arg1, int arg2) {

	}

	/*public void onMouseEnter(Widget arg0) {
		if(arg0 instanceof ItemTagLabel){
			ItemTagLabel label = (ItemTagLabel)arg0;
			if(label.getText().equals("SHOW ALL"))
				label.addStyleName("showAllHover");
			else if(label.getText().equals("CLEAR ALL"))
				label.addStyleName("clearAllHover");
			else
				label.addStyleName("specialLabelHover");
		}
	}

	public void onMouseLeave(Widget arg0) {
		if(arg0 instanceof ItemTagLabel){
			ItemTagLabel label = (ItemTagLabel)arg0;
			if(label.getText().equals("SHOW ALL"))
				label.removeStyleName("showAllHover");
			else if(label.getText().equals("CLEAR ALL"))
				label.removeStyleName("clearAllHover");
			else
				label.removeStyleName("specialLabelHover");

		}
		
	}*/

	public void onMouseMove(Widget arg0, int arg1, int arg2) {

	}

	public void onMouseUp(Widget arg0, int arg1, int arg2) {
		
	}

	public HashMap<ItemTagLabel, TagItem> getLabelTagMap() {
		return labelTagMap;
	}

	public void setLabelTagMap(HashMap<ItemTagLabel, TagItem> labelTagMap) {
		this.labelTagMap = labelTagMap;
	}

	public SnippetDetail getSnippetDetail() {
		return snippetDetail;
	}

	public void setSnippetDetail(SnippetDetail snippetDetail) {
		this.snippetDetail = snippetDetail;
	}

	public CategoryView getCategoryView() {
		return categoryView;
	}

	public void setCategoryView(CategoryView categoryView) {
		this.categoryView = categoryView;
	}
	public TagItem getLabelTag() {
		return labelTag;
	}

	public void setLabelTag(TagItem labelTag) {
		this.labelTag = labelTag;
	}

	public CheckBoxImage getCbImage() {
		return cbImage;
	}

	public void setCbImage(CheckBoxImage cbImage) {
		this.cbImage = cbImage;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof ItemTagLabel){
			NewsCenterMain.tagSelectionChanged = true;
			ItemTagLabel label = (ItemTagLabel)arg0;
			if(label.isClicked()){
				TagItem tag = getLabelTagMap().get(label);

				if(label.getText().equals("SELECT ALL")){
					label.setClicked(false);
					label.getCbImage().uncheckImage();
					if(label.getSnippetDetail() != null){
						MainNewsPresenter.setLoadingMessage("Updating news...");
						TagEvent evt = new TagEvent(getSnippetDetail(),TagEvent.DESELECTCATEGORYITEM,tag);
						AppEventManager.getInstance().fireEvent(evt);
					}
					else{
						MainNewsPresenter.setLoadingMessage("Updating news...");
						TagEvent evt = new TagEvent(getCategoryView(),TagEvent.DESELECTCATEGORYITEM,tag);
						AppEventManager.getInstance().fireEvent(evt);
					}
				}
				else{
					setChanged(true);
					if(count == 0){
						count++;
						delay();
					}
					label.setClicked(false);
					label.getCbImage().uncheckImage();
					tag.setSelected(false);
					tag.setDirty(true);
					MainNewsPresenter.setLoadingMessage("Updating news...");
					if(label.getSnippetDetail() != null){
						Iterator itt = getSnippetDetail().getCbComponentArray().iterator();
							while(itt.hasNext()){
								CheckBoxComponent comp = (CheckBoxComponent)itt.next();
								if(comp.getItemLabel().getText().equals("SELECT ALL")){
									comp.getItemLabel().getCbImage().uncheckImage();
									comp.getItemLabel().setClicked(false);
								}
						}
						/*TagEvent evt = new TagEvent(getSnippetDetail(),TagEvent.TAGDESELECTED,tag);
						AppEventManager.getInstance().fireEvent(evt);*/
					}
					else{
						Iterator itt = getCategoryView().getCbComponentArray().iterator();
						while(itt.hasNext()){
							CheckBoxComponent comp = (CheckBoxComponent)itt.next();
							if(comp.getItemLabel().getText().equals("SELECT ALL")){
								comp.getItemLabel().getCbImage().uncheckImage();
								comp.getItemLabel().setClicked(false);
							}
						}
						/*TagEvent evt = new TagEvent(getCategoryView(),TagEvent.TAGDESELECTED,tag);
						AppEventManager.getInstance().fireEvent(evt);*/
					}
				}
			}
			else{
				TagItem tag = getLabelTagMap().get(label);
				if(label.getText().equals("SELECT ALL")){
					label.setClicked(true);
					label.getCbImage().checkImage();
					if(label.getSnippetDetail() != null){
						MainNewsPresenter.setLoadingMessage("Updating news...");
						TagEvent evt = new TagEvent(getSnippetDetail(),TagEvent.SELECTCATEGORYITEM,tag);
						AppEventManager.getInstance().fireEvent(evt);
					}
					else{
						MainNewsPresenter.setLoadingMessage("Updating news...");
						TagEvent evt = new TagEvent(getCategoryView(),TagEvent.SELECTCATEGORYITEM,tag);
						AppEventManager.getInstance().fireEvent(evt);
					}
				}
				else if(label.getText().equals("SHOW ALL")){
					TagEvent evt = new TagEvent(getSnippetDetail(),TagEvent.VIEWCATEGORY,tag);
					AppEventManager.getInstance().fireEvent(evt);
				}
				else if(label.getText().equals("CLEAR ALL")){
					ArrayList list = ItemStore.getInstance().getVisibleCategories();
					Iterator iter = list.iterator();
					while(iter.hasNext()){
						CategoryItem citem = (CategoryItem)iter.next();
						HashMap map = (HashMap)citem.getItemMap();
						for(Object obj : map.keySet()){
							TagItem tagitem = (TagItem)map.get(obj);
							tagitem.setDirty(true);
							tagitem.setSelected(false);
						}
					}
					NewsEvent evt = new NewsEvent(this,NewsEvent.NEWSDELETED,null);
					//TagEvent evt = new TagEvent(this,TagEvent.CLEARTAGS,null);
					AppEventManager.getInstance().fireEvent(evt);
				}
				else{
					setChanged(true);
					if(count == 0){
						count++;
						delay();
					}
					
					label.setClicked(true);
					label.getCbImage().checkImage();
					tag.setSelected(true);
					tag.setDirty(true);
					MainNewsPresenter.setLoadingMessage("Updating news...");
					CategoryItem citem = tag.getCategoryItem();
					if(label.getSnippetDetail() != null){
						if(citem.areAllChildrenSelected()){
							 Iterator itt = getSnippetDetail().getCbComponentArray().iterator();
							 while(itt.hasNext()){
								CheckBoxComponent comp = (CheckBoxComponent)itt.next();
								if(comp.getItemLabel().getText().equals("SELECT ALL")){
									comp.getItemLabel().getCbImage().checkImage();
									comp.getItemLabel().setClicked(true);
								}
							}
						}
						
						/*TagEvent evt = new TagEvent(getSnippetDetail(),TagEvent.TAGSELECTED,tag);
						AppEventManager.getInstance().fireEvent(evt);*/
					}
					else{
						if(citem.areAllChildrenSelected()){
							 Iterator itt = getCategoryView().getCbComponentArray().iterator();
							 while(itt.hasNext()){
								CheckBoxComponent comp = (CheckBoxComponent)itt.next();
								if(comp.getItemLabel().getText().equals("SELECT ALL")){
									comp.getItemLabel().getCbImage().checkImage();
									comp.getItemLabel().setClicked(true);
								}
							}
						}
						/*TagEvent evt = new TagEvent(getCategoryView(),TagEvent.TAGSELECTED,tag);
						AppEventManager.getInstance().fireEvent(evt);*/
					}
				}
				
			}
		}	
		
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof ItemTagLabel){
			ItemTagLabel label = (ItemTagLabel)arg0;
			if(label.getText().equals("SHOW ALL"))
				label.addStyleName("showAllHover");
			else if(label.getText().equals("CLEAR ALL"))
				label.addStyleName("clearAllHover");
			else
				label.addStyleName("specialLabelHover");
		}
		
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof ItemTagLabel){
			ItemTagLabel label = (ItemTagLabel)arg0;
			if(label.getText().equals("SHOW ALL"))
				label.removeStyleName("showAllHover");
			else if(label.getText().equals("CLEAR ALL"))
				label.removeStyleName("clearAllHover");
			else
				label.removeStyleName("specialLabelHover");

		}
		
	}
	
}
