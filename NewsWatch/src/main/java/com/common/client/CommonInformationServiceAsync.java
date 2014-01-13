package com.common.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.newscenter.client.criteria.PageCriteria;

public interface CommonInformationServiceAsync 
{
	public void removeFromSession(AsyncCallback  callback);
	
	public void getIndustryNameFromSession(AsyncCallback callback);
	
	
	public void getUserInformationObject(AsyncCallback callback);
	
	public void changeuserdetails(String userpassword,String useremail,AsyncCallback callback);

	void getUserAccountHistory(int userId, AsyncCallback<ArrayList<UserHistory>> callback);

	void getEmailTemplate(int newscenterid, AsyncCallback<String> callback);
	
	void saveSignature(int userid, String signature,AsyncCallback<Void> callback);
	
	void getSignature(int userid,AsyncCallback<String> callback);

	void getUserItemAccessStats(PageCriteria crt, int userid, int industryid,AsyncCallback<PageResult> callback);

	void getSortedUserItemAccessStats(PageCriteria crt, int userid ,int industryid,	String columnname, String mode, AsyncCallback<PageResult> callback);

	void getSearchUserItemAccessStats(PageCriteria crt, int userid, int industryid,	String searchColumnName, String searchString,AsyncCallback<PageResult> callback);
	
	void getLoginStatistics(PageCriteria crt,int userid,int industryid,AsyncCallback<PageResult> callback);

	void getSortedLoginStatistics(PageCriteria crt,int userid, int industryid,	String columnname, String mode, AsyncCallback<PageResult> callback);

	void getSearchLoginStatistics(PageCriteria crt,int userid, int industryid,String searchColumnName, String searchString,AsyncCallback<PageResult> callback);

}
