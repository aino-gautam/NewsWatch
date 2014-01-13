package com.lighthouse.feed.server;
/**
 * author sachin@ensarm.com
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.newscenter.client.news.NewsItems;

public class FeedThreadManager implements Runnable{

	private Thread thread=null;
	private String url=null; 
	private Long pollFreq=null;
	private Integer ncid=null;
	private Integer userId=null;
	private String feedName=null;
	
	Logger logger = Logger.getLogger(FeedThreadManager.class.getName());
	
	public void createThread( String url,Long time,Integer ncid,Integer userId,String feedName){
		try{
			 thread = new Thread(this);
			 thread.setName("Thread : "+url);
			 this.url=url;
			 this.pollFreq=time;
			 this.ncid=ncid;
			 this.userId=userId;
			 this.feedName=feedName;
			 thread.start();
		}
		catch (Exception e) {
			logger.log(Level.INFO,"Failed to initiate thread ....",e);
		}
		
	}


	public void run() {
		try{
			while(true){
			
				logger.log(Level.INFO,"Running "+thread.getName());
				FeedManager feedManager = new FeedManager();
				String feedContent=feedManager.getPageContent(url);
			/*	if(feedContent!=null){
					FeedHelper feedHelper= new FeedHelper();
					ArrayList<HashMap<String, String>> feedMapList=feedHelper.parseFeed(feedContent);
					
					if(feedMapList!=null){
						HashMap<Integer, ArrayList<NewsItems>> map=feedHelper.saveNewsFeed(feedMapList, ncid,url,userId,feedName);
						for(int isSaved:map.keySet()){
											
						//	if(isSaved==-1)
						//		thread.stop();
							
						thread.sleep(pollFreq);
						}
					}
				//	else
					//	thread.stop();
			}*/
				//else
			//	thread.stop();
			}
		}
		
		catch(Exception ex){
			logger.log(Level.INFO,"Exception while running AutoSync .....",ex);
		//	thread.stop();
		}
	}

}
