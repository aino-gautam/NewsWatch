package com.lighthouse.group.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.client.service.ManageGroupService;
import com.lighthouse.group.server.helper.GroupHelper;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.server.helper.LhNewsItemHelper;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.server.categorydb.ItemProviderServiceImpl;

/**
 * ServiceImpl class for the ManageGroupService interface
 * @author prachi
 *
 */

public class ManageGroupServiceImpl extends ItemProviderServiceImpl implements ManageGroupService {

	private static final long serialVersionUID = 1L;
	public static String GROUPSLIST = "groupsList";
	Logger logger = Logger.getLogger(ManageGroupServiceImpl.class.getName());
	
	
	/**
	 * gets a list of all the groups of the user
	 * @userId - id of the user
	 * @ncid - id of the newscenter
	 */
	@Override
	public List<Group> getUserGroupsList(int userId, int ncid) {
		logger.log(Level.INFO,"[ Getting group list ---- ManageGroupServiceImpl ----- getUserGroupList() ]");
		HttpServletRequest req = this.getThreadLocalRequest();
		HttpSession session = req.getSession(false);
		
		GroupHelper grpHelper = new GroupHelper();
		List<Group> grpList = grpHelper.getGroupList(userId,ncid);
		
		session.setAttribute(GROUPSLIST, grpList);
		logger.log(Level.INFO,"[ fetched group list ---- ManageGroupServiceImpl ----- getUserGroupList() ] " +grpList.size());
		grpHelper.closeConnection();
		return grpList;
	}
    
	/**
	 * updates the session groups list
	 * @param groupCategoryMap GroupCategoryMap
	 */
	public void updateSessionGroupsList(GroupCategoryMap groupCategoryMap){
		logger.log(Level.INFO,"[ Updating session group list ---- ManageGroupServiceImpl ----- updateGroupsList() ]");
		HttpServletRequest req = this.getThreadLocalRequest();
		HttpSession session = req.getSession(false);

		int groupId = groupCategoryMap.getGroupId();
		
		List<Group> groupsList = (List<Group>)session.getAttribute(GROUPSLIST);
		for(Group group : groupsList){
			if(group.getGroupId() == groupId){
				group.setGroupCategoryMap(groupCategoryMap);
				break;
			}
		}
		logger.log(Level.INFO,"[ Updated session group list ---- ManageGroupServiceImpl ----- updateGroupsList() ]");
	}
	
	/**
	 * updates session group category map
	 */
	@Override
	public NewsItemList updateSessionGroupCategoryMap(GroupCategoryMap groupCategoryMap, PageCriteria criteria,int newsmode){
		try{
            logger.log(Level.INFO,"[ Selected tags ---- ManageGroupServiceImpl ----- updateSessionGroupCategoryMap() ] "+groupCategoryMap.getSelectedTags().size());
			
            logger.log(Level.INFO,"[ Dirty tags ---- ManageGroupServiceImpl ----- updateSessionGroupCategoryMap() ] "+groupCategoryMap.getDirtyTags().size());
						
			logger.log(Level.INFO,"[ Updating session groupCategoryMap ---- ManageGroupServiceImpl ----- updateSessionGroupCategoryMap() ]");
			updateSessionGroupsList(groupCategoryMap);
			logger.log(Level.INFO,"[ Updated session groupCategoryMap ---- ManageGroupServiceImpl ----- updateSessionGroupCategoryMap() ]");
			
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			LhUser lhUser  = (LhUser)session.getAttribute("userInfo");
			int userid = lhUser.getUserId();
			int industryId = lhUser.getUserSelectedIndustryID();
			
			LhNewsItemHelper newshelper = new LhNewsItemHelper(groupCategoryMap,tomcatpath); 
			NewsItemList newsList = null;
			if(lhUser.getUserPermission().isPrimaryHeadLinePermitted() == 1)
				newsList =  newshelper.getGroupedNewsList(newsmode, userid,industryId);
			else{
				GroupPageCriteria grpcriteria = (GroupPageCriteria) criteria;
				newsList =  newshelper.getPage(grpcriteria, newsmode, userid);
			}
			logger.log(Level.INFO,"[ Returning newslist ---- ManageGroupServiceImpl ----- updateSessionGroupCategoryMap() ] "+newsList.size());
			newshelper.closeConnection();
			return newsList;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * gets only the available groups list
	 */
	@Override
	public List<Group> getAvailableGroups() {
		try{
			logger.log(Level.INFO,"[ Getting available groups ---- ManageGroupServiceImpl ----- getAvailableGroups() ]");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			
			LhUser lhUser = (LhUser)session.getAttribute("userInfo");
			
			GroupHelper grpHelper = new GroupHelper();
			List<Group> grpList = grpHelper.getAvailableGroups(lhUser.getUserId(),lhUser.getUserSelectedNewsCenterID());
			logger.log(Level.INFO,"[ Fetched available groups ---- ManageGroupServiceImpl ----- getAvailableGroups() ] " +grpList.size());
			grpHelper.closeConnection();
			return grpList;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * clears the tag selections from the group category map
	 */
	@Override
	public void clearAllSelection(GroupCategoryMap groupCategoryMap) {
		try {
			logger.log(Level.INFO,"[ Refreshing user selections ---- ManageGroupServiceImpl ----- refreshUserSelection() ]");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			LhUser lhUser  = (LhUser)session.getAttribute("userInfo");

			GroupHelper groupHelper = new GroupHelper();
			groupHelper.refreshUserSelection(groupCategoryMap);
			updateSessionGroupsList(groupCategoryMap);
			groupCategoryMap.getSelectedTags();
			groupHelper.closeConnection();
			
			logger.log(Level.INFO,"[ Refreshed user selections ---- ManageGroupServiceImpl ----- refreshUserSelection() ]");
		} 
		catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "ManageGroupServiceImpl :: Unable to refresh user selections :: refreshUserSelection()");
		}
	}
     
	/**
	 * gets a blank group category map
	 */
	@Override
	public GroupCategoryMap getBlankGroupCategoryMap() {
		try{
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			LhUser lhUser  = (LhUser)session.getAttribute("userInfo");
			int ncid = lhUser.getUserSelectedNewsCenterID();
			int userId = lhUser.getUserId();
			GroupHelper groupHelper = new GroupHelper();
			GroupCategoryMap groupCategoryMap = groupHelper.getBlankGroupCategoryMap(userId, ncid);
			groupHelper.closeConnection();
			return groupCategoryMap;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.log(Level.SEVERE, "ManageGroupServiceImpl :: Unable to fetch group category map :: getBlankGroupCategoryMap()");
			return null;
		}
	}

	/**
	 * create a group
	 */
	@Override
	public Integer createGroup(Group group) {
		try{
			logger.log(Level.INFO,"[ Group creation initiated ---- ManageGroupServiceImpl ----- createGroup() ]");
			GroupHelper groupHelper = new GroupHelper();
			Integer groupId = groupHelper.createGroup(group);
			group.setGroupId(groupId);
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			List<Group> grpList = (List<Group>)session.getAttribute(GROUPSLIST);
			grpList.add(group);
			session.setAttribute(GROUPSLIST, grpList);
			groupHelper.closeConnection();
			logger.log(Level.INFO,"[ Group created ---- ManageGroupServiceImpl ----- createGroup() Id: ]" + groupId);
			
		return groupId;
		}catch(Exception ex){
			logger.log(Level.SEVERE, "ManageGroupServiceImpl :: Unable to create group :: createGroup()");
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * delete a group
	 */
	@Override
	public boolean deleteGroup(int groupId) {
		try{
			logger.log(Level.INFO,"[ Group deletion initiated ---- ManageGroupServiceImpl ----- deleteGroup() ]");
			GroupHelper groupHelper = new GroupHelper();
			boolean isDeleted = groupHelper.deleteGroup(groupId);
			groupHelper.closeConnection();
			logger.log(Level.INFO,"[ Group deleted ---- ManageGroupServiceImpl ----- deleteGroup() Id: ]" + groupId);
			
			return isDeleted;
		}catch(Exception ex){
			logger.log(Level.SEVERE, "ManageGroupServiceImpl :: Unable to delete group :: deleteGroup()");
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * saves a user's tag selections for all groups
	 */
	@Override
	public void saveUserGroupItemsSelections(List<Group> groupList, int ncid, int userId) {
		try{
			logger.log(Level.INFO,"[ saving user selections ---- ManageGroupServiceImpl ----- saveUserGroupItemsSelections() ]");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			List<Group> grplist = (List<Group>) session.getAttribute(ManageGroupServiceImpl.GROUPSLIST);
			GroupHelper groupHelper = new GroupHelper();
			groupHelper.setNcid(ncid);
			groupHelper.setUserId(userId);
			groupHelper.saveUserGroupItemsSelections(groupList);
		}catch(Exception ex){
			logger.log(Level.SEVERE, "ManageGroupServiceImpl :: Unable to save user selections :: saveUserGroupItemsSelections()");
			ex.printStackTrace();
		}
	}
	
	/**
	 * saves a user's news filter mode preference for a group
	 */
	public int saveNewsFilterModePreference(String choice, int groupId) {
		try{
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			int bool = 0;
			if(session!=null){
				LhUser lhUser  = (LhUser)session.getAttribute("userInfo");
				int userid = lhUser.getUserId();
				int ncid = lhUser.getUserSelectedNewsCenterID();
							
			    GroupHelper groupHelper = new GroupHelper();
			    bool = groupHelper.saveNewsFilterModeCriteria(choice,userid,ncid,groupId);
			    groupHelper.closeConnection();
			    
			    logger.log(Level.INFO," [ Filter mode preferences changed ]");
		       }
			return bool;
		}
		catch(Exception e){
                 e.printStackTrace();
                 logger.log(Level.INFO,"[Filter mode preferences unchanged ]");
                 return 0;
		   }
		
		
	   
		}

	@Override
	protected void doUnexpectedFailure(Throwable e) {
		super.doUnexpectedFailure(e);
		logger.log(Level.SEVERE, " doUnexpectedFailure() in ManageGroupServiceImpl -- " + e.getMessage());
	}
    
	
	/**
	 * update the mandatory groups
	 */
	@Override
	public boolean updateMandatoryGroups(ArrayList<Group> groupselectionList,Group defaultGroup) {
		try{
			logger.log(Level.INFO,"[ update mandatory groups initiated ---- ManageGroupServiceImpl ----- updateMandatoryGroups() ]");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			
			LhUser lhUser = (LhUser)session.getAttribute("userInfo");
			int ncid = lhUser.getUserSelectedNewsCenterID();
			
			GroupHelper grpHelper = new GroupHelper();
			boolean isUpdated = grpHelper.updateMandatoryGroups(groupselectionList,defaultGroup,ncid);
			grpHelper.closeConnection();
			logger.log(Level.INFO,"[ mandatory groups updated ---- ManageGroupServiceImpl ----- updateMandatoryGroups() ]" );
			return isUpdated;
		}
		catch(Exception e){
			logger.log(Level.SEVERE, "ManageGroupServiceImpl :: Unable to update mandatory group :: updateMandatoryGroups()");
			e.printStackTrace();
		}
		return false;
		
	}

	@Override
	public int getCustomGroupsAllowed(int ncid) {
		try{
			logger.log(Level.INFO,"[ fetching noOfCustomGrousp ---- ManageGroupServiceImpl ----- getCustomGroupsAllowed() ]");
			GroupHelper grpHelper = new GroupHelper();
			int customGroups = grpHelper.getCustomGroupsAllowed(ncid);
			grpHelper.closeConnection();
			logger.log(Level.INFO,"[ noOfCustomGroups fetched ---- ManageGroupServiceImpl ----- getCustomGroupsAllowed() ]" );
			return customGroups;
		}catch(Exception e){
			logger.log(Level.SEVERE, "ManageGroupServiceImpl :: Unable to fetch noOfCustomGroups:: getCustomGroupsAllowed()");
			e.printStackTrace();
		}
		return 0;
	}
}
