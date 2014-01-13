package com.lighthouse.newspage.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.login.client.UserInformation;
import com.login.server.MailServiceImpl;
import com.login.server.db.AllocateResources;
import com.newscenter.client.news.NewsItems;

/**
 * ShareStoryServlet for sharing particular user story with another user
 * 
 * @author kiran@ensarm.com & prachi@ensarm.com
 * 
 */
public class ShareStoryServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServletContext context;
	private StringBuilder stringBuilder;

	NewsItems newsItem = new NewsItems();
	MailServiceImpl mailService;
	String smtpusername;
	String smtppassword;
	String smtphost;
	String smtpport;
	String tomcaturl;
	String deploymentname;
	UserInformation user;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Logger logger = Logger.getLogger(ShareStoryServlet.class.getName());
		logger.log(Level.SEVERE, "ShareStoryServlet ::: doPost :: ");
		UserInformation userinformation = (UserInformation) req.getSession()
				.getAttribute("userInfo");
		user = userinformation;
		try {

			context = getServletContext();
			String connectionUrl = (String) context
					.getAttribute(AllocateResources.DBCONNECTIONURL);
			String driverClassName = (String) context
					.getAttribute(AllocateResources.DRIVERCLASSNAME);
			String username = (String) context
					.getAttribute(AllocateResources.USERNAME);
			String password = (String) context
					.getAttribute(AllocateResources.PASSWORD);

			smtpusername = context.getInitParameter("smtpusername");
			smtppassword = context.getInitParameter("smtppassword");
			smtphost = context.getInitParameter("smtphost");
			smtpport = context.getInitParameter("smtpport");
			tomcaturl = context.getInitParameter("tomcaturl");
			deploymentname = context.getInitParameter("deploymentname");
			getServletContext().log("Returning connection for ShareStoryServlet");
			Driver drv = (Driver) Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(drv);
			Connection conn = DriverManager.getConnection(connectionUrl,
					username, password);
			
			String s="";
			String userEmail = req.getParameter("email");
			String title = req.getParameter("title");		
			String abstractNews = req.getParameter("abstractNews");
			String newsDate = req.getParameter("date");
			String source = req.getParameter("source");
			String newsId = req.getParameter("id");
			String url = req.getParameter("url");
			String newsTag = req.getParameter("newsItemTag");
			String imageUrl = req.getParameter("imageUrl");
			
			ArrayList arraylist = new ArrayList();
			String[] tagname = new String[newsTag.length()];
			tagname = newsTag.split(";");
			for (int i = 0; i < tagname.length; i++) {
				if (!tagname[i].isEmpty())
					arraylist.add(tagname[i]);
			}
			newsItem.setAssociatedTagList(arraylist);
			newsItem.setImageUrl(imageUrl);
			
			String[] strEmail = userEmail.split(",");
			int newsid = Integer.parseInt(newsId);
			for (int index = 0; index < strEmail.length; index++) {

				String queryForFetchUser = "SELECT UserId FROM user u where email ='"
						+ strEmail[index] + "'";

				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(queryForFetchUser);
				UserInformation userinfo = new UserInformation();
				while (resultSet.next()) {

					userinfo.setUserId(resultSet.getInt("UserId"));

				}
				Integer userId = userinfo.getUserId();
				if (userId == 0) {
					Date date = new Date();
					java.sql.Timestamp sharedDate = new java.sql.Timestamp(
							date.getTime());
					String shareHistory = "insert into useritemsharehistory(id,sourceUser,destEmail,newsItemId,sharedDate)values(null,"
							+ userinformation.getUserId()
							+ ",'"
							+ strEmail[index]
							+ "',"
							+ newsid
							+ ",'"
							+ sharedDate + "')";
					Statement stat = conn.createStatement();
					stat.executeUpdate(shareHistory);
					String industryLink = "SELECT Description,Name FROM industryenum i where IndustryEnumId ="
							+ userinformation.getUserSelectedNewsCenterID()
							+ "";
					Statement statLink = conn.createStatement();
					ResultSet set = statLink.executeQuery(industryLink);
					String description = null, name = null;
					while (set.next()) {
						description = set.getString("Description");
						name = set.getString("Name");

					}
					sendMailForShareStoryNonExistUser(strEmail[index],
							userinformation.getEmail(), title, abstractNews,
							newsDate, source, newsId, url, name, description);

				} else {

					Date date = new Date();
					java.sql.Timestamp sharedDate = new java.sql.Timestamp(
							date.getTime());
					String shareHistory = "insert into useritemsharehistory(id,sourceUser,destinationUser,destEmail,newsItemId,sharedDate)values(null,"
							+ userinformation.getUserId()
							+ ","
							+ userinfo.getUserId()
							+ ",'"
							+ strEmail[index]
							+ "'," + newsid + ",'" + sharedDate + "')";
					Statement stat = conn.createStatement();
					stat.executeUpdate(shareHistory);
					String industryLink = "SELECT Description,Name FROM industryenum i where IndustryEnumId ="
							+ userinformation.getUserSelectedNewsCenterID()
							+ "";
					Statement statLink = conn.createStatement();
					ResultSet set = statLink.executeQuery(industryLink);
					String description = null, name = null;
					while (set.next()) {
						description = set.getString("Description");
						name = set.getString("Name");

					}
					sendMailForShareStory(strEmail[index],userinformation.getEmail(), title, abstractNews,
							newsDate, source, newsId, url,userinformation.getUserSelectedNewsCenterURL(),name);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method for send Mail For ShareStory NonExistUser
	 * 
	 * @param userEmail
	 * @param loggedUser
	 * @param title
	 * @param abstractNews
	 * @param newsDate
	 * @param source
	 * @param newsId
	 * @param url
	 * @param ncName
	 * @param description
	 */
	private void sendMailForShareStoryNonExistUser(String userEmail,
			String loggedUser, String title, String abstractNews,
			String newsDate, String source, String newsId, String url,
			String ncName, String description) {
		UserInformation userInformation = new UserInformation();
		newsItem.setNewsTitle(title);
		newsItem.setAbstractNews(abstractNews);
		newsItem.setNewsDate(newsDate);
		newsItem.setNewsSource(source);
		newsItem.setUrl(url);
		int newsid = Integer.parseInt(newsId);
		newsItem.setNewsId(newsid);
		String subject = "" + loggedUser + " "
				+ " has shared a news item with you from" + " " + ncName + "";

		userInformation.setRecipientsMail(userEmail); // admin user
		userInformation.setSenderMail("no_reply@newscatalyst.com");
		stringBuilder = new StringBuilder();
		getEmailHeader(userEmail, loggedUser);
		createNewsItem(newsItem, user);
		getEmailFooter(ncName, description);
		userInformation.setBodyMail(stringBuilder.toString());
		userInformation.setSubjectMail(subject);

		mailService = new MailServiceImpl();
		mailService.setSmtpusername(smtpusername);
		mailService.setSmtppassword(smtppassword);
		mailService.setSmtphost(smtphost);
		mailService.setSmtpport(smtpport);
		mailService.sendReportDeadLink(userInformation);

	}

	/**
	 * This method for creating news items
	 * 
	 * @param newsItem
	 * @param userInformation
	 */
	private void createNewsItem(NewsItems newsItem,
			UserInformation userInformation) {
		stringBuilder.append("<table width=\"100%\">");
		String name = userInformation.getIndustryNewsCenterName();
		/*String url = userInformation.getUserSelectedNewsCenterURL() + "/"
				+ deploymentname + "/imagefolder/" + newsItem.getNewsId()
				+ ".jpg";*/
		String url = tomcaturl+"imagefolder/"+newsItem.getNewsId()+".jpg";
		
		
		stringBuilder.append("<tr> <td width=\"75%\">");
		String encodedurl = newsItem.getUrl();
		if(!encodedurl.matches("^(https?)://.+$")){
			encodedurl = "http://"+encodedurl;
		}
		try {
			encodedurl = URLEncoder.encode(encodedurl.toString(),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		stringBuilder
				.append("<a href='"
						+ tomcaturl+ "com.lighthouse.main.lhmain/emailitemView?info="+ String.valueOf(userInformation.getUserId())
						+ "**"
						+ String.valueOf(userInformation
								.getUserSelectedIndustryID())
						+ "**"
						+ String.valueOf(newsItem.getNewsId())
						+ "**"+String.valueOf(newsItem.getIsLocked())+"**"
						+String.valueOf(newsItem.getIsReportItem())+"**"
						+ encodedurl
						+ "' target=\"new\" style=\"font-family:Arial;color:#0066CA;word-wrap:break-word;padding-left:3px\"><font size=\"2\"><strong class=\"newslink\">"
						+ newsItem.getNewsTitle() + "</strong></font></a></td>");

		if (!newsItem.getImageUrl().equals("-"))
			stringBuilder
					.append("<td width=\"25%\" rowspan=\"5\" valign=\"top\" style=\"padding-left: 30px;\"><img src=\""
							+ url
							+ "\" alt=\"No Image\" width=\"68\" height=\"68\" style=\"border: 1px solid rgb(196,196,196);margin-left:5px;padding: 5px 5px 5px 5px;\"/></td>");
		else
			stringBuilder.append("<td width=\"25%\" rowspan=\"5\"></td>");
		stringBuilder.append("</tr>");
		stringBuilder
				.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:9pt;color:black;text-align:left;word-wrap:break-word;padding-left:3px\">"
						+ newsItem.getAbstractNews() + "</td></tr>");
		stringBuilder
				.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:8pt;color:rgb(110,110,110);padding-right:5px;padding-left:3px\">PUBLISHED: "
						+ newsItem.getNewsDate()
						+ "<font size = 1pt> | </font> SOURCE: "
						+ newsItem.getNewsSource() + "</td></tr>");
		stringBuilder
				.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:8pt;color:rgb(110,110,110);padding-right:5px;padding-left:3px;padding-bottom:8px;text-align:top;word-wrap:break-word;\">TAGS:");

		ArrayList list = newsItem.getAssociatedTagList();
		Iterator iter = list.iterator();
		int count = 0;
		while (iter.hasNext()) {
			if (count != list.size() - 1) {
				String tagName = (String) iter.next();
				stringBuilder
						.append("<a href='' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:black;padding-left:2px;padding-right:2px\">"
								+ tagName + "</a>");
				stringBuilder.append("<font size = 1pt> | </font>");
				count++;
			} else {
				String tagName = (String) iter.next();
				stringBuilder
						.append("<a href='' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:black;padding-left:2px;padding-right:2px\">"
								+ tagName + "</a>");
				break;
			}

		}

		stringBuilder.append("<p></br></p></td></tr>");
		stringBuilder.append("</table>");

	}

	/**
	 * This method for making the footer for share story mail
	 * 
	 * @param industryName
	 * @param description
	 */
	private void getEmailFooter(String industryName, String description) {
		stringBuilder
				.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:10pt;color:black;text-align:left;padding-left:3px\">To view more related news, please subscribe to the "
						+ industryName + " " + " " + "newscatalyst" + " " + " "

						+ "</td></tr>");
		stringBuilder
				.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:10pt;color:black;text-align:left;padding-left:3px\">via "
						+ " " + description + "<br></td></tr><br>");

	}

	/**
	 * This method for design the email header for share story
	 * 
	 * @param userEmail
	 * @param loggedUser
	 */
	private void getEmailHeader(String userEmail, String loggedUser) {
		stringBuilder
				.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:10pt;color:black;text-align:left;padding-left:3px\">Dear "
						+ userEmail + ",<br></td></tr><br>");
		stringBuilder
				.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:10pt;color:black;text-align:left;padding-left:3px\"> "
						+ loggedUser
						+ " "
						+ " "
						+ "wants to share the following news with you,<br></td></tr><br>");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) {
		try {
			doPost(req, res);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Done with postfix server. This method will send mail to admin
	 * specifically with the pre-populated text.
	 * 
	 * @param userEmail
	 * @param newsId2
	 * @param ncNAme
	 * @param name
	 * @param string
	 * @param description
	 * @param string
	 * @param userinformation
	 */
	public void sendMailForShareStory(String userEmail, String currentUser,
			String title, String abstractNews, String date, String source,
			String newsId2, String url, String ncNAme, String name) {

		UserInformation userInformation = new UserInformation();

		newsItem.setNewsTitle(title);
		newsItem.setAbstractNews(abstractNews);
		newsItem.setNewsDate(date);
		newsItem.setNewsSource(source);
		int newsId = Integer.parseInt(newsId2);
		String subject = "" + currentUser + " "
				+ "has shared a news item with you from " + " " + name + "";
		newsItem.setNewsId(newsId);
		newsItem.setUrl(url);
		userInformation.setRecipientsMail(userEmail); // admin user
		userInformation.setSenderMail("no_reply@newscatalyst.com");
		stringBuilder = new StringBuilder();
		getEmailHeader(userEmail, currentUser);

		createNewsItem(newsItem, user);

		userInformation.setBodyMail(stringBuilder.toString());
		userInformation.setSubjectMail(subject);

		mailService = new MailServiceImpl();
		mailService.setSmtpusername(smtpusername);
		mailService.setSmtppassword(smtppassword);
		mailService.setSmtphost(smtphost);
		mailService.setSmtpport(smtpport);
		mailService.sendReportDeadLink(userInformation);

	}

}
