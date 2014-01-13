package com.lighthouse.newsletter.server;

/**
 * author sachin@ensarm.com
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lighthouse.newsletter.client.domain.NewsletterInformation;
import com.lighthouse.utils.server.EncrypterDecrypter;
import com.login.server.db.AllocateResources;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

public class AlertRssServlet  extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String MIME_TYPE = "application/xml; charset=UTF-8";
    private static final String COULD_NOT_GENERATE_FEED_ERROR = "Could not generate feed";

    private static final DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");
    Logger logger = Logger.getLogger(AlertRssServlet.class.getName());
    
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		logger.log(Level.INFO, "[ AlertRssSevlet initiated ]");
		EncrypterDecrypter encrypterDecrypter=EncrypterDecrypter.getInstance();
		try {
            SyndFeed feed = null;
            String para= req.getParameter("value");
            if(para.contains(" ")){
            	para = new StringBuffer(para).insert(para.indexOf(" ")+1, "+").toString();
                StringBuffer str= new StringBuffer(para);
                str.deleteCharAt(para.indexOf(" "));
                para=str.toString();
            }
            
            String queryVal=encrypterDecrypter.getDecryptedString(para);
            queryVal=encrypterDecrypter.getUrlDecodedString(queryVal);
            String id[]=null;
            String param[]=queryVal.split("&");
            String[] idArray = new String[3];
            for(int i=0;i<param.length;i++){
            	 id=param[i].split("=");
            	 idArray[i]=id[1];
            }
            Integer userId =Integer.parseInt(idArray[0]);
			Integer ncid = Integer.parseInt(idArray[1]);
            Integer alertId=Integer.parseInt(idArray[2]);
            feed = getFeed(req,userId,alertId,ncid);
            String feedType = "atom_0.3";
            feed.setFeedType(feedType);

            resp.setContentType(MIME_TYPE);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed,resp.getWriter());
        }
        catch (Exception ex) {
        	logger.log(Level.INFO, "[ AlertRssSevlet EXCEPTION!!! ]"+ex.getMessage());
            String msg = COULD_NOT_GENERATE_FEED_ERROR;
            log(msg,ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,msg);
        }
    }

    protected SyndFeed getFeed(HttpServletRequest req,int userId,int alertId,int ncid) throws IOException,FeedException, Exception {
    	ServletContext context=getServletContext();
		String connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		String driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		String username =(String)context.getAttribute(AllocateResources.USERNAME);
		String password =(String)context.getAttribute(AllocateResources.PASSWORD);
		String tomcaturl = context.getInitParameter("tomcaturl");
		
		getServletContext().log("Returning connection for Newsletter Servlet");
		Driver drv =(Driver)Class.forName(driverClassName).newInstance();
		DriverManager.registerDriver(drv);
		Connection conn=DriverManager.getConnection(connectionUrl,username,password);
		Statement stmt = conn.createStatement();
		String newsTitle;
		String newsAbstract;
		String publisheddate;
		UserNewsletterAlertConfigHelper alertConfigHelper=new UserNewsletterAlertConfigHelper();
    	NewsletterInformation newsletterInformation = null;
    	SyndFeed feed = new SyndFeedImpl();
    	
    	String query = "SELECT  i.name,n.Description FROM industryenum i,newscenter n where i.IndustryEnumId="+ncid+" limit 1";
    	ResultSet rs = stmt.executeQuery(query);
    	while(rs.next()){
    		 feed.setTitle(rs.getString("name"));
    	     feed.setDescription(rs.getString("Description"));
    		
    	}
    	rs.close();
    	stmt.close();
    	
    	List entries = new ArrayList();
        SyndEntry entry;
        SyndContent description;
        try{
     
        	newsletterInformation = alertConfigHelper.getNewsItemForAlert(alertId, userId, ncid,this.getServletConfig());
        	 
        	 if(newsletterInformation != null){
        		 HashMap<TagItem, List<NewsItems>> newsMap = newsletterInformation.getNewsMap();
        		 for(TagItem primaryTag : newsMap.keySet()){
        			 List<NewsItems> newsList = newsMap.get(primaryTag);
        			 for(NewsItems newsItem : newsList){
        				 newsTitle = new String(newsItem.getNewsTitle().getBytes("utf-8"),"utf-8");
        				 String encodedurl = newsItem.getUrl();
        				 if(!encodedurl.matches("^(https?)://.+$")){
        						encodedurl = "http://"+encodedurl;
        				 }
        				 try {
        					encodedurl = URLEncoder.encode(encodedurl.toString(), "UTF-8");
        				 } catch (UnsupportedEncodingException e) {
        					logger.log(Level.INFO, "[LhNewsletterServlet --- encodedurl exception --- createNews()]");
        					 e.printStackTrace();
        				 }
        				 String targetUrl = tomcaturl+"com.lighthouse.newsletter.newsletter/emailitemView?info="+String.valueOf(userId)+"**"+String.valueOf(ncid)+"**"+String.valueOf(newsItem.getNewsId())+"**"+String.valueOf(newsItem.getIsLocked())+"**"+String.valueOf(newsItem.getIsReportItem())+"**"+encodedurl;
  	      		       
        				 newsAbstract = new String(newsItem.getAbstractNews().getBytes("utf-8"),"utf-8");
  	      		         publisheddate = newsItem.getNewsDate();
  	      		         
  	      		         entry = new SyndEntryImpl();
  	      		         entry.setTitle(newsTitle);
  	      		         entry.setLink(targetUrl);
  	      		         entry.setPublishedDate(DATE_PARSER.parse(publisheddate));
  	      		         description = new SyndContentImpl();
  	      		         description.setType("text/plain");
  	      		         description.setValue(newsAbstract);
  	      		         entry.setDescription(description);
  	      		         entries.add(entry);
        			 }
        		 }
        		 
	       /* 	 for(Object item :itemList){
	        		 if(item instanceof NewsItems){
	        			 newsItem =(NewsItems)item;
	        			 
	        		   newsTitle = new String(newsItem.getNewsTitle().getBytes("utf-8"),"utf-8");
	      		       url =newsItem.getUrl();
		      			if(!url.matches("^(https?)://.+$")){
		      				url = "http://"+url;
		      			}
	      		       newsAbstract = new String(newsItem.getAbstractNews().getBytes("utf-8"),"utf-8");
	      		       publisheddate = newsItem.getNewsDate();
	      		       source = newsItem.getNewsSource();
	      		       entry = new SyndEntryImpl();
	      		       entry.setTitle(newsTitle);
	      		       entry.setLink(url);
	      		       entry.setPublishedDate(DATE_PARSER.parse(publisheddate));
	      		       description = new SyndContentImpl();
	      		       description.setType("text/plain");
	      		       description.setValue(newsAbstract);
	      		       entry.setDescription(description);
	      		       entries.add(entry);
	        		 }
	        	 }*/
        	}
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        finally{
        	conn.close();
        }
        
        feed.setEntries(entries);
        alertConfigHelper.closeConnection();
        return feed;
    }
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
