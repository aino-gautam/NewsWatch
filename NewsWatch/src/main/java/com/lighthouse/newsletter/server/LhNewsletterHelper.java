package com.lighthouse.newsletter.server;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.appUtils.server.helper.AppUtilsHelper;
import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.server.helper.GroupHelper;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.main.server.helper.LhNewsItemHelper;
import com.lighthouse.newsletter.client.domain.NewsLetterStats;
import com.lighthouse.newsletter.client.domain.NewsletterInformation;
import com.lighthouse.newsletter.client.domain.UserNewsletterAlertConfig;
import com.lighthouse.report.client.domain.ReportItem;
import com.lighthouse.report.client.domain.ReportItemList;
import com.lighthouse.report.server.helper.ReportsHelper;
import com.lighthouse.statistics.server.helper.StatisticsHelper;
import com.mysql.jdbc.PreparedStatement;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.categorydb.CategoryHelper;
import com.newscenter.server.db.DBHelper;
import com.newscenter.server.exception.CategoryHelperException;

/**
 * 
 * @author prachi & sachin 
 *
 */
public class LhNewsletterHelper extends DBHelper{

	Logger logger=Logger.getLogger(UserNewsletterAlertConfigHelper.class.getName());
	private String tomcatPath;

	public LhNewsletterHelper(){
		
	}
	
	/**
	 * Fetches the userMap according to the alert,depending on whether user has subscribed to individual alert or merged alert. 
	 * @param lastNewsDeliveryTime
	 * @param userId
	 * @return
	 */
	public HashMap<LhUser, HashMap<UserNewsletterAlertConfig, NewsletterInformation>> fetchAllUsersNewsItems(String weeklyNewsletterDay, Timestamp lastNewsDeliveryTime, int userId, int ncid){
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchAllUsersNewsItems() initated]");
		long start = System.currentTimeMillis();
		HashMap<UserNewsletterAlertConfig, NewsletterInformation> alertNewsInfoMap = null;
		HashMap<LhUser,HashMap<UserNewsletterAlertConfig,NewsletterInformation>> userNewsInfoMap=new HashMap<LhUser,HashMap<UserNewsletterAlertConfig,NewsletterInformation>>();
		
		try {
			//Step 1: fetch a list of all users who have subscribed to newsletters
			String userSubscriptionQuery = getSubscribedUsersQuery(userId,ncid);
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(userSubscriptionQuery);
			HashMap<Integer, Integer> userIdMap = new HashMap<Integer, Integer>();
			while(rs.next()){
				
				int userid = rs.getInt("UserId");
				int durationleft = rs.getInt("DurationLeft");
				userIdMap.put(userid, durationleft);
			}
			rs.close();
			stmt.close();
			
			String adminSubscriptionQuery = getSubscribedAdminQuery();
			Statement stmt1 = getConnection().createStatement();
			ResultSet rs1 = stmt1.executeQuery(adminSubscriptionQuery);
			while(rs1.next()){
				
				int userid = rs1.getInt("UserId");
			//	int ncid =rs.getInt("NewsCenterId");
				int durationleft = rs1.getInt("DurationLeft");
				userIdMap.put(userid, durationleft);
			}
			rs1.close();
			stmt1.close();
			for(Integer userid : userIdMap.keySet()){
				int durationleft = userIdMap.get(userid);
				if(durationleft > 0 ){
					//Step 2: fetch the user record
					LhUser user = fetchUser(userid, ncid);
					LhUserPermission lhUserPermission = user.getUserPermission();
					
					if (!(lhUserPermission == null)) {
						if (lhUserPermission.isMailAlertPermitted() == 1) { // check if mails are permitted

							//Step 3: check if user has a merged alert(isSingle==1)
							UserNewsletterAlertConfig mergedAlert = getMergedAlert(
									userid, ncid);

							//Step 4: fetch all alerts of the user for the newscenter. 
							ArrayList<UserNewsletterAlertConfig> alertList = getUserAlerts(
									userid, ncid);

							//Step 5: Check if the user has a merged alert or not and accordingly fetch the newslist. In case of merged alerts, 
							//frequency of the alert is not checked.
							if (mergedAlert != null)
								alertNewsInfoMap = fetchMergedAlertNews(mergedAlert, alertList,lhUserPermission, lastNewsDeliveryTime);
							else
								alertNewsInfoMap = fetchIndividualAlertNews(alertList, lhUserPermission,lastNewsDeliveryTime, weeklyNewsletterDay);

							//Step 6: populate the userNewsMap with the user and corresponding alertnewsmap
							if (alertNewsInfoMap != null) {
								userNewsInfoMap.put(user, alertNewsInfoMap);
							}
						}
					}
				}	
			}
			
			closeConnection();
			logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchAllUsersNewsItems() completed :: userNewsMap size:: "+userNewsInfoMap.size()+"  ]");
			long elapsed = System.currentTimeMillis() - start;
			logger.log(Level.INFO,"TIME REQUIRED TO FETCH ALL USERS ITEMS = " + elapsed + "ms");
			
			return userNewsInfoMap;
		}catch (SQLException e) {
			logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchAllUsersNewsItems() EXCEPTION !!!!! Returning NULL   ]");
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * fetches the merged alerts news list
	 * @param mergedAlert - The merged alert UserNewsletterAlertConfig object
	 * @param alertList - list of all user alerts
	 * @param userId - id of the user
	 * @param ncid - newscenter id
	 * @param lastNewsDeliveryTime - the last time of delivery of the newsletter
	 * @return HashMap<UserNewsletterAlertConfig,NewsItemList> map of config object vs. newslist
	 */
	public HashMap<UserNewsletterAlertConfig, NewsletterInformation> fetchMergedAlertNews(UserNewsletterAlertConfig mergedAlert,
			ArrayList<UserNewsletterAlertConfig> alertList, LhUserPermission lhUserPermission, Timestamp lastNewsDeliveryTime){
		
		 long start = System.currentTimeMillis();
		
		if(alertList != null){
			int userId = lhUserPermission.getUserId();
			int ncid = lhUserPermission.getNewsCenterId(); 
			
			logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchMergedAlertNews() initated  for USERID :: "+userId+" and NCID:: "+ncid+"]");
			ArrayList<Group> alertGroupList= new ArrayList<Group>();
			ArrayList<Group> ANDModeGroupList=new ArrayList<Group>();
			ArrayList<Group> ORModeGroupList=new ArrayList<Group>();
			
			
			NewsletterInformation mergedNewsletterInformation = new NewsletterInformation();
			HashMap<UserNewsletterAlertConfig, NewsletterInformation> alertNewsInfoMap=null;
			HashMap<TagItem, List<NewsItems>> mergedPrimaryTagNewsMap = new HashMap<TagItem, List<NewsItems>>();
			
			ReportItemList mergedReportsList = null;
			ReportItemList mergedAndReportsList = null;
			
			HashMap<String, List<NewsItems>> mergedPulseNewsMap = null;
			HashMap<String, List<NewsItems>> mergedAndPulseNewsMap = null;
			
			String concatedNewTagsQuery="";
			String concatedExistingTagsQuery="";
			String concatedUpdateNewTagsStatusQuery="";
			
			// adds all the groups of each alert into a single alertGroupList
			for(UserNewsletterAlertConfig alertConfig:alertList){
				alertGroupList.addAll(alertConfig.getAlertGroupList());
			}
			if(alertGroupList.size()>0){ 
				for(Group group: alertGroupList){
					if(group.getNewsFilterMode()==1)
						ANDModeGroupList.add(group);
					else
						ORModeGroupList.add(group);
				}
				
			   //fetch news for or mode
			   if(ORModeGroupList.size() > 0){
				   try{
					   HashMap<String, String> hashMap=concateGroupQuery(ORModeGroupList, ncid, userId, lastNewsDeliveryTime, false);
					   // fetch news
					   if(hashMap.size()>0){
						   concatedNewTagsQuery=hashMap.get("newTagsQuery");
						   concatedExistingTagsQuery=hashMap.get("existingTagsQuery");
						   concatedUpdateNewTagsStatusQuery=hashMap.get("updateNewTagsStatusQuery");
						   LhNewsItemHelper newsHelper=new LhNewsItemHelper(null, tomcatpath);
						   mergedPrimaryTagNewsMap = newsHelper.getAllNewsItemsForUser(concatedNewTagsQuery,concatedExistingTagsQuery,concatedUpdateNewTagsStatusQuery,ncid);
						   newsHelper.closeConnection();
					   }
					}catch(Exception ex){
						logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchMergedAlertNews() EXCEPTION in for concateGroupQuery/ getAllNewsItemsForUser ]");
						ex.printStackTrace();
					}
				}
				
			   //fetch news for AND mode
				if(ANDModeGroupList.size()>0){
					try {
						CategoryHelper catHelper = new CategoryHelper(ncid, userId);
						HashMap<TagItem, List<NewsItems>> primaryTagsAndNewsMap =null;
						
						for(Group group: ANDModeGroupList){
							CategoryMap categoryMap = catHelper.getCategories();
							GroupHelper grpHelper=new GroupHelper();
							GroupCategoryMap groupCategoryMap = grpHelper.getGroupCategoryMapWithSelections(group, categoryMap);
							group.setGroupCategoryMap(groupCategoryMap);
							
							LhNewsItemHelper lhNewsHelper = new LhNewsItemHelper(groupCategoryMap,tomcatpath);
							primaryTagsAndNewsMap = lhNewsHelper.getAllNewsItemsForUserAndMode(group,userId,ncid,lastNewsDeliveryTime);
							lhNewsHelper.closeConnection();
							
							if(primaryTagsAndNewsMap.size() > 0){
								for(TagItem tag : mergedPrimaryTagNewsMap.keySet()){
									for(TagItem tagitem : primaryTagsAndNewsMap.keySet()){
										if(tag.getTagId() == tagitem.getTagId()){
											List<NewsItems> orNewsList = mergedPrimaryTagNewsMap.get(tag);
											List<NewsItems> andNewsList = primaryTagsAndNewsMap.get(tagitem);
											List<NewsItems> mergedNewsList = getConcatenatedList(orNewsList,andNewsList);
											mergedPrimaryTagNewsMap.put(tag, mergedNewsList);
										}
									}
								}
							}
							catHelper.closeConnection();
							grpHelper.closeConnection();
						}
					}catch (CategoryHelperException e) {
						logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchMergedAlertNews() EXCEPTION in for ANDModeGroupList/ getAllNewsItemsForUser ]");
						e.printStackTrace();
					}
				}
				mergedNewsletterInformation.setNewsMap(mergedPrimaryTagNewsMap);	
				
				//fetch reports
				if(lhUserPermission.isReportsPermitted() == 1){
					mergedAndReportsList = getReportsForAlert(ncid, userId, ANDModeGroupList, 1);
					mergedReportsList = getConcatenatedReportsList(mergedReportsList, mergedAndReportsList);
					mergedReportsList = getReportsForAlert(ncid, userId, ORModeGroupList, 0);
					
					mergedNewsletterInformation.setReportsList(mergedReportsList);
				}

				
				// fetch pulse
				if(lhUserPermission.isPulsePermitted() == 1){
					 mergedPulseNewsMap = getPulseForUserAlert(ORModeGroupList, userId, ncid, 0);
					 mergedAndPulseNewsMap = getPulseForUserAlert(ANDModeGroupList, userId, ncid, 1);
					 if(mergedAndPulseNewsMap.size() > 0){
						List<NewsItems> orNewsList = mergedPulseNewsMap.get("groupMostReadNews");
						List<NewsItems> andNewsList = mergedAndPulseNewsMap.get("groupMostReadNews");
						List<NewsItems> mergedNewsList = getConcatenatedList(orNewsList,andNewsList);
						mergedPulseNewsMap.put("groupMostReadNews", mergedNewsList);
						
						List<NewsItems> orNewsList1 = mergedPulseNewsMap.get("groupMostDiscussedNews");
						List<NewsItems> andNewsList1 = mergedAndPulseNewsMap.get("groupMostDiscussedNews");
						List<NewsItems> mergedNewsList1 = getConcatenatedList(orNewsList1,andNewsList1);
						mergedPulseNewsMap.put("groupMostDiscussedNews", mergedNewsList1);
						
						if(mergedPulseNewsMap.get("portalMostReadNews").size()==0 && mergedAndPulseNewsMap.get("portalMostReadNews").size() > 0)
							mergedPulseNewsMap.put("portalMostReadNews", mergedAndPulseNewsMap.get("portalMostReadNews"));
						
						if(mergedPulseNewsMap.get("portalMostDiscussedNews").size()==0 && mergedAndPulseNewsMap.get("portalMostDiscussedNews").size() > 0)
							mergedPulseNewsMap.put("portalMostDiscussedNews", mergedAndPulseNewsMap.get("portalMostDiscussedNews"));
					}
					 
					 mergedNewsletterInformation.setMostReadInGroupNews(mergedPulseNewsMap.get("groupMostReadNews"));
					 mergedNewsletterInformation.setMostDiscussedInGroupNews(mergedPulseNewsMap.get("groupMostDiscussedNews"));
					 mergedNewsletterInformation.setMostReadInAllGroupsNews(mergedPulseNewsMap.get("portalMostReadNews"));
					 mergedNewsletterInformation.setMostDiscussedInAllGroupsNews(mergedPulseNewsMap.get("portalMostDiscussedNews"));
				}
				
				// fetch favorites
				if(lhUserPermission.isFavoriteGroupPermitted() == 1){
					List<NewsItems> favItemsList = getUserFavoriteNewsItems(userId, ncid, lastNewsDeliveryTime);
					mergedNewsletterInformation.setFavoriteItems(favItemsList);
				}

				//populates the hashmap with an alert config object and its newslist
				if(mergedNewsletterInformation != null){
					alertNewsInfoMap=new HashMap<UserNewsletterAlertConfig, NewsletterInformation>();
					alertNewsInfoMap.put(mergedAlert, mergedNewsletterInformation);
				}
			}
			logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchMergedAlertNews()  completed ]");
			long elapsed = System.currentTimeMillis() - start;
			logger.log(Level.INFO,"TIME REQUIRED FOR MERGED ALERT GENERATION = " + elapsed + "ms");
			return alertNewsInfoMap;
		}
		return null;
	}
	
	private HashMap<UserNewsletterAlertConfig, NewsletterInformation> fetchIndividualAlertNews(ArrayList<UserNewsletterAlertConfig> alertList, 
			LhUserPermission lhUserPermission, Timestamp lastNewsDeliveryTime, String weeklyNewsletterDay){
		int userId = lhUserPermission.getUserId();
		int ncid = lhUserPermission.getNewsCenterId();
		
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchIndividualAlertNews()  initiated :: for USERID :: "+userId+" and NCID :: "+ncid+"   ]");
		
		HashMap<UserNewsletterAlertConfig, NewsletterInformation> alertNewsInfoMap=new HashMap<UserNewsletterAlertConfig,NewsletterInformation>();
		
		
		/*
		 * iterates all the alerts of the user in the alert list, checks if the frequency of the alert is daily then it fetches the news for
		 * that alert and populates the hashmap with an alert config object and 
		 * its newslist
		 * If the frequency of the alert is weekly then it checks, if current day is a thursday then only it will fetch the news for
		 * that alert and populate the hashmap with an alert config object and its newslist
		 */
		if(alertList != null){
			for(UserNewsletterAlertConfig config:alertList){  
				NewsletterInformation newsletterInformation = null;
				try {
					if(config.getFrequency().equalsIgnoreCase("Daily"))
						newsletterInformation = getIndividualAlertNews(config, ncid, lhUserPermission, lastNewsDeliveryTime, false);
					else if(config.getFrequency().equalsIgnoreCase("Weekly")){
						String today = getCurrentDay();
						if(today.equalsIgnoreCase(weeklyNewsletterDay))
							newsletterInformation = getIndividualAlertNews(config, ncid, lhUserPermission, lastNewsDeliveryTime, true);
					}
				} catch (CategoryHelperException e) {
					logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchIndividualAlertNews() EXCEPTION in iteration alert list / getIndividualAlertNews !!!!! ]");
					e.printStackTrace();
				}
				//populates the hashmap with an alert config object and its newslist
				if(newsletterInformation != null){
						alertNewsInfoMap.put(config, newsletterInformation);
				}
			}
		}
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchIndividualAlertNews()  completed :: alertNewsMap size "+alertNewsInfoMap.size()+"   ]");
		return alertNewsInfoMap;
	}
	
	/**
	 * populate the given user id's user object
	 * @param userid
	 * @return UserInformation object
	 */
	public LhUser fetchUser(int userId, int ncid){
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchUser() :: userid :: "+userId+" :::: ncid :::"+ncid+" initated  ]");
		LhUser user = null;
		String query1 = "select u.UserId, u.IndustryEnumId, u.email, u.FirstName, u.LastName, ie.Name, ie.Description from user u,industryenum ie where u.IndustryEnumId = ie.IndustryEnumId and u.UserId = "+userId;
		try {
			Statement stmt1 = getConnection().createStatement();
			ResultSet rs1 = stmt1.executeQuery(query1);
			while(rs1.next()){
				user = new LhUser();
				user.setEmail(rs1.getString("email"));
				user.setFirstname(rs1.getString("FirstName"));
				user.setLastname(rs1.getString("LastName"));
				user.setIndustryNewsCenterName(rs1.getString("Name"));
			//	user.setPeriod(period);
				user.setUserSelectedNewsCenterURL(rs1.getString("Description"));
				user.setUserId(rs1.getInt("UserId"));
				user.setUserSelectedIndustryID(rs1.getInt("IndustryEnumId"));
				
				LhUserPermission userPermission = getUserPermission(userId, ncid);
				user.setUserPermission(userPermission);
				
			}
			rs1.close();
			stmt1.close();
			logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchUser() :: userid :: "+userId+" :::: completed  ]");
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "[ LhNewsletterHelper ::: fetchUser() :: userid :: "+userId+" ::::  EXCEPTION!!!! returning NULL  ]");
			return null;
		}
	}
	
	private LhUserPermission getUserPermission(int userId,int userSelectedNewsCenterID) throws SQLException {
		LhUserPermission lhUserPermission=null;
		try{
			String query= "SELECT * FROM user_permission where userId = "+userId+" and newsCenterId = "+userSelectedNewsCenterID;
			Statement stmt=getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				 lhUserPermission=new LhUserPermission();
				 lhUserPermission.setId(Integer.parseInt(rs.getString("id")));
				 lhUserPermission.setMailAlertPermitted((Integer.parseInt(rs.getString("mailAlert"))));
				 lhUserPermission.setGroupsPermitted(Integer.parseInt(rs.getString("groups")));
				 lhUserPermission.setReportsPermitted((Integer.parseInt(rs.getString("reports"))));
				 lhUserPermission.setCommentsPermitted((Integer.parseInt(rs.getString("comments"))));
				 lhUserPermission.setViewsPermitted((Integer.parseInt(rs.getString("views"))));
				 lhUserPermission.setSearchPermitted((Integer.parseInt(rs.getString("search"))));
				 lhUserPermission.setPrimaryHeadLinePermitted((Integer.parseInt(rs.getString("primaryHeadline"))));
				 lhUserPermission.setRssermitted((Integer.parseInt(rs.getString("rss"))));
				 lhUserPermission.setSharePermitted((Integer.parseInt(rs.getString("share"))));
				 lhUserPermission.setPulsePermitted((Integer.parseInt(rs.getString("pulse"))));
				 lhUserPermission.setFavoriteGroupPermitted((Integer.parseInt(rs.getString("favoriteGroup"))));
				 lhUserPermission.setUserId(userId);
				 lhUserPermission.setNewsCenterId((userSelectedNewsCenterID));
			}
			rs.close();
			stmt.close();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return lhUserPermission;
	}
	
	private String getCurrentDay(){
		String dayNameQuery = "select DAYNAME(CURDATE())";
		String day = "";
		try {
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(dayNameQuery);
			while(rs.next()){
				 day = rs.getString(1);
			}
			rs.close();
			stmt.close();
			return day;
		} catch (SQLException e) {
			logger.log(Level.INFO, "lhNewsletterHelper ::: getSubscribedUsersQuery() ::: exception!! "+ e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * fetches the query to find all the users who have subscribed for newsletters
	 * @param userId
	 * @return
	 */
	private String getSubscribedUsersQuery(int userId, int ncid){
		logger.log(Level.INFO, "lhNewsletterHelper ::: getSubscribedUsersQuery() ::: initiated");
		String userSubscriptionQuery = "";
		
		if(userId == -1)
			userSubscriptionQuery = "select UserId, Duration,DurationLeft,isSubscribed,Period,SubscriptionDate from usersubscription where isSubscribed= 1 and Period = 1 and NewsCenterId="+ncid;
		else
			userSubscriptionQuery = "select UserId, Duration,DurationLeft,isSubscribed,Period,SubscriptionDate from usersubscription where isSubscribed= 1 and Period = 1 and UserID = "+userId+" and NewsCenterId="+ncid;
		
		
		/*String dayNameQuery = "select DAYNAME(CURDATE())";
		
		try {
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(dayNameQuery);
			while(rs.next()){
				String day = rs.getString(1);
				if(day.equalsIgnoreCase("Thursday")){
					 if(userId == -1)
						userSubscriptionQuery = "select UserId, Duration,DurationLeft,isSubscribed,Period from usersubscription where isSubscribed= 1 and NewsCenterId="+ncid;
					 else
						 userSubscriptionQuery = "select UserId, Duration,DurationLeft,isSubscribed,Period,SubscriptionDate from usersubscription where isSubscribed= 1 and UserId = "+userId+" and NewsCenterId="+ncid; 
				}
				else{
					if(userId == -1)
						userSubscriptionQuery = "select UserId, Duration,DurationLeft,isSubscribed,Period,SubscriptionDate from usersubscription where isSubscribed= 1 and Period = 1 and NewsCenterId="+ncid;
					else
						userSubscriptionQuery = "select UserId, Duration,DurationLeft,isSubscribed,Period,SubscriptionDate from usersubscription where isSubscribed= 1 and Period = 1 and UserID = "+userId+" and NewsCenterId="+ncid;
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.log(Level.INFO, "lhNewsletterHelper ::: getSubscribedUsersQuery() ::: exception!! "+ e.getMessage());
			e.printStackTrace();
		}*/
		logger.log(Level.INFO, "lhNewsletterHelper ::: getSubscribedUsersQuery() ::: completed");
		logger.log(Level.INFO, "userSubscriptionQuery ::: " + userSubscriptionQuery);
		return userSubscriptionQuery;
	}
	
	
	private String getSubscribedAdminQuery(){
		logger.log(Level.INFO, "lhNewsletterHelper ::: getSubscribedUsersQuery() ::: initiated");
	
		String adminSubscriptionQuery = "select UserId,Duration,DurationLeft,isSubscribed,Period,SubscriptionDate from usersubscription where isSubscribed= 1 and Period = 1 and UserId in(select userid from user where isAdmin=1);";
		
		logger.log(Level.INFO, "lhNewsletterHelper ::: getSubscribedadminQuery() ::: completed");
		logger.log(Level.INFO, "userSubscriptionQuery ::: " + adminSubscriptionQuery);
		return adminSubscriptionQuery;
	}
	
	/**
	 * gets the merged alert for the user
	 * @param userId - id of the user
	 * @param ncid - newscenter id
	 * @return UserNewsletterAlertConfig (merged alert object)
	 */ 
	private UserNewsletterAlertConfig getMergedAlert(int userId, int ncid){
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getMergedAlert() for USER ::: "+userId+" and NCID :: "+ncid+"  initated]");
		UserNewsletterAlertConfigHelper helper=new UserNewsletterAlertConfigHelper();
		UserNewsletterAlertConfig mergedAlert = helper.getMergedAlertForUser(userId, ncid);
		helper.closeConnection();
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getMergedAlert() for USER ::: "+userId+" and NCID :: "+ncid+"  completed]");
		return mergedAlert;
	}
	
	/**
	 * fetches all the alerts for a user
	 * @param userId
	 * @param ncid
	 * @return ArrayList<UserNewsletterAlertConfig> (list of all alerts)
	 */
	public ArrayList<UserNewsletterAlertConfig> getUserAlerts(int userId, int ncid){
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getUserAlerts() for USER ::: "+userId+" and NCID :: "+ncid+"  initiated]");
		UserNewsletterAlertConfigHelper helper=new UserNewsletterAlertConfigHelper();
		ArrayList<UserNewsletterAlertConfig> alertList=new ArrayList<UserNewsletterAlertConfig>();
		alertList=helper.getAllUserAlertList(userId,ncid );
		helper.closeConnection();
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getUserAlerts() for USER ::: "+userId+" and NCID :: "+ncid+"  completed]");
		return alertList;
	}
	
	
	/**
	 * This will give the combined alertNewsList for all groups in an alert
	 * @param alertConfig
	 * @param ncid
	 * @param userid
	 * @param lastNewsDeliveryTime
	 * @return
	 * @throws CategoryHelperException
	 */
	public NewsletterInformation getIndividualAlertNews(UserNewsletterAlertConfig alertConfig,int ncid, LhUserPermission lhUserPermission, Timestamp lastNewsDeliveryTime, boolean isWeekly) throws CategoryHelperException{
		int userId = lhUserPermission.getUserId();
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getIndividualAlertNews()  initiated :: for USERID :: "+userId+" and NCID :: "+ncid+"   ]");
		 long start = System.currentTimeMillis();
		CategoryHelper catHelper=new CategoryHelper(ncid, userId);
		NewsletterInformation newsletterInformation = new NewsletterInformation();
		HashMap<TagItem, List<NewsItems>> primaryTagNewsMap = null;
		ReportItemList reportsList = null;
		ReportItemList reportItemsAndList = null;
		HashMap<String, List<NewsItems>> pulseNewsAndMap = null;
		HashMap<String, List<NewsItems>> pulseNewsMap = null;
		PrimaryTagNewsMap	sortedPrimaryTagNewsMap=null;
		LhNewsItemHelper itemHelper=new LhNewsItemHelper(null, tomcatPath);
		
		ArrayList<Group> grpList=alertConfig.getAlertGroupList();
		ArrayList<Group> ANDModeGroupList=new ArrayList<Group>();
		ArrayList<Group> ORModeGroupList=new ArrayList<Group>();
	
		if(grpList != null){
			for(Group group: grpList){
				if(group.getNewsFilterMode()==1){
					ANDModeGroupList.add(group);
				}else {
					ORModeGroupList.add(group);
				}
			}
		}
		
		LhNewsItemHelper newsHelper=new LhNewsItemHelper(null, tomcatpath);
		
		try{
			//iterates for each group
			if(ORModeGroupList.size()>0){
				//this will fetch the newsItems
				logger.log(Level.INFO, "[ LhNewsletterHelper ::: getIndividualAlertNews()  OR MODE :: for USERID :: "+userId+" and NCID :: "+ncid+"   ]");
				try{
					HashMap<String, String> hashMap=concateGroupQuery(ORModeGroupList, ncid, userId, lastNewsDeliveryTime, isWeekly);
					if(hashMap.size()>0){
						String concatedNewTagsQuery=hashMap.get("newTagsQuery");
						String concatedExistingTagsQuery=hashMap.get("existingTagsQuery");
						String concatedUpdateNewTagsStatusQuery=hashMap.get("updateNewTagsStatusQuery");
						//populate the primary headlined news items
						primaryTagNewsMap = newsHelper.getAllNewsItemsForUser(concatedNewTagsQuery,concatedExistingTagsQuery,concatedUpdateNewTagsStatusQuery,ncid); 
					}
					
				}catch(Exception ex){
					logger.log(Level.INFO, "[ LhNewsletterHelper ::: getIndividualAlertNews()  EXCEPTION in concateGroupQuery/getAllNewsItemsForUser :: for USERID :: "+userId+" and NCID :: "+ncid+"   ]");
					ex.printStackTrace();
				}
			}
			if(ANDModeGroupList.size()>0){
				logger.log(Level.INFO, "[ LhNewsletterHelper ::: getIndividualAlertNews()  AND MODE :: for USERID :: "+userId+" and NCID :: "+ncid+"   ]");
				HashMap<TagItem, List<NewsItems>> primaryTagsAndNewsMap = null;
				GroupHelper grpHelper=new GroupHelper();
				for(Group group: ANDModeGroupList){
					CategoryMap categoryMap = catHelper.getCategories();
					GroupCategoryMap groupCategoryMap = grpHelper.getGroupCategoryMapWithSelections(group, categoryMap);
					group.setGroupCategoryMap(groupCategoryMap);
				
					LhNewsItemHelper lhNewsHelper=new LhNewsItemHelper(groupCategoryMap,tomcatpath);
					primaryTagsAndNewsMap = lhNewsHelper.getAllNewsItemsForUserAndMode(group,userId,ncid,lastNewsDeliveryTime);
					lhNewsHelper.closeConnection();
					/*if(primaryTagsAndNewsMap.size() > 0){
						for(TagItem tag : primaryTagNewsMap.keySet()){
							for(TagItem tagitem : primaryTagsAndNewsMap.keySet()){
								if(tag.getTagId() == tagitem.getTagId()){
									List<NewsItems> orNewsList = primaryTagNewsMap.get(tag);
									List<NewsItems> andNewsList = primaryTagsAndNewsMap.get(tagitem);
									List<NewsItems> mergedNewsList = getConcatenatedList(orNewsList,andNewsList);
									primaryTagNewsMap.put(tag, mergedNewsList);
								}
							}
						}
					}*/
					
					if(primaryTagsAndNewsMap!=null && primaryTagsAndNewsMap.size() > 0){
						if (primaryTagNewsMap!=null && primaryTagNewsMap.size() > 0) {
							
							if(primaryTagsAndNewsMap.size()>primaryTagNewsMap.size()){
								
								for(TagItem tagitem : primaryTagsAndNewsMap.keySet()){
									for (TagItem tag : primaryTagNewsMap.keySet()) {
											if (tag.getTagId() == tagitem.getTagId()) {
												List<NewsItems> orNewsList = primaryTagNewsMap.get(tag);
												List<NewsItems> andNewsList = primaryTagsAndNewsMap.get(tagitem);
												List<NewsItems> mergedNewsList = getConcatenatedList(orNewsList, andNewsList);
												primaryTagNewsMap.put(tag,mergedNewsList);
											}
										}
								}
						}else{
							for(TagItem tag : primaryTagNewsMap.keySet()){
								for(TagItem tagitem : primaryTagsAndNewsMap.keySet()){
									if(tag.getTagId() == tagitem.getTagId()){
										List<NewsItems> orNewsList = primaryTagNewsMap.get(tag);
										List<NewsItems> andNewsList = primaryTagsAndNewsMap.get(tagitem);
										List<NewsItems> mergedNewsList = getConcatenatedList(orNewsList,andNewsList);
										primaryTagNewsMap.put(tag, mergedNewsList);
									}
								}
							}
						}
						}else{
							primaryTagNewsMap=primaryTagsAndNewsMap;
						}
					}
					
				}
				grpHelper.closeConnection();
			}
			
			sortedPrimaryTagNewsMap=itemHelper.sortHashMap(primaryTagNewsMap);
			newsletterInformation.setNewsMap(sortedPrimaryTagNewsMap);
			
			// fetch reports
			if(lhUserPermission.isReportsPermitted() == 1){
				reportsList = getReportsForAlert(ncid, userId, ORModeGroupList, 0);
				reportItemsAndList = getReportsForAlert(ncid, userId, ANDModeGroupList, 1);
				reportsList = getConcatenatedReportsList(reportsList, reportItemsAndList);
				newsletterInformation.setReportsList(reportsList);
			}
			
			//fetch pulse
			if(lhUserPermission.isPulsePermitted() == 1){
				pulseNewsMap = getPulseForUserAlert(ORModeGroupList, userId, ncid, 0);
				pulseNewsAndMap = getPulseForUserAlert(ANDModeGroupList, userId, ncid, 1);
				if(pulseNewsAndMap.size() > 0){
					List<NewsItems> orNewsList = pulseNewsMap.get("groupMostReadNews");
					List<NewsItems> andNewsList = pulseNewsAndMap.get("groupMostReadNews");
					List<NewsItems> mergedNewsList = getConcatenatedList(orNewsList,andNewsList);
					pulseNewsMap.put("groupMostReadNews", mergedNewsList);
					
					List<NewsItems> orNewsList1 = pulseNewsMap.get("groupMostDiscussedNews");
					List<NewsItems> andNewsList1 = pulseNewsAndMap.get("groupMostDiscussedNews");
					List<NewsItems> mergedNewsList1 = getConcatenatedList(orNewsList1,andNewsList1);
					pulseNewsMap.put("groupMostDiscussedNews", mergedNewsList1);
					
					if(pulseNewsMap.get("portalMostReadNews").size()==0 && pulseNewsAndMap.get("portalMostReadNews").size() > 0)
						pulseNewsMap.put("portalMostReadNews", pulseNewsAndMap.get("portalMostReadNews"));
					
					if(pulseNewsMap.get("portalMostDiscussedNews").size()==0 && pulseNewsAndMap.get("portalMostDiscussedNews").size() > 0)
						pulseNewsMap.put("portalMostDiscussedNews", pulseNewsAndMap.get("portalMostDiscussedNews"));
				}
				newsletterInformation.setMostReadInGroupNews(pulseNewsMap.get("groupMostReadNews"));
				newsletterInformation.setMostDiscussedInGroupNews(pulseNewsMap.get("groupMostDiscussedNews"));
				newsletterInformation.setMostReadInAllGroupsNews(pulseNewsMap.get("portalMostReadNews"));
				newsletterInformation.setMostDiscussedInAllGroupsNews(pulseNewsMap.get("portalMostDiscussedNews"));
			}
			
			// fetch favorites
			if(lhUserPermission.isFavoriteGroupPermitted() == 1){
				Group favoriteGroup = getFavoriteGroup(userId, ncid);
				List<NewsItems> favItemsList = getUserFavoriteNewsItems(userId, ncid, lastNewsDeliveryTime);
				newsletterInformation.setFavoriteGroup(favoriteGroup);
				newsletterInformation.setFavoriteItems(favItemsList);
			}
		}
		catch (Exception e) {
			logger.log(Level.INFO, "[ LhNewsletterHelper ::: getIndividualAlertNews()  EXCEPTION !!! for USERID :: "+userId+" and NCID :: "+ncid+"   ]");
			e.printStackTrace();
		}finally{
			catHelper.closeConnection();
			newsHelper.closeConnection();
		}
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getIndividualAlertNews()  completed :: for USERID :: "+userId+" and NCID :: "+ncid+"   ]");
		
		long elapsed = System.currentTimeMillis() - start;
		logger.log(Level.INFO,"TIME REQUIRED TO GET INDIVIDUAL ALERT NEWS = " + elapsed + "ms");
		return newsletterInformation;
	}
	
	/**
	 * gets the reports for an alert
	 * @param ncid
	 * @param userId
	 * @param groupsList
	 * @param filtermode
	 * @return
	 */
	public ReportItemList getReportsForAlert(int ncid, int userId, ArrayList<Group> groupsList, int filtermode){
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getReportsForAlert() initiated for USERID :: "+userId+" and NCID :: "+ncid+" ]");
		try{
			ReportsHelper reportsHelper = new ReportsHelper();
			if(filtermode == 1){ // AND MODE
				ReportItemList mergedReportList = new ReportItemList();
				for(Group group : groupsList){
					ReportItemList reportsList = reportsHelper.getUserAlertReportItemsForAndMode(group, userId);
					mergedReportList = getConcatenatedReportsList(mergedReportList, reportsList);
				}
				logger.log(Level.INFO, "[ LhNewsletterHelper ::: getReportsForAlert() completed for USERID :: "+userId+" and NCID :: "+ncid+" ]");
				return mergedReportList;
			}else{ //OR MODE
				HashMap<String, String> reportsQueryMap = reportsHelper.concateGroupsReportQuery(groupsList, ncid, userId);
				if(reportsQueryMap.size()>0){
					String concatenatedReportsQuery = reportsQueryMap.get("reportsQuery");
					ReportItemList reportsList = reportsHelper.getUserAlertReportItems(concatenatedReportsQuery);
					logger.log(Level.INFO, "[ LhNewsletterHelper ::: getReportsForAlert() completed for USERID :: "+userId+" and NCID :: "+ncid+" ]");
					reportsHelper.closeConnection();
					return reportsList;
				}
			}
			reportsHelper.closeConnection();
		}catch(Exception ex){
			logger.log(Level.INFO, "[ LhNewsletterHelper ::: getReportsForAlert() EXCEPTION!!! for USERID :: "+userId+" and NCID :: "+ncid+" ]");
			ex.printStackTrace();
		}
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getReportsForAlert() completed RETURNING NULL!!! for USERID :: "+userId+" and NCID :: "+ncid+" ]");
		return null;
	}
	
	/**
	 * gets the favorite group for the user
	 * @param userId
	 * @param ncid
	 * @return
	 */
	public Group getFavoriteGroup(int userId, int ncid){
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getFavoriteGroup() initiated for USERID :: "+userId+" and NCID :: "+ncid+" ]");
		GroupHelper groupHelper = new GroupHelper();
		Group favoriteGroup = groupHelper.getFavoriteGroup(userId, ncid);
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getFavoriteGroup() completed for USERID :: "+userId+" and NCID :: "+ncid+" ]");
		return favoriteGroup;
	}
	
	/**
	 * gets the favorite news item list for an alert
	 * @param userId
	 * @param ncid
	 * @param datetime
	 * @return
	 */
	public List<NewsItems> getUserFavoriteNewsItems(int userId, int ncid, Timestamp datetime){
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getFavoriteNewsItems() initiated for USERID :: "+userId+" and NCID :: "+ncid+" ]");
		StatisticsHelper statisticsHelper = new StatisticsHelper(tomcatpath);
		List<NewsItems> favItemsList = statisticsHelper.getFavoriteItems(null, userId, ncid, datetime);
		statisticsHelper.closeConnection();
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getFavoriteNewsItems() completed for USERID :: "+userId+" and NCID :: "+ncid+" ]");
		return favItemsList;
	}
	
	/**
	 * gets the pulse news items for an alert
	 * @param groupList
	 * @param userId
	 * @param ncid
	 * @param filtermode
	 * @return
	 */
	public HashMap<String, List<NewsItems>> getPulseForUserAlert(ArrayList<Group> groupList, int userId, int ncid, int filtermode){
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getPulseForUserAlert() initiated for USERID :: "+userId+" and NCID :: "+ncid+" ]");
		HashMap<String, List<NewsItems>> statsMap = new HashMap<String, List<NewsItems>>();
		try{
			StatisticsHelper statisticsHelper = new StatisticsHelper(tomcatpath);
			if(filtermode == 1){ // AND MODE
				statsMap = statisticsHelper.getUserAlertPulseItemsForAndMode(groupList, userId, ncid);
				statisticsHelper.closeConnection();
				logger.log(Level.INFO, "[ LhNewsletterHelper ::: getPulseForUserAlert() AND mode completed for USERID :: "+userId+" and NCID :: "+ncid+" ]");
			}else{ // OR MODE
				HashMap<String, String> pulseQueryMap = statisticsHelper.concateGroupsPulseQuery(groupList, ncid, userId);
				if(pulseQueryMap.size()>0){
					statsMap = statisticsHelper.getUserAlertPulseItems(pulseQueryMap);
					statisticsHelper.closeConnection();
				}
				logger.log(Level.INFO, "[ LhNewsletterHelper ::: getPulseForUserAlert() OR mode completed for USERID :: "+userId+" and NCID :: "+ncid+" ]");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return statsMap;
	}
	
	public List<NewsItems> getConcatenatedList(List<NewsItems> orNewsList, List<NewsItems> andNewsList) {
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getConcatenatedList()  initiated  ]");
		List<NewsItems> mergedNewslist= new ArrayList<NewsItems>();
		ArrayList<Integer> mergedIdlist= new ArrayList<Integer>();
		try{
			if(orNewsList != null){
				for(NewsItems news : orNewsList){
					mergedNewslist.add(news);
					mergedIdlist.add(news.getNewsId());
				}
			}
			if(andNewsList != null){
				for(NewsItems newsItem : andNewsList){
					if(!(mergedIdlist.contains(newsItem.getNewsId()))){
						mergedNewslist.add(newsItem);
						mergedIdlist.add(newsItem.getNewsId());
					}
				}
			}
		}catch (Exception e) {
			logger.log(Level.INFO, "[ LhNewsletterHelper ::: getConcatenatedList()  EXCEPTION!!!!  ]");
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getConcatenatedList()  completed :: mergeList Size :: "+mergedNewslist.size()+"  ]");
		return mergedNewslist;
	}

	public ReportItemList getConcatenatedReportsList(ReportItemList list1, ReportItemList list2){
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getConcatenatedReportsList()  initiated  ]");
		ReportItemList mergedNewslist= new ReportItemList();
		ArrayList<Integer> mergedIdlist= new ArrayList<Integer>();
		try{
			if(list1 != null){
				for(ReportItem report : list1){
					mergedNewslist.add(report);
					mergedIdlist.add(report.getNewsId());
				}
			}
			if(list2 != null){
				for(ReportItem reportItem : list2){
					if(!(mergedIdlist.contains(reportItem.getNewsId()))){
						mergedNewslist.add(reportItem);
						mergedIdlist.add(reportItem.getNewsId());
					}
				}
			}
		}catch (Exception e) {
			logger.log(Level.INFO, "[ LhNewsletterHelper ::: getConcatenatedReportsList()  EXCEPTION!!!!  ]");
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: getConcatenatedReportsList()  completed :: mergeList Size :: "+mergedNewslist.size()+"  ]");
		return mergedNewslist;
	}
	
	/**
	 * This method will concatenate the queries if the groupFilterode is OR only. 
	 * @param groupList
	 * @param ncid
	 * @param userid
	 * @param lastNewsDeliveryTime
	 * @return
	 * @throws CategoryHelperException
	 */
	public HashMap<String, String> concateGroupQuery(ArrayList<Group> groupList,int ncid,int userid,Timestamp lastNewsDeliveryTime, boolean isWeekly) throws CategoryHelperException{
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: concateGroupQuery()  initiated for USERID:: "+userid+" and NCID :: "+ncid+" ]");
		CategoryHelper catHelper=new CategoryHelper(ncid, userid);
		String concatedNewTagsQuery="";
		String concatedExistingTagsQuery="";
		String concatedUpdateNewTagsStatusQuery="";
		Boolean concatFlag=false;
		Boolean updateQueryFlag=false;
		LhNewsItemHelper newsHelper=null;
		HashMap<String, String> groupQueryMap = new HashMap<String, String>();
		
		try{
			for(Group group:groupList){
				CategoryMap categoryMap = catHelper.getCategories();
				GroupHelper grpHelper=new GroupHelper();
				GroupCategoryMap groupCategoryMap = grpHelper.getGroupCategoryMapWithSelections(group, categoryMap);
				group.setGroupCategoryMap(groupCategoryMap);
				int filterMode = group.getNewsFilterMode();
				newsHelper=new LhNewsItemHelper(groupCategoryMap, tomcatPath);
				HashMap<String, String> queryMap = newsHelper.generateNewsQueryMapForAllNewsItems(userid, filterMode, lastNewsDeliveryTime,updateQueryFlag,ncid, isWeekly);
				if(queryMap.size()>0){
					updateQueryFlag=true;
					String newTagsQuery=queryMap.get("newTagsQuery");
					String existingTagsQuery=queryMap.get("existingTagsQuery");
					String updateNewTagsStatusQuery=queryMap.get("updateNewTagsStatusQuery");
					if(concatedExistingTagsQuery.equalsIgnoreCase("")){
						concatedExistingTagsQuery=existingTagsQuery;
						concatedNewTagsQuery=newTagsQuery;
						concatedUpdateNewTagsStatusQuery=updateNewTagsStatusQuery;
					}else{
						if(!concatFlag){
							concatedNewTagsQuery="("+concatedNewTagsQuery+") UNION ("+newTagsQuery+")";
							concatedExistingTagsQuery="("+concatedExistingTagsQuery+") UNION ("+existingTagsQuery+")";
							concatedUpdateNewTagsStatusQuery=concatedUpdateNewTagsStatusQuery+""+updateNewTagsStatusQuery;
							concatFlag=true;
						}
						else{
							concatedNewTagsQuery=concatedNewTagsQuery+" UNION ("+newTagsQuery+")";
							concatedExistingTagsQuery=concatedExistingTagsQuery+" UNION ("+existingTagsQuery+")";
							concatedUpdateNewTagsStatusQuery=concatedUpdateNewTagsStatusQuery+""+updateNewTagsStatusQuery;
						}
					}
				}
				grpHelper.closeConnection();
		    }	
			catHelper.closeConnection();
			
			concatedUpdateNewTagsStatusQuery+= ")";
			groupQueryMap=new HashMap<String, String>();
			if(!concatedNewTagsQuery.equalsIgnoreCase(""))
				groupQueryMap.put("newTagsQuery", concatedNewTagsQuery);
			if(!concatedExistingTagsQuery.equalsIgnoreCase(""))
				groupQueryMap.put("existingTagsQuery", concatedExistingTagsQuery);
			if(!concatedUpdateNewTagsStatusQuery.equalsIgnoreCase(")"))
				groupQueryMap.put("updateNewTagsStatusQuery", concatedUpdateNewTagsStatusQuery);
		}catch (Exception e) {
			logger.log(Level.INFO," [ Concatenation of the  newTagsQuery,existingTagsQuery and updateNewTagsStatusQuery failed  ] ");
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[ LhNewsletterHelper ::: concateGroupQuery()  completed for USERID:: "+userid+" and NCID :: "+ncid+" ]");
		return groupQueryMap;
	}
	
	
	/**
	 * This method will save the new newsLetterTemplate given by the user. 
	 * @param newsletterTemplate
	 * @param newsletterFooter
	 * @param newscenterid
	 */
	public void saveNewsletterTemplate(String newsletterTemplate, String newsletterFooter, int newscenterid) {
		try{
			logger.log(Level.INFO," [ LhNewsletterHelper :::: Saving of the newsLetterHeaderTemplate initiated for ncid::: "+newscenterid+"  ] ");
			Statement stmt = getConnection().createStatement();
			String query = "update industryenum set newsLetterHeaderTemplate = '"+newsletterTemplate+"' where IndustryEnumId = "+newscenterid+"";
			stmt.executeUpdate(query);
			stmt.close();
			logger.log(Level.INFO," [ LhNewsletterHelper :::: Saving of the newsLetterHeaderTemplate completed for ncid::: "+newscenterid+"  ] ");
			
			logger.log(Level.INFO," [ LhNewsletterHelper :::: Saving of the newsLetterFooterTemplate initiated for ncid::: "+newscenterid+"  ] ");
			Statement stmt2 = getConnection().createStatement();
			String query1 = "update industryenum set newsLetterFooterTemplate = '"+newsletterFooter+"' where IndustryEnumId = "+newscenterid+"";
			stmt2.executeUpdate(query1);
			stmt2.close();
			logger.log(Level.INFO," [ LhNewsletterHelper :::: Saving of the newsLetterFooterTemplate completed for ncid::: "+newscenterid+"  ] ");
		}
		catch(Exception e){
			logger.log(Level.INFO," [ LhNewsletterHelper :::: Saving of the newsLetterTemplate failed  ] ");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will fetch the newsLetterTemplate saved by the user. 
	 * @param newscenterid
	 * @return
	 */
	public ArrayList<String> getNewsletterTemplate(int newscenterid) {
		logger.log(Level.INFO," [ LhNewsletterHelper :::: getNewsletterTemplate() initiated for ncid::: "+newscenterid+"  ] ");
		ArrayList<String> template = new ArrayList<String>();
		String header="", footer = ""; 
		try {
			Connection conn = (Connection) getConnection();
			String query = "select newsLetterHeaderTemplate,newsLetterFooterTemplate from industryenum where IndustryEnumId = "+ newscenterid;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				 header = rs.getString("newsLetterHeaderTemplate");
				 footer = rs.getString("newsLetterFooterTemplate");
			}
			template.add(header);
			template.add(footer);
			rs.close();
			stmt.close();
		} catch (Exception ex) {
			logger.log(Level.INFO," [ LhNewsletterHelper :::: getNewsletterTemplate() EXCEPTION!!! for ncid::: "+newscenterid+"  ] ");
			ex.printStackTrace();
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: getNewsletterTemplate() completed for ncid::: "+newscenterid+"  ] ");
		return template;
		
	}

	public boolean saveNewsletterDelivery(int newscenterid, String time) {
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsletterDelivery() initiated for ncid::: "+newscenterid+"  ] ");
		boolean value=false;
		try{
			 Statement stmt = getConnection().createStatement();
			 String query=  "update newsletterdelivery set timeOfDelivery = TIME_FORMAT('"+time+"', '%H:%i:%s') where newsCenterId ="+ +newscenterid;
			 stmt.executeUpdate(query);
			 stmt.close();
			 value=true;
			
		}catch(Exception e){
			logger.log(Level.INFO," [ Saving the newsLetterDeliverTime failed  ] ");
			value=false;
				
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsletterDelivery() completed for ncid::: "+newscenterid+"  ] ");
		return value;
		
	}
			
	public Timestamp getLastNewsLetterDelivery(int ncid) throws SQLException{
		logger.log(Level.INFO," [ LhNewsletterHelper :::: getLastNewsLetterDelivery() initiated for ncid::: "+ncid+"  ] ");
		Timestamp delivery=null;
		Connection connection=null;
		try{
			connection=getConnection(); 
			String query="SELECT delivery FROM newsletterdelivery where newsCenterId="+ncid;
			Statement statement=connection.createStatement();
			ResultSet resultSet=statement.executeQuery(query);
			while(resultSet.next()){
				delivery=resultSet.getTimestamp("delivery");
			}
			resultSet.close();
			statement.close();
		}catch (Exception e) {
			logger.log(Level.INFO," [ LhNewsletterHelper :::: getLastNewsLetterDelivery() EXCEPTION!!!! for ncid::: "+ncid+"  ] ");
			e.printStackTrace();
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: getLastNewsLetterDelivery() completed for ncid::: "+ncid+"  ] ");
		return delivery;
	}
	
	/**
	 * updates the last time of delivery of newsletters for a particular newscenterid with the current timestamp
	 * @param ncid
	 * @return
	 * @throws SQLException
	 */
	public boolean updateLastNewsLetterDelivery(int ncid) throws SQLException{
		logger.log(Level.INFO," [ LhNewsletterHelper :::: updateLastNewsLetterDelivery() initiated for ncid::: "+ncid+"  ] ");
		Connection connection=null;
		try{
			connection=getConnection(); 
			String query = "update newsletterdelivery set delivery = CURRENT_TIMESTAMP where newsCenterId="+ncid;
			Statement statement=connection.createStatement();
			statement.executeUpdate(query);
			statement.close();
			logger.log(Level.INFO," [ LhNewsletterHelper :::: updateLastNewsLetterDelivery() completed for ncid::: "+ncid+"  ] ");
			return true;
		}catch (Exception e) {
			logger.log(Level.INFO," [ LhNewsletterHelper :::: updateLastNewsLetterDelivery() EXCEPTION!!! for ncid::: "+ncid+"   ::: Returning false ] ");
			e.printStackTrace();
			return false;
		}
	}
	
	public Time getNewsLetterTimeOfDelivery(int ncid) throws SQLException{
		logger.log(Level.INFO," [ LhNewsletterHelper :::: getNewsLetterTimeOfDelivery() initiated for ncid::: "+ncid+"  ] ");
		Time timeOfDelivery=null;
		Connection connection=null;
		try{
			connection=getConnection(); 
			String query="SELECT timeOfDelivery FROM newsletterdelivery where newsCenterId="+ncid;
			Statement statement=connection.createStatement();
			ResultSet resultSet=statement.executeQuery(query);
			while(resultSet.next()){
				timeOfDelivery=resultSet.getTime("timeOfDelivery");
			}
			resultSet.close();
			statement.close();
		}catch (Exception e) {
			logger.log(Level.INFO," [ LhNewsletterHelper :::: getNewsLetterTimeOfDelivery() EXCEPTION!!!!! for ncid::: "+ncid+"  ] ");
			e.printStackTrace();
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: getNewsLetterTimeOfDelivery() completed for ncid::: "+ncid+"  ] ");
		return timeOfDelivery;
	}
	
	public ArrayList<Integer> getNewsCenterList() throws SQLException{
		logger.log(Level.INFO," [ LhNewsletterHelper :::: getNewsCenterList() initiated ] ");
		ArrayList<Integer> newsCenterList=new ArrayList<Integer>();
		Connection connection=null;
		try{
			connection=getConnection(); 
			String query="SELECT newscenterid FROM newscenter";
			Statement statement=connection.createStatement();
			ResultSet resultSet=statement.executeQuery(query);
			while(resultSet.next()){
				newsCenterList.add(resultSet.getInt("newscenterid"));
			}
			resultSet.close();
			statement.close();
		}catch (Exception e) {
			logger.log(Level.INFO," [ LhNewsletterHelper :::: getNewsCenterList() EXCEPTION!!!! ] ");
			e.printStackTrace();
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: getNewsCenterList() completed :::: newsCenterList size :: "+newsCenterList.size()+"   ] ");
		return newsCenterList;
	}
	
	public int getUserReportPermission(int userid, int ncid) {
		logger.log(Level.INFO," [ LhNewsletterHelper :::: getUserReportPermission() initiated for USERID :: "+userid+" and NCID :: "+ncid+" ] ");
		int isReportPermitted=0;
		String query="SELECT reports FROM user_permission where userid="+userid+" and newscenterid="+ncid;
		try{
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				isReportPermitted=rs.getInt("reports");
			}
			rs.close();
			stmt.close();
		}catch (Exception e) {
			logger.log(Level.INFO," [ LhNewsletterHelper :::: getUserReportPermission() EXCEPTION!!!! for USERID :: "+userid+" and NCID :: "+ncid+" ] ");
			e.printStackTrace();
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: getUserReportPermission() completed for USERID :: "+userid+" and NCID :: "+ncid+" :: Returning isReporPermitted = "+isReportPermitted+"] ");
		return isReportPermitted;
	}
	
	
	public boolean saveNewsletterStats(NewsLetterStats stats) {
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsletterStats() initiated for ncid::: "+stats.getNcId()+"  ] ");
		boolean value=false;
		try{
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			
			String formattedDate = stats.getNewsSentDate();
			logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsletterStats() formatted date:: "+formattedDate+"  ] ");
			String cdate = formatter.format(stats.getNewsOpenedDate());
			logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsletterStats() cdate::: "+cdate+"  ] ");
			
			 Statement stmt = getConnection().createStatement();
			 String query=  "insert into newsletter_statistics(userId,newscenterId,newsSent,newsOpened) values("+stats.getUserId()+","+stats.getNcId()+",'"+formattedDate+"','"+cdate+"');";
			 stmt.executeUpdate(query);
			 stmt.close();
			 value=true;
		}catch(Exception e){
			e.printStackTrace();
			logger.log(Level.INFO," [ Saving the saveNewsletterStats failed  ] ");
			value=false;
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsletterStats() completed for ncid::: "+stats.getNcId()+"  ] ");
		return value;
	}
	
	public String fetchHeaderConfiguration(int newsCenterId){
		logger.log(Level.INFO," [ LhNewsletterHelper :::: fetchHeaderConfiguration() initiated for ncid::: "+newsCenterId+"  ] ");
		String headerXml=null;
		try {

			Statement stmt = getConnection().createStatement();
			String query = "select headerXml from newsletter_design where ncid = "
					+ newsCenterId + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Blob xml = rs.getBlob("headerXml");
				InputStream in = xml.getBinaryStream();
				InputStreamReader isr = new InputStreamReader(in, "UTF8");
				BufferedReader br = new BufferedReader(isr);
				StringBuffer buffer = new StringBuffer();
				int ch;
				while ((ch = in.read()) > -1) {
					buffer.append((char) ch);
				}
				br.close();
				headerXml = buffer.toString();
			}
			rs.close();
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();
			logger.log(Level.INFO," [ fetching the Newsletterheader configuration failed  ] ");
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: fetchHeaderConfiguration() completed for ncid::: "+newsCenterId+"  ] ");
		return headerXml;
	}



	public String fetchOutlineConfiguration(int newsCenterId) {
		logger.log(Level.INFO," [ LhNewsletterHelper :::: fetchOutlineConfiguration() initiated for ncid::: "+newsCenterId+"  ] ");
		String headerXml=null;
		try {

			Statement stmt = getConnection().createStatement();
			String query = "select outlineXml from newsletter_design where ncid = "
					+ newsCenterId + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Blob xml = rs.getBlob("outlineXml");
				InputStream in = xml.getBinaryStream();
				InputStreamReader isr = new InputStreamReader(in, "UTF8");
				BufferedReader br = new BufferedReader(isr);
				StringBuffer buffer = new StringBuffer();
				int ch;
				while ((ch = in.read()) > -1) {
					buffer.append((char) ch);
				}
				br.close();
				headerXml = buffer.toString();
			}
			rs.close();
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();
			logger.log(Level.INFO," [ fetching the fetchOutlineConfiguration failed  ] ");
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: fetchOutlineConfiguration() completed for ncid::: "+newsCenterId+"  ] ");
		return headerXml;
	}



	public String fetchFooterConfiguration(int newsCenterId) {
		logger.log(Level.INFO," [ LhNewsletterHelper :::: fetchFooterConfiguration() initiated for ncid::: "+newsCenterId+"  ] ");
		String headerXml=null;
		try {
			Statement stmt = getConnection().createStatement();
			String query = "select footerXml from newsletter_design where ncid = "
					+ newsCenterId + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Blob xml = rs.getBlob("footerXml");
				InputStream in = xml.getBinaryStream();
				InputStreamReader isr = new InputStreamReader(in, "UTF8");
				BufferedReader br = new BufferedReader(isr);
				StringBuffer buffer = new StringBuffer();
				int ch;
				while ((ch = in.read()) > -1) {
					buffer.append((char) ch);
				}
				br.close();
				headerXml = buffer.toString();
			}
			rs.close();
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();
			logger.log(Level.INFO," [ fetching the fetchFooterConfiguration failed  ] ");
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: fetchFooterConfiguration() completed for ncid::: "+newsCenterId+"  ] ");
		return headerXml;
	}

	public String fetchContentConfiguration(int newsCenterId) {
		logger.log(Level.INFO," [ LhNewsletterHelper :::: fetchFooterConfiguration() initiated for ncid::: "+newsCenterId+"  ] ");
		String headerXml=null;
		try {

			Statement stmt = getConnection().createStatement();
			String query = "select contentXml from newsletter_design where ncid = "
					+ newsCenterId + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Blob xml = rs.getBlob("contentXml");
				InputStream in = xml.getBinaryStream();
				InputStreamReader isr = new InputStreamReader(in, "UTF8");
				BufferedReader br = new BufferedReader(isr);
				StringBuffer buffer = new StringBuffer();
				int ch;
				while ((ch = in.read()) > -1) {
					buffer.append((char) ch);
				}
				br.close();
				headerXml = buffer.toString();
			}
			rs.close();
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();
			logger.log(Level.INFO," [ fetching the fetchContentConfiguration failed  ] ");
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: fetchContentConfiguration() completed for ncid::: "+newsCenterId+"  ] ");
		return headerXml;
	}

	public boolean saveNewsLetterHeaderConfig(int newsCenterId, String xml) {
		
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsLetterHeaderConfig() initiated for ncid::: "+newsCenterId+"  ] ");
		boolean value=false;
		try{
			 Statement stmt = getConnection().createStatement();
			 String query= "update newsletter_design set headerXml='"+xml+"' where ncid = "+newsCenterId+";";
			 stmt.executeUpdate(query);
			 stmt.close();
			 value=true;
		}catch(Exception e){
			e.printStackTrace();
			logger.log(Level.INFO," [ Saving the saveNewsLetterHeaderConfig failed  ] ");
			value=false;
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsLetterHeaderConfig() completed for ncid::: "+newsCenterId+"  ] ");
		return value;
	}

	public boolean saveNewsLetterContentConfig(int newsCenterId, String xml) {
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsLetterContentConfig() initiated for ncid::: "+newsCenterId+"  ] ");
		boolean value=false;
		try{
			 Statement stmt = getConnection().createStatement();
			 String query=   "update newsletter_design set contentXml='"+xml+"' where ncid = "+newsCenterId+";";
			 stmt.executeUpdate(query);
			 stmt.close();
			 value=true;
		}catch(Exception e){
			e.printStackTrace();
			logger.log(Level.INFO," [ Saving the saveNewsLetterContentConfig failed  ] ");
			value=false;
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsLetterContentConfig() completed for ncid::: "+newsCenterId+"  ] ");
		return value;
	}

	public boolean saveNewsLetterFooterConfig(int newsCenterId, String xml) {
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsLetterFooterConfig() initiated for ncid::: "+newsCenterId+"  ] ");
		boolean value=false;
		try{
			 Statement stmt = getConnection().createStatement();
			 String query=  "update newsletter_design set footerXml='"+xml+"' where ncid = "+newsCenterId+";";
			 stmt.executeUpdate(query);
			 stmt.close();
			 value=true;
		}catch(Exception e){
			e.printStackTrace();
			logger.log(Level.INFO," [ Saving the saveNewsLetterFooterConfig failed  ] ");
			value=false;
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsLetterFooterConfig() completed for ncid::: "+newsCenterId+"  ] ");
		return value;
	}

	public boolean saveNewsletterOutlineConfig(int newsCenterId, String xml) {
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsletterOutlineConfig() initiated for ncid::: "+newsCenterId+"  ] ");
		boolean value=false;
		try{
			 Statement stmt = getConnection().createStatement();
			 String query=  "update newsletter_design set outlineXml='"+xml+"' where ncid = "+newsCenterId+";";
			 stmt.executeUpdate(query);
			 stmt.close();
			 value=true;
		}catch(Exception e){
			e.printStackTrace();
			logger.log(Level.INFO," [ Saving the saveNewsletterOutlineConfig failed  ] ");
			value=false;
		}
		logger.log(Level.INFO," [ LhNewsletterHelper :::: saveNewsletterOutlineConfig() completed for ncid::: "+newsCenterId+"  ] ");
		return value;
	}

	public void saveNewsletterLogoImage(int newsCenterId,InputStream inputstream, int filelength, String type) {
		logger.log(Level.INFO, "[LhNewsletterHelper :::: saveNewsletterLogoImage() initiated for NCID :: "+newsCenterId+" ]");
		try {
			String query = "";
			if (filelength != 0) {
				if (type.equals("header")) {
					query = "update newsletter_design set logo=? where ncid = ?";

				} else if (type.equals("outline")) {
					query = "update newsletter_design set bgImg=? where ncid = ?";
				}
				else if (type.equals("siloLogo")) {
					query = "update industryenum set siloLogo=? where IndustryEnumId = ?";
				}

				Connection conn = (Connection) getConnection();
				PreparedStatement preparedstmt = null;
				preparedstmt = (PreparedStatement) conn.prepareStatement(query);
				preparedstmt.setBinaryStream(1, inputstream, filelength);
				preparedstmt.setInt(2, newsCenterId);
				preparedstmt.executeUpdate();
				preparedstmt.close();
			}
			logger.log(Level.INFO, "[LhNewsletterHelper :::: saveNewsletterLogoImage() completed for NCID :: "+newsCenterId+" ]");
		} catch (Exception ex) {
			logger.log(Level.INFO, "[LhNewsletterHelper :::: saveNewsletterLogoImage() EXCEPTION!! for NCID :: "+newsCenterId+" ]");
			ex.printStackTrace();
		}
	}

	/**
	 * gets the image url for the particular newscenter
	 * @param industryId
	 * @param type
	 * @param tomcatPath
	 * @return
	 */
	public String getImageURL(int industryId, String type, String tomcatPath, boolean checkIfFileExists) {
		logger.log(Level.INFO, "[LhNewsletterHelper :::: getImageURL() initiated for NCID :: "+industryId+" ]");
		String imgUrl = null;
		try {
			Statement stmt = getConnection().createStatement();
			String query = null;
			if(type.equals("siloLogo"))
				query=  "select siloLogo from industryenum where IndustryEnumId="+industryId;
			else
				query=  "select "+type+" from newsletter_design where ncid="+industryId;
			
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (rs.getBlob(type) != null) {
					File file = new File(tomcatPath + "/configImages//nc"
							+ industryId + "_" + type + ".jpg");
					System.out.println("file path in getImageURL "
							+ file.getPath());
					imgUrl = "configImages/nc" + industryId + "_" + type
							+ ".jpg";
					System.out.println("image url :::: " + imgUrl);
					if(checkIfFileExists){
						AppUtilsHelper utilHelper = new AppUtilsHelper();
						boolean ifFileExist = utilHelper.alreadyExists(imgUrl);
						if (!ifFileExist) {
							Blob blobimg = (Blob) rs.getBlob(type);
							InputStream x = blobimg.getBinaryStream();
							try {
								int size = x.available();
								if (size != 0) {
									OutputStream out = new FileOutputStream(file);
									byte b[] = new byte[size];
									x.read(b);
									out.write(b);
								}
								logger.log(Level.INFO, "[LhNewsletterHelper :::: getImageURL() logo created for NCID :: "+industryId+" ]");
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}else
							logger.log(Level.INFO, "[LhNewsletterHelper :::: getImageURL() logo exists for NCID :: "+industryId+" ]");
					}else{
						Blob blobimg = (Blob) rs.getBlob(type);
						InputStream x = blobimg.getBinaryStream();
						try {
							int size = x.available();
							if (size != 0) {
								OutputStream out = new FileOutputStream(file);
								byte b[] = new byte[size];
								x.read(b);
								out.write(b);
							}
							logger.log(Level.INFO, "[LhNewsletterHelper :::: getImageURL() logo created for NCID :: "+industryId+" ]");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[LhNewsletterHelper :::: getImageURL() completed for NCID :: "+industryId+" ]");
		return imgUrl;
	}
}

