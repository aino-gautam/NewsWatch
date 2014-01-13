package com.newscenter.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.FlexTable;

import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.TagEvent;
import com.newscenter.client.events.TagEventListener;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.TagItem;

public class SnippetDetail extends FlexTable implements TagEventListener {
	
	private SnippetView snippetRef;
	private int visibleTags = 6;
	private FlexCellFormatter formatter;
	private CheckBoxComponent cbComponent;
	private ArrayList cbComponentArray;
	
	public SnippetDetail(SnippetView view){
		AppEventManager.getInstance().addTagEventListener(this);
		cbComponentArray = new ArrayList();
		setSnippetRef(view);
		setCellPadding(1);
		setCellSpacing(0);
		CategoryItem item = getSnippetRef().getCategory();
		int indx = 0;
		int row = 2;
		int col = 0;
		if(item.getItemMap().size() != 0){
		if(!item.areAllChildrenSelected()){
			cbComponent = new CheckBoxComponent(this);
			cbComponent.addComponent(new CheckBoxImage(cbComponent, false), item, "SELECT ALL");
			formatter = getFlexCellFormatter();
			formatter.setHeight(0, 0, "27px");
			setWidget(0, 0, cbComponent);
			cbComponentArray.add(cbComponent);
		}
		else{
			cbComponent = new CheckBoxComponent(this);
			cbComponent.addComponent(new CheckBoxImage(cbComponent,true), item, "SELECT ALL");
			formatter = getFlexCellFormatter();
			formatter.setHeight(0, 0, "35px");
			setWidget(0, 0, cbComponent);
			cbComponentArray.add(cbComponent);
			//setWidget(0, 0, createLabel(item,"Deselect All"));
		}
		}
		int temp = getVisibleTags();
		HashMap<Integer, TagItem> map = item.getItemMap();
		for(Object ob : map.keySet()){
			if(indx < temp){
				TagItem tag = (TagItem)map.get(ob);
				cbComponent = new CheckBoxComponent(this);
				if(tag.isSelected()){
					cbComponent.addComponent(new CheckBoxImage(cbComponent,true),tag);
				}
				else{
					cbComponent.addComponent(new CheckBoxImage(cbComponent,false),tag);
				}
				setWidget(row, col, cbComponent);
				cbComponentArray.add(cbComponent);
				row++;
				indx++;
			}
			else{
				cbComponent = new CheckBoxComponent(this);
				cbComponent.addComponent(null,item,"SHOW ALL");
				formatter = getFlexCellFormatter();
				formatter.setHeight(row, col, "25px");
				setWidget(row, col, cbComponent);
				cbComponentArray.add(cbComponent);
				break;
			}
		}
	}

	public SnippetView getSnippetRef() {
		return snippetRef;
	}

	public void setSnippetRef(SnippetView snippetRef) {
		this.snippetRef = snippetRef;
	}

	public int getVisibleTags() {
		return visibleTags;
	}

	public void setVisibleTags(int visibleTags) {
		this.visibleTags = visibleTags;
	}

	public boolean onEvent(TagEvent evt) {
		int evttype = evt.getEventType();
		if(evttype == TagEvent.DESELECTCATEGORYITEM){
			if(evt.getSource() instanceof SnippetDetail){
				SnippetDetail sd = (SnippetDetail)evt.getSource();
				Iterator iter = sd.cbComponentArray.iterator();
				while(iter.hasNext()){
					CheckBoxComponent cbcomp = (CheckBoxComponent)iter.next();
					ItemTagLabel lbl = cbcomp.getItemLabel();
					if((!lbl.getText().equals("SHOW ALL"))){
						if(!lbl.getText().equals("SELECT ALL"))
						{
							lbl.setSelected(false);
							lbl.setClicked(false);
							lbl.getCbImage().uncheckImage();
						}
					}
				}
				CategoryItem catitem = (CategoryItem)evt.getEventData();
				catitem.setAllChildrenSelected(false);
				TagEvent event = new TagEvent(this, TagEvent.CATEGORYITEMDESELECTED, catitem);
				AppEventManager.getInstance().fireEvent(event);
				return true;
			}
		}
		else if(evttype == TagEvent.SELECTCATEGORYITEM){
			if(evt.getSource() instanceof SnippetDetail){
				SnippetDetail sd = (SnippetDetail)evt.getSource();
				Iterator iter = sd.cbComponentArray.iterator();
				while(iter.hasNext()){
					CheckBoxComponent cbcomp = (CheckBoxComponent)iter.next();
					ItemTagLabel lbl = cbcomp.getItemLabel();
					if(!lbl.getText().equals("SHOW ALL")){
						if(!lbl.getText().equals("SELECT ALL"))
						{
							lbl.setSelected(true);
							lbl.setClicked(true);
							lbl.getCbImage().checkImage();
						}
					}
				}
				CategoryItem citem = (CategoryItem)evt.getEventData();
				citem.setAllChildrenSelected(true);				
				TagEvent event = new TagEvent(this, TagEvent.CATEGORYITEMSELECTED, citem);
				AppEventManager.getInstance().fireEvent(event);
				return true;
			}
		}
		/*else if(evttype == TagEvent.CLEARTAGS){
			Iterator iter = this.cbComponentArray.iterator();
			while(iter.hasNext()){
				CheckBoxComponent cbcomp = (CheckBoxComponent)iter.next();
				ItemTagLabel lbl = cbcomp.getItemLabel();
				if((!lbl.getText().equals("SHOW ALL"))){
					lbl.setSelected(false);
					lbl.setClicked(false);
					lbl.getCbImage().uncheckImage();
				}
			}
		}*/
		return false;
	}

	public void clearTags(){
		Iterator iter = this.cbComponentArray.iterator();
		while(iter.hasNext()){
			CheckBoxComponent cbcomp = (CheckBoxComponent)iter.next();
			ItemTagLabel lbl = cbcomp.getItemLabel();
			if((!lbl.getText().equals("SHOW ALL"))){
				lbl.setSelected(false);
				lbl.setClicked(false);
				lbl.getCbImage().uncheckImage();
			}
		}
	}
	public ArrayList getCbComponentArray() {
		return cbComponentArray;
	}

	public void setCbComponentArray(ArrayList cbComponentArray) {
		this.cbComponentArray = cbComponentArray;
	}
}
