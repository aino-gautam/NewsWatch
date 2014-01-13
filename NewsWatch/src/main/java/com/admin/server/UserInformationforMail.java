package com.admin.server;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.admin.client.AdminInformationService;
import com.admin.client.AdminRegistrationInformation;
import com.admin.client.NewsItemsAdminInformation;
import com.admin.client.TagItemInformation;
import com.admin.client.UserAdminInformation;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.login.client.UserInformation;
import com.newscenter.client.criteria.PageCriteria;
import com.admin.server.MailClient;
import com.admin.server.db.UserHelperAdmin;
import com.common.client.PageResult;

public class UserInformationforMail extends RemoteServiceServlet implements AdminInformationService {
	protected String subject = "";
	protected String body = "";
	protected String smtpusername;
	protected String smtppassword;
	protected String smtphost;
	protected String smtpport;
   
	public boolean sendMailForApproval(UserAdminInformation userinfo){
		subject=userinfo.getSubjectMail();
		body=userinfo.getBodyMail();
		
		try
		{
			MailClient testClient = new MailClient(getSmtpusername(), getSmtppassword(), getSmtpport(), getSmtphost());
			//MailClient testClient = new MailClient("fmaster", "fmaster", "localhost");
			//MailClient testClient = new MailClient();
			testClient.sendMessage("no_reply@newscatalyst.com", userinfo.getRecipientsMail(), subject, body);
			return false;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public void deleteSelectedNewsItems(HashMap hashmap) {
		
	}

	public void deleteUser(HashMap hashmap) {
		
	}

	public ArrayList getAllFieldOfNewsItems(int industryid) {
		return null;
	}

	public ArrayList getIndustryName() {
		return null;
	}

	public ArrayList getTagNames(String industryName,String categoryName) {
		return null;
	}

	public ArrayList getUserApprovalItems(UserAdminInformation useradmininfo) {
		return null;
	}

	public PageResult getUserInfoToModify(PageCriteria crt) {
		return null;
	}

	public ArrayList getUserInformation(int industryid) {
		return null;
	}

	public void saveApprovedUserInfo(HashMap hashmap, String newsCenterName) {
		
	}

	public void setAllNewsItemFields(NewsItemsAdminInformation newsitemInfo) {
		
	}

	public boolean validateUser() {
		return false;
	}

	public void editNewsItemFields(NewsItemsAdminInformation newsitemInfo) {
		
	}

	public ArrayList getNewsItemForThisTag(String industryName, String tagName) {

		return null;
	}

	public String convertFile(String file) {
		return null;
	}

	public ArrayList getCategoryNames(int industryid,String industryName) {
		return null;
	}

	public boolean saveNewTag(TagItemInformation tagItem, String parentName, boolean isCategory) {
		if(UserHelperAdmin.flag == true){
			return true;
		}
		else
			return false;
	}

	public void uploadTagItems(String fileName) {
	}


	
	public void removeFromSession() {
	}
	
	public HashMap<Integer, TagItemInformation> getCategoryTagsInfo(String userSelectedIndustryName,int userSelectedIndustryId,int parentId,boolean isCategory) {
		return null;
	
	}

	public String setUserInfoAdminRegistration(
			AdminRegistrationInformation adminregInfo) {
		return "true";
	}

	
	public String[] getIndustryNameFromSession() {
		
		return null;
	}
	public void deleteTags(HashMap hashmap, boolean isCategory){
		
	}
	public void updateTags(HashMap<Integer,TagItemInformation> hashmap){
		
	}

	public void getCategories() {
		
	}

	
	public HashMap<Integer, TagItemInformation> getCategoryTagsInformation(
			String categoryName, String userSelectedIndustryName,
			int userSelectedIndustryId, int parentId, boolean isCategory) {
		
		return null;
	}

	public int getCategoryId(String categoryName) {

		return 0;
	}

	public ArrayList getUserApprovalItems(ArrayList<UserAdminInformation> list) {
		return null;
	}

	public void updateSubscriptionDuration(ArrayList<AdminRegistrationInformation> adminuser) {
		
	}
	
	public void deleteUserSubscription(AdminRegistrationInformation user) {
		
	}

	public int getParentId(int industryid, String industryName) {
		return 0;
	}

	public String getSmtpusername() {
		return smtpusername;
	}

	public void setSmtpusername(String smtpusername) {
		this.smtpusername = smtpusername;
	}

	public String getSmtppassword() {
		return smtppassword;
	}

	public void setSmtppassword(String smtppassword) {
		this.smtppassword = smtppassword;
	}

	public String getSmtphost() {
		return smtphost;
	}

	public void setSmtphost(String smtphost) {
		this.smtphost = smtphost;
	}

	public String getSmtpport() {
		return smtpport;
	}

	public void setSmtpport(String smtpport) {
		this.smtpport = smtpport;
	}

	@Override
	public void saveEmailTemplate(String emailtemplate, int newscenterid) {
	
	}

	@Override
	public PageResult getSearchedUserInfoModify(PageCriteria crt,
			String columnname, String search) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageResult getSortedUserInfoModify(PageCriteria crt,
			String columnname, String mode) {
		// TODO Auto-generated method stub
		return null;
	}

}