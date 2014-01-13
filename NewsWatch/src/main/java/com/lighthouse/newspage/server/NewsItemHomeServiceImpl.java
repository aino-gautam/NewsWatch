package com.lighthouse.newspage.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.lighthouse.newspage.client.domain.CommentedNewsItem;
import com.lighthouse.newspage.client.service.NewsItemHomeService;
import com.lighthouse.newspage.server.helper.NewsItemHomeHelper;

/**
 * 
 * @author kiran@ensarm.com Imple class for NewsItemHome
 * 
 */
public class NewsItemHomeServiceImpl extends RemoteServiceServlet implements
		NewsItemHomeService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4477662159168998456L;
	Logger logger = Logger.getLogger(NewsItemHomeServiceImpl.class.getName());
	ServletContext context =null;
	
	@Override
	public void init() throws ServletException {
		context =getServletContext();
	}
	/**
	 * This Method for loading all the newsItem related content.
	 */
	@Override
	public CommentedNewsItem loadNewsItemContent(int newsId) {
		logger.log(Level.INFO,"In loadNewsItemContent method from NewsItemHomeServiceImpl");
		CommentedNewsItem commentedNewsItem = new CommentedNewsItem();
		
		
		try {
			String logoImagePath = (String) context.getAttribute("siloLogo");
		
			NewsItemHomeHelper itemHomeHelper = new NewsItemHomeHelper();
			ArrayList<CommentedNewsItem> commentedNewsItems = itemHomeHelper
					.getNewsItemContent(newsId);
			Iterator iterator = commentedNewsItems.iterator();
			while (iterator.hasNext()) {

				commentedNewsItem = (CommentedNewsItem) iterator.next();	
			}
			commentedNewsItem.setLogoImagePath(logoImagePath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return commentedNewsItem;
	}

	

	

}
