package com.lighthouse.feed.server;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONException;

import com.admin.client.UserAdminInformation;
import com.lighthouse.admin.server.LHAdminInformationServiceImpl;
import com.lighthouse.admin.server.db.LHAdminHelper;
import com.lighthouse.feed.client.domain.Feed;
import com.lighthouse.feed.client.service.FeedService;
import com.lighthouse.newsletter.server.LhNewsletterHelper;
import com.login.client.UserInformation;
import com.login.server.db.AllocateResources;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

public class FeedServiceImpl extends LHAdminInformationServiceImpl implements FeedService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Logger logger = Logger.getLogger(FeedServiceImpl.class.getName());
	private ServletContext context =null;
	private String tomcatpath=null;
	private FeedHelper feedHelper=null;
	
	@Override
	public void init( ) throws ServletException {
		context =getServletContext();
		String fileName = getServletContext().getRealPath("config/catalystConfig.xml");
		FeedManager.CONFIGFILENAME=fileName;
		tomcatpath=(String)context.getAttribute(AllocateResources.TOMCATPATH);
		if(feedHelper==null)
		feedHelper=new FeedHelper(tomcatpath);
	}
	@Override
	public ArrayList<Feed> getFeedUrlForCatalyst(Integer ncid) {
		try{
			FeedManager feedManager=new FeedManager();
			ArrayList<Feed> siloFeedList = feedManager.getFeedUrlListForCatalyst(ncid);
			return siloFeedList;
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed to fetch feed url ..",e);
			return null;
		}
	}

	@Override
	public HashMap<String, ArrayList<NewsItems>> syncFeed(String url) {
		/*String response="Failure";
		FeedManager feedManager = new FeedManager();
	//	FeedHelper feedHelper=new FeedHelper();
		HashMap<Integer, ArrayList<NewsItems>> map=null;
		HashMap<String, ArrayList<NewsItems>> newsMap=new HashMap<String, ArrayList<NewsItems>>();
		ArrayList<NewsItems> newsItems=null;
		try{
			HttpSession session = getThreadLocalRequest().getSession(false);
			UserInformation user = (UserInformation) session.getAttribute("userInfo");
			Integer ncid = user.getUserSelectedNewsCenterID();
			String feedContent=feedManager.getPageContent(url);
			//String fileName = getServletContext().getRealPath("config/catalystConfig.xml");
			String feedSourceName=feedManager.getFeedName(url,ncid);
			if(feedContent!=null){
				ArrayList<HashMap<String, String>> feedMapList=feedHelper.parseFeed(feedContent);
				if(feedMapList!=null){
					map=feedHelper.saveNewsFeed(feedMapList, ncid,url,user.getUserId(),feedSourceName);
					for(int isSaved:map.keySet()){
						newsItems=map.get(isSaved);
						 if(isSaved==1)
							 response="Success";
						 if(isSaved==0)
							 response="exist";
						 if(isSaved==-1){
							 response="Failed to save";
						 }
					}
				}
			}
		
			feedHelper.closeConnection();	
		}catch (JSONException e) {
			response="Invalid Url";
			logger.log(Level.INFO, "Failed to save feed items ..",e);
			
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed to sync feed items ..",e);
		}finally{
			newsMap.put(response, newsItems);
		}
		return newsMap;*/
		return null;
	}

	@Override
	public NewsItemList getFeedSourceNewsItems(String feedSourceUrl,String feedSourceName,Integer ncid) {
		NewsItemList newsItems=null;
		try{
			FeedHelper feedHelper=new FeedHelper(tomcatpath);
			newsItems=feedHelper.getNewsItemforFeedSource(feedSourceUrl,feedSourceName,ncid);
			feedHelper.closeConnection();
			return newsItems;
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed to fetch news items for feed ..",e);
		}
		return null;
	}

	@Override
	public NewsItemList getFeedItemsForReview(Integer ncid) {
		NewsItemList newsItems=null;
		try{
			FeedHelper feedHelper=new FeedHelper(tomcatpath);
			newsItems=feedHelper.getIndustryFeedItems(ncid);
			feedHelper.closeConnection();
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed to fetch catalyst  ..",e);
		}
		return newsItems;
	}
    /**
     * This method is responsible for returning the list of categories of the particular catalyst 
     */
	@Override
	public ArrayList getCategoryNames(int industryid, String industryName) {
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
    /**
     * This method is responsible for returning the list of primary tags of the particular catalyst
     */
	@Override
	public ArrayList fillprimaryTaglist(int industryId, String industryName) {
		ArrayList list = new ArrayList();
		LHAdminHelper helper = new LHAdminHelper();
		list = helper.fillprimaryTaglist(industryId, industryName);
		helper.closeConnection();
		return list;
	}
	/**
	 * This method is responsible for returning the list of tags of the particular catalyst
	 */
	 @Override
	public ArrayList getTagNames(String industryName, String categoryName) {
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
		public TagItem markAsTopNews(Long newsId, Integer ncid,boolean markStatus) {
			try{
				FeedHelper feedHelper=new FeedHelper();
				TagItem tagItem=feedHelper.markAsTopFeed(newsId,ncid,markStatus);
				feedHelper.closeConnection();
				return tagItem;
			}catch (Exception e) {
				logger.log(Level.INFO, "Failed to mark as top news..",e);
			}
			return null;
		}
	@Override
	public NewsItemList getTopFeedNews(Integer ncid) {
		NewsItemList newsItems=null;
		try{
			FeedHelper feedHelper=new FeedHelper(tomcatpath);
			newsItems=feedHelper.getIndustryTopFeedNews(ncid);
			feedHelper.closeConnection();
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed to fetch catalyst  ..",e);
		}
		return newsItems;
	}
	@Override
	public boolean deleteFeedNews(Long newsId) {
		try{
			FeedHelper feedHelper=new FeedHelper();
			boolean b=feedHelper.deleteNewsFeed(newsId);
			feedHelper.closeConnection();
			return b;
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed to fetch catalyst  ..",e);
		}
		return false;
	}
	/**
	 * This method is responsible for returning from session
	 */
	@Override
	public void removeFromSession() {
		super.removeFromSession(); 
		
	}
	/**
	 * This method is responsible for returning the siloLogo of the particular catalyst
	 */
	@Override
	public String getSiloLogo() {
		String logoImagePath = (String) context.getAttribute("siloLogo");
		return logoImagePath;
	}
	
	@Override
	public String getLastEditorialNewsletterDelivery(int ncid) {
		logger.log(Level.INFO, "[FeedServiceImpl --- getLastEditorialNewsletterDelivery initiated --- getLastEditorialNewsletterDelivery() for ncid:: "+ncid+"]");
		try {
			FeedHelper feedHelper=new FeedHelper();
			Timestamp datetime = feedHelper.getLastNewsLetterDelivery(ncid);
			logger.log(Level.INFO, "[FeedServiceImpl --- getLastEditorialNewsletterDelivery fetched --- getLastEditorialNewsletterDelivery():: "+datetime+" for ncid:: "+ncid+"]");
			feedHelper.closeConnection();
			return datetime.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "[LhNewsletterServlet --- Unable to getLastNewsDeliveryTime EXCEPTION!!--- getLastNewsDeliveryTime() for ncid:: "+ncid+"]");
			return null;
		}
	}
}
