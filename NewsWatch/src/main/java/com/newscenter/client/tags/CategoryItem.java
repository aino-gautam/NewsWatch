package com.newscenter.client.tags;

import java.util.HashMap;

/**
 * This class is used to represent a category of the db. It is a type of tag and hence it derives from
 * the TagItem class. Additionally it contains a HashMap of its child items i.e. a map of all tags which
 * come under a particular category.
 *
 */
public class CategoryItem extends TagItem {
	
	private int indexId;
	
	private HashMap<Integer, TagItem> itemMap = new HashMap<Integer, TagItem>();
	
	public CategoryItem(){
		
	}

	/**
	 * This methods iterates the itemMap of the categoryItem on which this method has been called on and sets 
	 * all the tags of the itemMap as selected or deselected based on the selection parameter passed.
	 * @param selection - true if all the tags under a particular category need to be set as selected and false
	 * in case all the tags under a particular category need to be set as deselected.
	 */
	public void setAllChildrenSelected(boolean selection){
		if(selection){
			for(Object obj : this.getItemMap().keySet()){
				TagItem tag = (TagItem)itemMap.get(obj);
				tag.setSelected(true);
				tag.setDirty(true);
			}
		}
		else{
			for(Object obj : this.getItemMap().keySet()){
				TagItem tag = (TagItem)itemMap.get(obj);
				tag.setSelected(false);
				tag.setDirty(true);
			}
		}
	}
	
	/**
	 * This method iterates the itemMap of the categoryItem on which it has been called to check whether
	 * all the tags in the map are set as selected. This method is used whenever we want to check if all the
	 * tags under a particular category are selected or not.
	 * @return true  if all tags are selected. If even one tag selection is false, it returns false.
	 */
	public boolean areAllChildrenSelected(){
		for(Object obj : itemMap.keySet()){
			TagItem tag = (TagItem)itemMap.get(obj);
			if(!tag.isSelected()){
				return false;
			}
		}
		return true;
	}
	public void addItem(TagItem tagitem){
		itemMap.put(tagitem.getTagId(), tagitem);
	}
	
	public HashMap<Integer, TagItem> getItemMap() {
		return itemMap;
	}

	public void setItemMap(HashMap<Integer, TagItem> itemMap) {
		this.itemMap = itemMap;
	}

	public int getIndexId() {
		return indexId;
	}

	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}
}
