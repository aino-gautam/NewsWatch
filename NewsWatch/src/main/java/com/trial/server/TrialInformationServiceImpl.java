package com.trial.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.common.client.PageResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.login.client.UserInformation;
import com.login.server.db.AllocateResources;
import com.newscenter.client.criteria.PageCriteria;
import com.trial.client.TrialAccount;
import com.trial.client.TrialInformationService;
import com.trial.server.TrialDBHelper.TrialHelper;

public class TrialInformationServiceImpl extends RemoteServiceServlet implements
		TrialInformationService {

	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private String connectionUrl;
	private String driverClassName;
	private String username;
	private String password;
	private String tomcatpath;
	private ServletConfig servletconfig;
	private ServletContext context;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		servletconfig = config;
		context=getServletContext();
		connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(AllocateResources.USERNAME);
		password =(String)context.getAttribute(AllocateResources.PASSWORD);
		tomcatpath = (String)context.getAttribute(AllocateResources.TOMCATPATH);
	}
	
	@Override
	public HashMap<Integer, String> getCatalystList() {
		HttpServletRequest req = this.getThreadLocalRequest();
		TrialHelper trialhelper = new TrialHelper(req,context,servletconfig,connectionUrl,driverClassName,username,password);
		HashMap<Integer,String> map = trialhelper.getCatalyst();
		return map;
	}

	@Override
	public UserInformation getuserinformation() {
		HttpServletRequest req = this.getThreadLocalRequest();
		TrialHelper trialhelper = new TrialHelper(req,context,servletconfig,connectionUrl,driverClassName,username,password);
		HttpSession session = req.getSession(false);
		UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
		int userid = userInformation.getUserId();
		String useremail = userInformation.getEmail();
		UserInformation userinfo = trialhelper.getuserinformation(userid, useremail);
		return userinfo;
	}

	@Override
	public boolean createTrialAccount(TrialAccount user,String mailformat,String subject) {
		HttpServletRequest req = this.getThreadLocalRequest();
		TrialHelper trialhelper = new TrialHelper(req,context,servletconfig,connectionUrl,driverClassName,username,password);
		trialhelper.setSmtpusername(context.getInitParameter("smtpusername"));
		trialhelper.setSmtppassword(context.getInitParameter("smtppassword"));
		trialhelper.setSmtphost(context.getInitParameter("smtphost"));
		trialhelper.setSmtpport(context.getInitParameter("smtpport"));
	    return trialhelper.createTrialAccount(user,mailformat,subject);
	}

	@Override
	public PageResult getTrialAccounts(PageCriteria crt) {
		HttpServletRequest req = this.getThreadLocalRequest();
		TrialHelper trialhelper = new TrialHelper(req,context,servletconfig,connectionUrl,driverClassName,username,password);
		PageResult list =  trialhelper.getTrialAccounts(crt);
		return list;
	}

	@Override
	public PageResult getSortTrialAccounts(PageCriteria crt, String columname,
			String mode) {
		HttpServletRequest req = this.getThreadLocalRequest();
		TrialHelper trialhelper = new TrialHelper(req,context,servletconfig,connectionUrl,driverClassName,username,password);
		PageResult list =  trialhelper.getSortTrialAccount(crt,columname,mode);
		return list;
	}

	@Override
	public PageResult getSearchData(PageCriteria crt, String columname,	String searchString) {
		HttpServletRequest req = this.getThreadLocalRequest();
		TrialHelper trialhelper = new TrialHelper(req,context,servletconfig,connectionUrl,driverClassName,username,password);
		PageResult list =  trialhelper.getSearchData(crt,columname,searchString);
		return list;
	}

	@Override
	public void updateTrialDuration(ArrayList<TrialAccount> trialaccountlist) {
		HttpServletRequest req = this.getThreadLocalRequest();
		TrialHelper trialhelper = new TrialHelper(req,context,servletconfig,connectionUrl,driverClassName,username,password);
		trialhelper.updateTrialAccountDuration(trialaccountlist);
	}

	@Override
	public void deleteTrialAccount(TrialAccount trialaccount) {
		HttpServletRequest req = this.getThreadLocalRequest();
		TrialHelper trialhelper = new TrialHelper(req,context,servletconfig,connectionUrl,driverClassName,username,password);
		trialhelper.deleteTrialAccount(trialaccount);
	}

	@Override
	public void reinitiateTrialAccount(TrialAccount trialaccount) {
		HttpServletRequest req = this.getThreadLocalRequest();
		TrialHelper trialhelper = new TrialHelper(req,context,servletconfig,connectionUrl,driverClassName,username,password);
		trialhelper.setSmtpusername(context.getInitParameter("smtpusername"));
		trialhelper.setSmtppassword(context.getInitParameter("smtppassword"));
		trialhelper.setSmtphost(context.getInitParameter("smtphost"));
		trialhelper.setSmtpport(context.getInitParameter("smtpport"));
		trialhelper.reinitiateTrialAccount(trialaccount);
	}
}