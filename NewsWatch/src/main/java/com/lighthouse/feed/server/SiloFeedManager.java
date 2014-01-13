package com.lighthouse.feed.server;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lighthouse.feed.client.domain.Feed;
import com.lighthouse.feed.client.domain.SiloFeed;
/**
 * 
 * @author sachin.s@ensarm.com
 *
 */
public class SiloFeedManager {
	
	public Logger logger = Logger.getLogger(this.getClass().getName());
	
	private SiloFeed siloFeed;
		
	public SiloFeed getSiloFeed() {
		return siloFeed;
	}

	public void setSiloFeed(SiloFeed siloFeed) {
		this.siloFeed = siloFeed;
	}
	
	public SiloFeedManager(SiloFeed siloFeed){
		this.siloFeed=siloFeed;		
	}

	public void initFeedSync(){
		logger.log(Level.INFO,"[----- In SiloFeedManager initFeedSync() started-----]");
		try{
			ArrayList<Feed> feedList=siloFeed.getFeedUrlList();
			if(siloFeed.isAutoSyncEnabled()){
				for(Feed feed:feedList){
					FeedManagerThread managerThread=new FeedManagerThread(feed,siloFeed.getNcid(),siloFeed.getFeedUserId(),siloFeed.getSiloPollFrequency());
					Thread thread=new Thread(managerThread);
					thread.start();
				}
			}
			logger.log(Level.INFO,"[----- In SiloFeedManager initFeedSync() completed-----]");
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed in method initFeedSync()..",e);
		}
	}
	
}
