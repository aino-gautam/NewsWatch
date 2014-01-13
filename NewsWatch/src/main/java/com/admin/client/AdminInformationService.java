package com.admin.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.client.PageResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.newscenter.client.criteria.PageCriteria;

public interface AdminInformationService extends RemoteService 
{
	public ArrayList getUserInformation(int industryid);
	
	public void saveApprovedUserInfo(HashMap hashmap,String newsCenterName);
	
	public PageResult getUserInfoToModify(PageCriteria crt);
	
	public void deleteUser(HashMap hashmap);
	
	public ArrayList getIndustryName();
	
	public ArrayList getTagNames(String industryName,String categoryName);
	
	public void setAllNewsItemFields(NewsItemsAdminInformation newsitemInfo);
	
	public ArrayList getAllFieldOfNewsItems(int industryId);
	
	public void deleteSelectedNewsItems(HashMap hashmap);
	
	public boolean validateUser();
	
	//public ArrayList getUserApprovalItems(UserAdminInformation useradmininfo);
	public ArrayList getUserApprovalItems(ArrayList<UserAdminInformation> list);
	
	public boolean sendMailForApproval(UserAdminInformation userinfo);
	
	public void uploadTagItems(String fileName);
	
	public String convertFile(String file);
	
	public ArrayList getNewsItemForThisTag(String industryName,String tagName);
	
	public void editNewsItemFields(NewsItemsAdminInformation newsitemInfo);
	
	public void removeFromSession();
	
	public ArrayList getCategoryNames(int industryid,String industryName);
	
	public boolean saveNewTag(TagItemInformation tagItem,String parentName, boolean isCategory);
	
	public HashMap<Integer, TagItemInformation> getCategoryTagsInfo(String userSelectedIndustryName,int userSelectedIndustryId,int parentId, boolean isCategory);
	
	public HashMap<Integer, TagItemInformation> getCategoryTagsInformation(String categoryName,String userSelectedIndustryName,int userSelectedIndustryId,int parentId, boolean isCategory);
	
	public String setUserInfoAdminRegistration(AdminRegistrationInformation adminregInfo);
	
	public String[] getIndustryNameFromSession();
	
	public void deleteTags(HashMap hashmap, boolean isCategory);
	
	public void updateTags(HashMap<Integer,TagItemInformation> hashmap);
	
	public int getCategoryId(String categoryName);
	
	public void updateSubscriptionDuration(ArrayList<AdminRegistrationInformation> adminuser);
	
	public void deleteUserSubscription(AdminRegistrationInformation user);
	
	public int getParentId(int industryid,String industryName);
	
	/*public PageResult getLoginStatistics(PageCriteria crt,int industryid);
	
	public PageResult getSortedLoginStatistics(PageCriteria crt,int industryid,String columnname,String mode);
	
	public PageResult getSearchLoginStatistics(PageCriteria crt,int industryid,String searchColumnName,String searchString);*/
	
	public void saveEmailTemplate(String emailtemplate,int newscenterid);

	public PageResult getSortedUserInfoModify(PageCriteria crt, String columnname,	String mode);

	public PageResult getSearchedUserInfoModify(PageCriteria crt, String columnname, String search);
	
	/*public PageResult getUserItemAccessStats(PageCriteria crt,int industryid);
	
    public PageResult getSortedUserItemAccessStats(PageCriteria crt,int industryid,String columnname,String mode);
	
	public PageResult getSearchUserItemAccessStats(PageCriteria crt,int industryid,String searchColumnName,String searchString);*/
	
	
	
}
