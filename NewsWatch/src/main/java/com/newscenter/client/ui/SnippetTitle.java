package com.newscenter.client.ui;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.TagEvent;
import com.newscenter.client.tags.CategoryItem;


public class SnippetTitle extends Composite implements ClickHandler,MouseOverHandler,MouseOutHandler{

	private HorizontalPanel titleBasePanel = new HorizontalPanel();
	private HorizontalPanel containerPanel = new HorizontalPanel();
	private VerticalPanel imagePanel = new VerticalPanel();
	private Image upImage = new Image("images/upTriangle.JPG");
	private Image downImage = new Image("images/downTriangle.JPG");
	private Label categoryNameLbl = new Label();
	private SnippetView snippetRef = null;
	private static ArrayList<CategoryItem> categoryList;
	private int currentViewedCategoryId;

	public SnippetTitle(SnippetView view){
		//setCategoryList( ItemStore.getInstance().getVisibleCategories());
		setCategoryList(view.getCategoryList());
		setSnippetRef(view);
		setCurrentViewedCategoryId(getSnippetRef().getCategory().getTagId());
	
		upImage.addClickHandler(this);
		downImage.addClickHandler(this);
		upImage.addMouseOutHandler(this);
		upImage.addMouseOutHandler(this);
		downImage.addMouseOutHandler(this);
		downImage.addMouseOverHandler(this);
		
		upImage.setStylePrimaryName("imageStyle");
		downImage.setStylePrimaryName("imageStyle");
		upImage.setTitle("click to view more categories");
		downImage.setTitle("click to view more categories");
		imagePanel.add(upImage);
		imagePanel.add(downImage);
		imagePanel.setSpacing(5);
		String categoryname = getSnippetRef().getCategory().getTagName();
		categoryNameLbl.setTitle(categoryname);
		if(categoryname.length() > 27){
			categoryname  = categoryname.substring(0, 23);
			categoryname = categoryname + "...";
		}
		categoryNameLbl.setText(categoryname);
		categoryNameLbl.setStylePrimaryName("snippetCategoryNameLabel");
		containerPanel.add(categoryNameLbl);
		containerPanel.add(imagePanel);
		
		containerPanel.setCellVerticalAlignment(categoryNameLbl, HasVerticalAlignment.ALIGN_MIDDLE);
		containerPanel.setCellHorizontalAlignment(imagePanel, HasHorizontalAlignment.ALIGN_RIGHT);
		containerPanel.setWidth("195px");
		containerPanel.setStylePrimaryName("snippetTitle");
		titleBasePanel.add(containerPanel);
		titleBasePanel.setStylePrimaryName("snippetbasePanel");
		initWidget(titleBasePanel);
	}

	public SnippetView getSnippetRef() {
		return snippetRef;
	}

	public void setSnippetRef(SnippetView snip) {
		this.snippetRef = snip;
	}


	/*public void onClick(Widget arg0) {
		int listSize = getCategoryList().size();
		if(arg0 == upImage){
			TagEvent evt = new TagEvent(getSnippetRef(),TagEvent.UPCATEGORYCHANGED,getSnippetRef().getCategory());
			AppEventManager.getInstance().fireEvent(evt);
		}
		else if(arg0 == downImage){
			TagEvent evt = new TagEvent(getSnippetRef(),TagEvent.DOWNCATEGORYCHANGED,getSnippetRef().getCategory());
			AppEventManager.getInstance().fireEvent(evt);
		}
		
	}*/

	public int getCurrentViewedCategoryId() {
		return currentViewedCategoryId;
	}

	public void setCurrentViewedCategoryId(int currentViewCategory) {
		this.currentViewedCategoryId = currentViewCategory;
	}

	public void onMouseDown(Widget arg0, int arg1, int arg2) {
		
	}

	/*public void onMouseEnter(Widget arg0) {
		if(arg0 instanceof Image){
			Image image = (Image)arg0;
			image.addStyleName("imageHover");
		}
	}*/

	/*public void onMouseLeave(Widget arg0) {
		if(arg0 instanceof Image){
			Image image = (Image)arg0;
			image.removeStyleName("imageHover");	
		}
	}*/

	public void onMouseMove(Widget arg0, int arg1, int arg2) {

	}

	public void onMouseUp(Widget arg0, int arg1, int arg2) {
	
	}

	public ArrayList<CategoryItem> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<CategoryItem> categoryList) {
		this.categoryList = categoryList;
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget arg0 = (Widget)event.getSource();
		int listSize = getCategoryList().size();
		if(arg0 == upImage){
			TagEvent evt = new TagEvent(getSnippetRef(),TagEvent.UPCATEGORYCHANGED,getSnippetRef().getCategory());
			AppEventManager.getInstance().fireEvent(evt);
		}
		else if(arg0 == downImage){
			TagEvent evt = new TagEvent(getSnippetRef(),TagEvent.DOWNCATEGORYCHANGED,getSnippetRef().getCategory());
			AppEventManager.getInstance().fireEvent(evt);
		}
		
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof Image){
			Image image = (Image)arg0;
			image.removeStyleName("imageHover");	
		}
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof Image){
			Image image = (Image)arg0;
			image.removeStyleName("imageHover");	
		}
		
	}

	

	
	
}
