package com.newscenter.client.tags;

import java.util.HashMap;

public class TagCategory extends TagItem 
{

	private HashMap items = new HashMap();
	
	public HashMap getTagItems(){
		return items ;
	}
	
	public void addItem(TagItem item){
		
		items.put(item.getTagId(),item);
	}
	
	public TagCategory clone()
	{
		TagCategory tagcategory = new TagCategory();
		tagcategory.clone();
		return this;
	}
	
}
