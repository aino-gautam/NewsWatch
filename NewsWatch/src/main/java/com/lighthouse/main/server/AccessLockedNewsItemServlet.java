package com.lighthouse.main.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.lighthouse.login.user.server.LhUserHelper;
import com.login.client.UserInformation;
import com.login.server.MailServiceImpl;
import com.login.server.db.AllocateResources;
import com.newscenter.client.news.NewsItems;

/**
 * 
 * @author prachi
 *This servlet is  used for sending email to client after a dead link is reported .  
 */
public class AccessLockedNewsItemServlet extends HttpServlet {
	
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
	Logger logger = Logger.getLogger(AccessLockedNewsItemServlet.class.getName());
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		logger.log(Level.INFO, "[AccessLockedNewsItemServlet initiated ]");
		UserInformation userinformation = (UserInformation)req.getSession().getAttribute("userInfo");
		user = userinformation;
		userEmail = userinformation.getEmail();
		userName = userinformation.getFirstname();
		userLastName = userinformation.getLastname();
		newsItem = new NewsItems();
		newsInfolist = new ArrayList();
		try {
			context=getServletContext();
			/*String connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
			String driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
			String username =(String)context.getAttribute(AllocateResources.USERNAME);
			String password =(String)context.getAttribute(AllocateResources.PASSWORD);*/
				
			smtpusername = context.getInitParameter("smtpusername");
			smtppassword = context.getInitParameter("smtppassword");
			smtphost = context.getInitParameter("smtphost");
			smtpport = context.getInitParameter("smtpport");
			tomcaturl = context.getInitParameter("tomcaturl");
			deploymentname = context.getInitParameter("deploymentname");
			
			/*getServletContext().log("Returning connection for AccessLockedNewsItemServlet");
			Driver drv =(Driver)Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(drv);
			Connection conn=DriverManager.getConnection(connectionUrl,username,password);*/
			
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			ArrayList items = null;
			items = (ArrayList) upload.parseRequest(req);
			Iterator iter = items.iterator();
			while(iter.hasNext()){
				DiskFileItem item = (DiskFileItem)iter.next();
				if(item.isFormField()){
					String fieldName = item.getFieldName();
					if(fieldName.equals("newsInformation")){
				    	String tags = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
				    	String[] attributeName = new String[tags.length()];
				    	attributeName = tags.split("[$|$]");
				    	for(int i=0;i<attributeName.length;i++){
				    		if(!attributeName[i].isEmpty()){
				    			newsInfolist.add(attributeName[i]);
				    		}
				    	}
					}
					else if(fieldName.equals("txtNewsItemTags")){
				    	ArrayList arraylist = new ArrayList();
				    	String tags = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
				    	String[] tagname = new String[tags.length()];
				    	tagname = tags.split(";");
				    	for(int i=0;i<tagname.length;i++){
				    		if(!tagname[i].isEmpty())
				    			arraylist.add(tagname[i]);
				    	}
				    	newsItem.setAssociatedTagList(arraylist);
					}
				}
			}
			newsItem.setNewsId(Integer.parseInt((String)newsInfolist.get(0)));
			newsItem.setNewsTitle((String) newsInfolist.get(1));
			newsItem.setAbstractNews((String) newsInfolist.get(2));
			newsItem.setNewsSource((String) newsInfolist.get(3));
			newsItem.setUrl((String) newsInfolist.get(4));
			newsItem.setNewsDate((String) newsInfolist.get(5));
			newsItem.setImageUrl((String) newsInfolist.get(6));
			
			sendMailForAccessNewsItem();
			logger.log(Level.INFO, "[AccessLockedNewsItemServlet completed ]");
			
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
		stringBuilder.append("<a href='"+tomcaturl+"com.lighthouse.main.lhmain/emailitemView?info="+String.valueOf(user.getUserId())+"**"+String.valueOf(user.getUserSelectedIndustryID())+"**"+String.valueOf(news.getNewsId())+"**"+String.valueOf(news.getIsLocked())+"**"+String.valueOf(news.getIsReportItem())+"**"+encodedurl+"' target=\"new\" style=\"font-family:Arial;color:#0066CA;word-wrap:break-word;padding-left:3px\"><font size=\"2\"><strong class=\"newslink\">"+news.getNewsTitle()+"</strong></font></a></td>");
		if(!news.getImageUrl().equals("-"))
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
				String tagName = (String)iter.next();
				stringBuilder.append("<a href='' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:black;padding-left:2px;padding-right:2px\">"+tagName+"</a>");
				stringBuilder.append("<font size = 1pt> | </font>");	
				count++;
			}
			else{
				String tagName = (String)iter.next();
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
		logger.log(Level.INFO, "[AccessLockedNewsItemServlet sendMailForAccessNewsItem() initiated ]");
		UserInformation userInfo = new UserInformation();
		userInfo.setSenderMail("no_reply@newscatalyst.com");
		stringBuilder = new StringBuilder();
		getEmailHeader();
		getEmailBodyMessage();
		createNewsItem(newsItem, user);
		//getEmailFooter();
		userInfo.setBodyMail(stringBuilder.toString());
		userInfo.setSubjectMail("Access requested to locked news item");
		userInfo.setUserSelectedIndustryID(user.getUserSelectedNewsCenterID());
		LhUserHelper helper = new LhUserHelper();
		helper.setSmtpusername(context.getInitParameter("smtpusername"));
		helper.setSmtppassword(context.getInitParameter("smtppassword"));
		helper.setSmtphost(context.getInitParameter("smtphost"));
		helper.setSmtpport(context.getInitParameter("smtpport"));
		helper.sendMailForAccessNewsItem(userInfo);
		helper.closeConnection();
		logger.log(Level.INFO, "[AccessLockedNewsItemServlet sendMailForAccessNewsItem() completed ]");
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
