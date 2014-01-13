package com.admin.client;


import java.util.ArrayList;
import java.util.HashMap;

import com.common.client.PageResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.newscenter.client.criteria.PageCriteria;

public interface AdminInformationServiceAsync 
{
	public void getUserInformation(int industryid,AsyncCallback  callback);
	
	public void saveApprovedUserInfo(HashMap hashmap,String newsCenterName,AsyncCallback  callback);
	
	public void getUserInfoToModify(PageCriteria crt,AsyncCallback<PageResult>  callback);
	
	public void getSortedUserInfoModify(PageCriteria crt,String columnname, String mode,AsyncCallback<PageResult> callback);
	
	public void getSearchedUserInfoModify(PageCriteria crt,String columnname, String search,AsyncCallback<PageResult> callback);
	
	public void deleteUser(HashMap hashmap,AsyncCallback  callback);
	
	public void getIndustryName(AsyncCallback  callback);
	
	public void getTagNames(String industryName,String categoryName,AsyncCallback  callback);
	
	public void setAllNewsItemFields(NewsItemsAdminInformation newsitemInfo,AsyncCallback  callback);
	
	public void getAllFieldOfNewsItems(int industryId,AsyncCallback  callback);
	
	public void deleteSelectedNewsItems(HashMap hashmap,AsyncCallback  callback);
	
	public void validateUser(AsyncCallback  callback);
	
	//public void getUserApprovalItems(UserAdminInformation userinfo, AsyncCallback  callback);
	public void getUserApprovalItems(ArrayList<UserAdminInformation> list, AsyncCallback  callback);
	
	void sendMailForApproval(UserAdminInformation userinfo,AsyncCallback  callback);
	
	public void uploadTagItems(String fileName,AsyncCallback  callback);
	
	public void convertFile(String file, AsyncCallback callback);
	
	public void getNewsItemForThisTag(String industryName,String tagName,AsyncCallback  callback);
	
	public void editNewsItemFields(NewsItemsAdminInformation newsitemInfo,AsyncCallback  callback);
	
	public void removeFromSession(AsyncCallback  callback);
	
	public void getCategoryNames(int industryid,String industryName,AsyncCallback  callback);
	
	public void saveNewTag(TagItemInformation tagItem,String parentName,boolean isCategory, AsyncCallback callback);
	
	public void getCategoryTagsInfo(String userSelectedIndustryName,int userSelectedIndustryId,int parentId, boolean isCategory, AsyncCallback callback);
	
	public void getCategoryTagsInformation(String categoryName,String userSelectedIndustryName,int userSelectedIndustryId,int parentId, boolean isCategory, AsyncCallback callback);
	
	public void setUserInfoAdminRegistration(AdminRegistrationInformation adminregInfo, AsyncCallback callback);
	
	public void getIndustryNameFromSession(AsyncCallback callback);
	
	public void deleteTags(HashMap hashmap, boolean isCategory, AsyncCallback callback);
	
	public void updateTags(HashMap<Integer,TagItemInformation> hashmap, AsyncCallback callback);

	public void getCategoryId(String categoryName,AsyncCallback callback);
	
	public void updateSubscriptionDuration(ArrayList<AdminRegistrationInformation> adminuser, AsyncCallback callback);
	
	public void deleteUserSubscription(AdminRegistrationInformation user, AsyncCallback callback);
	
	public void getParentId(int industryid,String industryName, AsyncCallback callback);

	/*void getLoginStatistics(PageCriteria crt,int industryid,AsyncCallback<PageResult> callback);

	void getSortedLoginStatistics(PageCriteria crt, int industryid,	String columnname, String mode, AsyncCallback<PageResult> callback);

	void getSearchLoginStatistics(PageCriteria crt, int industryid,String searchColumnName, String searchString,AsyncCallback<PageResult> callback);
*/
	void saveEmailTemplate(String emailtemplate, int newscenterid,AsyncCallback<Void> callback);

	/*void getUserItemAccessStats(PageCriteria crt, int industryid,AsyncCallback<PageResult> callback);

	void getSortedUserItemAccessStats(PageCriteria crt, int industryid,	String columnname, String mode, AsyncCallback<PageResult> callback);

	void getSearchUserItemAccessStats(PageCriteria crt, int industryid,	String searchColumnName, String searchString,AsyncCallback<PageResult> callback);*/
}