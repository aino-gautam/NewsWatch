package com.lighthouse.feed.server;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
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
/**
 * @author Dhananjay & Sachin
 *
 */
public class FactivaFeedProcessor implements FeedProcessor{
	
	public Logger logger = Logger.getLogger(this.getClass().getName());
	public static String FEEDFILENAME;

	@Override
	public String getContentToBeParsed(String url) {
	
		return null;
	}

	@Override
	public ArrayList<FeedNewsItem> parseContent(String feedContent, String feedName ) {
		try {
			
			if(FEEDFILENAME==null)
				return null;
				ArrayList<FeedNewsItem> newsItemsList=new ArrayList<FeedNewsItem>();
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document dom = db.parse(FEEDFILENAME);
			//	Document dom=db.parse(new ByteArrayInputStream(feedContent.getBytes()));
				dom.getDocumentElement().normalize();

				NodeList listOfNodes = dom.getElementsByTagName("DOC");
				for (int s = 0; s < listOfNodes.getLength(); s++) {
					FeedNewsItem newsItem=new FeedNewsItem();
					
					
					ArrayList<TagItem> tagItemList=new ArrayList<TagItem>();

					String dateString =null;
					//String title =null;
					String content =null;
					String docType =null;
					String summary =null;
					//String source =null;
					String feedText =null;
					String tagText=null;
					String langTag=null;
					String feedId=null;
					
					String xmlVersion = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
									
					StringWriter sw = new StringWriter();
					Transformer serializer = TransformerFactory.newInstance().newTransformer();
					serializer.transform(new DOMSource(listOfNodes.item(s)),new StreamResult(sw));
					feedText = sw.toString();
					
					if (feedText.contains(xmlVersion)) {
						feedText = feedText.replace(xmlVersion, "");
					}

					newsItem.setFeedContent(feedText);
					
					Node firstNode = listOfNodes.item(s);
					if (firstNode.getNodeType() == Node.ELEMENT_NODE) {

						Element firstElement = (Element) firstNode;

						NodeList dateList = firstElement
								.getElementsByTagName("DATE");
						Element dateElement = (Element) dateList.item(0);
						if (dateElement != null) {
							NodeList dateText = dateElement.getChildNodes();
							dateString = dateText.item(0).getNodeValue().trim();
							
						String	formattedDate = dateString.substring(0, 4) + "-"
							+ dateString.substring(4, 6) + "-"
							+ dateString.substring(6, 8);
					
						newsItem.setNewsDate(formattedDate);
						}

						NodeList titleList = firstElement
								.getElementsByTagName("TI");
						Element titleElement = (Element) titleList.item(0);

						if (titleElement != null) {
							NodeList titleText = titleElement.getChildNodes();
						//	title = titleText.item(0).getNodeValue().trim();
							String title = new String(titleText.item(0).getNodeValue().trim().getBytes("utf-8"), "utf-8");
							newsItem.setNewsTitle(title);
						}
						
						
						NodeList idNodeList = firstElement.getElementsByTagName("DOCN");
						Element idElement = (Element) idNodeList.item(0);

						if (idElement != null) {
							NodeList docIdText = idElement.getChildNodes();
							feedId = docIdText.item(0).getNodeValue().trim();
							newsItem.setFeedId(Long.parseLong(feedId));
						}

						NodeList contentList = firstElement
								.getElementsByTagName("TEXT");
						Element contentElement = (Element) contentList.item(0);
						if (contentElement != null) {
							NodeList contentText = contentElement.getChildNodes();
							//content = contentText.item(0).getNodeValue().trim();
							content = new String(contentText.item(0).getNodeValue().trim().getBytes("utf-8"), "utf-8");
							newsItem.setNewsContent(content);
						}

						NodeList docTypeList = firstElement
								.getElementsByTagName("DOCT");
						Element docTypeElement = (Element) docTypeList.item(0);
						if (docTypeElement != null) {
							NodeList docTypeText = docTypeElement.getChildNodes();
							docType = docTypeText.item(0).getNodeValue().trim();
							//
						}

						NodeList abstractList = firstElement
								.getElementsByTagName("NSD");
						Element abstractElement = (Element) abstractList.item(0);
						if (abstractElement != null) {
							NodeList abstractText = abstractElement.getChildNodes();
							summary = abstractText.item(0).getNodeValue().trim();
							newsItem.setAbstractNews(content);
						}

						NodeList sourceList = firstElement
								.getElementsByTagName("SO");
						Element sourceElement = (Element) sourceList.item(0);
						if (sourceElement != null) {
							NodeList sourceText = sourceElement.getChildNodes();
							//source = sourceText.item(0).getNodeValue().trim();
							String source = new String(sourceText.item(0).getNodeValue().trim().getBytes("utf-8"), "utf-8");
							newsItem.setNewsSource(source);
						}
						
						NodeList tagNodes = firstElement.getElementsByTagName("RED");
						Element tagElement = (Element) tagNodes.item(0);
						if (tagElement != null) {
							NodeList tagItemNode = tagElement.getChildNodes();
							tagText = tagItemNode.item(0).getNodeValue().trim();
							if(tagText.contains("\n\n")){
								String tagArray[]=tagText.split("\n\n");
								for(String tagName:tagArray){
									
									if(tagName.indexOf(" ")==0)
										tagName=tagName.substring(1);
									
									TagItem tagItem = new TagItem();
									tagItem.setTagName(tagName);
									tagItemList.add(tagItem);
									
								}
							}else{
								TagItem tagItem = new TagItem();
								tagItem.setTagName(tagText);
								tagItemList.add(tagItem);
							}
							
						}
						
						NodeList langtagNodes = firstElement.getElementsByTagName("LA");
						Element langTagElement = (Element) langtagNodes.item(0);
						if (langTagElement != null) {
							NodeList tagItemNode = langTagElement.getChildNodes();
							langTag = tagItemNode.item(0).getNodeValue().trim();
							TagItem tagItem=new TagItem();
							tagItem.setTagName(langTag);
							tagItemList.add(tagItem);
						}

					}
					if(tagItemList.size()>0)
						newsItem.setAssociatedTagList(tagItemList);
					
					newsItemsList.add(newsItem);
				}
				return  newsItemsList;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}

}
