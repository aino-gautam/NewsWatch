package com.lighthouse.group.server.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.internal.compiler.lookup.InferenceContext;

import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.client.exception.GroupException;
import com.lighthouse.group.server.LhSaveOnLogout;

import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.categorydb.CategoryHelper;
import com.newscenter.server.db.DBHelper;
import com.newscenter.server.exception.CategoryHelperException;

/**
 * 
 * @author prachi@ensarm.com This class has the data structure which holds the
 *         entire groupCategoryMap .
 * 
 */
public class GroupHelper extends DBHelper {

	private int groupId;
	private int userId;
	private int ncid;
	private GroupException grpException;

	Logger logger = Logger.getLogger(GroupHelper.class.getName());

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

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public GroupHelper() {
		super();
		this.setNcid(-1);
		this.setUserId(-1);
		this.setGroupId(-1);
	}

	public GroupHelper(int ncid, int userid, int groupid) throws GroupException {
		if (this.getNcid() == -1 || this.getUserId() == -1
				|| this.getGroupId() == -1) {
			throw grpException;
		} else {

			setNcid(ncid);
			setUserId(userid);
			setGroupId(groupid);
		}

	}

	/**
	 * gets a list of groups for a user for the catalyst
	 * @param userId - id of the logged in user
	 * @param ncid - id of the catalyst
	 * @return List<Group>
	 */
	public List<Group> getAvailableGroups(int userId, int ncid) throws SQLException{
		logger.log(Level.INFO, "[ GroupHepler --- getAvailableGroups() initiated for USERID:: "+userId+" and NCID:: "+ncid+" ]");
		List<Group> groupList = new ArrayList<Group>();
		List<Group> manGroupList = new ArrayList<Group>();
		List<Group> userGroupList = new ArrayList<Group>();
		Connection connection=null;
		try {
			connection = (Connection) getConnection();
			Integer favGroupPermitted = isFavoriteGroupPermitted(userId, ncid);
			if(favGroupPermitted == 1){
				logger.log(Level.INFO, "[ GroupHepler --- getAvailableGroups() fetching favorite group for USERID:: "+userId+" and NCID:: "+ncid+" ]");
				String favGroupQuery = "SELECT g.* FROM `group` g where g.isFavorite=1 and g.newsCenterId="+ ncid + " and g.userId=" +userId;
				Statement stmt2 = connection.createStatement();
				ResultSet rs2 = stmt2.executeQuery(favGroupQuery);
				while(rs2.next()){
					Group group = new Group();
					group.setGroupId(rs2.getInt("groupId"));
					group.setGroupName(rs2.getString("groupName"));
					group.setIsMandatory(rs2.getInt("isMandatory"));
					group.setGroupParentId(rs2.getInt("groupParentId"));
					group.setNewsCenterId(rs2.getInt("newsCenterId"));
					group.setUserId(rs2.getInt("userId"));
					group.setNewsFilterMode(rs2.getInt("newsFilterMode"));
					group.setIsDefaultGroup(rs2.getInt("isDefaultGroup"));
					group.setIsFavorite(rs2.getInt("isFavorite"));
					groupList.add(group);
				}
				rs2.close();
				stmt2.close();
			}
			logger.log(Level.INFO, "[ GroupHepler --- getAvailableGroups() fetching mandatory groups for NCID::"+ncid+" ]");
			String mandatoryGrpsQuery = "SELECT g.* FROM `group` g where isMandatory=1 and newscenterid="+ ncid +" order by groupname";
			Statement stmt1 = connection.createStatement();
			ResultSet rs1 = stmt1.executeQuery(mandatoryGrpsQuery);
			while (rs1.next()) {
				Group group = new Group();
				group.setGroupId(rs1.getInt("groupId"));
				group.setGroupName(rs1.getString("groupName"));
				group.setIsMandatory(rs1.getInt("isMandatory"));
				group.setGroupParentId(rs1.getInt("groupParentId"));
				group.setNewsCenterId(rs1.getInt("newsCenterId"));
				group.setUserId(rs1.getInt("userId"));
				group.setNewsFilterMode(rs1.getInt("newsFilterMode"));
				group.setIsDefaultGroup(rs1.getInt("isDefaultGroup"));
				manGroupList.add(group);
			}
			rs1.close();
			stmt1.close();
			logger.log(Level.INFO, "[ GroupHepler --- getAvailableGroups() mandatory groups fetched :: "+manGroupList.size()+" for NCID::"+ncid+" ]");
			groupList.addAll(manGroupList);
			
			// Step 1: fetch the list of groups for the user
			logger.log(Level.INFO, "[ GroupHepler --- getAvailableGroups() fetching user groups for USERID:: "+userId+" and NCID:: "+ncid+" ]");
			String grpQueryList = "SELECT g.* FROM `group` g where isMandatory=0 and isFavorite=0 and userid=" + userId + " and newscenterid="+ ncid + " order by groupname";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(grpQueryList);
			while (rs.next()) {
				Group group = new Group();
				group.setGroupId(rs.getInt("groupId"));
				group.setGroupName(rs.getString("groupName")); 
				group.setIsMandatory(rs.getInt("isMandatory"));
				group.setGroupParentId(rs.getInt("groupParentId"));
				group.setNewsCenterId(rs.getInt("newsCenterId"));
				group.setUserId(rs.getInt("userId"));
				group.setNewsFilterMode(rs.getInt("newsFilterMode"));
				group.setIsDefaultGroup(rs.getInt("isDefaultGroup"));
				userGroupList.add(group);
			}
			rs.close();
			stmt.close();
			logger.log(Level.INFO, "[ GroupHepler --- getAvailableGroups() user groups fetched::"+userGroupList.size()+" for USERID:: "+userId+" and NCID:: "+ncid+" ]");
			groupList.addAll(userGroupList);
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "GroupHelper ::: Unable to fetch the groupList:: EXCEPTION!!!:: getAvailableGroups()"+e.getMessage());  
		}
		logger.log(Level.INFO, "[ GroupHepler --- getAvailableGroups() total grouplist size::"+groupList.size()+" for USERID:: "+userId+" and NCID:: "+ncid+" ]");
		return groupList;
	}
	
	/**
	 * returns a blank group category map (without any selections)
	 * @param userId
	 * @param ncid
	 * @return
	 */
	public GroupCategoryMap getBlankGroupCategoryMap(int userId, int ncid){
		logger.log(Level.INFO, "[ GroupHepler --- getBlankGroupCategoryMap() inititated for USERID:: "+userId+" and NCID:: "+ncid+" ]");
		try {
			CategoryHelper categoryHelper = new CategoryHelper(ncid,userId);
			CategoryMap categoryMap = categoryHelper.getCategories();
			int index = 1;
			for(Object object : categoryMap.keySet()){
				CategoryItem categoryItem = (CategoryItem)categoryMap.get(object);
				if(!categoryItem.isPrimary()){
					categoryItem.setIndexId(index);
					index++;
				}
			}
			GroupCategoryMap groupCategoryMap = new GroupCategoryMap();
			for(Object object : categoryMap.keySet()){
				CategoryItem categoryItem = (CategoryItem)categoryMap.get(object);
				if(!categoryItem.isPrimary())
					groupCategoryMap.put(object, categoryItem);
			}
			logger.log(Level.INFO, "[ GroupHepler --- getBlankGroupCategoryMap() completed for USERID:: "+userId+" and NCID:: "+ncid+" ]");
			categoryHelper.closeConnection();
			return groupCategoryMap;
		} catch (CategoryHelperException e) {
			logger.log(Level.INFO, "[ GroupHepler --- getBlankGroupCategoryMap() EXCEPTION!! returning NULL:: for USERID:: "+userId+" and NCID:: "+ncid+" ]");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method gives the group information of the user logged in with its
	 * ncid and userid.
	 * 
	 * @param userId
	 * @param ncid
	 * @return
	 */
	public List<Group> getGroupList(int userId, int ncid) {
		logger.log(Level.INFO, "[ GroupHepler --- getGroupList() inititated for USERID:: "+userId+" and NCID:: "+ncid+" ]");
		List<Group> groupList = new ArrayList<Group>();
		List<Group> manGroupList = new ArrayList<Group>();
		List<Group> userGroupList = new ArrayList<Group>();
		Connection connection = (Connection) getConnection();
		try {
			Integer favGroupPermitted = isFavoriteGroupPermitted(userId, ncid);
			if(favGroupPermitted == 1){
				logger.log(Level.INFO, "[ GroupHepler --- getGroupList() fetching favorite group for USERID:: "+userId+" and NCID:: "+ncid+" ]");
				String favGroupQuery = "SELECT g.* FROM `group` g where g.isFavorite=1 and g.newsCenterId="+ ncid + " and g.userId=" +userId;
				Statement stmt2 = connection.createStatement();
				ResultSet rs2 = stmt2.executeQuery(favGroupQuery);
				while(rs2.next()){
					Group group = new Group();
					group.setGroupId(rs2.getInt("groupId"));
					group.setGroupName(rs2.getString("groupName"));
					group.setIsMandatory(rs2.getInt("isMandatory"));
					group.setGroupParentId(rs2.getInt("groupParentId"));
					group.setNewsCenterId(rs2.getInt("newsCenterId"));
					group.setUserId(rs2.getInt("userId"));
					group.setNewsFilterMode(rs2.getInt("newsFilterMode"));
					group.setIsDefaultGroup(rs2.getInt("isDefaultGroup"));
					group.setIsFavorite(rs2.getInt("isFavorite"));
					groupList.add(group);
				}
				rs2.close();
				stmt2.close();
			}
			logger.log(Level.INFO, "[ GroupHepler --- getGroupList() fetching Mandatory groups for NCID:: "+ncid+" ]");
			String grpMandatoryListQuery = "SELECT g.* FROM `group` g where g.isMandatory=1 and g.newsCenterId="+ncid;
			Statement stmt1 = connection.createStatement();
			ResultSet rs1 = stmt1.executeQuery(grpMandatoryListQuery);
			while (rs1.next()) {
				Group group = new Group();
				group.setGroupId(rs1.getInt("groupId"));
				group.setGroupName(rs1.getString("groupName"));
				group.setIsMandatory(rs1.getInt("isMandatory"));
				group.setGroupParentId(rs1.getInt("groupParentId"));
				group.setNewsCenterId(rs1.getInt("newsCenterId"));
				group.setUserId(rs1.getInt("userId"));
				group.setNewsFilterMode(rs1.getInt("newsFilterMode"));
				group.setIsDefaultGroup(rs1.getInt("isDefaultGroup"));
				manGroupList.add(group);
			}
			rs1.close();
			stmt1.close();
			
			logger.log(Level.INFO, "[ GroupHepler --- getGroupList() mandatory groups fetched :: "+manGroupList.size()+" for NCID::"+ncid+" ]");
			groupList.addAll(manGroupList);
			
			//For custom groups
			logger.log(Level.INFO, "[ GroupHepler --- getGroupList() fetching user groups for USERID:: "+userId+" and NCID:: "+ncid+" ]");
			String userGroupsQuery = "SELECT g.* FROM `group` g where g.isMandatory=0 and g.isFavorite=0 and g.newsCenterId="+ ncid + " and g.userId=" +userId;
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(userGroupsQuery);
			while (rs.next()) {
				Group group = new Group();
				group.setGroupId(rs.getInt("groupId"));
				group.setGroupName(rs.getString("groupName"));
				group.setIsMandatory(rs.getInt("isMandatory"));
				group.setGroupParentId(rs.getInt("groupParentId"));
				group.setNewsCenterId(rs.getInt("newsCenterId"));
				group.setUserId(rs.getInt("userId"));
				group.setNewsFilterMode(rs.getInt("newsFilterMode"));
				group.setIsDefaultGroup(rs.getInt("isDefaultGroup"));
				userGroupList.add(group);
			}
			rs.close();
			stmt.close();
			logger.log(Level.INFO, "[ GroupHepler --- getGroupList() user groups fetched::"+userGroupList.size()+" for USERID:: "+userId+" and NCID:: "+ncid+" ]");
			
			groupList.addAll(userGroupList);
			
			// Step 2: fetch all categories for the ncid
			CategoryHelper categoryHelper = new CategoryHelper(ncid,userId);
			
			// Step 3: populate groupcategorymap
			for (Group group : groupList) {
				CategoryMap categoryMap = categoryHelper.getCategories();
				int index = 1;
				for(Object object : categoryMap.keySet()){
					CategoryItem categoryItem = (CategoryItem)categoryMap.get(object);
					if(!categoryItem.isPrimary()){
						categoryItem.setIndexId(index);
						index++;
					}
				}
				GroupCategoryMap groupCategoryMap = getGroupCategoryMap(group, categoryMap);
				group.setGroupCategoryMap(groupCategoryMap);
			}
			categoryHelper.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "GroupHelper ::: Unable to fetch the groupList ::EXCEPTION!!! getGroupList() "+e.getMessage());  
		}
		logger.log(Level.INFO, "[ GroupHepler --- getGroupList() total grouplist size::"+groupList.size()+" for USERID:: "+userId+" and NCID:: "+ncid+" ]");
		return groupList;
	}

	/**
	 * gets the GroupCategoryMap for the specific groupId
	 * 
	 * @param Group 
	 * @param categoryMap 
	 * @return GroupCategoryMap
	 */
	public GroupCategoryMap getGroupCategoryMap(Group group,CategoryMap categoryMap) {
		logger.log(Level.INFO, "[ GroupHelper :: getGroupCategoryMap() initiated for GROUPID :: "+group.getGroupId()+"]");
		GroupCategoryMap groupCategoryMap = new GroupCategoryMap();
		groupCategoryMap.setGroupId(group.getGroupId());
		for(Object object : categoryMap.keySet()){
			CategoryItem categoryItem = (CategoryItem)categoryMap.get(object);
			if(!categoryItem.isPrimary())
				groupCategoryMap.put(object, categoryItem);
		}
		

		try {
			Connection connection = (Connection) getConnection();
			if (group.getIsMandatory() == 1) {
				String query = "select distinct t.TagItemId from tagitem t,usergrouptagselection ut where ut.TagItemId = t.TagItemId and ut.NewsCenterId = "+ group.getNewsCenterId()+ " and ut.groupId = " + group.getGroupId() + "";
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				// if categoryMap contains the id then set categoryItem as
				// selected
				// else fetch the itemMap of the categoryItem, search for the id
				// in the map and set that tagITem as selected
				while (rs.next()) {
					int id = rs.getInt(1);
					for (Object obj : groupCategoryMap.keySet()) {
						if (groupCategoryMap.containsKey(id)) {
							CategoryItem item = (CategoryItem) groupCategoryMap.get(id);
							item.setSelected(true);
							item.setAllChildrenSelected(true);
							break;
						} else {
							CategoryItem item = (CategoryItem) groupCategoryMap.get(obj); // tags in a particular category are selected.
							HashMap map = item.getItemMap();
							if (map.containsKey(id)) {
								TagItem tag = (TagItem) map.get(id);
								tag.setSelected(true);
							}
						}
					}
				}
				rs.close();
				stmt.close();
			}
			else {
				// if categoryMap contains the id then set categoryIem as selected
				// else fetch the itemMap of the categoryItem, search for the id
				// in the map and set that tagITem as selected
				 String query = "select distinct t.TagItemId from tagitem t,usergrouptagselection ut where ut.UserId = " + group.getUserId() + " " +
				 		"and ut.TagItemId= t.TagItemId and ut.NewsCenterId =  "+ group.getNewsCenterId()+ " and ut.groupId = " + group.getGroupId() + "";
				 Statement stmt = connection.createStatement();
				 ResultSet rs = stmt.executeQuery(query);
					while (rs.next()) {
						int id = rs.getInt(1);

						for (Object obj : groupCategoryMap.keySet()) {
							if (groupCategoryMap.containsKey(id)) {
								CategoryItem item = (CategoryItem) groupCategoryMap.get(id);
								item.setSelected(true);
								item.setAllChildrenSelected(true);
								break;
							} else {
								CategoryItem item = (CategoryItem) groupCategoryMap.get(obj); // tags in a particular category are selected.
								HashMap map = item.getItemMap();
								if (map.containsKey(id)) {
									TagItem tag = (TagItem) map.get(id);
									tag.setSelected(true);
								}
							}
						}
					}
					rs.close();
					stmt.close();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "GroupHelper :: EXCEPTION in getGroupCategoryMap()for GROUPID ::"+group.getGroupId()+ " :: "+e.getMessage());
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[ GroupHelper :: getGroupCategoryMap() completed for GROUPID :: "+group.getGroupId()+"]");
		return groupCategoryMap;
	}
	
	/**
	 * gets a groupCategoryMap with user selections marked
	 * @param group - the group for which the groupCategoryMap has to be fetched
	 * @param categoryMap
	 * @return GroupCategoryMap
	 */
	public GroupCategoryMap getGroupCategoryMapWithSelections(Group group,CategoryMap categoryMap) {
		logger.log(Level.INFO, "[ GroupHelper :: getGroupCategoryMapWithSelections() initiated for GROUPID :: "+group.getGroupId()+"]");
		//categoryMap.getSelectedTags().size();
		GroupCategoryMap groupCategoryMap = new GroupCategoryMap();
		groupCategoryMap.setGroupId(group.getGroupId());
		groupCategoryMap.putAll(categoryMap);

		try {
			Connection connection = (Connection) getConnection();
			if (group.getIsMandatory() == 1) {
				String query = "select distinct t.TagItemId from tagitem t,usergrouptagselection ut where ut.TagItemId = t.TagItemId and ut.NewsCenterId = "+ group.getNewsCenterId()+ " and ut.groupId = " + group.getGroupId() + "";
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				// if categoryMap contains the id then set categoryItem as
				// selected
				// else fetch the itemMap of the categoryItem, search for the id
				// in the map and set that tagITem as selected
				while (rs.next()) {
					int id = rs.getInt(1);
			
					for (Object obj : groupCategoryMap.keySet()) {
						if (groupCategoryMap.containsKey(id)) {
							CategoryItem item = (CategoryItem) groupCategoryMap.get(id);
							item.setSelected(true);
							break;
						} else {
							CategoryItem item = (CategoryItem) groupCategoryMap.get(obj);   // tags in a particular category are selected.
							HashMap map = item.getItemMap();
							if (map.containsKey(id)) {
								TagItem tag = (TagItem) map.get(id);
								tag.setSelected(true);
							}
						}
					}
				}
				rs.close();
				stmt.close();
			}
			else {
				// if categoryMap contains the id then set categoryIem as selected
				// else fetch the itemMap of the categoryItem, search for the id
				// in the map and set that tagITem as selected
				 String query = "select distinct t.TagItemId from tagitem t,usergrouptagselection ut where ut.UserId = " + group.getUserId() + " " +
				 		"and ut.TagItemId= t.TagItemId and ut.NewsCenterId =  "+ group.getNewsCenterId()+ " and ut.groupId = " + group.getGroupId() + "";
				 Statement stmt = connection.createStatement();
				 ResultSet rs = stmt.executeQuery(query);
					while (rs.next()) {
						int id = rs.getInt(1);
					
						for (Object obj : groupCategoryMap.keySet()) {
							if (groupCategoryMap.containsKey(id)) {
								CategoryItem item = (CategoryItem) groupCategoryMap.get(id);
								item.setSelected(true);
								item.setAllChildrenSelected(true);
								break;
							} else {
								CategoryItem item = (CategoryItem) groupCategoryMap.get(obj); // tags in a particular category are selected.
								HashMap map = item.getItemMap();
								if (map.containsKey(id)) {
									TagItem tag = (TagItem) map.get(id);
									tag.setSelected(true);
									
								}
							}
						}
					}
					rs.close();
					stmt.close();
					
			}
			closeConnection();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "GroupHelper :: EXCEPTION in getGroupCategoryMapWithSelections() for GROUPID :: "+group.getGroupId()+" :: "+e.getMessage());
			e.printStackTrace();
		}
		int size = groupCategoryMap.getSelectedTags().size();
		logger.log(Level.INFO, "[ GroupHelper :: getGroupCategoryMapWithSelections() completed for GROUPID :: "+group.getGroupId()+"]");
		return groupCategoryMap;
	}
	
	
	
	
	/**
	 * This method is used to refresh the user selection of TagItems in the db. The entire user selection for that
	 * particular user and ncid is deleted and the entries are made all over again.For a method of CategoryMap will
	 * be called which will find all the selected tags and return an arraylist of selected tags.
	 * @param groupCategoryMap - a HashMap of CategoryItems which contain a map of their respective TagItems
	 * whose selection / deselection has been updated
	 */
	public void refreshUserSelection(GroupCategoryMap groupCategoryMap){
		logger.log(Level.INFO, "[GroupHelper :: refreshUserSelection() initiated ]");
		ArrayList selectionList = groupCategoryMap.getSelectedTags();
		
		try{
			String query = "delete from usergrouptagselection where UserId =" +userId;
			Statement stmt = getConnection().createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			closeConnection();
		}
		catch(Exception ex){
			logger.log(Level.SEVERE, "GroupHelper :: Unable to clear user selection :: refreshUserSelection() "+ex.getMessage());
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[GroupHelper :: refreshUserSelection() completed ]");
	}
    
	
	
	/**
	 * deletes a group
	 * @param groupId - id of the group to be deleted
	 * @return true if group successfully deleted else false
	 */
	public boolean deleteGroup(int groupId){
		try {
			logger.log(Level.INFO, "GroupHelper :: delete groups initiated :: deleteGroup() for GROUPID:: "+groupId);
			String query = "delete FROM `group` where groupId="+groupId;
			Statement stmt = getConnection().createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			closeConnection();
			logger.log(Level.INFO, "GroupHelper :: delete groups completed :: deleteGroup() ");
			return true;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "GroupHelper :: delete groups EXCEPTION returning false :: deleteGroup()  "+e.getMessage());
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * create a group
	 * @param group
	 * @return
	 */
	public Integer createGroup(Group group){
		try{
			logger.log(Level.INFO, "GroupHelper :: createGroup() initiated for groupname :: " +group.getGroupName());
			String query="";
			if(group.getGroupParentId()!=null)
				query = "insert into `group`(groupName, isMandatory, groupParentId, newsCenterId, userId) " +
					"values('"+group.getGroupName()+"','"+group.getIsMandatory()+"','"+group.getGroupParentId()+"','"+group.getNewsCenterId()+"','"+group.getUserId()+"')";
			else
				query = "insert into `group`(groupName, isMandatory, newsCenterId, userId, isFavorite) " +
				"values('"+group.getGroupName()+"','"+group.getIsMandatory()+"','"+group.getNewsCenterId()+"','"+group.getUserId()+"','"+group.getIsFavorite()+"')";
			
			Statement stmt = getConnection().createStatement();
			stmt.execute(query,Statement.RETURN_GENERATED_KEYS);
			
			ResultSet rs = stmt.getGeneratedKeys();
			if(rs != null){
				while(rs.next()){
					int groupId = rs.getInt(1);
					logger.log(Level.INFO, "GroupHelper :: createGroup() returning group id :: " +groupId);
					return groupId;
				}
			}
			rs.close();
			stmt.close();
			closeConnection();
			return null;
		}catch(Exception ex){
			logger.log(Level.SEVERE, "GroupHelper :: Unable to create Group :: createGroup() returning NULL "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
	
	public Integer getCustomGroupsAllowed(int ncid){
		logger.log(Level.INFO, "GroupHelper :: getCustomGroupsAllowed() initiated for NCID :: " +ncid);
		try{
			Integer numCustGroups = 0;
			String query="select numCustGrps from newscenter where newscenterid="+ncid;
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				numCustGroups = rs.getInt("numCustGrps");
			}
			rs.close();
			stmt.close();
			closeConnection();
			logger.log(Level.INFO, "GroupHelper :: getCustomGroupsAllowed() custom grps allowed :: "+numCustGroups+" for NCID :: " +ncid);
			return numCustGroups;
		}catch(Exception ex){
			logger.log(Level.SEVERE, "GroupHelper :: Unable to create Group :: createGroup() returning NULL "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method is used to update an entire CategoryMap based on user modifications. It will first call a
	 * method of CategoryMap to get the dirty tags from the map.This method will internally parse the categorymap
	 * and return a list of tags which have been modified (selected / deselected) and then these tags will be updated
	 * in the db.
	 * @param categoryMap - a HashMap of CategoryItems which contain a map of their respective TagItems
	 */
	public void saveUserGroupItemsSelections(List<Group> groupList){
		try{
			logger.log(Level.INFO,"[ saving thread initiated ---- GroupHelper ----- saveUserGroupItemsSelections() ]");
			LhSaveOnLogout savethread = new LhSaveOnLogout();
			savethread.setNCID(getNcid());
			savethread.setUserId(userId);
			savethread.setGroupsList(groupList);
			logger.log(Level.INFO,"[ saving thread started ---- GroupHelper ----- saveUserGroupItemsSelections() ]");
			savethread.start();
		}catch(Exception ex){
			logger.log(Level.SEVERE, "GroupHelper :: Unable to save user selections :: saveUserGroupItemsSelections() "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * Save the news filter mode for the group
	 * @param choice
	 * @param userid2
	 * @param ncid2
	 * @param groupId2
	 * @return
	 */
	public int saveNewsFilterModeCriteria(String choice, int userid2, int ncid2, int groupId2) {
		logger.log(Level.INFO,"[ GroupHelper :: saveUserGroupItemsSelections() initiated for USERID:: "+userid2+" and NCID :: "+ncid2+" and GROUPID:: "+groupId2+"]");
		int result = 0;
		try
		{   
			
			String query="";
			if(choice.equals("OR"))
				query = "update `group` set newsFilterMode =0 where UserId ="+userid2+" and NewsCenterId = "+ncid2+" and groupId ="+groupId2;
			else if(choice.equals("AND"))
				query = "update `group` set newsFilterMode =1 where UserId ="+userid2+" and NewsCenterId = "+ncid2+" and groupId ="+groupId2;


			Statement stmt = getConnection().createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.log(Level.INFO,"[ GroupHelper :: saveUserGroupItemsSelections() EXCEPTION returning 0 for USERID:: "+userid2+" and NCID :: "+ncid2+" and GROUPID:: "+groupId2+"]");
			return 0;
		}
		if(choice.equals("OR"))
			result =  1;
		else if(choice.equals("AND"))
			result =  2;
		logger.log(Level.INFO,"[ GroupHelper :: saveUserGroupItemsSelections() completed returning :: "+result+" for USERID:: "+userid2+" and NCID :: "+ncid2+" and GROUPID:: "+groupId2+"]");
		return result;
}
    
	/**
	 * updates mandatory groups
	 * @param defaultGroup 
	 * @param groupsSet - GroupList
	 * @return true if successfully updated else false
	 */
	public boolean updateMandatoryGroups(ArrayList<Group> groupselectionList, Group defaultGroup, int ncid) {
		try{
			logger.log(Level.INFO,"[GroupHelper --- updating mandatory groups :: updateMandatoryGroups()   ] ");
			Statement st = getConnection().createStatement();
			//update all groups of newscenter as isMandatory=0
			String qry ="update `group` set isMandatory=0 where newsCenterId="+ncid; 
			st.executeUpdate(qry);
			st.close();
			
			if(groupselectionList.size()>0){
				Statement stmt = getConnection().createStatement();
				// update the particular groups passed to the method in the list as mandatory
				for(Group group : groupselectionList){
					String query = "update `group` set  isMandatory="+group.getIsMandatory()+" where groupId="+group.getGroupId();
					stmt.executeUpdate(query);
				}
				stmt.close();
				closeConnection();
			}
		
			//update all groups of newscenter as isDefault=0
			Statement stmt2 = getConnection().createStatement();
			String query2 ="update `group` set isDefaultGroup=0 where newsCenterId="+ncid; 
			stmt2.executeUpdate(query2);
			stmt2.close();
			closeConnection();
			// update the particular group passed to the method as default
			Statement stmt3 = getConnection().createStatement();
			String query3 ="update `group` set isDefaultGroup=1 where groupId="+defaultGroup.getGroupId();
			stmt3.executeUpdate(query3);
			stmt3.close();
			closeConnection();
			logger.log(Level.INFO,"[GroupHelper --- updated mandatory groups :: updateMandatoryGroups()   ] ");
			return true;
		}catch (SQLException e) {
			logger.log(Level.SEVERE, "GroupHelper --- unable to update mandatory groups :: updateMandatoryGroups()     "+e.getMessage());
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * checks if user has permissions for favorite groups
	 * @param userId
	 * @param ncid
	 * @return
	 */
	private Integer isFavoriteGroupPermitted(int userId, int ncid){
		logger.log(Level.INFO, "[ GroupHelper :: isFavoriteGroupPermitted() initiated ]");
		try{
			String query = "select favoriteGroup from user_permission where userId="+userId+" and newsCenterId="+ncid;
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				Integer permitted = rs.getInt("favoriteGroup");
				return permitted;
			}
			rs.close();
			stmt.close();
			closeConnection();
			logger.log(Level.INFO, "[ GroupHelper :: isFavoriteGroupPermitted() completed ]");
			
		}catch (SQLException e) {
			logger.log(Level.SEVERE, "GroupHelper --- isFavoriteGroupPermitted() EXCPTION!!   "+e.getMessage());
			e.printStackTrace();
			return 0;
		}
		return 0;
		
	}
	

	/**
	 * Get the favorite group
	 * @param userId
	 * @param ncid
	 * @return
	 */
	public Group getFavoriteGroup(int userId, int ncid){
		logger.log(Level.INFO, "[ StatisticsHelper :: getFavoriteItems() initiated ]");
		try{
			Group group = null;
			String favGroupQuery = "SELECT * FROM `group` where userId = "+userId+" and newsCenterId = "+ncid+" and isFavorite = 1";
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(favGroupQuery);
			while(rs.next()){
				group = new Group();
				group.setGroupId(rs.getInt("groupId"));
				group.setGroupName(rs.getString("groupName"));
				group.setIsMandatory(rs.getInt("isMandatory"));
				group.setGroupParentId(rs.getInt("groupParentId"));
				group.setNewsCenterId(rs.getInt("newsCenterId"));
				group.setUserId(rs.getInt("userId"));
				group.setNewsFilterMode(rs.getInt("newsFilterMode"));
				group.setIsDefaultGroup(rs.getInt("isDefaultGroup"));
				group.setIsFavorite(rs.getInt("isFavorite"));
				
			}
			rs.close();
			stmt.close();
			return group;
		}catch (Exception e) {
			logger.log(Level.SEVERE, "StatisticsHelper --- getFavoriteItems() EXCPTION!!  Returning NULL!!  "+e.getMessage());
			e.printStackTrace();
			return null;
		} 
	}
}