package com.lighthouse.main.server;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lighthouse.login.user.server.LhUserHelper;
import com.login.client.UserInformation;

/**
 * 
 * @author prachi
 *This servlet is  used for sending email to client after a dead link is reported .  
 */
public class ReportDeadLinkServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServletContext context;
	private StringBuilder stringBuilder;
	
	String smtpusername;
	String smtppassword;
	String smtphost;
	String smtpport;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		UserInformation userinformation = (UserInformation)req.getSession().getAttribute("userInfo");
		int newsCenterId = userinformation.getUserSelectedIndustryID();
		
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
			
			/*getServletContext().log("Returning connection for ReportDeadLinkServlet");
			Driver drv =(Driver)Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(drv);
			Connection conn=DriverManager.getConnection(connectionUrl,username,password);*/
			
			String title = req.getParameter("title");
			String link = req.getParameter("link");
			String message = req.getParameter("description");		
			sendMailForReportDeadLink(title,link,message,newsCenterId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**Done with postfix server.
	 * This method will send mail to admin specifically with the pre-populated text.
	 * @param newsCenterId 
	 * @param userinformation
	 */
	public void sendMailForReportDeadLink(String title, String link, String message, int newsCenterId) {
		UserInformation userInformation = new UserInformation();
		userInformation.setSenderMail("no_reply@newscatalyst.com");
		stringBuilder = new StringBuilder();
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:11pt;color:black;text-align:left;padding-left:3px\">"+message+"<br><br><b>Title</b>   "+title+"</td></tr><br>");
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:11pt;color:black;text-align:left;padding-left:3px\"><b>Link</b>   "+link+"</td></tr><br>");
		userInformation.setBodyMail(stringBuilder.toString());
		userInformation.setSubjectMail(title);
		userInformation.setUserSelectedIndustryID(newsCenterId);
		LhUserHelper helper = new LhUserHelper();
		helper.setSmtpusername(smtpusername);
		helper.setSmtppassword(smtppassword);
		helper.setSmtphost(smtphost);
		helper.setSmtpport(smtpport);
		
		helper.sendMailForReportDeadlink(userInformation);
		helper.closeConnection();
	}




	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		super.doGet(req, resp);
		UserInformation userinformation = (UserInformation)req.getSession().getAttribute("userInfo");
		int newsCenterId = userinformation.getUserSelectedIndustryID();
		
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
			
			/*getServletContext().log("Returning connection for ReportDeadLinkServlet");
			Driver drv =(Driver)Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(drv);
			Connection conn=DriverManager.getConnection(connectionUrl,username,password);*/
			
			String title = req.getParameter("title");
			String link = req.getParameter("link");
			String message = req.getParameter("description");
			
			sendMailForReportDeadLink(title,link,message,newsCenterId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
