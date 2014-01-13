package com.lighthouse.admin.client.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


import com.admin.client.AdminInformationService;
import com.admin.client.AdminRegistrationInformation;
import com.admin.client.TagItemInformation;
import com.admin.client.UserAdminInformation;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.lighthouse.admin.client.AdminReportItemList;
import com.lighthouse.login.user.client.domain.LhUser;

@RemoteServiceRelativePath("lhAdminInformationService")
public interface LHAdminInformationService extends AdminInformationService
{
	
	public boolean saveNewTags(TagItemInformation tagItemInfo,String parentName, boolean isCategory);
	
	public void deleteTag(HashMap hashmap, boolean isCategory);
	public void updateTag(HashMap<Integer,TagItemInformation> hashmap);

	public boolean checkprimarycategory(String parentName);

	AdminReportItemList getAllReportsInListBox(String industryName, String tagName);

	public ArrayList fillprimaryTaglist(int industryId,String industryName);

	HashMap<String, Serializable> getCategoryNames();

	HashMap<String, Serializable> deleteSelectedTags(HashMap selectedTagMap, boolean b);

	void deleteSelectedReport(HashMap selectedTagMap);

	LhUser validateUserInfo();
	
	public ArrayList getNewsItemForThisTag(String industryName,String tagName);
	
	public ArrayList getCategoryNames(int industryid,String industryName);
	
	public ArrayList getTagNames(String industryName,String categoryName);
	
	public void deleteSelectedNewsItems(HashMap hashmap);

	public HashMap<Integer, TagItemInformation> getCategoryTagsInfo(String userSelectedIndustryName,int userSelectedIndustryId,int parentId, boolean isCategory);

	void saveUserGroupItemsSelections();
	
	void saveApprovedUserInfo(HashMap hashmap);
	
	public String setUserInfoAdminRegistration(AdminRegistrationInformation adminregInfo);
	
	public ArrayList getUserApprovalItems(ArrayList<UserAdminInformation> list);
	
	public boolean sendMailForApproval(UserAdminInformation userinfo);
	
	public void deleteUser(HashMap hashmap);
	
	public ArrayList getAllFieldOfNewsItems(int industryId);
	
}
