package com.lighthouse.feed.server;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lighthouse.feed.client.domain.FeedNewsItem;
import com.newscenter.client.tags.TagItem;

public class NZRssFeedProcessor implements FeedProcessor{

	public Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public String getContentToBeParsed(String url) {
		logger.log(Level.INFO, "[--NZRssFeedProcessor--] getContentToBeParsed() initiated for URL: "+url);
		String result = null;
		try{
			  URL fURL=new URL(url); 
			  URLConnection connection=  fURL.openConnection();
		      Scanner scanner = new Scanner(connection.getInputStream());
		      scanner.useDelimiter("\\Z");
		      result = scanner.next();
		      logger.log(Level.INFO, "[--NZRssFeedProcessor--] getContentToBeParsed() completed for URL: "+url);
		      return result;
		}catch (Exception e) {
			logger.log(Level.INFO, "[--NZRssFeedProcessor--] Failed in method getContentToBeParsed()..",e);
		}
		return null;
	}

	@Override
	public ArrayList<FeedNewsItem> parseContent(String feedContent, String feedName ){
		logger.log(Level.INFO, "[--NZRssFeedProcessor--] parseContent() initiated for feedname: "+feedName);
		ArrayList<FeedNewsItem> newsItemsList=new ArrayList<FeedNewsItem>();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try{
			if(feedContent!=null){
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder(); 
				Document dom=db.parse(new ByteArrayInputStream(feedContent.getBytes()));
				dom.getDocumentElement().normalize(); 
				NodeList listOfNodes = dom.getElementsByTagName("item");
				String pubDate=null;
				NodeList pubNodeList = dom.getElementsByTagName("pubDate");
				Element pubDateElement = (Element) pubNodeList.item(0);
				if (pubDateElement != null) {
					NodeList dateText = pubDateElement.getChildNodes();
					String pubDateText = dateText.item(0).getNodeValue().trim();
					Date date=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").parse(pubDateText);
					pubDate = fmt.format(date);
				}
				
				TagItem tagItem=new TagItem();
				if(feedName.contains("::")){
					String arr[]=feedName.split("::");
					feedName=arr[0];
				}
				tagItem.setTagName(feedName);
				
				String xmlVersion = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
				for (int i = 0; i < listOfNodes.getLength(); i++) {
					
					
					String dateString =null;
					String feedText =null;
					
					FeedNewsItem newsItem=new FeedNewsItem();
					ArrayList<TagItem> tagItemList=new ArrayList<TagItem>();
					
					StringWriter sw = new StringWriter();
					Transformer serializer = TransformerFactory.newInstance().newTransformer();
					serializer.transform(new DOMSource(listOfNodes.item(i)),new StreamResult(sw));
					feedText = sw.toString();
					
					if (feedText.contains(xmlVersion)) {
						feedText = feedText.replace(xmlVersion, "");
					}
	
					newsItem.setFeedContent(feedText);
					tagItemList.add(tagItem);
					
					Node itemNode = listOfNodes.item(i);
					if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
	
						Element itemElement = (Element) itemNode;
	
						NodeList dateList = itemElement.getElementsByTagName("pubDate");
						Element dateElement = (Element) dateList.item(0);
						if (dateElement != null) {
							NodeList dateText = dateElement.getChildNodes();
							dateString = dateText.item(0).getNodeValue().trim();
							Date date=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").parse(dateString);
							String formattedDate = fmt.format(date);
							newsItem.setNewsDate(formattedDate);
						}else{
							if(pubDate!=null)
								newsItem.setNewsDate(pubDate);
						}
	
						NodeList titleList = itemElement
								.getElementsByTagName("title");
						Element titleElement = (Element) titleList.item(0);
						String title = null;
						if (titleElement != null) {
							NodeList titleText = titleElement.getChildNodes();
							title = new String(titleText.item(0).getNodeValue().trim().getBytes("utf-8"), "utf-8");
							newsItem.setNewsTitle(title);
						}
						
						NodeList abstractList = itemElement
						.getElementsByTagName("description");
						Element abstractElement = (Element) abstractList.item(0);
						if (abstractElement != null) {
							NodeList abstractText = abstractElement.getChildNodes();
							if(abstractText.item(0)!=null){
								
								String newsAbstract = new String(abstractText.item(0).getNodeValue().trim().getBytes("utf-8"), "utf-8");
								
								if(newsAbstract.contains(title)){
									String title1 = newsAbstract.substring(newsAbstract.indexOf("Title:"),newsAbstract.indexOf("<br/>")+5);
								  			newsAbstract = newsAbstract.replace(title1, "");
											newsAbstract = newsAbstract.replace("Title:", "");
								}
								
								if(newsAbstract.contains("Source:")){
								
									if(newsAbstract.contains("Document Type:")){
										newsAbstract=newsAbstract.substring(0, newsAbstract.indexOf("Document Type:"));
									}
									
									String sourceOne = newsAbstract.substring(newsAbstract.indexOf("Source:"));
									String newSource=sourceOne.substring(8,sourceOne.indexOf(","));
									newsItem.setNewsSource(newSource);
								}
								 newsItem.setAbstractNews(newsAbstract);
							}
						}
						
						/*NodeList langtagNodes = itemElement.getElementsByTagName("category");
						Element langTagElement = (Element) langtagNodes.item(0);
						if (langTagElement != null) {
							NodeList tagItemNode = langTagElement.getChildNodes();
							category = tagItemNode.item(0).getNodeValue().trim();
							TagItem tagItem=new TagItem();
							tagItem.setTagName(category);
							tagItemList.add(tagItem);
						}*/
						
						NodeList urlLinkNodeList = itemElement
						.getElementsByTagName("link");
						Element linkElement = (Element) urlLinkNodeList.item(0);
						if (linkElement != null) {
							NodeList urlText = linkElement.getChildNodes();
							
							String link = new String(urlText.item(0).getNodeValue().trim().getBytes("utf-8"), "utf-8");
							newsItem.setUrl(link);
						}
						
					}
					if(tagItemList.size()>0)
						newsItem.setAssociatedTagList(tagItemList);
					
					newsItemsList.add(newsItem);
				}
			}
		
		}catch (Exception e) {
			logger.log(Level.INFO, "[--NZRssFeedProcessor--] Failed in method parseContent()..",e);
		}
		logger.log(Level.INFO, "[--NZRssFeedProcessor--] parseContent() completed for feedname: "+feedName);
		return  newsItemsList;
	}

}
