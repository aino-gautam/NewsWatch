package com.lighthouse.admin.server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.admin.client.AdminRegistrationInformation;
import com.admin.client.TagItemInformation;
import com.admin.client.UserAdminInformation;

import com.admin.server.AdminInformationServiceImpl;
import com.admin.server.UserInformationforMail;
import com.admin.server.db.UserHelperAdmin;
import com.lighthouse.admin.client.AdminReportItemList;
import com.lighthouse.admin.client.service.LHAdminInformationService;
import com.lighthouse.admin.server.db.LHAdminHelper;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.server.helper.GroupHelper;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.login.user.server.LhUserHelper;
import com.lighthouse.report.server.helper.ReportsHelper;
import com.lighthouse.search.server.helper.SetupIndexHelper;
import com.lighthouse.search.server.helper.SingleRecordIndexOperations;

import com.login.client.UserInformation;
import com.login.server.db.AllocateResources;

public class LHAdminInformationServiceImpl extends AdminInformationServiceImpl
		implements LHAdminInformationService {


	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	String connectionUrl;
	String driverClassName;
	String username;
	String password;
	String tomcatpath;
	ServletContext context;
	String dirPath;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		context = getServletContext();
		connectionUrl = (String) context
				.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName = (String) context
				.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username = (String) context.getAttribute(AllocateResources.USERNAME);
		password = (String) context.getAttribute(AllocateResources.PASSWORD);
		tomcatpath = (String) context
				.getAttribute(AllocateResources.TOMCATPATH);
		
		dirPath = context.getInitParameter("DirPath");
	}

	@Override
	public boolean checkprimarycategory(String parentName) {
		LHAdminHelper helper = new LHAdminHelper();
		boolean isprimary = helper.checkprimarycategory(parentName);
		helper.closeConnection();
		return isprimary;
	}

	@Override
	public AdminReportItemList getAllReportsInListBox(String industryName,String tagName) {
		ReportsHelper helper = new ReportsHelper();
		AdminReportItemList list = new AdminReportItemList();
		list = helper.getAllReportItems(industryName,tagName);
		helper.closeConnection();
		return list;
	}

	@Override
	public void deleteSelectedReport(HashMap hashmap) {
		try {
			ReportsHelper helper = new ReportsHelper();
			helper.deleteSelectedReport(hashmap);
			helper.closeConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean saveNewTags(TagItemInformation tagItem, String parentName, boolean isCategory) {
		try {
			ReportsHelper reporthelper = new ReportsHelper();
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			LhUser userInformation  = (LhUser)session.getAttribute("userInfo");
			boolean bool = reporthelper.saveNewTags(tagItem, parentName,isCategory, userInformation);
			reporthelper.closeConnection();
			return bool;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public void updateTag(HashMap<Integer, TagItemInformation> hashmap) {
	}

	@Override
	public void deleteTag(HashMap hashmap, boolean isCategory) {
		// TODO Auto-generated method stub
	}

	@Override
	public ArrayList fillprimaryTaglist(int industryId, String industryName) {
		ArrayList list = new ArrayList();
		LHAdminHelper helper = new LHAdminHelper();
		list = helper.fillprimaryTaglist(industryId, industryName);
		helper.closeConnection();
		return list;
	}

	@Override
	public HashMap<String, Serializable> getCategoryNames() {
		HashMap<String, Serializable> hashMap = new HashMap<String, Serializable>();
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		LHAdminHelper helper = new LHAdminHelper(req, res);
		hashMap = helper.getCategoryNames(context);
		helper.closeConnection();
		return hashMap;
	}

	@Override
	public HashMap<String, Serializable> deleteSelectedTags(HashMap selectedTagMap, boolean b) {
		HashMap<String, Serializable> hashMap = new HashMap<String, Serializable>();
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		LHAdminHelper helper = new LHAdminHelper(req, res);
		helper.deleteSelectedTags(selectedTagMap, b);
		hashMap = getCategoryNames();
		helper.closeConnection();
		return hashMap;
	}

	
	private UserInformation getUserInfoFromSession(){
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(true);
		UserInformation userInformation = (UserInformation) session.getAttribute("userInfo");
		return userInformation;
	}
	
	/*@Override
	public void setAllNewsItemFields(NewsItemsAdminInformation newsitemInfo) {
		super.setAllNewsItemFields(newsitemInfo);
		SetupIndexHelper helper = new SetupIndexHelper();
		UserInformation userInfo = getUserInfoFromSession();
		String industryName = userInfo.getIndustryNewsCenterName();
		String dirPathIndustryName = dirPath+industryName;
		helper.addRecordToIndex(newsitemInfo, dirPathIndustryName);
	}*/

	@Override
	public void deleteSelectedNewsItems(HashMap hashmap) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		LHAdminHelper lhNewshelper = new LHAdminHelper(req, res);
		lhNewshelper.deleteNewsItems(hashmap);
		
	//	SetupIndexHelper helper = new SetupIndexHelper();
		/*UserInformation userInfo = getUserInfoFromSession();
		String industryName = userInfo.getIndustryNewsCenterName();
		String dirPathIndustryName = dirPath+industryName;*/
	/*	for (Object obj : hashmap.keySet()) {
			Long id = (Long) obj;
			deleteRecordFromIndex(id, dirPathIndustryName);
		}*/
		deleteRecordFromIndex(hashmap);
		lhNewshelper.closeConnection();
	}

	private void deleteRecordFromIndex(HashMap hashmapId){
		try{
			UserInformation userInfo = getUserInfoFromSession();
			String industryName = userInfo.getIndustryNewsCenterName();
			String dirPathIndustryName = dirPath+industryName;
			SingleRecordIndexOperations deleteRecordThread = new SingleRecordIndexOperations();
			deleteRecordThread.setDaemon(true);
			deleteRecordThread.setDirPath(dirPathIndustryName);
			deleteRecordThread.setOperation("deleteRecord");
			deleteRecordThread.setHmNewsItemId(hashmapId);
			
			deleteRecordThread.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
/*	@Override
	public void editNewsItemFields(NewsItemsAdminInformation newsitemInfo) {

		super.editNewsItemFields(newsitemInfo);
		UserInformation userInfo = getUserInfoFromSession();
		String industryName = userInfo.getIndustryNewsCenterName();
		String dirPathIndustryName = dirPath+industryName;
		SetupIndexHelper helper = new SetupIndexHelper();
		helper.updateRecordFromIndex(newsitemInfo, dirPathIndustryName);
	}*/

	@Override
	public LhUser validateUserInfo() {
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			LHAdminHelper helper = new LHAdminHelper(req, res);
			LhUser userInfo = helper.validateUser();
			helper.closeConnection();
			return userInfo;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public ArrayList getNewsItemForThisTag(String industryName, String tagName) {
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			LHAdminHelper helper = new LHAdminHelper(req, res);
			ArrayList list = helper.getNewsItemForThisTag(industryName, tagName);
			helper.closeConnection();
			return list;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	@Override
	public ArrayList getCategoryNames(int industryid,String industryName){
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			LHAdminHelper helper = new LHAdminHelper(req, res); 
			ArrayList list = helper.getCategoryNames(industryid,industryName);
			helper.closeConnection();
			return list;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public ArrayList getTagNames(String industryName,String categoryName) 
	{
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			LHAdminHelper helper = new LHAdminHelper(req, res);
			ArrayList list = helper.getTagName(industryName,categoryName);
			helper.closeConnection();
			return list;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
			
		}
	}
	
	@Override
	public HashMap<Integer, TagItemInformation> getCategoryTagsInfo(String userSelectedIndustryName,int userSelectedIndustryId,int parentId,boolean isCategory){
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			LHAdminHelper helper = new LHAdminHelper(req, res);
			HashMap<Integer, TagItemInformation> hashmap = helper.getCategoryTagsInfo(userSelectedIndustryName,userSelectedIndustryId,parentId, isCategory);
			helper.closeConnection();
			return hashmap;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void saveUserGroupItemsSelections() {
		try{
			context.log("saving user selections ---- ManageGroupServiceImpl ----- saveUserGroupItemsSelections()");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			UserInformation userInformation = (UserInformation) session.getAttribute("userInfo");
			List<Group> grplist = (List<Group>) session.getAttribute("groupsList");
			GroupHelper groupHelper = new GroupHelper();
			groupHelper.setNcid(userInformation.getUserSelectedNewsCenterID());
			groupHelper.setUserId(userInformation.getUserId());
			groupHelper.saveUserGroupItemsSelections(grplist);
			groupHelper.closeConnection();
		}catch(Exception ex){
			context.log("ManageGroupServiceImpl :: Unable to save user selections :: saveUserGroupItemsSelections()");
			ex.printStackTrace();
		}
	}
	
	@Override
	public void saveApprovedUserInfo(HashMap hashmap) {
		try{
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession();
			LhUser userInformation  = (LhUser)session.getAttribute("userInfo");
			LHAdminHelper helper = new LHAdminHelper();
			helper.saveApprovedUserInfo(hashmap,userInformation);
			helper.closeConnection();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public String setUserInfoAdminRegistration(AdminRegistrationInformation adminregInfo){
		try{
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpServletResponse res = this.getThreadLocalResponse();
			LHAdminHelper helper = new LHAdminHelper(req,res);
			String strstatus = helper.setUserInfoAdminRegistration(adminregInfo);
			helper.closeConnection();
			if(strstatus.equals("true"))
			return "true";
			else
				return "false";
		}
		catch(Exception ex){
			ex.printStackTrace();
			return "false";
		}
	}
	
	@Override
	public ArrayList getUserApprovalItems(ArrayList list){
		try{
			LHAdminHelper helper = new LHAdminHelper();
			ArrayList userlist = helper.getUserApprovalItems(list);
			helper.closeConnection();
			return userlist;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean sendMailForApproval(UserAdminInformation userinfo) {
		LhUserHelper helper = new LhUserHelper();
		helper.setSmtpusername(context.getInitParameter("smtpusername"));
		helper.setSmtppassword(context.getInitParameter("smtppassword"));
		helper.setSmtphost(context.getInitParameter("smtphost"));
		helper.setSmtpport(context.getInitParameter("smtpport"));
		boolean val = helper.sendMailForApproval(userinfo);
		helper.closeConnection();
		return val;
	}
	
	@Override
	public void deleteUser(HashMap hashmap){
		try {
			LhUserHelper helper = new LhUserHelper();
			helper.deleteUser(hashmap);
			helper.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public  ArrayList getAllFieldOfNewsItems(int industryId){
		try{
			LHAdminHelper helper = new LHAdminHelper();
			ArrayList list = helper.getAllFieldOfNewsItems(industryId);
			helper.closeConnection();
			return list;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
}