package com.lighthouse.feed.server;

/**
 * author sachin@ensarm.com
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.lighthouse.feed.client.domain.Feed;
public class FeedManager {
	
	public static String CONFIGFILENAME;
	
	public Logger logger = Logger.getLogger(FeedManager.class.getName());
	 private static final String END_OF_INPUT = "\\Z";
	 public String getPageContent(String url) {
		    String result = null;
		    URLConnection connection = null;
		    try {
		      URL fURL=new URL(url); 
		      connection =  fURL.openConnection();
		      Scanner scanner = new Scanner(connection.getInputStream());
		      scanner.useDelimiter(END_OF_INPUT);
		      result = scanner.next();
		    }
		    catch ( IOException ex ) {
		    	ex.printStackTrace();
		    	return null;
		    }
		    return result;
		  }
	 
	 
	 	public HashMap<String, Long> readConfigFileVal(String fileName){
			HashMap<String, Long> map= new HashMap<String, Long>();
	 		 Properties configFile = new Properties();
				try {
					 BufferedReader reader = new BufferedReader(new FileReader(fileName));
					configFile.load(reader);
					Set<Object> KeyList = configFile.keySet();
					for(Object object:KeyList){
						String key=(String) object;
						String property=configFile.getProperty(key);
						String []feedparam=property.split("[$$]");
						map.put(feedparam[2], Long.valueOf(feedparam[0]));
					}
					
				}catch(Exception e){
				    e.printStackTrace();
				    return  null;
				}
				return map;
		}
	 	
	 	public HashMap<String, Long> getIndustryConfig(int ncid){
			HashMap<String, Long> map = new HashMap<String, Long>();
			try {
				Element catalystElement = getCatalystElement(ncid);
				String pollFreq = getTagValueFromXml("pollfreq", catalystElement);
				NodeList feedElementList = catalystElement.getElementsByTagName("feed");
			
				for (int j = 0; j < feedElementList.getLength(); j++) {
					Element feedElement = (Element) feedElementList.item(j);
					String url=getTagValueFromXml("url", feedElement);
				//	String feedName=getTagValueFromXml("feedname", feedElement);
					map.put(url, Long.parseLong(pollFreq));
				}
				return map;
			} catch (Exception ex) {
				logger.log(Level.INFO, "Failed to parse Config file ..", ex);
			}
			return null;
		}
	 	
	 	
	 	public ArrayList<Feed> getFeedUrlListForCatalyst(int ncid){
	 		ArrayList<Feed> siloFeedList = new ArrayList<Feed>();
			try {
				Element catalystElement = getCatalystElement(ncid);
				//String pollFreq = getTagValueFromXml("pollfreq", catalystElement);
				NodeList feedElementList = catalystElement.getElementsByTagName("feed");
			
				for (int j = 0; j < feedElementList.getLength(); j++) {
					Feed feed = new Feed();
					Element feedElement = (Element) feedElementList.item(j);
					String url=getTagValueFromXml("url", feedElement);
					String feedName=getTagValueFromXml("feedname", feedElement);
					String feedDesc = getTagValueFromXml("feeddescription", feedElement);
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
	
	 	public NodeList getCatalystElementList(){
	 		try{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder(); 
				Document dom = db.parse(CONFIGFILENAME);
				dom.getDocumentElement().normalize(); 
				NodeList listOfCatalyst = dom.getElementsByTagName("catalyst");
				return listOfCatalyst;
			}catch (Exception e) {
	 			logger.log(Level.INFO, "Failed to read tag value ..",e);
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
	 	
	 	public String getFeedName(String feedUrl,int ncid){
	 		String feedSourceName=null;
	 		try{
	 			Element catalystElement = getCatalystElement(ncid);
				NodeList feedElementList = catalystElement.getElementsByTagName("feed");
			
				for (int j = 0; j < feedElementList.getLength(); j++) {
					Element feedElement = (Element) feedElementList.item(j);
					String url=getTagValueFromXml("url", feedElement);
					if(url.equals(feedUrl)){
						feedSourceName=getTagValueFromXml("feedname", feedElement);
						return feedSourceName;
					}
					
				}
	 		}catch (Exception e) {
	 			logger.log(Level.INFO, "Failed to read feed name ..",e);
			}
			return feedSourceName;
	 	}
	 	
	 	public String getFeedDescription(String feedUrl,int ncid){
	 		String feedDescription=null;
	 		try{
	 			Element catalystElement = getCatalystElement(ncid);
				NodeList feedElementList = catalystElement.getElementsByTagName("feed");
			
				for (int j = 0; j < feedElementList.getLength(); j++) {
					Element feedElement = (Element) feedElementList.item(j);
					String url=getTagValueFromXml("url", feedElement);
					if(url.equals(feedUrl)){
						feedDescription=getTagValueFromXml("feeddescription", feedElement);
						return feedDescription;
					}
					
				}
	 		}catch (Exception e) {
	 			logger.log(Level.INFO, "Failed to read feed Description ..",e);
			}
			return feedDescription;
	 	}
	 	
	 	
	 	public boolean isAutoSyncEnabled(Integer ncid){
	 		try{
	 			Element catalystElement = getCatalystElement(ncid);
	 			String autoSyncVal=getTagValueFromXml("autosync", catalystElement);
	 			if(autoSyncVal.equals("on"))
	 				return true;
	 		}catch (Exception e) {
	 			logger.log(Level.INFO, "Failed to read autoSync mode..",e);
			}
			return false;
	 	}
}
