package com.lighthouse.newsletter.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.appUtils.server.AppUtilsServiceImpl;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.newsletter.client.domain.NewsletterInformation;
import com.lighthouse.newsletter.client.domain.UserNewsletterAlertConfig;
import com.lighthouse.report.client.domain.ReportItem;
import com.lighthouse.report.client.domain.ReportItemList;
import com.lighthouse.utils.server.EncrypterDecrypter;
import com.login.client.UserInformation;
import com.login.server.MailServiceImpl;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.NewsletterServlet;

/**
 * Generates the userSpecific NewsLetter
 * @author sachin & prachi
 *
 */
public class LhNewsletterServlet extends NewsletterServlet{
    
	private static final long serialVersionUID = 7910188069221289855L;
	Logger logger = Logger.getLogger(LhNewsletterServlet.class.getName());
	boolean status = false;
	Integer ncid;
	String referrer=null;
	HttpServletRequest request;
	
	String headerXml ;
	String footerXml ;
	String contentXml;
	String outlineXml;
	
	HeaderDesignConfiguration header ;
	ContentDesignConfiguration content ;
	FooterDesignConfiguration footer;
	OutlineDesignConfiguration outline;
	private static String WEEKLYNEWSLETTERDAY = "WeeklyNewsletterDay";
	private String weeklyNewsletterDay;
	
	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		weeklyNewsletterDay = config.getInitParameter(WEEKLYNEWSLETTERDAY);
	}
	
	@Override
	public void fetchNews(){
		
		long start = System.currentTimeMillis();
		LhNewsletterHelper helper = new LhNewsletterHelper();
		headerXml = helper.fetchHeaderConfiguration(ncid);
		footerXml = helper.fetchFooterConfiguration(ncid);
		contentXml = helper.fetchContentConfiguration(ncid);
		outlineXml = helper.fetchOutlineConfiguration(ncid);
		
		header = new HeaderDesignConfiguration(headerXml);
		footer = new FooterDesignConfiguration(footerXml);
		content = new ContentDesignConfiguration(contentXml);
		outline = new OutlineDesignConfiguration(outlineXml);
		
		logger.log(Level.INFO, "[LhNewsletterServlet --- FETCH NEWS INITIATED --- fetchNews()]");
		Timestamp lastNewsDeliveryTime = getLastNewsDeliveryTime();
		HashMap<LhUser, HashMap<UserNewsletterAlertConfig, NewsletterInformation>> map = getUsersNewsItemsMap(lastNewsDeliveryTime,-1);
		if(map != null){
			if(map.size()>0)
			 	status = true;
			
			Integer getResult = generateNewsLetterContent(map); 
			if(getResult == 1)
				updateLastNewsDeliveryTime();
		}
		else
			logger.log(Level.INFO, "[LhNewsletterServlet --- USER NEWS ITEM MAP -- *** NULL ****  ----fetchNews()]");
		
		helper.closeConnection();
		logger.log(Level.INFO, "[LhNewsletterServlet --- FETCH NEWS COMPLETED --- fetchNews()]");
		long elapsed = System.currentTimeMillis() - start;
		logger.log(Level.INFO,"NEWSLETTER GENERATION TIME  = " + elapsed + "ms");

	}
	
	
	/**
	 * updates the newsletter delivery time to current timestamp
	 */
	private void updateLastNewsDeliveryTime(){
		logger.log(Level.INFO, "[LhNewsletterServlet --- updateLastNewsDeliveryTime initiated --- updateLastNewsDeliveryTime() for ncid:: "+ncid+"]");
		LhNewsletterHelper lhNewsletterHelper = new LhNewsletterHelper();
		try {
			Boolean updated = lhNewsletterHelper.updateLastNewsLetterDelivery(ncid);
			logger.log(Level.INFO, "[LhNewsletterServlet --- updateLastNewsDeliveryTime completed --- updateLastNewsDeliveryTime() for ncid:: "+ncid+"]");
			lhNewsletterHelper.closeConnection();
		} catch (SQLException e) {
			logger.log(Level.INFO, "[LhNewsletterServlet --- Unable to updateLastNewsDeliveryTime EXCEPTION!!--- updateLastNewsDeliveryTime() for ncid:: "+ncid+"]");
			e.printStackTrace();
		}
	}
	
	@Override
	public Timestamp getLastNewsDeliveryTime(){
		logger.log(Level.INFO, "[LhNewsletterServlet --- getLastNewsDeliveryTime initiated --- getLastNewsDeliveryTime() for ncid:: "+ncid+"]");
		LhNewsletterHelper lhNewsletterHelper = new LhNewsletterHelper();
		try {
			Timestamp datetime = lhNewsletterHelper.getLastNewsLetterDelivery(ncid);
			logger.log(Level.INFO, "[LhNewsletterServlet --- getLastNewsDeliveryTime fetched --- getLastNewsDeliveryTime():: "+datetime+" for ncid:: "+ncid+"]");
			lhNewsletterHelper.closeConnection();
			return datetime;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "[LhNewsletterServlet --- Unable to getLastNewsDeliveryTime EXCEPTION!!--- getLastNewsDeliveryTime() for ncid:: "+ncid+"]");
			return null;
		}
	}
	
	@Override
	public HashMap<LhUser, HashMap<UserNewsletterAlertConfig, NewsletterInformation>> getUsersNewsItemsMap(Timestamp lastNewsDeliveryTime, int userId){
		logger.log(Level.INFO, "[LhNewsletterServlet --- getUsersNewsItemsMap initiated --- getUsersNewsItemsMap()]");
		LhNewsletterHelper lhNewsletterHelper = new LhNewsletterHelper();
		HashMap<LhUser, HashMap<UserNewsletterAlertConfig, NewsletterInformation>> map = lhNewsletterHelper.fetchAllUsersNewsItems(weeklyNewsletterDay, lastNewsDeliveryTime,userId,ncid);
		if(map != null)
			logger.log(Level.INFO, "[LhNewsletterServlet --- getUsersNewsItemsMap completed --- getUsersNewsItemsMap() -- mapSize:: "+map.size()+"]");
		else
			logger.log(Level.INFO, "[LhNewsletterServlet --- getUsersNewsItemsMap completed --- getUsersNewsItemsMap() -- map is NULL]");
		lhNewsletterHelper.closeConnection();
		return map;
	}
	
	 /**
     * Generates the newsLetter content
     * @param userSelectionMap
	 * @param req 
	 * @param req 
     */
	
	public Integer generateNewsLetterContent(HashMap<LhUser, HashMap<UserNewsletterAlertConfig, NewsletterInformation>> userSelectionMap){ 
		logger.log(Level.INFO, "[LhNewsletterServlet --- generateNewsLetterContent initiated --- generateNewsLetterContent()]");
		String p="";
		Integer result = 0;
		try{
			logger.log(Level.INFO, "[LhNewsletterServlet ---iterating userSelectionMapp --- generateNewsLetterContent()]");
			for (LhUser userInfo : userSelectionMap.keySet()) {
				HashMap<UserNewsletterAlertConfig, NewsletterInformation> alertMap = (HashMap<UserNewsletterAlertConfig, NewsletterInformation>) userSelectionMap.get(userInfo);
				if( alertMap!=null){
					if(alertMap.size() > 0){
						for (UserNewsletterAlertConfig alertConfig : alertMap.keySet()) {
							  String dateOut = getCurrentDate();
							  logger.log(Level.INFO, "[LhNewsletterServlet --- currentDate is:"+ dateOut +" --- generateNewsLetterContent()]");
							 
							  logger.log(Level.INFO, "[LhNewsletterServlet --- creating Head Layout --- generateNewsLetterContent()]");
							  createHeadLayoutNew();
							 
							  logger.log(Level.INFO, "[LhNewsletterServlet --- creating Body Layout --- generateNewsLetterContent()]");
							  createBodyNew(dateOut, alertConfig.getAlertName(), userInfo);
							  
							  NewsletterInformation newsletterInformation = alertMap.get(alertConfig);
							  HashMap<TagItem, List<NewsItems>> newsMap = newsletterInformation.getNewsMap();
							  if(newsMap != null){
								  if (newsletterInformation.isNoTagSelected()) {
									  createFooterNew(userInfo);
									  sendMailForAlert(userInfo,alertConfig);
								  } else if (newsMap.size() != 0) {
									  for(TagItem primaryTag : newsMap.keySet()){
										  List<NewsItems> newslist = newsMap.get(primaryTag);
										  
										  if(userInfo.getUserPermission().isPrimaryHeadLinePermitted()==1)
											  createPrimaryHeadline(primaryTag);
										  
										  Iterator iter = newslist.iterator();
										  while (iter.hasNext()) {
											  NewsItems news = (NewsItems) iter.next();
											  createNewsNew(news, userInfo);
										  }
									  }
									  
									  if(userInfo.getUserPermission().isReportsPermitted()==1)
										  createReports(newsletterInformation.getReportsList(),userInfo);
									  if(userInfo.getUserPermission().isFavoriteGroupPermitted()==1)
										  createFavouritesNews(newsletterInformation.getFavoriteGroup(), newsletterInformation.getFavoriteItems(),userInfo);
									  if(userInfo.getUserPermission().isPulsePermitted()==1)
										  createPulse(newsletterInformation,userInfo,alertConfig);
									  
									  if(userInfo.getUserPermission().isReportsPermitted() == 0 && userInfo.getUserPermission().isFavoriteGroupPermitted() == 0
											  && userInfo.getUserPermission().isPulsePermitted() == 0){
										  stringBuilder.append("</tr></table>"); // ending tags for reports or favorites or pulse if none are permitted
									  }else{
										  stringBuilder.append("</td></tr></table>"); // ending tags for reports or favorites or pulse
									  }
									  
									  createFooterNew(userInfo);
									  sendMailForAlert(userInfo,alertConfig);
								  }
							  }
						}  //for end
					}
				}  // if end
			}
			result = 1;
		}catch (Exception e) {
			logger.log(Level.INFO, "[LhNewsletterServlet --- generateNewsLetterContent EXCEPTION!!! --- generateNewsLetterContent()]");
			e.printStackTrace();
			return 0;
		}
		logger.log(Level.INFO, "[LhNewsletterServlet --- generateNewsLetterContent completed --- generateNewsLetterContent()]");
		return result;
	}

	private void createPulse(NewsletterInformation newsletterInformation,LhUser userInfo, UserNewsletterAlertConfig alertConfig) {
		
		if(userInfo.getUserPermission().isReportsPermitted() == 0 && userInfo.getUserPermission().isFavoriteGroupPermitted() == 0)
			stringBuilder.append("<td width=\""+content.getSecondColumnWidth()+"\" align=\""+content.getSecondColumnAlign()+"\" valign=\""+content.getSecondColumnVerticalAlign()+"\" style=\"padding:"+content.getSecondColumnPadding()+";border-top:"+content.getSecondColumnBorder()+";\">");
		
		stringBuilder.append("<div>"+
				"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding: 4px; background-color: "+content.getPulseBackgroundColor()+"; margin-bottom: 5px; padding: 6px;\">"+
					"<tr>"+
						"<td style=\"color: "+content.getPulseHeaderColor()+"; border-bottom: "+content.getPulseHeaderHorizontalRulerColor()+"; font-weight: bold; font-size: 10pt; margin: 2px 0;\">Activities in the past 7 days</td>"+
					"</tr>");
		
		//Most Discussed News in a group
		if(newsletterInformation.getMostDiscussedInGroupNews()!=null)
			mostDiscussedInGroup(newsletterInformation.getMostDiscussedInGroupNews(),alertConfig,userInfo);
		
		//Most Read News in a group
		if(newsletterInformation.getMostReadInGroupNews()!=null)
			mostReadInGroup(newsletterInformation.getMostReadInGroupNews(),alertConfig,userInfo);
		
		// Most disussed in all group
		if(newsletterInformation.getMostDiscussedInAllGroupsNews()!=null)
			mostDiscussedInAllGroup(newsletterInformation.getMostDiscussedInAllGroupsNews(),userInfo);
		
		// Most read in all group
		if(newsletterInformation.getMostReadInAllGroupsNews()!=null)
			mostReadInAllGroup(newsletterInformation.getMostReadInAllGroupsNews(),userInfo);
		
		stringBuilder.append("</table>"+
					"</div>"); /*+
				"</td>"+
			"</tr>"+
		"</table>");*/
	}
	

	private void mostDiscussedInGroup(List<NewsItems> mostDiscussedInGroupNews, UserNewsletterAlertConfig alertConfig, LhUser userInfo) {
		stringBuilder
		.append("<tr>"
				+ "<td>"
				+ "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding: 0px; margin: 5px 0px;\">"
				+ "<tr>"
				+ "<td style=\"border-bottom: 2px inset #B2B2B2; font-weight: 700; font-size: 10pt; margin-top: 5px;\">Most discussed items in "
				+ alertConfig.getAlertName() + "</td>" + "</tr>");
		
		if (mostDiscussedInGroupNews.size()!=0) {
			Iterator iter = mostDiscussedInGroupNews.iterator();
			while (iter.hasNext()) {
					NewsItems news = (NewsItems) iter.next();
					boolean isUrlExist=true;
					String encodedurl = news.getUrl();
					if(encodedurl!=null){
						if(!encodedurl.equals("")){
							if (!encodedurl.matches("^(https?)://.+$")) {
								encodedurl = "http://" + encodedurl;
							}
							try {
								encodedurl = URLEncoder.encode(encodedurl.toString(),
										"UTF-8");
							} catch (UnsupportedEncodingException e) {
								logger.log(Level.INFO,
										"[LhNewsletterServlet --- encodedurl exception --- createReports()]");
								e.printStackTrace();
							}
						}else{
							isUrlExist=false;
						}
					}else{
						isUrlExist=false;
					}
					
					if(isUrlExist){
					stringBuilder
							.append(""
									+ "<tr>"
									+ "<td>"+
									"<a href = '"+tomcaturl+"com.lighthouse.newsletter.newsletter/emailitemView?info="+String.valueOf(userInfo.getUserId())+"**"+String.valueOf(userInfo.getUserSelectedIndustryID())+"**"+String.valueOf(news.getNewsId())+"**"+String.valueOf(news.getIsLocked())+"**"+String.valueOf(news.getIsReportItem())+"**"+encodedurl+"' " +
											"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >"+news.getNewsTitle()+"</a>");
									//"<a href = '' \"target=\"_new\" style=\"color: "+ content.getPulseItemColor()+"; font-family: Arial; font-size: 95%; font-weight: bold; text-align: left; text-decoration: none;\">"+ news.getNewsTitle() + "</a>" 
								stringBuilder.append("</td>"+
										"</tr>");
					}else{
                   	 String refUrl=referrer;
                	 if(refUrl.contains("lhadmin.html")){
                		 refUrl=refUrl.replace("lhadmin.html", "newshome.html");
                		 refUrl=refUrl+"&nid="+news.getNewsId()+"&display=true&uid="+userInfo.getUserId();
                	 }
                	 stringBuilder.append("<a href='"+refUrl+"'target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >"+news.getNewsTitle()+"</a>");
                    
                 }
			}
			/*stringBuilder.append("</table>" + "</td>" + "</tr>");*/
		}else{
			stringBuilder.append(""
					+ "<tr>"
					+ "<td>"
					+"<a href = '' \"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >No news to display</a>"+
					"</td>" +
					"</tr>");
		}
		stringBuilder.append("</table>" + "</td>" + "</tr>");
		
	}

	private void mostReadInGroup(List<NewsItems> mostReadInGroupNews, UserNewsletterAlertConfig alertConfig, LhUser userInfo) {
		stringBuilder
		.append("<tr>"
				+ "<td>"
				+ "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 5px 0px;\">"
				+ "<tr>"
				+ "<td style=\"border-bottom: 2px inset #B2B2B2; font-weight: 700;font-size: 10pt; margin: 2px 0;\">Most read items in "
				+ alertConfig.getAlertName() + "</td>"
				+ "</tr>");
		if (mostReadInGroupNews.size()!=0) {
			Iterator iter1 = mostReadInGroupNews.iterator();
			while (iter1.hasNext()) {
					NewsItems news = (NewsItems) iter1.next();
					boolean isUrlExist=true;
					String encodedurl = news.getUrl();
					if(encodedurl!=null){
						if(!encodedurl.equals("")){
							if (!encodedurl.matches("^(https?)://.+$")) {
								encodedurl = "http://" + encodedurl;
							}
							try {
								encodedurl = URLEncoder.encode(encodedurl.toString(),
										"UTF-8");
							} catch (UnsupportedEncodingException e) {
								logger.log(Level.INFO,
										"[LhNewsletterServlet --- encodedurl exception --- createReports()]");
								e.printStackTrace();
							}
						}else{
							isUrlExist=false;
						}
					}else{
						isUrlExist=false;
					}
					
					if(isUrlExist){
					stringBuilder
							.append(""
									+ "<tr>"
									+ "<td>"+
									"<a href = '"+tomcaturl+"com.lighthouse.newsletter.newsletter/emailitemView?info="+String.valueOf(userInfo.getUserId())+"**"+String.valueOf(userInfo.getUserSelectedIndustryID())+"**"+String.valueOf(news.getNewsId())+"**"+String.valueOf(news.getIsLocked())+"**"+String.valueOf(news.getIsReportItem())+"**"+encodedurl+"' " +
											"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >"+news.getNewsTitle()+"</a>");
									//"<a href = '' \"target=\"_new\" style=\"color: "+ content.getPulseItemColor()+"; font-family: Arial; font-size: 95%; font-weight: bold; text-align: left; text-decoration: none;\">"+ news.getNewsTitle() + "</a>" 
								stringBuilder.append("</td>"+
										"</tr>");
					}else{
                   	 String refUrl=referrer;
                	 if(refUrl.contains("lhadmin.html")){
                		 refUrl=refUrl.replace("lhadmin.html", "newshome.html");
                		 refUrl=refUrl+"&nid="+news.getNewsId()+"&display=true&uid="+userInfo.getUserId();
                	 }
                	 stringBuilder.append("<a href='"+refUrl+"'target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >"+news.getNewsTitle()+"</a>");
                    
                 }
			}
		}else{
			stringBuilder.append(""
					+ "<tr>"
					+ "<td>"
					+"<a href = '' \"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >No news to display</a>"+
					"</td>" +
					"</tr>");
		}
		stringBuilder.append("</table>" + "</td>" + "</tr>");
	}

	private void mostDiscussedInAllGroup(List<NewsItems> mostDiscussedInAllGroupsNews, LhUser userInfo) {
		stringBuilder
		.append("<tr>"
				+ "<td>"
				+ "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 5px 0px;\">"
				+ "<tr>"
				+ "<td style=\"border-bottom: 2px inset #B2B2B2; font-weight: 700;font-size: 10pt; margin: 2px 0;\">Most discussed items in Portal </td>"
				+ "</tr>");
		if (mostDiscussedInAllGroupsNews.size()!=0) {
			Iterator iter1 = mostDiscussedInAllGroupsNews.iterator();
			while (iter1.hasNext()) {
					NewsItems news = (NewsItems) iter1.next();
					boolean isUrlExist=true;
					String encodedurl = news.getUrl();
					if(encodedurl!=null){
						if(!encodedurl.equals("")){
							if (!encodedurl.matches("^(https?)://.+$")) {
								encodedurl = "http://" + encodedurl;
							}
							try {
								encodedurl = URLEncoder.encode(encodedurl.toString(),
										"UTF-8");
							} catch (UnsupportedEncodingException e) {
								logger.log(Level.INFO,
										"[LhNewsletterServlet --- encodedurl exception --- createReports()]");
								e.printStackTrace();
							}
						}else{
							isUrlExist=false;
						}
					}else{
						isUrlExist=false;
					}
					
					if(isUrlExist){
					stringBuilder
							.append(""
									+ "<tr>"
									+ "<td>"+
									"<a href = '"+tomcaturl+"com.lighthouse.newsletter.newsletter/emailitemView?info="+String.valueOf(userInfo.getUserId())+"**"+String.valueOf(userInfo.getUserSelectedIndustryID())+"**"+String.valueOf(news.getNewsId())+"**"+String.valueOf(news.getIsLocked())+"**"+String.valueOf(news.getIsReportItem())+"**"+encodedurl+"' " +
											"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >"+news.getNewsTitle()+"</a>");
									//"<a href = '' \"target=\"_new\" style=\"color: "+ content.getPulseItemColor()+"; font-family: Arial; font-size: 95%; font-weight: bold; text-align: left; text-decoration: none;\">"+ news.getNewsTitle() + "</a>" 
								stringBuilder.append("</td>"+
										"</tr>");
					}else{
                   	 String refUrl=referrer;
                	 if(refUrl.contains("lhadmin.html")){
                		 refUrl=refUrl.replace("lhadmin.html", "newshome.html");
                		 refUrl=refUrl+"&nid="+news.getNewsId()+"&display=true&uid="+userInfo.getUserId();
                	 }
                	 stringBuilder.append("<a href='"+refUrl+"'target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >"+news.getNewsTitle()+"</a>");
                    
                 }
			}
		}else{
			stringBuilder.append(""
					+ "<tr>"
					+ "<td>"
					+"<a href = '' \"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >No news to display</a>"+
					"</td>" +
					"</tr>");
		}
		stringBuilder.append("</table>" + "</td>" + "</tr>");
	}

	private void mostReadInAllGroup(List<NewsItems> mostReadInAllGroupsNews, LhUser userInfo) {
		stringBuilder
		.append("<tr>"
				+ "<td>"
				+ "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 5px 0px;\">"
				+ "<tr>"
				+ "<td style=\"border-bottom: 2px inset #B2B2B2; font-weight: 700;font-size: 10pt; margin: 2px 0;\">Most read items in Portal </td>"
				+ "</tr>");
		if (mostReadInAllGroupsNews.size()!=0) {
			Iterator iter1 = mostReadInAllGroupsNews.iterator();
			while (iter1.hasNext()) {
					NewsItems news = (NewsItems) iter1.next();
					boolean isUrlExist=true;
					String encodedurl = news.getUrl();
					if(encodedurl!=null){
						if(!encodedurl.equals("")){
							if (!encodedurl.matches("^(https?)://.+$")) {
								encodedurl = "http://" + encodedurl;
							}
							try {
								encodedurl = URLEncoder.encode(encodedurl.toString(),
										"UTF-8");
							} catch (UnsupportedEncodingException e) {
								logger.log(Level.INFO,
										"[LhNewsletterServlet --- encodedurl exception --- createReports()]");
								e.printStackTrace();
							}
						}else{
							isUrlExist=false;
						}
					}else{
						isUrlExist=false;
					}
					
					if(isUrlExist){
					stringBuilder
							.append(""
									+ "<tr>"
									+ "<td>"+
									"<a href = '"+tomcaturl+"com.lighthouse.newsletter.newsletter/emailitemView?info="+String.valueOf(userInfo.getUserId())+"**"+String.valueOf(userInfo.getUserSelectedIndustryID())+"**"+String.valueOf(news.getNewsId())+"**"+String.valueOf(news.getIsLocked())+"**"+String.valueOf(news.getIsReportItem())+"**"+encodedurl+"' " +
											"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >"+news.getNewsTitle()+"</a>");
									//"<a href = '' \"target=\"_new\" style=\"color: "+ content.getPulseItemColor()+"; font-family: Arial; font-size: 95%; font-weight: bold; text-align: left; text-decoration: none;\">"+ news.getNewsTitle() + "</a>" 
								stringBuilder.append("</td>"+
										"</tr>");
					}else{
                   	 String refUrl=referrer;
                	 if(refUrl.contains("lhadmin.html")){
                		 refUrl=refUrl.replace("lhadmin.html", "newshome.html");
                		 refUrl=refUrl+"&nid="+news.getNewsId()+"&display=true&uid="+userInfo.getUserId();
                	 }
                	 stringBuilder.append("<a href='"+refUrl+"'target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >"+news.getNewsTitle()+"</a>");
                    
                 }
			}
		}else{
			stringBuilder.append(""
					+ "<tr>"
					+ "<td>"
					+"<a href = '' \"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getPulseItemColor()+"; cursor: pointer; cursor: hand;\" >No news to display</a>"+
					"</td>" +
					"</tr>");
		}
		stringBuilder.append("</table>" + "</td>" + "</tr>");
	}

	/**
	 * gets the current date in long format i.e . 1st January, 2011
	 * @return
	 * @throws ParseException 
	 */
	private String getCurrentDate() throws ParseException{
		 logger.log(Level.INFO, "[LhNewsletterServlet --- getCurrentDate initiated --- getCurrentDate()]");
		 Date today = new Date();
		 SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 String result = formater.format(today);
		 logger.log(Level.INFO, "[LhNewsletterServlet --- getCurrentDate completed --- getCurrentDate()]");
		 return result;
	}
	
	private void createHeadLayout(){
		logger.log(Level.INFO, "[LhNewsletterServlet --- creating Head Layout initiated --- createHeadLayout()]");
		stringBuilder = new StringBuilder();
		
		stringBuilder.append("<html>");
		stringBuilder.append("<head>");
		stringBuilder.append("<script src=\"/A2EB891D63C8/avg_ls_dom.js\" type=\"text/javascript\"></script>");
		
		stringBuilder.append("<style type=\"text/css\" media=\"screen\">");
		stringBuilder.append("<!--");
		
		stringBuilder.append("body{ background-color:#fff;background-position:center top;}");
		stringBuilder.append("h2{ font-size: 18px;color: #361824;}");
		stringBuilder.append("a img { border: none;}");
		stringBuilder.append("a {border: none;}");
		stringBuilder.append("td.permission {padding: 30px 0 10px 0;}");
		stringBuilder.append(".permission {font-family: 'Lucida Grande';font-size: 10px;color: #666666;padding: 4px 0 4px 0;}");
		stringBuilder.append(".permission a {color: #333333;}");
		stringBuilder.append(".permission p { margin: 0 0 4px 0;}");
		stringBuilder.append(".header {color: #bfbfbf; background-color: #fff; padding-top:0px;}");
		stringBuilder.append(".header a{color: #3b92b1;}");
		stringBuilder.append(".header h1 {font-family: Verdana;font-size: 26px;font-weight: normal;color: #361824;display: inline;text-align: left;}");
		stringBuilder.append(".date{font-family: Georgia;font-size: 12px;color: #bfbfbf;font-weight: normal;/*display: inline;*/font-style: italic;}");
		stringBuilder.append(" .body {background-color: #ffffff;}");
		stringBuilder.append(".body a{font-family: Georgia;font-size: 11px;color: #2679b9;font-style: italic;}");
		stringBuilder.append("td.mainbar {padding: 22px 40px 0 40px;border-top: 5 solid #C2D22D;}");
		stringBuilder.append(".mainbar p {font-family: 'Lucida Grande';font-size: 12px;color: #333333;margin: 0 0 4px 0;text-align: left;}");
		stringBuilder.append("p.contact-info {text-align: center;}");
		stringBuilder.append("td.contact {padding: 0px 40px 5 40px; }");
		stringBuilder.append("contact p {font-family: 'Lucida Grande';font-size: 12px;color: #333333;text-align: left;}");
		stringBuilder.append(".mainbar p.first {margin-top: 10px;}");
		stringBuilder.append(".mainbar h2 {font-family: 'Lucida Grande';margin: 0px 0 4px 0;color: #766436;}");
		stringBuilder.append(".mainbar h2.categoryName{border-bottom:1px solid Silver; }");
		stringBuilder.append(".mainbar h2 a {font-size: 18px;color: #c4d940;text-decoration: underline;font-style: normal; }");
		stringBuilder.append(" .mainbar a {font-family: Georgia;color: #006cb8;font-style: italic;}");
		stringBuilder.append(".mainbar a.center {font-size: 11px;text-align: center;display: block;color: #999999;padding: 2px 0 2px 0;margin:0;text-decoration: none; }");
		stringBuilder.append(".mainbar img.inline {border: 1px solid #dedede;padding: 4px;}");
		stringBuilder.append("td.footer {padding: 0 0 10px 0;}");
		stringBuilder.append(" .footer p {color: #a1a1a1;font-size: 11px;margin: 0;padding: 0;}");
		stringBuilder.append(" .footer p.first {margin: 14px 0 0 0;}");
		stringBuilder.append(".footer a {font-family: 'Lucida Grande';font-size: 11px;color: #333333; }");
		stringBuilder.append(".style1{width: 461px;font-family: Georgia;font-size: 30;font-weight: 500; color: #766436;}");
		stringBuilder.append(" .maintable{background-color:#fff; background-image:url('http://122.169.111.248:3080/Lighthouse/images/nz/bg.png');background-repeat:no-repeat;background-position:center top;}  ");
		
		stringBuilder.append("-->");
		stringBuilder.append("</style>");
		
		stringBuilder.append("</head>");
		
		 logger.log(Level.INFO, "[LhNewsletterServlet --- creating Head Layout completed --- createHeadLayout()]");
	}
	
	/**
	 * creates the primary headline html
	 */
	private void createPrimaryHeadline(TagItem primaryTag){
		stringBuilder.append("" +
				"<h2 style=\"font-family:'"+content.getHeadlineFontFamily()+"';font-size:"+content.getHeadlineFontSize()+";font-style:"+content.getHeadlineFontStyle()+";font-weight:"+content.getHeadlineFontWeight()+";color:"+content.getHeadlineFontColor()+";padding:"+content.getHeadlinePadding()+";border-bottom:1px solid Silver;\" class=\"categoryName\" id=\"cat_101\">"+ 
				primaryTag.getTagName() +
				"</h2>" );
	}
	
	private String getStatsImage(String dateInfo, UserInformation userInfo) {
		
		String statsImgPath = null;
		try {
			String currentFile = request.getContextPath();
			URL currentURL = new URL(request.getScheme(),
					request.getServerName(), request.getServerPort(),
					currentFile);
			String encryptedString = EncrypterDecrypter.getInstance()
					.getEncryptedString(ncid + "**" + userInfo.getUserId() + "**" + dateInfo);
			
			String urlencrypted =EncrypterDecrypter.getInstance().getUrlEncodedString(encryptedString);
			statsImgPath = tomcaturl + "getImg?imgId="+ urlencrypted;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statsImgPath;
	}

	/**
	 * Sets the mail specific information in UserInformation
	 * @param user
	 * @param alertConfig
	 */
	private void sendMailForAlert(LhUser user,UserNewsletterAlertConfig alertConfig) {
		try{
			logger.log(Level.INFO, "[LhNewsletterServlet --- send mail initiated --- sendMailForAlert()]");
			LhUser userInformation = new LhUser();
			userInformation.setBodyMail(stringBuilder.toString());
			userInformation.setRecipientsMail(user.getEmail());
			userInformation.setSenderMail("no_reply@newscatalyst.com");
			
			String alertName = alertConfig.getAlertName();
			ArrayList<Group> alertGroupList =alertConfig.getAlertGroupList();
			
			StringBuilder str1 = new StringBuilder();  
			if(alertGroupList!=null){
				if(alertGroupList.size()>0){
					str1.append("[");
					for(int i=0;i<alertGroupList.size();i++){
						if(i!=alertGroupList.size()-1)
							str1.append(alertGroupList.get(i).getGroupName()+",");
						else
							str1.append(alertGroupList.get(i).getGroupName());
					}
					str1.append("]");
					//userInformation.setSubjectMail(" "+alertName+""+str1.toString());
					userInformation.setSubjectMail(" "+alertName);
				}
			}
			else{
				userInformation.setSubjectMail(" "+alertName);
			}
			mailService = new MailServiceImpl();
			mailService.setSmtpusername(smtpusername);
			mailService.setSmtppassword(smtppassword);
			mailService.setSmtphost(smtphost);
			mailService.setSmtpport(smtpport);
			logger.log(Level.INFO, "[LhNewsletterServlet --- sending newsletters --- sendMailForAlert()]");
			mailService.sendNewsletters(userInformation);    
			logger.log(Level.INFO, "[LhNewsletterServlet --- send mail completed --- sendMailForAlert()]");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getNcid(HttpServletRequest req){
		logger.log(Level.INFO, "[LhNewsletterServlet --- getNcid of newsletter to be sent]");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try{
			ArrayList items = (ArrayList) upload.parseRequest(req);
			if(items != null){
				Iterator iter = items.iterator();
				while(iter.hasNext()){
					DiskFileItem item = (DiskFileItem)iter.next();
					if(item.isFormField()){
						String fieldName = item.getFieldName();
						String value = item.getString();
						if(fieldName.equals("ncid")){
							String nc = value ; 
							ncid = Integer.parseInt(nc);
							logger.log(Level.INFO, "[LhNewsletterServlet --- getNcid of newsletter to be sent::: NCID found :::"+ncid+"]");
							break;
						}
					}
				} 
			}
		}catch(Exception ex){
			logger.log(Level.INFO, "[LhNewsletterServlet --- getNcid of newsletter to be sent EXCEPTION!!!!]");
			ex.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.log(Level.INFO, "[LhNewsletterServlet --- in doPost() ]");
		request = req;
		referrer = request.getHeader("referer");
		getNcid(req);
		try {
			if(validateReq(req)){
				fetchNews();
			if(status)
				resp.getOutputStream().print("Success");
			else
				resp.getOutputStream().print("Failed");
			}else{
				resp.getOutputStream().print("You are not authorised to access this page.");
			}
			
		} catch (Exception e) {
			logger.log(Level.INFO, "[LhNewsletterServlet] ....doGet()",e);
		}
	
	}

	public boolean validateReq(HttpServletRequest req){
		AppUtilsServiceImpl appServiceImpl=new AppUtilsServiceImpl();
		try{
			if (appServiceImpl.isValidLicensing(req)) {
				HttpSession session = req.getSession(false);
				if (session != null) {
					UserInformation userInformation = (UserInformation) session.getAttribute("userInfo");
					if (userInformation != null) {
						if (userInformation.getAdmin() == 1)
							return true;
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.log(Level.INFO, "[LhNewsletterServlet --- in doGet() ]");
		request = req;
		String nc = req.getParameter("ncid");
		ncid = Integer.parseInt(nc);
		
		try {
			if(validateReq(req)){
				fetchNews();
			if(status)
				resp.getOutputStream().print("Success");
			else
				resp.getOutputStream().print("Failed");
			}else{
				resp.getOutputStream().print("You are not authorised to access this page.");
			}
			
		} catch (Exception e) {
			logger.log(Level.INFO, "[LhNewsletterServlet] ....doGet()",e);
		}
		
		
	} 
	
	
	private void createHeadLayoutNew(){
		
		logger.log(Level.INFO, "[LhNewsletterServlet --- creating Head Layout initiated --- createHeadLayout()]");
		stringBuilder = new StringBuilder();
		
		stringBuilder.append("<html>");
		stringBuilder.append("<head>");
		stringBuilder.append("<script src=\"/A2EB891D63C8/avg_ls_dom.js\" type=\"text/javascript\"></script>");
		stringBuilder.append("</head>");
		
		 logger.log(Level.INFO, "[LhNewsletterServlet --- creating Head Layout completed --- createHeadLayout()]");
	}
	
	
	private void createBodyNew(String dateOut,String alertName, LhUser userInfo){
		logger.log(Level.INFO, "[LhNewsletterServlet --- creating Body Layout --- createBodyLayout()]");
		stringBuilder.append("<body>");
		String url = getStatsImage(dateOut, userInfo);
		stringBuilder.append("<img id=\"logo\" name=\"blank-image\" src=\""+url+" \" />");
		stringBuilder.append("" +
				"<table style=\"background-color:"+outline.getTableBackgroundColor()+";background-image:url('"+outline.getTableBackgroundImageUrl()+"');background-repeat:no-repeat;background-position:center top;\" width=\""+outline.getTableWidth()+"\" cellspacing=\"0\" cellpadding=\"0\">" +
					"<tr>" +
						"<td align=\"center\">" +
								"<table width=\""+header.getBaseTableWidth()+"\" cellspacing=\""+header.getBaseTableCellSpacing()+"\" cellpadding=\""+header.getBaseTableCellPadding()+"\" background-color="+header.getBaseTableBackgroundColor()+">" +
										"<tr>" +
											"<td align=\"center\" style=\"font-family:'Lucida Grande';font-size:10px;color:#666666;padding:4px 0 4px 0;\">" +
												"<p>" +
											"</td>" +
										"</tr>" +
										"<tr>" +
											"<td height=\"100%\" width=\"100%\" align=\"center\" style=\"color:#bfbfbf;background-color:#fff;padding-top:0px;\">" +
												/*"<table align=\"center\" width=\"700\" cellspacing=\"0\" cellpadding=\"0\">" +*/
												"<table align=\"center\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">" +
														"<tr>" +
															"<td align=\""+header.getAlertNameTextAlignment()+"\" style=\"width:461px;font-family:"+header.getAlertNameFontFamily()+";font-style:"+header.getAlertNameFontStyle()+";font-size:"+header.getAlertNameFontSize()+";font-weight:"+header.getAlertNameFontWeight()+";color:"+header.getAlertNameFontColor()+";\">" + 
															alertName +
															"</td>" +
															"<td align=\""+header.getLogoAlignment()+"\">" +
																	"<a href=\""+header.getLogoTargetUrl()+"\">" );
																	boolean val = true;
																	if(!header.getLogoImageUploadUrl().trim().equals("")){ // when image is on file system
																		stringBuilder.append("<img id=\"logo\" onclick=\""+header.getLogoTargetUrl()+"\" name=\"header-image\" src=\""+tomcaturl+header.getLogoImageUploadUrl()+"\" alt=\""+header.getLogoAlternativeText()+"\" />");
																		val = false;
																	}
																	else if(val){// when image is a url
																		if(!header.getLogoImage().trim().equals(""))
																			stringBuilder.append("<img id=\"logo\" onclick="+header.getLogoTargetUrl()+" name=\"header-image\" src=\""+header.getLogoImage()+"\" alt="+header.getLogoAlternativeText()+" />");
																		/*else
																			stringBuilder.append("<img id=\"logo\" onclick="+header.getLogoTargetUrl()+" name=\"header-image\" src=\"http://marketscape.facility.dir.dk/Lighthouse/images/nz/NZLogo.jpg\" alt="+header.getLogoAlternativeText()+" />");*/
																	}
																	stringBuilder.append("</a>" +
															"</td>" +
														"</tr>" +
												"</table>" +
											"</td>" +
										"</tr>" +
										"<tr>" +
											"<td>" +
												/*"<table align=\"center\" width=\"700\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +*/
												"<table align=\"center\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
													"<tr style=\"background-color:#ffffff;\">" +
														"<td>"+
															"&nbsp;"+
														"</td>" +
														"<td align=\""+header.getDateTextAlignment()+"\" style=\"font-family:"+header.getDateFontFamily()+";font-size:"+header.getDateFontSize()+";color:"+header.getDateFontColor()+";font-weight:"+header.getDateFontWeight()+";/*display: inline;*/font-style:"+header.getDateFontStyle()+";\">" +
															"<span>"+dateOut+"</span>"+
														"</td>" +
				                                        "<td>" +
				                                            "&nbsp;" +
				                                        "</td>" +
				                                     "</tr>" +
				                                 "</table>" +
                                             "</td>" +
                                        "</tr>" +
                                  "</table>" +
                               "</td>" +
                            "</tr>" +
                               
                            "<tr>" +
                            "<td align=\"center\">" +
	                            "<table width=\""+content.getTableWidth()+"\" cellspacing=\""+content.getTableCellSpacing()+"\" cellpadding=\""+content.getTableCellPadding()+"\" style=\"background-color:"+content.getTableBackgroundColor()+";\" class=\"body\">" +
		                            "<tr>"+
		                            	"<td align=\""+content.getFirstColumnAlign()+"\" valign=\""+content.getFirstColumnVerticalAlign()+"\" style=\"padding:"+content.getFirstColumnPadding()+";border-top:"+content.getFirstColumnBorder()+";width:"+content.getFirstColumnWidth()+";\">");
			                            	
		 logger.log(Level.INFO, "[LhNewsletterServlet --- creating Body Layout completed--- createBodyLayout()]");
			
	}
	
	private void createFooterNew(LhUser user){
		 logger.log(Level.INFO, "[LhNewsletterServlet --- creating Footer --- createFooter()]");
		 
		 stringBuilder.append("" +
				"<table width=\""+footer.getTableWidth()+"\" cellspacing=\""+footer.getTableCellSpacing()+"\" cellpadding=\""+footer.getTableCellPadding()+"\" style=\"background-color:"+footer.getTableBackgroundColor()+";\">" +
					"<tr>" +
						"<td align=\""+footer.getFirstRowAlign()+"\" valign=\""+footer.getFirstRowVerticalAlign()+"\" width=\""+footer.getFirstRowWidth()+"\" style=\"padding:"+footer.getFirstRowPadding()+";border-top:"+footer.getFirstRowBorder()+";\">" +
                              "<p style=\"text-align:"+footer.getNameAndElementTextAlignment()+";font-family:"+footer.getNameAndElementFontFamily()+";font-size:"+footer.getNameAndElementFontSize()+";color:"+footer.getNameAndElementFontColor()+";font-weight:"+footer.getNameAndElementFontWeight()+";font-style:"+footer.getNameAndElementFontStyle()+";\">" +
                                   footer.getNameAndElementNameToBeDisplayed()+", Email: <a href=\"mailto:"+footer.getNameAndElementEmailToBeDisplayed()+" \">"+footer.getNameAndElementEmailToBeDisplayed()+"</a>"+
                               "</p>" + 
                        "</td>" +
                    "</tr>"+
                     "<tr>" +
                           "<td colspan=\"2\" align=\"center\">" +
                               "<table width=\"560\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
                                   "<tr>" +
                                       "<td style=\"padding:0px 0px 10px 0px; name=\"footer\" align=\"center\">"+
                                       "<p style=\"color: #a1a1a1;font-size:12px;margin:0;padding:0;\">");
                                       if (footer.getUnsubscribedEnabeledOrNot().equalsIgnoreCase("true")||footer.getUnsubscribedEnabeledOrNot().equalsIgnoreCase("yes")) {
                                    	   stringBuilder.append(footer.getUnsubscribedDisplayText()+" <a href=\""+ footer.getUnsubscribedLinkUrl()+"\" target=\"new\" style=\"font-family:'"+footer.getUnsubscribedFontFamily()+"';font-size:"+footer.getUnsubscribedFontSize()+";font-style:"+footer.getUnsubscribedFontStyle()+";color:"+footer.getUnsubscribedFontColor()+";" +"text-align: "+footer.getUnsubscribedTextAlignment()+";font-weight:"+ footer.getUnsubscribedFontWeight()+";\">"+ footer.getUnsubscribedDisplayLinkText()+ " </a>.<br />");
                                       }
                                       
                                       if(footer.getPoweredByEnabeledOrNot().equalsIgnoreCase("true")||footer.getPoweredByEnabeledOrNot().equalsIgnoreCase("yes")){
                                    	   stringBuilder.append(footer.getPoweredByDisplayText()+" <a href=\""+footer.getPoweredByLinkUrl()+"\" target=\"new\" style=\"font-family:'"+footer.getPoweredByFontFamily()+"';font-size:"+footer.getPoweredByFontSize()+";font-style:"+footer.getPoweredByFontStyle()+";color:"+footer.getPoweredByFontColor()+";" +"text-align:"+footer.getPoweredByTextAlignment()+";font-weight:"+ footer.getPoweredByFontWeight()+";\">"+footer.getPoweredByDisplayLinkText()+"</a>.");
                                       }
										stringBuilder.append("</p>"+
                                       "</td>"+
                                   "</tr>"+
                              "</table>"+
                           "</td>"+
                       "</tr>"+
                   "</table>" +
                       
               // close tags
              "</td>"+
          "</tr>"+
       "</table>");
		
		stringBuilder.append("</body>");
		stringBuilder.append("</html>");
		 logger.log(Level.INFO, "[LhNewsletterServlet --- footer completed --- createFooter()]");
	}
	
	private void createNewsNew(NewsItems news,LhUser user){
		logger.log(Level.INFO, "[LhNewsletterServlet --- create News initiated for newsid::: "+news.getNewsId()+" --- createNews()]");
		String url = tomcaturl+"imagefolder/"+news.getNewsId()+".jpg";
		String encodedurl = news.getUrl();
		int alignPx=3;
		boolean isClipped=false;
		String newsAbstract=news.getAbstractNews();
		if(encodedurl!=null){
			if(!encodedurl.equals("")){
				if(!encodedurl.matches("^(https?)://.+$")){
					encodedurl = "http://"+encodedurl;
				}
				try {
					encodedurl = URLEncoder.encode(encodedurl.toString(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					 logger.log(Level.INFO, "[LhNewsletterServlet --- encodedurl exception --- createNews()]");
					 e.printStackTrace();
				}
			}else{
				if(newsAbstract.length()>200)
					newsAbstract=newsAbstract.substring(0, 200)+"....";
				isClipped=true;
			}
		}else{
			if(newsAbstract.length()>200)
				newsAbstract=newsAbstract.substring(0, 200)+"....";
			isClipped=true;
		}
		stringBuilder.append("" +
				"<div class=\"article\">" +
				"<table cellspacing=\"0\" width=\"100%\">" +
				//news title
				"<tr>"+
                     "<td>");
                     if(!isClipped){
	                     stringBuilder.append("<a href='"+tomcaturl+"com.lighthouse.newsletter.newsletter/emailitemView?info="+String.valueOf(user.getUserId())+"**"+String.valueOf(user.getUserSelectedIndustryID())+"**"+String.valueOf(news.getNewsId())+"**"+String.valueOf(news.getIsLocked())+"**"+String.valueOf(news.getIsReportItem())+"**"+encodedurl+
	                     			"' target=\"new\" style=\"font-family:'"+content.getNewsTitleFontFamily()+"';font-style:"+content.getNewsTitleFontStyle()+";font-weight:"+content.getNewsTitleFontWeight()+";font-size:"+content.getNewsTitleFontSize()+";color:"+content.getNewsTitleFontColor()+";word-wrap: break-word;padding-left:"+content.getNewsTitlePadding()+";text-decoration:underline;\">"+news.getNewsTitle()+
	                    "");
                     }else{
                    	 String refUrl=referrer;
                    	 if(refUrl.contains("lhadmin.html")){
                    		 refUrl=refUrl.replace("lhadmin.html", "newshome.html");
                    		 refUrl=refUrl+"&nid="+news.getNewsId()+"&display=true&uid="+user.getUserId();
                    	 }
                    	 stringBuilder.append("<a href='"+refUrl+"' target=\"new\" style=\"font-family:'"+content.getNewsTitleFontFamily()+"';font-style:"+content.getNewsTitleFontStyle()+";font-weight:"+content.getNewsTitleFontWeight()+";font-size:"+content.getNewsTitleFontSize()+";color:"+content.getNewsTitleFontColor()+";word-wrap: break-word;padding-left:"+content.getNewsTitlePadding()+";text-decoration:underline;\">"+news.getNewsTitle()+
	                    "");
                     }
	
		if(news.getIsLocked()==1){
			
			stringBuilder.append( "<img id=\"lock_key\" name=\"key-image\" src=\""+tomcaturl+"images/key_gray.png\">" );
		}
		stringBuilder.append("</a>"+
					 "</td>");
		stringBuilder.append("</tr>" +
                  
                  //abstract + published date + source + related tags
                  "<tr>" +
                  	"<td valign=\"top\">" +
		                  "<p style=\"font-family:'"+content.getAbstractFontFamily()+"';font-size:"+content.getAbstractFontSize()+";color:"+content.getAbstractFontColor()+";font-style:"+content.getAbstractFontStyle()+";font-weight:"+content.getAbstractFontWeight()+";margin:0px 0px 0px 0px;text-align:left;padding-left:"+content.getAbstractPadding()+";\">" +
		                 newsAbstract+
		                  "</p>");
					
					boolean isPublished = false;
		                  if(content.getPublishedDateEnabledOrNot().equalsIgnoreCase("true")||content.getPublishedDateEnabledOrNot().equalsIgnoreCase("yes")){
		                	  stringBuilder.append("<div style=\"font-family:'"+content.getPublishedDateFontFamily()+"';font-size:"+content.getPublishedDateFontSize()+";color:"+content.getPublishedDateFontColor()+";font-style:"+content.getPublishedDateFontStyle()+";font-weight:"+content.getPublishedDateFontWeight()+";padding-right:5px;padding-left:3px\">");
		                	  stringBuilder.append("<span>PUBLISHED: "+news.getNewsDate()+ "</span>");
		                	  isPublished=true;
		                  }
		                  if(content.getSourceEnabledOrNot().equalsIgnoreCase("true")||content.getSourceEnabledOrNot().equalsIgnoreCase("yes")){
		                	  	if(isPublished){
		                	  		stringBuilder.append("<font size = 1pt> | </font>");
		                	  		stringBuilder.append("<span>  SOURCE: "+news.getNewsSource() +"</span>");
		                	  		stringBuilder.append("</div>");
		                	  	}
		                	  	else{
		                	  		stringBuilder.append("<div style=\"font-family:'"+content.getSourceFontFamily()+"';font-size:"+content.getSourceFontSize()+";color:"+content.getSourceFontColor()+";font-style:"+content.getSourceFontStyle()+";font-weight:"+content.getSourceFontWeight()+";padding-right:5px;padding-left:3px\">");
		                	  		stringBuilder.append("<span>  SOURCE: "+news.getNewsSource() +"</span>");
		                	  		stringBuilder.append("</div>");
		                	  	}
		                  }
		                  if(news.getAuthor()!=null){
		                	  if(!news.getAuthor().equals("")){
			                  stringBuilder.append("<div style=\"font-family:'"+content.getPublishedDateFontFamily()+"';font-size:"+content.getPublishedDateFontSize()+";color:"+content.getPublishedDateFontColor()+";font-style:"+content.getPublishedDateFontStyle()+";font-weight:"+content.getPublishedDateFontWeight()+";padding-right:5px;padding-left:3px\">");
		                	  stringBuilder.append("<span>AUTHOR: "+news.getAuthor()+ "</span>");
		                	  alignPx=0;
		                	  }
		                  }
	                	  
		                  if(content.getTagsEnabledOrNot().equalsIgnoreCase("true")||content.getTagsEnabledOrNot().equalsIgnoreCase("yes")){
		                	  stringBuilder.append("<div style=\"font-family:'"+content.getTagsFontFamily()+"';font-size:"+content.getTagsFontSize()+";color:"+content.getTagsFontColor()+";font-style:"+content.getTagsFontStyle()+";font-weight:"+content.getTagsFontWeight()+";padding-right:5px;padding-left:"+alignPx+"px \">"+
			                  "<span>"+
			                  "TAGS:");
                  	
		                	  ArrayList list = news.getAssociatedTagList();
		                	  Iterator iter = list.iterator();
		                	  int count = 0;
		                	  while(iter.hasNext()){
		                		  if(count != list.size()-1){
		                			  TagItem tag = (TagItem)iter.next();
		                			  stringBuilder.append("<a href='' target=\"new\" style=\"font-family:'"+content.getTagsFontFamily()+"';font-size:"+content.getTagsFontSize()+";color:"+content.getTagsFontColor()+";font-style:"+content.getTagsFontStyle()+";font-weight:"+content.getTagsFontWeight()+";padding-right:5px;padding-left:3px \">"+tag.getTagName()+"</a>");
		                			  stringBuilder.append("<font size = 1pt> | </font>");	
		                			  count++;
		                		  }
		                		  else{
		                			  TagItem tag = (TagItem)iter.next();
		                			  stringBuilder.append("<a href='' target=\"new\" style=\"font-family:'"+content.getTagsFontFamily()+"';font-size:"+content.getTagsFontSize()+";color:"+content.getTagsFontColor()+";font-style:"+content.getTagsFontStyle()+";font-weight:"+content.getTagsFontWeight()+";padding-right:5px;padding-left:3px\">"+tag.getTagName()+"</a>");
		                			  break;
		                		  }
		                	  } 
					                 stringBuilder.append("" +
					                		 "</span></div>"+
					                 		"</td>");
					                 if(!news.getImageUrl().equals("")){
					                	 stringBuilder.append("" +
					                      		"<td align=\"right\">" +
					     	                 		"<div>" +
					     		                 		"<img src=\""+url+"\"" +
					     		                 		"width=\"68\" height=\"68\" alt=\"No Image\" style=\"border: 1px solid rgb(196,196,196);margin-left:5px;padding: 5px 5px 5px 5px;\" class=\"inline\" />" +
					     	                 		"</div>" +
					                      		"</td>");
					                 }
		                  }
		                  stringBuilder.append("" +
		                       	"</tr>" +
		                       "</table>" +
		                       "</div>"+
		                       "<br />");
                 logger.log(Level.INFO, "[LhNewsletterServlet --- create news completed --- createNews()]");
		
	}
	
	private void createReports(ReportItemList list, LhUser userInfo){
		stringBuilder.append("<td width=\""+content.getSecondColumnWidth()+"\" align=\""+content.getSecondColumnAlign()+"\" valign=\""+content.getSecondColumnVerticalAlign()+"\" style=\"padding:"+content.getSecondColumnPadding()+";border-top:"+content.getSecondColumnBorder()+";\">"+
				"<div>"+
					"<table width = \"100%\" cellpadding=\"1\" cellspacing=\"1\" style=\"background-color:"+content.getReportBackgroundColor()+";padding:4px;margin-bottom:5px;padding:6px;\">"+
					"<tr>"+
						"<td style=\"font-family:Arial;font-weight:bold;font-size:10pt;margin-bottom:2px;color:"+content.getReportHeaderColor()+";border-bottom:"+content.getReportHeaderHorizontalRulerColor()+";\">Reports</td>"+
					"</tr>");
		if(list != null){
			if (!list.isEmpty()) {
				Iterator iter = list.iterator();
				while (iter.hasNext()) {
					ReportItem report = (ReportItem) iter.next();
					String encodedurl = report.getUrl();
					if(encodedurl!=null){
						if (!encodedurl.matches("^(https?)://.+$")) {
							encodedurl = "http://" + encodedurl;
						}
						try {
							encodedurl = URLEncoder.encode(encodedurl.toString(), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							logger.log(Level.INFO,
									"[LhNewsletterServlet --- encodedurl exception --- createReports()]");
							e.printStackTrace();
						}
					}
					stringBuilder
							.append(""
									+ "<tr>"
									+ "<td>"
									+"<a href = '"+tomcaturl+"com.lighthouse.newsletter.newsletter/emailitemView?info="+String.valueOf(userInfo.getUserId())+"**"+String.valueOf(userInfo.getUserSelectedIndustryID())+"**"+String.valueOf(report.getNewsId())+"**"+String.valueOf(report.getIsLocked())+"**"+String.valueOf(report.getIsReportItem())+"**"+encodedurl+"' " +
											"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getReportItemColor()+"; cursor: pointer; cursor: hand;\" >"+report.getNewsTitle()+"</a>"
									+ "</td></tr>");
				}
			}else{
				stringBuilder.append(""
						+ "<tr>"
						+ "<td>"
						+"<a href = '' \"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getReportItemColor()+"; cursor: pointer; cursor: hand;\" >No reports to display</a>"+
						"</td></tr>");
			}
		}else{
	   		stringBuilder.append(""
					+ "<tr>"
					+ "<td>"
					+"<a href = '' \"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getReportItemColor()+"; cursor: pointer; cursor: hand;\" >No reports to display</a>"+
					"</td></tr>");
   	    }
		stringBuilder.append("</table>"+"</div>");
	}
	
	private void createFavouritesNews(Group favoriteGroup, List<NewsItems> favoriteItemsList,LhUser userInfo) {
		String favoriteGroupName = "Favorites";
		if(favoriteGroup != null)
			favoriteGroupName = favoriteGroup.getGroupName();
		
		if(userInfo.getUserPermission().isReportsPermitted() == 0)
			stringBuilder.append("<td width=\""+content.getSecondColumnWidth()+"\" align=\""+content.getSecondColumnAlign()+"\" valign=\""+content.getSecondColumnVerticalAlign()+"\" style=\"padding:"+content.getSecondColumnPadding()+";border-top:"+content.getSecondColumnBorder()+";\">");
			
		stringBuilder.append("<div>"+
					"<table width = \"100%\" cellpadding=\"1\" cellspacing=\"1\" style=\"background-color: "+content.getFavouritesBackgroundColor()+"; padding: 4px; margin-bottom: 5px; padding: 6px;\">"+
					"<tr>"+
						"<td style=\"font-family: Arial; font-weight: bold; font-size: 10pt; margin-bottom: 2px; color: "+content.getFavouritesHeaderColor()+"; border-bottom: "+content.getFavouritesHeaderHorizontalRulerColor()+";\">"+favoriteGroupName+"</td>"+
					"</tr>");
		if(favoriteItemsList != null){
			if (!favoriteItemsList.isEmpty()) {
				Iterator iter = favoriteItemsList.iterator();
				while (iter.hasNext()) {
					NewsItems newsItem = (NewsItems) iter.next();
					String encodedurl = newsItem.getUrl();
					if (!encodedurl.matches("^(https?)://.+$")) {
						encodedurl = "http://" + encodedurl;
					}
					try {
						encodedurl = URLEncoder.encode(encodedurl.toString(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.log(Level.INFO,
								"[LhNewsletterServlet --- encodedurl exception --- createFavouritesNews()]");
						e.printStackTrace();
					}

					stringBuilder
							.append(""
									+ "<tr>"
									+ "<td>"
									+ "<a href = '"
									+ tomcaturl
									+ "com.lighthouse.newsletter.newsletter/emailitemView?info="
									+ String.valueOf(userInfo.getUserId())
									+ "**"
									+ String.valueOf(userInfo
											.getUserSelectedIndustryID())
									+ "**"
									+ String.valueOf(newsItem.getNewsId())
									+ "**"
									+ String.valueOf(newsItem.getIsLocked())
									+ "**"
									+ String.valueOf(newsItem.getIsReportItem())
									+ "**"
									+ encodedurl
									+ "' "
									+ "target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"
									+ content.getFavouritesItemColor()
									+ "; cursor: pointer; cursor: hand;\" >"
									+ newsItem.getNewsTitle() + "</a>" +
									//"<a href = '' \"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getFavouritesItemColor()+"; cursor: pointer; cursor: hand;\" >"+newsItem.getNewsTitle()+"</a>"+
									"</td>" + "</tr>");
				}
			}else{
				stringBuilder.append(""
						+ "<tr>"
						+ "<td>"
						+"<a href = '' \"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getFavouritesItemColor()+"; cursor: pointer; cursor: hand;\" > No favorite items to display</a>"+
						"</td></tr>");
			}
		}else{
	   		stringBuilder.append(""
					+ "<tr>"
					+ "<td>"
					+"<a href = '' \"target=\"_new\" style=\"font-family: Arial; text-decoration: none; font-weight: bold; font-size: 95%; color:"+content.getFavouritesItemColor()+"; cursor: pointer; cursor: hand;\" > No favorite items to display</a>"+
					"</td></tr>");
   	    }
		stringBuilder.append("</table>"+"</div>");
	}
}
