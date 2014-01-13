package com.newscenter.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.login.client.UserInformation;
import com.login.server.MailServiceImpl;
import com.login.server.db.AllocateResources;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.categorydb.ItemProviderServiceImpl;

public class NewsletterServlet extends HttpServlet implements Servlet, Runnable{

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	String connectionUrl;
	String driverClassName;
	String username;
	String password;
	String tomcatpath;
	protected String tomcaturl;
	protected String deploymentname;
	Thread thread;
	ServletConfig servletConfig;
	protected StringBuilder stringBuilder;
	//UserInformationforMail mailService;
	protected MailServiceImpl mailService;
	protected String smtpusername;
	protected String smtppassword;
	protected String smtphost;
	protected String smtpport;
	ItemProviderServiceImpl itemprovider = new ItemProviderServiceImpl();
	
	
	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		servletConfig = config;
        ServletContext context=getServletContext();
        itemprovider.init(servletConfig);
        
     	connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(AllocateResources.USERNAME);
		password =(String)context.getAttribute(AllocateResources.PASSWORD);
		tomcatpath = (String)context.getAttribute(AllocateResources.TOMCATPATH);
		
		smtpusername = context.getInitParameter("smtpusername");
		smtppassword = context.getInitParameter("smtppassword");
		smtphost = context.getInitParameter("smtphost");
		smtpport = context.getInitParameter("smtpport");
		tomcaturl = context.getInitParameter("tomcaturl");
		deploymentname = context.getInitParameter("deploymentname");
		
		thread = new Thread(this);
	//	thread.start();
	}
	
	public void fetchNews(){
		System.out.println("!*!*!*! IN FETCH NEWS METHOD !*!*!*!*!*!*!");
		ItemProviderServiceImpl itemprovider = new ItemProviderServiceImpl();
		try {
			itemprovider.init(servletConfig);
			Timestamp datetime = itemprovider.getNewsletterDeliveryTime();
			HashMap map = itemprovider.getAllUserSelectionMaps(datetime,-1);
			for(Object obj : map.keySet()){
				UserInformation user = (UserInformation)obj;
				createLayout(user);
				NewsItemList newslist = (NewsItemList)map.get(obj);
					 if(newslist.isNoTagSelected()){
							endLayout(newslist.isNoTagSelected(), user);
							sendMail(user);
						}
					 else if(newslist.size()!=0){
						Iterator iter = newslist.iterator();
						while(iter.hasNext()){
							NewsItems news = (NewsItems)iter.next();
							//System.out.println("NEWS ITEMS: " + news.getNewsTitle());
							createNewsItem(news,user);
						}
						endLayout(newslist.isNoTagSelected(), user);
						sendMail(user);
				}
				}
		} 
		catch (ServletException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try{
			while(true){
				//Thread.sleep(86400000);
				fetchNews();
				//thread.sleep(86400000);
			}
		}
		/*catch (InterruptedException e){
			e.printStackTrace();
		
		}*/
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
    	
		

	public HashMap getUsersNewsItemsMap(Timestamp datetime, int userId){
		HashMap map = itemprovider.getAllUserSelectionMaps(datetime,userId);
		return map;
	}
	
	public Timestamp getLastNewsDeliveryTime(){
		Timestamp datetime = itemprovider.getNewsletterDeliveryTime();
		return datetime;
	}
	
	public void createLayout(UserInformation user){
		stringBuilder = new StringBuilder();
		//stringBuilder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		//stringBuilder.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		stringBuilder.append("<html>");
		stringBuilder.append("<head>");
		stringBuilder.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
		stringBuilder.append("</head>");
		stringBuilder.append("<body style=\"border: thin solid rgb(222,222,222);padding:5px 5px 5px 5px; background-image: url(\"images/nz/BGImage1.png\");\">");
		stringBuilder.append("<h3 style='font-family:Arial;font-size:11pt;color:black;margin-left: 5px;text-align:left'>"+user.getIndustryNewsCenterName()+" NewsCatalyst </h3>");
		
		stringBuilder.append("<table width=\"100%\">");
	}
	
	
	public void createNewsItem(NewsItems news,UserInformation user){
		stringBuilder.append("<table width=\"700\" align=\"center\" background=\""+tomcaturl+"images/nz/BGImage1.png\">");
		String name = user.getIndustryNewsCenterName();
		String url = user.getUserSelectedNewsCenterURL()+"/"+deploymentname+"/imagefolder/"+news.getNewsId()+".jpg";
		stringBuilder.append("<tr background=\""+tomcaturl+"images/nz/BGImage1.png\"> <td width=\"75%\" style=\"border-top: 5 solid #C2D22D;\">"); 
		String encodedurl = null;;
		try {
			encodedurl = URLEncoder.encode(news.getUrl().toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		}
		stringBuilder.append("<a href='"+tomcaturl+"com.lighthouse.newsletter.newsletter/emailitemView?info="+String.valueOf(user.getUserId())+"**"+String.valueOf(user.getUserSelectedIndustryID())+"**"+String.valueOf(news.getNewsId())+"**"+encodedurl+"' target=\"new\" style=\"font-family:'Lucida Grande';font-style:normal;font-size: 18px;color:#c4d940;word-wrap:break-word;padding-left:3px;text-decoration:underline;\"><strong class=\"newslink\">"+news.getNewsTitle()+"</strong></a></td>");
		if(!news.getImageUrl().equals(""))
			stringBuilder.append("<td width=\"25%\" rowspan=\"5\" valign=\"top\" style=\"padding-left: 30px;\"><img src=\""+url+"\" alt=\"No Image\" width=\"68\" height=\"68\" style=\"border: 1px solid rgb(196,196,196);margin-left:5px;padding: 5px 5px 5px 5px;\"/></td>");
		else
			stringBuilder.append("<td width=\"25%\" rowspan=\"5\"></td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:9pt;color:black;text-align:left;word-wrap:break-word;padding-left:3px\">"+news.getAbstractNews()+"</td></tr>");
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:8pt;color:rgb(110,110,110);padding-right:5px;padding-left:3px\">PUBLISHED: "+news.getNewsDate()+"<font size = 1pt> | </font> SOURCE: "+news.getNewsSource()+"</td></tr>");
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:8pt;color:rgb(110,110,110);padding-right:5px;padding-left:3px;padding-bottom:8px;text-align:top;word-wrap:break-word;\">TAGS:"); 
		ArrayList list = news.getAssociatedTagList();
		Iterator iter = list.iterator();
		int count = 0;
		while(iter.hasNext()){
			if(count != list.size()-1){
				TagItem tag = (TagItem)iter.next();
				stringBuilder.append("<a href='' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:black;padding-left:2px;padding-right:2px\">"+tag.getTagName()+"</a>");
				stringBuilder.append("<font size = 1pt> | </font>");	
				count++;
			}
			else{
				TagItem tag = (TagItem)iter.next();
				stringBuilder.append("<a href='' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:black;padding-left:2px;padding-right:2px\">"+tag.getTagName()+"</a>");
				break;
			}
			
		} 
		stringBuilder.append("<p></br></p></td></tr>");
		stringBuilder.append("</table>");
	}
	
	public void createStaticNewsItem(NewsItems news,UserInformation user){
		
		String name = user.getIndustryNewsCenterName();
		String url = user.getUserSelectedNewsCenterURL()+"/"+deploymentname+"/imagefolder/"+news.getNewsId()+".jpg";
		String encodedurl = null;
		try {
			encodedurl = URLEncoder.encode(news.getUrl().toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		}
		
		stringBuilder.append("<table cellspacing=\"0\" width=\"100%\">");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td colspan=\"2\">");
		stringBuilder.append("<h2>");
		stringBuilder.append("<a href='"+tomcaturl+"com.lighthouse.newsletter.newsletter/emailitemView?info="+String.valueOf(user.getUserId())+"**"+String.valueOf(user.getUserSelectedIndustryID())+"**"+String.valueOf(news.getNewsId())+"**"+encodedurl+"' target=\"new\">"+news.getNewsTitle()+"</a>");
		stringBuilder.append("</h2>");
		stringBuilder.append("</td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td valign=\"top\">");
		stringBuilder.append("<div style=\"padding: 0px; color: Gray; font-size: 11px;\">");
		stringBuilder.append("<span>PUBLISHED ON</span> : <span>"+news.getNewsDate()+"</span>");
		stringBuilder.append("</div>");
		stringBuilder.append("<div style=\"padding: 0px; color: Gray; font-size: 11px;\">");
		stringBuilder.append("<span>SOURCE </span> : <span>"+news.getNewsSource()+"</span>");
		stringBuilder.append("</div>");
		stringBuilder.append("<div style=\"padding: 0px; color: Gray; font-size: 11px;\">");
		stringBuilder.append("TAGS:");
		ArrayList list = news.getAssociatedTagList();
		Iterator iter = list.iterator();
		int count = 0;
			while(iter.hasNext()){
				if(count != list.size()-1){
					TagItem tag = (TagItem)iter.next();
					stringBuilder.append("<a href='' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:black;padding-left:2px;padding-right:2px\">"+tag.getTagName()+"</a>");
					stringBuilder.append("<font size = 1pt> | </font>");	
					count++;
				}
				else{
					TagItem tag = (TagItem)iter.next();
					stringBuilder.append("<a href='' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:black;padding-left:2px;padding-right:2px\">"+tag.getTagName()+"</a>");
					break;
				}
			}
		stringBuilder.append("</div>");
		stringBuilder.append("<p>"+news.getAbstractNews()+"</p>");
		stringBuilder.append("<p style=\"font-style: italic;\">");
		stringBuilder.append("<td align=\"right\">");
		stringBuilder.append("</td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("</table>");
	}
	
	public void endLayout(boolean bool,UserInformation user){
		stringBuilder.append("</table>");
		if(bool)
			stringBuilder.append("<p style=\"font-family:Arial;font-size:8pt;margin-left: 5px;color:rgb(0,102,202);line-height: 250%;\"> Please be aware that you have not yet configured your newsletter from the "+user.getIndustryNewsCenterName()+" Newscatalyst</p>");
		stringBuilder.append("<p style=\"font-family:Arial;font-size:8pt;margin-left: 5px;color:rgb(0,102,202);line-height: 250%;\"> To adjust filters for your newsletter or to deactivate this alert please click <a href='"+user.getUserSelectedNewsCenterURL()+"' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:rgb(0,102,202)\"> here </a> </p>");
		stringBuilder.append("<p style=\"font-family:Arial;font-size:8pt;margin-left: 5px; padding-bottom: 15px;color:rgb(0,102,202);word-wrap:break-word;\"> For questions regarding the newsletter please contact "+user.getIndustryNewsCenterName()+" principal analyst Miriam Baurichter at +4586204422 or mb@aalundnetsearch.com </p>");
		stringBuilder.append("</body>");
		stringBuilder.append("</html>");
	}
	
	
	public void sendMail(UserInformation user){
		//added by nairutee on 21 aug 2011
		UserInformation userInformation = new UserInformation();
		userInformation.setBodyMail(stringBuilder.toString());
		userInformation.setRecipientsMail(user.getEmail());
		userInformation.setSenderMail("no_reply@newscatalyst.com");
		userInformation.setSubjectMail(user.getIndustryNewsCenterName() + "Newsletter");
		mailService = new MailServiceImpl();
		mailService.setSmtpusername(smtpusername);
		mailService.setSmtppassword(smtppassword);
		mailService.setSmtphost(smtphost);
		mailService.setSmtpport(smtpport);
		mailService.sendNewsletters(userInformation);
		
		
	}
	
	

	private Connection getConnection(){
		try {
			if(conn==null){
				System.out.println("Returning connection for Newsletter Servlet");
				Driver drv =(Driver)Class.forName(driverClassName).newInstance();
				DriverManager.registerDriver(drv);
				conn=DriverManager.getConnection(connectionUrl,username,password);
				return conn;
			}
			else
				return conn;
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return null ;
	}
	
	protected void finalize() throws Throwable{
		super.finalize();
		if (conn != null)
			conn.close();
	}

	public void closeConnection(){
		try{
			if(conn!=null){
				System.out.println("Closing connection for Newsletter Servlet");
				conn.close();
			}
			}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public ServletConfig getServletConfig() {
		return servletConfig;
	}

	public void setServletConfig(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}
}
