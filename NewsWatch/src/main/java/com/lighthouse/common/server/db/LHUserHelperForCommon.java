package com.lighthouse.common.server.db;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.common.client.PageResult;
import com.common.server.db.UserHelperForCommon;
import com.lighthouse.newsletter.client.domain.NewsLetterStats;
import com.login.client.UserInformation;
import com.newscenter.client.criteria.PageCriteria;

public class LHUserHelperForCommon extends UserHelperForCommon {

	public LHUserHelperForCommon(HttpServletRequest req,
			HttpServletResponse res, String connectionUrl,
			String driverClassName, String username, String password) {
		super(req, res, connectionUrl, driverClassName, username, password);
	}
	public PageResult getNewsLetterAccessStats(PageCriteria crt, int userid, int industryid) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int userSelectedIndurtyId = userInformation.getUserSelectedIndustryID();
		NewsLetterStats newsLetterAccessStats;
		ArrayList list= new ArrayList();
		try{
			String query;
			if(userid == -1){
			    query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid order by newsOpened desc";
			}
			else{
				query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid and uias.userId = "+userid+" order by newsOpened desc";
			}
			stmt=getConnection().createStatement();
			ResultSet rs =stmt.executeQuery(query);
			listSize = rs.last() ? rs.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
			
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			
			if(userid == -1){
			    query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid order by newsOpened desc LIMIT "+startRecord+","+pagesize+"";
			}
			else{
				query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid and uias.userId = "+userid+" order by newsOpened desc LIMIT "+startRecord+","+pagesize+"";
				 	
			}
			ResultSet rss = stmt.executeQuery(query);
			
			while(rss.next()){
				newsLetterAccessStats = new NewsLetterStats();
				newsLetterAccessStats.setFirstName(rss.getString("firstName"));
				newsLetterAccessStats.setEmail(rss.getString("email"));
				newsLetterAccessStats.setNewsSentDate(rss.getTimestamp("newsSent").toString());
				newsLetterAccessStats.setNewsOpenedDate(rss.getTimestamp("newsOpened"));
				list.add(newsLetterAccessStats);
			}
			rss.close();
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(list);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("problem in getNewsLetterAccessStats()");
		}
		return pageresult;
	}


	public PageResult getSortedNewsLetterAccessStats(PageCriteria crt,int userid,int industryid, String columnname, String mode) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int userSelectedIndurtyId = userInformation.getUserSelectedIndustryID();
		NewsLetterStats newsLetterAccessStats;
		ArrayList list= new ArrayList();
		try{
			String query;
			if(userid == -1){
			    query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid order by newsOpened desc";
			}
			else{
				query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid and uias.userId = "+userid+" order by newsOpened desc";
			}
			stmt=getConnection().createStatement();
			ResultSet rs =stmt.executeQuery(query);
			listSize = rs.last() ? rs.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
						
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			
			
			if(userid == -1){
			    query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid order by "+columnname+" "+mode+" LIMIT "+startRecord+","+pagesize+"";
			}
			else{
				query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid and uias.userId = "+userid+" order by "+columnname+" "+mode+" LIMIT "+startRecord+","+pagesize+"";
				 	
			}
		
			ResultSet rss = stmt.executeQuery(query);
			
			while(rss.next()){
				newsLetterAccessStats = new NewsLetterStats();
				newsLetterAccessStats.setFirstName(rss.getString("firstName"));
				newsLetterAccessStats.setEmail(rss.getString("email"));
				newsLetterAccessStats.setNewsSentDate(rss.getTimestamp("newsSent").toString());
				newsLetterAccessStats.setNewsOpenedDate(rss.getTimestamp("newsOpened"));
				list.add(newsLetterAccessStats);
			}
			rss.close();
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(list);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("problem in getSortedNewsLetterAccessStats()");
		}
		return pageresult;
	}
	
	public PageResult getSearchNewsLetterAccessStatistics(PageCriteria crt,int userid, int industryId, String columnName, String searchString) {
		PageResult pageresult = new PageResult();
		int listSize;
		int startRecord = 0;
		Statement stmt = null;
		int currentPage = crt.getCurrentPage();
		int pagesize = crt.getPageSize();
		HttpSession session = request.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int userSelectedIndurtyId = userInformation.getUserSelectedIndustryID();
		NewsLetterStats newsLetterAccessStats;
		ArrayList list= new ArrayList();
		try{
			String query;
			if(userid == -1){
			    query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid and "+columnName+" like '%"+searchString+"%'";
			}
			else{
				query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid and uias.userId = "+userid+" and "+columnName+" like '%"+searchString+"%'";
			}
			stmt=getConnection().createStatement();
			ResultSet rs =stmt.executeQuery(query);
			listSize = rs.last() ? rs.getRow() : 0;
			if(listSize != 0){
			double div = Math.ceil((double) listSize / (double) pagesize);
			if (currentPage > div) {
				currentPage = (int) div;
			}
			startRecord = (crt.getPageSize() * currentPage) - crt.getPageSize();
			
			stmt.setFetchSize(pagesize);
			stmt.setMaxRows(pagesize);
			stmt.setFetchDirection(ResultSet.FETCH_FORWARD);
			
			if(userid == -1){
			    query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid and "+columnName+" like '%"+searchString+"%' order by newsOpened desc LIMIT "+startRecord+","+pagesize+"";
			}
			else{
				query = "SELECT u.firstname, u.email, uias.newsSent,uias.newsOpened FROM newsletter_statistics as uias, user as u where uias.newsOpened BETWEEN SYSDATE() - INTERVAL 1 MONTH AND SYSDATE() and uias.newscenterId = "+userSelectedIndurtyId+" and u.userid = uias.userid and uias.userId = "+userid+" and "+columnName+" like '%"+searchString+"%' order by newsOpened desc LIMIT "+startRecord+","+pagesize+"";
				 	
			}
			ResultSet rss = stmt.executeQuery(query);
			
			while(rss.next()){
				newsLetterAccessStats = new NewsLetterStats();
				newsLetterAccessStats.setFirstName(rss.getString("firstName"));
				newsLetterAccessStats.setEmail(rss.getString("email"));
				newsLetterAccessStats.setNewsSentDate(rss.getTimestamp("newsSent").toString());
				newsLetterAccessStats.setNewsOpenedDate(rss.getTimestamp("newsOpened"));
				list.add(newsLetterAccessStats);
			}
			rss.close();
			pageresult.setCurrentPage(currentPage);
			double divison = Math.ceil((double) listSize / (double) pagesize);
			pageresult.setTotalNoOfPages((int) divison);
			pageresult.setList(list);
			}
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("problem in getNewsLetterAccessStats()");
		}
		return pageresult;
	}
}
