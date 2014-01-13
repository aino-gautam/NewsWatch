package com.lighthouse.feed.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lighthouse.feed.client.domain.SiloFeed;
/**
 * 
 * @author sachin.s@ensarm.com
 *
 */
public class AutoFeedSyncServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Logger logger = Logger.getLogger(FeedServlet.class.getName());

	@Override
	public void init() throws ServletException {
		logger.log(Level.INFO,"[----- In AutoFeedSyncServlet init() -----]");
		try{
			XMLConfigFileParser parser=new XMLConfigFileParser();
			String siloConfigFileName = getServletContext().getRealPath("config/catalystConfig.xml");
			XMLConfigFileParser.CONFIGFILENAME=siloConfigFileName;
			ArrayList<SiloFeed> siloFeedList=parser.getSiloFeedList();
			for(SiloFeed siloFeed:siloFeedList){
				SiloFeedManager feedManager=new SiloFeedManager(siloFeed);
				feedManager.initFeedSync();
			}
			logger.log(Level.INFO,"[----- In AutoFeedSyncServlet init() completed -----]");
		}catch (Exception e) {
			logger.log(Level.INFO,"[  Failed to initialize AutoFeedSyncServlet  ]");
		}
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	
	}

	

}
