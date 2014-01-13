package com.lighthouse.feed.server;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.lighthouse.feed.client.domain.FeedNewsItem;
import com.newscenter.client.tags.TagItem;

/**
 * 
 * @author sachin.s@ensarm.com
 *
 */
public class NewsWatchFeedProcessor implements FeedProcessor{

	public Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Override
	public String getContentToBeParsed(String url) {
		String result = null;
		try{
			  URL fURL=new URL(url); 
			  URLConnection connection=  fURL.openConnection();
		      Scanner scanner = new Scanner(connection.getInputStream());
		      scanner.useDelimiter("\\Z");
		      result = scanner.next();
		      
		      return result;
		}catch (Exception e) {
			logger.log(Level.INFO, "[--NewsWatchFeedProcessor--] Failed in method getContentToBeParsed()..",e);
		}
		return null;
	}

	@Override
	public ArrayList<FeedNewsItem> parseContent(String feedContent, String feedName ) {
		logger.log(Level.INFO, "[--NewsWatchFeedProcessor--] In method parseContent()...");
		ArrayList<FeedNewsItem> newsItemList = new ArrayList<FeedNewsItem>();
		try {
	
			JSONArray json = (JSONArray) JSONSerializer.toJSON(feedContent);
			
			for (Object obj : json) {

				Integer id = null;
				//String title = "";
				String date = "0000-00-00";
				//String summary = "";
				//String media = "";
				String pdfUrl = "";
				String thumbnail = "";
				//String text = "";
				//String url = "";
				Integer isReport = 0;
				String category = "";
				String tags = "";

				ArrayList<TagItem> tagItemList=new ArrayList<TagItem>();
				
				JSONObject feedItem = (JSONObject) obj;
				
				FeedNewsItem newsItems = new FeedNewsItem();
				newsItems.setFeedContent(feedItem.toString());
				
				if (feedItem.containsKey("headline")) {
					//title = feedItem.getString("headline");
					String title = new String(feedItem.getString("headline").trim().getBytes("utf-8"), "utf-8");
					newsItems.setNewsTitle(title);
				}

				if (feedItem.containsKey("category")) {
					category = feedItem.getString("category");
					if(!category.equals("")){
						String categoryArray[]=null;
						if(category.contains(",")){
							categoryArray=category.split(",");
						}else{
							categoryArray=new String[1];
							categoryArray[0]=category;
						}
						for(String catName:categoryArray){
							
							if(catName.indexOf(" ")==0)
								catName=catName.substring(1);
							
							TagItem tagItem = new TagItem();
							tagItem.setTagName(catName);
							tagItemList.add(tagItem);
							
						}
					}
				}

				if (feedItem.containsKey("tags")) {
					tags = feedItem.getString("tags");
					if(!tags.equals("")){
						String tagArray[]=null;
						if(tags.contains(",")){
							tagArray=tags.split(",");
						}else{
							tagArray=new String[1];
							tagArray[0]=tags;
						}
						for(String tagName:tagArray){
							
							if(tagName.indexOf(" ")==0)
								tagName=tagName.substring(1);
							
							TagItem tagItem = new TagItem();
							tagItem.setTagName(tagName);
							tagItemList.add(tagItem);
							
						}
					}
				}

				if (feedItem.containsKey("id")) {
					id = feedItem.getInt("id");					
					newsItems.setFeedId(id);
				}
				if (feedItem.containsKey("date_published")) {
					date = feedItem.getString("date_published");
					newsItems.setNewsDate(date);
				}

				if (feedItem.containsKey("summary")) {
					//summary = feedItem.getString("summary");
					String summary = new String(feedItem.getString("summary").trim().getBytes("utf-8"), "utf-8");
					newsItems.setAbstractNews(summary);
				}
				if (feedItem.containsKey("media")) {
					//media = feedItem.getString("media");
					String media = new String(feedItem.getString("media").trim().getBytes("utf-8"), "utf-8");
					newsItems.setNewsSource(media);
				}
				if (feedItem.containsKey("pdf_file")) {
					pdfUrl = feedItem.getString("pdf_file");
					if (!pdfUrl.equalsIgnoreCase("")) {
						isReport = 1;
						newsItems.setReportUrl(pdfUrl);
						newsItems.setIsReportItem(isReport);
					}

				}
				if (feedItem.containsKey("thumbnail")) {
					thumbnail = feedItem.getString("thumbnail");
					if(!thumbnail.equals(""))
						newsItems.setImageUrl(thumbnail);
				}
				if (feedItem.containsKey("textversion")) {
					//text = feedItem.getString("textversion");
					String text = new String(feedItem.getString("textversion").trim().getBytes("utf-8"), "utf-8");
					newsItems.setNewsContent(text);
				}
				if (feedItem.containsKey("url")) {
					//url = feedItem.getString("url");
					String url = new String(feedItem.getString("url").trim().getBytes("utf-8"), "utf-8");
					newsItems.setUrl(url);
				}
				
				if(tagItemList.size()>0)
					newsItems.setAssociatedTagList(tagItemList);
				
				newsItemList.add(newsItems);
			}

		} catch (Exception e) {
			logger.log(Level.INFO, "[--NewsWatchFeedProcessor--] Exception in feed parsing.... \n", e);
			throw new JSONException();
			
		}
		logger.log(Level.INFO, "[--NewsWatchFeedProcessor--] [--Exiting parse Feed method.--]");
		return newsItemList;
		
	}

}
