package com.newscenter.client.ui;

import java.util.HashMap;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.TagItem;

public class CheckBoxComponent extends Composite{
	
	private HorizontalPanel hpComponent = new HorizontalPanel();
	private HorizontalPanel componentContainer = new HorizontalPanel();
	//private HashMap<CheckBoxImage, Label> tagImageMap = new HashMap<CheckBoxImage, Label>();
	private HashMap<CheckBoxImage, Label> tagImageMap = new HashMap<CheckBoxImage, Label>();
	private boolean checked = false;
	private SnippetDetail snipDetailRef;
	private CategoryView catViewRef;
	private ItemTagLabel itemLabel;
	
	public CheckBoxComponent(SnippetDetail snippet){
		setSnipDetailRef(snippet);
		hpComponent.setSpacing(2);
		componentContainer.add(hpComponent);
		componentContainer.setWidth("100%");
		initWidget(componentContainer);
	}
	public CheckBoxComponent(CategoryView catview){
		setCatViewRef(catview);
		hpComponent.setSpacing(2);
		componentContainer.add(hpComponent);
		componentContainer.setWidth("100%");
		initWidget(componentContainer);
	}
	
	public void addComponent(CheckBoxImage image, TagItem tag){
		hpComponent.add(image);
		if(getSnipDetailRef() != null)
			hpComponent.add(createLabel(image,tag, getSnipDetailRef()));
		else
			hpComponent.add(createLabel(image,tag, getCatViewRef()));
	}
	
	public void addComponent(CheckBoxImage image, CategoryItem category, String text){
		if(!text.equals("SHOW ALL")){
			hpComponent.add(image);
			if(getSnipDetailRef() != null)
				hpComponent.add(createBoldLabel(image,category, text, getSnipDetailRef()));
			else
				hpComponent.add(createBoldLabel(image,category, text, getCatViewRef()));
		}
		else{
			hpComponent.add(createBoldLabel(image,category, text, getSnipDetailRef()));
		}
		
	}
	
	public ItemTagLabel createLabel(CheckBoxImage image,TagItem tagitem, Object obj){
		ItemTagLabel label = new ItemTagLabel();
		if(obj instanceof SnippetDetail)
			label.setSnippetDetail((SnippetDetail)obj);
		else
			label.setCategoryView((CategoryView)obj);
		
		label.setText(tagitem.getTagName());
		label.setId(tagitem.getTagId());
		label.setLabelTag(tagitem);
		label.addTagItem(tagitem);
		label.setCbImage(image);
		label.setStylePrimaryName("snippetSpecialLabel");
		if(tagitem.isSelected()){
			label.setClicked(true);
		}
		setItemLabel(label);
		tagImageMap.put(image, label);
		return label;
	}
	
	/**
	 * for capital text
	 * @param text
	 * @return
	 */
	public ItemTagLabel createBoldLabel(CheckBoxImage image,CategoryItem citem, String text, Object obj){
		ItemTagLabel label = new ItemTagLabel();
		if(obj instanceof SnippetDetail)
			label.setSnippetDetail((SnippetDetail)obj);
		else
			label.setCategoryView((CategoryView)obj);
		if(!text.equals("SHOW ALL")){
			if(citem.areAllChildrenSelected())
				label.setClicked(true);
			else
				label.setClicked(false);
		}
		label.setText(text);
		label.setId(citem.getTagId());
		label.setLabelTag(citem);
		label.addTagItem(citem);
		label.setCbImage(image);
		if(text.equals("SHOW ALL")){
			label.setStylePrimaryName("showAllLabelStyle");
			label.setTitle("View all tags of "+ citem.getTagName());
		}
		else{
			label.setStylePrimaryName("labelStyle");
			label.setTitle("Selects all tags of "+ citem.getTagName());
		}
		setItemLabel(label);
		tagImageMap.put(image, label);
		return label;
	}

	public HashMap<CheckBoxImage, Label> getTagImageMap() {
		return tagImageMap;
	}

	public void setTagImageMap(HashMap<CheckBoxImage, Label> tagImageMap) {
		this.tagImageMap = tagImageMap;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public SnippetDetail getSnipDetailRef() {
		return snipDetailRef;
	}
	public void setSnipDetailRef(SnippetDetail snipDetailRef) {
		this.snipDetailRef = snipDetailRef;
	}
	public CategoryView getCatViewRef() {
		return catViewRef;
	}
	public void setCatViewRef(CategoryView catViewRef) {
		this.catViewRef = catViewRef;
	}
	public ItemTagLabel getItemLabel() {
		return itemLabel;
	}
	public void setItemLabel(ItemTagLabel itemLabel) {
		this.itemLabel = itemLabel;
	}
}

