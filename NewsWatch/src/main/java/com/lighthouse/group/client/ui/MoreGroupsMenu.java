package com.lighthouse.group.client.ui;

import java.util.HashMap;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.lighthouse.group.client.domain.Group;

public class MoreGroupsMenu extends MenuBar{

	private HashMap<String, Group> hmMenuItems = new HashMap<String, Group>();
	private HashMap<Group, MenuItem> hmGroupMenuItems = new HashMap<Group, MenuItem>();
	
	public MoreGroupsMenu(boolean vertical, Resources resources){
		super(vertical, resources);
	}
	
	public MoreGroupsMenu(boolean vertical){
		super(vertical);
	}
	
	public MenuItem addItem(String text,Group group,Command cmd){
	    MenuItem item = super.addItem(text, cmd);
	    hmMenuItems.put(text, group);
	    hmGroupMenuItems.put(group, item);
		return item;
	}
	
	public Group getGroup(MenuItem menuitem){
		Group group = hmMenuItems.get(menuitem.getText());
		return group;
	}
	
	public void setGroup(Group group, String text){
		hmMenuItems.put(text, group);
	}
	
	public void removeGroup(String text){
		Group group = hmMenuItems.get(text);
		hmMenuItems.remove(text);
		hmGroupMenuItems.remove(group);
	}
	
	public MenuItem getGroupMenuItem(Group group){
		return hmGroupMenuItems.get(group);
	}
	
	public void setGroupMenuItem(Group group, MenuItem menuItem){
		hmGroupMenuItems.put(group, menuItem);
	}
	
	public MenuItem getSelectedItem(){
		return super.getSelectedItem();
		
	}
}
