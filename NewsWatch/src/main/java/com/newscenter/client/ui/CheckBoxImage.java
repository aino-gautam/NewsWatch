package com.newscenter.client.ui;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.TagEvent;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.TagItem;

public class CheckBoxImage extends Image implements ClickHandler{
	
	private boolean checked = false;
	private SnippetDetail snipDetailRef;
	private CheckBoxComponent cbcomponentRef;
	private CategoryView categoryView;
	
	public CheckBoxImage(CheckBoxComponent cbCompRef, boolean bool){
		if(bool){
			checkImage();
		}
		else{
			uncheckImage();
		}
		
		setCbcomponentRef(cbCompRef);
		addClickHandler(this);
		setStylePrimaryName("checkboxImage");
	}
	
	public void checkImage(){
		setUrl("images/Check box.gif");
		setChecked(true);
	}
	public void uncheckImage(){
		setUrl("images/Checkbox1.gif");
		setChecked(false);
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/*public void onClick(Widget arg0) {
		CheckBoxImage cbimage = (CheckBoxImage)arg0;
		if(!cbimage.isChecked()){
			cbimage.checkImage();
			cbimage.setChecked(true);
			HashMap map = getCbcomponentRef().getTagImageMap();
			for(Object ob : map.keySet()){
				if(ob == cbimage){
					ItemTagLabel label = (ItemTagLabel)map.get(cbimage);
					label.setClicked(true);
					TagItem tagitem = label.getLabelTag();
					if(label.getText().equals("SELECT ALL")){
						if(getCbcomponentRef().getSnipDetailRef() != null){
							MainNewsPresenter.setLoadingMessage("Updating news...");
							TagEvent evt = new TagEvent(getCbcomponentRef().getSnipDetailRef(),TagEvent.SELECTCATEGORYITEM,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
						else{
							MainNewsPresenter.setLoadingMessage("Updating news...");
							TagEvent evt = new TagEvent(getCbcomponentRef().getCatViewRef(),TagEvent.SELECTCATEGORYITEM,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
					}
					else{
						label.setClicked(true);
						tagitem.setSelected(true);
						tagitem.setDirty(true);
						MainNewsPresenter.setLoadingMessage("Updating news...");
						CategoryItem citem = tagitem.getCategoryItem();
						if(getCbcomponentRef().getSnipDetailRef() != null){
								if(citem.areAllChildrenSelected()){
									 Iterator itt = getCbcomponentRef().getSnipDetailRef().getCbComponentArray().iterator();
									 while(itt.hasNext()){
										CheckBoxComponent comp = (CheckBoxComponent)itt.next();
										if(comp.getItemLabel().getText().equals("SELECT ALL")){
											comp.getItemLabel().setClicked(true);
											comp.getItemLabel().getCbImage().checkImage();
										}
									}
								}
							TagEvent evt = new TagEvent(getCbcomponentRef().getSnipDetailRef(),TagEvent.TAGSELECTED,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
						else{
							if(citem.areAllChildrenSelected()){
								 Iterator itt = getCbcomponentRef().getCatViewRef().getCbComponentArray().iterator();
								 while(itt.hasNext()){
									CheckBoxComponent comp = (CheckBoxComponent)itt.next();
									if(comp.getItemLabel().getText().equals("SELECT ALL")){
										comp.getItemLabel().setClicked(true);
										comp.getItemLabel().getCbImage().checkImage();
									}
								}
							}
							TagEvent evt = new TagEvent(getCbcomponentRef().getCatViewRef(),TagEvent.TAGSELECTED,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
					}
					
				}
			}
		}
		else{
			cbimage.uncheckImage();
			cbimage.setChecked(false);
			HashMap map = getCbcomponentRef().getTagImageMap();
			for(Object ob : map.keySet()){
				if(ob == cbimage){
					ItemTagLabel label = (ItemTagLabel)map.get(cbimage);
					label.setClicked(false);
					TagItem tagitem = label.getLabelTag();
					if(label.getText().equals("SELECT ALL")){
						if(getCbcomponentRef().getSnipDetailRef() != null){
							MainNewsPresenter.setLoadingMessage("Updating news...");
							TagEvent evt = new TagEvent(getCbcomponentRef().getSnipDetailRef(),TagEvent.DESELECTCATEGORYITEM,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
						else{
							MainNewsPresenter.setLoadingMessage("Updating news...");
							TagEvent evt = new TagEvent(getCbcomponentRef().getCatViewRef(),TagEvent.DESELECTCATEGORYITEM,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
					}
					else{
						label.setClicked(false);
						tagitem.setSelected(false);
						tagitem.setDirty(true);
						MainNewsPresenter.setLoadingMessage("Updating news...");
						if(getCbcomponentRef().getSnipDetailRef()!= null){
						    Iterator itt = getCbcomponentRef().getSnipDetailRef().getCbComponentArray().iterator();
							while(itt.hasNext()){
								CheckBoxComponent comp = (CheckBoxComponent)itt.next();
								if(comp.getItemLabel().getText().equals("SELECT ALL")){
									comp.getItemLabel().setClicked(false);
									comp.getItemLabel().getCbImage().uncheckImage();
								}
							}
							TagEvent evt = new TagEvent(getCbcomponentRef().getSnipDetailRef(),TagEvent.TAGDESELECTED,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
						else{
							Iterator itt = getCbcomponentRef().getCatViewRef().getCbComponentArray().iterator();
							while(itt.hasNext()){
								CheckBoxComponent comp = (CheckBoxComponent)itt.next();
								if(comp.getItemLabel().getText().equals("SELECT ALL")){
									comp.getItemLabel().setClicked(false);
									comp.getItemLabel().getCbImage().uncheckImage();
								}
							}
							TagEvent evt = new TagEvent(getCbcomponentRef().getCatViewRef(),TagEvent.TAGDESELECTED,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
					}
				}
			}
		}
	}*/

	public SnippetDetail getSnipDetailRef() {
		return snipDetailRef;
	}

	public void setSnipDetailRef(SnippetDetail snipDetailRef) {
		this.snipDetailRef = snipDetailRef;
	}
	public CheckBoxComponent getCbcomponentRef() {
		return cbcomponentRef;
	}
	public void setCbcomponentRef(CheckBoxComponent cbcomponentRef) {
		this.cbcomponentRef = cbcomponentRef;
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget arg0=(Widget)event.getSource();
		CheckBoxImage cbimage = (CheckBoxImage)arg0;
		if(!cbimage.isChecked()){
			cbimage.checkImage();
			cbimage.setChecked(true);
			HashMap map = getCbcomponentRef().getTagImageMap();
			for(Object ob : map.keySet()){
				if(ob == cbimage){
					ItemTagLabel label = (ItemTagLabel)map.get(cbimage);
					label.setClicked(true);
					TagItem tagitem = label.getLabelTag();
					if(label.getText().equals("SELECT ALL")){
						if(getCbcomponentRef().getSnipDetailRef() != null){
							MainNewsPresenter.setLoadingMessage("Updating news...");
							TagEvent evt = new TagEvent(getCbcomponentRef().getSnipDetailRef(),TagEvent.SELECTCATEGORYITEM,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
						else{
							MainNewsPresenter.setLoadingMessage("Updating news...");
							TagEvent evt = new TagEvent(getCbcomponentRef().getCatViewRef(),TagEvent.SELECTCATEGORYITEM,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
					}
					else{
						label.setClicked(true);
						tagitem.setSelected(true);
						tagitem.setDirty(true);
						MainNewsPresenter.setLoadingMessage("Updating news...");
						CategoryItem citem = tagitem.getCategoryItem();
						if(getCbcomponentRef().getSnipDetailRef() != null){
								if(citem.areAllChildrenSelected()){
									 Iterator itt = getCbcomponentRef().getSnipDetailRef().getCbComponentArray().iterator();
									 while(itt.hasNext()){
										CheckBoxComponent comp = (CheckBoxComponent)itt.next();
										if(comp.getItemLabel().getText().equals("SELECT ALL")){
											comp.getItemLabel().setClicked(true);
											comp.getItemLabel().getCbImage().checkImage();
										}
									}
								}
							TagEvent evt = new TagEvent(getCbcomponentRef().getSnipDetailRef(),TagEvent.TAGSELECTED,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
						else{
							if(citem.areAllChildrenSelected()){
								 Iterator itt = getCbcomponentRef().getCatViewRef().getCbComponentArray().iterator();
								 while(itt.hasNext()){
									CheckBoxComponent comp = (CheckBoxComponent)itt.next();
									if(comp.getItemLabel().getText().equals("SELECT ALL")){
										comp.getItemLabel().setClicked(true);
										comp.getItemLabel().getCbImage().checkImage();
									}
								}
							}
							TagEvent evt = new TagEvent(getCbcomponentRef().getCatViewRef(),TagEvent.TAGSELECTED,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
					}
					
				}
			}
		}
		else{
			cbimage.uncheckImage();
			cbimage.setChecked(false);
			HashMap map = getCbcomponentRef().getTagImageMap();
			for(Object ob : map.keySet()){
				if(ob == cbimage){
					ItemTagLabel label = (ItemTagLabel)map.get(cbimage);
					label.setClicked(false);
					TagItem tagitem = label.getLabelTag();
					if(label.getText().equals("SELECT ALL")){
						if(getCbcomponentRef().getSnipDetailRef() != null){
							MainNewsPresenter.setLoadingMessage("Updating news...");
							TagEvent evt = new TagEvent(getCbcomponentRef().getSnipDetailRef(),TagEvent.DESELECTCATEGORYITEM,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
						else{
							MainNewsPresenter.setLoadingMessage("Updating news...");
							TagEvent evt = new TagEvent(getCbcomponentRef().getCatViewRef(),TagEvent.DESELECTCATEGORYITEM,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
					}
					else{
						label.setClicked(false);
						tagitem.setSelected(false);
						tagitem.setDirty(true);
						MainNewsPresenter.setLoadingMessage("Updating news...");
						if(getCbcomponentRef().getSnipDetailRef()!= null){
						    Iterator itt = getCbcomponentRef().getSnipDetailRef().getCbComponentArray().iterator();
							while(itt.hasNext()){
								CheckBoxComponent comp = (CheckBoxComponent)itt.next();
								if(comp.getItemLabel().getText().equals("SELECT ALL")){
									comp.getItemLabel().setClicked(false);
									comp.getItemLabel().getCbImage().uncheckImage();
								}
							}
							TagEvent evt = new TagEvent(getCbcomponentRef().getSnipDetailRef(),TagEvent.TAGDESELECTED,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
						else{
							Iterator itt = getCbcomponentRef().getCatViewRef().getCbComponentArray().iterator();
							while(itt.hasNext()){
								CheckBoxComponent comp = (CheckBoxComponent)itt.next();
								if(comp.getItemLabel().getText().equals("SELECT ALL")){
									comp.getItemLabel().setClicked(false);
									comp.getItemLabel().getCbImage().uncheckImage();
								}
							}
							TagEvent evt = new TagEvent(getCbcomponentRef().getCatViewRef(),TagEvent.TAGDESELECTED,tagitem);
							AppEventManager.getInstance().fireEvent(evt);
						}
					}
				}
			}
		}
		
	}

}
