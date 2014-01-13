package com.lighthouse.newsletter.server;

import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.lighthouse.newsletter.client.NewsletterConfigService;
import com.login.client.UserInformation;

public class NewsletterConfigImpl extends RemoteServiceServlet implements
		NewsletterConfigService {
	
	private static final long serialVersionUID = -6163927202487310879L;
	Logger logger = Logger.getLogger(NewsletterConfigImpl.class.getName());

	@Override
	public boolean saveNewsletterHeaderConfig(String xml) {
		LhNewsletterHelper helper = new LhNewsletterHelper();
		boolean saved = helper.saveNewsLetterHeaderConfig(getNewsCenterIdFromSession(),xml);
		helper.closeConnection();
		return saved;
	}

	private int getNewsCenterIdFromSession() {
		HttpServletRequest req = this.getThreadLocalRequest();
		HttpSession session = req.getSession(false);
		UserInformation userInformation = (UserInformation) session
				.getAttribute("userInfo");
		int newscenterId = userInformation.getUserSelectedIndustryID();
		return newscenterId;
	}

	@Override
	public String getNewsletterHeaderConfig() {
		LhNewsletterHelper helper = new LhNewsletterHelper();
		return helper.fetchHeaderConfiguration(getNewsCenterIdFromSession());
	}

	@Override
	public boolean saveNewsletterContentConfig(String xml) {
		LhNewsletterHelper helper = new LhNewsletterHelper();
		boolean saved = helper.saveNewsLetterContentConfig(getNewsCenterIdFromSession(),xml);
		helper.closeConnection();
		return saved;
	}

	@Override
	public String getNewsletterContentConfig() {
		LhNewsletterHelper helper = new LhNewsletterHelper();
		String contentConfig = helper.fetchContentConfiguration(getNewsCenterIdFromSession());
		helper.closeConnection();
		return contentConfig;
	}

	@Override
	public boolean saveNewsletterFooterConfig(String xml) {
		LhNewsletterHelper helper = new LhNewsletterHelper();
		boolean saved = helper.saveNewsLetterFooterConfig(getNewsCenterIdFromSession(),xml);
		helper.closeConnection();
		return saved;
	}

	@Override
	public String getNewsletterFooterConfig() {
		LhNewsletterHelper helper = new LhNewsletterHelper();
		String footerConfig = helper.fetchFooterConfiguration(getNewsCenterIdFromSession());
		helper.closeConnection();
		return footerConfig;
	}

	@Override
	public boolean saveNewsletterOutlineConfig(String xml) {
		LhNewsletterHelper helper = new LhNewsletterHelper();
		boolean saved = helper.saveNewsletterOutlineConfig(getNewsCenterIdFromSession(),xml);
		helper.closeConnection();
		return saved;
	}

	@Override
	public String getNewsletterOutlineConfig() {
		LhNewsletterHelper helper = new LhNewsletterHelper();
		String outlineConfig = helper.fetchOutlineConfiguration(getNewsCenterIdFromSession());
		helper.closeConnection();
		return outlineConfig;
	}

	@Override
	public String getImageUrl(int industryId, String type, boolean checkIfFileExists) {
		LhNewsletterHelper helper = new LhNewsletterHelper();
		ServletContext context = this.getServletContext();
		String imgurl = helper.getImageURL(industryId,type,(String)context.getAttribute("tomcatpath"), checkIfFileExists);
		helper.closeConnection();
		return imgurl;
	}
}
