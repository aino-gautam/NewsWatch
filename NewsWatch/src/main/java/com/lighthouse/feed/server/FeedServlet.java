package com.lighthouse.feed.server;
/**
 * author sachin@ensarm.com
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONException;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.login.client.UserInformation;
import com.newscenter.client.news.NewsItems;

public class FeedServlet extends HttpServlet {

	private FeedThreadManager manager =  null;
	public Logger logger = Logger.getLogger(FeedServlet.class.getName());
	
	@Override
	public void init() throws ServletException {
		String fileName = getServletContext().getRealPath("config/catalystConfig.xml");
		FeedManager.CONFIGFILENAME=fileName;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		logger.log(Level.INFO,"[----- In FeedServlet -----]");
		
		String mode=null;
		String response="Processing";
		
		try{
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			ArrayList items=new ArrayList();
		
			items = (ArrayList) upload.parseRequest(req);
			Iterator iter = items.iterator();
			while(iter.hasNext()){
				DiskFileItem item = (DiskFileItem)iter.next();
				if(item.isFormField()){
					String fieldName = item.getFieldName();
					if(fieldName.equals("mode")){
						 mode = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
					}
				}
			}
		
			HttpSession session = req.getSession(false);
			UserInformation user = (UserInformation) session.getAttribute("userInfo");
			Integer ncid = user.getUserSelectedNewsCenterID();
			
			FeedManager feedManager = new FeedManager();
			FeedHelper feedHelper=new FeedHelper();
			//String filename = getServletContext().getRealPath("config/test.cnf");
			//HashMap<String, Long> feedMap =feedManager.readConfigFileVal(filename);
			//String fileName = getServletContext().getRealPath("config/catalystConfig.xml");
			HashMap<String, Long> feedMap =feedManager.getIndustryConfig(ncid);			
			
			if(feedMap!=null){
				
				for(String url:feedMap.keySet()){
				Long pollFreq=feedMap.get(url);
				String feedName=feedManager.getFeedName(url,ncid);
			
					if(mode.equalsIgnoreCase("A")){
						manager=new FeedThreadManager();
						manager.createThread(url, pollFreq,ncid,user.getUserId(),feedName);
					}
					if(mode.equalsIgnoreCase("M")){
						String feedContent=feedManager.getPageContent(url);
						if(feedContent!=null){
						/*	ArrayList<HashMap<String, String>> feedMapList=feedHelper.parseFeed(feedContent);
							if(feedMapList!=null){
								HashMap<Integer, ArrayList<NewsItems>>	map=feedHelper.saveNewsFeed(feedMapList, ncid,url,user.getUserId(),feedName);
								for(int isSaved:map.keySet()){
									 if(isSaved==1)
										 response="Success";
									 if(isSaved==0)
										 response="exist";
									 if(isSaved==-1){
										 response="Failed to save";
										 break;
									 }
								}
							}
							else
								response="Failure";*/
						}
						else
							response="Failure";
					}
				}
		}
		else
			response="Failure";
		feedHelper.closeConnection();	
		}catch (JSONException e) {
			e.printStackTrace();
			response="Invalid Url";
			
	}catch (Exception e) {
			e.printStackTrace();
			response="Failure";
			
	}
			resp.getWriter().print(response);
	}
}
