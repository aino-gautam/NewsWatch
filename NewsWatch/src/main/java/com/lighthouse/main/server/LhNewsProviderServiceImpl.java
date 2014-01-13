package com.lighthouse.main.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.server.ManageGroupServiceImpl;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.client.service.LhNewsProviderService;
import com.lighthouse.main.server.helper.LhNewsItemHelper;
import com.lighthouse.newsletter.server.LhNewsletterHelper;
import com.login.client.UserInformation;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.newsdb.NewsProviderServiceImpl;
/**
 * author prachi
 * 
 */


public class LhNewsProviderServiceImpl extends NewsProviderServiceImpl
		implements LhNewsProviderService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6031357569763899760L;

	Logger logger = Logger.getLogger(LhNewsProviderServiceImpl.class.getName());

	public ArrayList getadmininformation() {
		try{
			logger.log(Level.INFO,"In getadmininformation() in LhNewsProviderServiceImpl");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			LhUser userInformation = (LhUser) session.getAttribute("userInfo");
			logger.log(Level.INFO,"LhNewsProviderServiceImpl -- fetched user from session -- ");
			ArrayList list = new ArrayList();
			list.add(userInformation);
			ServletContext context = getServletContext();
			String siloLogo = (String) context.getAttribute("siloLogo");
			list.add(siloLogo);
			logger.log(Level.INFO,"Exiting getadmininformation() in LhNewsProviderServiceImpl");
			return list;
		}catch(Exception e){
			logger.log(Level.INFO,"Exception In getadmininformation() in LhNewsProviderServiceImpl ....................  "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
    
	
	@Override
	public NewsItemList getPage(GroupPageCriteria criteria, int newsmode){
		try{
		logger.log(Level.INFO, "In getPage in LhNewsProviderServiceImpl");
		HttpServletRequest req = this.getThreadLocalRequest();
		HttpSession session = req.getSession(false);
		if(session!=null){
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			int userid = userInformation.getUserId();
			int groupId = criteria.getGroupId(); 
			
			List<Group> groupsList = (List<Group>) session.getAttribute(ManageGroupServiceImpl.GROUPSLIST);
			GroupCategoryMap currentMap = null;
			for (Group group : groupsList) {
				if (group.getGroupId() == groupId){
					currentMap = group.getGroupCategoryMap();
				    break;
				}
			}
			LhNewsItemHelper lhNewsItemHelper = new LhNewsItemHelper(currentMap, tomcatpath);
			NewsItemList newslist = lhNewsItemHelper.getPage(criteria, newsmode, userid);
			logger.log(Level.INFO, "[LhNewsProviderServiceImpl Number of news items returned getPage:::: ] " +newslist.size());
			lhNewsItemHelper.closeConnection();
			return newslist;
		}
		else{
			NewsItemList newsList = new NewsItemList();
			newsList.setRedirect(true);
			return newsList;
		}
		}
		catch(Exception e){
			logger.log(Level.INFO,"LhNewsProviderServiceImpl getPage() Exception ....................  "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public NewsItemList getAndPage(GroupPageCriteria criteria, int newsmode) {
		try {
			logger.log(Level.INFO,"[ In getAndPage in LhNewsProviderServiceImpl ]");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			if (session != null) {
				UserInformation userInformation = (UserInformation) session.getAttribute("userInfo");
				int userid = userInformation.getUserId();
				int groupId = criteria.getGroupId();
				
				List<Group> groupsList = (List<Group>) session.getAttribute(ManageGroupServiceImpl.GROUPSLIST);
				
				GroupCategoryMap currentMap = null;
				for (Group group : groupsList) {
					if (group.getGroupId() == groupId)
						currentMap = group.getGroupCategoryMap();
				}
				LhNewsItemHelper lhNewsItemHelper = new LhNewsItemHelper(currentMap, tomcatpath);
				NewsItemList newslist = lhNewsItemHelper.getAndPage(criteria,newsmode, userid);
				logger.log(Level.INFO, "LhNewsProviderServiceImpl Number of news items returned getAndPage::::" +newslist.size());
				lhNewsItemHelper.closeConnection();
				return newslist;
			} else {
				NewsItemList newsList = new NewsItemList();
				newsList.setRedirect(true);
				return newsList;
			}
		} catch (Exception ex) {
			logger.log(Level.INFO,"LhNewsProviderServiceImpl getAndPage() Exception ....................  "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	protected void doUnexpectedFailure(Throwable e) {
		super.doUnexpectedFailure(e);
		logger.log(Level.SEVERE, " doUnexpectedFailure() in LhNewsProviderServiceImpl -- " + e.getMessage());
	}

	@Override
	public NewsItemList getAllNewsforTag(TagItem tagitem,GroupPageCriteria criteria) {
		try{
			logger.log(Level.INFO , "[In getAllNewsforTag in LhNewsProviderServiceImpl :: tagname :: "+tagitem.getTagName()+"]");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			if(session!=null){
				int groupId = criteria.getGroupId();
                List<Group> groupsList = (List<Group>) session.getAttribute(ManageGroupServiceImpl.GROUPSLIST);
				
				GroupCategoryMap currentMap = null;
				for (Group group : groupsList) {
                    if (group.getGroupId() == groupId)
						currentMap = group.getGroupCategoryMap();
			    
				}
				
				LhNewsItemHelper lhNewsItemHelper = new LhNewsItemHelper(currentMap, tomcatpath);
				NewsItemList newslist = lhNewsItemHelper.getAllNewsforTag(tagitem, criteria);
				logger.log(Level.INFO, "[ LhNewsProviderServiceImpl Number of news items returned getAllNewsforTag::::] " +newslist.size());
				lhNewsItemHelper.closeConnection();
				return newslist;
			}
			else{
				NewsItemList newsList = new NewsItemList();
				newsList.setRedirect(true);
				return newsList;
			}
		}
		catch(Exception ex){
			logger.log(Level.INFO,"LhNewsProviderServiceImpl -- Exception ....................  "+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	public  NewsItemList getGroupedNewsList(GroupPageCriteria criteria,int newsmode) {
		
		HashMap<TagItem, NewsItemList> hashMap = new HashMap<TagItem, NewsItemList>();
		try{
			logger.log(Level.INFO, "In getGroupedNewsList in LhNewsProviderServiceImpl");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			if(session!=null){
				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
				int userid = userInformation.getUserId();
				int industryId = userInformation.getUserSelectedIndustryID();
				int groupId = criteria.getGroupId(); 
				
				List<Group> groupsList = (List<Group>) session.getAttribute(ManageGroupServiceImpl.GROUPSLIST);
				GroupCategoryMap currentMap = null;
				for (Group group : groupsList) {
					if (group.getGroupId() == groupId)
						currentMap = group.getGroupCategoryMap();
					    break;
				}
				LhNewsItemHelper lhNewsItemHelper = new LhNewsItemHelper(currentMap, tomcatpath);
				NewsItemList newsItemList = lhNewsItemHelper.getGroupedNewsList(newsmode, userid,industryId );
				lhNewsItemHelper.closeConnection();
				return newsItemList;
			}
			else{
				NewsItemList newsList = new NewsItemList();
				newsList.setRedirect(true);
				return newsList;
			}
		}
		catch(Exception e){
			logger.log(Level.INFO,"Exception ....................  "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}	
	
	@Override
	public LhUser getUser(int uid, int nid) {
	try{
		LhNewsletterHelper newsletterHelper=new LhNewsletterHelper();
		return newsletterHelper.fetchUser(uid, nid);
	}catch (Exception e) {
		e.printStackTrace();
	}
		return null;
	}
}
