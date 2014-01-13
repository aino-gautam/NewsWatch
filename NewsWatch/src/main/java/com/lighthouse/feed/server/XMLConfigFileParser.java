package com.lighthouse.feed.server;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lighthouse.feed.client.domain.Feed;
import com.lighthouse.feed.client.domain.SiloFeed;

/**
 * 
 * @author sachin.s@ensarm.com
 *
 */
public class XMLConfigFileParser {

	public Logger logger = Logger.getLogger(this.getClass().getName());
	public static String CONFIGFILENAME;
	
	public ArrayList<SiloFeed> getSiloFeedList(){
			Element catalystElement=null;
	 		Document dom;
	 		ArrayList<SiloFeed> siloFeedList=new ArrayList<SiloFeed>();
	 		
			try{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder(); 
				dom = db.parse(CONFIGFILENAME);
				dom.getDocumentElement().normalize(); 
				logger.log(Level.INFO,"Root element of the doc is " + dom.getDocumentElement().getNodeName());
				NodeList listOfSites = dom.getElementsByTagName("catalyst");
				logger.log(Level.INFO,"Total no of catalyst : " + listOfSites.getLength());
				for(int i=0;i<listOfSites.getLength();i++){
					Node node=listOfSites.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						catalystElement = (Element) node;
						SiloFeed siloFeed=new SiloFeed();
						
						String id=getTagValueFromXml("id", catalystElement);
						siloFeed.setNcid(Integer.parseInt(id));
						
						String pollFreq = getTagValueFromXml("pollfreq", catalystElement);
						siloFeed.setSiloPollFrequency(Long.parseLong(pollFreq));
						
						String pTag=getTagValueFromXml("ptag", catalystElement);
						siloFeed.setPtag(pTag);
						
						String eTag=getTagValueFromXml("etag", catalystElement);
						siloFeed.setEtag(eTag);
						
						String autoSync=getTagValueFromXml("autosync", catalystElement);
						if(autoSync.equals("on"))
							siloFeed.setAutoSyncEnabled(true);
						else
							siloFeed.setAutoSyncEnabled(false);
						
						String ncName=getTagValueFromXml("name", catalystElement);
						siloFeed.setNcName(ncName);
						
						String userId=getTagValueFromXml("feedUserId", catalystElement);
						siloFeed.setFeedUserId(Integer.parseInt(userId));
						
						ArrayList<Feed> feedList=getFeedUrlListForCatalyst(Integer.parseInt(id));
						
						siloFeed.setFeedUrlList(feedList);
						
						siloFeedList.add(siloFeed);
					}
				}
				
				return siloFeedList;
	 					
		}catch (Exception e) {
			logger.log(Level.INFO, "Failed to parse silo feed list..",e);
		}
		return null;
	}
	
	
 	public ArrayList<Feed> getFeedUrlListForCatalyst(int ncid){
 		ArrayList<Feed> siloFeedList = new ArrayList<Feed>();
		try {
			Element catalystElement = getCatalystElement(ncid);
			NodeList feedElementList = catalystElement.getElementsByTagName("feed");
		
			for (int j = 0; j < feedElementList.getLength(); j++) {
				Feed feed = new Feed();
				Element feedElement = (Element) feedElementList.item(j);
				String url=getTagValueFromXml("url", feedElement);
				String feedName=getTagValueFromXml("feedname", feedElement);
				String feedDesc = getTagValueFromXml("feeddescription", feedElement);
				String feedProcessor=getTagValueFromXml("feedprocessor", feedElement);
				String feedPTag=getTagValueFromXml("feedPTag", feedElement);
				feed.setFeedPTag(feedPTag);
				feed.setFeedProcessor(feedProcessor);
				feed.setFeedDescription(feedDesc);
				feed.setFeedName(feedName);
				feed.setFeedUrl(url);
				siloFeedList.add(feed);
			}
			return siloFeedList;
		} catch (Exception ex) {
			logger.log(Level.INFO, "Failed to parse Config file ..", ex);
		}
		return null;
	}
	
	public SiloFeed getSiloFeed(String feedName){
		Element catalystElement=null;
 		Document dom;
 	
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			dom = db.parse(CONFIGFILENAME);
			dom.getDocumentElement().normalize(); 
			NodeList listOfSites = dom.getElementsByTagName("catalyst");
			for(int i=0;i<listOfSites.getLength();i++){
				Node node=listOfSites.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					catalystElement = (Element) node;
					SiloFeed siloFeed=new SiloFeed();
					
					String id=getTagValueFromXml("id", catalystElement);
					siloFeed.setNcid(Integer.parseInt(id));
					
					ArrayList<Feed> feedList=getFeedUrlListForCatalyst(Integer.parseInt(id));
					for(Feed feed:feedList){
						if(feed.getFeedName().equals(feedName)){
							
							String pollFreq = getTagValueFromXml("pollfreq", catalystElement);
							siloFeed.setSiloPollFrequency(Long.parseLong(pollFreq));
							
							String pTag=getTagValueFromXml("ptag", catalystElement);
							siloFeed.setPtag(pTag);
							
							String eTag=getTagValueFromXml("etag", catalystElement);
							siloFeed.setEtag(eTag);
							
							String autoSync=getTagValueFromXml("autosync", catalystElement);
							if(autoSync.equals("on"))
								siloFeed.setAutoSyncEnabled(true);
							else
								siloFeed.setAutoSyncEnabled(false);
							
							String ncName=getTagValueFromXml("name", catalystElement);
							siloFeed.setNcName(ncName);
							
							String userId=getTagValueFromXml("feedUserId", catalystElement);
							siloFeed.setFeedUserId(Integer.parseInt(userId));
							
							ArrayList<Feed> feedItems=new ArrayList<Feed>();
							feedItems.add(feed);
							siloFeed.setFeedUrlList(feedItems);
							
							return siloFeed;
						}
					}
				}
			}
		
			
			
		} catch (Exception ex) {
			logger.log(Level.INFO, "Failed to parse in method getSiloFeed() ..", ex);
		}
		return null;
	}

	public String getTagValueFromXml(String xmlTagName,Element element){
 		String tagValue=null;
 		try{
 			NodeList nodeList = element.getElementsByTagName(xmlTagName);
			Element subElement = (Element) nodeList.item(0);
			NodeList childList = subElement.getChildNodes();
			tagValue=childList.item(0).getNodeValue().trim();
		}catch (Exception e) {
 			logger.log(Level.INFO, "Failed to read tag value ..",e);
		}
		return tagValue;
 	}
	
	
	public Element getCatalystElement(int ncid){
 		Element catalystElement=null;
 		Document dom;
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			dom = db.parse(CONFIGFILENAME);
			dom.getDocumentElement().normalize(); 
			logger.log(Level.INFO,"Root element of the doc is " + dom.getDocumentElement().getNodeName());
			NodeList listOfSites = dom.getElementsByTagName("catalyst");
			logger.log(Level.INFO,"Total no of catalyst : " + listOfSites.getLength());
			for(int i=0;i<listOfSites.getLength();i++){
				Node node=listOfSites.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					catalystElement = (Element) node;
					String id=getTagValueFromXml("id", catalystElement);
					int industryId=Integer.parseInt(id);
					if(industryId==ncid){
						return catalystElement;
					}
				}
			}
 			
		}catch (Exception e) {
 			logger.log(Level.INFO, "Failed to read tag value ..",e);
		}
		return null;
 	}

 }
