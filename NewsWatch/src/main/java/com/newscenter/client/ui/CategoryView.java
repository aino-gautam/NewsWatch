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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.TagEvent;
import com.newscenter.client.events.TagEventListener;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.TagItem;

public class CategoryView extends FlexTable implements TagEventListener,ClickHandler,MouseOverHandler,MouseOutHandler{

	private CategoryItem categoryItem;
	private Label backLabel = new Label("GO BACK");
	private HorizontalPanel backPanel = new HorizontalPanel();
	private Image backImage = new Image("images/less.gif");
	private CheckBoxComponent cbComponent;
	private ArrayList cbComponentArray = new ArrayList();;
	private FlexCellFormatter formatter;
	private HorizontalPanel extrapanel = new HorizontalPanel();
	
	public CategoryView(CategoryItem catitem){
		AppEventManager.getInstance().addTagEventListener(this);
		setCategoryItem(catitem);
	}
	
	public void createCategoryView(){
		//setCellPadding(5);
		setWidth("100%");
		backLabel.addClickHandler(this);
		backLabel.addMouseOutHandler(this);
		backLabel.addMouseOverHandler(this);
				
		backLabel.setStylePrimaryName("goBackLabel");
		backLabel.setTitle("Go back to snippet view of categories");
		backPanel.add(new Image("images/goback_arrows.gif"));
		backPanel.add(backLabel);
		extrapanel.add(new Label(" "));
		extrapanel.setStylePrimaryName("categoryextrapanel");
		
		int row = 4;
		int col = 0;
		FlexCellFormatter formatter = getFlexCellFormatter();
		formatter.setColSpan(0, 0, 6);
		formatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		setWidget(0, 0, backPanel);
		formatter.setColSpan(1, 0, 6);
		setWidget(1, 0, createCategoryTitle(getCategoryItem()));
		formatter.setColSpan(2, 0, 6);
		formatter.setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);
		//setWidget(2, 0, new HTML("<hr style=\"height: '1'; text-align: 'left'; width: '98%'\">"));
		//formatter.setColSpan(2, 0, 6);
		formatter.setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_LEFT);
		setWidget(2, 0,extrapanel);
		
		//row+=4;
		if(!getCategoryItem().areAllChildrenSelected()){
			cbComponent = new CheckBoxComponent(this);
			cbComponent.addComponent(new CheckBoxImage(cbComponent, false), getCategoryItem(), "SELECT ALL");
			formatter = getFlexCellFormatter();
			formatter.setHeight(row, col, "25px");
			setWidget(row, col, cbComponent);
			cbComponentArray.add(cbComponent);
		}
		else{
			cbComponent = new CheckBoxComponent(this);
			cbComponent.addComponent(new CheckBoxImage(cbComponent,true), getCategoryItem(), "SELECT ALL");
			formatter = getFlexCellFormatter();
			formatter.setHeight(row, col, "25px");
			setWidget(row, col, cbComponent);
			cbComponentArray.add(cbComponent);
		}
		formatter.setHorizontalAlignment(row, col, HasHorizontalAlignment.ALIGN_LEFT);
		row++;
		
		HashMap map = getCategoryItem().getItemMap();
		int mapsize = map.size();
		int count = 1;
		int noOfrows = (mapsize / 6);
		if (mapsize%6 != 0)
			noOfrows = noOfrows+1;
		
		for(Object obj : map.keySet()){
			TagItem tag = (TagItem)map.get(obj);
			
			cbComponent = new CheckBoxComponent(this);
			if(tag.isSelected()){
				cbComponent.addComponent(new CheckBoxImage(cbComponent,true),tag);
			}
			else{
				cbComponent.addComponent(new CheckBoxImage(cbComponent,false),tag);
			}
			
			formatter.setWidth(row, col, "15%");
			formatter.setHeight(row, col, "15px");
			setWidget(row, col, cbComponent);
			cbComponentArray.add(cbComponent);
			formatter.setHorizontalAlignment(row, col, HasHorizontalAlignment.ALIGN_LEFT);
			formatter.setVerticalAlignment(row, col, HasVerticalAlignment.ALIGN_TOP);
			row++;
			count++;
			if(count > noOfrows){
				col++;
				row = 5;
				count=1;
			}
			/*col++;
			if(col>5)
			{
				row++;
				col = 0;
			}*/
		}
	}
	
	public Label createCategoryTitle(TagItem tagitem){
		Label label = new Label();
		label.setText(tagitem.getTagName());
		label.setStylePrimaryName("CategoryTitleLabel");
		return label;
	}
	
	public CategoryItem getCategoryItem() {
		return categoryItem;
	}
	
	public void setCategoryItem(CategoryItem categoryItem) {
		this.categoryItem = categoryItem;
	}

	public boolean onEvent(TagEvent evt) {
		int evttype = evt.getEventType();
		
		switch(evttype){
		case (TagEvent.SELECTCATEGORYITEM):
		{
			if(evt.getSource() instanceof CategoryView){
				CategoryView cv = (CategoryView)evt.getSource();
				Iterator iter = cv.cbComponentArray.iterator();
				while(iter.hasNext()){
					CheckBoxComponent cbcomp = (CheckBoxComponent)iter.next();
					ItemTagLabel lbl = cbcomp.getItemLabel();
					if(!lbl.getText().equals("SELECT ALL")){
						lbl.setSelected(true);
						lbl.setClicked(true);
						lbl.getCbImage().checkImage();
					}
				}
				CategoryItem citem = (CategoryItem)evt.getEventData();
				citem.setAllChildrenSelected(true);
				TagEvent event = new TagEvent(this, TagEvent.CATEGORYITEMSELECTED, citem);
				AppEventManager.getInstance().fireEvent(event);
			}
			break;
		}
		case (TagEvent.DESELECTCATEGORYITEM):
		{
			if(evt.getSource() instanceof CategoryView){
				CategoryView cv = (CategoryView)evt.getSource();
				Iterator iter = cv.cbComponentArray.iterator();
				while(iter.hasNext()){
					CheckBoxComponent cbcomp = (CheckBoxComponent)iter.next();
					ItemTagLabel lbl = cbcomp.getItemLabel();
					if(!lbl.getText().equals("SELECT ALL")){
						lbl.setSelected(false);
						lbl.setClicked(false);
						lbl.getCbImage().uncheckImage();
					}
				}
				CategoryItem catitem = (CategoryItem)evt.getEventData();
				catitem.setAllChildrenSelected(false);
				TagEvent event = new TagEvent(this, TagEvent.CATEGORYITEMDESELECTED, catitem);
				AppEventManager.getInstance().fireEvent(event);
			}
			break;
		}
		case (TagEvent.CLEARTAGS):{
			Iterator iter = this.cbComponentArray.iterator();
			while(iter.hasNext()){
				CheckBoxComponent cbcomp = (CheckBoxComponent)iter.next();
				ItemTagLabel lbl = cbcomp.getItemLabel();
				if((!lbl.getText().equals("SHOW ALL"))){
					//if(!lbl.getText().equals("SELECT ALL"))
					//{
						lbl.setSelected(false);
						lbl.setClicked(false);
						lbl.getCbImage().uncheckImage();
					//}
				}
			}
			break;
		}
		}
		return false;
	}

	/*public void onClick(Widget arg0) {
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			if(label.getText().equals("GO BACK")){
				TagEvent evt = new TagEvent(this, TagEvent.VIEWSNIPPETS, null);
				AppEventManager.getInstance().fireEvent(evt);
			}
		}
	}*/

	public ArrayList getCbComponentArray() {
		return cbComponentArray;
	}

	public void setCbComponentArray(ArrayList cbComponentArray) {
		this.cbComponentArray = cbComponentArray;
	}

	public void onMouseDown(Widget arg0, int arg1, int arg2) {

	}

	/*public void onMouseEnter(Widget arg0) {
		if(arg0 instanceof Label){
			Label lbl = (Label)arg0;
			if(lbl.getText().equals("GO BACK"))
				lbl.addStyleName("showAllHover");
		}
	}*/

	/*public void onMouseLeave(Widget arg0) {	
		if(arg0 instanceof Label){
			Label lbl = (Label)arg0;
			if(lbl.getText().equals("GO BACK"))
				lbl.removeStyleName("showAllHover");
		}
	}*/

	public void onMouseMove(Widget arg0, int arg1, int arg2) {	
	}

	public void onMouseUp(Widget arg0, int arg1, int arg2) {
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			if(label.getText().equals("GO BACK")){
				TagEvent evt = new TagEvent(this, TagEvent.VIEWSNIPPETS, null);
				AppEventManager.getInstance().fireEvent(evt);
			}
		}	
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof Label){
			Label lbl = (Label)arg0;
			if(lbl.getText().equals("GO BACK"))
				lbl.removeStyleName("showAllHover");
		}
		
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof Label){
			Label lbl = (Label)arg0;
			if(lbl.getText().equals("GO BACK"))
				lbl.addStyleName("showAllHover");
		}
		
	}
}
