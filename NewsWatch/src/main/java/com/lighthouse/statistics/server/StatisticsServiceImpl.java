package com.lighthouse.statistics.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.server.ManageGroupServiceImpl;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.server.LhNewsProviderServiceImpl;
import com.lighthouse.statistics.client.domain.NewsStatistics;
import com.lighthouse.statistics.client.service.StatisticsService;
import com.lighthouse.statistics.server.helper.StatisticsHelper;
import com.login.client.UserInformation;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;

/**
 * 
 * @author nairutee@ensarm.com and kiran@ensarm.com
 * 
 */
public class StatisticsServiceImpl extends LhNewsProviderServiceImpl implements
		StatisticsService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(StatisticsServiceImpl.class.getName());

	/**
	 * fetches the news statistics data
	 * 
	 * @param groupId
	 *            - id of the current group for which statistics need to be
	 *            fetched
	 * @return
	 */
	/*public NewsStatistics getNewsStatisticsData(int groupId) {
		try {
			logger.log(Level.INFO,
					"[StatisticsServiceImpl ::: fetching news stats ::: getNewsStatisticsData()]");

			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			LhUser userInformation = (LhUser) session.getAttribute("userInfo");

			int userId = userInformation.getUserId();
			int newsCenterId = userInformation.getUserSelectedIndustryID();

			StatisticsHelper statisticsHelper = new StatisticsHelper(tomcatpath);
			NewsStatistics newsStatistics = new NewsStatistics();

			List<NewsItems> newsItemsForMostReadInGroupNews = statisticsHelper
					.getMostReadInGroupNews(groupId, userId, newsCenterId);
			newsStatistics
					.setMostReadInGroupNews(newsItemsForMostReadInGroupNews);

			List<NewsItems> newsItemsForMostReadInAllGroupsNews = statisticsHelper
					.getMostReadInAllGroupsNews(newsCenterId);
			newsStatistics
					.setMostReadInAllGroupsNews(newsItemsForMostReadInAllGroupsNews);
			// fetch most read in group
			// fetch most read in all groups

			List<NewsItems> newsItemsForMostDiscussedInGroupNews = statisticsHelper
					.getMostDiscussedInGroupNews(groupId, userId, newsCenterId);
			newsStatistics
					.setMostDiscussedInGroupNews(newsItemsForMostDiscussedInGroupNews);

			List<NewsItems> newsItemsForMostDiscussedInAllGroupsNews = statisticsHelper
					.getMostDiscussedInAllGroupsNews(newsCenterId);
			newsStatistics
					.setMostDiscussedInAllGroupsNews(newsItemsForMostDiscussedInAllGroupsNews);
			// fetch most discussed in group
			// fetch most discussed in all groups
			return newsStatistics;
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.INFO,
					"[StatisticsServiceImpl ::: fetche news stats ::: getNewsStatisticsData()]");
		}
		return null;

	}*/

	/**
	 * fetches the news statistics data
	 * 
	 * @param groupId
	 *            - id of the current group for which statistics need to be
	 *            fetched
	 * @return
	 */
	public NewsStatistics getNewsStatisticsData(Group group) {
		try {
			logger.log(Level.INFO,
					"[StatisticsServiceImpl::: getNewsStatisticsData() initiated for GroupID::: "+group.getGroupId()+"]");

			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			LhUser userInformation = (LhUser) session.getAttribute("userInfo");

			int userId = userInformation.getUserId();
			int newsCenterId = userInformation.getUserSelectedIndustryID();

			StatisticsHelper statisticsHelper = new StatisticsHelper(tomcatpath);
			NewsStatistics newsStatistics = new NewsStatistics();

			// fetch most read in group
			List<NewsItems> newsItemsForMostReadInGroupNews = statisticsHelper.getMostReadInGroupNews(group, userId, newsCenterId);
			newsStatistics.setMostReadInGroupNews(newsItemsForMostReadInGroupNews);

			// fetch most read in all groups
			List<NewsItems> newsItemsForMostReadInAllGroupsNews = statisticsHelper.getMostReadInAllGroupsNews(newsCenterId);
			newsStatistics.setMostReadInAllGroupsNews(newsItemsForMostReadInAllGroupsNews);

			// fetch most discussed in group
			List<NewsItems> newsItemsForMostDiscussedInGroupNews = statisticsHelper.getMostDiscussedInGroupNews(group, userId, newsCenterId);
			newsStatistics.setMostDiscussedInGroupNews(newsItemsForMostDiscussedInGroupNews);

			// fetch most discussed in all groups
			List<NewsItems> newsItemsForMostDiscussedInAllGroupsNews = statisticsHelper.getMostDiscussedInAllGroupsNews(newsCenterId);
			newsStatistics.setMostDiscussedInAllGroupsNews(newsItemsForMostDiscussedInAllGroupsNews);
			logger.log(Level.INFO, "[StatisticsServiceImpl ::: getNewsStatisticsData() completed for GroupID::: "+group.getGroupId()+"]");
			statisticsHelper.closeConnection();
			return newsStatistics;
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.INFO, "[StatisticsServiceImpl ::: getNewsStatisticsData() EXCEPTION!!! Returning NULL for GroupID::: "+group.getGroupId()+"]");
			return null;
		}
	}
	
	@Override
	public NewsStatistics getRefreshNewsStatisticsData(/*int groupId*/Group group,int newsmode) {
		try {
			logger.log(Level.INFO,"[StatisticsServiceImpl ::: getRefreshNewsStatisticsData() initiated for GroupId ::: "+group.getGroupId()+"]");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			if (session != null) {
				UserInformation userInformation = (UserInformation) session
						.getAttribute("userInfo");
				int userid = userInformation.getUserId();
				int newsCenterId = userInformation.getUserSelectedIndustryID();

				List<Group> groupsList = (List<Group>) session.getAttribute(ManageGroupServiceImpl.GROUPSLIST);
				GroupCategoryMap currentMap = null;
				for (Group grp : groupsList) {
					if (grp.getGroupId() == group.getGroupId())
						currentMap = group.getGroupCategoryMap();
				}

				NewsStatistics newsStatistics = new NewsStatistics();
				StatisticsHelper statisticsHelper = new StatisticsHelper(currentMap,tomcatpath);

				List<NewsItems> newsItemsForMostReadInGroupNews = statisticsHelper.getRefreshMostReadInGroupNews(group,newsmode,userid);
				newsStatistics.setMostReadInGroupNews(newsItemsForMostReadInGroupNews);

				List<NewsItems> newsItemsForMostReadInAllGroupsNews = statisticsHelper.getMostReadInAllGroupsNews(newsCenterId);
				newsStatistics.setMostReadInAllGroupsNews(newsItemsForMostReadInAllGroupsNews);

				List<NewsItems> newsItemsForMostDiscussedInGroupNews = statisticsHelper.getRefreshMostDiscussedInGroupNews(group,newsmode,userid);
				newsStatistics.setMostDiscussedInGroupNews(newsItemsForMostDiscussedInGroupNews);

				List<NewsItems> newsItemsForMostDiscussedInAllGroupsNews = statisticsHelper.getMostDiscussedInAllGroupsNews(newsCenterId);
				newsStatistics.setMostDiscussedInAllGroupsNews(newsItemsForMostDiscussedInAllGroupsNews);
				logger.log(Level.INFO,"[StatisticsServiceImpl :::getRefreshNewsStatisticsData() completed for GroupID::: "+group.getGroupId()+"]");
				statisticsHelper.closeConnection();
				return newsStatistics;
			}
			logger.log(Level.INFO,"[StatisticsServiceImpl:: getRefreshNewsStatisticsData() SESSION NULL Returning NULL for GroupID::: "+group.getGroupId()+"]");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.INFO,"[StatisticsServiceImpl :::getRefreshNewsStatisticsData() EXCEPTION!! Returning NULL for GroupID::: "+group.getGroupId()+"]");
			return null;
		}
	}
	
	@Override
	public List<NewsItems> getFavoriteItems(Group group, boolean refreshed) {
		try{
			logger.log(Level.INFO,"[ StatisticsServiceImpl ----- getFavoriteItems() initiated for GROUPID :: "+group.getGroupId()+"]");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			LhUser lhUser  = (LhUser)session.getAttribute("userInfo");
			int ncid = lhUser.getUserSelectedNewsCenterID();
			int userId = lhUser.getUserId();
			StatisticsHelper statisticsHelper = new StatisticsHelper(tomcatpath);
			List<NewsItems> favItemsList =  null;
			if(refreshed){
				List<Group> groupsList = (List<Group>) session.getAttribute(ManageGroupServiceImpl.GROUPSLIST);
				GroupCategoryMap currentMap = null;
				for (Group grp : groupsList) {
					if (grp.getGroupId() == group.getGroupId())
						currentMap = group.getGroupCategoryMap();
				}
				favItemsList = statisticsHelper.getRefreshedFavoriteItems(currentMap);
				statisticsHelper.closeConnection();
				return favItemsList;
			}else
				favItemsList = statisticsHelper.getFavoriteItems(group, userId, ncid, null);
				
			logger.log(Level.INFO,"[ StatisticsServiceImpl ----- getFavoriteItems() completed for GROUPID :: "+group.getGroupId()+"]");
			statisticsHelper.closeConnection();
			return favItemsList;
		}catch(Exception e){
			logger.log(Level.INFO,"[ StatisticsServiceImpl ----- getFavoriteItems() EXCEPTION!!! Returning NULL!! for GROUPID :: "+group.getGroupId()+"]");
			e.printStackTrace();
			return null;
		}
	}
}
