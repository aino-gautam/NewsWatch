package com.newscenter.server.categorydb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.SaveOnLogout;
import com.newscenter.server.db.DBHelper;
import com.newscenter.server.exception.CategoryHelperException;

public class CategoryHelper extends DBHelper {
	
	private int userId;
	private int ncid;
	private CategoryHelperException categoryHelperEx;
	private CategoryMap categoryMap = new CategoryMap();
	
	Logger logger = Logger.getLogger(CategoryHelper.class.getName());
	
	public CategoryHelper(){
		super();
		setUserId(-1);
		setNcid(-1);
	}
	
	/**
	 * 
	 * @param ncid - the id of the newscenter into which the user has logged in
	 * @param userid - the id of the logged in user
	 */
	public CategoryHelper(int ncid, int userid) throws CategoryHelperException {
		logger.log(Level.INFO, "[  CategoryHelper -- constructor -- userid :: "+userid+" and ncid :: "+ncid+"]");
		if(userid == -1){ 
			throw categoryHelperEx;
		}
		else{
			try {
				String query = "select * from user where UserId =" + userid;
				Statement stmt = getConnection().createStatement();
				ResultSet rs = stmt.executeQuery(query);
				
				if(rs.next()){
					setUserId(userid);
					setNcid(ncid);
				}
				rs.close();
				stmt.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method is used to get a HashMap of all the categories without any user selections specifid
	 * @return a HashMap of all the categories
	 */
	public CategoryMap getCategories(){
		logger.log(Level.INFO, "[  CategoryHelper -- constructor -- getCategories() initiated ]");
		CategoryMap categoriesmap = new CategoryMap();
		//CategoryMap cmap = new CategoryMap();
		//HashMap categoriesmap = new HashMap();
		CategoryItem categoryitem;
		TagItem tagitem;
		
		try{
			String query1 = "select TagItemId,Name,ParentId,isPrimary from tagitem where IndustryId ="+getNcid()+" and ParentId =(SELECT TagItemId from tagitem where ParentId is null and Name =(SELECT Name from industryenum where IndustryEnumId =(SELECT IndustryEnumId from newscenter where NewsCenterId ="+getNcid()+")))";
			Statement stmt = getConnection().createStatement();
			ResultSet rs1 = stmt.executeQuery(query1);
			
			while(rs1.next()){
				categoryitem = new CategoryItem();
				categoryitem.setTagId(rs1.getInt("TagItemId"));
				categoryitem.setTagName(rs1.getString("Name"));
				categoryitem.setParentId(rs1.getInt("ParentId"));
				categoryitem.setPrimary(rs1.getBoolean("isPrimary"));
				categoryitem.setSelected(false);
				
				int id = rs1.getInt("TagItemId");
				String query2 = "select TagItemId,Name,ParentId,isPrimary from tagitem where ParentId ="+ id;
				stmt = getConnection().createStatement();
				ResultSet rs2 = stmt.executeQuery(query2);
				
				while(rs2.next()){
					tagitem = new TagItem();
					tagitem.setTagId(rs2.getInt("TagItemId"));
					tagitem.setTagName(rs2.getString("Name"));
					tagitem.setParentId(rs2.getInt("ParentId"));
					tagitem.setPrimary(rs1.getBoolean("isPrimary"));
					tagitem.setDirty(false);
					tagitem.setSelected(false);
					tagitem.setCategoryItem(categoryitem);
					categoryitem.addItem(tagitem);
				}
				rs2.close();
				categoriesmap.put(id, categoryitem);
			}
			rs1.close();
			stmt.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		logger.log(Level.INFO, "[  CategoryHelper -- constructor -- getCategories() -- completed-- categorymap size: "+categoriesmap.size()+"  ]");
		return categoriesmap;
	}
	
	/**
	 * This method is used to get a HashMap of all the categories with the logged in user's selections specified
	 * @return a HashMap of all the categories with user selections marked
	 */
	public CategoryMap getUserSelectionCategories(){
		CategoryMap userselectionmap = null;
		try{
			userselectionmap = getCategories();
			userselectionmap.setUserid(getUserId());
			String query = "select distinct t.TagItemId from tagitem t, usertagselection ut where ut.UserId ="+userId+" and ut.TagItemId = t.TagItemId and ut.NewsCenterId = "+getNcid();
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				int id = rs.getInt(1);
				
				for(Object obj : userselectionmap.keySet()){
					if(userselectionmap.containsKey(id)){
						CategoryItem item = (CategoryItem)userselectionmap.get(id);
						item.setSelected(true);
						break;
					}
					else{
						CategoryItem item = (CategoryItem)userselectionmap.get(obj);
						HashMap map = item.getItemMap();
						if(map.containsKey(id)){
							TagItem tag = (TagItem)map.get(id);
							tag.setSelected(true);
						}
					}
				}
			}
			rs.close();
			stmt.close();
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return userselectionmap;
	}
		
	
	/**
	 * This method is used to update a single TagItem based on the user modification
	 * @param tagItem - the particular tagItem to be updated
	 * @param selectionStatus - specifies whether the tag has been selected / deselected
	 */
	public void updateUserItemSelection(TagItem tagItem, boolean selectionStatus){
		try{
			int tagid = tagItem.getTagId();
			if(selectionStatus){
				String query = "insert into usertagselection(UserId,TagItemId,NewsCenterId) values(" +userId+ "," +tagid+ "," +this.ncid+ ")";
				Statement stmt = getConnection().createStatement();
				stmt.executeUpdate(query);
				stmt.close();
			}
			else{
				String query = "delete from usertagselection where TagItemId=" +tagid+ " and UserId=" +userId+ " and NewsCenterId=" +this.ncid;
				Statement stmt = getConnection().createStatement();
				stmt.executeUpdate(query);
				stmt.close();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * This method is used to update a list of TagItems which have been modified by the user.It checks the selection
	 * status of each tag and inserts/deletes in the usertagselection table.
	 * @param tagList - an ArrayList of all TagItems which have been modified
	 */
	public void updateUserItemSelection(ArrayList tagList){
		Iterator iter = tagList.iterator();
		String query1 = "insert into usertagselection(UserId,TagItemId,NewsCenterId) values(";
		String query2 = "delete from usertagselection where UserId=" +userId+ " and NewsCenterId=" +this.ncid+" and TagItemId in(0";
		while(iter.hasNext()){
			TagItem tagitem = (TagItem)iter.next();
			int tagid = tagitem.getTagId();
		
			if(tagitem.isSelected()){
				query1 += userId+ "," +tagid+ "," +this.ncid+ "),(";
			}
			else{
				query2 += "," + tagid;
			}
		}
		query1 = query1.substring(0, query1.length()-2);
		query2+=")";
		try{
			Statement stmt = getConnection().createStatement();
			stmt.executeUpdate(query1);
			stmt.executeUpdate(query2);
			stmt.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method is used to update an entire CategoryMap based on user modifications. It will first call a
	 * method of CategoryMap to get the dirty tags from the map.This method will internally parse the categorymap
	 * and return a list of tags which have been modified (selected / deselected) and then these tags will be updated
	 * in the db.
	 * @param categoryMap - a HashMap of CategoryItems which contain a map of their respective TagItems
	 */
	public void updateUserItemSelection(CategoryMap categoryMap){
		//ArrayList dirtyList = categoryMap.getDirtyTags();
		SaveOnLogout savethread = new SaveOnLogout();
		savethread.setNCID(getNcid());
		savethread.setUserId(userId);
		//savethread.setSavingList(dirtyList);]
		savethread.setSavingMap(categoryMap);
		
		savethread.start();
		/*Iterator iter = dirtyList.iterator();
		while(iter.hasNext()){
			TagItem tag = (TagItem)iter.next();
			int tagid = tag.getTagId();
			try{
				Statement stmt = getConnection().createStatement();
				if(tag.isSelected()){
					String query1 = "select * from usertagselection where TagItemId=" +tagid+ " and UserId=" +userId+ " and NewsCenterId=" +getNcid();
					ResultSet rs1 = stmt.executeQuery(query1);
					if(!rs1.next()){					
						String query = "insert into usertagselection(UserId,TagItemId,NewsCenterId) values(" +userId+ "," +tagid+ "," +getNcid()+ ")";
						Statement stmt1 = getConnection().createStatement();
						stmt1.executeUpdate(query);
						stmt1.close();
					}
					rs1.close();
				}
				else{
					String query = "delete from usertagselection where TagItemId=" +tagid+ " and UserId=" +userId+ " and NewsCenterId=" +getNcid();
					Statement stmt1 = getConnection().createStatement();
					stmt1.executeUpdate(query);
					stmt1.close();
				}
				stmt.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			
		}*/
	}
	
	/**
	 * This method is used to refresh the user selection of TagItems in the db. The entire user selection for that
	 * particular user and ncid is deleted and the entries are made all over again.For a method of CategoryMap will
	 * be called which will find all the selected tags and return an arraylist of selected tags.
	 * @param categoryMap - a HashMap of CategoryItems which contain a map of their respective TagItems
	 * whose selection / deselection has been updated
	 */
	public void refreshUserSelection(CategoryMap categoryMap){
		ArrayList selectionList = categoryMap.getSelectedTags();
		
		try{
			String query = "delete from usertagselection where UserId =" +userId;
			Statement stmt = getConnection().createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public CategoryMap getAllUserSelection(){
		CategoryMap userselectionmap = null;
		try{
			userselectionmap = getCategories();
			userselectionmap.setUserid(getUserId());
			String query = "select distinct t.TagItemId, ut.TagSelectionStatus from tagitem t, usertagselection ut where ut.UserId ="+userId+" and ut.TagItemId = t.TagItemId and ut.NewsCenterId = "+getNcid();
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				int id = rs.getInt("TagItemId");
				boolean status = rs.getBoolean("TagSelectionStatus");
				
				for(Object obj : userselectionmap.keySet()){
					if(userselectionmap.containsKey(id)){
						CategoryItem item = (CategoryItem)userselectionmap.get(id);
						item.setSelected(true);
						break;
					}
					else{
						CategoryItem item = (CategoryItem)userselectionmap.get(obj);
						HashMap map = item.getItemMap();
						if(map.containsKey(id)){
							TagItem tag = (TagItem)map.get(id);
							tag.setSelected(true);
							tag.setTagSelectionStatus(status);
						}
					}
				}
			}
			rs.close();
			stmt.close();
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		userselectionmap.getSelectedTags();
		return userselectionmap;
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getNcid() {
		return ncid;
	}

	public void setNcid(int ncid) {
		this.ncid = ncid;
	}

	public CategoryMap getCategoryMap() {
		return categoryMap;
	}

	public void setCategoryMap(CategoryMap categoryMap) {
		this.categoryMap = categoryMap;
	}
}
