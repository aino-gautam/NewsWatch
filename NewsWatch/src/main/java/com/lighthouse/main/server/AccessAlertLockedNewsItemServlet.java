package com.lighthouse.main.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.lighthouse.login.user.server.LhUserHelper;
import com.lighthouse.main.server.helper.LhNewsItemHelper;
import com.login.client.UserInformation;
import com.login.server.MailServiceImpl;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

public class AccessAlertLockedNewsItemServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServletContext context;
	private StringBuilder stringBuilder;
	
	MailServiceImpl mailService;
	String smtpusername;
	String smtppassword;
	String smtphost;
	String smtpport;
	String newsInfoMessage;
	NewsItems newsItem;
	String tomcatpath;
	String tomcaturl;
	String deploymentname;
	String userEmail;
	String userName;
	String userLastName;
	ArrayList newsInfolist;
	
	UserInformation user;
	Logger logger = Logger.getLogger(AccessAlertLockedNewsItemServlet.class.getName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		
		logger.log(Level.INFO, "[In AccessAlertLockedNewsItemServlet initiated]");
		
		newsItem = new NewsItems();
		newsInfolist = new ArrayList();
		try {
			context=getServletContext();
						
			/*smtpusername = context.getInitParameter("smtpusername");
			smtppassword = context.getInitParameter("smtppassword");
			smtphost = context.getInitParameter("smtphost");
			smtpport = context.getInitParameter("smtpport");
			tomcaturl = context.getInitParameter("tomcaturl");
			deploymentname = context.getInitParameter("deploymentname");*/
			
			LhNewsItemHelper helper=new LhNewsItemHelper(null, tomcaturl);
			Integer newsId = Integer.parseInt(req.getParameter("newsId"));
			Integer usrid = Integer.parseInt(req.getParameter("uid"));
			newsItem=helper.getNewsItem(newsId);
			
			UserInformation userinformation = helper.getUser(usrid);
			user = userinformation;
			user.setUserId(usrid);
			userEmail = userinformation.getEmail();
			userName = userinformation.getFirstname();
			userLastName = userinformation.getLastname();
			sendMailForAccessNewsItem();
			helper.closeConnection();
			logger.log(Level.INFO, "[In AccessAlertLockedNewsItemServlet completed]");
			
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
	
	public void createNewsItem(NewsItems news,UserInformation user){
		stringBuilder.append("<table width=\"100%\">");
		String url = tomcaturl+"imagefolder/"+news.getNewsId()+".jpg";
		stringBuilder.append("<tr> <td width=\"75%\">"); 
		String encodedurl = news.getUrl();
		if(!encodedurl.matches("^(https?)://.+$")){
			encodedurl = "http://"+encodedurl;
		}
		try {
			encodedurl = URLEncoder.encode(encodedurl.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		stringBuilder.append("<a href='"+tomcaturl+"com.lighthouse.main.lhmain/emailitemView?info="+String.valueOf(user.getUserId())+"**"+String.valueOf(user.getUserSelectedIndustryID())+"**"+String.valueOf(news.getNewsId())+"**0**"+String.valueOf(news.getIsReportItem())+"**"+encodedurl+"' target=\"new\" style=\"font-family:Arial;color:#0066CA;word-wrap:break-word;padding-left:3px\"><font size=\"2\"><strong class=\"newslink\">"+news.getNewsTitle()+"</strong></font></a></td>");
		if(news.getImageUrl().equals("-"))
			stringBuilder.append("<td width=\"25%\" rowspan=\"5\" valign=\"top\" style=\"padding-left: 30px;\"><img src=\""+url+"\" width=\"68\" height=\"68\" alt=\"No Image\" style=\"border: 1px solid rgb(196,196,196);margin-left:5px;padding: 5px 5px 5px 5px;\"/></td>");
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
				TagItem tagItem=(TagItem)iter.next();
				String tagName = tagItem.getTagName();
				stringBuilder.append("<a href='' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:black;padding-left:2px;padding-right:2px\">"+tagName+"</a>");
				stringBuilder.append("<font size = 1pt> | </font>");	
				count++;
			}
			else{
				TagItem tagItem=(TagItem)iter.next();
				String tagName = tagItem.getTagName();
				stringBuilder.append("<a href='' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:black;padding-left:2px;padding-right:2px\">"+tagName+"</a>");
				break;
			}
			
		} 
		stringBuilder.append("<p></br></p></td></tr>");
		stringBuilder.append("</table>");
	}


	/**Done with postfix server.
	 * This method will send mail to admin specifically with the pre-populated text.
	 * @param userinformation
	 */
	public void sendMailForAccessNewsItem() {
		logger.log(Level.INFO, "[In AccessAlertLockedNewsItemServlet sendMailForAccessNewsItem() initiated]");
		UserInformation userInfo = new UserInformation();
		userInfo.setSenderMail("no_reply@newscatalyst.com");
		stringBuilder = new StringBuilder();
		getEmailHeader();
		getEmailBodyMessage();
		createNewsItem(newsItem, user);
		//getEmailFooter();
		userInfo.setBodyMail(stringBuilder.toString());
		userInfo.setSubjectMail("Access requested to locked news item");
		userInfo.setUserSelectedIndustryID(user.getUserSelectedIndustryID());
		LhUserHelper helper = new LhUserHelper();
		helper.setSmtpusername(context.getInitParameter("smtpusername"));
		helper.setSmtppassword(context.getInitParameter("smtppassword"));
		helper.setSmtphost(context.getInitParameter("smtphost"));
		helper.setSmtpport(context.getInitParameter("smtpport"));
		helper.sendMailForAccessNewsItem(userInfo);
		helper.closeConnection();
		logger.log(Level.INFO, "[In AccessAlertLockedNewsItemServlet sendMailForAccessNewsItem() completed]");
	}
	
	private void getEmailHeader() {
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:10pt;color:black;text-align:left;padding-left:3px\">Dear Admin,<br></td></tr><br>");
	}
	
	private void getEmailBodyMessage() {
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:10pt;color:black;text-align:left;padding-left:3px\">The user, "+userName+" "+userLastName+" ( "+userEmail+" ) has requested acces to the following News Item:<br><br></td></tr><br>");
	}
	
	private void getEmailFooter() {
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:10pt;color:black;text-align:left;padding-left:3px\">Thank You,<br></td></tr><br>");
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:10pt;color:black;text-align:left;padding-left:3px\">"+userName+"<br></td></tr><br>");
	}
}
