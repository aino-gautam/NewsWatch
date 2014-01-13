package com.lighthouse.feed.server;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lighthouse.feed.client.domain.Feed;
import com.lighthouse.feed.client.domain.FeedNewsItem;

/**
 * @author sachin.s@ensarm.com
 *
 */
public class FeedManagerThread implements Runnable{

	public Logger logger = Logger.getLogger(this.getClass().getName());

	private Feed feed;
	private int ncid;
	private int userId;
	private long pollFreq;
	
	public FeedManagerThread(Feed feed,int ncid,int userId,long pollFreq){
		this.feed=feed;
		this.ncid=ncid;
		this.userId=userId;
		this.pollFreq=pollFreq;
	}
	
	@Override
	public void run() {
		try{
			while(true){
				logger.log(Level.INFO, "Running thread for  feedname: "+feed.getFeedName()+" and URL: "+feed.getFeedUrl());
				
				String processor=feed.getFeedProcessor();
				FeedProcessor feedProcessor=(FeedProcessor) Class.forName(processor).newInstance();
				String contentToBeParse=feedProcessor.getContentToBeParsed(feed.getFeedUrl());
				logger.log(Level.INFO, "Before parseContent() for  feedname: "+feed.getFeedName());
				ArrayList<FeedNewsItem> newsItems=feedProcessor.parseContent(contentToBeParse,feed.getFeedName());
				if(newsItems!=null){
					logger.log(Level.INFO, "News fetched for feedname: "+feed.getFeedName()+" : " + newsItems.size());
					if(newsItems.size()>0){
						FeedHelper feedHelper=new FeedHelper();
						feedHelper.saveNewsFeed(newsItems, ncid, userId, feed);
					}
				}
				logger.log(Level.INFO, "Completed, before thread sleeping feedname: "+feed.getFeedName()+" and URL: "+feed.getFeedUrl());
				Thread.sleep(pollFreq);
			}
						
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed in [--FeedManagerThread--] run() feedname: "+feed.getFeedName()+" and URL: "+feed.getFeedUrl(),e);
		}
	}
}
