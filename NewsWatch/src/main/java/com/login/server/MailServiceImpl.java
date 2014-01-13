package com.login.server;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.lighthouse.newsletter.server.LhNewsletterServlet;
import com.login.client.UserInformation;
import com.login.client.UserInformationService;

/**
 * @author prachi
 * 
 *
 */
public class MailServiceImpl extends RemoteServiceServlet implements UserInformationService
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String subject = "";
	private String body = "";
	protected String smtpusername;
	protected String smtppassword;
	protected String smtphost;
	protected String smtpport;
	Logger logger = Logger.getLogger(MailServiceImpl.class.getName());
	
	public boolean sendMailForForgotPassword(UserInformation userinfo)
	{
		logger.log(Level.INFO, "MailServiceImpl :::: sendMailForForgotPassword() initiated");
		HttpServletRequest req = this.getThreadLocalRequest();
		ServletContext context = req.getSession().getServletContext();
		smtpusername = context.getInitParameter("smtpusername");
		smtppassword = context.getInitParameter("smtppassword");
		smtphost = context.getInitParameter("smtphost");
		smtpport = context.getInitParameter("smtpport");
		
		subject=userinfo.getSubjectMail();
		body=userinfo.getBodyMail();
		try{
			MailClient testClient = new MailClient(getSmtpusername(), getSmtppassword(), getSmtpport(), getSmtphost());
			testClient.sendMessage("no_reply@newscatalyst.com", userinfo.getRecipientsMail(), subject, body);
			return false;
		}
		catch(Exception ex){
			logger.log(Level.INFO, "MailServiceImpl :::: sendMailForForgotPassword() exception!! "+ex.getMessage());
			return false;
			
		}
	}

	public boolean sendNewsletters(UserInformation userinfo){
		logger.log(Level.INFO, "MailServiceImpl :::: sendNewsletters() initiated");
		subject=userinfo.getSubjectMail();
		body=userinfo.getBodyMail();
		
		try{
			MailClient testClient = new MailClient(getSmtpusername(), getSmtppassword(), getSmtpport(), getSmtphost());
			testClient.sendMessage("no_reply@newscatalyst.com", userinfo.getRecipientsMail(), subject, body);
			return false;
		}
		catch(Exception ex){
			logger.log(Level.INFO, "MailServiceImpl :::: sendNewsletters() exception!! "+ ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
	
	public String forgotPasswordRetrieve(UserInformation userinfo) {

		return null;
	}

	public String saveUserRecord(UserInformation userinfo) {
		return null;
	}

	public String updateRecord(UserInformation userinfo, String colName,
			String email) {
		return null;
	}

	public ArrayList validateUser(UserInformation userinfo) {
		return null;
	}

	
	public void setValuesToSession(int ncId, String ncName) {
		
	}

	
	public ArrayList checkUserLogin() {
		return null;
	}

	public int emailapprovalCheck(String email) {
		return 0;
	}

	public String getSmtpusername() {
		return smtpusername;
	}

	public void setSmtpusername(String smtpusername) {
		this.smtpusername = smtpusername;
	}

	public String getSmtppassword() {
		return smtppassword;
	}

	public void setSmtppassword(String smtppassword) {
		this.smtppassword = smtppassword;
	}

	public String getSmtphost() {
		return smtphost;
	}

	public void setSmtphost(String smtphost) {
		this.smtphost = smtphost;
	}

	public String getSmtpport() {
		return smtpport;
	}

	public void setSmtpport(String smtpport) {
		this.smtpport = smtpport;
	}
    
	
	/**
	 * This method is used to 
	 * @param userInformation
	 * @param link 
	 */
	public void sendReportDeadLink(UserInformation userInformation) {
		subject=userInformation.getSubjectMail();
		body=userInformation.getBodyMail();
		
		try
		{
			MailClient testClient = new MailClient(getSmtpusername(), getSmtppassword(), getSmtpport(), getSmtphost());
			testClient.sendMessageForReportDeadLink("no_reply@newscatalyst.com", userInformation.getRecipientsMail(), subject, body);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
		}
		
	}
	
	
}