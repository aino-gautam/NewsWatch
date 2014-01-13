package com.common.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.newscenter.client.criteria.PageCriteria;

public interface CommonInformationService extends RemoteService 

{
	public void removeFromSession();
	
	public String[] getIndustryNameFromSession();
	
	public ArrayList getUserInformationObject();
	
	public void changeuserdetails(String userpassword,String useremail);
	
	public ArrayList<UserHistory> getUserAccountHistory(int userId);
	
	public String getEmailTemplate(int newscenterid);
	
	public void saveSignature(int userid,String signature);

	public String getSignature(int userid);
	
    public PageResult getLoginStatistics(PageCriteria crt,int userid,int industryid);
	
	public PageResult getSortedLoginStatistics(PageCriteria crt,int userid,int industryid,String columnname,String mode);
	
	public PageResult getSearchLoginStatistics(PageCriteria crt,int userid,int industryid,String searchColumnName,String searchString);
	
    public PageResult getUserItemAccessStats(PageCriteria crt, int userid, int industryid);
	
    public PageResult getSortedUserItemAccessStats(PageCriteria crt,int userid, int industryid,String columnname,String mode);
	
	public PageResult getSearchUserItemAccessStats(PageCriteria crt,int userid, int industryid,String searchColumnName,String searchString);
}
