package com.newscenter.client.ui;

import java.util.ArrayList;

import com.appUtils.client.GlassPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.newscenter.client.tags.CategoryItem;

public class SnippetView extends Composite {
	
	private SnippetTitle snippetTitle;
	private SnippetDetail snippetDetail;
	private CategoryItem category = null ;
	private VerticalPanel snippetPanel = new VerticalPanel();
	private Label filterLabel = new Label("Filter tags by:");
	private ArrayList categoryList;
	private GlassPanel glassPanel;
	private int isMandatory;
	
	public SnippetView(){
		snippetPanel.setPixelSize(220, 0);
		snippetPanel.setStylePrimaryName("snippetViewPanel");
		initWidget(snippetPanel);
	}
	
	public SnippetView(int isMandatory) {
		this.isMandatory = isMandatory;
		glassPanel = new GlassPanel();
		snippetPanel.setPixelSize(220, 0);
		snippetPanel.setStylePrimaryName("snippetViewPanel");
		initWidget(snippetPanel);  
		
	}
	
	public void createSnippet() {
		filterLabel.setStylePrimaryName("filterLabel");
		snippetPanel.clear();
		snippetTitle = new SnippetTitle(this);
		snippetDetail = new SnippetDetail(this);
		snippetPanel.add(filterLabel);
		snippetPanel.add(snippetTitle);
		snippetPanel.add(snippetDetail);
		if(isMandatory == 1)
			snippetPanel.add(glassPanel);
		
	}
	
	public CategoryItem getCategory() {
		return category;
	}

	public void setCategory(CategoryItem category) {
		this.category = category;
	}
	public ArrayList getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}
	
	public SnippetDetail getSnippetDetail() {
		return snippetDetail;
	}
}
