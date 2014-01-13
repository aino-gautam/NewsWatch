package com.lighthouse.admin.client.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.admin.client.AdminRegistrationInformation;
import com.admin.client.NewsItemsAdminInformation;
import com.admin.client.TagItemInformation;
import com.admin.client.UserAdminInformation;
import com.common.client.PageResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lighthouse.admin.client.AdminReportItemList;
import com.lighthouse.login.user.client.domain.LhUser;
import com.newscenter.client.criteria.PageCriteria;

public interface LHAdminInformationServiceAsync {

	void checkprimarycategory(String parentName, AsyncCallback<Boolean> callback);

	void deleteSelectedNewsItems(HashMap hashmap, AsyncCallback<Void> callback);

	void deleteSelectedReport(HashMap selectedTagMap,
			AsyncCallback<Void> callback);

	void deleteSelectedTags(HashMap selectedTagMap, boolean b,
			AsyncCallback<HashMap<String, Serializable>> callback);

	void deleteTag(HashMap hashmap, boolean isCategory,
			AsyncCallback<Void> callback);

	void deleteUser(HashMap hashmap, AsyncCallback<Void> callback);

	void fillprimaryTaglist(int industryId, String industryName,
			AsyncCallback<ArrayList> callback);

	void getAllFieldOfNewsItems(int industryId,
			AsyncCallback<ArrayList> callback);

	void getAllReportsInListBox(String industryName, String tagName,
			AsyncCallback<AdminReportItemList> asyncCallback);

	void getCategoryNames(AsyncCallback<HashMap<String, Serializable>> callback);

	void getCategoryNames(int industryid, String industryName,
			AsyncCallback<ArrayList> callback);

	void getCategoryTagsInfo(String userSelectedIndustryName,
			int userSelectedIndustryId, int parentId, boolean isCategory,
			AsyncCallback<HashMap<Integer, TagItemInformation>> callback);

	void getNewsItemForThisTag(String industryName, String tagName,
			AsyncCallback<ArrayList> callback);

	void getTagNames(String industryName, String categoryName,
			AsyncCallback<ArrayList> callback);

	void getUserApprovalItems(ArrayList<UserAdminInformation> list,
			AsyncCallback<ArrayList> callback);

	void saveApprovedUserInfo(HashMap hashmap, AsyncCallback<Void> callback);

	void saveNewTags(TagItemInformation tagItemInfo, String parentName,
			boolean isCategory, AsyncCallback<Boolean> callback);

	void saveUserGroupItemsSelections(AsyncCallback<Void> callback);

	void sendMailForApproval(UserAdminInformation userinfo,
			AsyncCallback<Boolean> callback);

	void setUserInfoAdminRegistration(
			AdminRegistrationInformation adminregInfo,
			AsyncCallback<String> callback);

	void updateTag(HashMap<Integer, TagItemInformation> hashmap,
			AsyncCallback<Void> callback);

	void validateUserInfo(AsyncCallback<LhUser> callback);

	void convertFile(String file, AsyncCallback<String> callback);

	void deleteTags(HashMap hashmap, boolean isCategory,
			AsyncCallback<Void> callback);

	void deleteUserSubscription(AdminRegistrationInformation user,
			AsyncCallback<Void> callback);

	void editNewsItemFields(NewsItemsAdminInformation newsitemInfo,
			AsyncCallback<Void> callback);

	void getCategoryId(String categoryName, AsyncCallback<Integer> callback);

	void getCategoryTagsInformation(String categoryName,
			String userSelectedIndustryName, int userSelectedIndustryId,
			int parentId, boolean isCategory,
			AsyncCallback<HashMap<Integer, TagItemInformation>> callback);

	void getIndustryName(AsyncCallback<ArrayList> callback);

	void getIndustryNameFromSession(AsyncCallback<String[]> callback);

	void getParentId(int industryid, String industryName,
			AsyncCallback<Integer> callback);

	void getSearchedUserInfoModify(PageCriteria crt, String columnname,
			String search, AsyncCallback<PageResult> callback);

	void getSortedUserInfoModify(PageCriteria crt, String columnname,
			String mode, AsyncCallback<PageResult> callback);

	void getUserInfoToModify(PageCriteria crt,
			AsyncCallback<PageResult> callback);

	void getUserInformation(int industryid, AsyncCallback<ArrayList> callback);

	void removeFromSession(AsyncCallback<Void> callback);

	void saveApprovedUserInfo(HashMap hashmap, String newsCenterName,
			AsyncCallback<Void> callback);

	void saveEmailTemplate(String emailtemplate, int newscenterid,
			AsyncCallback<Void> callback);

	void saveNewTag(TagItemInformation tagItem, String parentName,
			boolean isCategory, AsyncCallback<Boolean> callback);

	void setAllNewsItemFields(NewsItemsAdminInformation newsitemInfo,
			AsyncCallback<Void> callback);

	void updateSubscriptionDuration(
			ArrayList<AdminRegistrationInformation> adminuser,
			AsyncCallback<Void> callback);

	void updateTags(HashMap<Integer, TagItemInformation> hashmap,
			AsyncCallback<Void> callback);

	void uploadTagItems(String fileName, AsyncCallback<Void> callback);

	void validateUser(AsyncCallback<Boolean> callback);

}
