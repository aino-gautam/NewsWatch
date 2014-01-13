package com.newscenter.client.tags;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryMap extends HashMap {

	private static final long serialVersionUID = 1L;
	private int ncid;
	private int userId;
	
	public CategoryMap(){
		ncid = -1;
		userId = -1;
	}
	
	/**
	 * Sets the newscenterid and userid to associate a particular CategoryMap for a particular user and newscenter
	 * @param ncid - the newscenterid into which the user has currently logged in
	 * @param userid - the id of the logged in user
	 */
	public CategoryMap(int ncid, int userid){
		this.ncid = ncid;
		userId = userid;
	}
	
	public CategoryMap(int ncid){
		this.ncid = ncid;
	}
	
	/**
	 * 
	 * @param cMap - a CategoryMap of all tags and those tags selected by the user marked as selected. The method 
	 * will find all such tags from the map and fill them in an arraylist.
	 * @return an arraylist of all the tags selected by the user
	 */
	public ArrayList getSelectedTags(){
		ArrayList selectedTagList = new ArrayList();
		for(Object obj : this.keySet()){
			CategoryItem citem = (CategoryItem)this.get(obj);
			HashMap map = citem.getItemMap();
			for(Object o : map.keySet()){
				TagItem tagitem = (TagItem)map.get(o);
				if(tagitem.isSelected()){
					selectedTagList.add(tagitem);
				}
			}
		}
		return selectedTagList;
	}
	
	public ArrayList getSelectedTagsByCategory(){
		ArrayList selectedTagList = new ArrayList();
		ArrayList taglist;
		
		for(Object obj : this.keySet()){
			taglist = new ArrayList();
			CategoryItem citem = (CategoryItem)this.get(obj);
			HashMap map = citem.getItemMap();
			for(Object o : map.keySet()){
				TagItem tagitem = (TagItem)map.get(o);
				if(tagitem.isSelected()){
					taglist.add(tagitem);
				}
			}
			if(taglist.size() != 0)
				selectedTagList.add(taglist);
		}
		return selectedTagList;
	}
	
	/**
	 * 
	 * @param cMap - a CategoryMap of all tags and those tags modified by the user as 'dirty'. The method will find
	 * all such tags from the map and fill them in an arraylist.
	 * @return an arraylist of TagItems which have been modified i.e marked as dirty
	 */
	public ArrayList getDirtyTags(){
		ArrayList dirtyTagList = new ArrayList();
		for(Object obj : this.keySet()){
			CategoryItem citem = (CategoryItem)this.get(obj);
			if(citem.isDirty())
				dirtyTagList.add(citem);
			HashMap map = citem.getItemMap();
			for(Object o : map.keySet()){
				TagItem tagitem = (TagItem)map.get(o);
				if(tagitem.isDirty()){
					dirtyTagList.add(tagitem);
				}
			}
		
		}
		return dirtyTagList;
	}

	
	/**
	 * 
	 * @return the newscenter id associated with a particular CategoryMap instance
	 */
	public int getNcid() {
		return ncid;
	}

	
	/**
	 * Sets the ncid of a particular CategoryMap instance
	 * @param ncid - current newscenter id
	 */
	public void setNcid(int ncid) {
		this.ncid = ncid;
	}

	
	/**
	 * 
	 * @return the user id associated with a particular CategoryMap instance
	 */
	public int getUserid() {
		return userId;
	}
	

	/**
	 * Sets the userid of a particular CategoryMap instance
	 * @param userid - the id of the logged in user
	 */
	public void setUserid(int userid) {
		userId = userid;
	}

}
